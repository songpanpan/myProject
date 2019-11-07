package com.yueyou.adreader.view.ReaderPage;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import static android.content.Context.BATTERY_SERVICE;

public class BatteryView extends View{
    private int mColor;
    public BatteryView(Context context) {
        super(context);
    }

    public BatteryView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    @Override
    @TargetApi(21)
    public void draw(Canvas canvas) {
        super.draw(canvas);
        BatteryManager manager = (BatteryManager) this.getContext().getSystemService(BATTERY_SERVICE);
        //int battery = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        int battery = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            battery = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            battery = getSystemBattery(this.getContext());
        }
        Rect rcBody = new Rect(0, 4, getWidth(), getHeight());
        rcBody.right = rcBody.right - 10;
        Rect rcHead = new Rect();
        rcHead.left = rcBody.right + 1;
        rcHead.top = rcBody.top + rcBody.height() / 4;
        rcHead.right = rcHead.left + 8;
        rcHead.bottom = rcHead.top + rcBody.height() / 2;
        Rect rcStatus = new Rect();
        rcStatus.left = rcBody.left + 2;
        rcStatus.top = rcBody.top + 2;
        rcStatus.bottom = rcBody.bottom - 2;
        rcStatus.right = (rcBody.left + (rcBody.width() - 2)) * battery / 100;
        drawLine(canvas, 2, new Point(rcBody.left, rcBody.top), new Point(rcBody.right, rcBody.top));
        drawLine(canvas, 2, new Point(rcBody.left, rcBody.top), new Point(rcBody.left, rcBody.bottom));
        drawLine(canvas, 2, new Point(rcBody.left, rcBody.bottom), new Point(rcBody.right, rcBody.bottom));
        drawLine(canvas, 3, new Point(rcBody.right, rcBody.top), new Point(rcBody.right, rcBody.bottom));
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rcHead, paint);
        canvas.drawRect(rcStatus, paint);
    }

    private void drawLine(Canvas canvas, int width, Point start, Point end) {
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStrokeWidth(width);
        canvas.drawLine(start.x, start.y, end.x, end.y, paint);
    }
    public void setColor(int color) {
        mColor = 0xb2000000 | (0x00ffffff & color);
        invalidate();
    }

    /**
     * 实时获取电量
     */
    public int getSystemBattery(Context context) {
        int level;
        Intent batteryInfoIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        level = batteryInfoIntent.getIntExtra("level", 0);
        int batterySum = batteryInfoIntent.getIntExtra("scale", 100);
        int percentBattery = 100 * level / batterySum;
        return percentBattery;
    }
}
