package com.yueyou.adreader.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;

import com.yueyou.adreader.service.db.BookFileEngine;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.AppUpdateInfo;
import com.yueyou.adreader.util.FILE;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.dlg.TitleMessageDlg;

import java.io.File;

/**
 * Created by zy on 2017/3/30.
 */

public class UpgradeEngine {
    public static final int REQUEST_CODE_APP_INSTALL = 101;
    private AppUpdateInfo mAppUdateInfo;

    public void check(Context context) {
        Action.getInstance().checkAppUpdate(context, (Object object) -> {
            ((Activity) context).runOnUiThread(() -> {
                try {
                    parseCheckResult(context, object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void parseCheckResult(Context context, Object object) {
        mAppUdateInfo = (AppUpdateInfo) Widget.jsonToObjectByMapStr(object, AppUpdateInfo.class);
        if (mAppUdateInfo == null) return;
        if (Widget.isBlank(mAppUdateInfo.getUrl()) || Widget.isBlank(mAppUdateInfo.getApkVersion()))
            return;
        if (mAppUdateInfo.getAutoDownload() == 1 && mAppUdateInfo.getWebDownload() == 0) {
            if (!downloadNewVersion(context, false))
                return;
        }
        String desc = "";
        if (mAppUdateInfo.getList() != null && mAppUdateInfo.getList().size() > 0) {
            for (int i = 0; i < mAppUdateInfo.getList().size(); i++) {
                desc += mAppUdateInfo.getList().get(i).getName() + "\n";
            }
        }
        if (mAppUdateInfo.isForceUpdate()) {
            TitleMessageDlg.showOnlyOk(context, mAppUdateInfo.getTitle(), desc, (boolean result) -> {
                if (mAppUdateInfo.getWebDownload() == 1) {
                    Widget.downloadApk((Activity) context, mAppUdateInfo.getUrl());
                    if (mAppUdateInfo.getFinish() == 1)
                        ((Activity) context).finish();
                } else {
                    downloadNewVersion(context, true);
                }
            });
        } else {
            if (mAppUdateInfo.getRepeatTip() == 0) {
                String version = DataSHP.getUpgradeVersion(context);
                if (version != null && version.equals(mAppUdateInfo.getApkVersion()))
                    return;
            }
            TitleMessageDlg.show(context, mAppUdateInfo.getTitle(), desc, (boolean result) -> {
                if (result) {
                    if (mAppUdateInfo.getWebDownload() == 1) {
                        Widget.downloadApk((Activity) context, mAppUdateInfo.getUrl());
                    } else {
                        downloadNewVersion(context, true);
                    }
                } else {
                    DataSHP.setUpgradeVersion(context, mAppUdateInfo.getApkVersion());
                }
            });
        }
    }

    private boolean downloadNewVersion(Context context, boolean install) {
        try {
            String fileName = Widget.encodeByMd5Bit32(mAppUdateInfo.getUrl()) + ".apk";
            File file = BookFileEngine.getFile(context, "apk/" + fileName);
            if (file == null) {
                return false;
            }
            if (file.exists() && mAppUdateInfo.getApkVersion() != null
                    && mAppUdateInfo.getApkVersion().equals(Widget.getApkFileVersionName(context, file.getAbsolutePath()))) {
                if (install) {
                    installNewVersion(context, file);
                }
                return true;
            }
            file.delete();
            new Thread(() -> {
                Looper.prepare();
                byte[] data = (byte[]) Action.getInstance().request().httpEngine().getRequest(context, mAppUdateInfo.getUrl(), install);
                if (data == null)
                    return;
                FILE.saveFile(file, data);
                if (install)
                    installNewVersion(context, file);
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void callBackInstall(Context context) {
        Intent intents = new Intent();
        intents.setAction("android.intent.action.VIEW");
        Uri contentUri = FileProvider.getUriForFile(context, Widget.getPacketName(context) + ".fileprovider", file);
        intents.setDataAndType(contentUri, "application/vnd.android.package-archive");
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intents);
    }

    private void installNewVersion(Context context, File file) {
        setFile(file);
        Intent intents = new Intent();
        intents.setAction("android.intent.action.VIEW");
        intents.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            intents.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, Widget.getPacketName(context) + ".fileprovider", file);
            intents.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = isHasInstallPermissionWithO(context);
            if (!hasInstallPermission) {
                startInstallPermissionSettingActivity(context);
                return;
            } else {
                callBackInstall(context);
            }
        }
        context.startActivity(intents);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isHasInstallPermissionWithO(Context context) {
        if (context == null) {
            return false;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }

    /**
     * 开启设置安装未知来源应用权限界面
     *
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.setData(Uri.parse("package:" + Widget.getPacketName(context)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_APP_INSTALL);
    }
}
