package com.yueyou.adreader.frament;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yueyou.adreader.service.Url;

/**
 * Created by zy on 2018/5/22.
 */

public class BookStoreFrament extends WebViewFrament {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.init(inflater, container, Url.URL_INDEX);
    }
}
