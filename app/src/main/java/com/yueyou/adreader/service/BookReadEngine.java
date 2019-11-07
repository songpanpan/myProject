package com.yueyou.adreader.service;

import android.content.Context;

import com.yueyou.adreader.service.db.BookFileEngine;
import com.yueyou.adreader.service.model.ChapterContent;
import com.yueyou.adreader.service.model.ChapterInfo;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Widget;

import java.util.List;

/**
 * Created by zy on 2017/3/30.
 */

public class BookReadEngine {
    private ChapterContent mChapterContent;
    private int mBufferOffset;
    private int mSeekOffset;
    private String mOutBuffer;
    private int mBookId;
    private int mChapterId;
    private static final int MAX_BUFFER_SIZE = 8 * 1024;
    private static final int MAX_LINE_SIZE = 1024;
    private static final int MAX_REDUNDANCE_SIZE = 1024;

    public boolean openBook(Context context, int bookId, int chapterId) {
        mChapterContent = BookFileEngine.getBookChapterContent(context, bookId, chapterId);
        mBufferOffset = 0;
        mSeekOffset = 0;
        mOutBuffer = "";
        mBookId = bookId;
        mChapterId = chapterId;
        return !contentEmpty();
    }

    private boolean contentEmpty() {
        return mChapterContent == null || Widget.isBlank(mChapterContent.getContent());
    }

    public String getChapterName() {
        String str = mChapterContent.getTitle();
        if (Widget.isBlank(str))
            return "";
        str = str.replaceAll(" +", " ");
        return str;
    }

    public boolean isVip() {
        if (mChapterContent == null) {
            return false;
        }
        return mChapterContent.isVip();
    }

    /**
     * 判断是否是第一章
     * @return
     */
    public boolean isFirstChapter(){
        if (mChapterContent == null) {
            return false;
        }
        if (Widget.isBlank(mChapterContent.getPreviousChapterId()))
            return false;
        if ("firstpage".equals(mChapterContent.getPreviousChapterId()))
            return true;
        return false;
    }

    public int preChapterId() {
        if (mChapterContent == null) {
            return 0;
        }
        if (Widget.isBlank(mChapterContent.getPreviousChapterId()))
            return 0;
        if ("firstpage".equals(mChapterContent.getPreviousChapterId()))
            return 0;
        return Integer.parseInt(mChapterContent.getPreviousChapterId());
    }

    public void reloadOnlyChapterContent(Context context, int bookId, int chapterId) {
        mChapterContent = BookFileEngine.getBookChapterContent(context, bookId, chapterId);
    }

    public int nextChapterId(Context context) {
        if (mChapterContent == null) {
            return 0;
        }
        if (Widget.isBlank(mChapterContent.getNextChapterId()))
            return 0;
        if ("lastpage".equals(mChapterContent.getNextChapterId())) {
            return 0;
        }
        return Integer.parseInt(mChapterContent.getNextChapterId());
    }

    private int getNextChapterId(Context context) {
        List<ChapterInfo> chapterInfoList = BookFileEngine.getBookChapterList(context, mBookId);
        if (chapterInfoList == null || chapterInfoList.size() == 0) {
            return 0;
        }
        for (int i = chapterInfoList.size() - 1; i > 0; i--) {
            LogUtil.e("@ getNextChapterId, chapterList size" + chapterInfoList.size() + " i:" + i);
            if (mChapterId == chapterInfoList.get(i).getChapterID()) {
                if (i < chapterInfoList.size() - 1) {
                    return chapterInfoList.get(i + 1).getChapterID();
                } else {
                    break;
                }
            }
        }

//        for (int i = 0; i < chapterInfoList.size(); i++) {
//            if (mChapterId == chapterInfoList.get(i).getChapterID() && i < chapterInfoList.size() - 1) {
//                return chapterInfoList.get(i + 1).getChapterID();
//            }
//        }
        return 0;
    }

    public boolean preBlock() {
        if (contentEmpty())
            return false;
        if (mBufferOffset == 0)
            return false;
        int offset = mBufferOffset - (MAX_BUFFER_SIZE + MAX_LINE_SIZE);
        mSeekOffset = mBufferOffset;
        if (offset <= 0) {
            mBufferOffset = 0;
            mOutBuffer = contentSubstring(0, mSeekOffset);
        } else {
            int index = mChapterContent.getContent().indexOf("\n", offset);
            if (index >= MAX_LINE_SIZE || index < 0) {
                index = MAX_LINE_SIZE;
            } else {
                index++;
            }
            if (index <= MAX_REDUNDANCE_SIZE) {
                index = 0;
            }
            mBufferOffset = index;
            mOutBuffer = contentSubstring(index, mSeekOffset);
        }
        mSeekOffset = mBufferOffset + mOutBuffer.length();
        return true;
    }

    public boolean nextBlock() {
        if (contentEmpty() || mSeekOffset >= mChapterContent.getContent().length())
            return false;
        mBufferOffset = mSeekOffset;
        mOutBuffer = contentSubstring(mSeekOffset, mSeekOffset + MAX_BUFFER_SIZE);
        mSeekOffset += mOutBuffer.length();
        if (mOutBuffer.length() < MAX_BUFFER_SIZE || mSeekOffset == mChapterContent.getContent().length()) {
            return true;
        }
        int index = mChapterContent.getContent().indexOf("\n", mSeekOffset);
        if (index < 0 || index > MAX_LINE_SIZE + mSeekOffset) {
            index = MAX_LINE_SIZE + mSeekOffset;
        }
        index++;
        String line = contentSubstring(mSeekOffset, index);
        mSeekOffset += line.length();
        mOutBuffer += line;
        int remain = mChapterContent.getContent().length() - mSeekOffset;
        if (0 < remain && remain <= MAX_REDUNDANCE_SIZE) {
            String remainStr = contentSubstring(mSeekOffset, mChapterContent.getContent().length());
            mSeekOffset += remainStr.length();
            mOutBuffer += remainStr;
        }
        return true;
    }

    private String contentSubstring(int begin, int end) {
        if (begin < 0)
            begin = 0;
        if (end >= mChapterContent.getContent().length())
            end = mChapterContent.getContent().length();
        if (begin >= end)
            return "";
        return mChapterContent.getContent().substring(begin, end);
    }

    public boolean seek(int position) {
        if (contentEmpty())
            return false;
        mSeekOffset = position;
        return true;
    }

    public boolean gotoPosition(int position) {
        if (contentEmpty())
            return false;
        seekAndLoad(position);
        return true;
    }

    public boolean seekAndLoad(int position) {
        if (contentEmpty())
            return false;
        mSeekOffset = position;
        if (mSeekOffset >= length())
            mSeekOffset = length() - MAX_BUFFER_SIZE;
        if (mSeekOffset < 50)
            mSeekOffset = 0;
        return nextBlock();
    }

    public String outBuffer() {
        return mOutBuffer;
    }

    public int outBufferOffset() {
        return mBufferOffset;
    }

    public boolean isEnd() {
        if (contentEmpty())
            return false;
        return mChapterContent.getContent().length() == mSeekOffset;
    }

    public int length() {
        if (contentEmpty())
            return 0;
        return mChapterContent.getContent().length();
    }
}
