package com.yueyou.adreader.service.advertisement.partner.KeDaXunFei;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.conn.NativeDataRef;
import com.iflytek.voiceads.listener.IFLYNativeListener;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.model.AdContent;

public class FeedAd {
    public static void show(Context context, ViewGroup viewGroup, AdContent adContent) {
        try {
            IFLYNativeAd nativeAd = new IFLYNativeAd(context, adContent.getPlaceId(), new IFLYNativeListener() {
                @Override
                public void onAdLoaded(NativeDataRef nativeDataRef) {
                    try {
                        View[] views = AdEvent.getInstance().adShowPre(adContent, viewGroup, nativeDataRef.getTitle(), nativeDataRef.getDesc(), "", nativeDataRef.getImgUrl());
                        if (views == null)
                            return;
                        if (adContent.getSiteId() == AdSite.readePageScreen) {
                            AdEvent.getInstance().adShow(adContent, ()->{
                                boolean r = nativeDataRef.onExposure(views[0]);
                            });
                        }else {
                            AdEvent.getInstance().adShow(adContent, viewGroup, null);
                            nativeDataRef.onExposure(views[0]);
                        }
                        for (View item : views) {
                            item.setOnClickListener((View v) -> {
                                AdEvent.getInstance().adClicked(adContent);
                                boolean r = nativeDataRef.onClick(v);
                            });
                        }
                    }catch (Exception e){

                    }
                }

                @Override
                public void onAdFailed(AdError adError) {
                    AdEvent.getInstance().loadAdError(adContent, adError.getErrorCode(), adError.getErrorDescription());
                }

                @Override
                public void onConfirm() {

                }

                @Override
                public void onCancel() {

                }
            });
            nativeAd.loadAd();
        }catch (Exception e){

        }
    }
}