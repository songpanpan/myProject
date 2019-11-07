package com.yueyou.adreader.service.advertisement.adObject;

import android.view.ViewGroup;

import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.model.AdContent;

public class AdResume extends AdSplash {
    private AdSplashListener mAdEventObjectListener;
    ViewGroup mAdContianer;

    public AdResume(AdSplashListener listener) {
        super(listener, AdSite.resumeSplash);
    }

    @Override
    public void load(ViewGroup viewGroup) {
        AdEngine.getInstance().loadResumeSplashAd(viewGroup);
        mAdContianer = viewGroup;
    }

    @Override
    protected void load(AdContent adContent) {
        AdEngine.getInstance().loadResumeSplashAd(adContent, mAdContianer);
    }

}
