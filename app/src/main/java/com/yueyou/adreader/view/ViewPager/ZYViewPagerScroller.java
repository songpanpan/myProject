package com.yueyou.adreader.view.ViewPager;

import android.view.animation.Interpolator;
import android.content.Context;
import android.widget.Scroller;

public class ZYViewPagerScroller extends Scroller
{
    private int mDuration;
    
    public ZYViewPagerScroller(final Context context) {
        super(context);
        this.mDuration = 10;
    }
    
    public ZYViewPagerScroller(final Context context, final Interpolator interpolator) {
        super(context, interpolator);
        this.mDuration = 10;
    }
    
    public int getZYDuration() {
        return this.mDuration;
    }
    
    public void setZYDuration(final int mDuration) {
        this.mDuration = mDuration;
    }
    
    public void startScroll(final int n, final int n2, final int n3, final int n4) {
        super.startScroll(n, n2, n3, n4, this.mDuration);
    }
    
    public void startScroll(final int n, final int n2, final int n3, final int n4, final int n5) {
        super.startScroll(n, n2, n3, n4, this.mDuration);
    }
}
