package com.yueyou.adreader.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.yueyou.adreader.BuildConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static void log(String tag, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, format(format, args));
        }
    }

    public static void logError(String tag, Throwable e, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, format(format, args), e);
        }
    }

    public static void logNoTag(String format, Object... args) {
        if (BuildConfig.DEBUG) {
            log("ReadView", format, args);
        }
    }

    public static void logError(String format, Object... args) {
        logError("ReadView", null, format, args);
    }

    public static void logError(Throwable e, String format, Object... args) {
        logError("ReadView", e, format, args);
    }

    public static String format(String format, Object... args) {
        return new Formatter(Locale.getDefault()).format(format, args).toString();
    }

    public static void toast(Context context, String msg) {
        toast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, String msg, int dulation) {
        Toast.makeText(context, msg, dulation).show();
    }

    public static String extractNum(String str) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(str);
        String num = matcher.replaceAll("");
        return num;
    }

    private static long lastEventTime = 0;//上次触发的时间
    private static int spaceTime = 1000;//时间间隔

    public static boolean isFastEvent() {
        long currentTime = System.currentTimeMillis();//当前系统时间
        boolean isEventAllow;//是否允许点击

        if (currentTime - lastEventTime > spaceTime) {

            isEventAllow = true;

        } else {
            isEventAllow = false;
        }
        lastEventTime = currentTime;
        return !isEventAllow;
    }

    private static long lastLoadTime = 0;//上次触发的时间
//    private static int spaceLoadTime = 1000;//时间间隔

    /**
     * 指定时间间隔
     * @param spaceLoadTime
     * @return
     */
    public static boolean isNotFastLoad(int spaceLoadTime) {
        long currentTime = System.currentTimeMillis();//当前系统时间
        boolean isEventAllow;//是否允许点击
        if (currentTime - lastLoadTime > spaceLoadTime) {
            isEventAllow = true;
        } else {
            isEventAllow = false;
        }
        lastLoadTime = currentTime;
        return isEventAllow;
    }

    public static String dateFormat(String pattern, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static boolean isOss(String url) {
        return url.contains("aliyuncs.com");
    }
}
