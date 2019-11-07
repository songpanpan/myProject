package com.yueyou.adreader.service.advertisement.partner.GuangDianTong;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.util.AdError;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.model.AdContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NativeSelfRender {
    public static void show(Context context, ViewGroup viewGroup, AdContent adContent, boolean banner) {
        NativeUnifiedAD nativeUnifiedAD = new NativeUnifiedAD(context, adContent.getAppKey(), adContent.getPlaceId(), new NativeADUnifiedListener() {
            @Override
            public void onADLoaded(List<NativeUnifiedADData> list) {
                if (list == null || list.isEmpty()) {
                    AdEvent.getInstance().loadAdError(adContent, 0, "list is null");
                    return;
                }
                NativeUnifiedADData nativeUnifiedADData = list.get(0);
                String imgUrl = nativeUnifiedADData.getImgUrl();
//                if (banner)
//                    imgUrl = nativeUnifiedADData.getIconUrl();
                View[] views = AdEvent.getInstance().adShowPre(adContent, viewGroup, nativeUnifiedADData.getTitle(),
                        nativeUnifiedADData.getDesc(), null, imgUrl);
                if (views == null || views.length == 0) {
                    return;
                }
                List<View> viewList = new ArrayList<>();
                viewList = Arrays.asList(views);
                nativeUnifiedADData.bindAdToView(context, (NativeAdContainer) views[0], null, viewList);
                nativeUnifiedADData.setNativeAdEventListener(new NativeADEventListener() {
                    @Override
                    public void onADExposed() {
                        AdEvent.getInstance().adShow(adContent, null, null);
                    }

                    @Override
                    public void onADClicked() {
                        AdEvent.getInstance().adClicked(adContent);
                    }

                    @Override
                    public void onADError(AdError adError) {
                        AdEvent.getInstance().loadAdError(adContent, adError.getErrorCode(), adError.getErrorMsg());
                    }

                    @Override
                    public void onADStatusChanged() {

                    }
                });
            }

            @Override
            public void onNoAD(AdError adError) {
                AdEvent.getInstance().loadAdError(adContent, adError.getErrorCode(), adError.getErrorMsg());
            }
        });
        nativeUnifiedAD.loadData(1);
    }
}
