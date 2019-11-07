package com.yueyou.adreader.service.advertisement.adObject;

import android.view.View;
import android.view.ViewGroup;

import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.model.AdContent;

public class AdBookCover extends AdEventObject {
    private AdEventObjectListener mAdEventObjectListener;
    public AdBookCover(AdEventObjectListener listener) {
        super(AdSite.bookShelfCover);
        mAdEventObjectListener = listener;
    }
    @Override
    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {
        mAdEventObjectListener.showed(adContent);
    }

    public void load() {
        AdEngine.getInstance().loadBookShelfCoverAd();
    }
}
