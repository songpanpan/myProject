package com.yueyou.adreader.service.model;

/**
 * Created by zy on 2017/5/3.
 * 返回码
 */

public class ResponseCode {
    public static final int SUCCESS = 0x0;
    public static final int FAILED = 0x01;
    public static final int TOKEN_ERROR = 0x02;
    public static final int NEW_VERSION = 0x03;
    public static final int REMAIN_LESS= 0x04; //余额不足
    public static final int CONTENT_UNPAY= 0x05; //章节未购买
    public static final int SAVE_USER_INFO = 0x06;
}
