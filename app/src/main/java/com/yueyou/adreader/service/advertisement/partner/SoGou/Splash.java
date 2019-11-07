package com.yueyou.adreader.service.advertisement.partner.SoGou;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.yueyou.adreader.activity.SplashActivity;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.Widget;

public class Splash {
    public static void show(Context context, ViewGroup viewGroup, AdContent adContent) {
        Widget.startActivity((Activity)context, SplashActivity.class, SplashActivity.ADCONTENT_INFO, adContent);
    }
}
