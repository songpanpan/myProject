package com.yueyou.adreader.service.advertisement.adObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.view.dlg.AlertWindow;

public class AdStartPopWindow extends AdEventObject {
    private AlertWindow.AlertWindowListener mAlertWindowListener;
    Activity mActivity;
    public AdStartPopWindow(Activity activity, AlertWindow.AlertWindowListener listener) {
        super(AdSite.startPopupWindow);
        mAlertWindowListener = listener;
        mActivity = activity;
    }

    public void load() {
        AdEngine.getInstance().loadStartPopWindowAd();
    }

    @Override
    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {
        AlertWindow.show(mActivity, adContent.getAppKey(), mAlertWindowListener);
    }
}