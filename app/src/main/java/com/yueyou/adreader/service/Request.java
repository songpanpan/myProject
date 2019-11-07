package com.yueyou.adreader.service;

import android.content.Context;
import android.widget.Toast;

import com.qq.e.comm.util.StringUtil;
import com.yueyou.adreader.service.Action.ActionType;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.Response;
import com.yueyou.adreader.service.model.ResponseCode;
import com.yueyou.adreader.service.model.UserInfoWithRedirectUrl;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zy on 2017/3/29.
 */

public class Request {
    private HttpEngine mHttpEngine;
    private RequestListener mRequestListener;

    public void init(RequestListener requestListener) {
        mRequestListener = requestListener;
        mHttpEngine = new HttpEngine();
        mHttpEngine.init(mHttpEngineListener);
    }

    public HttpEngine httpEngine() {
        return mHttpEngine;
    }

    public byte[] downloadImg(Context context, String url, boolean showProgress) {
        return (byte[]) mHttpEngine.getRequest(context, url, showProgress);
    }

    private Response exec(Context context, Map<String, String> params, String url, List<String> imgList, RequestListener requestListener, boolean showProgress) {
        try {
            String result = (String) mHttpEngine.postRequest(context, url, params, imgList, showProgress);
            Response response = (Response) Widget.stringToObject(result, Response.class);
            if (response == null) return new Response();
            if (response.getCode() == ResponseCode.SAVE_USER_INFO) {
                UserInfoWithRedirectUrl userInfoWithRedirectUrl = (UserInfoWithRedirectUrl) Widget.jsonToObjectByMapStr(response.getData(), UserInfoWithRedirectUrl.class);
                if (userInfoWithRedirectUrl != null) {
                    DataSHP.saveUserInfo(context, userInfoWithRedirectUrl.getUserId(), userInfoWithRedirectUrl.getToken());
                    if (url.startsWith(Url.URL_LOGIN)) {
                        return new Response(ResponseCode.SUCCESS);
                    }
                    return new Response();//exec(context, params, url, imgList, requestListener, showProgress);
                }
            } else {
            }
            return response;
        } catch (Exception e) {
            ThirdAnalytics.reportError(context, "url ->%s ,error ", url, e.getMessage());
        }
        return new Response();
    }

    public Response get(Context context, String url, RequestListener requestListener, boolean showProgress) {
        Object result = mHttpEngine.getRequest(context, url, showProgress);
        Response response;
        if (Utils.isOss(url)) {
            response = new Response();
            if (result != null) {
                response.setData(result);
                response.setCode(ResponseCode.SUCCESS);
            }
        } else {
            response = (Response) Widget.stringToObject((String) result, Response.class);
        }
        return response;
    }

    public Response start(Context context, Map<String, String> params, String url, List<String> imgList, RequestListener requestListener, boolean showProgress) {
        if (StringUtil.isEmpty(url)) {
            Utils.logError("error url -->" + url);
            return new Response();
        }
        url = Url.signUrl(context, url);
        return exec(context, params, url, imgList, requestListener, showProgress);
    }

    public Response start(Context context, Map<String, String> params, ActionType type, List<String> imgList, RequestListener requestListener, boolean showProgress) {
        String url = getUrl(context, type);
        return exec(context, params, url, imgList, requestListener, showProgress);
    }

    public Response start(Context context, Map<String, String> params, ActionType type, RequestListener requestListener, boolean showProgress) {
        return start(context, params, type, (List<String>) null, requestListener, showProgress);
    }


    public Response start(Context context, Map<String, String> params, ActionType type, String imgName, RequestListener requestListener, boolean showProgress) {
        List<String> list = new ArrayList<String>();
        if (imgName != null && imgName.length() > 0) {
            list.add(imgName);
        }
        return start(context, params, type, list, requestListener, showProgress);
    }

    public void startAsync(Context context, Map<String, String> params, ActionType type, Object userData, boolean showProgress) {
        String url = getUrl(context, type);
        mHttpEngine.postRequest(context, url, params, userData, showProgress);
    }

    private HttpEngine.HttpEngineListener mHttpEngineListener = (Context ctx, boolean isSuccessed, Object resultData, Object userData, boolean showProgress) -> {
        if (!isSuccessed) {
            if (showProgress) {
                Toast.makeText(ctx, "数据加载失败", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        mRequestListener.onRespone(ctx, resultData, userData);
    };

    public String getUrl(Context context, ActionType actionType) {
        String url = "";
        switch (actionType) {
            case downloadChapter:
                url = Url.URL_DOWNLOAD_CHAPTER;
                break;
            case downloadChapterList:
                url = Url.URL_DOWNLOAD_CHAPTER_LIST;
                break;
            case getChapterCount:
                url = Url.URL_GET_CHAPTER_COUNT;
                break;
            case getBuildinBook: //废弃
                url = Url.URL_GET_BUILDIN;
                break;
            case getBuildinBookNew:
                url = Url.URL_GET_BUILDIN_NEW;
                break;
            case ShelfBookPull:
                url = Url.URL_SHELF_BOOK_PULL;
                break;
            case checkUpdateBook:
                url = Url.URL_CHECK_UPDATE;
                break;
            case login:
                url = Url.URL_LOGIN;
                break;
            case checkAppUpdate:
                url = Url.URL_CHECK_APP_UPDATE;
                //url = "http://192.168.1.48:9090/api/upgrade/getNewApk.do?";
                break;
            case wechatBind:
                url = Url.URL_WECHAT_BIND;
                break;
            case wechatLogin:
                url = Url.URL_WECHAT_LOGIN;
                break;
            case getPopupWindow:
                url = Url.URL_POPUO_WINDOW;
                break;
            case adContent:
                url = Url.URL_AD_CONTENT.replace(Url.URL_BASE, Url.URL_BASE_AD);
//                url = "http://192.168.1.19:8080/api/advertisement/getAdContent.do?";
//                url = "http://game.ireader.com.cn:60000/api/advertisement/getAdContent.do?";
                break;
            case adContentList:
                url = Url.URL_AD_CONTENT_NEW.replace(Url.URL_BASE, Url.URL_BASE_AD);
                break;
            case getBook:
                url = Url.URL_GET_BOOK;
                break;
            case userCheckBind:
                url = Url.URL_USER_CHECK_BIND;
                break;
            case ctlContent:
                url = Url.URL_AD_CRL_BTN;
                break;
            case adLog:
                url = Url.URL_AD_LOG;
                break;
            case uploadBookId:
                url = Url.URL_UPLOAD_BOOKID;
                break;
            case redSpotState:
                url = Url.URL_BOOKSHELF_RED_SPOT;
                break;
            case getBookDetail:
                url = Url.URL_GET_BOOK_DETAIL;
                break;
            default:
                break;
        }
        return Url.signUrl(context, url);
    }

    public interface RequestListener {
        void onRespone(Context context, Object resultData, Object userData);
    }
}
