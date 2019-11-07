package com.yueyou.adreader.service.advertisement.service;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.yueyou.adreader.service.model.AdContent;

public class AdEventObject {
    private int mSiteId;
    public interface AdEventObjectListener {
        void showed(AdContent adContent);
        void closed();
    }

    public AdEventObject(int siteId) {
        mSiteId = siteId;
        AdEvent.getInstance().add(this, siteId);
    }

    public void release() {
        AdEvent.getInstance().remove(this);
    }

    public void adShowPre(AdContent adContent, ViewGroup viewGroup, View adView) {

    }
    public View[] adShowPre(AdContent adContent, ViewGroup viewGroup, String adTitle, String adDesc,
                                 String adButtonStr, String adImgUrl) {
        return null;
    }
    public View[] adShowPre(AdContent adContent, ViewGroup viewGroup, String adTitle, String adDesc,
                            String adButtonStr, View adView) {
        return null;
    }
    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {

    }

    public void adShow(AdContent adContent, AdEvent.AdEventListener adEventListener) {

    }

    public View[] adShow(AdContent adContent, ViewGroup viewGroup, String adTitle,
                         String adDesc, String adButtonStr, String adImgUrl,
                         AdEvent.AdEventListener adEventListener){
        return null;
    }

    public void adClicked() {

    }

    public void adClosed() {

    }

    public void adRewardVideoCompleted(Context context, AdContent adContent) {

    }

    public void adError(AdContent adContent) {
    }
    public void adError(Context context, AdContent adContent) {
    }
    public AdViewSize adViewSize(AdContent adContent, ViewGroup viewGroup) {
        return new AdViewSize(viewGroup.getWidth(), viewGroup.getHeight());
    }

    public static class AdViewSize {
        public int width;
        public int height;
        public AdViewSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public void resume() {
        if (mSiteId != 0) {
            AdEvent.getInstance().add(this, mSiteId);
        }
    }

    public void pause() {
        if (mSiteId != 0) {
            AdEvent.getInstance().pause(this, mSiteId);
        }
    }
}
