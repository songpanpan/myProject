package com.yueyou.adreader.service.analytics.model;

import com.yueyou.adreader.service.analytics.model.base.Base;

public class Activate extends Base {
    private String siteId;
    private String bookId;
    private String bookName;
    private String androidVersion;
    private String phoneBrand;//手机厂商
    private String phoneModel;//手机型号
    private int apnType;//联网方式
    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getPhoneBrand() {
        return phoneBrand;
    }

    public void setPhoneBrand(String phoneBrand) {
        this.phoneBrand = phoneBrand;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public int getApnType() {
        return apnType;
    }

    public void setApnType(int apnType) {
        this.apnType = apnType;
    }
}
