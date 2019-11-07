package com.yueyou.adreader.service.advertisement.adObject;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.yueyou.adreader.activity.WebViewActivity;
import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.AdContent;

public class AdRewardVideo extends AdEventObject {
    public AdRewardVideo() {
        super(AdSite.rewardVideo);
    }
    public void show() {
        AdEngine.getInstance().loadRewardVideoAd();
    }

    @Override
    public AdViewSize adViewSize(AdContent adContent, ViewGroup viewGroup) {
        int width = adContent.getWidth() > 0 ? adContent.getWidth() : 690;
        int height = adContent.getHeight() > 0 ? adContent.getHeight() : 338;
        return new AdViewSize(width, height);
    }

    @Override
    public void adRewardVideoCompleted(Context context, AdContent adContent) {
        DataSHP.saveRewardVideoViewTime(context, adContent.getTime() * 1000 + System.currentTimeMillis());
    }

    @Override
    public void adError(Context context, AdContent adContent) {
        if (context instanceof Activity)
            WebViewActivity.show((Activity) context, Url.URL_AD_VIP,
                    WebViewActivity.ACCOUNT, "");
    }
    
}
