package com.yueyou.adreader.service.db;

import android.content.Context;
import android.util.Log;

import com.yueyou.adreader.service.analytics.AnalyticsEngine;
import com.yueyou.adreader.service.model.ReadSettingInfo;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.util.YYStringUtils;

/**
 * Created by zy on 2018/5/23.
 */

public class DataSHP {
    public static final String USERID_SHP = "userid_name";
    public static final String TOKEN_SHP = "token_name";
    public static final String SEX_TYPE_SHP = "sex_type_name";
    public static final String READ_SETTING_SHP = "read_setting_name";
    public static final String UPGRADE_VERSION = "upgrade_version";
    public static final String DEVICE_ID = "device_id";
    public static final String PERMISSIONS_COUNT = "ermissions_count";
    public static final String DEVICE_ACTIVATE = "device_activate";
    public static final String READ_BOOK_CHAPTER = "read_book_chapter";
    public static final String USER_IS_BIND = "user_is_bind";
    public static final String USER_IS_VIP = "user_is_vip";
    public static final String CTL_CONTENT = "ctl_content";
    public static final String REWARD_VIDEO_VIEW_TIME = "reward_video_view_time";
    public static final String CHAPTER_COUNT = "chapter_count_%d";

    public static String getUserId(Context context) {
        return Widget.getSPDataValue(context, USERID_SHP, null);
    }

    public static void saveUserInfo(Context context, String userId, String token) {
        Log.i("saveUserInfo", "saveUserInfo");
        synchronized (DataSHP.class){
            Widget.setSPDataValue(context, USERID_SHP, userId);
            Widget.setSPDataValue(context, TOKEN_SHP, token);
        }
        AnalyticsEngine.login(context);
    }

    public static String getToken(Context context) {
        return Widget.getSPDataValue(context, TOKEN_SHP, null);
    }

    public static String getSexType(Context context) {
        return Widget.getSPDataValue(context, SEX_TYPE_SHP, null);
    }

    public static void saveSexType(Context context, String sexType) {
        Widget.setSPDataValue(context, SEX_TYPE_SHP, sexType);
    }

    public static void savePermissionsCount(Context context, int permissionsCount) {
        Widget.setSPDataValue(context, PERMISSIONS_COUNT, permissionsCount + "");
    }

    public static int getPermissionsCount(Context context) {
        try {
            return Integer.parseInt(Widget.getSPDataValue(context, PERMISSIONS_COUNT, "0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void saveDeviceActivate(Context context) {
        Widget.setSPDataValue(context, DEVICE_ACTIVATE, "1");
    }

    public static boolean isDeviceActivate(Context context) {
        return Widget.getSPDataValue(context, DEVICE_ACTIVATE, null) != null;
    }

    public static String getUpgradeVersion(Context context) {
        return Widget.getSPDataValue(context, UPGRADE_VERSION, null);
    }

    public static void setUpgradeVersion(Context context, String version) {
        Widget.setSPDataValue(context, UPGRADE_VERSION, version);
    }

    public static String getDeviceId(Context context) {
        return Widget.getSPDataValue(context, DEVICE_ID, null);
    }

    public static void setDeviceId(Context context, String deviceId) {
        Widget.setSPDataValue(context, DEVICE_ID, deviceId);
    }

    public static void saveReadSettingInfo(Context context, ReadSettingInfo readSettingInfo) {
        Widget.setSPDataValue(context, READ_SETTING_SHP, Widget.objectToString(readSettingInfo));
    }

    public static ReadSettingInfo getReadSettingInfo(Context context) {
        String str = Widget.getSPDataValue(context, READ_SETTING_SHP, null);
        return (ReadSettingInfo) Widget.stringToObject(str, ReadSettingInfo.class);
    }

    public static void saveReadBookChapter(Context context, int page) {
        Widget.setSPIntValue(context, READ_BOOK_CHAPTER, page);
    }

    public static int getReadBookChapter(Context context) {
        return Widget.getSPIntValue(context, READ_BOOK_CHAPTER, 0);
    }

    public static void saveUserIsBind(Context context, int isBind) {
        Widget.setSPIntValue(context, USER_IS_BIND, isBind);
    }

    public static int getUserIsBind(Context context) {
        return Widget.getSPIntValue(context, USER_IS_BIND, 0);
    }

    public static void saveUserIsVIP(Context context, int isBind) {
        Widget.setSPIntValue(context, USER_IS_VIP, isBind);
    }

    public static boolean checkUserIsVIP(Context context) {
        return Widget.getSPIntValue(context, USER_IS_VIP, 0) == 1;
    }

    public static void saveRewardVideoViewTime(Context context, long time) {
        Widget.setSPDataValue(context, REWARD_VIDEO_VIEW_TIME, time + "");
    }

    public static long getRewardVideoViewTime(Context context) {
        String lastTime = Widget.getSPDataValue(context, REWARD_VIDEO_VIEW_TIME, "");
        if (YYStringUtils.isEmpty(lastTime)) {
            return 0;
        }
        return Long.parseLong(lastTime);
    }

    public static void saveCtlContent(Context context, String key) {
        Widget.setSPDataValue(context, CTL_CONTENT, key);
    }

    public static void removeCtlContent(Context context, String key) {
        Widget.setSPDataValue(context, CTL_CONTENT, "");
    }

    private static String getCtlContent(Context context) {
        return Widget.getSPDataValue(context, CTL_CONTENT, "");
    }

    public static boolean checkCtlContent(Context context, String key) {
        String ctlContent = getCtlContent(context);
        Utils.logNoTag("checkCtlContent::ctlContent -> %s", ctlContent);
        return ctlContent.contains(key);
    }

    public static void saveChapterCount(Context context, int bookId, int chapterCount) {
        Widget.setSPDataValue(context, Utils.format(CHAPTER_COUNT, bookId), Utils.format("%d_%d", chapterCount, System.currentTimeMillis()));
    }

    public static String getChapterCount(Context context, int bookId) {
        return Widget.getSPDataValue(context, Utils.format(CHAPTER_COUNT, bookId), "");
    }
}
