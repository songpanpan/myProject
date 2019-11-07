package com.yueyou.adreader.service.advertisement.partner.HanBo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.Gson;
import com.yueyou.adreader.service.advertisement.partner.ChuangShen.AdWebViewActivity;
import com.yueyou.adreader.service.advertisement.partner.ChuangShen.DownService;
import com.yueyou.adreader.service.advertisement.partner.ChuangShen.OkHttpUtil;
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
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FeedAd {
    public static final String url = "https://appapisdk.gotoline.cn/1_0_1/InfoFlow.php";

    public static void show(Context context, AdContent adContent, ViewGroup viewGroup) {
        try {
            OkHttpClient okHttpClient = com.yueyou.adreader.service.advertisement.partner.ChuangShen.OkHttpUtil.getInstance();
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            String[] adInfo = adContent.getAppKey().split(":");
            if (adInfo == null || adInfo.length != 2) {
                return;
            }
            Map<String, String> params = getRequestMap(context, adInfo[0], adContent.getPlaceId(), adInfo[1]);
            if (params != null && params.size() >= 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            } else {
                builder.addFormDataPart("tmp", "");
            }
            MultipartBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e("request failure");
                    AdEvent.getInstance().loadAdError(adContent, 1, "request failure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String strResponse = response.body().string();
                    LogUtil.e("onResponse:" + strResponse);
                    try {
                        com.yueyou.adreader.service.advertisement.partner.HanBo.FeedAdBean bannerAdBean = new Gson().fromJson(strResponse, com.yueyou.adreader.service.advertisement.partner.HanBo.FeedAdBean.class);
                        if (bannerAdBean != null && bannerAdBean.getData() != null) {
                            ((Activity) context).runOnUiThread(() -> {
                                try {
                                    setFeedAdData(context, adContent, bannerAdBean, bannerAdBean.getData().get(0).getAdsImgUrl(), viewGroup);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
//                        AdEvent.getInstance().loadAdError(adContent, 1, bannerAdBean.getMsg());
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

    public static void dealWithGotoUrl(Context context, String url, String title, int adId) {
        if (url != null && url.length() > 0) {
//            if (!url.contains("apk")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
//                AdWebViewActivity.invoke(context, url);
//            } else {
//                DownService.invoke(context, url, title, adId);
//            }
        }
    }

    private static void setFeedAdData(Context context, AdContent adContent, FeedAdBean bannerAdBean, String imgUrl, ViewGroup viewGroup) {
        View[] views = AdEvent.getInstance().adShowPre(adContent, viewGroup, bannerAdBean.getData().get(0).getTitle(), bannerAdBean.getData().get(0).getAdsName(), "", imgUrl);
        if (imgUrl != null && views != null && views.length > 0) {
            AdEvent.getInstance().adShow(adContent, null, null);
            String report = bannerAdBean.getData().get(0).getPvStatisticsUrl();
            if (report != null && report.length() > 0)
                adReport(report);
        }


        if (views.length > 0) {
            for (int i = 0; i < views.length; i++) {
                views[i].setOnClickListener(v -> {
                    AdEvent.getInstance().adClicked(adContent);
                    String clickReport = bannerAdBean.getData().get(0).getClickStatisticsUrl();
                    if (clickReport != null && clickReport.length() > 0) {
                        adReport(clickReport);
                    }
                    dealWithGotoUrl(context, bannerAdBean.getData().get(0).getAdsUrl(), bannerAdBean.getData().get(0).getTitle(), 0);
                });
            }

        }
    }


    public static void adReport(String url) {
        OkHttpClient okHttpClient = OkHttpUtil.getInstance();
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d("onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strResponse = response.body().string();
                LogUtil.d("onResponse: " + strResponse);
            }
        });
    }

    public static Map<String, String> getRequestMap(Context context, String userId, String appId, String appkey) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        String json = getClientInfo(context);
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("UserID", userId);
        requestMap.put("AppID", appId);
        requestMap.put("CheckKey", md5(userId + appId + appkey));
        requestMap.put("WidthPixels", dm.widthPixels + "");
        requestMap.put("HeightPixels", dm.heightPixels + "");
        requestMap.put("System", "0");
        requestMap.put("NetState", (NetworkUtils.isWifiConnected() ? 2 : 1) + "");
        requestMap.put("PackageName", "com.yueyou.adreader");
        requestMap.put("ClientInfo", json);
        requestMap.put("is_s", "1");
        requestMap.put("ShowType", "1");
        return requestMap;
    }

    public static String getClientInfo(Context context) {
        ClientInfoBean clientInfoBean = new ClientInfoBean();
        clientInfoBean.setIMEI(Widget.getDeviceId(context));
        clientInfoBean.setMac(DeviceUtils.getMacAddress());
        clientInfoBean.setDevicetype(android.os.Build.BRAND);
        clientInfoBean.setSystemVersion(android.os.Build.VERSION.RELEASE);
        clientInfoBean.setSystemName(System.getProperty("os.name"));
        clientInfoBean.setAppVerion(AppUtils.getAppVersionName());
        clientInfoBean.setAppName(AppUtils.getAppName());
        clientInfoBean.setDeviceModel(android.os.Build.MODEL);
        return new Gson().toJson(clientInfoBean);
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
