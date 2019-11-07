package com.yueyou.adreader.service.advertisement.adObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.model.AdContent;

public class AdTextViewScreen extends AdEventObject{

    public AdTextViewScreen() {
        super(AdSite.readPageBanner);
    }



    @Override
    public void adError(AdContent adContent) {
        load(adContent);
    }

    public void load() {
        load(null);
    }

    private void load(AdContent adContent) {

    }

    public void setColor(int bgCr, int titleCr, int descCr, boolean mask) {

    }

    @Override
    public View[] adShowPre(AdContent adContent, ViewGroup viewGroup, String adTitle,
                            String adDesc, String adButtonStr, String adImgUrl) {
        return null;
    }

    @Override
    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {

    }

    @Override
    public AdViewSize adViewSize(AdContent adContent, ViewGroup viewGroup) {
        int width = adContent.getWidth() > 0 ? adContent.getWidth() : 690;
        int height = adContent.getHeight() > 0 ? adContent.getHeight() : 338;
        return new AdViewSize(width, height);
    }

    @Override
    public void adRewardVideoCompleted(Context context, AdContent adContent) {

    }
}
