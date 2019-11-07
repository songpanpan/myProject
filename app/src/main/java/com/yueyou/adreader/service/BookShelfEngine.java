package com.yueyou.adreader.service;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.google.gson.reflect.TypeToken;
import com.yueyou.adreader.activity.WebViewActivity;
import com.yueyou.adreader.activity.YueYouApplication;
import com.yueyou.adreader.service.advertisement.adObject.AdBookCover;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.service.db.BookFileEngine;
import com.yueyou.adreader.service.db.DBEngine;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.service.model.BookInfo;
import com.yueyou.adreader.service.model.BookShelfItem;
import com.yueyou.adreader.util.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zy on 2017/3/30.
 */

public class BookShelfEngine {
    List<BookShelfItem> mBooks;
    private Context mContext;
    private BookUpdateListener mBookUpdateListener;
    public BookShelfEngine(Context context, BookUpdateListener bookUpdateListener){
        mContext = context;
        mBookUpdateListener = bookUpdateListener;
        loadBooks();
    }

    public void release(){
        mHandler.removeMessages(1);
    }

    private void loadBooks(){
        if (mBooks != null)
            return;
        mBooks = new ArrayList<BookShelfItem>();
        DBEngine.getInstens(mContext).loadData(mBooks, BookShelfItem.class);
        sortBookShelf();
    }

    public List<BookShelfItem> books() {
        return mBooks;
    }

    public int size(){
        return mBooks.size();
    }

    public synchronized BookShelfItem get(int index){
        if (index < 0 || index >= mBooks.size())
            index = 0;
        return mBooks.get(index);
    }

    public synchronized boolean refreshBookChapterCount(int bookId, int chapterCount){
        int index = this.getBookIndex(bookId);
        if (index < 0)
            return false;
        if (chapterCount < mBooks.get(index).getChapterCount())
            return false;
        mBooks.get(index).setChapterCount(chapterCount);
        DBEngine.getInstens(mContext).updateObject(mBooks.get(index));
        return true;
    }

    public synchronized void refreshBookReadProgress(BookShelfItem book){
        book.refreshReadTime();
        DBEngine.getInstens(mContext).updateObject(book);
        try {
            if (mBooks.get(0) != book){
                sortBookShelf();
                mBookUpdateListener.bookUpdate();
            }
        }catch (Exception e){
            ThirdAnalytics.reportError(mContext, e);
            e.printStackTrace();
        }
    }

    public synchronized BookShelfItem getBook(int bookId){
        int index = getBookIndex(bookId);
        if(index < 0)
            return null;
        return mBooks.get(index);
    }

    public synchronized boolean addBook(BookInfo bookInfo, int chapterId, boolean sort, boolean reset){
        int index = this.getBookIndex(bookInfo.getSiteBookID());
        if (index < 0){
            BookShelfItem bookShelfItem = new BookShelfItem(bookInfo);
            bookShelfItem.setChapterIndex(chapterId);
            mBooks.add(bookShelfItem);
            DBEngine.getInstens(mContext).addDObject(bookShelfItem);
        }else {
            mBooks.get(index).refreshReadTime();
            if (reset) {
                mBooks.get(index).setDisplayOffset(0);
                mBooks.get(index).setDataOffset(0);
                mBooks.get(index).setChapterIndex(chapterId);
            }
            DBEngine.getInstens(mContext).updateObject(mBooks.get(index));
        }
        if (sort)
            sortBookShelf();
        return index < 0;
    }

    public synchronized boolean deleteBook(int bookId){
        int index = this.getBookIndex(bookId);
        if (index < 0)
            return false;
        if (mBooks.get(index).isAd()){
            WebViewActivity.show((Activity) mContext, Url.URL_AD_VIP,
                    WebViewActivity.ACCOUNT, "");
        }
        DBEngine.getInstens(mContext).deleteObject(mBooks.get(index));
        mBooks.remove(index);
        BookFileEngine.deleteBook(mContext, bookId);
        return true;
    }

    public synchronized void addBookAd(BookShelfItem bookShelfItem){
        mBooks.add(bookShelfItem);
        sortBookShelf();
    }

    private synchronized int getBookIndex(int bookId){
        for (int i = 0; i < mBooks.size(); i++){
            if (mBooks.get(i).getBookId() == bookId){
                return i;
            }
        }
        return -1;
    }

    private synchronized void resetUpdateFlag(){
        for (BookShelfItem item : mBooks){
            item.setUpdate(false);
        }
    }

    private synchronized void setUpdateFlag(int bookId){
        for (BookShelfItem item : mBooks){
            if (item.getBookId() == bookId) {
                item.setUpdate(true);
                return;
            }
        }
    }

    public synchronized void sortBookShelf(){
        Collections.sort(mBooks, (BookShelfItem o1, BookShelfItem o2)-> {
            return Long.compare(o2.getReadTimer(), o1.getReadTimer());
        });
    }

    public void startCheckUpdate(){
        mHandler.sendEmptyMessageDelayed(1, 10000);
    }

    public void startGetBookAd(){
        mHandler.sendEmptyMessageDelayed(2, 2000);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                checkUpdate();
                mHandler.sendEmptyMessageDelayed(1, 600000);
            }else if (msg.what == 2) {
                mAdBookCover.load();
            }
        };
    };
    private AdBookCover mAdBookCover = new AdBookCover(new AdEventObject.AdEventObjectListener() {
        @Override
        public void showed(AdContent adContent) {
            BookShelfItem bookShelfItem = new BookShelfItem();
            bookShelfItem.setBookType(11);
            bookShelfItem.setBookId(0xefffffff);
            bookShelfItem.setBookName(adContent.getAppKey());
            bookShelfItem.setDataOffset(adContent.getSiteId());
            bookShelfItem.setAuthor(adContent.getCp());
            bookShelfItem.refreshReadTime();
            bookShelfItem.setReadTimer(bookShelfItem.getReadTimer() + 360000);
            Action.getInstance().downloadCover(mContext, adContent.getPlaceId(),
                    bookShelfItem.getBookId(), false);
            addBookAd(bookShelfItem);
            ((YueYouApplication)mContext.getApplicationContext()).getMainActivity().bookshelfFrament().refreshView();
        }

        @Override
        public void closed() {

        }
    });

    public synchronized void checkCover() {
        try {
            if (Action.getInstance().getBookCover(mContext, mBooks))
                mBookUpdateListener.bookUpdate();
        }catch (Exception e){

        }
    }

    public synchronized void uploadBookIds() {
        try {
            String param = "";
            for (BookShelfItem item : mBooks){
                if (item.isAd())
                    continue;
                param += item.getBookId() + "," ;
            }
            Action.getInstance().uploadBookIds(mContext, param);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void checkUpdate(){
        try {
            String param = "";
            for (BookShelfItem item : mBooks){
                if (item.isAd())
                    continue;
                param += item.getBookId() + ":" + item.getChapterCount() + ";";
            }
             Action.getInstance().getUpdateBook(mContext, param, (Object object)->{
                 List<String> updateBooks = (List<String>) Widget.jsonToObjectByMapStr(object, new TypeToken<List<String>>(){}.getType());
                 resetUpdateFlag();
                 if (updateBooks.size() > 0){
                     for (String item : updateBooks){
                        setUpdateFlag(Integer.parseInt(item));
                     }
                 }
                 mBookUpdateListener.bookUpdate();
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        new Thread(()-> {
            checkCover();
        }).start();
    }

    public interface BookUpdateListener{
        void bookUpdate();
    }
}
