package com.yueyou.adreader.service.advertisement.partner.GuangDianTong;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.model.AdContent;

public class Splash {
    public static void showSplash(Context context, ViewGroup viewGroup, TextView skipView, AdContent adContent) {
        SplashAD splashAD = new SplashAD((Activity) context, skipView, adContent.getAppKey(), adContent.getPlaceId(), new SplashADListener() {
            @Override
            public void onADDismissed() {
                AdEvent.getInstance().adClosed(adContent);
            }

            @Override
            public void onNoAD(AdError adError) {
                AdEvent.getInstance().loadAdError(adContent, adError.getErrorCode(), adError.getErrorMsg());
            }

            @Override
            public void onADPresent() {
                AdEvent.getInstance().adShowPre(adContent, null, null);
                AdEvent.getInstance().adShow(adContent, null, null);
            }

            @Override
            public void onADClicked() {
                AdEvent.getInstance().adClicked(adContent);
            }

            @Override
            public void onADTick(long millisUntilFinished) {
                //skipView.setText(Utils.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
            }

            @Override
            public void onADExposure() {

            }
        }, 0);
        splashAD.fetchAndShowIn(viewGroup);
    }
}
