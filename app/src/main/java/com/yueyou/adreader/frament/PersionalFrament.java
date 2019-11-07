package com.yueyou.adreader.frament;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.view.Event.UserEvent;

/**
 * Created by zy on 2018/5/22.
 */

public class PersionalFrament extends WebViewFrament{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.init(inflater, container, Url.URL_WODE);
        UserEvent.getInstance().add(this);
        return view;
    }

    @Override
    public void onDestroy(){
        UserEvent.getInstance().remove(this);
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();;
    }

    public void loginSuccess(){
        mCustomWebView.loadUrl(Url.URL_WODE);
    }

    public void rechargeSuccess(){
        mCustomWebView.reload();
    }
}
