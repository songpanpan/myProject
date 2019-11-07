package com.yueyou.adreader.service.advertisement.adObject;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yueyou.adreader.R;
import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.model.AdContent;

public class AdSplash extends AdEventObject {
    private AdSplashListener mAdEventObjectListener;
    private ViewGroup mAdContianer;
    private View mAdMixSplash;

    public void init(ViewGroup viewGroup) {
        mAdContianer = viewGroup;
        mAdMixSplash = LayoutInflater.from(mAdContianer.getContext()).inflate(R.layout.ad_splash_mix, null, false);
    }

    public interface AdSplashListener extends AdEventObjectListener {
        void preShow();
    }

    public AdSplash(AdSplashListener listener) {
        super(AdSite.splash);
        mAdEventObjectListener = listener;
    }

    public AdSplash(AdSplashListener listener, int siteId) {
        super(siteId);
        mAdEventObjectListener = listener;
    }

    @Override
    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {
        mAdEventObjectListener.showed(adContent);
    }

    @Override
    public void adClosed() {
        mAdEventObjectListener.closed();
    }

    @Override
    public void adError(AdContent adContent) {
        load(adContent);
//        mAdEventObjectListener.closed();
    }

    protected void load(AdContent adContent) {
        AdEngine.getInstance().loadSplashAd(adContent, mAdContianer);
    }

    public void load(ViewGroup viewGroup) {
        AdEngine.getInstance().loadSplashAd(viewGroup);
    }

    @Override
    public View[] adShowPre(AdContent adContent, ViewGroup viewGroup, String adTitle, String adDesc,
                            String adButtonStr, String adImgUrl) {
        this.adContent = adContent;
        mAdContianer.removeAllViews();
        View view;
        if (adContent.getCp().equals("hanbo")
                || adContent.getCp().equals("kedaxunfei")) {
            view = mAdMixSplash;
            countdownView = mAdMixSplash.findViewById(R.id.countdown);
            Glide.with(mAdContianer.getContext().getApplicationContext()).load(adImgUrl).into((ImageView) (view.findViewById(R.id.img_splash)));
            mAdContianer.addView(view);
            handler.sendEmptyMessage(1);
        } else {
            return null;
        }
        if (view != null)
            return new View[]{view, view.findViewById(R.id.countdown)};
        return null;
    }

    private int countdown = 5;
    private String countStr;
    SpannableString spannableString;
    private TextView countdownView;
    AdContent adContent;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (countdown <= 0) {
                AdEvent.getInstance().adClosed(adContent);
                return;
            } else {
                if (countdownView != null) {
                    countStr = countdown + "s | 跳过";
                    spannableString = new SpannableString(countStr);
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FFFFFF"));
                    spannableString.setSpan(colorSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    countdownView.setText(spannableString);
                }
                countdown--;
                handler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };
}
