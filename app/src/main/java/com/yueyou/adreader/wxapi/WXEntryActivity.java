package com.yueyou.adreader.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!WechatApi.getInstance().wxApi().handleIntent(getIntent(), this))
            finish();
    }
    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_SHARE == resp.getType()){
                    Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        String code = ((SendAuth.Resp) resp).code;
                        WechatApi.getInstance().getToken(code);
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
        finish();
    }
}
