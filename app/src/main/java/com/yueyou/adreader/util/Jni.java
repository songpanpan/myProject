package com.yueyou.adreader.util;

/**
 * Created by zy on 2017/3/24.
 */

public class Jni {
    public native static String requestJni(String data);
    public native static String responseJni(String data);
    static {
        System.loadLibrary("zyDaiLian-lib");
    }

    public static String request(String data){
        return requestJni(data);
    }

    public static String response(String data){
        return responseJni(data);
    }
}
