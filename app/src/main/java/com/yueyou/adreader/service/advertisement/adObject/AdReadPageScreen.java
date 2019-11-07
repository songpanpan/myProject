package com.yueyou.adreader.service.advertisement.adObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.WebViewActivity;
import com.yueyou.adreader.service.Action;
import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.service.db.BookFileEngine;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.Const;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Utils;

public class AdReadPageScreen extends AdEventObject {
    private int mPageNumForDisplay = -1;
    private boolean mAdReady = false;
    private ViewGroup mAdRootContianer;
    private ViewGroup mAdParentContianer;
    private View mAdMixScreen;
    private View mAdGDTScreen;
    private AdRewardVideo mAdRewardVideo;
    private AdEvent.AdEventListener mAdEventListener = null;
    private int mBookId = 0;
    private int mChapterId = 0;
    private boolean mIsVipChapter = false;
    private boolean mIsMiddle = false;
    private View mCurAdView;
    private boolean mMask = false;
    private View mTTVideoView;
    private int mTextColor;
    private AdReadPageScreenListener mAdReadPageScreenListener;
    private boolean mKeDaXunFei;

    private int adbgColor;
    private int bgCr;
    private int titleCr;
    private int descCr;
    private boolean mask;
    private boolean parchment;

    public interface AdReadPageScreenListener {
        void adReadPageScreenHide();
    }

    public AdReadPageScreen() {
        super(AdSite.readePageScreen);
        mAdRewardVideo = new AdRewardVideo();
    }

    public void init(ViewGroup viewGroup, AdReadPageScreenListener adReadPageScreenListener) {
        mAdReadPageScreenListener = adReadPageScreenListener;
        mAdRootContianer = viewGroup.findViewById(R.id.ad_container);
        hide();
        mAdParentContianer = mAdRootContianer.findViewById(R.id.ad_container_page);
        displayRewardVideo(mAdRootContianer.findViewById(R.id.rewardVideo));
        mAdRootContianer.findViewById(R.id.vip_toast).setOnClickListener(mOnClickListener);
    }

    private void displayRewardVideo(View rewardVideoView) {
        boolean isJlCtl = DataSHP.checkCtlContent(rewardVideoView.getContext(), Const.KEY_JL_BTN_SHOW);
        rewardVideoView.setVisibility(isJlCtl ? View.GONE : View.VISIBLE);
        if (!isJlCtl) {
            rewardVideoView.setOnClickListener((View v) -> {
                mAdRewardVideo.show();
            });
        }
    }

    private void loadAdLayout() {
        if (mAdMixScreen != null)
            return;
        if (mIsMiddle) {
            mAdMixScreen = LayoutInflater.from(mAdRootContianer.getContext()).inflate(R.layout.ad_read_page_screen_mix_middle, null, false);
            mAdGDTScreen = LayoutInflater.from(mAdRootContianer.getContext()).inflate(R.layout.ad_read_page_screen_gdt_middle, null, false);
            displayRewardVideo(mAdMixScreen.findViewById(R.id.bottom_toast));
            displayRewardVideo(mAdGDTScreen.findViewById(R.id.bottom_toast));
        } else {
            mAdMixScreen = LayoutInflater.from(mAdRootContianer.getContext()).inflate(R.layout.ad_read_page_screen_mix, null, false);
            mAdGDTScreen = LayoutInflater.from(mAdRootContianer.getContext()).inflate(R.layout.ad_read_page_screen_gdt, null, false);
        }
        mAdGDTScreen.findViewById(R.id.button).setVisibility(View.GONE);
        mAdGDTScreen.findViewById(R.id.img_close).setOnClickListener(mOnClickListener);
        mAdMixScreen.findViewById(R.id.img_close).setOnClickListener(mOnClickListener);
        setColor(adbgColor, bgCr, titleCr, descCr, mask, parchment);
    }

