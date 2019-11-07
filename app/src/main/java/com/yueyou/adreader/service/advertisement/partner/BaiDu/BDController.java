package com.yueyou.adreader.service.advertisement.partner.BaiDu;

import android.content.Context;
import android.view.ViewGroup;

import com.baidu.mobads.AdView;
import com.yueyou.adreader.service.advertisement.partner.AdControllerBase;
import com.yueyou.adreader.service.model.AdContent;

public class BDController extends AdControllerBase {
    private Context mContext;
    public void init(Context context, String appKey) {
        mContext = context;
        AdView.setAppSid(context, appKey);
    }

    @Override
    public void loadSplash(AdContent adContent, ViewGroup viewGroup) {
        Splash.show(mContext, viewGroup, adContent);
    }

    @Override
    public void loadBookShelfBanner(AdContent adContent, ViewGroup viewGroup) {

    }

    @Override
    public void loadReadPageBanner(AdContent adContent, ViewGroup viewGroup) {

    }

    @Override
    public void loadReadPageScreen(AdContent adContent, ViewGroup viewGroup) {

    }

    @Override
    public void loadRewardVideoAd(AdContent adContent, ViewGroup viewGroup, String rewardName, int rewardAmount, String extra) {
        BDRewardVideo.show(mContext, adContent);
    }

    @Override
    public void loadWebBanner(AdContent adContent, ViewGroup viewGroup) {

    }
}
