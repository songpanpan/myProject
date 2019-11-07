package com.yueyou.adreader.service.advertisement.service;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.yueyou.adreader.service.Action;
import com.yueyou.adreader.service.advertisement.partner.AdControllerBase;
import com.yueyou.adreader.service.advertisement.partner.BaiDu.BDController;
import com.yueyou.adreader.service.advertisement.partner.ChuangShen.CSController;
import com.yueyou.adreader.service.advertisement.partner.GuangDianTong.GDTController;
import com.yueyou.adreader.service.advertisement.partner.HanBo.HanBoController;
import com.yueyou.adreader.service.advertisement.partner.KeDaXunFei.KDXFController;
import com.yueyou.adreader.service.advertisement.partner.SoGou.SGController;
import com.yueyou.adreader.service.advertisement.partner.TouTiao.TTController;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.service.model.AdContentList;
import com.yueyou.adreader.util.Widget;

public class AdEngine {
    private static AdEngine mAdEngine = null;
    private Context mContext = null;
    private TTController mTTController = null;
    private GDTController mGDTController = null;
    private SGController mSGController = null;
    private CSController mCSController = null;
    private HanBoController mHanBoController = null;
    private KDXFController mKDXFController = null;
    private BDController mBDController = null;
    private AdContentList mAdReadPageBannerList;
    private AdContentList mAdReadPageScreenList;
    private AdContentList mAdSplashList;
    private AdContentList mAdResumeSplashList;

    public static AdEngine getInstance() {
        if (mAdEngine == null) {
            synchronized (AdEngine.class) {
                if (mAdEngine == null) {
                    mAdEngine = new AdEngine();
                    mAdEngine.mAdReadPageBannerList = new AdContentList();
                    mAdEngine.mAdReadPageScreenList = new AdContentList();
                    mAdEngine.mAdSplashList = new AdContentList();
                    mAdEngine.mAdResumeSplashList = new AdContentList();

                }
            }
        }
        return mAdEngine;
    }

    public void resetReadPageBannerAdList() {
        mAdReadPageBannerList.reset();
    }

    public void resetReadPageScreenAdList() {
        mAdReadPageScreenList.reset();
    }

    public void resetSplashAdList() {
        mAdSplashList.reset();
    }

    private synchronized AdControllerBase getPartner(String cp, String appKey) {
        if ("toutiao".equals(cp)) {
            if (mTTController == null) {
                mTTController = new TTController();
                mTTController.init(mContext, appKey);
            }
            return mTTController;
        } else if ("guangdiantong".equals(cp)) {
            if (mGDTController == null) {
                mGDTController = new GDTController();
                mGDTController.init(mContext, appKey);
            }
            return mGDTController;
        } else if ("sogou".equals(cp)) {
            if (mSGController == null) {
                mSGController = new SGController();
                mSGController.init(mContext, appKey);
            }
            return mSGController;
        } else if ("chuangshen".equals(cp)) {
            if (mCSController == null) {
                mCSController = new CSController();
                mCSController.init(mContext, appKey);
            }
            return mCSController;
        } else if ("hanbo".equals(cp)) {
            if (mHanBoController == null) {
                mHanBoController = new HanBoController();
                mHanBoController.init(mContext, appKey);
            }
            return mHanBoController;
        } else if ("kedaxunfei".equals(cp)) {
            if (mKDXFController == null) {
                mKDXFController = new KDXFController();
                mKDXFController.init(mContext, appKey);
            }
            return mKDXFController;
        } else if ("baidu".equals(cp)) {
            if (mBDController == null) {
                mBDController = new BDController();
                mBDController.init(mContext, appKey);
            }
            return mBDController;
        }
        return null;
    }

    public void setContext(Context context) {
        mContext = context;
        AdEvent.getInstance().setContext(context);
    }

    public void release() {
        AdEvent.getInstance().release();
        mAdEngine = null;
    }

