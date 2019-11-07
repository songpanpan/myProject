package com.yueyou.adreader.service.model;

import com.google.gson.annotations.SerializedName;

public class ChapterInfo {
    private String chapterName;
    @SerializedName("chapterId")
    private int chapterID;
    private int bookID;
    private int categoryID;
    private boolean isVipChapter;
    private int vip;
    @SerializedName("preChapterId")
    private String previousChapterId;
    private String nextChapterId;
    private String chapterCategoryName;
    private String contentUrl;
    private int chapterCount;
    //是否付费资产
    @SerializedName("is_hide_ad")
    private int hideAd;
    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getChapterID() {
        return chapterID;
    }

    public void setChapterID(int chapterID) {
        this.chapterID = chapterID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public boolean isVipChapter() {
        return vip == 1;
    }

    public void setVipChapter(boolean vipChapter) {
        isVipChapter = vipChapter;
    }

    public String getPreviousChapterId() {
        return previousChapterId;
    }

    public void setPreviousChapterId(String previousChapterId) {
        this.previousChapterId = previousChapterId;
    }

    public String getNextChapterId() {
        return nextChapterId;
    }

    public void setNextChapterId(String nextChapterId) {
        this.nextChapterId = nextChapterId;
    }

    public String getChapterCategoryName() {
        return chapterCategoryName;
    }

    public void setChapterCategoryName(String chapterCategoryName) {
        this.chapterCategoryName = chapterCategoryName;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
        this.isVipChapter = vip==1;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public int getHideAd() {
        return hideAd;
    }

    public void setHideAd(int hideAd) {
        this.hideAd = hideAd;
    }
}
