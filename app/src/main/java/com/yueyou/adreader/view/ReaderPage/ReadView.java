package com.yueyou.adreader.view.ReaderPage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.YueYouApplication;
import com.yueyou.adreader.service.Action;
import com.yueyou.adreader.service.BookMarkEngine;
import com.yueyou.adreader.service.BookReadEngine;
import com.yueyou.adreader.service.DownloadChapterCallBack;
import com.yueyou.adreader.service.analytics.AnalyticsEngine;
import com.yueyou.adreader.service.db.BookFileEngine;
import com.yueyou.adreader.service.model.BookShelfItem;
import com.yueyou.adreader.service.model.ChapterInfo;
import com.yueyou.adreader.util.Const;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.Event.ReadViewEvent;
import com.yueyou.adreader.view.ReaderPage.YYTextView.TEXTVIEW_EVENT;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ReadView extends LinearLayout implements YYTextView.YYTextViewListener, ReadViewEvent.Event, ReadViewTouchManager.ReadViewTouchManagerListener, DownloadChapterCallBack {
    private ReadViewListener mReadViewListener;
    private YYTextView mYYTextView;
    private BookReadEngine mBookReadEngine;
    private BookShelfItem mBook;
    private Timer mTimer;
    private TextView mTimerTextView;
    private boolean mChapterDownloading;
    private BookMarkEngine mBookMarkEngine;
    private int mCurDownloadChapterId;
    private boolean mGotoEndPage;
    private ReadViewTouchManager mReadViewTouchManager;
    private int currentPageIndex = 0;
//    private BookCoverForReader mBookCoverForReader;

    int bgColor;
    int textColor;
    int barBgColor;
    boolean parchment;

    @Override
    public void onDetachedFromWindow() {
        ReadViewEvent.setEventListener(null);
        mTimer.cancel();
        mReadViewTouchManager.release();
        super.onDetachedFromWindow();
    }

    public ReadView(final Context context) {
        super(context);
    }

    public ReadView(final Context context, final AttributeSet set) {
        super(context, set);
        mReadViewListener = (ReadViewListener) context;
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.read_view, (ViewGroup) this);
        mReadViewTouchManager = new ReadViewTouchManager(this, findViewById(R.id.bg), (SurfaceViewAnimator) findViewById(R.id.animator_page));
        this.setOnTouchListener(mReadViewTouchManager);

        mYYTextView = findViewById(R.id.text);
        mYYTextView.setListener(this);
        mBookReadEngine = new BookReadEngine();
        ReadViewEvent.setEventListener(this);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ((Activity) getContext()).runOnUiThread(() -> {
                    findViewById(R.id.battery).invalidate();
                    setTimer();
                });
            }
        };
        mTimerTextView = findViewById(R.id.timer);
        setTimer();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 1000, 10000);
    }

    private void setTimer() {
        String str = Utils.format("%02d:%02d", Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
        mTimerTextView.setText(str);
    }

    /**
     * 打开指定的图书章节
     *
     * @param book 书架图书Item
     */
    public void openBook(BookShelfItem book) {
        mBook = book;
        Utils.logNoTag("bookId ->" + book.getBookId());
        if (BookFileEngine.isNeedDownloadChapter(this.getContext(), mBook.getBookId(), mBook.getChapterIndex())) {
            new Thread(() -> {
                Looper.prepare();
                Action.getInstance().downloadChapter(getContext(), mBook.getBookId(), mBook.getBookName(), mBook.getChapterIndex(), false, this);
                ((Activity) getContext()).runOnUiThread(() -> openBook());
            }).start();
        } else {
            openBook();
        }
    }

    /**
     * 执行实际的打开图书动作
     */
    private void openBook() {
        LogUtil.e("1031 openBook");
        // 加载书签
        mBookMarkEngine = new BookMarkEngine(getContext(), mBook.getBookId());

        // 打开图书章节的某一页
        if (!mBookReadEngine.openBook(this.getContext(), mBook.getBookId(), mBook.getChapterIndex())) {
            return;
        }
        mBookReadEngine.seek(mBook.getDataOffset());
        if (!mBookReadEngine.nextBlock())
            return;
//        mBookCoverForReader = findViewById(R.id.book_cover);
//        mBookCoverForReader.init(mBook, bgColor, textColor, barBgColor, parchment);
        // 加载插屏广告

        // 绘制当前页内容
        fillDataBlock(mYYTextView.getDataBlock1(), mBook.getDisplayOffset());
        mYYTextView.invalidate();
        mReadViewTouchManager.refreshCurPageView();
        setPage(0);
//        if (!(mYYTextView.isFirstPage() && mBookReadEngine.preChapterId() == 0)
//                || mBook.getBookPathType() == 0) {
//            hideBookCover();
//        }
        // 预加载下一章, 如果下一章ID为0(最后一章), 则尝试去重新下载当前章信息, 看看是否已经有更新
        if (mBookReadEngine.nextChapterId(getContext()) == 0) {
            new Thread(() -> {
                Looper.prepare();
                Action.getInstance().downloadChapter(getContext(), mBook.getBookId(), mBook.getBookName(), mBook.getChapterIndex(), true, this);
                ((Activity) getContext()).runOnUiThread(() -> {
                    mBookReadEngine.reloadOnlyChapterContent(getContext(), mBook.getBookId(), mBook.getChapterIndex());
                    prealodNextChapter();
                });
            }).start();
        } else {
            prealodNextChapter();
        }

        // 刷新书架中本书的章节总数量
        Action.getInstance().refreshChapterCount(getContext(), mBook.getBookId(), (Object object) -> {
            ChapterInfo chapterInfo = (ChapterInfo) Widget.jsonToObjectByMapStr(object, ChapterInfo.class);
            ((YueYouApplication) getContext().getApplicationContext()).getMainActivity().bookshelfFrament().refreshBookChapterCount(mBook.getBookId(), chapterInfo.getChapterCount());
        });
    }

    public boolean isVipChapter() {
        return mBookReadEngine.isVip();
    }

    /**
     * 预加载下一章
     */
    private void prealodNextChapter() {
        new Thread(() -> {
            int chapterId = mBookReadEngine.nextChapterId(getContext());
            if (chapterId == 0) {
                return;
            }
            Looper.prepare();
            Action.getInstance().downloadChapter(getContext(), mBook.getBookId(), mBook.getBookName(), chapterId, true, this);
        }).start();
    }

    public interface ReadViewListener {
        boolean onClickReadView(float x, float y, int w, int h);

        boolean isNight();

        void sendFlipPageEvent(boolean flipNext);

        void goRecommend();

        boolean menuShowed();

        void refreshChapter(boolean isVipChapter);
    }

    @Override
    public int textViewEvent(TEXTVIEW_EVENT event) {
        if (event == TEXTVIEW_EVENT.TEXTVIEW_EVENT_BOTTOM) {
            boolean hasNextBlock = mBookReadEngine.nextBlock();
            if (!hasNextBlock) {
                return gotoNextChapter();
            } else {
                fillDataBlock(mYYTextView.getDataBlock2(), 0);
            }
        } else if (event == TEXTVIEW_EVENT.TEXTVIEW_EVENT_TOP) {
            if (!mBookReadEngine.preBlock()) {
                return gotoPreChapter(true);
            } else {
                fillDataBlock(mYYTextView.getDataBlock2(), 0);
            }
        } else if (event == TEXTVIEW_EVENT.TEXTVIEW_EVENT_DRAW) {
            if (mBookReadEngine.length() == 0)
                return 0;
            ((TextView) findViewById(R.id.title)).setText(mBookReadEngine.getChapterName());
            int offsetInBlock = mYYTextView.getDataBlock1().getLineList().get(mYYTextView.getDataBlock1().getCurLine()).getOffsetInBlock();
            int blockSize = mYYTextView.getDataBlock1().getData().length();
            int offsetInchapter = mYYTextView.getDataBlock1().getDataLengthInSrc() * offsetInBlock / blockSize + mYYTextView.getDataBlock1().getOffsetInChapter();
            float progress = (float) offsetInchapter / mBookReadEngine.length() * 100;
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            String str = decimalFormat.format(progress);
            if (mYYTextView.isLastPage()) {
                str = "100";
            }
            ((TextView) findViewById(R.id.progress)).setText(str + "%");
            mBook.setDataOffset(mYYTextView.getDataBlock1().getOffsetInChapter());
            mBook.setDisplayOffset(offsetInBlock);
            ((YueYouApplication) getContext().getApplicationContext()).getMainActivity().bookshelfFrament().refreshBookReadProgress(mBook);
            //DataSHP.saveReadBookChapter(getContext(), this.currentPageIndex);
        } else if (event == TEXTVIEW_EVENT.TEXTVIEW_EVENT_RELEASE_BLOCK) {
            mBookReadEngine.seek(mYYTextView.getDataBlock1().getOffsetInChapter() + mYYTextView.getDataBlock1().getDataLengthInSrc());
        }
        return 0;
    }

    private void setPage(int pageNum) {
        if (pageNum <= 0)
            pageNum = 0;
        Log.i("setPage", "setPage: " + pageNum);
        this.currentPageIndex = pageNum;
    }

    @Override
    public String getChapterName() {
        return mBookReadEngine.getChapterName();
    }

    @Override
    public boolean isShowText() {
        return false;
    }

    public float getProgress() {
        String str = ((TextView) findViewById(R.id.progress)).getText().toString();
        return Float.parseFloat(str.substring(0, str.length() - 1));
    }

    private void fillDataBlock(YYTextView.DataBlock dataBlock, int displayOffset) {
        String buffer = mBookReadEngine.outBuffer();
        int offset = mBookReadEngine.outBufferOffset();
        if (Widget.isBlank(buffer))
            return;
        boolean first = dataBlock.getOffsetInChapter() == 0;
        boolean end = mBookReadEngine.isEnd();
        mYYTextView.dataInit(dataBlock, buffer, offset, first, end, displayOffset, mBook, mBookReadEngine.isFirstChapter());
        mReadViewListener.refreshChapter(mBookReadEngine.isVip());
    }

    public int gotoNextChapter() {
        int chapterId = mBookReadEngine.nextChapterId(getContext());
        if (chapterId == 0) {
            AnalyticsEngine.read(getContext(), mBook.getBookId(), mBook.getBookName(), mBook.getChapterIndex(), true, 0);
            Toast.makeText(getContext(), "已到最后一页", Toast.LENGTH_SHORT).show();
            return -1;
        }
        return gotoChapter(chapterId, false);
    }

    public int gotoPreChapter(boolean gotoEndPage) {
        if (mBookReadEngine.preChapterId() == 0) {
            Toast.makeText(getContext(), "已是第一页", Toast.LENGTH_SHORT).show();
            return -1;
        }
        return gotoChapter(mBookReadEngine.preChapterId(), gotoEndPage);
    }

    private int gotoChapter(int chapterId, boolean gotoEndPage) {
//        hideBookCover();
        if (!BookFileEngine.isNeedDownloadChapter(getContext(), mBook.getBookId(), chapterId)) {
            if (!mBookReadEngine.openBook(getContext(), mBook.getBookId(), chapterId)) {
                return -1;
            }
            mBook.setChapterIndex(chapterId);
            if (gotoEndPage) {
                gotoPosition(100, false);
            } else {
                gotoPosition(0, false);
            }
            prealodNextChapter();
            return -2;
        }
        downloadChapter(chapterId, gotoEndPage);
        return -1;
    }

    public void buyBook() {
        downloadChapter(mCurDownloadChapterId, mGotoEndPage);
    }

    private void downloadChapter(int chapterId, boolean gotoEndPage) {
        if (mChapterDownloading)
            return;
        mChapterDownloading = true;
        mCurDownloadChapterId = chapterId;
        mGotoEndPage = gotoEndPage;
        new Thread(() -> {
            Looper.prepare();
            int result = Action.getInstance().downloadChapter(getContext(), mBook.getBookId(), mBook.getBookName(), chapterId, false, this);
            ((Activity) getContext()).runOnUiThread(() -> {
                if (result == 0) {
                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_LONG).show();
                } else if (result == 1) {
                    gotoChapter(chapterId, gotoEndPage);
                }
                mChapterDownloading = false;
            });
        }).start();
    }

    public void gotoPosition(float position, boolean refreshCurPageView) {
//        hideBookCover();
        int offset = (int) (position * mBookReadEngine.length() / 100) + 1;
        mBookReadEngine.gotoPosition(offset);
        fillDataBlock(mYYTextView.getDataBlock1(), 0);
        if (position == 100) {
            mYYTextView.gotoEndPage();
        }
        mYYTextView.invalidate();
        if (refreshCurPageView)
            mReadViewTouchManager.refreshCurPageView();
        setPage(0);
    }

    @Override
    public void gotoChapter(int chapterId) {
        gotoChapter(chapterId, false);
    }

    @Override
    public void gotoMark(int index) {
//        hideBookCover();
        mBookReadEngine.openBook(getContext(), mBook.getBookId(), mBookMarkEngine.get(index).getChapterIndex());
        mBookReadEngine.seekAndLoad(mBookMarkEngine.get(index).getDataOffset());
        fillDataBlock(mYYTextView.getDataBlock1(), mBookMarkEngine.get(index).getDisplayOffset());
        mReadViewTouchManager.refreshCurPageView();
        setPage(0);
    }

    @Override
    public BookMarkEngine getMarkEngine() {
        return mBookMarkEngine;
    }

    public void setLineSpace(int value) {
        mYYTextView.setLineSpace(value);
        mYYTextView.invalidate();
    }

    public void setFontSize(int value) {
        mYYTextView.setFontSize(value);
        mYYTextView.invalidate();
    }

    public void setColor(int adbgColor, int bgColor, int textColor, boolean night, boolean parchment) {
        try {
            this.bgColor = bgColor;
            this.textColor = textColor;
            this.barBgColor = adbgColor;
            this.parchment = parchment;
//            if (mBookCoverForReader != null)
////                mBookCoverForReader.init(mBook, bgColor, textColor, barBgColor, parchment);
            findViewById(R.id.bg_content).setBackgroundColor(bgColor);
            ((TextView) findViewById(R.id.title)).setTextColor(textColor);
            mTimerTextView.setTextColor(textColor);
            ((TextView) findViewById(R.id.progress)).setTextColor(textColor);
            ((BatteryView) findViewById(R.id.battery)).setColor(textColor);
            mYYTextView.setFontColor(textColor);
            mYYTextView.invalidate();
            if (night) {
                mYYTextView.setAlpha(0.76f);
                findViewById(R.id.screen_mask).setVisibility(VISIBLE);
                mReadViewTouchManager.setMask(true);
            } else {
                mYYTextView.setAlpha(1f);
                findViewById(R.id.screen_mask).setVisibility(GONE);
                mReadViewTouchManager.setMask(false);
            }
            if (parchment) {
                findViewById(R.id.skin_parchment).setVisibility(VISIBLE);
            } else findViewById(R.id.skin_parchment).setVisibility(GONE);
            mReadViewTouchManager.refreshCurPageView();
            if ((bgColor & 0xffffff) < 0x800000)
                mReadViewTouchManager.setShadowColor(Color.BLACK);
            else mReadViewTouchManager.setShadowColor(Color.GRAY);
        } catch (Exception e) {

        }
    }

    public void setFlipMode(int flipMode) {
        mReadViewTouchManager.setFlipMode(flipMode);
    }

    public boolean isMark() {
        if (mBookMarkEngine == null) {
            return false;
        }
        return mBookMarkEngine.isMark(mBook.getChapterIndex(), mBook.getDataOffset(), mBook.getDisplayOffset());
    }

    public void mark() {
        if (isMark()) {
            mBookMarkEngine.deleteMark(getContext(), mBook.getChapterIndex(), mBook.getDataOffset(), mBook.getDisplayOffset());
            Toast.makeText(getContext(), "书签已删除", Toast.LENGTH_SHORT).show();
        } else {
            mBookMarkEngine.addMark(getContext(), mBookReadEngine.getChapterName(), mYYTextView.firstLine(), mBook.getChapterIndex(), mBook.getDataOffset(), mBook.getDisplayOffset());
            Toast.makeText(getContext(), "书签已添加", Toast.LENGTH_SHORT).show();
        }
    }

    public void resume() {
        try {
            mBookMarkEngine.reload(getContext(), mBook.getBookId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
    }

    private boolean isLastOfChapter() {
        return mBookReadEngine.nextChapterId(getContext()) == 0 && mBookReadEngine.isEnd()
                && mYYTextView.isLastPage();
    }

    private boolean isLastOfChapterPre() {
        return mBookReadEngine.nextChapterId(getContext()) == 0
                && mYYTextView.isLastPagePre();
    }

//    private boolean hideBookCover() {
//        if (mBookCoverForReader.getVisibility() == VISIBLE) {
//            mBookCoverForReader.setVisibility(GONE);
//            mBook.setBookPathType(0);
//            ((YueYouApplication) getContext().getApplicationContext()).getMainActivity().bookshelfFrament().refreshBookReadProgress(mBook);
//            return true;
//        }
//        return false;
//    }

//    private boolean displayBookCover() {
//        if (mBookCoverForReader.getVisibility() == GONE) {
//            mBookCoverForReader.setVisibility(VISIBLE);
//            mBook.setBookPathType(1);
//            ((YueYouApplication) getContext().getApplicationContext()).getMainActivity().bookshelfFrament().refreshBookReadProgress(mBook);
//            return true;
//        }
//        return false;
//    }

    @Override
    public boolean nextPage(boolean reflip) {
        LogUtil.e("nextPage");
        if (mBook == null)
            return false;
//        if (hideBookCover()) {
//            return true;
//        }
        if (isLastOfChapter()) {
            LogUtil.e("@bookUpdate@4  isLastOfChapter");
            if (!Utils.isFastEvent()) {
                mReadViewListener.goRecommend();
            }
            return false;
        }
        if (isLastOfChapterPre()) {
            LogUtil.e("@bookUpdate@4  isLastOfChapterPre");
            new Thread(() -> {
                Looper.prepare();
                Action.getInstance().downloadChapter(getContext(), mBook.getBookId(), mBook.getBookName(), mBook.getChapterIndex(), true, this);
                ((Activity) getContext()).runOnUiThread(() -> mBookReadEngine.reloadOnlyChapterContent(getContext(), mBook.getBookId(), mBook.getChapterIndex()));
            }).start();
        }

        if (!mYYTextView.isLastPage())
            setPage(this.currentPageIndex + 1);
        showScreenAd(false);
        mYYTextView.nextPage();
        mYYTextView.invalidate();
        Const.READ_PAGE_COUNT += 1;
        return true;
    }

    public boolean isCover() {
        if (mYYTextView.isFirstPage() && mBookReadEngine.preChapterId() == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean prePage(boolean reflip) {
        if (mBook == null)
            return false;
        if (mYYTextView.isFirstPage() && mBookReadEngine.preChapterId() == 0) {
//            if (displayBookCover()) {
////                return true;
////            }
            if (!Utils.isFastEvent())
                Toast.makeText(getContext(), "已是第一页", Toast.LENGTH_SHORT).show();
            return false;
        }
        mYYTextView.prePage();
        mYYTextView.invalidate();
        Const.READ_PAGE_COUNT += 1;
        return true;
    }

    private boolean showScreenAd(boolean prePage) {
        boolean lastPage = mYYTextView.isLastPage();
        boolean lastPagePre = mYYTextView.isLastPagePre();
        mYYTextView.showAd(null, !prePage);
        return false;
    }

    @Override
    public int click(float x, float y, boolean flip) {
        if (mReadViewListener.onClickReadView(x, y, getWidth(), getHeight()))
            return 0;
        if (x < getWidth() / 2) {
            if (flip)
                prePage(false);
            return 1;
        } else {
            if (flip)
                nextPage(false);
            return 2;
        }
    }

    @Override
    public boolean menuShowed() {
        return mReadViewListener.menuShowed();
    }
}
