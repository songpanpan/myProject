package com.yueyou.adreader.service.analytics.model;

import com.yueyou.adreader.service.analytics.model.base.Base;

public class Advertisement extends Base {
    private int siteId;
    private boolean clicked;
    private String cp;
    private boolean displayEnd;
    private int status;//0：成功 1：失败
    private int action;//1:拉取结果 0：展示结果（含点击和曝光）
    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public boolean isDisplayEnd() {
        return displayEnd;
    }

    public void setDisplayEnd(boolean displayEnd) {
        this.displayEnd = displayEnd;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
