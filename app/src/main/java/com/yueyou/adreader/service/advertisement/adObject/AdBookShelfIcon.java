package com.yueyou.adreader.service.advertisement.adObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yueyou.adreader.activity.WebViewActivity;
import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.Widget;

public class AdBookShelfIcon extends AdEventObject {
    private ImageView mIcon;
    public AdBookShelfIcon() {
        super(AdSite.bookShelfBottomIcon);
    }

    public void load(ImageView icon) {
        mIcon = icon;
        AdEngine.getInstance().loadBookShelfIconAd();
    }

    @Override
    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {
        View icon = viewGroup;
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mIcon.getContext()).load(adContent.getPlaceId()).apply(options).into(mIcon);
        mIcon.setOnClickListener((View v) -> {
            String url = adContent.getAppKey();
            if ("tuia".equals(adContent.getCp())) {
                url += "&device_id=" + Widget.getDeviceId(mIcon.getContext());
                WebViewActivity.show((Activity) mIcon.getContext(), url, WebViewActivity.CLOSED, "");
            } else {
                WebViewActivity.show((Activity) mIcon.getContext(), url, WebViewActivity.CLOSED, "");
            }
            AdEvent.getInstance().adClicked(adContent);
        });
    }
}
