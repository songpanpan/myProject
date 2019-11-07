package com.yueyou.adreader.activity.ViewPagerView;

import android.content.Context;

import com.yueyou.adreader.service.Url;

public class BookStoreView extends BaseWebView {
    public BookStoreView(final Context context, boolean preload) {
        super(context, preload, Url.URL_INDEX);
    }
}
