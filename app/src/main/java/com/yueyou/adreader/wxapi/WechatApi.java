package com.yueyou.adreader.wxapi;

import android.app.Activity;
import android.content.Context;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yueyou.adreader.service.Action;
import com.yueyou.adreader.service.HttpEngine;
import com.yueyou.adreader.util.Widget;

public class WechatApi {
    private static final String GET_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private String APP_ID = "wx00d330957ffc8345";
    private String APP_KEY = "361ecbf14fb61c9e9b48d38c30605c70";
    private IWXAPI mApi;
    private Context mContext;
    private boolean mIsLogin;
    private static WechatApi mWechatApi;
    public static WechatApi getInstance(){
        if(mWechatApi == null){
            synchronized (WechatApi.class) {
                if(mWechatApi == null){
                    mWechatApi = new WechatApi();
                }
            }
        }
        return mWechatApi;
    }

    public boolean isInstalled() {
        if (mApi == null)
            return false;
        return mApi.isWXAppInstalled();
    }

    public IWXAPI wxApi() {
        return mApi;
    }

    public void registerApp(Context context) {
        mContext = context;
        if(Widget.getPacketName(context).equals("com.yueyou.adreaderwp")) {
            APP_ID = "wx62c8ecc974f39dbe";
            APP_KEY = "f4bafca0bd9546d8123d177a05e410a0";
        }
        mApi = WXAPIFactory.createWXAPI(context, APP_ID, false);
        mApi.registerApp(APP_ID);
    }

    public void getAuth(boolean login) {
        mIsLogin = login;
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wx_login";
        mApi.sendReq(req);
    }

    public void getToken(String code) {
        try {
            String url = String.format(GET_TOKEN_URL, APP_ID, APP_KEY, code);
            HttpEngine.get(mContext, url, (Context ctx, boolean isSuccessed, Object resultData, Object userData, boolean showProgress)->{
                if (isSuccessed){
                    TokenInfo tokenInfo = (TokenInfo) Widget.stringToObject((String) resultData, TokenInfo.class);
                    ((Activity)mContext).runOnUiThread(()->{
                        Action.getInstance().postWechatOpenId(mContext, tokenInfo.openid, tokenInfo.access_token, mIsLogin);
                    });
                }
            }, true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class TokenInfo{
        private String access_token;
        private String openid;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }
    }
}
