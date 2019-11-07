package com.yueyou.adreader.view.ViewPager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.lang.reflect.Field;

public class ZYViewPager extends ViewPager
{
    private final int DefaultScrollSpeed;
    private boolean isDisableScroll;
    protected float mLastMotionX;
    private int mScrollSpeed;
    
    public ZYViewPager(final Context context) {
        super(context);
        this.DefaultScrollSpeed = 10;
        this.isDisableScroll = false;
        this.setViewPagerScrollSpeed(10);
    }
    
    public ZYViewPager(final Context context, final AttributeSet set) {
        super(context, set);
        this.DefaultScrollSpeed = 10;
        this.isDisableScroll = false;
        this.setViewPagerScrollSpeed(10);
    }
    
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        if (isDisableScroll)
            return false;
        if (motionEvent.getAction() == 0 && 10 != this.mScrollSpeed) {
            this.setViewPagerScrollSpeed(10);
        }
//        if (this.isDisableScroll || !JsInterface.misEnableThreeScreen) {
//            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
//                JsInterface.misEnableThreeScreen = true;
//            }
//            return false;
//        }
        return super.onInterceptTouchEvent(motionEvent);
    }
    
    public void setDisableScroll(final boolean isDisableScroll) {
        this.isDisableScroll = isDisableScroll;
    }
    
    public void setViewPagerScrollSpeed(final int n) {
        try {
            this.mScrollSpeed = n;
            final Field declaredField = ViewPager.class.getDeclaredField("mScroller");
            declaredField.setAccessible(true);
            final ZYViewPagerScroller zyViewPagerScroller = new ZYViewPagerScroller(this.getContext());
            zyViewPagerScroller.setZYDuration(n);
            declaredField.set(this, zyViewPagerScroller);
        }
        catch (IllegalAccessException ex) {}
        catch (IllegalArgumentException ex2) {}
        catch (NoSuchFieldException ex3) {}
    }
}
