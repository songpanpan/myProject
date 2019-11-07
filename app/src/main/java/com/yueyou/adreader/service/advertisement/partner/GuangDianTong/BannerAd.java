package com.yueyou.adreader.service.advertisement.partner.GuangDianTong;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.model.AdContent;

public class BannerAd {
    public static void show(Context context, ViewGroup viewGroup, AdContent adContent) {
        BannerView bannerView = new BannerView((Activity) context, ADSize.BANNER, adContent.getAppKey(), adContent.getPlaceId());
        // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
        // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
        bannerView.setRefresh(0);
        bannerView.setADListener(new AbstractBannerADListener() {
            @Override
            public void onNoAD(AdError adError) {
                AdEvent.getInstance().loadAdError(adContent, adError.getErrorCode(), adError.getErrorMsg() );
            }

            @Override
            public void onADReceiv() {
                AdEvent.getInstance().adShowPre(adContent, viewGroup, bannerView);
            }

            @Override
            public void onADClicked() {
                AdEvent.getInstance().adClicked(adContent);
            }

            @Override
            public void onADExposure() {
                super.onADExposure();
                AdEvent.getInstance().adShow(adContent, viewGroup, bannerView);
            }
        });
        bannerView.loadAD();
    }

}
