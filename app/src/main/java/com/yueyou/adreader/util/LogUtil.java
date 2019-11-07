package com.yueyou.adreader.util;

import android.util.Log;

import com.blankj.utilcode.util.AppUtils;


public class LogUtil {
    private static final String TAG = "yueyoutag";

    public static void d(String debugInfo) {
        if (AppUtils.isAppDebug()) {
            Log.d(TAG, debugInfo);
        }
    }

    public static void e(String debugInfo) {
        if (AppUtils.isAppDebug()) {
            Log.e(TAG, debugInfo);
        }
    }

    public static void e(Throwable e) {
        if (e == null) return;
        if (AppUtils.isAppDebug()) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

}