    public void setColor(int adbgColor, int bgCr, int titleCr, int descCr, boolean mask, boolean parchment) {
        try {
            this.adbgColor = adbgColor;
            this.bgCr = bgCr;
            this.titleCr = titleCr;
            this.descCr = descCr;
            this.mask = mask;
            this.parchment = parchment;
            if (parchment)
                mAdRootContianer.setBackgroundResource(R.drawable.parchment);
            else {
                mAdRootContianer.setAlpha(1.0f);
                mAdRootContianer.setBackgroundColor(bgCr);
            }
            ((TextView) mAdRootContianer.findViewById(R.id.toast)).setTextColor(titleCr);
//            ((TextView) mAdRootContianer.findViewById(R.id.rewardVideo)).setTextColor(titleCr);
            mAdRootContianer.findViewById(R.id.under_line).setBackgroundColor(titleCr);
            ((TextView) mAdMixScreen.findViewById(R.id.text_title)).setTextColor(titleCr);
            ((TextView) mAdMixScreen.findViewById(R.id.text_desc)).setTextColor(titleCr);
            ((TextView) mAdGDTScreen.findViewById(R.id.text_title)).setTextColor(titleCr);
            ((TextView) mAdGDTScreen.findViewById(R.id.text_desc)).setTextColor(titleCr);
            if (parchment) {
                mAdMixScreen.findViewById(R.id.ad_bottom_bg).setBackgroundColor(Color.parseColor("#FFE1D3C2"));
                mAdGDTScreen.findViewById(R.id.ad_bottom_bg).setBackgroundColor(Color.parseColor("#FFE1D3C2"));
                GradientDrawable drawable = new GradientDrawable();
                //设置外形为为矩形
                drawable.setShape(GradientDrawable.RECTANGLE);
                //设置外形为为矩形,同上一句功能一致
                //drawable.setGradientType(GradientDrawable.RECTANGLE); 
                //设置圆角角度
                float cornerSize = Utils.dp2px(mAdMixScreen.getContext(), 4);
                float[] radii = new float[]{0, 0, 0, 0, cornerSize, cornerSize, cornerSize, cornerSize};
                drawable.setCornerRadii(radii);
                drawable.setAlpha(76);
                //设置背景色
                drawable.setColor(Color.parseColor("#FFE1D3C2"));
                mAdMixScreen.findViewById(R.id.rl_bottom).setBackground(drawable);
                mAdGDTScreen.findViewById(R.id.rl_bottom).setBackground(drawable);
                mAdMixScreen.findViewById(R.id.ad_bottom_bg).setAlpha(0.3f);
                mAdGDTScreen.findViewById(R.id.ad_bottom_bg).setAlpha(0.3f);
            } else {
                mAdMixScreen.findViewById(R.id.ad_bottom_bg).setBackgroundColor(adbgColor);
                mAdGDTScreen.findViewById(R.id.ad_bottom_bg).setBackgroundColor(adbgColor);
                GradientDrawable drawable = new GradientDrawable();
                //设置外形为为矩形
                drawable.setShape(GradientDrawable.RECTANGLE);
                //设置外形为为矩形,同上一句功能一致
                //drawable.setGradientType(GradientDrawable.RECTANGLE); 
                //设置圆角角度
                float cornerSize = Utils.dp2px(mAdMixScreen.getContext(), 4);
                float[] radii = new float[]{0, 0, 0, 0, cornerSize, cornerSize, cornerSize, cornerSize};
                drawable.setCornerRadii(radii);
                //设置背景色
                drawable.setColor(adbgColor);
                mAdMixScreen.findViewById(R.id.rl_bottom).setBackground(drawable);
                mAdGDTScreen.findViewById(R.id.rl_bottom).setBackground(drawable);
                mAdMixScreen.findViewById(R.id.ad_bottom_bg).setAlpha(1f);
                mAdGDTScreen.findViewById(R.id.ad_bottom_bg).setAlpha(1f);
            }


            mMask = mask;
            mTextColor = titleCr;
            refreshMask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            WebViewActivity.show(((Activity) v.getContext()), Url.URL_AD_VIP,
                    WebViewActivity.ACCOUNT, "");
        }
    };
    private Handler mAdHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x20) {//科大讯飞广告显示回调
                try {
                    mAdEventListener.AdShowed();
                } catch (Exception e) {

                }
            }
        }
    };

    @Override
    public void release() {
        super.release();
        mAdRewardVideo.release();
        mAdHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void resume() {
        super.resume();
        mAdRewardVideo.resume();
    }

    @Override
    public void pause() {
        super.pause();
        mAdRewardVideo.pause();
        //AdEngine.getInstance().resetReadPageScreenAdList();
    }

    public boolean isShow() {
        return mAdRootContianer.getVisibility() == View.VISIBLE;
    }

    public void hide() {
        mAdReady = false;
        if (mAdRootContianer.getVisibility() != View.GONE)
            mAdRootContianer.setVisibility(View.GONE);
    }

    public synchronized boolean show(boolean isVipChapter, int pageIndex, boolean chapterEnd, boolean chapterEndPre, boolean refreshAd, boolean isCover) {
        LogUtil.e("isVipChapter:" + isVipChapter + " pageIndex" + pageIndex + " chapterEnd:" + chapterEnd + " refreshAd:" + refreshAd);
        boolean display = false;
        mIsVipChapter = isVipChapter;
        if ((mPageNumForDisplay > 0) && Const.READ_PAGE_COUNT > 0
                && Const.READ_PAGE_COUNT % mPageNumForDisplay == 0 && adViewLoaded()) {
            display = true;
        }
//        if (mPageNumForDisplay == 0 && chapterEnd && adViewLoaded()) {
//            display = true;
//        }
//        if (chapterEndPre && adViewLoaded() && pageIndex % mPageNumForDisplay > 1) {
//            display = true;
//        }
//        Const.IS_DISPLAY_PRE = display;
        if (display && mCurAdView != null) {
            if (mAdParentContianer.getChildCount() > 1)
                mAdParentContianer.removeViewAt(0);
            if (!adViewCanShowed(refreshAd))
                return false;
            if (mAdEventListener != null) {
                if (mKeDaXunFei) {
                    mAdHandler.sendEmptyMessageDelayed(0x20, 300);
                } else {
                    mAdEventListener.AdShowed();
                }
            }
            if (mAdMixScreen == mCurAdView) {
                try {
                    AdContent adContent = (AdContent) mAdMixScreen.getTag();
                    if (adContent.getCp().equals("sogou"))
                        AdEvent.getInstance().uploadAdShowed(adContent);
                } catch (Exception e) {

                }
            }
            if (!mIsMiddle)
                mAdRootContianer.setVisibility(View.VISIBLE);
            mAdReady = false;
            return true;
        }
        if (mAdReady) {
            if (AdEngine.getInstance().hideReadPageScreenAd(mIsVipChapter)) {
                hideAd();
            }
            return false;
        }
        load(null);
        return false;
    }

    private boolean adViewLoaded() {
        if (mIsMiddle) {
            return mAdReady || mCurAdView != null;
        } else {
            return mAdReady || mAdParentContianer.getChildCount() > 0;
        }
    }

    private boolean adViewCanShowed(boolean refresh) {
        try {
            if (mCurAdView.findViewById(R.id.video_poster).getVisibility() == View.VISIBLE) {
//                if (!refresh && ((ViewGroup)mCurAdView.findViewById(R.id.video_poster)).getChildCount() > 0)
//                    return true;
                ((ViewGroup) mCurAdView.findViewById(R.id.video_poster)).removeAllViews();
                ((ViewGroup) mCurAdView.findViewById(R.id.video_poster)).addView(mTTVideoView);
                return true;
            }
            Drawable drawable = null;
            if (mCurAdView.findViewById(R.id.img_poster) != null) {
                drawable = ((ImageView) mCurAdView.findViewById(R.id.img_poster)).getDrawable();
            }
            if (!mIsMiddle) {
                return mAdParentContianer.getChildCount() > 0 && drawable != null;
            } else {
                return drawable != null;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public boolean middleMode() {
        return mIsMiddle;
    }

    public View middleAd() {
        refreshMask();
        if (mIsMiddle)
            return mCurAdView;
        return null;
    }

    private void refreshMask() {
        if (mCurAdView == null || !mIsMiddle)
            return;
        if (mCurAdView == mAdGDTScreen) {
            if (mMask == (mCurAdView.findViewById(R.id.gdt_screen_middle_mask).getVisibility() != View.VISIBLE))
                mCurAdView.findViewById(R.id.gdt_screen_middle_mask).setVisibility(mMask ? View.VISIBLE : View.GONE);
            mCurAdView.findViewById(R.id.gdt_screen_middle_mask).bringToFront();
        } else if (mMask == (mCurAdView.findViewById(R.id.screen_middle_mask).getVisibility() != View.VISIBLE)) {
            mCurAdView.findViewById(R.id.screen_middle_mask).setVisibility(mMask ? View.VISIBLE : View.GONE);
            mCurAdView.findViewById(R.id.screen_bottom_mask).setVisibility(mMask ? View.VISIBLE : View.GONE);
        }
        if (((TextView) mCurAdView.findViewById(R.id.rewardVideo)).getCurrentTextColor() != mTextColor) {
//            ((TextView) mCurAdView.findViewById(R.id.rewardVideo)).setTextColor(mTextColor);
            mCurAdView.findViewById(R.id.under_line).setBackgroundColor(mTextColor);
        }
    }

    @Override
    public void adError(AdContent adContent) {
        mAdHandler.removeCallbacksAndMessages(null);
        load(adContent);
    }

    public void load(int bookId, int chapterId, boolean isVipChapter) {
        mBookId = bookId;
        mChapterId = chapterId;
        mIsVipChapter = isVipChapter;
        load(null);
    }

    private void load(AdContent adContent) {
        hide();
        if (mAdHandler.hasMessages(1))
            return;
        mAdHandler.sendEmptyMessageDelayed(1, 1000);
        if (AdEngine.getInstance().hideReadPageScreenAd(mIsVipChapter)) {
            hideAd();
            return;
        }
        if (mAdReady)
            return;
        AdEngine.getInstance().loadReadPageScreenAd(mAdParentContianer, mBookId, mChapterId, mIsVipChapter, adContent);
    }

    @Override
    public View[] adShowPre(AdContent adContent, ViewGroup viewGroup, String adTitle,
                            String adDesc, String adButtonStr, View adView) {
        return adShowPre(adContent, viewGroup, adTitle, adDesc, adButtonStr, adView, null, true);
    }

    @Override
    public View[] adShowPre(AdContent adContent, ViewGroup viewGroup, String adTitle,
                            String adDesc, String adButtonStr, String adImgUrl) {
        return adShowPre(adContent, viewGroup, adTitle, adDesc, adButtonStr, null, adImgUrl, true);
    }

    @Override
    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {
        mAdEventListener = null;
        //mAdReady = true;
    }

    @Override
    public void adShow(AdContent adContent, AdEvent.AdEventListener adEventListener) {
        mAdEventListener = adEventListener;
    }

    @Override
    public View[] adShow(AdContent adContent, ViewGroup viewGroup, String adTitle,
                         String adDesc, String adButtonStr, String adImgUrl, AdEvent.AdEventListener adEventListener) {
        View[] views = adShowPre(adContent, viewGroup, adTitle, adDesc, adButtonStr, null, adImgUrl, true);
        mAdEventListener = adEventListener;
        return views;
    }

    @Override
    public AdViewSize adViewSize(AdContent adContent, ViewGroup viewGroup) {
        int width = adContent.getWidth() > 0 ? adContent.getWidth() : 690;
        int height = adContent.getHeight() > 0 ? adContent.getHeight() : 338;
        return new AdViewSize(width, height);
    }

    private synchronized View[] adShowPre(AdContent adContent, ViewGroup viewGroup, String adTitle,
                                          String adDesc, String adButtonStr, View adView, String adImgUrl, boolean imgReady) {
        if (isShow())
            return null;
        if ("kedaxunfei".equals(adContent.getCp())) {
            mKeDaXunFei = true;
        } else {
            mKeDaXunFei = false;
        }
        mIsMiddle = AdEngine.getInstance().readPageScreenAdStyle() == 2;
        loadAdLayout();
        mCurAdView = mAdMixScreen;
        if (adContent.getCp().equals("guangdiantong")) {
            mCurAdView = mAdGDTScreen;
        }
        if (!adContent.getCp().equals("toutiao")) {
            mCurAdView.findViewById(R.id.cp_logo).setVisibility(View.GONE);
        } else {
            mCurAdView.findViewById(R.id.cp_logo).setVisibility(View.VISIBLE);
        }
        if ("sogou".equals(adContent.getCp())) {
            mAdMixScreen.setTag(adContent);
        }
        mPageNumForDisplay = adContent.getTime();
        if (!mIsMiddle) {
            if (mCurAdView.getParent() != null) {
                mCurAdView.bringToFront();
            } else {
                mAdParentContianer.addView(mCurAdView);
            }
        }
        ((TextView) mCurAdView.findViewById(R.id.text_title)).setText(adTitle);
        ((TextView) mCurAdView.findViewById(R.id.text_desc)).setText(adDesc);
        if (adImgUrl != null) {
            mCurAdView.findViewById(R.id.img_poster).setVisibility(View.VISIBLE);
            mCurAdView.findViewById(R.id.video_poster).setVisibility(View.GONE);
            if (!setImgView(adImgUrl, mCurAdView.findViewById(R.id.img_poster))) {
                new Thread(() -> {
                    try {
                        byte[] data = (byte[]) Action.getInstance().request().httpEngine().getImgRequest(mAdRootContianer.getContext(), adImgUrl, false);
                        BookFileEngine.saveAdImg(mAdRootContianer.getContext(), adImgUrl, data);
                        try {
                            ((Activity) mAdRootContianer.getContext()).runOnUiThread(() -> {
                                setImgView(adImgUrl, mCurAdView.findViewById(R.id.img_poster));
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        ThirdAnalytics.reportError(mAdParentContianer.getContext(), e);
                    }

                }).start();
            }
            mAdEventListener = null;
            return new View[]{mCurAdView, mCurAdView.findViewById(R.id.img_poster), mCurAdView.findViewById(R.id.button)};
        } else {
            mAdEventListener = null;
            mTTVideoView = adView;
            mCurAdView.findViewById(R.id.video_poster).setVisibility(View.VISIBLE);
            mCurAdView.findViewById(R.id.img_poster).setVisibility(View.GONE);
            mAdReady = true;
            return new View[]{mCurAdView, mCurAdView.findViewById(R.id.button)};
        }
    }

    private boolean setImgView(String adImgUrl, ImageView imageView) {
        try {
            Bitmap bitmap = BookFileEngine.getAdImg(mAdRootContianer.getContext(), adImgUrl);
            if (bitmap == null)
                return false;
            imageView.setImageBitmap(bitmap);
            mAdReady = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void adRewardVideoCompleted(Context context, AdContent adContent) {
        hideAd();
    }

    private void hideAd() {
        try {
            mAdReady = false;
            if (middleMode()) {
                mCurAdView = null;
                mAdReadPageScreenListener.adReadPageScreenHide();
            }
            if (mAdRootContianer.getVisibility() == View.VISIBLE) {
                mAdRootContianer.setVisibility(View.GONE);
            }
            if (mAdParentContianer.getChildCount() > 0)
                mAdParentContianer.removeAllViews();
        } catch (Exception e) {

        }
    }
}
