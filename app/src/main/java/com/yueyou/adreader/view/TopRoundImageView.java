package com.yueyou.adreader.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;


public class TopRoundImageView extends android.support.v7.widget.AppCompatImageView {

    int dp = 4;
    float px = Resources.getSystem().getDisplayMetrics().density * dp;
    //圆角弧度
    private float[] rids = {px, px, px, px, 0.0f, 0.0f, 0.0f, 0.0f,};

    public TopRoundImageView(Context context) {
        super(context);
    }

    public TopRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopRoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        //绘制圆角imageview
        path.addRoundRect(new RectF(0, 0, w, h), rids, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
