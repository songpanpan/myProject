package com.yueyou.adreader.service;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.DeviceUtils;
import com.yueyou.adreader.activity.YueYouApplication;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.util.Const;
import com.yueyou.adreader.util.DES;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.wxapi.WechatApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.yueyou.adreader.BuildConfig.AD_API_HOST;
import static com.yueyou.adreader.BuildConfig.BI_API_HOST;
import static com.yueyou.adreader.BuildConfig.DL_API_HOST;
import static com.yueyou.adreader.BuildConfig.READ_API_HOST;


/**
 * Created by zy on 2017/5/12.
 */

public class Url {
    public static final String URL_SCHEME = "http://";
    public static final String URL_BASE = URL_SCHEME + READ_API_HOST;
    public static final String URL_BASE_AD = URL_SCHEME + AD_API_HOST;
    public static final String BI_URL_BASE = URL_SCHEME + BI_API_HOST;
    public static final String DL_URL_BASE = URL_SCHEME + DL_API_HOST;

    public static final String URL_INDEX = URL_BASE + "/bookStore/recommend";
    public static final String URL_SIGNIN = URL_BASE + "/h5/act/signin";
    public static final String URL_SELECTED = URL_BASE + "/bookStore/selected";
    public static final String URL_CLASSIFY = URL_BASE + "/bookStore/classify?classifyId=0";
    public static final String URL_WODE = URL_BASE + "/bookStore/userCenter";
    public static final String URL_SEARCH = URL_BASE + "/bookStore/search";
    public static final String URL_WECHAT_BIND = URL_BASE + "/userCenter/bindWithWechat";
    public static final String URL_WECHAT_LOGIN = URL_BASE + "/userCenter/loginByWechat";
    //从下载资源获取
    public static final String URL_DOWNLOAD_CHAPTER = DL_URL_BASE + "/userCenter/downloadChapter";
    public static final String URL_DOWNLOAD_CHAPTER_LIST = DL_URL_BASE + "/userCenter/downloadChapterList";

    public static final String URL_GET_CHAPTER_COUNT = URL_BASE + "/userCenter/getChapterCount";
    public static final String URL_CHECK_UPDATE = URL_BASE + "/userCenter/checkBookUpdate";
    public static final String URL_GET_BOOK = URL_BASE + "/api/shelf/book/info";
    public static final String URL_LOGIN = URL_BASE + "/userCenter/autoLogin";
    public static final String URL_CHECK_APP_UPDATE = URL_BASE + "/userCenter/checkAppUpdate";
    public static final String URL_UPLOAD_BOOKID = URL_BASE + "/api/shelf/book/ids_push";
    //获取书籍详情
    public static final String URL_GET_BOOK_DETAIL = URL_BASE + "/api/shelf/book/detail";
    //获取签名状态
    public static final String URL_BOOKSHELF_RED_SPOT = URL_BASE + "/api/act/signin/checkState";
    /**
     * 废弃
     */
    public static final String URL_GET_BUILDIN = URL_BASE + "/userCenter/buildInBook";
    public static final String URL_GET_BUILDIN_NEW = URL_BASE + "/api/shelf/bookList";
    public static final String URL_SHELF_BOOK_PULL = URL_BASE + "/api/shelf/book/pull";
    public static final String URL_POPUO_WINDOW = URL_BASE + "/api/shelf/alert";
    public static final String URL_AD_CONTENT = URL_BASE + "/api/ad/conf/list";
    /**
     * 广告列表
     */
    public static final String URL_AD_CONTENT_NEW = URL_BASE + "/api/ad/conf/new_list";
    public static final String URL_AD_VIP_BASE = URL_BASE + "/h5/ucenter/privilegeAd";
    public static final String URL_AD_VIP = URL_AD_VIP_BASE + "?YYFullScreen=1";
    public static final String URL_AD_CRL_BTN = URL_BASE + "/api/ad/button/toggle";
    public static final String URL_USER_CHECK_BIND = URL_BASE + "/api/uc/user/check_bind";
    public static final String URL_UCENTER_BIND = URL_BASE + "/h5/ucenter/bind" + "?YYFullScreen=1";
    /**
     * 末章推书
     */
    public static final String URL_RECOMMEND_ENDPAGE = URL_BASE + "/h5/book/recommend/endpage" + "?YYFullScreen=1&book_id=%d";
    public static final String URL_AD_LOG = URL_BASE + "/api/ad/logs/upload";

