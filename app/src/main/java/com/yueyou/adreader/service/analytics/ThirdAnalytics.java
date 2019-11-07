package com.yueyou.adreader.service.analytics;

import android.content.Context;
import android.webkit.WebView;

import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.yueyou.adreader.BuildConfig;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.HttpUrl;

import static com.yueyou.adreader.BuildConfig.APPID_BUGLY;
import static com.yueyou.adreader.BuildConfig.WP_APPID_BUGLY;

public class ThirdAnalytics {

    public static class EventId {
        public static final String EVENT_ACTIVATE = "1100";
        public static final String API = "4000";
        public static final String API_LOGIN = "4001";
        public static final String API_ERROR = "4100";
        public static final String API_AD_ERROR = "4500";
        public static final String USER_REFRESHVAILD = "4200";
        public static final String EVENT_READ = "3000";
        public static final String EVENT_READ_BUILD_BOOK = "3100";
        public static final String EVENT_PAY = "2000";
        public static final String AD = "5000";
        public static final String EVENT_AD_VIEW = "5100";
        public static final String EVENT_AD_CLICK = "5200";

        /**
         * 搜索页访问
         */
        public static final String EVENT_PAGE_VIEW_SEARCH = "6100";
        /**
         * 书城访问
         */
        public static final String EVENT_PAGE_VIEW_BOOKSTORE = "6200";

        /**
         * 书库点击
         */
        public static final String EVENT_CLICK_BOOKSTORE = "6300";

        /**
         * 精品点击
         */
        public static final String EVENT_CLICK_SELECT = "6400";

        /**
         * 书架点击
         */
        public static final String EVENT_CLICK_BOOKSHELF = "6500";

        /**
         * 我的点击
         */
        public static final String EVENT_CLICK_PERSIONAL = "6600";

        /**
         * WAP站启动
         */
        public static final String EVENT_FROM_WAP = "6700";
    }

    public static void buylyInit(Context context) {
        String packageName = Widget.getPacketName(context);
        String processName = Widget.getProcessName(android.os.Process.myPid());
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        strategy.setAppChannel(Widget.getChannelId(context));
        strategy.setAppVersion(Widget.getAppVersionName(context));
        if ("com.yueyou.adreaderwp".equals(Widget.getPacketName(context)))
            CrashReport.initCrashReport(context, WP_APPID_BUGLY, BuildConfig.DEBUG);
        else
            CrashReport.initCrashReport(context, APPID_BUGLY, BuildConfig.DEBUG);
    }

    public static void setJavascriptMonitor(WebView view) {
//        CrashReport.setJavascriptMonitor(view, true);
    }

    public static void umengInit(Context context) {
        UMConfigure.init(context, "5cde35fa570df33aaa000c6b", Widget.getChannelId(context), UMConfigure.DEVICE_TYPE_PHONE, null);
//        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        UMConfigure.setEncryptEnabled(BuildConfig.DEBUG);
        UMConfigure.setProcessEvent(true);
        MobclickAgent.setCatchUncaughtExceptions(true);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }

    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    public static void onPageStart(String pageName) {
        MobclickAgent.onPageStart(pageName);
    }

    public static void onPageEnd(String pageName) {
        MobclickAgent.onPageEnd(pageName);
    }

    public static void onSignIn(String Provider, String ID) {
        MobclickAgent.onProfileSignIn(Provider, ID);
    }

    public static void onSignOff() {
        MobclickAgent.onProfileSignOff();
    }

