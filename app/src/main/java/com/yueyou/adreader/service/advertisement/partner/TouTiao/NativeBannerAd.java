package com.yueyou.adreader.service.advertisement.partner.TouTiao;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NativeBannerAd {
    public static void show(Context context, TTAdManager ttAdManager, ViewGroup viewGroup, AdContent adContent) {
        AdEventObject.AdViewSize adViewSize = AdEvent.getInstance().adViewSize(adContent, viewGroup);
        if (adViewSize == null || adViewSize.width == 0 || adViewSize.height == 0)
            return;
        final AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adContent.getPlaceId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(adViewSize.width, adViewSize.height)
                .setNativeAdType(AdSlot.TYPE_BANNER) //请求原生广告时候，请务必调用该方法，设置参数为TYPE_BANNER或TYPE_INTERACTION_AD
                .setAdCount(1)
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        TTAdNative ttAdNative = ttAdManager.createAdNative(context);
        ttAdNative.loadNativeAd(adSlot, new TTAdNative.NativeAdListener() {
            @Override
            public void onError(int code, String message) {
                //Toast.makeText(context, code + message, Toast.LENGTH_LONG).show();
                AdEvent.getInstance().loadAdError(adContent, code, message);
            }

            @Override
            public void onNativeAdLoad(List<TTNativeAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    AdEvent.getInstance().loadAdError(adContent, 0, Utils.format("onNativeAdLoad %d", 0));
                    return;
                }
                TTNativeAd nativeAd = ads.get(0);
                //绑定原生广告的数据
                setAdData(context, nativeAd, viewGroup, adContent);
                //ThirdAnalytics.onEventAd(context, ThirdAnalytics.getMapAd(adContent, 0, "onNativeAdLoad"));
            }
        });
    }

    private static String imgUrl(TTNativeAd nativeAd) {
        if (nativeAd.getImageList() == null || nativeAd.getImageList().isEmpty()) {
            return null;
        }
        TTImage image = nativeAd.getImageList().get(0);
        if (image != null && image.isValid()) {
            return image.getImageUrl();
        }
        return null;
    }

    private static String buttonStr(TTNativeAd nativeAd) {
        switch (nativeAd.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
                //nativeAd.setActivityForDownloadApp((Activity) nativeAd.getco());
                return "点击下载";
            case TTAdConstant.INTERACTION_TYPE_DIAL:
                return "立即拨打";
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case TTAdConstant.INTERACTION_TYPE_BROWSER:
                return "查看详情";
            default:
                return null;
        }
    }

    private static void setAdData(Context context, TTNativeAd nativeAd, ViewGroup viewGroup, AdContent adContent) {
        View[] views = AdEvent.getInstance().adShowPre(adContent, viewGroup, nativeAd.getTitle(),
                nativeAd.getDescription(), buttonStr(nativeAd), imgUrl(nativeAd));
        if (views == null || views.length == 0)
            return;
        //可以被点击的view, 也可以把nativeView放进来意味整个广告区域可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(views[0]);

        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
        if (views != null && views.length > 0) {
            creativeViewList = Arrays.asList(views);
        }

        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        nativeAd.registerViewForInteraction((ViewGroup) views[0], clickViewList, creativeViewList, null, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                AdEvent.getInstance().adClicked(adContent);
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                AdEvent.getInstance().adClicked(adContent);
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                AdEvent.getInstance().adShow(adContent, null, null);
            }
        });
    }
}
