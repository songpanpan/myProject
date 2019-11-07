package com.yueyou.adreader.service.advertisement.partner.HanBo;

import android.content.Context;

import com.yueyou.adreader.service.analytics.ThirdAnalytics;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtil {
    private static OkHttpClient singleton;

    private OkHttpUtil() {
    }

    public static OkHttpClient getInstance() {
        if (singleton == null) {
            synchronized (OkHttpUtil.class) {
                if (singleton == null) {
                    singleton = new OkHttpClient();
                }
            }
        }
        return singleton;
    }
}
