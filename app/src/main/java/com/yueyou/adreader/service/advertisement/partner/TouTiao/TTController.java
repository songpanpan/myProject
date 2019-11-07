package com.yueyou.adreader.service.advertisement.partner.TouTiao;

import android.content.Context;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.yueyou.adreader.service.advertisement.partner.AdControllerBase;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Widget;

public class TTController extends AdControllerBase {
    private TTAdManager mttAdManager = null;
    private Context mContext;
    public void init(Context context, String appKey) {
        mContext = context;
        mttAdManager = TTAdSdk.init(mContext, new TTAdConfig.Builder()
                .appId(appKey)
                .useTextureView(true)
                .appName(Widget.getAppName(mContext))
                .allowShowNotify(true)
                .allowShowPageWhenScreenLock(true)
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK).allowShowPageWhenScreenLock(true)
                //.debug(true)
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_4G, TTAdConstant.NETWORK_STATE_3G).build());
    }

    @Override
    public void loadSplash(AdContent adContent, ViewGroup viewGroup) {
        Splash.show(mContext, mttAdManager, viewGroup, adContent);
    }

    @Override
    public void loadBookShelfBanner(AdContent adContent, ViewGroup viewGroup) {
        NativeFeedAd.show(mContext, mttAdManager, viewGroup, adContent);
    }

    @Override
    public void loadReadPageBanner(AdContent adContent, ViewGroup viewGroup) {
        if (adContent.getType() == 2) {
            LogUtil.e("ExpressBannerAd show");
            ExpressBannerAd.show(mContext, mttAdManager, viewGroup, adContent);
        } else {
            NativeBannerAd.show(mContext, mttAdManager, viewGroup, adContent);
        }
    }

    @Override
    public void loadReadPageScreen(AdContent adContent, ViewGroup viewGroup) {
        NativeFeedAd.show(mContext, mttAdManager, viewGroup, adContent);
    }

    @Override
    public void loadRewardVideoAd(AdContent adContent, ViewGroup viewGroup, String rewardName, int rewardAmount, String extra) {
        RewardVideo.show(mContext, mttAdManager, adContent, rewardName, rewardAmount, extra);
    }

    @Override
    public void loadWebBanner(AdContent adContent, ViewGroup viewGroup) {
        //NativeBannerAd.show(mContext, mttAdManager, viewGroup, adContent);
        ExpressBannerAd.show(mContext, mttAdManager, viewGroup, adContent);
    }
}
