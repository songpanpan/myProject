package com.yueyou.adreader.view.ViewPager;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AdapterViewPager extends PagerAdapter
{
    private ArrayList<View> mViews;
    
    public AdapterViewPager(final ArrayList<View> mViews) {
        this.mViews = mViews;
    }
    
    @Override
    public void destroyItem(final View view, final int n, final Object o) {
        ((ViewPager)view).removeView(this.mViews.get(n));
    }
    
    @Override
    public int getCount() {
        return this.mViews.size();
    }
    
    public View getView(final int n) {
        return this.mViews.get(n);
    }
    
    @Override
    public Object instantiateItem(final ViewGroup viewGroup, final int n) {
        ((ViewPager)viewGroup).addView((View)this.mViews.get(n));
        return this.mViews.get(n);
    }
    
    @Override
    public boolean isViewFromObject(final View view, final Object o) {
        return view == o;
    }
    
    @Override
    public void setPrimaryItem(final ViewGroup viewGroup, final int n, final Object o) {
    }
}