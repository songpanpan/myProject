package com.yueyou.adreaderwp.wxapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yueyou.adreader.R;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.view.Event.BuyBookEvent;
import com.yueyou.adreader.view.Event.UserEvent;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, "wx62c8ecc974f39dbe");
        api.handleIntent(getIntent(), this);
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WXPayEntryActivity.this.finish();
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        PayResp payResp = (PayResp) resp;
        String payType = payResp.extData;
        LogUtil.e("onPayFinish, errCode = " + resp.errCode);
        LogUtil.e("onPayFinish, payType = " + payType);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                UserEvent.getInstance().rechargeSuccess();
                if (payType.equals("1")) {
                    BuyBookEvent.buyBook(false);
                } else if (payType.equals("2")) {
                    BuyBookEvent.buyBook(true);
                }
            } else if (resp.errCode == -1) {
                Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
            } else if (resp.errCode == -2) {
                Toast.makeText(this, "取消支付", Toast.LENGTH_SHORT).show();
            }
        }
    }
}