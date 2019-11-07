package com.yueyou.adreader.service.model;

/**
 * Created by zy on 2017/3/29.
 */

public class Response {
    private int code;
    private String msg;
    private Object data;

    public Response(){
        code = ResponseCode.FAILED;
    }

    public Response(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
