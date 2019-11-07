package com.yueyou.adreader.service.advertisement.partner.TouTiao;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Utils;

import java.util.List;

public class ExpressBannerAd {
    public static void show(Context context, TTAdManager ttAdManager, ViewGroup viewGroup, AdContent adContent) {
        AdEventObject.AdViewSize viewSize = AdEvent.getInstance().adViewSize(adContent, viewGroup);
        AdSlot adSlot;
        if (adContent.getSiteId() == 5)
            adSlot = new AdSlot.Builder()
                    .setCodeId(adContent.getPlaceId()) //广告位id
                    .setSupportDeepLink(true)
                    .setAdCount(1) //请求广告数量为1到3条
                    .setExpressViewAcceptedSize(viewSize.width, 0) //期望模板广告view的size,单位dp
                    .setImageAcceptedSize(640, 320)//这个参数设置即可，不影响模板广告的size
                    .build();
        else
            adSlot = new AdSlot.Builder()
                    .setCodeId(adContent.getPlaceId()) //广告位id
                    .setSupportDeepLink(true)
                    .setAdCount(1) //请求广告数量为1到3条
                    .setExpressViewAcceptedSize(viewSize.width, viewSize.height) //期望模板广告view的size,单位dp
                    .setImageAcceptedSize(640, 320)//这个参数设置即可，不影响模板广告的size
                    .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        TTAdNative ttAdNative = ttAdManager.createAdNative(context);
        ttAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                LogUtil.e("ExpressBannerAd onError:" + message);
                AdEvent.getInstance().loadAdError(adContent, code, message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {

                if (ads == null || ads.size() == 0) {
                    LogUtil.e("ExpressBannerAd onNativeExpressAdLoad:" + "ads.size() == 0");
                    AdEvent.getInstance().loadAdError(adContent, 0, Utils.format("onNativeAdLoad %d", 0));
                    return;
                }
                LogUtil.e("ExpressBannerAd onNativeExpressAdLoad:");
                TTNativeExpressAd ttAd = ads.get(0);
                bindAdListener(ttAd, adContent);
                //bindDislike(viewGroup.getContext(), ttAd);
                ttAd.render();
            }
        });
    }

    private static void bindAdListener(TTNativeExpressAd ad, AdContent adContent) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                LogUtil.e("ExpressBannerAd onAdClicked:");
                AdEvent.getInstance().adClicked(adContent);
            }

            @Override
            public void onAdShow(View view, int type) {
                LogUtil.e("ExpressBannerAd onAdShow:");
                AdEvent.getInstance().adShow(adContent, null, view);
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                LogUtil.e("ExpressBannerAd onRenderFail:");
                AdEvent.getInstance().loadAdError(adContent, code, msg);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                LogUtil.e("ExpressBannerAd onRenderSuccess:");
                AdEvent.getInstance().adShowPre(adContent, null, view);
            }
        });
    }

    private static void bindDislike(Context context, TTNativeExpressAd ad) {
        //使用默认模板中默认dislike弹出样式
        ad.setDislikeCallback((Activity) context, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {

            }

            @Override
            public void onCancel() {

            }
        });
    }
}
