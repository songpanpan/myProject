package com.yueyou.adreader.service.advertisement.partner.GuangDianTong;

import android.content.Context;
import android.view.ViewGroup;

import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.Utils;

import java.util.List;

public class NativeBannerAd {
    public static void show(Context context, ViewGroup viewGroup, AdContent adContent) {
        com.qq.e.ads.nativ.ADSize adSize = new com.qq.e.ads.nativ.ADSize(com.qq.e.ads.nativ.ADSize.FULL_WIDTH, com.qq.e.ads.nativ.ADSize.AUTO_HEIGHT);
        NativeExpressAD nativeExpressAD = new NativeExpressAD(context, adSize, adContent.getAppKey(), adContent.getPlaceId(), new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onNoAD(AdError adError) {
                AdEvent.getInstance().loadAdError(adContent, adError.getErrorCode(), adError.getErrorMsg());
            }

            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                if (list != null && !list.isEmpty()) {
                    AdEvent.getInstance().adShowPre(adContent, viewGroup, list.get(0));
                    list.get(0).render();
                } else {
                    AdEvent.getInstance().loadAdError(adContent, 0, Utils.format("onADLoaded list %d", 0));
                }
            }

            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {
                AdEvent.getInstance().loadAdError(adContent, 0, "onRenderFail");
            }

            @Override
            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADExposure(NativeExpressADView nativeExpressADView) {
                AdEvent.getInstance().adShow(adContent, viewGroup, nativeExpressADView);
            }

            @Override
            public void onADClicked(NativeExpressADView nativeExpressADView) {
                AdEvent.getInstance().adClicked(adContent);
            }

            @Override
            public void onADClosed(NativeExpressADView nativeExpressADView) {
                AdEvent.getInstance().adClosed(adContent);
            }

            @Override
            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

            }
        });
        nativeExpressAD.loadAD(1);
    }
}
