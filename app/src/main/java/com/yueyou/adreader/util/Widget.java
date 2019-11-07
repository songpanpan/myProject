package com.yueyou.adreader.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.WindowManager;

import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.yueyou.adreader.service.db.DataSHP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipFile;

/**
 * Created by zy on 2017/3/29.
 */

public class Widget {
    public static boolean isBlank(String str) {
        if (str == null || str.length() == 0)
            return true;
        return false;
    }

    public static String encodeByMd5Bit32(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

    public static String getDeviceId(Context ctx) {
        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return uuid(ctx);
            }
            String imei = tm.getDeviceId().trim();
            if (YYStringUtils.isBlank(imei.replace("0", "")) || YYStringUtils.isBlank(imei)
                    || imei.length() > 40)
                return uuid(ctx);
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uuid(ctx);
    }

    public static String getNetStatus() {
        NetworkUtils.NetworkType networkType = NetworkUtils.getNetworkType();
        switch (networkType) {
            case NETWORK_2G:
                return "2g";
            case NETWORK_3G:
                return "3g";
            case NETWORK_4G:
                return "4g";
            case NETWORK_WIFI:
                return "wifi";
        }
        return "wifi";
    }

    public static String getNetIsp(Context ctx) {
        String imsi = getImsi(ctx);
        if (imsi != null && imsi.length() > 5) {
            return imsi.substring(0, 4);
        }
        return "";
    }

