package com.yueyou.adreader.service.advertisement.adObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.WebViewActivity;
import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.GlideRoundTransform;

public class AdReadPageBanner extends AdEventObject {
    private int mTime = 30;
    private ViewGroup mAdContianer;
    private View mDefaultView;
    private View mAdMixBannerView;
    private View mAdGDTBannerView;
    private View mAdCSBannerView;
    private AdReadPageBannerListener mAdReadPageBannerListener;
    private boolean mPreIsHide;
    private boolean mNoRestTime;

    public interface AdReadPageBannerListener {
        int AdBookId();

        int AdChapterId();

        boolean isVipChapter();
    }

    public AdReadPageBanner() {
        super(AdSite.readPageBanner);
    }

    private Handler mAdHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                load();
                Widget.sendEmptyMessageDelayed(this, msg.what, mTime * 1000);
            } catch (Exception e) {

            }
        }
    };

    public void init(ViewGroup viewGroup, AdReadPageBannerListener adReadPageBannerListener) {
        mAdContianer = viewGroup.findViewById(R.id.ad_container_banner);
        mDefaultView = viewGroup.findViewById(R.id.default_bg);
        mAdMixBannerView = LayoutInflater.from(mAdContianer.getContext()).inflate(R.layout.ad_read_page_banner_mix, mAdContianer, false);
        mAdGDTBannerView = LayoutInflater.from(mAdContianer.getContext()).inflate(R.layout.ad_read_page_banner_gdt, mAdContianer, false);
        mAdCSBannerView = LayoutInflater.from(mAdContianer.getContext()).inflate(R.layout.ad_read_page_banner_cs, mAdContianer, false);
        mAdReadPageBannerListener = adReadPageBannerListener;
        mAdGDTBannerView.findViewById(R.id.button).setVisibility(View.GONE);
        mAdGDTBannerView.findViewById(R.id.img_cp).setVisibility(View.GONE);
        mAdCSBannerView.findViewById(R.id.img_cp).setVisibility(View.GONE);
    }

    @Override
    public void release() {
        super.release();
        mAdHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void pause() {
        super.pause();
        //AdEngine.getInstance().resetReadPageBannerAdList();
        mAdHandler.removeCallbacksAndMessages(null);
        mNoRestTime = false;
    }

    @Override
    public void resume() {
        super.resume();
        if (!mNoRestTime)
            Widget.sendEmptyMessageDelayed(mAdHandler, 1, mTime * 1000);
        mNoRestTime = false;
    }

    @Override
    public void adError(AdContent adContent) {
        load(adContent);
    }

    public void refreshChapterVip(boolean isVipChapter) {
        if (AdEngine.getInstance().hideReadPageBannerAd(isVipChapter)) {
            mPreIsHide = true;
            hideAd();
        } else {
            if (mPreIsHide) {
                mNoRestTime = true;
                Widget.sendEmptyMessageDelayed(mAdHandler, 1, 1000);
            }
            mPreIsHide = false;
        }
    }

    public void load() {
        load(null);
    }

    private void load(AdContent adContent) {
        try {
            AdEngine.getInstance().loadReadPageBannerAd(mAdContianer, mAdReadPageBannerListener.AdBookId(),
                    mAdReadPageBannerListener.AdChapterId(), mAdReadPageBannerListener.isVipChapter(), adContent);
            Widget.sendEmptyMessageDelayed(mAdHandler, 1, mTime * 1000);
        } catch (Exception e) {

        }
    }

    public void setColor(int bgCr, int titleCr, int descCr, boolean mask, boolean parchment) {
        try {
            if (parchment) {
                ((View) mAdContianer.getParent()).setAlpha(0.9f);
                ((View) mAdContianer.getParent()).setBackgroundResource(R.drawable.parchment);
            } else {
                ((View) mAdContianer.getParent()).setAlpha(1.0f);
                ((View) mAdContianer.getParent()).setBackgroundColor(bgCr);
            }
            ((TextView) mDefaultView.findViewById(R.id.default_title)).setTextColor(titleCr);
            mDefaultView.findViewById(R.id.title_left).setBackgroundColor(titleCr);
            mDefaultView.findViewById(R.id.title_right).setBackgroundColor(titleCr);
            ((TextView) mAdGDTBannerView.findViewById(R.id.text_desc)).setTextColor(titleCr);
            ((TextView) mAdMixBannerView.findViewById(R.id.text_desc)).setTextColor(titleCr);
            ((TextView) mAdGDTBannerView.findViewById(R.id.text_title)).setTextColor(titleCr);
            ((TextView) mAdMixBannerView.findViewById(R.id.text_title)).setTextColor(titleCr);
            if (mask) {
                ((View) mAdContianer.getParent()).findViewById(R.id.mask).setVisibility(View.VISIBLE);
            } else {
                ((View) mAdContianer.getParent()).findViewById(R.id.mask).setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public View[] adShowPre(AdContent adContent, ViewGroup viewGroup, String adTitle,
                            String adDesc, String adButtonStr, String adImgUrl) {
        mAdContianer.removeAllViews();
        View view = mAdMixBannerView;
        if (adContent.getCp().equals("guangdiantong")) {
            view = mAdGDTBannerView;
        } else if (adContent.getCp().equals("sogou")) {
            mAdMixBannerView.findViewById(R.id.img_cp).setVisibility(View.GONE);
            adShow(adContent, null, null);
            AdEvent.getInstance().uploadAdShowed(adContent);
        } else if (adContent.getCp().equals("chuangshen")) {
            view = mAdCSBannerView;
        }else if(adContent.getCp().equals("hanbo")
        || adContent.getCp().equals("kedaxunfei")){
            mAdMixBannerView.findViewById(R.id.img_cp).setVisibility(View.GONE);
        }else {
            mAdMixBannerView.findViewById(R.id.img_cp).setVisibility(View.VISIBLE);
        }
        mAdContianer.addView(view);
        if (mAdContianer.getVisibility() == View.GONE) {
            mAdContianer.setVisibility(View.VISIBLE);
            mDefaultView.setVisibility(View.GONE);
        }
        view.findViewById(R.id.img_close).setOnClickListener(v -> {
            WebViewActivity.show(((Activity) mAdContianer.getContext()), Url.URL_AD_VIP,
                    WebViewActivity.ACCOUNT, "");
        });
        ((TextView) view.findViewById(R.id.text_title)).setText("");
        ((TextView) view.findViewById(R.id.text_desc)).setText("");
        if (adTitle != null && adTitle.length() > 0) {
            ((TextView) view.findViewById(R.id.text_desc)).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.text_desc)).setText(adDesc);
            ((TextView) view.findViewById(R.id.text_title)).setText(adTitle);
        } else {
            ((TextView) view.findViewById(R.id.text_title)).setText(adDesc);
            ((TextView) view.findViewById(R.id.text_desc)).setVisibility(View.GONE);
        }
        if(adContent.getCp().equals("toutiao")&&adContent.getType()==2){
            ((ImageView) view.findViewById(R.id.img_logo)).setVisibility(View.GONE);
        }else{
            RequestOptions options1 = new RequestOptions()
                    .priority(Priority.HIGH)//优先级
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//缓存策略
                    .transform(new GlideRoundTransform(4,false));//转化为圆
            Glide.with(mAdContianer.getContext().getApplicationContext()).load(adImgUrl).apply(options1).into((ImageView) (view.findViewById(R.id.img_logo)));
        }

        if (adContent.getCp().equals("guangdiantong")) {
            return new View[]{view, view.findViewById(R.id.ad_left),
                    view.findViewById(R.id.ad_middle), view.findViewById(R.id.ad_right)};
        }else if(adContent.getCp().equals("chuangshen")){
            return new View[]{view,view.findViewById(R.id.img_logo)};
        }
        return new View[]{view, view.findViewById(R.id.button)};
    }

    @Override
    public void adShowPre(AdContent adContent, ViewGroup viewGroup, View adView) {
        mAdContianer.removeAllViews();
        mAdContianer.addView(adView);
        mAdContianer.setVisibility(View.VISIBLE);
        mDefaultView.setVisibility(View.GONE);
    }

    @Override
    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {
        if (adContent != null && adContent.getTime() >= 10)
            mTime = adContent.getTime();
        Widget.sendEmptyMessageDelayed(mAdHandler, 1, mTime * 1000);
    }

    @Override
    public AdViewSize adViewSize(AdContent adContent, ViewGroup viewGroup) {
        int width = adContent.getWidth() > 0 ? adContent.getWidth() : 690;
        int height = adContent.getHeight() > 0 ? adContent.getHeight() : 338;
        return new AdViewSize(width, height);
    }

    @Override
    public void adRewardVideoCompleted(Context context, AdContent adContent) {
        hideAd();
    }

    private void hideAd() {
        try {
            mAdContianer.setVisibility(View.GONE);
            mDefaultView.setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }
    }
}
