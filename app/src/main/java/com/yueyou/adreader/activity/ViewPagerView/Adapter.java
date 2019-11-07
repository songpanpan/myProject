package com.yueyou.adreader.activity.ViewPagerView;


import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class Adapter extends PagerAdapter {
    private List<View> mViews;
    private boolean mActivity = false;
    public Adapter(final List<View> mViews) {
        this.mViews = mViews;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(this.mViews.get(position));
    }

    @Override
    public int getCount() {
        return this.mViews.size();
    }

    public View getView(final int postion) {
        return this.mViews.get(postion);
    }

    public void setActivity() {
        mActivity = true;
    }
    @Override
    public Object instantiateItem(final ViewGroup viewGroup, final int postion) {
        ((ViewPager)viewGroup).addView((View)this.mViews.get(postion));
        return this.mViews.get(postion);
    }

    @Override
    public boolean isViewFromObject(final View view, final Object o) {
        return view == o;
    }

    @Override
    public void setPrimaryItem(final ViewGroup viewGroup, final int postion, final Object o) {
        Log.i("setPrimaryItem", "setPrimaryItem: " + postion);
        if (!mActivity || postion >= mViews.size())
            return;
        for (int i = 0; i < mViews.size(); i++){
            if (i == postion)
                ((ViewPagerBase)mViews.get(i)).enter();
            else
                ((ViewPagerBase)mViews.get(i)).leave();
        }
    }

    public void setAllLeave() {
        for (int i = 0; i < mViews.size(); i++){
            ((ViewPagerBase)mViews.get(i)).leave();
        }
    }
}
