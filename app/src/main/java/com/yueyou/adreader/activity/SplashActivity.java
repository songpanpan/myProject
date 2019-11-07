package com.yueyou.adreader.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.yueyou.adreader.R;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.Widget;

public class SplashActivity extends Activity {
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
