package com.yueyou.adreader.service.model;

import java.util.ArrayList;
import java.util.List;


/**
 * 广告列表实体
 */
public class AdContentList {
    private List<AdContent> defaultAdContentList;
    private List<AdContent> adContentList;
    private List<AdContent> adActiveContentList;
    private int pos = 0;//本地轮询索引(注客户端本地字段)
    private int bookId = 0; // 当前书籍id
    private int chapterId = 0; //当前章节id
    private boolean status = false; //状态(注客户端本地字段)
    private int mode = 1; //": 展示模式  1: 轮播 2: 优先级
    private int failThreshold = 2; //: 优先级模式时的，错误次数阈值
    private int failExpires = 600;//广告获取失败后，重置错误次数的时间
    private int retryCount = 3;//优先模式时，广告尝试次数，次数内失败则跳转容错
    private int enableMatNotify; // 是否开启广告素材上报  0 不 1上报                            // 3.0.1新增
    private int displayFlag;// 显示控制, 第0位：免费章节显示广告 第1位:收费章节显示广告   // 3.0.1新增
    private int way;//展示方式  1: 全屏显示 2: 正文嵌入                              // 3.0.2新增
    private String lock = "list_lock";

    public AdContentList() {
        defaultAdContentList = new ArrayList<>();
        adContentList = new ArrayList<>();
        adActiveContentList = new ArrayList<>();
    }

    public void reset() {
        synchronized (lock) {
            try {
                defaultAdContentList.clear();
                adContentList.clear();
                pos = 0;
                this.bookId = 0;
                this.chapterId = 0;
                status = false;
            } catch (Exception e) {

            }
        }
    }

    public void loadError(AdContent adContent) {
        if (mode != 2)
            return;
        try {
            for (AdContent item : adContentList) {
                if (item.equals(adContent)) {
                    item.setNativeErrorCount(item.getNativeErrorCount() + 1);
                    if (item.getNativeErrorCount() >= failThreshold) {
                        item.setNativeErrorTime(System.currentTimeMillis());
//                        adContentList.remove(item);
                        return;
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void loadSuccess(AdContent adContent) {
        if (mode != 2)
            return;
        try {
            for (AdContent item : adContentList) {
                if (item.equals(adContent)) {
                    item.setNativeErrorCount(0);
                }
            }
        } catch (Exception e) {

        }
    }

    public void set(int bookId, int chapterId, boolean status) {
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.status = status;
    }

    public List<AdContent> getDefaultAdContentList() {
        return defaultAdContentList;
    }

    public void setDefaultAdContentList(List<AdContent> defaultAdContentList) {
        if (defaultAdContentList == null)
            return;
        this.defaultAdContentList = defaultAdContentList;
    }

    public List<AdContent> getAdContentList() {
        return adContentList;
    }

    public void setAdContentList(List<AdContent> adContentList) {
        if (adContentList == null)
            return;
        this.adContentList = adContentList;
    }

    public AdContent getAdContent(AdContent adContentPre) {
        synchronized (lock) {
            try {
                if (mode == 2) {
                    return getAdContentByPrior(adContentPre);
                } else {
                    return getAdContentByCarousel(adContentPre);
                }
            } catch (Exception e) {
                return null;
            }
        }
    }

    //老方法
//    public AdContent getAdContentByPrior(AdContent adContentPre) {
//        if (adContentPre == null)
//            pos = 0;
//        if (adContentList != null && !adContentList.isEmpty()
//                && pos < 2 && pos < adContentList.size()){
//            AdContent adContent = adContentList.get(pos);
//            pos++;
//            return adContent.copy();
//        }
//        if (defaultAdContentList == null || defaultAdContentList.size() == 0)
//            return null;
//        if (adContentPre != null && adContentPre.equals(defaultAdContentList.get(0)))
//            return null;
//        return defaultAdContentList.get(0).copy();
//    }
    public AdContent getAdContentByPrior(AdContent adContentPre) {
        if (adContentPre == null) {
            pos = 0;
            adActiveContentList = getActiveAdContentList();
        }
        if (adActiveContentList != null && !adActiveContentList.isEmpty()
                && pos < retryCount && pos < adActiveContentList.size()) {
            AdContent adContent = adActiveContentList.get(pos);
            pos++;
            return adContent.copy();
        }
        if (defaultAdContentList == null || defaultAdContentList.size() == 0)
            return null;
        if (adContentPre != null && adContentPre.equals(defaultAdContentList.get(0)))
            return null;
        return defaultAdContentList.get(0).copy();
    }

    /**
     * 获取当前可以展示的广告列表,
     *
     * @return
     */
    public List<AdContent> getActiveAdContentList() {
        try {
            List<AdContent> activeAdList = new ArrayList<>();
            if (adContentList != null && adContentList.size() > 0) {
                long currentTime = System.currentTimeMillis();
                for (AdContent adContent : adContentList) {
                    if (adContent.getNativeErrorCount() >= failThreshold) {
                        if (currentTime - adContent.getNativeErrorTime() > 1000 * failExpires) {
                            adContent.setNativeErrorCount(0);
                            activeAdList.add(adContent);
                        }
                    } else {
                        activeAdList.add(adContent);
                    }
                }
                return activeAdList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AdContent getAdContentByCarousel(AdContent adContentPre) {
        if (adContentPre != null && adContentPre.isNativeErrorFlag()) {
            return null;
        }
        if (adContentPre != null || adContentList.size() == 0) {
            List<AdContent> defaultAdContentList = this.getDefaultAdContentList();
            if (defaultAdContentList != null && !defaultAdContentList.isEmpty()) {
                return defaultAdContentList.get(0).copy();
            }
        }
        if (adContentList != null && !adContentList.isEmpty()) {
            if (pos >= adContentList.size()) {
                pos = 0;
            }
            AdContent adContent = adContentList.get(pos);
            pos++;
            return adContent.copy();
        }
        return null;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getFailThreshold() {
        return failThreshold;
    }

    public void setFailThreshold(int failThreshold) {

        if (failThreshold > 0)
            this.failThreshold = failThreshold;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        if (retryCount > 0)
            this.retryCount = retryCount;
    }

    public int getFailExpires() {
        return failExpires;
    }

    public void setFailExpires(int failExpires) {
        if (failExpires > 0)
            this.failExpires = failExpires;
    }

    public int getEnableMatNotify() {
        return enableMatNotify;
    }

    public void setEnableMatNotify(int enableMatNotify) {
        this.enableMatNotify = enableMatNotify;
    }

    public int getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(int displayFlag) {
        this.displayFlag = displayFlag;
    }

    public boolean showAd(boolean isVipChapter) {
        if ((displayFlag & 0x1) != 0 && !isVipChapter)
            return true;
        if ((displayFlag & 0x2) != 0 && isVipChapter)
            return true;
        return false;
    }

    public int getWay() {
        return way;
    }

    public void setWay(int way) {
        this.way = way;
    }
}
