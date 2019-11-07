package com.yueyou.adreader.service.model;

public class AdContent {
    public static final String[] CP = {"luomi", "guangdiantong", "baidu", "toutiao"};//洛米，广点通,百度，头条
    private int siteId;//1:开屏，2：书架，3：阅读页（下一章），4:退出, 5:阅读页banner
    private String cp;//广告商
    private String style;//样式
    private int type;//广告类型 1:自渲染 2:模版,
    private int time;//展示时长，秒为单位。0为永久
    private int canClosed;//是否可关闭 0不，1关闭
    private String appKey;//开发者后台分配的appid
    private String placeId;//开发者后台分配的广告位id
    private int width;
    private int height;
    private boolean nativeErrorFlag;//本地透传是否为容错标志，注意不能与服务端协议字段重名；
    private int nativeErrorCount;//失败次数 注意不能与服务端协议字段重名；
    private long nativeErrorTime;//广告多次获取失败后记录时间

    public int getSiteId() {
        return siteId;
    }


    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCanClosed() {
        return canClosed;
    }

    public void setCanClosed(int canClosed) {
        this.canClosed = canClosed;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isNativeErrorFlag() {
        return nativeErrorFlag;
    }

    public void setNativeErrorFlag(boolean nativeErrorFlag) {
        this.nativeErrorFlag = nativeErrorFlag;
    }

    public int getNativeErrorCount() {
        return nativeErrorCount;
    }

    public void setNativeErrorCount(int nativeErrorCount) {
        this.nativeErrorCount = nativeErrorCount;
    }

    public long getNativeErrorTime() {
        return nativeErrorTime;
    }

    public void setNativeErrorTime(long nativeErrorTime) {
        this.nativeErrorTime = nativeErrorTime;
    }

    public AdContent copy() {
        AdContent adContent = new AdContent();
        adContent.setPlaceId(this.getPlaceId());
        adContent.setAppKey(this.getAppKey());
        adContent.setCp(this.getCp());
        adContent.setTime(this.getTime());
        adContent.setSiteId(this.getSiteId());
        adContent.setCanClosed(this.getCanClosed());
        adContent.setStyle(this.getStyle());
        adContent.setType(this.getType());
        adContent.setHeight(this.getHeight());
        adContent.setWidth(this.getWidth());
        return adContent;
    }

    public boolean equals(AdContent adContent) {
        try {
            return adContent.getSiteId() == getSiteId() && adContent.getCp().equals(getCp())
                    && adContent.getAppKey().equals(getAppKey()) && adContent.getPlaceId().equals(getPlaceId());
        } catch (Exception e) {
            return false;
        }
    }
}
