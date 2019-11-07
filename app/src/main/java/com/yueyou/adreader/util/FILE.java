package com.yueyou.adreader.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.yueyou.adreader.service.db.BookFileEngine;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zy on 2017/4/19.
 */

public class FILE {
    /**
     * 复制文件
     *
     * @param fromPathName
     * @param toPathName
     * @return
     */
    public static int copy(String fromPathName, String toPathName) {
        try {
            InputStream from = new FileInputStream(fromPathName);
            return copy(from, toPathName);
        } catch (FileNotFoundException e) {
            return -1;
        }
    }

    /**
     * 复制文件
     *
     * @param from
     * @param toPathName
     * @return
     */
    public static int copy(InputStream from, String toPathName) {
        try {
            FILE.delete(toPathName);
            OutputStream to = new BufferedOutputStream(new FileOutputStream(toPathName));
            byte buf[] = new byte[1024];
            int c;
            while ((c = from.read(buf)) > 0) {
                to.write(buf, 0, c);
            }
            from.close();
            to.close();
            return 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    /**
     * 删除文件
     */
    public static void delete(String filePathName) {
        try{
            if (TextUtils.isEmpty(filePathName)) return;
            File file = new File(filePathName);
            if (file.isFile() && file.exists()) {
                boolean flag = file.delete();
            }
        }catch (Exception e){

        }

    }

    public static void delete(File file) {
        try {
            if (file == null) return;
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    f.delete();
                }
                file.delete();//如要保留文件夹，只删除文件，请注释这行
            } else if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {

        }
    }

    public static void createDirWithFile(String path) {
        File file = new File(path);
        if (!path.endsWith("/")) {
            file = file.getParentFile();
        }
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean fileIsExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public static File saveFile(File file, byte[] b) {
        BufferedOutputStream stream = null;
        try {
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    public static boolean saveFile(File file, String content) {
        BufferedOutputStream stream = null;
        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(content.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fstream != null) {
                try {
                    fstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static String readFile(File file) {
        BufferedInputStream stream = null;
        ByteArrayOutputStream outputStream = null;
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(file);
            stream = new BufferedInputStream(fstream);
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[10240];
            for (; ; ) {
                int length = stream.read(buffer);
                if (length <= 0)
                    break;
                outputStream.write(buffer, 0, length);
//                content += new String(buffer, 0, length);//会导致乱码bug（读取了一版字符）
            }
            outputStream.flush();
            String content = outputStream.toString();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fstream != null) {
                try {
                    fstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Bitmap getBitmap(String imgPath) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            return bitmap;
        } catch (Exception e) {

        }
        return null;
    }

    public static Bitmap getBookCover(Context context, int bookId) {
        File file = BookFileEngine.getFile(context, "books/" + bookId + "/cover.jpg");
        String imgPath = file.getAbsolutePath();
        return getBitmap(imgPath);
    }
}
