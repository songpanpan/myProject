package com.yueyou.adreader.service.model;

import com.google.gson.annotations.SerializedName;

public class BookInfo {
    @SerializedName("SiteBookID")
    private int siteBookID;
    @SerializedName("Name")
    private String name;
    private String categoryName;
    private String information;
    @SerializedName("Author")
    private String author;
    private String copyrightName;
    private int categoryID;
    @SerializedName("IsFinished")
    private boolean isFinished;
    @SerializedName("ChapterCount")
    private int chapterCount;
    @SerializedName("ImageUrl")
    private String imageUrl;

    public int getSiteBookID() {
        return siteBookID;
    }

    public void setSiteBookID(int siteBookID) {
        this.siteBookID = siteBookID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCopyrightName() {
        return copyrightName;
    }

    public void setCopyrightName(String copyrightName) {
        this.copyrightName = copyrightName;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
