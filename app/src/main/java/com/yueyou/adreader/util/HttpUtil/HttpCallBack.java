package com.yueyou.adreader.util.HttpUtil;

interface HttpCallBack<T> {
    void onFailure(String error);

    void onSuccess(T json);
}
