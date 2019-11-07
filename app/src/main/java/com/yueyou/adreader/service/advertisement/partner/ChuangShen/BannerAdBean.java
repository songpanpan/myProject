package com.yueyou.adreader.service.advertisement.partner.ChuangShen;

import java.util.List;

public class BannerAdBean {

    /**
     * succ : true
     * msg : ok
     * ads : {"creativeId":"1701","adsId":"1624","image":"http://images.cshudong.com/yd-xxx/1.jpg","width":"1200","height":"180","gotourl":"https://dl.7725.com/xxwdxyjx/hycs_dl/xxwdxyjx_hycs_dl_1.apk","indices":101,"words":"","showReport":[["http://stats1.toukeads.com/openapi/analysis2.action?g=a24bIR23HzXHGEOacMb%2BltL7w7naKLFdM7arg6eFk6EOQsJNxXEJx3L9a54SMzZcdnTbutLb3Ss9ZitVa%2BSXFWprdc6bzFGsNq7O20yNYyoQBQGPWsmxGKIp6nDx%2FswHKOsfAX6T3alPu9WNi6azPAS9ERiKR1GfyIl6LXjM%2FWY8J5AzxCkn%2FqeIrrGXt%2Fb6QssJ5o%2BhxyoJNzXLmyXs2jCgGh7MCCFIrx9Av2%2FTlV5l","http://stats1.toukeads.com/openapi/stats2.action?g=41d6lalK2oK%2FCtMbeUhavK9sYCDdXkwKcZOgRgcgqSA%2FZCe85A%2BTNxCT6gyoTe2nRjvAdrW4gz9NMn1zffufIxM8SeGtjqDIYVPadzCgpvQIXvSIl4pXQ5mcTcXpxGie%2F01fX063DFvGvtPDFajHWX%2BoXqPuqPv4ZofQWwa8rLcQDzcfJUlJpLo8J4fkpdf42ojW3x4C6SAnf3cCY3TtooSSV%2F3qX3gh3Q&vt=_TIMESTAMP_"]],"clickReport":["http://stats1.toukeads.com/openapi/analysis2.action?g=a976y4BPZF52uUf0T0CgZN7Vmp2Vkw8ZWo3uK4g2ODvE7BgEHqCmX3ZMJNMqjlhx1fxyibYUC4eIALdE6GDl2uriOBngSLY3UD%2FyHfmGRavt0E5bkh030AGPhiOpEkFLvKUuUlO6jNwEUP3CrqTS3hErmDl2n9GIbMigu631Kbk12HCQ%2F08u8VXtvbBg7jPvcJfqJ9MeNvmpHVSx6DfyKuj52%2BzvOkSEHomm1fK4oGuBXJk"]}
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
         * creativeId : 1701
         * adsId : 1624
         * image : http://images.cshudong.com/yd-xxx/1.jpg
         * width : 1200
         * height : 180
         * gotourl : https://dl.7725.com/xxwdxyjx/hycs_dl/xxwdxyjx_hycs_dl_1.apk
         * indices : 101
         * words :
         * showReport : [["http://stats1.toukeads.com/openapi/analysis2.action?g=a24bIR23HzXHGEOacMb%2BltL7w7naKLFdM7arg6eFk6EOQsJNxXEJx3L9a54SMzZcdnTbutLb3Ss9ZitVa%2BSXFWprdc6bzFGsNq7O20yNYyoQBQGPWsmxGKIp6nDx%2FswHKOsfAX6T3alPu9WNi6azPAS9ERiKR1GfyIl6LXjM%2FWY8J5AzxCkn%2FqeIrrGXt%2Fb6QssJ5o%2BhxyoJNzXLmyXs2jCgGh7MCCFIrx9Av2%2FTlV5l","http://stats1.toukeads.com/openapi/stats2.action?g=41d6lalK2oK%2FCtMbeUhavK9sYCDdXkwKcZOgRgcgqSA%2FZCe85A%2BTNxCT6gyoTe2nRjvAdrW4gz9NMn1zffufIxM8SeGtjqDIYVPadzCgpvQIXvSIl4pXQ5mcTcXpxGie%2F01fX063DFvGvtPDFajHWX%2BoXqPuqPv4ZofQWwa8rLcQDzcfJUlJpLo8J4fkpdf42ojW3x4C6SAnf3cCY3TtooSSV%2F3qX3gh3Q&vt=_TIMESTAMP_"]]
         * clickReport : ["http://stats1.toukeads.com/openapi/analysis2.action?g=a976y4BPZF52uUf0T0CgZN7Vmp2Vkw8ZWo3uK4g2ODvE7BgEHqCmX3ZMJNMqjlhx1fxyibYUC4eIALdE6GDl2uriOBngSLY3UD%2FyHfmGRavt0E5bkh030AGPhiOpEkFLvKUuUlO6jNwEUP3CrqTS3hErmDl2n9GIbMigu631Kbk12HCQ%2F08u8VXtvbBg7jPvcJfqJ9MeNvmpHVSx6DfyKuj52%2BzvOkSEHomm1fK4oGuBXJk"]
         */

        private int creativeId;
        private String adsId;
        private String image;
        private String width;
        private String height;
        private String gotourl;
        private int indices;
        private String words;
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