    public static String getImsi(Context ctx) {
        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            return tm.getSubscriberId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String uuid(Context context) {
        String uuid = DataSHP.getDeviceId(context);
        if (YYStringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            DataSHP.setDeviceId(context, uuid);
        }
        return uuid;
    }

    public static String getPhoneType() {
        return android.os.Build.BRAND;
    }

    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    public static int getAppVersionId(Context ctx) {
        try {
            PackageManager packageManager = ctx.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
            int version = packInfo.versionCode;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String getApkFileVersionName(Context ctx, String fileName) {
        try {
            PackageManager packageManager = ctx.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageArchiveInfo(fileName, 0);
            String version = packInfo.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAppVersionName(Context ctx) {
        try {
            PackageManager packageManager = ctx.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0";
    }

    public static String getPacketName(Context ctx) {
        try {
            PackageManager packageManager = ctx.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
            return packInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "com.yueyou.reader";
    }

    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isWifiConnect(Context ctx) {
        try {
            if (Build.VERSION.SDK_INT < 21) {
                return false;
            }
            ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network[] networks = manager.getAllNetworks();
            for (Network network : networks) {
                NetworkInfo networkInfo = manager.getNetworkInfo(network);
                if (networkInfo.getTypeName().equalsIgnoreCase("WIFI") && networkInfo.isConnected())
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isNetWorkConnected(Context ctx) {
        try {
            if (Build.VERSION.SDK_INT < 21) {
                return false;
            }
            ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network[] networks = manager.getAllNetworks();
            for (Network network : networks) {
                NetworkInfo networkInfo = manager.getNetworkInfo(network);
                String typeName = networkInfo.getTypeName();
                if ((typeName.equalsIgnoreCase("WIFI") || typeName.equalsIgnoreCase("MOBILE")) && networkInfo.isConnected())
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前的网络状态 ：没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2
     * 自定义
     *
     * @param context
     * @return
     */
    public static int getAPNType(Context context) {
        try {
            //结果返回值
            int netType = 0;
            //获取手机所有连接管理对象
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取NetworkInfo对象
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //NetworkInfo对象为空 则代表没有网络
            if (networkInfo == null) {
                return netType;
            }
            //否则 NetworkInfo对象不为空 则获取该networkInfo的类型
            int nType = networkInfo.getType();
            if (nType == ConnectivityManager.TYPE_WIFI) {
                //WIFI
                netType = 1;
            } else if (nType == ConnectivityManager.TYPE_MOBILE) {
                int nSubType = networkInfo.getSubtype();
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                //3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
                if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
                        && !telephonyManager.isNetworkRoaming()) {
                    netType = 4;
                } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                        || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                        || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
                        && !telephonyManager.isNetworkRoaming()) {
                    netType = 3;
                    //2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
                } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
                        || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                        || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                        && !telephonyManager.isNetworkRoaming()) {
                    netType = 2;
                } else {
                    netType = 2;
                }
            }
            return netType;
        } catch (Exception e) {
            return 0;
        }
    }

    public static void setBrightness(Context context, float value) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.screenBrightness = value;
        ((Activity) context).getWindow().setAttributes(lp);
    }

    public static void systemBrightness(Context context) {
        setBrightness(context, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spVal) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,

                spVal, context.getResources().getDisplayMetrics());

    }

    public static String getTimeStamp() {
        long ts_long = System.currentTimeMillis() / 1000;
        int ts_int = (int) ts_long;
        String ts = String.valueOf(ts_int);
        return ts;
    }

    public static String getChannelId(Context ctx) {
        return Widget.getMetaDataValue(ctx, "YueYouChannelId", "yueyou");
    }

    public static String getMetaDataValue(Context context, String name, String def) {
        String value = getMetaDataValueEx(context, context.getPackageName(), name);
        return (value == null) ? def : value;
    }

    public static String getMetaDataValueEx(Context context, String packageName, String name) {
        Object value = null;
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                value = applicationInfo.metaData.get(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (value == null) return null;
        return value.toString();
    }

    public static String getSPDataValue(Context ctx, String name, String def) {
        SharedPreferences sp = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        return sp.getString(name, def);
    }

    public static void setSPDataValue(Context ctx, String key, String value) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getSPIntValue(Context ctx, String name, int def) {
        SharedPreferences sp = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        return sp.getInt(name, def);
    }

    public static void setSPIntValue(Context ctx, String key, int value) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startActivity(Activity srcActivity, Class dstActivity) {
        final Intent intentData = new Intent(srcActivity, dstActivity);
        srcActivity.startActivity(intentData);
    }

    public static void startActivity(Activity srcActivity, Class dstActivity, String key, Object object) {
        final Intent intentData = new Intent(srcActivity, dstActivity);
        intentData.putExtra(key, Widget.objectToString(object));
        srcActivity.startActivity(intentData);
    }

    public static void startActivity(Activity srcActivity, Class dstActivity, Map<String, Object> params) {
        final Intent intentData = new Intent(srcActivity, dstActivity);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            intentData.putExtra(entry.getKey(), Widget.objectToString(entry.getValue()));
        }
        srcActivity.startActivity(intentData);
    }

    public static void startActivityForResult(Activity srcActivity, Class dstActivity, int requestCode) {
        final Intent intentData = new Intent(srcActivity, dstActivity);
        srcActivity.startActivityForResult(intentData, requestCode);
    }

    public static void callPhone(Activity activity, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
//        Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + phoneNum));
//        if (ContextCompat.checkSelfPermission(activity,
//                Manifest.permission.CALL_PHONE)
//                != PackageManager.PERMISSION_GRANTED)
//        {
//
//            ActivityCompat.requestPermissions(activity,
//                    new String[]{Manifest.permission.CALL_PHONE},
//                    1);
//        } else
//        {
//            activity.startActivity(intent);
//        }
    }


    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String apkInstallName(Context context) {
        String path = context.getPackageResourcePath();
        return path;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getApkCommentInfo(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return null;
        }
        File file = new File(apkInstallName(context));
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);
            String commentStr = zipFile.getComment();
            commentStr = URLDecoder.decode(commentStr, "utf-8");
            return commentStr;
        } catch (Exception e) {
            Utils.logNoTag("getApkCommentInfo error %s", e.getMessage());
        } finally {
            if (null != zipFile) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getTextFromClip(Context context) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            //判断剪切版时候有内容
            if (!clipboardManager.hasPrimaryClip())
                return null;
            ClipData clipData = clipboardManager.getPrimaryClip();
            //获取 ClipDescription
            ClipDescription clipDescription = clipboardManager.getPrimaryClipDescription();
            //获取 lable
            //String lable = clipDescription.getLabel().toString();
            //获取 text
            String text = clipData.getItemAt(0).getText().toString();
            return decodeClipText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decodeClipText(String text) {
        try {
            byte[] r = android.util.Base64.decode(text, android.util.Base64.DEFAULT);
            text = new String(r);
            if (!text.startsWith("yueyou:"))
                return null;
            return text.substring("yueyou:".length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static String objectToString(Object object) {
        try {
            Gson gson = new Gson();
            return gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object stringToObject(String string, Class classz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(string, classz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object stringToObject(String string, Type typeOfT) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(string, typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object jsonToObject(Object jsonElement, Class classz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson((JsonElement) jsonElement, classz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object jsonToObjectByMapStr(Object map, Class classz) {
        try {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            String str = gson.toJson(map);
            return stringToObject(str, classz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object jsonToObjectByMapStr(Object map, Type typeOfT) {
        try {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            String str = gson.toJson(map);
            return stringToObject(str, typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void downloadApk(Activity activity, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public static void sendEmptyMessageDelayed(Handler handler, int what, long delayMillis) {
        handler.removeMessages(what);
        handler.sendEmptyMessageDelayed(what, delayMillis);
    }
}
