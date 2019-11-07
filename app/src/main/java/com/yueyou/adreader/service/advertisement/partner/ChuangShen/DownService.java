package com.yueyou.adreader.service.advertisement.partner.ChuangShen;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;


import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.ReadActivity;
import com.yueyou.adreader.util.LogUtil;

import java.io.File;

public class DownService extends Service {
    static String URL = "URL";
    static String TITLE = "TITLE";
    static String ADID = "ADID";
    private NotificationUtils notificationUtils;
    private NotificationUtils.ChannelBuilder channelBuilder;
    private PendingIntent pendingIntent;
    String downloadingApk = "";

    public static void invoke(Context context, String url, String title, int adId) {
        Intent intent = new Intent(context, DownService.class);
        intent.putExtra(URL, url);
        intent.putExtra(TITLE, title);
        intent.putExtra(ADID, adId);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra(URL);
        String title = intent.getStringExtra(TITLE);
        int adId = intent.getIntExtra(ADID, 0);
        if (!TextUtils.isEmpty(url) && adId > 0) {
            if (checkIsDownloading(downloadingApk, adId)) {
                Toast.makeText(this, "正在下载中...", Toast.LENGTH_SHORT).show();
            } else {
                channelBuilder = new NotificationUtils.ChannelBuilder("channelGroupOne", "channelTwo", "channelTwoName", NotificationManager.IMPORTANCE_HIGH)
                        .setChannelName("channelTwoName").setByPassDnd(true).setLightColor(Color.GREEN)
                        .setShowBadge(false).setEnableLight(false).setEnableSound(false).setEnableVibrate(false)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                notificationUtils = new NotificationUtils(this, channelBuilder);
                notificationUtils.init("channelTwo", "channelTwoName", "channelGroupOne", "channelGroupOneName");
                pendingIntent = PendingIntent.getService(this, adId, new Intent(this, ReadActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
                downFile(this, url, title, adId);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public boolean checkIsDownloading(String downloadingApk, int adId) {
        String[] downloadings = downloadingApk.split(",");
        if (downloadings != null && downloadings.length > 0) {
            for (int i = 0; i < downloadings.length; i++) {
                if (downloadings[i].equals(adId + "")) {
                    return true;
                }
            }
        }
        return false;
    }


    private void downFile(Context context, String url, String title, int adId) {
        downloadingApk = downloadingApk + "," + adId;
        Toast.makeText(this, "开始下载", Toast.LENGTH_SHORT).show();
        String fileName = BannerAd.md5(url) + ".apk";
//        Log.e("spptag", "fileName:" + fileName);
        File parentFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//        Log.e("spptag", "parentFile:" + parentFile.getAbsolutePath());
        if (parentFile == null) {
            return;
        }
        DownloadUtil.get().download(url, parentFile.getAbsolutePath(), fileName,
                new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        //下载完成进行相关逻辑操作
//                        isRunning = false;
                        notificationUtils.cancelNoti(adId);
                        downloadingApk.replace("!" + adId, "");
                        installApk(context, file);
                    }

                    @Override
                    public void onDownloading(int progress) {
                        //下载中
                        notificationUtils.notifyProgress(adId, pendingIntent, R.mipmap.logo_300, R.mipmap.logo_300, "ticker", "", title, "下载进度" + progress + "%", 100, progress);
                        LogUtil.e("下载中" + progress);
//                        isRunning = true;
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        //下载异常进行相关提示操作
                        LogUtil.e("下载异常 e");
                        notificationUtils.cancelNoti(adId);
                        downloadingApk.replace("!" + adId, "");
//                        e.printStackTrace();
//                        isRunning = false;
                    }
                });


    }

    public static boolean installApk(Context context, File apkFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", apkFile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