    static void onEvent(Context context, String eventID, Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            MobclickAgent.onEvent(context, eventID);
        } else {
            MobclickAgent.onEvent(context, eventID, map);
        }
    }

    public static void onEventStartFromWap(Context context, String port) {
        Map<String, String> map = getMapUser(context);
        map.put("port", port);
        onEvent(context, EventId.EVENT_FROM_WAP, map);
    }

    public static void onEventTabClick(Context context, String eventId) {
        Map<String, String> map = getMapUser(context);
        onEvent(context, eventId, map);
        Utils.logNoTag("onEventTabClick");
    }

    public static void onEventPageViewSearch(Context context, String from) {
        Map<String, String> map = getMapUser(context);
        map.put("from", from == null ? "" : from);
        onEvent(context, EventId.EVENT_PAGE_VIEW_SEARCH, map);
        Utils.logNoTag("onEventPageViewSearch from %s", from);
    }

    public static void onEventPageViewBookStore(Context context) {
        onEvent(context, EventId.EVENT_PAGE_VIEW_BOOKSTORE, getMapUser(context));
        Utils.logNoTag("onEventPageViewBookStore ");
    }

    public static void onEventAd(Context context, Map<String, String> map) {
        onEvent(context, EventId.AD, map);
    }

    static void onEventAdvertisement(Context context, int siteId, String cp, boolean clicked) {
        onEvent(context, clicked ? EventId.EVENT_AD_CLICK : EventId.EVENT_AD_VIEW, getMapAd(siteId, cp));
    }

    static void onEventRead(Context context, int bookId, String bookName, boolean isLastChapter) {
        onEvent(context, EventId.EVENT_READ, getMapRead(context, bookId, bookName, isLastChapter));
    }

    public static void onEventApi(Context context, Map<String, String> map) {
        onEvent(context, EventId.API, map);
    }

    static void onEventLogin(Context context, String userId, String token) {
        onEvent(context, EventId.API_LOGIN, getMapUser(context, userId, token));
    }

    static void onEventRefreshVaild(Context context, String userId, String token) {
        onEvent(context, EventId.USER_REFRESHVAILD, getMapUser(context, userId, token));
    }

    static void onEventActivate(Context context, String siteId, String bookId, String bookName) {
        onEvent(context, EventId.EVENT_ACTIVATE, getMapActivate(siteId, bookId, bookName));
    }

    public static void onEventErrorApi(Context context, HttpUrl url, int status, String message) {
        onEvent(context, EventId.API_ERROR, getMapUrl(url, status, message));
    }

    public static void onEventErrorApiAd(Context context, String url, int status, String message) {
        onEvent(context, EventId.API_AD_ERROR, getMapUrl(url, status, message));
    }

    public static void reportError(Context context, String format, Object... args) {
        MobclickAgent.reportError(context, new Formatter(Locale.getDefault()).format(format, args).toString());
        Utils.logError(format, args);
    }

    public static void reportError(Context context, Throwable e) {
        MobclickAgent.reportError(context, e);
        Utils.logError(e, "error %s", e.getMessage());
    }

    public static Map<String, String> getMapUrl(String url, String deviceId, String version,
                                                String packetId, String userId, String sex, String channelI) {
        Map<String, String> map = new HashMap<>();
        setUrl(map, url);
        map.put("deviceId", deviceId);
        map.put("version", version);
        map.put("packetId", packetId);
        map.put("userId", userId);
        map.put("sex", sex);
        map.put("channelI", channelI);
        return map;
    }

    public static Map<String, String> getMapAd(AdContent adContent, int status, String message) {
        Map<String, String> map = new HashMap<>();
        map.put("cp", adContent.getCp());
        map.put("siteId", String.valueOf(adContent.getSiteId()));
        map.put("status", status == 0 ? "success" : "fail");
        map.put("message", adContent.getCp() + "::" + message);
        return map;
    }

    private static Map<String, String> getMapRead(Context context, int bookId, String bookName, boolean isLastChapter) {
        Map<String, String> map = getMapUser(context);
        map.put("bookId", String.valueOf(bookId));
        map.put("bookName", bookName);
        map.put("isLastChapter", String.valueOf(isLastChapter));
        return map;
    }

    private static Map<String, String> getMapActivate(String siteId, String bookId, String bookName) {
        Map<String, String> map = new HashMap<>();
        map.put("bookId", String.valueOf(bookId));
        map.put("bookName", bookName);
        map.put("siteId", siteId);
        return map;
    }

    private static Map<String, String> getMapAd(int siteId, String cp) {
        Map<String, String> map = new HashMap<>();
        map.put("cp", Utils.format("%s%d", cp, siteId));
        map.put("siteId", String.valueOf(siteId));
        return map;
    }

    private static Map<String, String> getMapUrl(HttpUrl url, int status, String message) {
        Map<String, String> map = new HashMap<>();
        map.put("url", Utils.format("%s://%s/%s", url.scheme(), url.host(), url.encodedPath()));
        map.put("status", status == 0 ? "success" : "fail");
        map.put("message", message);
        return map;
    }

    private static Map<String, String> getMapUrl(String url, int status, String message) {
        Map<String, String> map = new HashMap<>();
        map.put("url", url);
        map.put("status", status == 0 ? "success" : "fail");
        map.put("message", message);
        return map;
    }

    private static Map<String, String> getMapUser(Context context, String userId, String token) {
        Map<String, String> map = new HashMap<>();
        map.put("deviceId", Widget.getDeviceId(context));
        map.put("userId", userId);
        map.put("token", token);
        return map;
    }

    private static Map<String, String> getMapUser(Context context, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        return map;
    }

    private static Map<String, String> getMapUser(Context context) {
        String userId = DataSHP.getUserId(context);
        String token = DataSHP.getToken(context);
        Map<String, String> map = new HashMap<>();
        map.put("deviceId", Widget.getDeviceId(context));
        map.put("userId", userId);
        map.put("token", token);
        return map;
    }

    private static void setUrl(Map<String, String> map, String url) {
        if (url.contains("?")) {
            map.put("url", url.substring(0, url.indexOf("?") + 1));
        } else {
            map.put("url", url);
        }
    }

}
