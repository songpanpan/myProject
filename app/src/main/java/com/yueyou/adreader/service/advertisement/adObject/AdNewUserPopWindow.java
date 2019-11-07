package com.yueyou.adreader.service.advertisement.adObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.view.dlg.AlertWindow;

public class AdNewUserPopWindow extends AdEventObject {
    private AlertWindow.AlertWindowListener mAlertWindowListener;
    Activity mActivity;
    public AdNewUserPopWindow(Activity activity, AlertWindow.AlertWindowListener listener) {
        super(AdSite.newUserPopWindow);
        mAlertWindowListener = listener;
        mActivity = activity;
    }

    public void load() {
        AdEngine.getInstance().loadNewUserPopWindowAd();
    }

    @Override
    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {
        AlertWindow.show(mActivity, adContent.getAppKey(), mAlertWindowListener);
    }
}
