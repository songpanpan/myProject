package com.yueyou.adreader.service.advertisement.partner.TouTiao;

import android.app.Activity;
import android.content.Context;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.AdContent;

public class RewardVideo {
    public static void show(Context context, TTAdManager ttAdManager, AdContent adContent,
                            String rewardName, int rewardAmount, String extra) {
        AdEventObject.AdViewSize adViewSize = AdEvent.getInstance().adViewSize(adContent, null);
        if (adViewSize == null || adViewSize.width == 0 || adViewSize.height == 0)
            return;
        final AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adContent.getPlaceId())
                .setAdCount(1)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(adViewSize.width, adViewSize.height)
                .setRewardName("分钟") //奖励的名称
                .setRewardAmount(20)  //奖励的数量
                .setUserID(DataSHP.getUserId(context))//用户id,必传参数
                .setMediaExtra(extra) //附加参数，可选
                .setOrientation(TTAdConstant.VERTICAL)
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        TTAdNative ttAdNative = ttAdManager.createAdNative(context);
        ttAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                AdEvent.getInstance().loadAdError(context, adContent, 1, "onVideoError");
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                ThirdAnalytics.onEventAd(context, ThirdAnalytics.getMapAd(adContent, 0, "onRewardVideoCached"));
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
//               ad.setShowDownLoadBar(false);
                ad.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        AdEvent.getInstance().adShowPre(adContent, null, null);
                        AdEvent.getInstance().adShow(adContent, null, null);
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        AdEvent.getInstance().adClicked(adContent);
                    }

                    @Override
                    public void onAdClose() {
                        AdEvent.getInstance().adRewardVideoClosed(adContent);
                        AdEvent.getInstance().adClosed(adContent);
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {

                    }

                    @Override
                    public void onVideoError() {
                        AdEvent.getInstance().loadAdError(adContent, 1, "onVideoError");
                    }

                    @Override
                    public void onSkippedVideo() {
                        ThirdAnalytics.onEventAd(context, ThirdAnalytics.getMapAd(adContent, 1, "onSkippedVideo"));
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        try {
                            AdEvent.getInstance().adRewardVideoCompleted(context, adContent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                ad.showRewardVideoAd((Activity) context);
            }
        });
    }
}
