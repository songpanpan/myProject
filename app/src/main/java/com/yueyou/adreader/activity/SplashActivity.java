package com.yueyou.adreader.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.sogou.feedads.api.AdClient;
import com.sogou.feedads.api.AdViewListener;
import com.sogou.feedads.api.view.SplashADView;
import com.sogou.feedads.data.entity.AdTemplate;
import com.yueyou.adreader.R;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.Widget;

public class SplashActivity extends Activity {
    SplashADView splashADView;
    public final static String ADCONTENT_INFO = "adcontent";
    public final static String VIEW_ID = "view_id";
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                finish();
            }catch (Exception e){

            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setFullScreen(true);
        mHandler.sendEmptyMessageDelayed(1, 5000);
        try {
            int viewId = getIntent().getIntExtra(VIEW_ID, 0);
            String adcontentstr = getIntent().getStringExtra(ADCONTENT_INFO);
            AdContent adContent = (AdContent) Widget.stringToObject(adcontentstr, AdContent.class);
            AdClient adClient = AdClient.newClient(getApplicationContext())
                    .pid("yueyouxs_app_1")
                    .mid(adContent.getPlaceId())// 必填：代码位ID
                    .addAdTemplate(AdTemplate.BIG_IMG_TPL_ID)         //大图样式
                    .addAdTemplate(AdTemplate.DOWNLOAD_BIG_TPL_ID)    //大图下载
                    .addAdTemplate(AdTemplate.SPLASH_VERTICAL_TPL_ID) //竖屏样式
                    .addAdTemplate(AdTemplate.DOWNLOAD_SPLASH_TPL_ID) //竖屏下载
                    .debug(true)
                    .create();

            splashADView = (SplashADView) adClient.with(this).getSplashADView(R.id.adview_splash)
                    //是否显示“跳过”
                    .cancelable(true)
                    //当获取广告失败时是否finish，默认为true，若设置为false，需自行处理onFailed情况
                    .finishIfFailed(true)
                    //设置finish后调起的activity，可用setTargetIntent()代替
                    .setTargetActivity(MainActivity.class)
                    .setAdViewListener(new AdViewListener() {
                        @Override
                        public void onSuccess() {
                            AdEvent.getInstance().adClosed(adContent);
                            AdEvent.getInstance().adShowPre(adContent, null, null);
                            AdEvent.getInstance().adShow(adContent, null, null);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            AdEvent.getInstance().adClosed(adContent);
                            AdEvent.getInstance().loadAdError(adContent, 0, "");
                        }

                        @Override
                        public void onAdClick() {
                            AdEvent.getInstance().adClicked(adContent);
                        }

                        @Override
                        public void onClose() {
                            finish();
                        }
                    });
            splashADView.getAd();
        }catch (Exception e){
            e.printStackTrace();
            finish();
        }
    }
    private void setFullScreen(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        } else {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        }
    }
}
