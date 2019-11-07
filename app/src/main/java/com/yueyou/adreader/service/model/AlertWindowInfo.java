package com.yueyou.adreader.service.model;

public class AlertWindowInfo {
    private String url;// 页面访问地址,
    private String width;// 窗体的宽度, "auto" 适应最大宽度
    private String height;//窗体高度,  "auto" 适应最大高度
    private String align;// 窗体的位置, "center", "left", "right", "bottom", "top"
    private int type;//类型: 1、一次性，2、时效性，只有时效性广告才会有 start_time 和 end_time， 3、永久
    private String start_time;//开始时间,
    private String end_time;//结束时间,
    private int interval;//显示时长单位（秒), 0 标识永久
    private boolean transparent;//是否透明显示
    private boolean topbar;//是否显示顶栏
    private String title;//标题
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public boolean isTopbar() {
        return topbar;
    }

    public void setTopbar(boolean topbar) {
        this.topbar = topbar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
