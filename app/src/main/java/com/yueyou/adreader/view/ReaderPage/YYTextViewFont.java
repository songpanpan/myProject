package com.yueyou.adreader.view.ReaderPage;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.ArrayList;
import java.util.List;

public class YYTextViewFont {
    private float mWordHeight;
    private float mWordWidth;
    private float mChapterTitleWordHeight;
    private Paint mFontPaint;
    private Paint mChapterTitleFontPaint;
    private List<FontWidthInfo> mFontWidths = new ArrayList<>();
    private class FontWidthInfo {
        String str;
        float width;
        public FontWidthInfo(String str, float width) {
            this.str = str;
            this.width = width;
        }
    }

    public float chapterTitleWordHeight() {return mChapterTitleWordHeight;}
    public float wordHeight() {
        return mWordHeight;
    }

    public float wordWidth() {
        return mWordWidth;
    }

    public float strWidth(String str) {
        float width = 0;
        for(int i = 0; i < str.length(); i++)
            width += charWidth(str.substring(i, i + 1));
        return width;
    }

    //单个字符宽度
    public float charWidth(String str) {
        if (false)
        return mWordWidth;
        for (FontWidthInfo fontWidthInfo : mFontWidths) {
            if (fontWidthInfo.str.equals(str)) {
                return fontWidthInfo.width;
            }
        }
        float w = fontWidth(str);
        mFontWidths.add(new FontWidthInfo(str, w));
        return w;
    }

    public void setFontProperty(int fontSize) {
        mFontWidths.clear();
        if (mFontPaint == null) {
            mFontPaint = new Paint();
            mFontPaint.setAntiAlias(true); // 消除锯齿
            mFontPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mChapterTitleFontPaint = new Paint();
            mChapterTitleFontPaint.setFakeBoldText(true);
        }
        mFontPaint.setTextSize(fontSize);
        mChapterTitleFontPaint.setTextSize(fontSize + 16);
        Rect rect = fontBound("汉");
        mWordHeight = rect.height();
        mWordWidth = fontWidth("汉");
        mChapterTitleFontPaint.getTextBounds("汉", 0, 1, rect);
        mChapterTitleWordHeight = rect.height();
    }

    private float fontWidth(String str) {
        float[] a = new float[1];
        mFontPaint.getTextWidths(str, a);
        return a[0];
    }

    public float chapterFontWidth(String str) {
        float[] a = new float[1];
        mChapterTitleFontPaint.getTextWidths(str, a);
        return a[0];
    }

    private Rect fontBound(String str) {
        Rect rect = new Rect();
        mFontPaint.getTextBounds(str, 0, 1, rect);
        return  rect;
    }

    public Paint chapterTitleFontPaint() {
        return mChapterTitleFontPaint;
    }

    public Paint fontPaint() {
        return mFontPaint;
    }

    public void setColor(int color) {
        mFontPaint.setColor(color);
        mChapterTitleFontPaint.setColor(color);
    }
}
