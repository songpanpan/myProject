package com.yueyou.adreader.service.advertisement.partner.HanBo;

import java.util.List;

public class FeedAdBean {

    /**
     * Status : 1001
     * Info :
     * Data : [{"Title":"下载送本金，赢一局赚5W，敢挑战吗？","AdsName":"真人提现棋牌","AdsUrl":"https://appapisdk.gotoline.cn/1_0_1/J2.php?url=https%3A%2F%2Flm.vdfe02.com%2F402%2F&e=fDJ8fDk%3D&c=1097%3C%3E129%3C%3E716%3C%3E19%3C%3E1036%3C%3E1_0_1%3C%3E%3C%3E0%3C%3E867350044774645%3C%3Ehttp%3A%2F%2Fimg1.mlion.cn%2Fmaterial%2F7572%2F1%2F7572_20.jpeg%3C%3E20%3C%3E%E4%B8%8B%E8%BD%BD%E9%80%81%E6%9C%AC%E9%87%91%EF%BC%8C%E8%B5%A2%E4%B8%80%E5%B1%80%E8%B5%9A5W%EF%BC%8C%E6%95%A2%E6%8C%91%E6%88%98%E5%90%97%EF%BC%9F%3C%3E%3C%3E&z=000950698823093f7be9e91819050ff3&zs=65fcc3f377f1c278bce99fdd0e80c1c3","AdsImgUrl":"http://img1.mlion.cn/material/7572/1/7572_20.jpeg","PvStatisticsUrl":"https://appapisdk.gotoline.cn/1_0_1/PvStatistics.php?isget=1&Pvkey=1097%3C%3E129%3C%3E716%3C%3E19%3C%3E1036%3C%3E1_0_1%3C%3E%3C%3E0%3C%3E867350044774645%3C%3Ehttp%3A%2F%2Fimg1.mlion.cn%2Fmaterial%2F7572%2F1%2F7572_20.jpeg%3C%3E20%3C%3E%E4%B8%8B%E8%BD%BD%E9%80%81%E6%9C%AC%E9%87%91%EF%BC%8C%E8%B5%A2%E4%B8%80%E5%B1%80%E8%B5%9A5W%EF%BC%8C%E6%95%A2%E6%8C%91%E6%88%98%E5%90%97%EF%BC%9F%3C%3E%3C%3E&t=1567750969&e=b5fbe5e32bd4fca0dca60fa81ba793c8%7C%7C2%7C%7C9&s=8786301ba55523cd43df9faa87983532&z=000950698823093f7be9e91819050ff3&zs=65fcc3f377f1c278bce99fdd0e80c1c3","ClickStatisticsUrl":"https://appapisdk.gotoline.cn/1_0_1/ClickStatistics.php?isget=1&Clickkey=1097%3C%3E129%3C%3E716%3C%3E19%3C%3E1036%3C%3E1_0_1%3C%3E%3C%3E0%3C%3E867350044774645%3C%3Ehttp%3A%2F%2Fimg1.mlion.cn%2Fmaterial%2F7572%2F1%2F7572_20.jpeg%3C%3E20%3C%3E%E4%B8%8B%E8%BD%BD%E9%80%81%E6%9C%AC%E9%87%91%EF%BC%8C%E8%B5%A2%E4%B8%80%E5%B1%80%E8%B5%9A5W%EF%BC%8C%E6%95%A2%E6%8C%91%E6%88%98%E5%90%97%EF%BC%9F%3C%3E%3C%3E&t=1567750969&e=b5fbe5e32bd4fca0dca60fa81ba793c8%7C%7C2%7C%7C9&s=5e46ad10562decc188036485c085985f&z=000950698823093f7be9e91819050ff3&zs=65fcc3f377f1c278bce99fdd0e80c1c3&purl=https%3A%2F%2Flm.vdfe02.com%2F402%2F","BackStatisticsUrl":"https://appapisdk.gotoline.cn/1_0_1/BackStatistics.php?isget=1&Backkey=1097%3C%3E129%3C%3E716%3C%3E19%3C%3E1036%3C%3E1_0_1%3C%3E%3C%3E0%3C%3E867350044774645%3C%3Ehttp%3A%2F%2Fimg1.mlion.cn%2Fmaterial%2F7572%2F1%2F7572_20.jpeg%3C%3E20%3C%3E%E4%B8%8B%E8%BD%BD%E9%80%81%E6%9C%AC%E9%87%91%EF%BC%8C%E8%B5%A2%E4%B8%80%E5%B1%80%E8%B5%9A5W%EF%BC%8C%E6%95%A2%E6%8C%91%E6%88%98%E5%90%97%EF%BC%9F%3C%3E%3C%3E&t=1567750969&e=b5fbe5e32bd4fca0dca60fa81ba793c8%7C%7C2%7C%7C9&s=5e46ad10562decc188036485c085985f&z=000950698823093f7be9e91819050ff3&zs=65fcc3f377f1c278bce99fdd0e80c1c3","Width":298,"Height":395}]
     */

