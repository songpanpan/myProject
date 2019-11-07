package com.yueyou.adreader.activity.ViewPagerView;

import android.content.Context;

import com.yueyou.adreader.service.Url;

public class BookSelectedView extends BaseWebView {
    public BookSelectedView(final Context context, boolean preload) {
        //super(context, preload, "http://192.168.0.105:9090/test.html");
        super(context, preload, Url.URL_SELECTED);
    }
}
