package com.yueyou.adreader.service.advertisement.partner.KeDaXunFei;

import android.content.Context;
import android.view.ViewGroup;

import com.iflytek.voiceads.dex.DexLoader;
import com.yueyou.adreader.service.advertisement.partner.AdControllerBase;
import com.yueyou.adreader.service.model.AdContent;

public class KDXFController extends AdControllerBase {
    private Context mContext;
    public void init(Context context, String appKey) {
        mContext = context;
        //SDKLogger.setDebug(true);
        DexLoader.initIFLYADModule(context);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadSplash(AdContent adContent, ViewGroup viewGroup) {
        Splash.show(mContext, viewGroup, adContent);
    }

    @Override
    public void loadBookShelfBanner(AdContent adContent, ViewGroup viewGroup) {
        FeedAd.show(mContext, viewGroup, adContent);
    }

    @Override
    public void loadReadPageBanner(AdContent adContent, ViewGroup viewGroup) {
        FeedAd.show(mContext, viewGroup, adContent);
    }

    @Override
    public void loadReadPageScreen(AdContent adContent, ViewGroup viewGroup) {
        FeedAd.show(mContext, viewGroup, adContent);
    }

    @Override
    public void loadRewardVideoAd(AdContent adContent, ViewGroup viewGroup, String rewardName, int rewardAmount, String extra) {

    }

    @Override
    public void loadWebBanner(AdContent adContent, ViewGroup viewGroup) {
        FeedAd.show(mContext, viewGroup, adContent);
    }
}
