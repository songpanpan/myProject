package com.yueyou.adreader.service;

import android.content.Context;



import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by zy on 2017/5/5.
 */

public class JuHePay {
    public static void init(Context context) {
//        IpaynowPlugin.getInstance().init(context);
    }

    public static void pay(Context context, String payData, boolean fromBuy, boolean autoBuy, RechargeAndBuyListener rechargeListener) {
        try {
            if (payData != null)
                payData = URLDecoder.decode(payData, "utf-8");
            else
                return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        JuHePay.init(context);
//        IpaynowPlugin.getInstance().setCallResultReceiver((ResponseParams responseParams) -> {
//            String respCode = responseParams.respCode;
//            String errorCode = responseParams.errorCode;
//            String errorMsg = responseParams.respMsg;
//            StringBuilder temp = new StringBuilder();
//            if (respCode.equals("00")) {
                rechargeListener.rechargeSuccess(fromBuy, autoBuy);
//                temp.append("支付成功");
//                ((Activity) context).runOnUiThread(() -> {
//                    Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
//                });
//                return;
//            } else if (respCode.equals("02")) {
//                temp.append("交易状态:取消");
//            } else if (respCode.equals("01")) {
//                temp.append("交易状态:失败").append("\n").append("错误码:").append(errorCode).append("原因:" + errorMsg);
//            } else if (respCode.equals("03")) {
//                temp.append("交易状态:未知").append("\n").append("原因:" + errorMsg);
//            } else {
//                temp.append("respCode=").append(respCode).append("\n").append("respMsg=").append(errorMsg);
//            }
//            ToastDlg.show(context, temp.toString());
//        });
//        IpaynowPlugin.getInstance().pay(payData);
    }
}
