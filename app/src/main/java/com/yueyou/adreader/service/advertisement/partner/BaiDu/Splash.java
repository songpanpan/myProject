package com.yueyou.adreader.service.advertisement.partner.BaiDu;

import android.content.Context;
import android.view.ViewGroup;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashLpCloseListener;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.model.AdContent;

public class Splash {
    public static void show(Context context, ViewGroup viewGroup, AdContent adContent) {
        new SplashAd(context, viewGroup, new SplashLpCloseListener() {
            @Override
            public void onLpClosed() {
                AdEvent.getInstance().adClosed(adContent);
            }

            @Override
            public void onAdPresent() {
                AdEvent.getInstance().adShow(adContent, viewGroup, null);
            }

            @Override
            public void onAdDismissed() {
                AdEvent.getInstance().adClosed(adContent);
            }

            @Override
            public void onAdFailed(String s) {
                AdEvent.getInstance().loadAdError(adContent, 0, s);
            }

            @Override
            public void onAdClick() {
                AdEvent.getInstance().adClicked(adContent);
            }
        }, adContent.getPlaceId(), true);
    }
}
