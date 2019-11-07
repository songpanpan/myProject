package com.yueyou.adreader.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogToFile {
    private static String TAG = "LogToFile";

    private static String logPath = null;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private static Date date = new Date();

    /**
     * 初始化，须在使用之前设置，最好在Application创建时调用
     *
     * @param context
     */
    public static void init(Context context) {
        logPath = getFilePath(context) + "/Logs";
        Utils.logNoTag("init logPath-> %s", logPath);
    }

    /**
     * 获得文件存储路径
     *
     * @return
     */
    private static String getFilePath(Context context) {
        return context.getExternalFilesDir(null).getPath();
    }

    private static final char VERBOSE = 'v';

    private static final char DEBUG = 'd';

    private static final char INFO = 'i';

    private static final char WARN = 'w';

    private static final char ERROR = 'e';

    public static void v(String tag, String msg) {
        writeToFile(VERBOSE, tag, msg);
    }

    public static void d(String tag, String msg) {
        writeToFile(DEBUG, tag, msg);
    }

    public static void i(String tag, String msg) {
        writeToFile(INFO, tag, msg);
    }

    public static void w(String tag, String msg) {
        writeToFile(WARN, tag, msg);
    }

    public static void e(String tag, String msg) {
        writeToFile(ERROR, tag, msg);
    }

    /**
     * 将log信息写入文件中
     *
     * @param type
     * @param tag
     * @param msg
     */
    private static void writeToFile(char type, String tag, String msg) {
        Log.d(tag, msg);
        if (!Const.DEBUG) return;

        if (null == logPath) {
            Log.e(TAG, "logPath == null ，未初始化LogToFile");
            return;
        }

        String fileName = logPath + "/log_" + dateFormat.format(new Date()) + ".logNoTag";
        String log = dateFormat.format(date) + " " + type + " " + tag + " " + getMsgFormat(msg) + "\n";

        //如果父路径不存在
        File file = new File(logPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        FileOutputStream fos;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(fileName, true);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(log);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 获取类名,方法名,行号
     *
     * @return Thread:main, at com.haier.ota.OTAApplication.onCreate(OTAApplication.java:35)
     */
    private static String getFunctionName() {
        try {
            StackTraceElement[] sts = Thread.currentThread().getStackTrace();
            if (sts != null) {
                for (StackTraceElement st : sts) {
                    if (st.isNativeMethod()) {
                        continue;
                    }
                    if (st.getClassName().equals(Thread.class.getName())) {
                        continue;
                    }
                    if (st.getClassName().equals(LogToFile.class.getName())) {
                        continue;
                    }
                    return "Thread:" + Thread.currentThread().getName() + ", at " + st.getClassName() + "." + st.getMethodName()
                            + "(" + st.getFileName() + ":" + st.getLineNumber() + ")";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getMsgFormat(String msg) {
        return msg + "----" + getFunctionName();
    }

}
