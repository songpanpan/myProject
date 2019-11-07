package com.yueyou.adreader.service.advertisement.partner.SoGou;

import android.content.Context;
import android.view.ViewGroup;

import com.sogou.feedads.api.AdClient;
import com.sogou.feedads.data.entity.AdTemplate;
import com.yueyou.adreader.service.advertisement.partner.AdControllerBase;
import com.yueyou.adreader.service.model.AdContent;

public class SGController extends AdControllerBase {
    private Context mContext;
    public void init(Context context, String appKey) {
        mContext = context;
        AdClient.init(mContext.getApplicationContext());
    }

    @Override
    public void loadSplash(AdContent adContent, ViewGroup viewGroup) {
        try {
            Splash.show(mContext, viewGroup, adContent);
        }catch (Exception e){

        }
    }

    @Override
    public void loadBookShelfBanner(AdContent adContent, ViewGroup viewGroup) {
        try {
            Feed.show(mContext, viewGroup, adContent, AdTemplate.BIG_IMG_TPL_ID);
        }catch (Exception e){

        }
    }

    @Override
    public void loadReadPageBanner(AdContent adContent, ViewGroup viewGroup) {
        try {
            Feed.show(mContext, viewGroup, adContent, AdTemplate.SMALL_IMG_TPL_ID);
        }catch (Exception e){

        }
    }

    @Override
    public void loadReadPageScreen(AdContent adContent, ViewGroup viewGroup) {
        try {
            Feed.show(mContext, viewGroup, adContent, AdTemplate.BIG_IMG_TPL_ID);
        }catch (Exception e){

        }
    }

    @Override
    public void loadWebBanner(AdContent adContent, ViewGroup viewGroup) {
        try {
            Feed.show(mContext, viewGroup, adContent, AdTemplate.SMALL_IMG_TPL_ID);
        }catch (Exception e){

        }
    }
}
