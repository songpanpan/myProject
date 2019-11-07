package com.yueyou.adreader.service.model;

/**
 * Created by zy on 2018/5/23.
 */

public class UserInfoWithRedirectUrl {
    private String userId;
    private String token;
    private String url;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
