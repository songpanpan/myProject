package com.yueyou.adreader.service.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.reflect.TypeToken;
import com.yueyou.adreader.service.model.BookMarkItem;
import com.yueyou.adreader.service.model.ChapterContent;
import com.yueyou.adreader.service.model.ChapterInfo;
import com.yueyou.adreader.util.FILE;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Widget;

import java.io.File;
import java.util.List;

public class BookFileEngine {
    public static void deleteBook(Context context, int bookId) {
        File file = getFile(context, "books/" + bookId);
        FILE.delete(file);
    }

    public static void saveBookChapterList(Context context, int bookId, List<ChapterInfo> list) {
        File file = getFile(context, "books/" + bookId + "/list.txt");
        FILE.saveFile(file, Widget.objectToString(list));
        LogUtil.e("bookUpdate@3  saveBookChapterList:");
    }

    public static List<ChapterInfo> getBookChapterList(Context context, int bookId) {
        File file = getFile(context, "books/" + bookId + "/list.txt");
        String content = FILE.readFile(file);
        return (List<ChapterInfo>) Widget.stringToObject(content, new TypeToken<List<ChapterInfo>>() {
        }.getType());
    }

    public static void saveBookChapterContent(Context context, int bookId, int chapterId, ChapterContent chapterContent) {
        File file = getFile(context, "books/" + bookId + "/" + chapterId + ".txt");
        FILE.saveFile(file, Widget.objectToString(chapterContent));
        LogUtil.e("bookUpdate@3  saveBookChapterContent:");
    }

    public static ChapterContent getBookChapterContent(Context context, int bookId, int chapterId) {
        File file = getFile(context, "books/" + bookId + "/" + chapterId + ".txt");
        String str = FILE.readFile(file);
        if (Widget.isBlank(str))
            return new ChapterContent();
        return (ChapterContent) Widget.stringToObject(str, ChapterContent.class);
    }

    public static void saveBookCover(Context context, int bookId, byte[] bytes) {
        File file = getFile(context, "books/" + bookId + "/cover.jpg");
        FILE.saveFile(file, bytes);
    }

    public static File getBookCover(Context context, int bookId) {
        return getFile(context, "books/" + bookId + "/cover.jpg");
    }

    public static void saveBookMark(Context context, int bookId, List<BookMarkItem> list) {
        File file = getFile(context, "books/" + bookId + "/mark.txt");
        FILE.saveFile(file, Widget.objectToString(list));
    }

    public static List<BookMarkItem> getBookMark(Context context, int bookId) {
        File file = getFile(context, "books/" + bookId + "/mark.txt");
        String content = FILE.readFile(file);
        return (List<BookMarkItem>) Widget.stringToObject(content, new TypeToken<List<BookMarkItem>>() {
        }.getType());
    }

    public static void saveAdImg(Context context, String url, byte[] bytes) {
        String name = Widget.encodeByMd5Bit32(url) + ".jpg";
        File file = getFile(context, "ad/" + name);
        FILE.saveFile(file, bytes);
    }

    public static Bitmap getAdImg(Context context, String url) {
        try {
            String name = Widget.encodeByMd5Bit32(url) + ".jpg";
            File file = getFile(context, "ad/" + name);
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isNeedDownloadChapter(Context context, int bookId, int chapterId) {
        File file = getFile(context, "books/" + bookId + "/" + chapterId + ".txt");
        if (file == null) return false;
        return !file.exists();
    }

    public static boolean isNeedDownloadChapterForDownload(Context context, int bookId, int chapterId) {
        File file = getFile(context, "books/" + bookId + "/" + chapterId + ".txt");
        if (file == null) return false;
        if (file.exists()) {
            try {
                ChapterContent chapterContent = getBookChapterContent(context, bookId, chapterId);
                if (Widget.isBlank(chapterContent.getNextChapterId()))
                    return true;
                if ("lastpage".equals(chapterContent.getNextChapterId())) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        } else {
            return true;
        }
    }

    public static File getFile(Context context, String fileName) {
        File file = context.getExternalFilesDir(fileName.substring(0, fileName.lastIndexOf("/")));
        if (file == null) {
            return null;
        }
        return new File(file.getPath() + "/" + fileName.substring(fileName.lastIndexOf("/") + 1));
    }

    public static String getFilePath(Context context, String fileName) {
        File file = context.getExternalFilesDir(fileName.substring(0, fileName.lastIndexOf("/")));
        if (file == null) {
            return null;
        }
        return file.getPath() + "/" + fileName.substring(fileName.lastIndexOf("/") + 1);
    }
}
