package com.yueyou.adreader.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yueyou.adreader.R;
import com.yueyou.adreader.service.Action;
import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.service.model.BookShelfItem;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.NavigationBarTools;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.view.Event.BuyBookEvent;
import com.yueyou.adreader.view.ReaderPage.ReadMenu;
import com.yueyou.adreader.view.ReaderPage.ReadView;
import com.yueyou.adreader.view.dlg.MessageDlg;

import java.util.HashMap;
import java.util.Map;


public class ReadActivity extends com.yueyou.adreader.activity.base.BaseActivity implements ReadMenu.MenuListener, ReadView.ReadViewListener, BuyBookEvent.BuyBookEventListener {
    public static final String KEY_BOOKID = "keyBookId";
    public static final String KEY_BOOK_TMP = "keyIsTmpBook";
    public static final String KEY_CHAPTERID = "keyChapterId";
    private final int AD_SWITCH_TIME = 70 * 1000;
    private BookShelfItem mBook;
    private ReadMenu mReadMenu;
    private ReadView mReadView;
    //是否内置书
    private boolean mIsTmpBook;
    private int mNavigationBarColor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_read);
        getNavigationBarColor();
        String bookId = getIntent().getStringExtra(KEY_BOOKID);
        String bookTmp = getIntent().getStringExtra(KEY_BOOK_TMP);
        if (bookTmp == null || bookTmp.equals("false")) {
            mIsTmpBook = false;
        } else {
            mIsTmpBook = true;
        }
        Log.i("blank screen", "blank screen ReadActivity: " + bookId);
        try {
            mBook = ((YueYouApplication) getApplicationContext()).getMainActivity().bookshelfFrament().getBook(Integer.parseInt(bookId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mBook == null) {
            finish();
            return;
        }
        Log.i("blank screen", "blank screen ReadActivity00000: " + bookId);
        mReadMenu = findViewById(R.id.read_menu);
        mReadView = findViewById(R.id.read_view);
        mReadMenu.setVisibility(View.GONE);
        new Thread(() -> Action.getInstance().getCtlContent(this)).start();
        mReadView.post(() -> {
            mReadMenu.initSetting();
            mReadView.openBook(mBook);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBook == null) {
            return;
        }
        try {
            mReadView.resume();
        } catch (Exception e) {
            Utils.logNoTag("onResume error %s", e.getMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mReadView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.logNoTag("onDestroy");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                sendFlipPageEvent(true);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                sendFlipPageEvent(false);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void sendFlipPageEvent(boolean flipNext) {
        float y = 10;
        float x = mReadView.getWidth() / 5;
        if (flipNext) {
            x = mReadView.getWidth() * 4 / 5;
        }
        MotionEvent evenDownt = MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis() + 100, MotionEvent.ACTION_DOWN, x, y, 0);
        dispatchTouchEvent(evenDownt);
        MotionEvent eventUp = MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis() + 100, MotionEvent.ACTION_UP, x, y, 0);
        dispatchTouchEvent(eventUp);
        evenDownt.recycle();
        eventUp.recycle();
    }

    @Override
    public void goRecommend() {
        Map<String, Object> data = new HashMap<>();
        data.put(WebViewActivity.KEY_IS_TMPBOOK, mIsTmpBook);
        data.put(WebViewActivity.KEY_BOOK_ID, mBook.getBookId());
        WebViewActivity.show(this, Utils.format(Url.URL_RECOMMEND_ENDPAGE, mBook.getBookId()),
                WebViewActivity.RECOMMEND_ENDPAGE, "", isNight(), "readbook", data);
        ReadActivity.this.finish();
    }

    @Override
    public void initTop(String title, int leftButtonImgId, int rightButtonImgId) {

    }

    protected void onClickTopBarLeft(View v) {
        if (mIsTmpBook) {
            MessageDlg.show(this, "是否添加到书架？", (boolean result) -> {
                if (!result) {
                    if (mBook != null)
                        ((YueYouApplication) getApplicationContext()).getMainActivity().bookshelfFrament().deleteBook(mBook.getBookId());
                }
                finish();
            });
        } else {
            finish();
        }
    }

    @Override
    public boolean onClickReadView(float x, float y, int w, int h) {
        LogUtil.e("onClickReadView");
        if (mReadMenu.isShown()) {
            mReadMenu.click();
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            return true;
        }
        if (x > w * 3 / 8 && x < w * 5 / 8) {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mReadMenu.click();
            if (mReadMenu.isShown()) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                setStatusNavBarColor(ReadActivity.this, Color.TRANSPARENT, Color.TRANSPARENT);
                if (NavigationBarTools.checkDeviceHasNoamalButton(ReadActivity.this)) {
                    mReadMenu.hideNavigationBar();
                }
//                if (!NavigationBarTools.isNavigationBarExist(ReadActivity.this)) {
//                    mReadMenu.hideNavigationBar();
//                }
            }
            return true;
        }
        return false;
    }

    /**
     * 状态栏、导航栏全透明去阴影（5.0以上）
     *
     * @param activity
     * @param color_status
     * @param color_nav
     */
    public static void setStatusNavBarColor(Activity activity, int color_status, int color_nav) {
        Window window = activity.getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(color_status);

            window.setNavigationBarColor(color_nav);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }
    }

    @Override
    public boolean menuShowed() {
        return mReadMenu.isShown();
    }

    @Override
    public void refreshChapter(boolean isVipChapter) {
    }

    @Override
    public boolean isNight() {
        return mReadMenu.isNight();
    }

    @Override
    public void onClickBack() {
        onClickTopBarLeft(null);
    }

    @Override
    public void onClickChapter() {
        final Intent intentData = new Intent(this, ChapterActivity.class);
        intentData.putExtra(ReadActivity.KEY_BOOKID, mBook.getBookId() + "");
        intentData.putExtra(ReadActivity.KEY_CHAPTERID, mBook.getChapterIndex() + "");
        startActivity(intentData);
    }

    @Override
    public void onClickMark() {
        mReadView.mark();
        mReadMenu.click();
    }

    @Override
    public boolean isMark() {
        return mReadView.isMark();
    }

    @Override
    public void onFlipPageModel(int model) {
        mReadView.setFlipMode(model);
    }

    @Override
    public void onClickGoto(float progress) {
        mReadView.gotoPosition(progress, true);
    }

    @Override
    public void onClickPreChapter() {
        mReadView.gotoPreChapter(false);
    }

    @Override
    public void onClickNextChapter() {
        mReadView.gotoNextChapter();
    }

    @Override
    public void onClickFont(int value) {
        mReadView.setFontSize(value);
    }

    @Override
    public void onClickLine(int value) {
        mReadView.setLineSpace(value);
    }

    @Override
    public void onClickSkin(int bgColor, int textColor, int barBgColor, boolean parchment) {
        LogUtil.e("1031 onClickSkin");

        mReadView.setColor(barBgColor, bgColor, textColor, mReadMenu.isNight(), parchment);
        if (mReadMenu.isNight()) {
            findViewById(R.id.banner_mask).setVisibility(View.VISIBLE);
            setNavigationBarColor(0xff000000);
        } else {
            findViewById(R.id.banner_mask).setVisibility(View.GONE);
            setNavigationBarColor(mNavigationBarColor);
        }
    }

    private void getNavigationBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNavigationBarColor = getWindow().getNavigationBarColor();
        }
    }

    private void setNavigationBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(color);
        }
    }

    @Override
    public int getReadProgress() {
        return (int) mReadView.getProgress();
    }

    @Override
    public void buyBook() {
        mReadView.buyBook();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LogUtil.e("onWindowFocusChanged");
        if (hasFocus) {
            LogUtil.e("onWindowFocusChanged  hasFocus");
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
