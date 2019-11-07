package com.yueyou.adreader.service.model;

public class GetBookResponse {
    private int bookId;
    private String bookCover;
    private String bookName;
    private String firstChapterId;
    private String curReadChapterId;

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

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getFirstChapterId() {
        return firstChapterId;
    }

    public void setFirstChapterId(String firstChapterId) {
        this.firstChapterId = firstChapterId;
    }

    public String getCurReadChapterId() {
        return curReadChapterId;
    }

    public void setCurReadChapterId(String curReadChapterId) {
        this.curReadChapterId = curReadChapterId;
    }
}
