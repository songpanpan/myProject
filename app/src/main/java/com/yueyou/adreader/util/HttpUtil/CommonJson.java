package com.yueyou.adreader.util.HttpUtil;

import java.io.Serializable;

public class CommonJson implements Serializable {

    private int status;//0为成功
    private String des;

    /**
     * 是否正常相应
     * @return  true or false
     */
    public boolean isOk() {
        return 0 == this.status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }


    @Override
    public String toString() {
        return "CommonJson{" +
                "status=" + status +
                ", des='" + des + '\'' +
                '}';
    }
}
