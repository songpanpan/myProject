package com.yueyou.adreader.view.Event;

import com.yueyou.adreader.service.BookMarkEngine;

public class ReadViewEvent {
    public interface Event{
        void gotoChapter(int chapterId);
        void gotoMark(int index);
        BookMarkEngine getMarkEngine();
    }
    private static Event mReadViewListener;
    public static void setEventListener(Event readViewListener){
        mReadViewListener = readViewListener;
    }

    public static Event eventListener(){
        return mReadViewListener;
    }
}
