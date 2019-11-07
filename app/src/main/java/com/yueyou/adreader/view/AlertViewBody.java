package com.yueyou.adreader.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

@SuppressLint("AppCompatCustomView")
public class AlertViewBody extends RelativeLayout {
    public AlertViewBody(final Context context) {
        super(context);
    }

    public AlertViewBody(final Context context, final AttributeSet set) {
        super(context, set);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 宽模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 宽大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        // 高大小
        int heightSize;
        // 只有宽的值是精确的才对高做精确的比例校对
        if (widthMode == MeasureSpec.EXACTLY) {
            heightSize = (int) (widthSize * 14 / 10);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,
                    MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
