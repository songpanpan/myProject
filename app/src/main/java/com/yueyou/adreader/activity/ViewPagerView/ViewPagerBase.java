package com.yueyou.adreader.activity.ViewPagerView;

import android.content.Context;
import android.widget.RelativeLayout;

import com.yueyou.adreader.service.analytics.ThirdAnalytics;

public class ViewPagerBase extends RelativeLayout {
    private boolean mEnter = false;
    public ViewPagerBase(Context context) {
        super(context);
    }

    public boolean enter() {
        if (mEnter)
            return false;
        ThirdAnalytics.onPageStart(this.getClass().getSimpleName());
        mEnter = true;
        return true;
    }

    public boolean leave() {
        if (!mEnter)
            return false;
        ThirdAnalytics.onPageEnd(this.getClass().getSimpleName());
        mEnter = false;
        return true;
    }
}
