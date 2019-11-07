package com.yueyou.adreader.view.ReaderPage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yueyou.adreader.R;
import com.yueyou.adreader.service.model.BookShelfItem;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Widget;

import java.util.ArrayList;
import java.util.List;

public class YYTextView extends View {
    private final int KParagraphHeadForFontMultiple = 4;
    private final int KParagraphHeadNum = 5;
    private float mLineSpace;
    private float mLineSpaceSrc;
    private float mXOffsetParagraphHead;
    private float mYOffsetParagraphHeadSum;
    private int mDrawLineNum;
    private DataBlock mDataBlock1;
    private DataBlock mDataBlock2;
    private YYTextViewListener mYYTextViewListener;
    private YYTextViewFont mYYTextViewFont;
    private int mChapterTitleLen;
    private boolean mAdPlaceholder;
    private int mAdViewTop;
    private int mCurDrawLine;
    private int mAdPos = 0;
    private BookShelfItem bookShelfItem;
    private Context context;
    private Boolean isFirstChapter;
    private int deep = 0;

    public YYTextView(final Context context) {
        super(context);
    }

    public YYTextView(final Context context, final AttributeSet set) {
        super(context, set);
        this.context = context;
        mDataBlock1 = new DataBlock();
        mDataBlock2 = new DataBlock();
        mYYTextViewFont = new YYTextViewFont();
    }

    public void setListener(YYTextViewListener yyyTextViewListener) {
        mYYTextViewListener = yyyTextViewListener;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        LogUtil.e("YYtextview draw");
        if (!mYYTextViewListener.isShowText())
            return;
        float[] correctLayoutParam = new float[2];
        correctLayout(correctLayoutParam);
        float[] xy = new float[2];
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        mCurDrawLine = 0;
        mAdViewTop = 0;
        int i = drawBlock(canvas, mDataBlock1, xy, mDrawLineNum, correctLayoutParam[0], correctLayoutParam[1]);
        if (i != mDrawLineNum && mDataBlock2.lineListSize() > 0) {
            drawBlock(canvas, mDataBlock2, xy, mDrawLineNum - i, correctLayoutParam[0], correctLayoutParam[1]);
        }
        if (mAdPlaceholder && mAdViewTop == 0)
            mAdViewTop = (int) xy[1];
        setAdViewRect(mAdViewTop);
        mYYTextViewListener.textViewEvent(TEXTVIEW_EVENT.TEXTVIEW_EVENT_DRAW);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldw == 0)
            return;
        refreshLineNum();
    }

    private int getAdPos() {
        int a = mDrawLineNum / 2;
        if (mAdPos == 0) {
            a = a - 1;
        } else if (mAdPos == 2) {
            a = a + 1;
        }
        return a;
    }

    private int drawBlock(Canvas canvas, DataBlock dataBlock, float[] xy, int lineNum, float yOffsetParagraphHead, float lineSpaceAdd) {
        if (dataBlock.curLine < 0)
            dataBlock.curLine = 0;
        float x = xy[0], y = xy[1];
        int i = 0, offset = dataBlock.curLine;
        int adPos = 0;
        if (mAdPlaceholder) {
            adPos = getAdPos();
        }
        float lasty = y;
        for (; i < lineNum && offset < dataBlock.lineListSize(); i++, offset++, mCurDrawLine++) {
            Line line = dataBlock.lineList.get(offset);
            if (line.placeholder == 1) {
                y += mYYTextViewFont.wordHeight();
                continue;
            }
            String str = dataBlock.data.substring(line.offsetInBlock, line.offsetInBlock + line.size);
            x = 0;
            boolean isChapterTitle = isChapterTitle(dataBlock, i + dataBlock.curLine);
            if (line.paragraphHead && !isChapterTitle) {
                x += mXOffsetParagraphHead;
                if (!(i == 0 && lineNum == mDrawLineNum))
                    y += yOffsetParagraphHead;
            }
            if (mAdPlaceholder && mCurDrawLine == adPos) {
                mAdViewTop = (int) (lasty + (y - lasty) / 2);
                y += adHeight();
            }
            if (isChapterTitle)
                y += mYYTextViewFont.chapterTitleWordHeight();
            else
                y += mYYTextViewFont.wordHeight();
            boolean nextLineIsPatagraph = true;
            if (offset + 1 < dataBlock.lineList.size())
                nextLineIsPatagraph = dataBlock.lineList.get(offset + 1).paragraphHead;
            drawStr(canvas, str, x, y, isChapterTitle, nextLineIsPatagraph);
            lasty = y;
            y += mLineSpace + lineSpaceAdd;
        }
        xy[0] = x;
        xy[1] = y;
        return i;
    }

    private boolean isChapterTitle(DataBlock dataBlock, int index) {
        if (dataBlock.offsetInChapter != 0 || index >= 3)
            return false;
        return dataBlock.lineList.get(index).offsetInBlock < mChapterTitleLen;
    }

