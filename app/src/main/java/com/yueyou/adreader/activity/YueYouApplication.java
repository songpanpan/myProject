package com.yueyou.adreader.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.util.Log;


import com.qq.e.ads.cfg.MultiProcessFlag;
import com.tuia.ad.TuiaAdConfig;
import com.yueyou.adreader.service.analytics.AnalyticsEngine;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.util.Const;
import com.yueyou.adreader.util.LogUtil;

import java.util.UUID;

public class YueYouApplication extends Application {
    private int mActivityCount;
    private long mTimeOfBackround;
    private boolean mMainActivityExit;
    private MainActivity mMainActivity;
    private String mSessionToken = "";

    @Override
    public void onCreate() {
        super.onCreate();
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        registerActivityLifecycleCallbacks(new YueYouActivityLifecycleCallbacks());
        Context context = getApplicationContext();
        mSessionToken = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        statrtRefreshValid();
//        McLogUtil.setENABLE_LOGCAT(false);
//        TuiaAdConfig.init(this);
        ThirdAnalytics.buylyInit(context);
        ThirdAnalytics.umengInit(context);
        MultiProcessFlag.setMultiProcess(true);
        AnalyticsEngine.engineInit(this);
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
//        try{
//            JLibrary.InitEntry(base);
//            MiitHelper miitHelper = new MiitHelper(new MiitHelper.AppIdsUpdater() {
//                @Override
//                public void OnIdsAvalid(@NonNull String ids) {
//                    LogUtil.e("OnIdsAvalid ids:" + ids);
//                    Const.OAID = ids;
//                }
//            });
//            miitHelper.getDeviceIds(base);
//        }catch (Exception e){
//        }
    }

    public String getSessionToken() {
        return mSessionToken;
    }

    public boolean isBackground() {
        return mActivityCount == 0;
    }

    public void statrtRefreshValid() {
        mHandler.sendEmptyMessageDelayed(1, 60000);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                refreshValid();
                mHandler.sendEmptyMessageDelayed(1, 60000);
            } else if (msg.what == 2) {
//                final Intent intentData = new Intent(getApplicationContext(), ReSplashActivity.class);
                final Intent intentData = new Intent(getApplicationContext(), MainActivity.class);

                intentData.putExtra(MainActivity.NO_FROM_INIT, true);
                try {
                    intentData.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intentData);
                } catch (Exception e) {
                    ThirdAnalytics.reportError(getApplicationContext(), e);
                }
            }
        }
    };

    public void refreshValid() {
        try {
            if (!isBackground())
                AnalyticsEngine.refreshVaild(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MainActivity getMainActivity() {
        Log.i("blank screen", "blank screen getMainActivity: " + mMainActivity);
        return mMainActivity;
    }

    private class YueYouActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            if (activity instanceof MainActivity) {
                Log.i("blank screen", "blank screen onActivityCreated: " + activity);
                mMainActivity = (MainActivity) activity;
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            try {
                if (mActivityCount == 0) { //后台切换到前台
                    if (!mMainActivityExit && mTimeOfBackround != 0 && (System.currentTimeMillis() - mTimeOfBackround) > 1000 * 300) {
                        mHandler.sendEmptyMessage(2);
                    }
                }
                mTimeOfBackround = 0;
                mActivityCount++;
            } catch (Exception e) {

            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (activity instanceof MainActivity) {
                mMainActivityExit = false;
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            mActivityCount--;
            if (mActivityCount == 0) { //前台切换到后台
                mTimeOfBackround = System.currentTimeMillis();
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (activity instanceof MainActivity) {
                Log.i("blank screen", "blank screen onActivityDestroyed");
                mMainActivityExit = true;
                mMainActivity = null;
            }
        }
    }
}