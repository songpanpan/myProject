package com.yueyou.adreader.util;

public class YYStringUtils {
    public static boolean isBlank(String var0) {
        return var0 == null || var0.trim().length() == 0;
    }
    public static boolean isEmpty(CharSequence var0) {
        return var0 == null || var0.length() == 0;
    }
}