    private void getSignRewardAdContent(String extra) {
        Action.getInstance().getAdContent(mContext, AdSite.signRewardVideo, (Object object) -> {
            try {
                AdContent adContent = (AdContent) Widget.jsonToObjectByMapStr(object, AdContent.class);
                if (adContent == null)
                    return;
                ((Activity) mContext).runOnUiThread(() -> {
                    try {
                        if (adContent.getSiteId() == AdSite.signRewardVideo) {
                            loadSignRewardVideoAd(adContent, extra);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void getAdContent(int siteId, ViewGroup viewGroup) {
        Action.getInstance().getAdContent(mContext, siteId, (Object object) -> {
            try {
                AdContent adContent = (AdContent) Widget.jsonToObjectByMapStr(object, AdContent.class);
                if (adContent == null)
                    return;
                if (siteId > AdSite.errorFlag) {
                    adContent.setNativeErrorFlag(true);
                } else {
                    adContent.setNativeErrorFlag(false);
                }
                ((Activity) mContext).runOnUiThread(() -> {
                    try {
                        if (adContent.getSiteId() == AdSite.splash
                                || adContent.getSiteId() == AdSite.resumeSplash) {
                            loadSplashAd(adContent, viewGroup);
                        } else if (adContent.getSiteId() == AdSite.bookShelfBanner) {
                            loadBookShelfBannerAd(adContent, viewGroup);
                        } else if (adContent.getSiteId() == AdSite.rewardVideo) {
                            loadRewardVideoAd(adContent);
                        } else if (adContent.getSiteId() == AdSite.bookShelfHeader) {
                            AdEvent.getInstance().adShow(adContent, viewGroup, null);
                        } else if (adContent.getSiteId() == AdSite.newUserPopWindow) {
                            AdEvent.getInstance().adShow(adContent, null, null);
                        } else if (adContent.getSiteId() == AdSite.startPopupWindow) {
                            AdEvent.getInstance().adShow(adContent, null, null);
                        } else if (adContent.getSiteId() == AdSite.startPopupWindowForTuiA) {
//                            if (mTAController == null) {
//                                mTAController = new TAController();
//                                mTAController.loadTuiADlg(mContext, adContent);
//                            }
                        } else if (adContent.getSiteId() == AdSite.bookShelfCover) {
                            AdEvent.getInstance().adShow(adContent, null, null);
                        } else if (adContent.getSiteId() == AdSite.bookShelfBottomIcon) {
                            AdEvent.getInstance().adShow(adContent, viewGroup, null);
                        } else if (adContent.getSiteId() == AdSite.webBanner) {
                            loadWebBannerAd(adContent, viewGroup);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void getAdContentList(int siteId, ViewGroup viewGroup, int bookId, int chapterId, boolean isVipChapter) {
        Action.getInstance().getAdContentList(mContext, siteId, (Object object) -> {
            try {
                AdContentList list = (AdContentList) Widget.jsonToObjectByMapStr(object, AdContentList.class);
                if (list == null) {
                    return;
                }
                if (siteId == AdSite.readPageBanner) {
                    mAdReadPageBannerList.setAdContentList(list.getAdContentList());
                    mAdReadPageBannerList.setDefaultAdContentList(list.getDefaultAdContentList());
                    mAdReadPageBannerList.set(bookId, chapterId, true);
                    mAdReadPageBannerList.setMode(list.getMode());
                    mAdReadPageBannerList.setFailThreshold(list.getFailThreshold());
                    mAdReadPageBannerList.setFailExpires(list.getFailExpires());
                    mAdReadPageBannerList.setRetryCount(list.getRetryCount());
                    mAdReadPageBannerList.setDisplayFlag(list.getDisplayFlag());
                    mAdReadPageBannerList.setEnableMatNotify(list.getEnableMatNotify());
                    mAdReadPageBannerList.setWay(list.getWay());
                    loadReadPageBannerAd(viewGroup, bookId, chapterId, isVipChapter, null);
                } else if (siteId == AdSite.readePageScreen) {
                    mAdReadPageScreenList.setAdContentList(list.getAdContentList());
                    mAdReadPageScreenList.setDefaultAdContentList(list.getDefaultAdContentList());
                    mAdReadPageScreenList.set(bookId, chapterId, true);
                    mAdReadPageScreenList.setMode(list.getMode());
                    mAdReadPageScreenList.setFailThreshold(list.getFailThreshold());
                    mAdReadPageScreenList.setFailExpires(list.getFailExpires());
                    mAdReadPageScreenList.setRetryCount(list.getRetryCount());
                    mAdReadPageScreenList.setDisplayFlag(list.getDisplayFlag());
                    mAdReadPageScreenList.setEnableMatNotify(list.getEnableMatNotify());
                    mAdReadPageScreenList.setWay(list.getWay());
                    loadReadPageScreenAd(viewGroup, bookId, chapterId, isVipChapter, null);
                } else if (siteId == AdSite.splash) {
                    mAdSplashList.setAdContentList(list.getAdContentList());
                    mAdSplashList.setDefaultAdContentList(list.getDefaultAdContentList());
                    mAdSplashList.set(bookId, chapterId, true);
                    mAdSplashList.setMode(list.getMode());
                    mAdSplashList.setFailThreshold(list.getFailThreshold());
                    mAdSplashList.setFailExpires(list.getFailExpires());
                    mAdSplashList.setRetryCount(list.getRetryCount());
                    mAdSplashList.setDisplayFlag(list.getDisplayFlag());
                    mAdSplashList.setEnableMatNotify(list.getEnableMatNotify());
                    mAdSplashList.setWay(list.getWay());
                    loadSplashAd(null, viewGroup);
                } else if (siteId == AdSite.resumeSplash) {
                    mAdResumeSplashList.setAdContentList(list.getAdContentList());
                    mAdResumeSplashList.setDefaultAdContentList(list.getDefaultAdContentList());
                    mAdResumeSplashList.set(bookId, chapterId, true);
                    mAdResumeSplashList.setMode(list.getMode());
                    mAdResumeSplashList.setFailThreshold(list.getFailThreshold());
                    mAdResumeSplashList.setFailExpires(list.getFailExpires());
                    mAdResumeSplashList.setRetryCount(list.getRetryCount());
                    mAdResumeSplashList.setDisplayFlag(list.getDisplayFlag());
                    mAdResumeSplashList.setEnableMatNotify(list.getEnableMatNotify());
                    mAdResumeSplashList.setWay(list.getWay());
                    loadResumeSplashAd(null, viewGroup);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, bookId, chapterId);
    }

    private boolean isRewardVideoEffect() {
        if (mContext != null) {
            long time = DataSHP.getRewardVideoViewTime(mContext);
            if (System.currentTimeMillis() < time)
                return true;
        }
        return false;
    }

    public void loadAdError(AdContent adContent) {
        if (adContent.getSiteId() == AdSite.readPageBanner) {
            mAdReadPageBannerList.loadError(adContent);
        } else if (adContent.getSiteId() == AdSite.readePageScreen) {
            mAdReadPageScreenList.loadError(adContent);
        } else if (adContent.getSiteId() == AdSite.splash) {
            mAdSplashList.loadError(adContent);
        }
    }

    public void loadAdSuccess(AdContent adContent) {
        if (adContent.getSiteId() == AdSite.readPageBanner) {
            mAdReadPageBannerList.loadSuccess(adContent);
        } else if (adContent.getSiteId() == AdSite.readePageScreen) {
            mAdReadPageScreenList.loadSuccess(adContent);
        } else if (adContent.getSiteId() == AdSite.splash) {
            mAdSplashList.loadSuccess(adContent);
        }
    }


    public void loadSplashAd(AdContent adContentPre, ViewGroup viewGroup) {
        if (!mAdSplashList.isStatus()) {
            getAdContentList(AdSite.splash, viewGroup, 0, 0, false);
            return;
        }
        AdContent adContent = mAdSplashList.getAdContent(adContentPre);
        if (adContent == null)
            return;
        AdControllerBase adControllerBase = getPartner(adContent.getCp(), adContent.getAppKey());
        if (adControllerBase == null) {
            return;
        }
        ((Activity) mContext).runOnUiThread(() -> {
            adControllerBase.loadSplash(adContent, viewGroup);
        });
    }

    public void loadResumeSplashAd(AdContent adContentPre, ViewGroup viewGroup) {
        if (!mAdResumeSplashList.isStatus()) {
            getAdContentList(AdSite.resumeSplash, viewGroup, 0, 0, false);
            return;
        }
        AdContent adContent = mAdResumeSplashList.getAdContent(adContentPre);
        if (adContent == null)
            return;
        AdControllerBase adControllerBase = getPartner(adContent.getCp(), adContent.getAppKey());
        if (adControllerBase == null) {
            return;
        }
        ((Activity) mContext).runOnUiThread(() -> {
            adControllerBase.loadSplash(adContent, viewGroup);
        });
    }

    public void loadSplashAd(ViewGroup viewGroup) {
//        getAdContent(AdSite.splash, viewGroup);
        getAdContentList(AdSite.splash, viewGroup, 0, 0, false);
    }

    //后台启动广告
    public void loadResumeSplashAd(ViewGroup viewGroup) {
//        getAdContent(AdSite.resumeSplash, viewGroup);
        getAdContentList(AdSite.resumeSplash, viewGroup, 0, 0, false);
    }

    //新用户弹框
    public void loadNewUserPopWindowAd() {
        getAdContent(AdSite.newUserPopWindow, null);
    }

    //启动弹框
    public void loadStartPopWindowAd() {
        getAdContent(AdSite.startPopupWindow, null);
    }

    //推啊弹框
    public void loadTuiAPopDlg() {
        getAdContent(AdSite.startPopupWindowForTuiA, null);
    }



    public void loadBookShelfBannerAd(ViewGroup viewGroup, boolean fromError) {
//        if (isRewardVideoEffect())
//            return;
        getAdContent(fromError ? AdSite.bookShelfBanner | AdSite.errorFlag : AdSite.bookShelfBanner, viewGroup);
    }

    private void loadBookShelfBannerAd(AdContent adContent, ViewGroup viewGroup) {
        if ("yueyou".equals(adContent.getCp())) {
            AdEvent.getInstance().adShowPre(adContent, null, null);
            return;
        }
        AdControllerBase adControllerBase = getPartner(adContent.getCp(), adContent.getAppKey());
        if (adControllerBase == null) {
            return;
        }
        adControllerBase.loadBookShelfBanner(adContent, viewGroup);
    }

    public void loadBookShelfHeaderAd(ViewGroup viewGroup) {
        getAdContent(AdSite.bookShelfHeader, viewGroup);
    }

    public void loadBookShelfCoverAd() {
        getAdContent(AdSite.bookShelfCover, null);
    }

    public void loadBookShelfIconAd() {
        getAdContent(AdSite.bookShelfBottomIcon, null);
    }

    public boolean hideReadPageBannerAd(boolean isVipChapter) {
        if (!mAdReadPageBannerList.isStatus())
            return false;
        return !mAdReadPageBannerList.showAd(isVipChapter);
    }

    public void loadReadPageBannerAd(ViewGroup viewGroup, int bookId, int chapterId, boolean isVipChapter, AdContent adContentPre) {
        if (isRewardVideoEffect())
            return;
        if (mAdReadPageBannerList.getBookId() != bookId)
            resetReadPageBannerAdList();
        if (!mAdReadPageBannerList.isStatus()
                && chapterId != mAdReadPageBannerList.getChapterId()) {
            getAdContentList(AdSite.readPageBanner, viewGroup, bookId, chapterId, isVipChapter);
            return;
        }
        if (!mAdReadPageBannerList.showAd(isVipChapter))
            return;
        AdContent adContent = mAdReadPageBannerList.getAdContent(adContentPre);
        if (adContent == null)
            return;
        adContent.setNativeErrorFlag(adContentPre == null ? false : true);
        loadReadPageBannerAd(adContent, viewGroup);
    }

    private void loadReadPageBannerAd(AdContent adContent, ViewGroup viewGroup) {
        AdControllerBase adControllerBase = getPartner(adContent.getCp(), adContent.getAppKey());
        if (adControllerBase == null) {
            return;
        }
        adControllerBase.loadReadPageBanner(adContent, viewGroup);
    }

    public boolean hideReadPageScreenAd(boolean isVipChapter) {
        if (!mAdReadPageScreenList.isStatus())
            return false;
        return !mAdReadPageScreenList.showAd(isVipChapter);
    }

    public int readPageScreenAdStyle() {
        return mAdReadPageScreenList.getWay();
    }

    public void loadReadPageScreenAd(ViewGroup viewGroup, int bookId, int chapterId, boolean isVipChapter, AdContent adContentPre) {
        if (isRewardVideoEffect())
            return;
        if (mAdReadPageScreenList.getBookId() != bookId)
            resetReadPageScreenAdList();
        if (!mAdReadPageScreenList.isStatus()
                && chapterId != mAdReadPageScreenList.getChapterId()) {
            getAdContentList(AdSite.readePageScreen, viewGroup, bookId, chapterId, isVipChapter);
            return;
        }
        if (!mAdReadPageScreenList.showAd(isVipChapter))
            return;
        AdContent adContent = mAdReadPageScreenList.getAdContent(adContentPre);
        if (adContent == null)
            return;
        adContent.setNativeErrorFlag(adContentPre == null ? false : true);
        loadReadPageScreenAd(adContent, viewGroup);
    }

    private void loadReadPageScreenAd(AdContent adContent, ViewGroup viewGroup) {
        AdControllerBase adControllerBase = getPartner(adContent.getCp(), adContent.getAppKey());
        if (adControllerBase == null) {
            return;
        }
        adControllerBase.loadReadPageScreen(adContent, viewGroup);
    }

    public void loadRewardVideoAd() {
        getAdContent(AdSite.rewardVideo, null);
    }

    private void loadRewardVideoAd(AdContent adContent) {
        AdControllerBase adControllerBase = getPartner(adContent.getCp(), adContent.getAppKey());
        if (adControllerBase == null) {
            return;
        }
        adControllerBase.loadRewardVideoAd(adContent, null, "免广告", 20, "media_extra");
    }

    public void loadSignRewardVideoAd(String extra) {
        getSignRewardAdContent(extra);
    }

    private void loadSignRewardVideoAd(AdContent adContent, String extra) {
        AdControllerBase adControllerBase = getPartner(adContent.getCp(), adContent.getAppKey());
        if (adControllerBase == null) {
            return;
        }
        adControllerBase.loadRewardVideoAd(adContent, null, "签到", 1, extra);
    }

    public void loadWebBannerAd(ViewGroup viewGroup, boolean fromError) {
        getAdContent(fromError ? AdSite.webBanner | AdSite.errorFlag : AdSite.webBanner, viewGroup);
    }

    private void loadWebBannerAd(AdContent adContent, ViewGroup viewGroup) {
        AdControllerBase adControllerBase = getPartner(adContent.getCp(), adContent.getAppKey());
        if (adControllerBase == null) {
            return;
        }
        adControllerBase.loadWebBanner(adContent, viewGroup);
    }

    public boolean enableMatNotify(int siteId) {
        if (siteId == AdSite.readePageScreen && mAdReadPageScreenList.isStatus()
                && mAdReadPageScreenList.getEnableMatNotify() == 0)
            return false;
        if (siteId == AdSite.readPageBanner && mAdReadPageBannerList.isStatus()
                && mAdReadPageBannerList.getEnableMatNotify() == 0)
            return false;
        return true;
    }
}
