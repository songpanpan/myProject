package com.yueyou.adreader.service.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.DeviceUtils;
import com.yueyou.adreader.service.HttpEngine;
import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.service.advertisement.service.AdEngine;
import com.yueyou.adreader.service.analytics.model.Activate;
import com.yueyou.adreader.service.analytics.model.AddBuildinBookFinish;
import com.yueyou.adreader.service.analytics.model.Advertisement;
import com.yueyou.adreader.service.analytics.model.Login;
import com.yueyou.adreader.service.analytics.model.Read;
import com.yueyou.adreader.service.analytics.model.Valid;
import com.yueyou.adreader.service.analytics.model.base.Base;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.util.Const;
import com.yueyou.adreader.util.DES;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.util.YYStringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Bi {
    //    public static final String BI_URL_BASE = "http://192.168.1.48:9090";
//    public static final String BI_URL_BASE = "http://game.ireader.com.cn:60000";
    public static final String BI_URL_BASE = Url.BI_URL_BASE;
    public static final String BI_URL = BI_URL_BASE + "/api/%s/create.do";
    public static final String BI_AD_URL = Url.URL_BASE_AD + "/api/%s/create.do";
    private static Handler handler;
    private static final List<String> pathList = Arrays.asList("login", "activate", "read");
    private static LinkedList<String> failData;
    private static Context mContext;

    protected static void initHandler(Context context) {
        mContext = context;
        failData = new LinkedList<>();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                _handleMessage(msg);
                super.handleMessage(msg);
            }
        };
        sendMessageObj();
    }

    private static void _handleMessage(Message msg) {
        try {
            String signUrl = failData.pop();
            if (!YYStringUtils.isBlank(signUrl)) {
                reUpload(mContext, signUrl);
            }
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            sendMessageObj();
        }
    }

    private static void sendMessageObj() {
        handler.sendEmptyMessageDelayed(1, 5 * 1000);
    }

    public static String signUrl(Context context, String url) {
        Login login = new Login();
        login.setUserId(DataSHP.getUserId(context));
        String data = signData(context, login);
        if (url.contains("?")) {
            url += "&data=" + data;
        } else {
            url += "?data=" + data;
        }
        return url;
    }

    public static void activate(Context context, String siteId, String bookId, String bookName) {
        Activate activate = new Activate();
        activate.setSiteId(siteId);
        activate.setBookId(bookId);
        activate.setBookName(bookName);
        activate.setAndroidVersion(Widget.getSystemVersion());
        activate.setApnType(Widget.getAPNType(context));
        activate.setPhoneBrand(Widget.getPhoneType());
        activate.setPhoneModel(Widget.getSystemModel());
        upload(context, activate);

    }

    public static void addBuildinBookFinish(Context context, boolean result, String msg) {
        AddBuildinBookFinish addBuildinBookFinish = new AddBuildinBookFinish();
        addBuildinBookFinish.setResult(result);
        addBuildinBookFinish.setMsg(msg);
        upload(context, addBuildinBookFinish);
    }

    public static void login(Context context, String userId) {
        Login login = new Login();
        login.setUserId(userId);
        upload(context, login);
    }

    public static void vaild(Context context, String userId) {
        Valid vaild = new Valid();
        vaild.setUserId(userId);
        upload(context, vaild);
    }

    public static void read(Context context, String userId, int bookId, String bookName, int chapterId, boolean isLastChapter, int words) {
        Read read = new Read();
        read.setUserId(userId);
        read.setBookId(bookId);
        read.setBookName(bookName);
        read.setChapterId(chapterId);
        read.setLastChapter(isLastChapter);
        read.setWords(words);
        upload(context, read);
    }

    public static void advertisementLoad(Context context, int siteId, String cp, boolean result) {
        if (!AdEngine.getInstance().enableMatNotify(siteId))
            return;
        Advertisement advertisement = new Advertisement();
        advertisement.setSiteId(siteId);
        advertisement.setCp(cp);
        advertisement.setAction(1);
        advertisement.setStatus(result ? 0 : 1);
        upload(context, advertisement);
    }

    public static void advertisement(Context context, int siteId, String cp, boolean clicked) {
        Advertisement advertisement = new Advertisement();
        advertisement.setSiteId(siteId);
        advertisement.setClicked(clicked);
        advertisement.setCp(cp);
        upload(context, advertisement);
    }

    public static void advertisementEnd(Context context, int siteId, String cp, HttpEngine.HttpEngineListener listener) {
        Advertisement advertisement = new Advertisement();
        advertisement.setSiteId(siteId);
        advertisement.setDisplayEnd(true);
        advertisement.setCp(cp);
        upload(context, advertisement, listener);
    }

    private static void setBase(Context context, Base base) {
        base.setAppId(Widget.getPacketName(context));
        base.setChannelId(Widget.getChannelId(context));
        base.setPlatId(2);
        base.setDeviceId(Widget.getDeviceId(context));
        base.setVersion(Widget.getAppVersionName(context));
    }

    private static String signData(Context context, Base base) {
        setBase(context, base);
        String content = Widget.objectToString(base);
        String data = DES.encryption(content, "k&sjnT35");
        LogUtil.e("signData:" + content);
        try {
            data = URLEncoder.encode(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    private static void setBaseActivate(Context context, Base base) {
        base.setAppId(Widget.getPacketName(context));
        base.setChannelId(Widget.getChannelId(context));
        base.setPlatId(2);
        base.setDeviceId(Widget.getDeviceId(context));
        base.setVersion(Widget.getAppVersionName(context));
        base.setImei(Widget.getDeviceId(context));
        base.setAndroidId(DeviceUtils.getAndroidID());
        base.setMac(DeviceUtils.getMacAddress());
        base.setOaid(Const.OAID);
    }

    private static String signDataActivate(Context context, Base base) {
        setBaseActivate(context, base);
        String content = Widget.objectToString(base);
        String data = DES.encryption(content, "k&sjnT35");
        try {
            data = URLEncoder.encode(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    private static void upload(Context context, Base base) {
        upload(context, base, null);
    }

    private static void upload(Context context, Base base, HttpEngine.HttpEngineListener listener) {
        String data = signData(context, base);
        if (data == null)
            return;
        String path = base.getClass().getSimpleName().toLowerCase();
        String url = String.format(BI_URL, path);
        String signUrl;
        if ("advertisement".equals(path)) {
            url = String.format(BI_AD_URL, path);
        }
        if (path.equals("activate")) {
            data = signDataActivate(context, base);
            if (data == null)
                return;
            signUrl = Url.signUrlActivate(context, url) + "&data=" + data;
        } else
            signUrl = Url.signUrl(context, url) + "&data=" + data;
//        LogUtil.e("signUrl:" + signUrl);
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(() -> HttpEngine.get(context, signUrl, (Context ctx, boolean isSuccessed, Object resultData, Object userData, boolean showProgress) -> {
                if (listener != null) {
                    listener.onResult(ctx, isSuccessed, resultData, userData, showProgress);
                }
                onResult(isSuccessed, resultData, signUrl, path);
            }, false));
        } else {
            HttpEngine.get(context, signUrl, (Context ctx, boolean isSuccessed, Object resultData, Object userData, boolean showProgress) -> {
                if (listener != null) {
                    listener.onResult(ctx, isSuccessed, resultData, userData, showProgress);
                }
                onResult(isSuccessed, resultData, signUrl, path);
            }, false);
        }
    }

    private static void reUpload(Context context, String signUrl) {
        Utils.logNoTag("BI::reUpload , signUrl : %s", signUrl);
        HttpEngine.get(context, signUrl, (Context ctx, boolean isSuccessed, Object resultData, Object userData, boolean showProgress) -> {
            if (!isSuccessed(isSuccessed, resultData)) {
                failData.add(signUrl);
            }
        }, false);
    }


    private static void onResult(boolean isSuccessed, Object resultData, String signUrl, String path) {
        if (isSuccessed(isSuccessed, resultData))
            return;
        if (pathList.contains(path)) {
            failData.add(signUrl);
        }
    }

    private static boolean isSuccessed(boolean isSuccessed, Object resultData) {
        return isSuccessed && resultData != null && ((String) resultData).toLowerCase().contains("success");
    }
}
