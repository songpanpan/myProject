package com.yueyou.adreader.activity.ViewPagerView;

import android.content.Context;

import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.view.Event.UserEvent;

public class PersionalView extends BaseWebView {
    public PersionalView(final Context context, boolean preload) {
        super(context, preload, Url.URL_WODE);
        UserEvent.getInstance().add(this);
    }

    @Override
    public void onDetachedFromWindow() {
        UserEvent.getInstance().remove(this);
        super.onDetachedFromWindow();
    }

    public void loginSuccess(){
        mCustomWebView.loadUrl(Url.URL_WODE);
    }

    public void rechargeSuccess(){
        mCustomWebView.reload();
    }
}
