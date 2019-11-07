package com.yueyou.adreader.service.bean;

import com.google.gson.annotations.SerializedName;

public class WechatPayBean {

    /**
     * package : Sign=WXPay
     * timestamp : 1569741765
     * sign : 6E449013C78AD56B021FA11CE9BDEF1B
     * partnerid : 1556183561
     * appid : wx00d330957ffc8345
     * prepayid : wx29152245254374af6992180d1944229500
     * noncestr : 1c48d7b35764ffc037bc53a9cff7033e
     */

    @SerializedName("package")
    private String packageX;
    private String timestamp;
    private String sign;
    private String partnerid;
    private String appid;
    private String prepayid;
    private String noncestr;

    public String getPackageX() {
        return packageX;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }
}
