package com.yueyou.adreader.service.advertisement.partner.TouTiao;

import android.content.Context;
import android.support.annotation.MainThread;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.model.AdContent;

public class Splash {
    public static void show(Context context, TTAdManager ttAdManager, ViewGroup viewGroup, AdContent adContent) {
        TTAdNative ttAdNative = ttAdManager.createAdNative(context);
        int w = viewGroup.getWidth();
        int h = viewGroup.getHeight();
        //开屏广告参数
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adContent.getPlaceId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(w, h)
                .build();
        //调用开屏广告异步请求接口
        try {
            ttAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
                @Override
                @MainThread
                public void onError(int code, String message) {
                    AdEvent.getInstance().loadAdError(adContent, code, message);
                }

                @Override
                @MainThread
                public void onTimeout() {
                    AdEvent.getInstance().loadAdError(adContent, -1, "timeout");
                }

                @Override
                @MainThread
                public void onSplashAdLoad(TTSplashAd ad) {
//                //获取SplashView
                View view = ad.getSplashView();
                viewGroup.addView(view);
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮
                    //ad.setNotAllowSdkCountdown();

                    //设置SplashView的交互监听器
                    AdEvent.getInstance().adShowPre(adContent, null, null);
                    ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {
                            AdEvent.getInstance().adClicked(adContent);
                        }

                        @Override
                        public void onAdShow(View view, int type) {
                            AdEvent.getInstance().adShow(adContent, viewGroup, view);
                        }

                        @Override
                        public void onAdSkip() {
                            AdEvent.getInstance().adClosed(adContent);
                        }

                        @Override
                        public void onAdTimeOver() {
                            AdEvent.getInstance().adClosed(adContent);
                        }
                    });
                }
            }, 2000);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