    private int Status;
    private String Info;
    private List<DataBean> Data;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String Info) {
        this.Info = Info;
    }

    public List<DataBean> getData() {
        return Data;
    }

    public void setData(List<DataBean> Data) {
        this.Data = Data;
    }

    public static class DataBean {
        /**
         * Title : 下载送本金，赢一局赚5W，敢挑战吗？
         * AdsName : 真人提现棋牌
         * AdsUrl : https://appapisdk.gotoline.cn/1_0_1/J2.php?url=https%3A%2F%2Flm.vdfe02.com%2F402%2F&e=fDJ8fDk%3D&c=1097%3C%3E129%3C%3E716%3C%3E19%3C%3E1036%3C%3E1_0_1%3C%3E%3C%3E0%3C%3E867350044774645%3C%3Ehttp%3A%2F%2Fimg1.mlion.cn%2Fmaterial%2F7572%2F1%2F7572_20.jpeg%3C%3E20%3C%3E%E4%B8%8B%E8%BD%BD%E9%80%81%E6%9C%AC%E9%87%91%EF%BC%8C%E8%B5%A2%E4%B8%80%E5%B1%80%E8%B5%9A5W%EF%BC%8C%E6%95%A2%E6%8C%91%E6%88%98%E5%90%97%EF%BC%9F%3C%3E%3C%3E&z=000950698823093f7be9e91819050ff3&zs=65fcc3f377f1c278bce99fdd0e80c1c3
         * AdsImgUrl : http://img1.mlion.cn/material/7572/1/7572_20.jpeg
         * PvStatisticsUrl : https://appapisdk.gotoline.cn/1_0_1/PvStatistics.php?isget=1&Pvkey=1097%3C%3E129%3C%3E716%3C%3E19%3C%3E1036%3C%3E1_0_1%3C%3E%3C%3E0%3C%3E867350044774645%3C%3Ehttp%3A%2F%2Fimg1.mlion.cn%2Fmaterial%2F7572%2F1%2F7572_20.jpeg%3C%3E20%3C%3E%E4%B8%8B%E8%BD%BD%E9%80%81%E6%9C%AC%E9%87%91%EF%BC%8C%E8%B5%A2%E4%B8%80%E5%B1%80%E8%B5%9A5W%EF%BC%8C%E6%95%A2%E6%8C%91%E6%88%98%E5%90%97%EF%BC%9F%3C%3E%3C%3E&t=1567750969&e=b5fbe5e32bd4fca0dca60fa81ba793c8%7C%7C2%7C%7C9&s=8786301ba55523cd43df9faa87983532&z=000950698823093f7be9e91819050ff3&zs=65fcc3f377f1c278bce99fdd0e80c1c3
         * ClickStatisticsUrl : https://appapisdk.gotoline.cn/1_0_1/ClickStatistics.php?isget=1&Clickkey=1097%3C%3E129%3C%3E716%3C%3E19%3C%3E1036%3C%3E1_0_1%3C%3E%3C%3E0%3C%3E867350044774645%3C%3Ehttp%3A%2F%2Fimg1.mlion.cn%2Fmaterial%2F7572%2F1%2F7572_20.jpeg%3C%3E20%3C%3E%E4%B8%8B%E8%BD%BD%E9%80%81%E6%9C%AC%E9%87%91%EF%BC%8C%E8%B5%A2%E4%B8%80%E5%B1%80%E8%B5%9A5W%EF%BC%8C%E6%95%A2%E6%8C%91%E6%88%98%E5%90%97%EF%BC%9F%3C%3E%3C%3E&t=1567750969&e=b5fbe5e32bd4fca0dca60fa81ba793c8%7C%7C2%7C%7C9&s=5e46ad10562decc188036485c085985f&z=000950698823093f7be9e91819050ff3&zs=65fcc3f377f1c278bce99fdd0e80c1c3&purl=https%3A%2F%2Flm.vdfe02.com%2F402%2F
         * BackStatisticsUrl : https://appapisdk.gotoline.cn/1_0_1/BackStatistics.php?isget=1&Backkey=1097%3C%3E129%3C%3E716%3C%3E19%3C%3E1036%3C%3E1_0_1%3C%3E%3C%3E0%3C%3E867350044774645%3C%3Ehttp%3A%2F%2Fimg1.mlion.cn%2Fmaterial%2F7572%2F1%2F7572_20.jpeg%3C%3E20%3C%3E%E4%B8%8B%E8%BD%BD%E9%80%81%E6%9C%AC%E9%87%91%EF%BC%8C%E8%B5%A2%E4%B8%80%E5%B1%80%E8%B5%9A5W%EF%BC%8C%E6%95%A2%E6%8C%91%E6%88%98%E5%90%97%EF%BC%9F%3C%3E%3C%3E&t=1567750969&e=b5fbe5e32bd4fca0dca60fa81ba793c8%7C%7C2%7C%7C9&s=5e46ad10562decc188036485c085985f&z=000950698823093f7be9e91819050ff3&zs=65fcc3f377f1c278bce99fdd0e80c1c3
         * Width : 298
         * Height : 395
         */

        private String Title;
        private String AdsName;
        private String AdsUrl;
        private String AdsImgUrl;
        private String PvStatisticsUrl;
        private String ClickStatisticsUrl;

        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public String getAdsName() {
            return AdsName;
        }

        public void setAdsName(String AdsName) {
            this.AdsName = AdsName;
        }

        public String getAdsUrl() {
            return AdsUrl;
        }

        public void setAdsUrl(String AdsUrl) {
            this.AdsUrl = AdsUrl;
        }

        public String getAdsImgUrl() {
            return AdsImgUrl;
        }

        public void setAdsImgUrl(String AdsImgUrl) {
            this.AdsImgUrl = AdsImgUrl;
        }

        public String getPvStatisticsUrl() {
            return PvStatisticsUrl;
        }

        public void setPvStatisticsUrl(String PvStatisticsUrl) {
            this.PvStatisticsUrl = PvStatisticsUrl;
        }

        public String getClickStatisticsUrl() {
            return ClickStatisticsUrl;
        }

        public void setClickStatisticsUrl(String ClickStatisticsUrl) {
            this.ClickStatisticsUrl = ClickStatisticsUrl;
        }

    }
}
