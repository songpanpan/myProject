package com.yueyou.adreader.service.model;

public class BookDetail {

    /**
     * showTag : 0
     * bookId : 203001
     * author : 十月流年
     * firstChapterId : 203002
     * bookCover : https://cdn.p.yueyouxs.com/wap/30523/30523.jpg
     * bookName : 至尊修罗
     * copyrightName :
     * curReadChapterId : 0
     * wapBookId : 30523
     */

    private int showTag;
    private int bookId;
    private String author;
    private String firstChapterId;
    private String bookCover;
    private String bookName;
    private String copyrightName;
    private String curReadChapterId;
    private int wapBookId;

    public int getShowTag() {
        return showTag;
    }

    public void setShowTag(int showTag) {
        this.showTag = showTag;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFirstChapterId() {
        return firstChapterId;
    }

    public void setFirstChapterId(String firstChapterId) {
        this.firstChapterId = firstChapterId;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getCopyrightName() {
        return copyrightName;
    }

    public void setCopyrightName(String copyrightName) {
        this.copyrightName = copyrightName;
    }

    public String getCurReadChapterId() {
        return curReadChapterId;
    }

    public void setCurReadChapterId(String curReadChapterId) {
        this.curReadChapterId = curReadChapterId;
    }

    public int getWapBookId() {
        return wapBookId;
    }

    public void setWapBookId(int wapBookId) {
        this.wapBookId = wapBookId;
    }
}
