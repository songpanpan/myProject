package com.yueyou.adreader.service.advertisement.partner.ChuangShen;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.Gson;
import com.yueyou.adreader.service.advertisement.service.AdEvent;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FeedAd {
    public static final String url = "http://openapi.toukeads.com/openapi/adservice/f.action";

    public static void show(Context context, AdContent adContent, ViewGroup viewGroup) {
        try {
            String url = getFinalUrl(context, adContent.getPlaceId(), adContent.getAppKey());
            OkHttpClient okHttpClient = OkHttpUtil.getInstance();
            final Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e("onFailure");
                    AdEvent.getInstance().loadAdError(adContent, 1, "request failure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String strResponse = response.body().string();
                    LogUtil.e("onResponse: " + strResponse);
                    try {
                        FeedAdBean feedAdBean = new Gson().fromJson(strResponse, FeedAdBean.class);
                        if (feedAdBean != null && feedAdBean.getAds() != null) {
                            ((Activity) context).runOnUiThread(() -> {
                                try {
                                    setFeedAdData(context, adContent, feedAdBean, feedAdBean.getAds().getImage(), viewGroup);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            AdEvent.getInstance().loadAdError(adContent, 1, feedAdBean.getMsg());
                        }
                    } catch (Exception e) {
                        ThirdAnalytics.reportError(context, e);
                    }
                }
            });
        } catch (Exception e) {
            ThirdAnalytics.reportError(context, e);
        }
    }

    private static void setFeedAdData(Context context, AdContent adContent, FeedAdBean feedAdBean, String imgUrl, ViewGroup viewGroup) {
        View[] views = AdEvent.getInstance().adShowPre(adContent, viewGroup, feedAdBean.getAds().getWords(), feedAdBean.getAds().getWords2(), "", imgUrl);
        if (imgUrl != null && views != null && views.length > 0) {
            AdEvent.getInstance().adShow(adContent, null, null);
            List<String> report = feedAdBean.getAds().getShowReport().get(0);
            for (int i = 0; i < report.size(); i++) {
                adReport(report.get(i));
            }
        }


        if (views.length > 0) {
            for (int i = 0; i < views.length; i++) {
                views[i].setOnClickListener(v -> {
                    AdEvent.getInstance().adClicked(adContent);
                    List<String> clickReport = feedAdBean.getAds().getClickReport();
                    if (clickReport != null && clickReport.size() > 0) {
                        adReport(clickReport.get(0));
                    }
                    dealWithGotoUrl(context, feedAdBean.getAds().getGotourl(), feedAdBean.getAds().getWords(), feedAdBean.getAds().getCreativeId());
                });
            }

        }
    }

    public static void dealWithGotoUrl(Context context, String url, String title, int adId) {
        if (url != null && url.length() > 0) {
            if (url.contains("#page")) {
                url = url.replace("#page", "");
                AdWebViewActivity.invoke(context, url);
            } else {
                DownService.invoke(context, url, title, adId);
            }
        }
    }

    public static String getFinalUrl(Context context, String adId, String appkey) {
        Map<String, String> params = getRequestMap(context, adId, appkey);
        String paramsString = "";
        if (params != null && params.size() >= 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (paramsString.equals(""))
                    paramsString = paramsString + entry.getKey() + "=" + entry.getValue();
                else
                    paramsString = paramsString + "&" + entry.getKey() + "=" + entry.getValue();
            }
        }
//        Log.e("spptag", "paramsString:" + paramsString);
        String realUrl = url + "?" + paramsString;
//        Log.e("spptag", "realUrl" + realUrl);
        return realUrl;
    }

    public static void getAs(Context context, Map<String, String> params) {
        String paramsString = "";
        if (params != null && params.size() >= 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (paramsString.equals(""))
                    paramsString = paramsString + entry.getKey() + "=" + entry.getValue();
                else
                    paramsString = paramsString + "&" + entry.getKey() + "=" + entry.getValue();
            }
        }
//        Log.e("spptag", "FeedAd paramsString:" + paramsString);
        String realUrl = url + "?" + paramsString;
//        Log.e("spptag", "FeedAd realUrl" + realUrl);
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        final Request request = new Request.Builder()
                .url(realUrl)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Log.d("spptag", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strResponse = response.body().string();
//                Log.d("spptag", "FeedAd onResponse: " + strResponse);
                FeedAdBean feedAdBean = new Gson().fromJson(strResponse, FeedAdBean.class);
            }
        });
    }

    public static void adReport(String url) {
        url = url.replace("_TIMESTAMP_", (int) (System.currentTimeMillis() / 1000) + "");
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Log.d("spptag", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strResponse = response.body().string();
//                Log.d("spptag", "onResponse: " + strResponse);
            }
        });
    }

    public static Map<String, String> getRequestMap(Context context, String adId, String appkey) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("posId", adId);
        requestMap.put("thumbWidth", "1200");
        requestMap.put("thumbHeight", "800");
        requestMap.put("timestamp", (int) (System.currentTimeMillis() / 1000) + "");
        requestMap.put("platform", "2");
        requestMap.put("device", Widget.getDeviceId(context));
        requestMap.put("version", "3");
        requestMap.put("clientType", "0");
        requestMap.put("clientOsType", "0");
        requestMap.put("ip", NetworkUtils.getIPAddress(true));
        requestMap.put("clientOsVersion", Build.VERSION.RELEASE);
        requestMap.put("ua", "");
        requestMap.put("screenWidth", dm.widthPixels + "");
        requestMap.put("screenHeight", dm.heightPixels + "");
        requestMap.put("screenDensity", dm.densityDpi + "");
        requestMap.put("andid", DeviceUtils.getAndroidID());
        requestMap.put("idfa", "");
        requestMap.put("idfv", "");
        requestMap.put("mac", DeviceUtils.getMacAddress());
        requestMap.put("netStatus", Widget.getNetStatus());
        requestMap.put("netIsp", Widget.getNetIsp(context));
        requestMap.put("signature", getSignature(context, adId, appkey));
        requestMap.put("clientVendor", Build.BRAND);
        requestMap.put("clientModel", Build.MODEL);
        return requestMap;
    }

    public static String getSignature(Context context, String adid, String appkey) {
        String normalSign = Utils.format("posId=%s&thumbWidth=%s&thumbHeight=%s&platform=%s&device=%s&timestamp=%s&secret=%s",
                adid, "1200", "800", "2", Widget.getDeviceId(context), (int) (System.currentTimeMillis() / 1000) + "", appkey);
//        Log.e("spptag", "normalSign:" + normalSign);
        String signature = md5(normalSign);
        return signature;
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
