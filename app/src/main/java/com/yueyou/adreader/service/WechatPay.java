package com.yueyou.adreader.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yueyou.adreader.service.bean.WechatPayBean;

import org.json.JSONObject;

public class WechatPay {
    public static void pay(Context context, String payData, boolean fromBuy, boolean autoBuy, RechargeAndBuyListener rechargeListener) {

        try {
            WechatPayBean wechatPayBean = new Gson().fromJson(payData, WechatPayBean.class);
            IWXAPI api = WXAPIFactory.createWXAPI(context, wechatPayBean.getAppid());
            PayReq req = new PayReq();
            req.appId = wechatPayBean.getAppid();
            req.partnerId = wechatPayBean.getPartnerid();
            req.prepayId = wechatPayBean.getPrepayid();
            req.nonceStr = wechatPayBean.getNoncestr();
            req.timeStamp = wechatPayBean.getTimestamp();
            req.packageValue = wechatPayBean.getPackageX();
            req.sign = wechatPayBean.getSign();
            String extData = "";
            if (!fromBuy && !autoBuy) {
                extData = "0";
            } else if (fromBuy && !autoBuy) {
                extData = "1";
            } else if (fromBuy && autoBuy) {
                extData = "2";
            }
            req.extData = extData; // optional
//            Toast.makeText(context, "正常调起支付", Toast.LENGTH_SHORT).show();
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            api.sendReq(req);

        } catch (Exception e) {
            Log.e("PAY_GET", "异常：" + e.getMessage());
            Toast.makeText(context, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
