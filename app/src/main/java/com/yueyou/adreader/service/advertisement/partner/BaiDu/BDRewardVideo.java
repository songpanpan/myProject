package com.yueyou.adreader.service.advertisement.partner.BaiDu;

import android.content.Context;

import com.baidu.mobads.rewardvideo.RewardVideoAd;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.LogUtil;

public class BDRewardVideo {
    private static RewardVideoAd rewardVideoAd;
    public static void show(Context context, AdContent adContent) {
        rewardVideoAd = new RewardVideoAd(context, adContent.getPlaceId(), new RewardVideoAd.RewardVideoAdListener() {
            @Override
            public void onAdShow() {
                AdEvent.getInstance().adShow(adContent, null, null);
            }

            @Override
            public void onAdClick() {
                AdEvent.getInstance().adClicked(adContent);
            }

            @Override
            public void onAdClose(float v) {
                AdEvent.getInstance().adRewardVideoClosed(adContent);
            }

            @Override
            public void onAdFailed(String s) {
                LogUtil.e("baidu onAdFailed:"+s);
                AdEvent.getInstance().loadAdError(context, adContent, 1, "onVideoError");
                AdEvent.getInstance().loadAdError(adContent, 0, s);
            }

            @Override
            public void onVideoDownloadSuccess() {
                rewardVideoAd.show();
            }

            @Override
            public void onVideoDownloadFailed() {
                AdEvent.getInstance().loadAdError(context, adContent, 1, "onVideoError");
                LogUtil.e("baidu onVideoDownloadFailed:");
                AdEvent.getInstance().loadAdError(adContent, 0, "onVideoDownloadFailed");
            }

            @Override
            public void playCompletion() {
                AdEvent.getInstance().adRewardVideoCompleted(context, adContent);
            }
        }, false);
        rewardVideoAd.load();
    }
}
