package com.yueyou.adreader.service.advertisement.adObject;

import android.content.Context;
import android.view.ViewGroup;

import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.view.webview.JavascriptAction;

public class AdSignRewardVideo extends AdEventObject {
    public AdSignRewardVideo() {
        super(AdSite.signRewardVideo);
    }

    public void show(String extra) {
        AdEngine.getInstance().loadSignRewardVideoAd(extra);
    }

    @Override
    public AdViewSize adViewSize(AdContent adContent, ViewGroup viewGroup) {
        int width = adContent.getWidth() > 0 ? adContent.getWidth() : 690;
        int height = adContent.getHeight() > 0 ? adContent.getHeight() : 338;
        return new AdViewSize(width, height);
    }

    @Override
    public void adRewardVideoCompleted(Context context, AdContent adContent) {
        JavascriptAction.rewardVerify = true;
        release();
    }

}
