package com.yueyou.adreader.service.advertisement.partner.GuangDianTong;

import android.content.Context;
import android.view.ViewGroup;

import com.yueyou.adreader.service.advertisement.partner.AdControllerBase;
import com.yueyou.adreader.service.model.AdContent;

public class GDTController extends AdControllerBase{
    private Context mContext;
    public void init(Context context, String appKey) {
        mContext = context;
    };

    @Override
    public void loadSplash(AdContent adContent, ViewGroup viewGroup) {
        Splash.showSplash(mContext, viewGroup, null, adContent);
    }

    @Override
    public void loadBookShelfBanner(AdContent adContent, ViewGroup viewGroup) {
        NativeSelfRender.show(mContext, viewGroup, adContent, false);
    }

    @Override
    public void loadReadPageBanner(AdContent adContent, ViewGroup viewGroup) {
        //BannerAd.show(mContext, viewGroup, adContent);
        NativeSelfRender.show(mContext, viewGroup, adContent, true);
    }

    @Override
    public void loadReadPageScreen(AdContent adContent, ViewGroup viewGroup) {
        NativeSelfRender.show(mContext, viewGroup, adContent, false);
    }

    @Override
    public void loadWebBanner(AdContent adContent, ViewGroup viewGroup) {
        NativeSelfRender.show(mContext, viewGroup, adContent, true);
    }
}
