package com.yueyou.adreader.activity.ViewPagerView;

import android.content.Context;
import android.widget.RelativeLayout;

public class ViewPagerBase extends RelativeLayout {
    private boolean mEnter = false;
    public ViewPagerBase(Context context) {
        super(context);
    }

    public boolean enter() {
        if (mEnter)
            return false;
        mEnter = true;
        return true;
    }

    public boolean leave() {
        if (!mEnter)
            return false;
        mEnter = false;
        return true;
    }
}
