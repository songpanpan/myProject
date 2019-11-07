package com.yueyou.adreader.service;

import android.content.Context;

import com.yueyou.adreader.service.db.BookFileEngine;
import com.yueyou.adreader.service.model.BookMarkItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2017/3/30.
 */

public class BookMarkEngine {
    private List<BookMarkItem> mBookMarkItems;
    private int mBookId;
    public BookMarkEngine(Context context, int bookId){
        reload(context, bookId);
    }

    public void reload(Context context, int bookId) {
        mBookId = bookId;
        mBookMarkItems = BookFileEngine.getBookMark(context, mBookId);
        if (mBookMarkItems == null)
            mBookMarkItems = new ArrayList<>();
    }

    public int size() {
        return mBookMarkItems.size();
    }

    public BookMarkItem get(int index){
        return mBookMarkItems.get(index);
    }

    public boolean isMark(int chapterId, int dataOffset, int displayOffset) {
        return getMark(chapterId, dataOffset, displayOffset) >= 0;
    }

    private int getMark(int chapterId, int dataOffset, int displayOffset) {
        for (int i = 0; i < mBookMarkItems.size(); i++){
            BookMarkItem item = mBookMarkItems.get(i);
            if (displayOffset == item.getDisplayOffset() && dataOffset == item.getDataOffset() && chapterId == item.getChapterIndex()) {
                return i;
            }
        }
        return -1;
    }

    public void addMark(Context context, String chapterName, String markName, int chapterId, int dataOffset, int displayOffset) {
        if (getMark(chapterId, dataOffset, displayOffset) >= 0)
            return;
        BookMarkItem item = new BookMarkItem();
        item.setChapterName(chapterName);
        item.setMarkName(markName);
        item.setChapterIndex(chapterId);
        item.setDataOffset(dataOffset);
        item.setDisplayOffset(displayOffset);
        mBookMarkItems.add(item);
        BookFileEngine.saveBookMark(context, mBookId, mBookMarkItems);
    }

    public void deleteMark(Context context, int chapterId, int dataOffset, int displayOffset) {
        int index = getMark(chapterId, dataOffset, displayOffset);
        if (index >= 0) {
            mBookMarkItems.remove(index);
            BookFileEngine.saveBookMark(context, mBookId, mBookMarkItems);
        }
    }

    public void deleteMark(Context context, int index) {
        mBookMarkItems.remove(index);
        BookFileEngine.saveBookMark(context, mBookId, mBookMarkItems);
    }

    public void clear(Context context) {
        mBookMarkItems.clear();
        BookFileEngine.saveBookMark(context, mBookId, mBookMarkItems);
    }
}