    public static String signUrl(Context context, String url) {
        url = url.trim();
        if (!url.contains("?")) {
            url += "?";
        }
        int index = url.indexOf("platId");
        if (index > 0) {
            url = url.substring(0, index);
        }
        String deviceId = Widget.getDeviceId(context);
        String version = Widget.getAppVersionName(context);
        String packetId = context.getPackageName();
        String userId = DataSHP.getUserId(context);
        String token = DataSHP.getToken(context);
        String time = Widget.getTimeStamp();
        String sex = DataSHP.getSexType(context);
        String channelId = Widget.getChannelId(context);
        String sessionToken = ((YueYouApplication) context.getApplicationContext()).getSessionToken();
        int wx = 0;
        if (WechatApi.getInstance().isInstalled()) {
            wx = 1;
        }
        if (!"girl".equals(sex))
            sex = "boy";
        ThirdAnalytics.onEventApi(context, ThirdAnalytics.getMapUrl(url, deviceId, version, packetId, userId, sex, channelId));
        url += Utils.format("&platId=2&deviceId=%s&appId=%s&channelId=%s&appVersion=%s&time=%s&userId=%s&sex=%s&wx=%d&tmpToken=%s",
                deviceId, packetId, channelId, version, time, userId, sex, wx, sessionToken);
        url = urlEncode(url.replace("?&", "?"));
        return sign(url, token);
    }

    public static String signUrlActivate(Context context, String url) {
        url = url.trim();
        if (!url.contains("?")) {
            url += "?";
        }
        int index = url.indexOf("platId");
        if (index > 0) {
            url = url.substring(0, index);
        }
        String deviceId = Widget.getDeviceId(context);
        String version = Widget.getAppVersionName(context);
        String packetId = context.getPackageName();
        String userId = DataSHP.getUserId(context);
        String token = DataSHP.getToken(context);
        String time = Widget.getTimeStamp();
        String sex = DataSHP.getSexType(context);
        String channelId = Widget.getChannelId(context);
        String sessionToken = ((YueYouApplication) context.getApplicationContext()).getSessionToken();
        String imei = Widget.getDeviceId(context);
        String androidId = DeviceUtils.getAndroidID();
        String mac = DeviceUtils.getMacAddress();
        int wx = 0;
        if (WechatApi.getInstance().isInstalled()) {
            wx = 1;
        }
        if (!"girl".equals(sex))
            sex = "boy";
        ThirdAnalytics.onEventApi(context, ThirdAnalytics.getMapUrl(url, deviceId, version, packetId, userId, sex, channelId));
        url += Utils.format("&platId=2&deviceId=%s&appId=%s&channelId=%s&appVersion=%s&time=%s&userId=%s&sex=%s&wx=%d&tmpToken=%s&imei=%s&androidId=%s&mac=%s&oaid=%s",
                deviceId, packetId, channelId, version, time, userId, sex, wx, sessionToken, imei, androidId, mac, Const.OAID);
        LogUtil.e("url:" + url);
        url = urlEncode(url.replace("?&", "?"));
        return sign(url, token);
    }

    private static String sign(String url, String token) {
        Uri uri = Uri.parse(url);
        String str = url.replace("?", "");
        String signStr = DES.encryption(uri.getPath() + uri.getEncodedQuery() + "&userToken=" + token, "snY%169j");
        try {
            signStr = URLEncoder.encode(signStr, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url + "&sign=" + signStr;
    }

    private static String urlEncode(String url) {
        String params = url.substring(url.indexOf("?") + 1);
        url = url.substring(0, url.indexOf("?") + 1);
        String[] items = params.split("&");
        for (String item : items) {
            String[] kv = item.split("=");
            if (kv.length != 2)
                continue;
            try {
                url += "&" + kv[0] + "=" + URLEncoder.encode(kv[1], "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return url.replace("?&", "?");
    }
}
