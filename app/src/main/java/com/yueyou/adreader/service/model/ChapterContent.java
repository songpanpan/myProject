package com.yueyou.adreader.service.model;

public class ChapterContent {
    private String title;
    private String content;
    private String previousChapterId;
    private String nextChapterId;
    private boolean vip;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }
}
