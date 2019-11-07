package com.yueyou.adreader.service.analytics.model;

import com.yueyou.adreader.service.analytics.model.base.Base;

public class AddBuildinBookFinish extends Base {
    private boolean result;
    private String msg;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
