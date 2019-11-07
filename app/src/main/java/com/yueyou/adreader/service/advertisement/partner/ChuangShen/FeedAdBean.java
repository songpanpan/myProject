package com.yueyou.adreader.service.advertisement.partner.ChuangShen;

import java.util.List;

public class FeedAdBean {

    /**
     * succ : true
     * msg : ok
     * ads : {"creativeId":"1700","adsId":"1623","image":"http://images.cshudong.com/yd-xxlxx/3.jpg","width":"1200","height":"800","gotourl":"https://dl.7725.com/xxwdxyjx/hycs_dl/xxwdxyjx_hycs_dl_1.apk","indices":101,"words":"最火热的仙侠游戏 小姐姐在这里等你","words2":"","icon":"","image2":"","showReport":[["http://stats1.toukeads.com/openapi/analysis2.action?g=14e6rsBp%2FARuOXXEMLXroaKjqllYWLwFYq0EyalEAsEkCtm4gdk20j4wUOQrcbY4Q16XLPN8y2x0f0b6iVj7kdyj1mw7oH7t%2FFbwDuKHY8i%2FmzO7HS%2BFvH65yiqVpyyNA2fjexbkOnQ%2FhJezqUiLuV2kSqVC6ygQTUk%2FJTQbitfahSCcSlatIEImAzZ23b0ZD68Ja6qUGmtsxKeZrubBMfSavwBE16yA8zwAllkAG9kW","http://stats1.toukeads.com/openapi/stats2.action?g=e249U0wfTQ54QVs5i6gS2Fc8isB2DJ3GfDyLdg%2FbUXwcBVUo5k5n0ANieeVr5G%2FqhamtdHkicxdMEF%2BVYeS5Auo7AxK2XF23YMVwp1w4%2F75KZx2ZQ5H48SW9TBm23Ko1uGXy5P5wr5XF2sHbJna3uno4MAKM%2Bx1Dpy1iwhcMplXA50Zi2DnGRWevyi9Rsr6HPBAsTVg9n8uRIJhmY5B2sD96%2FL8dHwP6GQ&vt=_TIMESTAMP_"]],"clickReport":["http://stats1.toukeads.com/openapi/analysis2.action?g=392aC%2BcboA6MRcZnV4OwWVdl6JVmUErxA19VtlrJjwKk2bteBecBRwpoJvu5VgwITAf0jyLfdFnrrSvlDVtBeh9Xw4dXfcAOGG2qYr%2B0NIXxmv7JiiOKGUBAEwIqJJw9ery5hJfLF%2BBHbq%2FB9nyq%2BD%2F3RRjjxFOpmce3GAt1JYV%2FIqOilGKa8y77PnVuWWpK5gmgy%2BUEfZ0IDZs%2FwsseBxokQlBrq6bmHRC8G6mVobOcz8k"]}
     */

    private boolean succ;
    private String msg;
    private AdsBean ads;

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AdsBean getAds() {
        return ads;
    }

    public void setAds(AdsBean ads) {
        this.ads = ads;
    }

    public static class AdsBean {
        /**
         * creativeId : 1700
         * adsId : 1623
         * image : http://images.cshudong.com/yd-xxlxx/3.jpg
         * width : 1200
         * height : 800
         * gotourl : https://dl.7725.com/xxwdxyjx/hycs_dl/xxwdxyjx_hycs_dl_1.apk
         * indices : 101
         * words : 最火热的仙侠游戏 小姐姐在这里等你
         * words2 :
         * icon :
         * image2 :
         * showReport : [["http://stats1.toukeads.com/openapi/analysis2.action?g=14e6rsBp%2FARuOXXEMLXroaKjqllYWLwFYq0EyalEAsEkCtm4gdk20j4wUOQrcbY4Q16XLPN8y2x0f0b6iVj7kdyj1mw7oH7t%2FFbwDuKHY8i%2FmzO7HS%2BFvH65yiqVpyyNA2fjexbkOnQ%2FhJezqUiLuV2kSqVC6ygQTUk%2FJTQbitfahSCcSlatIEImAzZ23b0ZD68Ja6qUGmtsxKeZrubBMfSavwBE16yA8zwAllkAG9kW","http://stats1.toukeads.com/openapi/stats2.action?g=e249U0wfTQ54QVs5i6gS2Fc8isB2DJ3GfDyLdg%2FbUXwcBVUo5k5n0ANieeVr5G%2FqhamtdHkicxdMEF%2BVYeS5Auo7AxK2XF23YMVwp1w4%2F75KZx2ZQ5H48SW9TBm23Ko1uGXy5P5wr5XF2sHbJna3uno4MAKM%2Bx1Dpy1iwhcMplXA50Zi2DnGRWevyi9Rsr6HPBAsTVg9n8uRIJhmY5B2sD96%2FL8dHwP6GQ&vt=_TIMESTAMP_"]]
         * clickReport : ["http://stats1.toukeads.com/openapi/analysis2.action?g=392aC%2BcboA6MRcZnV4OwWVdl6JVmUErxA19VtlrJjwKk2bteBecBRwpoJvu5VgwITAf0jyLfdFnrrSvlDVtBeh9Xw4dXfcAOGG2qYr%2B0NIXxmv7JiiOKGUBAEwIqJJw9ery5hJfLF%2BBHbq%2FB9nyq%2BD%2F3RRjjxFOpmce3GAt1JYV%2FIqOilGKa8y77PnVuWWpK5gmgy%2BUEfZ0IDZs%2FwsseBxokQlBrq6bmHRC8G6mVobOcz8k"]
         */

        private int creativeId;
        private String adsId;
        private String image;
        private String width;
        private String height;
        private String gotourl;
        private int indices;
        private String words;
        private String words2;
        private String icon;
        private String image2;
        private List<List<String>> showReport;
        private List<String> clickReport;

        public int getCreativeId() {
            return creativeId;
        }

        public void setCreativeId(int creativeId) {
            this.creativeId = creativeId;
        }

        public String getAdsId() {
            return adsId;
        }

        public void setAdsId(String adsId) {
            this.adsId = adsId;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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

        public String getGotourl() {
            return gotourl;
        }

        public void setGotourl(String gotourl) {
            this.gotourl = gotourl;
        }

        public int getIndices() {
            return indices;
        }

        public void setIndices(int indices) {
            this.indices = indices;
        }

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }

        public String getWords2() {
            return words2;
        }

        public void setWords2(String words2) {
            this.words2 = words2;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getImage2() {
            return image2;
        }

        public void setImage2(String image2) {
            this.image2 = image2;
        }

        public List<List<String>> getShowReport() {
            return showReport;
        }

        public void setShowReport(List<List<String>> showReport) {
            this.showReport = showReport;
        }

        public List<String> getClickReport() {
            return clickReport;
        }

        public void setClickReport(List<String> clickReport) {
            this.clickReport = clickReport;
        }
    }
}
