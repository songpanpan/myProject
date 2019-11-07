package com.yueyou.adreader.service.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppUpdateInfo {
    private String title;
    @SerializedName("is_forced")
    private int forced;//0:非强更，且用户取消后不再提示；1：强更，用户不能取消；
    @SerializedName("finish")
    private int finish;//0:不结束，1：结束（只有forced=1时有效）
    @SerializedName("repeat_tip")
    private int repeatTip;//0:不重复提示，1：重复提示（只有forced=0时有效）
    @SerializedName("download_url")
    private String url;
    private List<Feture> list;
    @SerializedName("auto_download")//是否自动下载；0:否 1：是
    private int autoDownload;
    @SerializedName("web_download")//是否用系统浏览器执行下载安装 0:否 1：是
    private int webDownload;
    @SerializedName("apk_version")//apk版本号
    private String apkVersion;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getForced() {
        return forced;
    }

    public void setForced(int forced) {
        this.forced = forced;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Feture> getList() {
        return list;
    }

    public void setList(List<Feture> list) {
        this.list = list;
    }

    public boolean isForceUpdate() {
        return forced == 1;
    }

    public int getAutoDownload() {
        return autoDownload;
    }

    public void setAutoDownload(int autoDownload) {
        this.autoDownload = autoDownload;
    }

    public int getWebDownload() {
        return webDownload;
    }

    public void setWebDownload(int webDownload) {
        this.webDownload = webDownload;
    }


    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public int getRepeatTip() {
        return repeatTip;
    }

    public void setRepeatTip(int repeatTip) {
        this.repeatTip = repeatTip;
    }

    public String getApkVersion() {
        return apkVersion;
    }

    public void setApkVersion(String apkVersion) {
        this.apkVersion = apkVersion;
    }

    public class Feture{
        private String name;
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
