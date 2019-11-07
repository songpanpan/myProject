package com.yueyou.adreader.service.model;

public class BuildinBookInfo {
    private int bookId;
    private String bookName;
    private String author;
    private String copyrightName;
    private String bookCover;
    private String firstChapterId;
    private int chapterCount;
    private String coverContent;
    private int dftOpen;
    private String curReadChapterId;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
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

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getFirstChapterId() {
        return firstChapterId;
    }

    public void setFirstChapterId(String firstChapterId) {
        this.firstChapterId = firstChapterId;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public String getCoverContent() {
        return coverContent;
    }

    public void setCoverContent(String coverContent) {
        this.coverContent = coverContent;
    }

    public int getDftOpen() {
        return dftOpen;
    }

    public void setDftOpen(int dftOpen) {
        this.dftOpen = dftOpen;
    }

    public String getCurReadChapterId() {
        return curReadChapterId;
    }

    public void setCurReadChapterId(String curReadChapterId) {
        this.curReadChapterId = curReadChapterId;
    }
}
