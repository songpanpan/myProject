package com.yueyou.adreader.service.advertisement.adObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.WebViewActivity;
import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.advertisement.service.AdSite;
import com.yueyou.adreader.service.model.AdContent;

public class AdBookShelfHeader extends AdEventObject {
    public AdBookShelfHeader() {
        super(AdSite.bookShelfHeader);
    }

    public void load(ViewGroup viewGroup) {
        AdEngine.getInstance().loadBookShelfHeaderAd(viewGroup);
        viewGroup.findViewById(R.id.gridview_header_title).setSelected(true);
    }

    @Override
    public void adShow(AdContent adContent, ViewGroup viewGroup, View adView) {
        viewGroup.setVisibility(View.VISIBLE);
        ((TextView)viewGroup.findViewById(R.id.gridview_header_title)).setText(adContent.getAppKey());
        viewGroup.setOnClickListener((View v)->{
            WebViewActivity.show((Activity) viewGroup.getContext(), adContent.getPlaceId(), WebViewActivity.CLOSED, "");
        });
    }
}
