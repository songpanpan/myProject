package com.yueyou.adreader.service.advertisement.partner.TouTiao;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.model.AdContent;

public class BannerAd {
    public static void show(Context context, TTAdManager ttAdManager, ViewGroup viewGroup, boolean heightEffect, AdContent adContent, boolean isCarousel) {
        AdEventObject.AdViewSize size = AdEvent.getInstance().adViewSize(adContent, viewGroup);
        if (size == null || size.width == 0 || size.height == 0)
            return;
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adContent.getPlaceId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(size.width, size.height)
                .build();
        TTAdNative ttAdNative = ttAdManager.createAdNative(context);
        ttAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {
            @Override
            public void onError(int code, String message) {
                AdEvent.getInstance().loadAdError(adContent, code, message);
            }

            @Override
            public void onBannerAdLoad(final TTBannerAd ad) {
                if (ad == null) {
                    AdEvent.getInstance().loadAdError(adContent, -1, "ad=null");
                    return;
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    AdEvent.getInstance().loadAdError(adContent, -1, "bannerView=null");
                    return;
                }
                //设置轮播的时间间隔  间隔在30s到120秒之间的值，不设置默认不轮播
                if (isCarousel)
                    ad.setSlideIntervalTime(adContent.getTime() * 1000);
                AdEvent.getInstance().adShowPre(adContent, viewGroup, bannerView);
                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        AdEvent.getInstance().adClicked(adContent);
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        AdEvent.getInstance().adShow(adContent, viewGroup, view);
                    }
                });
                //bindDownloadListener(ad);
                //在banner中显示网盟提供的dislike icon
                ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {
                        AdEvent.getInstance().adClosed(adContent);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }
}
