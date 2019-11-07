package com.yueyou.adreader.service.advertisement.adObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.WebViewActivity;
import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.Widget;

public class AdWebViewBanner extends AdEventObject{
    private ViewGroup mAdContianer;
    private View mDefaultView;
    private View mAdMixBannerView;
    private View mAdGDTBannerView;
    private int mWidth;
    private int mHeight;
    private View mTTCloseView;
    public AdWebViewBanner() {
        super(AdSite.webBanner);
    }

    public void init(ViewGroup viewGroup, int width, int height) {
        mWidth = width;
        mHeight = height;
        mAdContianer = viewGroup.findViewById(R.id.ad_container_banner);
        mDefaultView = viewGroup.findViewById(R.id.default_bg);
        mAdMixBannerView = LayoutInflater.from(mAdContianer.getContext()).inflate(R.layout.ad_webview_banner_mix,  mAdContianer, false);
        mAdGDTBannerView = LayoutInflater.from(mAdContianer.getContext()).inflate(R.layout.ad_webview_banner_gdt,  mAdContianer, false);
        mAdGDTBannerView.findViewById(R.id.button).setVisibility(View.GONE);
        mAdGDTBannerView.findViewById(R.id.img_cp).setVisibility(View.GONE);
        mTTCloseView = ((ViewGroup)mAdContianer.getParent()).findViewById(R.id.container_img_close);
        mTTCloseView.setOnClickListener((View v)->{
            WebViewActivity.show(((Activity)mAdContianer.getContext()), Url.URL_AD_VIP,
                    WebViewActivity.ACCOUNT, "");
        });
    }

    @Override
    public void adError(AdContent adContent) {
        if (adContent.isNativeErrorFlag())
            return;
        AdEngine.getInstance().loadWebBannerAd(mAdContianer, true);
    }

    public void load() {
        AdEngine.getInstance().loadWebBannerAd(mAdContianer, false);
    }

    @Override
    public View[] adShowPre(AdContent adContent, ViewGroup viewGroup, String adTitle,
                            String adDesc, String adButtonStr, String adImgUrl) {
        mTTCloseView.setVisibility(View.GONE);
        mAdContianer.removeAllViews();
        View view = mAdMixBannerView;
        if (adContent.getCp().equals("guangdiantong")) {
            view = mAdGDTBannerView;
        }else if (adContent.getCp().equals("sogou") || adContent.getCp().equals("kedaxunfei")) {
            mAdMixBannerView.findViewById(R.id.img_cp).setVisibility(View.GONE);
            adShow(adContent, null, null);
            AdEvent.getInstance().uploadAdShowed(adContent);
        }else {
            mAdMixBannerView.findViewById(R.id.img_cp).setVisibility(View.VISIBLE);
        }
        mAdContianer.addView(view, new ViewGroup.LayoutParams(Widget.dip2px(viewGroup.getContext(), mWidth), Widget.dip2px(viewGroup.getContext(), mHeight)));
        if (mAdContianer.getVisibility() == View.GONE) {
            mAdContianer.setVisibility(View.VISIBLE);
            mDefaultView.setVisibility(View.GONE);
        }
        view.findViewById(R.id.img_close).setOnClickListener(v->{
            WebViewActivity.show(((Activity)mAdContianer.getContext()), Url.URL_AD_VIP,
                    WebViewActivity.ACCOUNT, "");
        });
        if (adTitle != null && adTitle.length() > 0) {
            ((TextView) view.findViewById(R.id.text_desc)).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.text_desc)).setText(adDesc);
            ((TextView) view.findViewById(R.id.text_title)).setText(adTitle);
        }else {
            ((TextView) view.findViewById(R.id.text_title)).setText(adDesc);
            ((TextView) view.findViewById(R.id.text_desc)).setVisibility(View.GONE);
        }
        Glide.with(mAdContianer.getContext().getApplicationContext()).load(adImgUrl).into((ImageView) (view.findViewById(R.id.img_logo)));
        if (adContent.getCp().equals("guangdiantong")) {
            return new View[]{view,
                    view.findViewById(R.id.ad_middle), view.findViewById(R.id.ad_left), view.findViewById(R.id.ad_right)};
        }
        return new View[]{view, view.findViewById(R.id.button)};
    }

    @Override
    public void adShowPre(AdContent adContent, ViewGroup viewGroup, View adView) {
        mAdContianer.removeAllViews();
        mAdContianer.addView(adView);
        mTTCloseView.setVisibility(View.VISIBLE);
        mAdContianer.setVisibility(View.VISIBLE);
        mDefaultView.setVisibility(View.GONE);
    }

    @Override
    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {

    }

    @Override
    public AdViewSize adViewSize(AdContent adContent, ViewGroup viewGroup) {
        if (adContent.getCp().equals("toutiao"))
            return new AdViewSize(mWidth, mHeight);
        int width = adContent.getWidth() > 0 ? adContent.getWidth() : 690;
        int height = adContent.getHeight() > 0 ? adContent.getHeight() : 338;
        return new AdViewSize(width, height);
    }
}
