package com.yueyou.adreader.service.advertisement.partner.ChuangShen;

import android.content.Context;
import android.view.ViewGroup;

import com.yueyou.adreader.service.advertisement.partner.AdControllerBase;
import com.yueyou.adreader.service.model.AdContent;

public class CSController extends AdControllerBase {
    private Context mContext;

    public void init(Context context, String appKey) {
        this.mContext = context;
    }

    @Override
    public void loadReadPageBanner(AdContent adContent, ViewGroup viewGroup) {
        BannerAd.show(mContext, adContent, viewGroup);
    }

    @Override
    public void loadReadPageScreen(AdContent adContent, ViewGroup viewGroup) {
        FeedAd.show(mContext, adContent, viewGroup);
    }
}
