package com.yueyou.adreader.service.analytics.model;

import com.yueyou.adreader.service.analytics.model.base.BookBase;

public class Read extends BookBase {
    private int chapterCount;
    private boolean isLastChapter;
    private int words;

    public boolean isLastChapter() {
        return isLastChapter;
    }

    public void setLastChapter(boolean lastChapter) {
        isLastChapter = lastChapter;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public int getWords() {
        return words;
    }

    public void setWords(int words) {
        this.words = words;
    }

}
