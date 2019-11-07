package com.yueyou.adreader.service.advertisement.adObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.service.db.BookFileEngine;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.view.webview.CustomWebView;
import com.yueyou.adreader.view.webview.PullToRefreshWebView;

public class AdBookShelfBanner extends AdEventObject {
    private ViewGroup mAdContianer;
    private View mAdMixScreen;
    private View mAdGDTScreen;
    private View mAdWebView;
    private AdEventObjectListener mAdEventObjectListener;
    public AdBookShelfBanner(AdEventObjectListener listener) {
        super(AdSite.bookShelfBanner);
        mAdEventObjectListener = listener;
    }
    public void init(ViewGroup viewGroup) {
        mAdContianer = viewGroup;
        ((ViewGroup)mAdContianer.getParent()).findViewById(R.id.vip_toast).setOnClickListener((v)->{
            WebViewActivity.show(((Activity)mAdContianer.getContext()), Url.URL_AD_VIP,
                    WebViewActivity.ACCOUNT, "");
        });
        mAdMixScreen = LayoutInflater.from(mAdContianer.getContext()).inflate(R.layout.ad_bookshelf_banner_mix, null, false);
        mAdGDTScreen = LayoutInflater.from(mAdContianer.getContext()).inflate(R.layout.ad_bookshelf_banner_gdt, null, false);
        mAdGDTScreen.findViewById(R.id.ad_icon_cp).setVisibility(View.GONE);
    }
    public void load() {
        AdEngine.getInstance().loadBookShelfBannerAd(mAdContianer, false);
    }

    @Override
    public void adClosed() {
        mAdEventObjectListener.closed();
    }

    @Override
    public void adError(AdContent adContent) {
        //mAdEventObjectListener.closed();
        if (adContent.isNativeErrorFlag())
            return;
        AdEngine.getInstance().loadBookShelfBannerAd(mAdContianer, true);
    }

//    @Override
//    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {
//        if (mAdContianer.getChildCount() > 1)
//            mAdContianer.removeViewAt(0);
//        mAdEventObjectListener.showed(adContent);
//    }

    private void show(AdContent adContent) {
        if (mAdContianer.getChildCount() > 1)
            mAdContianer.removeViewAt(0);
        mAdEventObjectListener.showed(adContent);
    }

    @Override
    public AdViewSize adViewSize(AdContent adContent, ViewGroup viewGroup) {
        int width = adContent.getWidth() > 0 ? adContent.getWidth() : 690;
        int height = adContent.getHeight() > 0 ? adContent.getHeight() : 338;
        return new AdViewSize(width, height);
    }

    @Override
    public void adShowPre(AdContent adContent, ViewGroup viewGroup, View adView) {
        if (mAdWebView == null){
            mAdWebView = LayoutInflater.from(mAdContianer.getContext()).inflate(R.layout.ad_bookshelf_banner_webview, null, false);
        }
        CustomWebView customWebView = ((PullToRefreshWebView)mAdWebView.findViewById(R.id.webview)).getRefreshableView();
        ((PullToRefreshWebView)mAdWebView.findViewById(R.id.webview)).setPullToRefreshEnabled(false);
        customWebView.init(new CustomWebView.CustomWebViewListener() {
            @Override
            public void onWebViewProgressChanged(int progress) {

            }

            @Override
            public void onPageFinished(String title, boolean canGoBack) {
                try {
                    ((ViewGroup)mAdContianer.getParent()).findViewById(R.id.vip_toast).setVisibility(View.GONE);
                    if (mAdWebView.getParent() != null) {
                        mAdWebView.bringToFront();
                    }else {
                        mAdContianer.addView(mAdWebView);
                    }
                    show(adContent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError() {

            }
        });
        customWebView.loadUrl(adContent.getAppKey());
    }

    @Override
    public View[] adShowPre(AdContent adContent, ViewGroup viewGroup, String adTitle,
                                          String adDesc, String adButtonStr, String adImgUrl) {
        ((ViewGroup)mAdContianer.getParent()).findViewById(R.id.vip_toast).setVisibility(View.VISIBLE);
        View adView = mAdMixScreen;
        if (adContent.getCp().equals("guangdiantong")) {
            adView = mAdGDTScreen;
        }else if (adContent.getCp().equals("toutiao")) {
            mAdMixScreen.findViewById(R.id.ad_icon_cp).setVisibility(View.VISIBLE);
        }else {
            mAdMixScreen.findViewById(R.id.ad_icon_cp).setVisibility(View.GONE);
        }
        if (adView.getParent() != null) {
            adView.bringToFront();
        }else {
            mAdContianer.addView(adView);
        }
        ((TextView) adView.findViewById(R.id.text_desc)).setText(adDesc);
        if (!setImgView(adContent, adImgUrl, adView.findViewById(R.id.img_poster))) {
            View finalAdView = adView;
            new Thread(() -> {
                try{
                    byte[] data = (byte[]) Action.getInstance().request().httpEngine().getImgRequest(mAdContianer.getContext(), adImgUrl, false);
                    BookFileEngine.saveAdImg(mAdContianer.getContext(), adImgUrl, data);
                    ((Activity)mAdContianer.getContext()).runOnUiThread(()->{
                        setImgView(adContent, adImgUrl, finalAdView.findViewById(R.id.img_poster));
                    });
                }catch(Exception e){
                    ThirdAnalytics.reportError(mAdContianer.getContext(), e);
                }

            }).start();
        }
        return new View[]{adView, adView.findViewById(R.id.img_poster)};
    }

    private boolean setImgView(AdContent adContent, String adImgUrl, ImageView imageView) {
        Bitmap bitmap = BookFileEngine.getAdImg(mAdContianer.getContext(), adImgUrl);
        if (bitmap == null)
            return false;
        imageView.setImageBitmap(bitmap);
        show(adContent);
        return true;
    }

    @Override
    public void adRewardVideoCompleted(Context context, AdContent adContent) {
//        mAdContianer.removeAllViews();
//        mAdEventObjectListener.closed();
    }
}
