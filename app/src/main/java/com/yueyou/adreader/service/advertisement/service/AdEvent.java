package com.yueyou.adreader.service.advertisement.service;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.yueyou.adreader.service.analytics.AnalyticsEngine;
import com.yueyou.adreader.service.analytics.Bi;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.service.model.AdContent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdEvent {
    private List<EventInfo> mEventListener;
    private static AdEvent mAdEvent;
    private Context mContext;

    private class EventInfo {
        private AdEventObject object;
        private int siteId;
        private boolean pause;

        public EventInfo(AdEventObject object, int siteId) {
            this.object = object;
            this.siteId = siteId;
            this.pause = false;
        }
    }

    public static AdEvent getInstance() {
        if (mAdEvent == null) {
            synchronized (AdEvent.class) {
                if (mAdEvent == null) {
                    mAdEvent = new AdEvent();
                }
            }
        }
        return mAdEvent;
    }

    public void release() {
        try {
            mEventListener.clear();
            mAdEvent = null;
        } catch (Exception e) {

        }
    }

    public void setContext(Context context) {
        try {
            if (mEventListener != null)
                mEventListener.clear();
            mContext = context;
        } catch (Exception e) {

        }
    }

    public AdEvent() {
        mEventListener = new CopyOnWriteArrayList<>();
    }

    public void add(AdEventObject adEventObject, int siteId) {
        try {
            int index = get(adEventObject, siteId);
            if (index >= 0) {
                mEventListener.get(index).pause = false;
                return;
            }
            mEventListener.add(new EventInfo(adEventObject, siteId));
        } catch (Exception e) {

        }
    }

    public void pause(AdEventObject adEventObject, int siteId) {
        try {
            int index = get(adEventObject, siteId);
            if (index < 0)
                return;
            mEventListener.get(index).pause = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(AdEventObject adEventObject) {
        try {
            int index = get(adEventObject, 0);
            if (index < 0)
                return;
            mEventListener.remove(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int get(AdEventObject adEventObject, int siteId) {
        try {
            for (int i = 0; i < mEventListener.size(); i++) {
                if (mEventListener.get(i).object == adEventObject
                        && (siteId == 0 || mEventListener.get(i).siteId == siteId))
                    return i;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public AdEventObject.AdViewSize adViewSize(AdContent adContent, ViewGroup viewGroup) {
        try {
            for (EventInfo eventInfo : mEventListener) {
                if (eventInfo.siteId == adContent.getSiteId()
                        && !eventInfo.pause)
                    return eventInfo.object.adViewSize(adContent, viewGroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AdEventObject.AdViewSize(0, 0);
    }

    public void adShowPre(AdContent adContent, ViewGroup viewGroup, View adView) {
        try {
            AdEngine.getInstance().loadAdSuccess(adContent);
            Bi.advertisementLoad(mContext, adContent.getSiteId(), adContent.getCp(), true);
            for (EventInfo eventInfo : mEventListener) {
                if (eventInfo.siteId == adContent.getSiteId() && !eventInfo.pause)
                    eventInfo.object.adShowPre(adContent, viewGroup, adView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public View[] adShowPre(AdContent adContent, ViewGroup viewGroup, String adTitle, String adDesc, String buttonStr, String imgUrl) {
        try {
            AdEngine.getInstance().loadAdSuccess(adContent);
            Bi.advertisementLoad(mContext, adContent.getSiteId(), adContent.getCp(), true);
            for (EventInfo eventInfo : mEventListener) {
                if (eventInfo.siteId == adContent.getSiteId() && !eventInfo.pause)
                    return eventInfo.object.adShowPre(adContent, viewGroup, adTitle, adDesc, buttonStr, imgUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public View[] adShowPre(AdContent adContent, ViewGroup viewGroup, String adTitle, String adDesc, String buttonStr, View adView) {
        try {
            AdEngine.getInstance().loadAdSuccess(adContent);
            Bi.advertisementLoad(mContext, adContent.getSiteId(), adContent.getCp(), true);
            for (EventInfo eventInfo : mEventListener) {
                if (eventInfo.siteId == adContent.getSiteId() && !eventInfo.pause)
                    return eventInfo.object.adShowPre(adContent, viewGroup, adTitle, adDesc, buttonStr, adView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public View[] adShow(AdContent adContent, ViewGroup viewGroup, String adTitle, String adDesc, String buttonStr, String imgUrl,
                         AdEventListener adEventListener) {
        try {
            for (EventInfo eventInfo : mEventListener) {
                if (eventInfo.siteId == adContent.getSiteId() && !eventInfo.pause)
                    return eventInfo.object.adShow(adContent, viewGroup, adTitle, adDesc, buttonStr, imgUrl, adEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void uploadAdShowed(AdContent adContent) {
        AnalyticsEngine.advertisement(mContext, adContent.getSiteId(), adContent.getCp(), false);
        ThirdAnalytics.onEventAd(mContext, ThirdAnalytics.getMapAd(adContent, 0, "onAdShow"));
    }

    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {
        try {
            uploadAdShowed(adContent);
            for (EventInfo eventInfo : mEventListener) {
                if (eventInfo.siteId == adContent.getSiteId() && !eventInfo.pause)
                    eventInfo.object.adShow(adContent, viewGroup, adView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void adShow(AdContent adContent, AdEventListener adEventListener) {
        try {
            uploadAdShowed(adContent);
            for (EventInfo eventInfo : mEventListener) {
                if (eventInfo.siteId == adContent.getSiteId() && !eventInfo.pause)
                    eventInfo.object.adShow(adContent, adEventListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void adClicked(AdContent adContent) {
        try {
            AnalyticsEngine.advertisement(mContext, adContent.getSiteId(), adContent.getCp(), true);
            for (EventInfo eventInfo : mEventListener) {
                if (eventInfo.siteId == adContent.getSiteId() && !eventInfo.pause)
                    eventInfo.object.adClicked();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void adClosed(AdContent adContent) {
        try {
            for (EventInfo eventInfo : mEventListener) {

                if (eventInfo.siteId == adContent.getSiteId() && !eventInfo.pause)
                    eventInfo.object.adClosed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void adRewardVideoClosed(AdContent adContent) {
        try {
            AnalyticsEngine.advertisementEnd(mContext, adContent.getSiteId(), adContent.getCp(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void adRewardVideoCompleted(Context context, AdContent adContent) {
        try {
            for (EventInfo eventInfo : mEventListener) {
                //if (eventInfo.siteId == adContent.getSiteId()) //激励视频关闭当前广告
                eventInfo.object.adRewardVideoCompleted(context, adContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAdError(AdContent adContent, int errorCode, String errorMsg) {
        try {
            //ThirdAnalytics.onEventAd(mContext, ThirdAnalytics.getMapAd(adContent, 1, Utils.format("onError(%d, %s)", errorCode, errorMsg)));
            Bi.advertisementLoad(mContext, adContent.getSiteId(), adContent.getCp(), false);
            AdEngine.getInstance().loadAdError(adContent);
            for (EventInfo eventInfo : mEventListener) {
                if (eventInfo.siteId == adContent.getSiteId() && !eventInfo.pause)
                    eventInfo.object.adError(adContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAdError(Context context,AdContent adContent, int errorCode, String errorMsg) {
        try {
            Bi.advertisementLoad(mContext, adContent.getSiteId(), adContent.getCp(), false);
            AdEngine.getInstance().loadAdError(adContent);
            for (EventInfo eventInfo : mEventListener) {
                //if (eventInfo.siteId == adContent.getSiteId()) //激励视频关闭当前广告
                eventInfo.object.adError(context,adContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface AdEventListener {
        void AdShowed();
    }
}