//    private void drawBookCover(Canvas canvas) {
//        try {
//            if (bookShelfItem != null) {
//                Paint paint = mYYTextViewFont.fontPaint();
//                Bitmap coverBitmap = FILE.getBookCover(context, bookShelfItem.getBookId());
//                if (coverBitmap == null)
//                    return;
//                int mWidth = com.blankj.utilcode.util.ScreenUtils.getScreenWidth();
//                int mHeight = com.blankj.utilcode.util.ScreenUtils.getScreenHeight();
//                int mContentHeight = ScreenUtils.dpToPxInt(context, 270f);
//                int marginWidth = ScreenUtils.dpToPxInt(context, 20f);
//                int mContentWidth = mWidth - marginWidth * 2;
//                int mIconWidth = ScreenUtils.dpToPxInt(context, 32f);
//                float coverealWidth = ScreenUtils.dpToPxInt(context, 124f);
//                float coverealHeight = ScreenUtils.dpToPxInt(context, 165.33f);
//
//                @SuppressLint("ResourceType")
//                InputStream is = context.getResources().openRawResource(R.drawable.cover_bg);
//                Bitmap bgBitmap = BitmapFactory.decodeStream(is);
//                RectF bgRect = new RectF(marginWidth, ScreenUtils.dpToPxInt(context, 20f), mWidth - marginWidth * 2, mHeight - ScreenUtils.dpToPxInt(context, 150f));
////                canvas.drawBitmap(bgBitmap, null, bgRect, paint);
//                RectF coverRect = new RectF((mContentWidth - coverealWidth) * 0.5f,
//                        (mContentHeight - coverealHeight) * 0.5f,
//                        (mContentWidth + coverealWidth) * 0.5f,
//                        (mContentHeight + coverealHeight) * 0.5f);
//                canvas.drawBitmap(coverBitmap, null, coverRect, paint);
//
//
//                float bookNameWidth = paint.measureText(bookShelfItem.getBookName());
//                canvas.drawText(bookShelfItem.getBookName(), (mContentWidth - bookNameWidth) * 0.5f, (mContentHeight + coverealHeight) * 0.5f + ScreenUtils.dpToPxInt(context, 40f), paint);
//                Paint paintText = new Paint();
//
//                if (bookShelfItem.getAuthor() != null && bookShelfItem.getAuthor().length() > 0) {
//                    paintText.setTextSize(50);
//                    paintText.setColor(context.getResources().getColor(R.color.colorText));
//                    float bookAuthorWidth = paintText.measureText(bookShelfItem.getAuthor());
//                    canvas.drawText(bookShelfItem.getAuthor(), (mContentWidth - bookAuthorWidth) * 0.5f, (mContentHeight + coverealHeight) * 0.5f + +ScreenUtils.dpToPxInt(context, 61f), paintText);
//                }
//                @SuppressLint("ResourceType")
//                InputStream iconIs = context.getResources().openRawResource(R.drawable.logo_300);
//                Bitmap iconBitmap = BitmapFactory.decodeStream(iconIs);
//                RectF iconRect = new RectF((mContentWidth - mIconWidth) * 0.5f,
//                        ScreenUtils.dpToPxInt(context, 469f),
//                        (mContentWidth + mIconWidth) * 0.5f,
//                        ScreenUtils.dpToPxInt(context, 501f));
//                canvas.drawBitmap(iconBitmap, null, iconRect, paint);
//
//                RectF roundRect = new RectF(ScreenUtils.dpToPxInt(context, 38f),
//                        ScreenUtils.dpToPxInt(context, 521f),
//                        mContentWidth - ScreenUtils.dpToPxInt(context, 38f),
//                        ScreenUtils.dpToPxInt(context, 573f));
//                paintText.setStyle(Paint.Style.STROKE);//设置空心
//
//                canvas.drawRoundRect(roundRect, 10, 10, paintText);
//                paintText.reset();
//                paintText.setColor(context.getResources().getColor(R.color.colorText));
//                paintText.setTextSize(32);
//                paintText.setAntiAlias(true); // 消除锯齿
//                String copyright = bookShelfItem.getCopyrightName();
//                String copyrightInfo = "本作品已授权客户端版权与发行";
//                if (copyright != null && copyright.length() > 0) {
//                    copyrightInfo = "本作品由" + copyright + "授权客户端版权与发行";
//                }
//                float appWidth = paintText.measureText(copyrightInfo);
//                canvas.drawText(copyrightInfo, (mContentWidth - appWidth) * 0.5f, ScreenUtils.dpToPxInt(context, 542f), paintText);
//
//                float copyrightWidth = paintText.measureText("版权所有，侵权必究");
//                canvas.drawText("版权所有，侵权必究", (mContentWidth - copyrightWidth) * 0.5f, ScreenUtils.dpToPxInt(context, 562f), paintText);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    private void drawStr(Canvas canvas, String str, float x, float y, boolean isChapterTitle, boolean nextLineIsPatagraph) {
        Paint paint = mYYTextViewFont.fontPaint();
        if (isChapterTitle)
            paint = mYYTextViewFont.chapterTitleFontPaint();
        float xAddSpace = 0;
        if (!isChapterTitle) {
            float remain = getWidth() - (x + mYYTextViewFont.strWidth(str));
            if (!nextLineIsPatagraph) {
                xAddSpace = remain / str.length();
            }
        }
        for (int i = 0; i < str.length(); i++) {
            String tmpStr = str.substring(i, i + 1);
            canvas.drawText(tmpStr, x, y, paint);
            if (isChapterTitle) {
                x += mYYTextViewFont.chapterFontWidth(tmpStr) + xAddSpace;
            } else {
                x += mYYTextViewFont.charWidth(tmpStr) + xAddSpace;
            }
        }
    }

    private void correctLayout(float[] param) {
        int lineNum = 0;
        int paragraphHead = 0;
        if (mDataBlock1.curLine < 0)
            mDataBlock1.curLine = 0;
        for (int i = mDataBlock1.curLine + 1; i < mDrawLineNum + mDataBlock1.curLine && i < mDataBlock1.lineListSize(); i++) {
            if (mDataBlock1.lineList.get(i).paragraphHead)
                paragraphHead++;
            lineNum++;
        }
        if (lineNum < mDrawLineNum) {
            for (int i = 0; i < mDrawLineNum - lineNum && i < mDataBlock2.lineListSize(); i++) {
                if (mDataBlock2.lineList.get(i).paragraphHead)
                    paragraphHead++;
            }
        }
        float yOffsetParagraphHead, lineSpaceAdd;
        if (paragraphHead >= KParagraphHeadNum) {
            yOffsetParagraphHead = mYOffsetParagraphHeadSum / paragraphHead;
            lineSpaceAdd = 0;
        } else if (paragraphHead == 0) {
            yOffsetParagraphHead = 0;
            lineSpaceAdd = mYOffsetParagraphHeadSum / (mDrawLineNum - 1);
        } else {
            yOffsetParagraphHead = mYOffsetParagraphHeadSum / KParagraphHeadNum;
            lineSpaceAdd = (mYOffsetParagraphHeadSum - yOffsetParagraphHead * paragraphHead) / (mDrawLineNum - 1);
        }
        param[0] = yOffsetParagraphHead;
        param[1] = lineSpaceAdd;
    }

    public void setFontSize(int size) {
        mYYTextViewFont.setFontProperty(Widget.sp2px(getContext(), size));
        refreshLineNum();
        if (mDataBlock1.lineListSize() == 0)
            return;
        int curOffset = mDataBlock1.lineList.get(mDataBlock1.curLine).offsetInBlock;
        mDataBlock1.lineList.clear();
        dataFormat(mDataBlock1, curOffset);
        if (mDataBlock2.lineListSize() > 0) {
            mDataBlock2.lineList.clear();
            dataFormat(mDataBlock2, 0);
        }
        for (int i = 0; i < mDataBlock1.lineListSize(); i++) {
            if (mDataBlock1.lineList.get(i).offsetInBlock == curOffset) {
                mDataBlock1.curLine = i;
                break;
            }
        }
    }

    public void setFontColor(int color) {
        mYYTextViewFont.setColor(color);
    }

    public void setLineSpace(int lineSpace) {
        mLineSpaceSrc = lineSpace;
        refreshLineNum();
    }

    public boolean isLastPage() {
        if (mDrawLineNum >= mDataBlock1.lineListSize() - mDataBlock1.curLine + mDataBlock2.lineListSize()
                && (mDataBlock1.isEnd() || mDataBlock2.isEnd()))
            return true;
        return false;
    }

    public boolean isLastPagePre() {
        if (mDrawLineNum < mDataBlock1.lineListSize() - mDataBlock1.curLine + mDataBlock2.lineListSize() && (mDrawLineNum + mDrawLineNum) >= mDataBlock1.lineListSize() - mDataBlock1.curLine + mDataBlock2.lineListSize())
            return true;
        return false;
    }

    public boolean isFirstPage() {
        try {
            return mDataBlock1.curLine == 0 && mDataBlock1.isFirst();
        } catch (Exception e) {

        }
        return true;
    }

    public void showAd(View adView, boolean changePos) {
        if (adView == null) {
            mAdPlaceholder = false;
            View view = getAdView();
            if (view != null) {
                ((ViewGroup) getParent()).removeView(view);
            }
        } else if (adView.getParent() == null) {
            if (changePos) {
                mAdPos++;
                if (mAdPos > 2)
                    mAdPos = 0;
            }
            adView.setTag(R.id.tag_textview_ad, "adview");
            ((ViewGroup) getParent()).addView(adView, ViewGroup.LayoutParams.MATCH_PARENT, getHeight() / 3);
            adView.setY(0);
            mAdPlaceholder = true;
        }
    }

    private void setBookCoverRect() {
        int top = 0;
        View view = getAdView();
        if (view == null || view.getY() == top)
            return;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = getHeight();
        layoutParams.width = getWidth();
        view.setX(getX());
        view.setY(top);
    }

    private void setAdViewRect(int top) {
        if (!mAdPlaceholder)
            return;
        if (top <= 0) {
            top = (int) getY() + getHeight() / 2;
        } else {
            top += getY();
        }
        View view = getAdView();
        if (view == null || view.getY() == top)
            return;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = adHeight();
        layoutParams.width = getWidth();
        view.setX(getX());
        view.setY(top);
    }

    private View getAdView() {
        int count = ((ViewGroup) getParent()).getChildCount();
        View view = ((ViewGroup) getParent()).getChildAt(count - 1);
        Object object = view.getTag(R.id.tag_textview_ad);
        if (object != null && "adview".equals(object))
            return view;
        return null;
    }

    private int adHeight() {
        return (int) (((ViewGroup) getParent()).getHeight() * 0.4f);
    }

    public void prePage() {
        refreshLineNum();
        int lineNum = mDrawLineNum;
        if (mDataBlock1.first) {
            if (mDataBlock1.curLine != 0 && mDataBlock1.curLine - mDrawLineNum < 0) {
                mDataBlock1.curLine = 0;
                return;
            }
        }
        for (; lineNum > 0; lineNum--) {
            if (mDataBlock1.curLine > 0 && mDataBlock1.curLine - lineNum < 0 && !Widget.isBlank(mDataBlock2.data)) {
                mDataBlock1.curLine = 0;
                return;
            }
            if (!preLine())
                return;
        }
    }

    public void nextPage() {
        int lineNum = mDrawLineNum;
        if (mDataBlock1.end) {
            if (mDataBlock1.lineListSize() - (mDataBlock1.curLine + mDrawLineNum) > 0 && mDrawLineNum > mDataBlock1.lineListSize() - (mDataBlock1.curLine + mDrawLineNum)) {
                lineNum = mDataBlock1.lineListSize() - (mDataBlock1.curLine + mDrawLineNum);
                mDataBlock1.curLine = mDataBlock1.curLine + mDrawLineNum;
                refreshLineNum();
                return;
            }
        }
        for (; lineNum > 0; lineNum--) {
            if (!nextLine()) {
                break;
            }
        }
        refreshLineNum();
    }

    public void gotoEndPage() {
        mDataBlock1.curLine = mDataBlock1.lineListSize() - mDrawLineNum;
        if (mDataBlock1.curLine < 0)
            mDataBlock1.curLine = 0;
    }

    private boolean nextLine() {
        if (mDataBlock1.isEmpty() || mDataBlock1.lineListSize() == 0) {
            mYYTextViewListener.textViewEvent(TEXTVIEW_EVENT.TEXTVIEW_EVENT_BOTTOM);
            return false;
        }
        mDataBlock1.curLine++;
        if ((mDataBlock1.curLine > mDataBlock1.lineListSize() - mDrawLineNum) && mDataBlock2.isEmpty()) {
            if (mYYTextViewListener.textViewEvent(TEXTVIEW_EVENT.TEXTVIEW_EVENT_BOTTOM) < 0) {
                mDataBlock1.curLine--;
                if (mDataBlock1.curLine < 0) {
                    mDataBlock1.curLine = 0;
                }
                return false;
            }
            if (mDataBlock2.isEmpty()) {
                mDataBlock1.curLine--;
                if (mDataBlock1.curLine < 0) {
                    mDataBlock1.curLine = 0;
                }
                return false;
            }
            return true;
        }
        if (mDataBlock1.curLine == mDataBlock1.lineListSize() && !mDataBlock2.isEmpty()) {
            mDataBlock1.release();
            swapBlockData();
            mDataBlock1.curLine = 0;
            mYYTextViewListener.textViewEvent(TEXTVIEW_EVENT.TEXTVIEW_EVENT_RELEASE_BLOCK);
        }
        return true;
    }

    private boolean preLine() {
        if (mDataBlock1.isEmpty())
            return false;
        mDataBlock1.curLine--;
        if (mDataBlock1.curLine <= mDataBlock1.lineListSize() - mDrawLineNum && !mDataBlock2.isEmpty()) {
            mDataBlock2.release();
            mYYTextViewListener.textViewEvent(TEXTVIEW_EVENT.TEXTVIEW_EVENT_RELEASE_BLOCK);
        }
        if (mDataBlock1.curLine < 0) {
            int res;
            res = mYYTextViewListener.textViewEvent(TEXTVIEW_EVENT.TEXTVIEW_EVENT_TOP);
            if (res == -1) {
                mDataBlock1.curLine = 0;
                return false;
            } else if (res == -2) {
                return false;
            }
            if (!mDataBlock2.isEmpty()) {
                swapBlockData();
                mDataBlock2.curLine = 0;
                mDataBlock1.curLine = mDataBlock1.lineListSize() - 1;
                if (mDataBlock2.curLine < 0) {
                    mDataBlock2.curLine = 0;
                }
            } else {
                mDataBlock1.curLine = 0;
                return false;
            }
        }
        return true;
    }

    private void swapBlockData() {
        DataBlock tmp = mDataBlock1;
        mDataBlock1 = mDataBlock2;
        mDataBlock2 = tmp;
    }

    public void refreshLineNum() {
        int rectHeight = getHeight();
        if (mAdPlaceholder)
            rectHeight -= adHeight();
        mLineSpace = mLineSpaceSrc;
        mYOffsetParagraphHeadSum = mYYTextViewFont.wordHeight() * KParagraphHeadForFontMultiple;
        if (mAdPlaceholder)
            mYOffsetParagraphHeadSum = mYYTextViewFont.wordHeight();
        float height;
        height = rectHeight - mYOffsetParagraphHeadSum;
        mDrawLineNum = (int) (height / (mYYTextViewFont.wordHeight() + mLineSpace));
        float remain = height - mDrawLineNum * (mYYTextViewFont.wordHeight() + mLineSpace);
        mLineSpace += remain / (mDrawLineNum - 1);
    }

    public void dataInit(DataBlock dataBlock, String data, int offset, boolean first, boolean end, int displayOffset, BookShelfItem bookShelfItem, boolean isFirstChapter) {
        int dataLengthInSrc = data.length();
        this.bookShelfItem = bookShelfItem;
        this.isFirstChapter = isFirstChapter;

//        data = data.replace("？", "?");
//        data = data.replace("！", "!");
//        data = data.replace("“", "\"");
//        data = data.replace("”", "\"");
        data = data.replaceAll("　+", " ");
        data = data.replaceAll(" +", " ");
        data = data.replaceAll("\r\n", "\n");
        data = data.replaceAll("\n+", "\n");
        data = data.replaceAll("\n ", "\n");
        if (data.startsWith(" ")) {
            data = data.substring(1, data.length());
        }
        if (data.startsWith("\n"))
            data = data.substring(1);
        if (data.endsWith("\n") && data.length() > 0)
            data = data.substring(0, data.length() - 1);
        if (offset == 0 && !Widget.isBlank(mYYTextViewListener.getChapterName())) {
            String title = mYYTextViewListener.getChapterName().replace("\r\n", "");
            title = title.replace("\n", "");
            mChapterTitleLen = title.length();
            data = String.format("%s\n%s", title, data);
            data = data.replaceAll("\n+", "\n");
        }
        dataBlock.release();
        dataBlock.offsetInChapter = offset;
        dataBlock.data = data;
        dataBlock.first = first;
        dataBlock.end = end;
        dataBlock.dataLengthInSrc = dataLengthInSrc;
        dataFormat(dataBlock, displayOffset);
        if (displayOffset != 0) {
            for (int i = 0; i < dataBlock.getLineList().size(); i++) {
                if (dataBlock.getLineList().get(i).getOffsetInBlock() == displayOffset) {
                    dataBlock.curLine = i;
                    break;
                }
            }
        }
        if (!dataBlock.isEnd() && dataBlock.getCurLine() + mDrawLineNum > dataBlock.getLineList().size()) {
            mYYTextViewListener.textViewEvent(TEXTVIEW_EVENT.TEXTVIEW_EVENT_BOTTOM);
        }
    }

    private void dataFormat(DataBlock dataBlock, int forceLine) {
        String data = dataBlock.data;
        float width = getWidth();
        mXOffsetParagraphHead = mYYTextViewFont.wordWidth() * 2;
        float lineWidth = 0;
        int seek = 0;
        for (int i = 0; i < data.length(); i++) {
            String curStr = data.substring(i, i + 1);
            if (curStr.equals("\n")) {
                addLine(dataBlock, i - seek, seek, isParagraphHead(data, seek));
                seek = i + 1;
                lineWidth = mXOffsetParagraphHead;
                continue;
            }
            if (forceLine != 0 && i == forceLine && !data.substring(i - 1, i).equals("\n")) {//切换字体后仍显示在行首
                addLine(dataBlock, i - seek, seek, isParagraphHead(data, seek));
                forceLine = 0;
                seek = i;
                lineWidth = 0;
                continue;
            }
            if (dataBlock.offsetInChapter == 0 && mChapterTitleLen > i)
                lineWidth += mYYTextViewFont.chapterFontWidth(curStr);
            else
                lineWidth += mYYTextViewFont.charWidth(curStr);
            if (lineWidth >= width) {
                boolean morePunc = false;
                if (i > 3 && isPunc(curStr) && isPunc(data.substring(i - 1, i))
                        && isPunc(data.substring(i - 2, i - 1))) {
                    morePunc = true;
                }
                while (!morePunc && i > 3 && i > seek && isPunc(curStr)) {
                    i--;
                    curStr = data.substring(i, i + 1);
                }
                addLine(dataBlock, i - seek, seek, isParagraphHead(data, seek));
                lineWidth = 0;
                seek = i;
                if (isParagraphHead(data, seek)) {
                    lineWidth += mXOffsetParagraphHead;
                }
            }
        }
        if (dataBlock.lineListSize() > 0) {
            Line line = dataBlock.lineList.get(dataBlock.lineListSize() - 1);
            int remain = dataBlock.data.length() - (line.offsetInBlock + line.size);
            if (remain > 0) {
                if ("\n".equals(dataBlock.data.substring(line.offsetInBlock + line.size, line.offsetInBlock + line.size + 1))) {
                    remain--;
                    if (remain > 0)
                        addLine(dataBlock, remain, line.offsetInBlock + line.size + 1, true);
                } else {
                    addLine(dataBlock, remain, line.offsetInBlock + line.size, false);
                }
            }
        }
        addChapterTitlePlaceholder(dataBlock);
    }

    private void addChapterTitlePlaceholder(DataBlock dataBlock) {
        if (dataBlock.offsetInChapter != 0 || dataBlock.lineList.size() < 5)
            return;
        for (int i = 0; i < dataBlock.lineList.size(); i++) {
            if (dataBlock.lineList.get(i).offsetInBlock >= mChapterTitleLen) {
                Line placeholder = new Line();
                placeholder.offsetInBlock = dataBlock.lineList.get(i).offsetInBlock;
                placeholder.placeholder = 1;
                dataBlock.lineList.add(i, placeholder);
                break;
            }
        }
    }

    private boolean isPunc(String str) {
        if (",".equals(str) || "，".equals(str)
                || ".".equals(str) || "。".equals(str)
                || "!".equals(str) || "！".equals(str)
                || ";".equals(str) || "；".equals(str)
                || ":".equals(str) || "：".equals(str)
                || "?".equals(str) || "？".equals(str)
                || "\"".equals(str) || "”".equals(str)
                || "…".equals(str))
            return true;
        return false;
    }

    private boolean isParagraphHead(String str, int seek) {
        try {
            return seek == 0 || str.substring(seek - 1, seek).equals("\n");
        } catch (Exception e) {

        }
        return false;
    }

    private void addLine(DataBlock dataBlock, int size, int offset, boolean paragraphHead) {
        Line line = new Line();
        line.size = size;
        line.offsetInBlock = offset;
        line.paragraphHead = paragraphHead;
        dataBlock.lineList.add(line);
    }

    public String firstLine() {
        if (mDataBlock1.isEmpty())
            return null;
        Line line = mDataBlock1.lineList.get(mDataBlock1.curLine);
        return mDataBlock1.data.substring(line.offsetInBlock, line.offsetInBlock + line.size);
    }

    public DataBlock getDataBlock1() {
        return mDataBlock1;
    }

    public DataBlock getDataBlock2() {
        return mDataBlock2;
    }

    public class Line {
        private boolean paragraphHead;
        private int size;
        private int offsetInBlock;
        private int placeholder;//0 不是占位符；1：标题；2：广告

        public boolean isParagraphHead() {
            return paragraphHead;
        }

        public void setParagraphHead(boolean paragraphHead) {
            this.paragraphHead = paragraphHead;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getOffsetInBlock() {
            return offsetInBlock;
        }

        public void setOffsetInBlock(int offsetInBlock) {
            this.offsetInBlock = offsetInBlock;
        }
    }

    public class DataBlock {
        private List<Line> lineList;
        private boolean first;//章节首
        private boolean end;//章节末尾
        private int offsetInChapter;//在章节中的偏移量
        private int curLine;//当前显示行
        private String data;
        private int dataLengthInSrc;

        public DataBlock() {
            lineList = new ArrayList<>();
            release();
        }

        public void release() {
            data = "";
            lineList.clear();
            first = false;
            end = false;
            offsetInChapter = 0;
            curLine = 0;
        }

        public boolean isEmpty() {
            return Widget.isBlank(data);
        }

        public int lineListSize() {
            return lineList.size();
        }

        public List<Line> getLineList() {
            return lineList;
        }

        public void setLineList(List<Line> lineList) {
            this.lineList = lineList;
        }

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public boolean isEnd() {
            return end;
        }

        public void setEnd(boolean end) {
            this.end = end;
        }

        public int getOffsetInChapter() {
            return offsetInChapter;
        }

        public void setOffsetInChapter(int offsetInChapter) {
            this.offsetInChapter = offsetInChapter;
        }

        public int getCurLine() {
            return curLine;
        }

        public void setCurLine(int curLine) {
            this.curLine = curLine;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public int getDataLengthInSrc() {
            return dataLengthInSrc;
        }

        public void setDataLengthInSrc(int dataLengthInSrc) {
            this.dataLengthInSrc = dataLengthInSrc;
        }
    }

    public enum TEXTVIEW_EVENT {
        TEXTVIEW_EVENT_TOP,
        TEXTVIEW_EVENT_BOTTOM,
        TEXTVIEW_EVENT_DRAW,
        TEXTVIEW_EVENT_RELEASE_BLOCK,
        TEXTVIEW_EVENT_MAX
    }

    public interface YYTextViewListener {
        int textViewEvent(TEXTVIEW_EVENT event);

        String getChapterName();

        boolean isShowText();

    }
}
