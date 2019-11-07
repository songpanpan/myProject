package com.yueyou.adreader.service.model;

import com.yueyou.adreader.service.db.DBEngine;
import com.yueyou.adreader.util.Widget;

public class BookShelfItem {
    @DBEngine.PrimaryKey
    private int bookId;
    private String bookName;
    private String bookCover;
    private String categoryName;
    private String information;
    private String author;
    private String copyrightName;
    private int categoryID;
    private boolean isFinished;
    private int chapterCount;
    private int bookType;//0:txt 1:hs 10:到书城 11:广告
    private int bookPathType;//1:当前为封面页；0：不是封面页
    private int chapterIndex;
    private int dataOffset;
    private int displayOffset;
    private int readTimer;
    private boolean isUpdate;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
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

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public int getBookPathType() {
        return bookPathType;
    }

    public void setBookPathType(int bookPathType) {
        this.bookPathType = bookPathType;
    }

    public int getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(int dataOffset) {
        this.dataOffset = dataOffset;
    }

    public int getDisplayOffset() {
        return displayOffset;
    }

    public void setDisplayOffset(int displayOffset) {
        this.displayOffset = displayOffset;
    }

    public int getReadTimer() {
        return readTimer;
    }

    public void setReadTimer(int readTimer) {
        this.readTimer = readTimer;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public BookShelfItem() {

    }

    public BookShelfItem(BookInfo bookInfo) {
        this.bookId = bookInfo.getSiteBookID();
        this.bookName = bookInfo.getName();
        this.categoryName = bookInfo.getCategoryName();
        this.information = bookInfo.getInformation();
        this.author = bookInfo.getAuthor();
        this.categoryID = bookInfo.getCategoryID();
        this.isFinished = bookInfo.isFinished();
        this.chapterCount = bookInfo.getChapterCount();
        this.bookCover = bookInfo.getImageUrl();
        this.copyrightName = bookInfo.getCopyrightName();
        this.bookType = 1;
        this.bookPathType = 1;
        this.isUpdate = false;
        this.readTimer = Integer.parseInt(Widget.getTimeStamp());
    }

    public void refreshReadTime() {
        this.readTimer = Integer.parseInt(Widget.getTimeStamp());
    }

    public boolean isAd() {
        return bookType == 11;
    }
}
