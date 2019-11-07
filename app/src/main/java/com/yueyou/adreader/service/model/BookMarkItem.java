package com.yueyou.adreader.service.model;

public class BookMarkItem {
    private String chapterName;
    private String markName;
    private int chapterIndex;//章节索引
    private int dataOffset;//块首地址在章节中的偏移
    private int displayOffset;//显示的行的首地址偏移

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
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
}
