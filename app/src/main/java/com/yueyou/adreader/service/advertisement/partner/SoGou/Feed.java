package com.yueyou.adreader.service.advertisement.partner.SoGou;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.sogou.feedads.api.AdClient;
import com.sogou.feedads.api.AdData;
import com.sogou.feedads.api.AdRequestListener;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.model.AdContent;

public class Feed {
    public static void show(Context context, ViewGroup viewGroup, AdContent adContent, int adTemplate) {
        AdClient adClient = AdClient.newClient(context)
                .pid(adContent.getAppKey())
                .mid(adContent.getPlaceId())// 必填：代码位ID
                .addAdTemplate(adTemplate)
                .debug(true)
                .create();
        adClient.with((Activity)context).getAd(new AdRequestListener() {
            @Override
            public void onSuccess(AdData adData) {
                try {
                    if (adData == null || adData.getImglist() == null || adData.getImglist().length == 0){
                        AdEvent.getInstance().loadAdError(adContent, 0, "");
                        return;
                    }
                    adData.onAdImpression(context);
                    View[] views = AdEvent.getInstance().adShowPre(adContent, null, adData.getClient(), adData.getTitle(), null, adData.getImglist()[0]);
                    if (views != null && views.length > 0) {
                        for (View item : views){
                            item.setOnClickListener(v -> {
                                AdEvent.getInstance().adClicked(adContent);
                                adData.onAdClick(context);
                            });
                        }
                    }
                }catch (Exception e){

                }
            }

            @Override
            public void onFailed( Exception e ) {
                AdEvent.getInstance().loadAdError(adContent, 0, "");
            }
        });
    }
}
