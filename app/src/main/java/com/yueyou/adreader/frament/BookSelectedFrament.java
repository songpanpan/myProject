package com.yueyou.adreader.frament;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zy on 2018/5/22.
 */

public class BookSelectedFrament extends WebViewFrament {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.init(inflater, container, "http://192.168.0.106:9090/test.html");
        //return super.init(inflater, container, Url.URL_SELECTED);
    }
}
