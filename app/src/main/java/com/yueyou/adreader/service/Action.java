package com.yueyou.adreader.service;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.qq.e.comm.util.StringUtil;
import com.yueyou.adreader.activity.YueYouApplication;
import com.yueyou.adreader.service.analytics.AnalyticsEngine;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.service.db.BookFileEngine;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.service.model.BookDetail;
import com.yueyou.adreader.service.model.BookInfo;
import com.yueyou.adreader.service.model.BookShelfItem;
import com.yueyou.adreader.service.model.BuildinBookInfo;
import com.yueyou.adreader.service.model.ChapterContent;
import com.yueyou.adreader.service.model.ChapterInfo;
import com.yueyou.adreader.service.model.GetBookResponse;
import com.yueyou.adreader.service.model.RedSpotBean;
import com.yueyou.adreader.service.model.Response;
import com.yueyou.adreader.service.model.ResponseCode;
import com.yueyou.adreader.service.model.UserInfoWithRedirectUrl;
import com.yueyou.adreader.util.Const;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.Event.BuyBookEvent;
import com.yueyou.adreader.view.Event.UserEvent;
import com.yueyou.adreader.view.dlg.WebViewDlg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zy on 2017/3/29.
 */

public class Action {
    private static Action mAction = null;
    private Request mRequest;
    private boolean mInBuyView;
    private boolean mAutoBuy;
    private int mChapterCount;

    public static Action getInstance() {
        if (mAction == null) {
            synchronized (Action.class) {
                if (mAction == null) {
                    mAction = new Action();
                    mAction.init();
                }
            }
        }
        return mAction;
    }

    public void init() {
        try {
            mRequest = new Request();
            mRequest.init(mRequestListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Request request() {
        return mRequest;
    }

    private void startWebViewDlg(Context context, String title, String url) {
        mInBuyView = true;
        BuyBookEvent.setEventListener((BuyBookEvent.BuyBookEventListener) context);
        ((Activity) context).runOnUiThread(() -> {
            try {
                WebViewDlg.show((Activity) context, title, url, () -> {
                    mInBuyView = false;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setAutoBuy(boolean autoBuy) {
        mAutoBuy = autoBuy;
    }

    public int downloadChapter(Context context, int bookId, String bookName, int chapterId, boolean isBg) {
        return downloadChapter(context, bookId, bookName, chapterId, isBg, null);
    }

    public int downloadChapter(Context context, int bookId, String bookName, int chapterId, boolean isBg, DownloadChapterCallBack callBack) {
        String isOssed = "1";
        synchronized (this) {
            try {
                if (isBg && mInBuyView)
                    return 0;
                if (!BookFileEngine.isNeedDownloadChapterForDownload(context, bookId, chapterId))
                    return 1;
                HashMap<String, String> params = new HashMap<String, String>() {{
                    put("bookId", bookId + "");
                    put("chapterId", chapterId + "");
                    put("inBuyView", mInBuyView + "");
                    put("autoBuy", mAutoBuy + "");
                    put("isOssed", isOssed);//1开启,0关闭
                }};
                Response response = mRequest.start(context, params, ActionType.downloadChapter, mRequestListener, !isBg);
                Utils.logNoTag("charge code -> %d ,data -> %s", response.getCode(), response.getData());
                mInBuyView = false;
                if (response.getCode() == ResponseCode.REMAIN_LESS && !isBg) {//余额不足
                    startWebViewDlg(context, "充值", (String) response.getData());
                    return 2;
                } else if (response.getCode() == ResponseCode.CONTENT_UNPAY && !isBg) {//显示订阅页
                    startWebViewDlg(context, "购买", (String) response.getData());
                    return 4;
                } else if (response.getCode() != ResponseCode.SUCCESS) {
                    return 0;
                }
                ChapterInfo chapterInfo = (ChapterInfo) Widget.jsonToObjectByMapStr(response.getData(), ChapterInfo.class);
                Utils.logNoTag("chapter::Info -> %s", Widget.objectToString(chapterInfo));
                chapterInfo.setBookID(bookId);
                ChapterContent chapterContent;
                if (Utils.isOss(chapterInfo.getContentUrl())) {
                    response = mRequest.get(context, chapterInfo.getContentUrl(), mRequestListener, !isBg);
                    chapterContent = new ChapterContent();
                    chapterContent.setContent((String) response.getData());
                    chapterContent.setContent(chapterContent.getContent().replace(chapterInfo.getChapterName() + "\n", ""));
                    String content = chapterContent.getContent();
                    if (content.startsWith("\n")) {
                        chapterContent.setContent(content.substring(content.indexOf("\n")));
                    }
                    chapterContent.setTitle(chapterInfo.getChapterName());
                } else {
                    response = mRequest.start(context, null, chapterInfo.getContentUrl(), null, mRequestListener, !isBg);
                    chapterContent = (ChapterContent) Widget.jsonToObjectByMapStr(response.getData(), ChapterContent.class);
                }
                if (response.getCode() != ResponseCode.SUCCESS) {
                    return 0;
                }
                if (chapterContent == null || Widget.isBlank(chapterContent.getContent()))
                    return 0;
                Utils.logNoTag("chapter::Content -> %s", Widget.objectToString(chapterContent));
                chapterContent.setPreviousChapterId(chapterInfo.getPreviousChapterId());
                chapterContent.setNextChapterId(chapterInfo.getNextChapterId());
                chapterContent.setVip(chapterInfo.isVipChapter());
                BookFileEngine.saveBookChapterContent(context, bookId, chapterId, chapterContent);
                ((YueYouApplication) context.getApplicationContext()).getMainActivity().bookshelfFrament().refreshBookChapterCount(bookId, chapterInfo.getChapterCount());
                mChapterCount = chapterInfo.getChapterCount();
                String content = chapterContent.getContent();
                if (bookName != null && content != null) {
                    AnalyticsEngine.read(context, bookId, bookName, chapterId, false, content.length());
                }
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public int getChapterCount() {
        return mChapterCount;
    }

    public void refreshChapterCount(Context context, int bookId, ActionListener listener) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("bookId", bookId + "");
        }};
        mRequest.startAsync(context, params, ActionType.getChapterCount, listener, false);
    }

    public List<ChapterInfo> downloadChapterList(Context context, int bookId) {
        String count = "0";
        String ts = "";
        String savedChaterInfo = DataSHP.getChapterCount(context, bookId);
        if (!StringUtil.isEmpty(savedChaterInfo)) {
            count = savedChaterInfo.split("_")[0];
            ts = savedChaterInfo.split("_")[1];
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("bookId", bookId + "");
        params.put("chapterCount", count);
        params.put("ts", ts);

        Response response = mRequest.start(context, params, ActionType.downloadChapterList, mRequestListener, false);

        String url = (String) response.getData();
        List<ChapterInfo> chapterInfos = null;
        try {
            chapterInfos = BookFileEngine.getBookChapterList(context, bookId);
        } catch (Exception e) {
            Utils.logError(e, "downloadChapterList error");
        }
        if (response.getCode() != ResponseCode.SUCCESS) {
            LogUtil.e("downloadChapterList  %s ,本地列表 %d ,params ：不需要更新");
            Utils.logNoTag("downloadChapterList  %s ,本地列表 %d ,params ：" + params, "不需要更新", chapterInfos == null ? 0 : chapterInfos.size());
            if (chapterInfos == null || chapterInfos.size() == 0) {
                chapterInfos = requestChapterList(context, bookId, url);
            }
        } else {
            Utils.logNoTag("downloadChapterList  %s ,本地列表 %d ,params ：" + params, "需要更新", chapterInfos == null ? 0 : chapterInfos.size());
            LogUtil.e("downloadChapterList  %s ,本地列表 %d ,params ：需要更新");
            chapterInfos = requestChapterList(context, bookId, url);
        }


        return chapterInfos;
    }

    private List<ChapterInfo> requestChapterList(Context context, int bookId, String url) {
        Response response = mRequest.start(context, null, url, null, mRequestListener, true);
        List<ChapterInfo> chapterInfos = (List<ChapterInfo>) Widget.jsonToObjectByMapStr(response.getData(), new TypeToken<List<ChapterInfo>>() {
        }.getType());
        if (chapterInfos != null) {
            BookFileEngine.saveBookChapterList(context, bookId, chapterInfos);
            DataSHP.saveChapterCount(context, bookId, chapterInfos.size());
        }
        return chapterInfos;
    }

    public int downloadCover(Context context, String url, int bookId, boolean showProgress) {
        byte[] data = mRequest.downloadImg(context, url, showProgress);
        if (data == null)
            return 0;
        BookFileEngine.saveBookCover(context, bookId, data);
        return 1;
    }

    public void login(Context context, ActionListener actionListener) {
        mRequest.startAsync(context, null, ActionType.login, actionListener, false);
    }

    public void logUpload(Context context, ActionListener listener, String type, int siteId, String action, String action_desc, String status) {
//        HashMap<String, String> params = new HashMap<String, String>() {{
//            put("type", type);
//            put("siteId", siteId + "");
//            put("action", action);
//            put("action_desc", action_desc);
//            put("status", status);
//            put("time", Utils.dateFormat("", new Date()));
//        }};
//        mRequest.startAsync(context, params, ActionType.adLog, listener, false);
    }


    public void getAdContent(Context context, int siteId, ActionListener listener) {
        try {
            getAdContent(context, siteId, listener, 0, 0);
        } catch (Exception e) {

        }
    }

    public void getAdContent(Context context, int siteId, ActionListener listener, int bookId, int chapterId) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("siteId", siteId + "");
            put("bookId", bookId + "");
            put("chapterId", chapterId + "");
        }};
//        Utils.logNoTag("url begin siteId %d, bookId %d , chapterId %d", siteId, bookId, chapterId);
        mRequest.startAsync(context, params, ActionType.adContent, listener, false);
    }

    public void getAdContentList(Context context, int siteId, ActionListener listener, int bookId, int chapterId) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("bookId", bookId + "");
            put("chapterId", chapterId + "");
            put("siteId", siteId + "");
        }};
        Utils.logNoTag("localAdList getAdContentList --> siteId:%d bookId：%d, chapterId：%d", siteId, bookId, chapterId);
        mRequest.startAsync(context, params, ActionType.adContentList, listener, false);
    }

    public AdContent getAdContent(Context context, int siteId) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("siteId", siteId + "");
        }};
        Response response = mRequest.start(context, params, ActionType.adContent, mRequestListener, false);
        try {
            AdContent adContent = (AdContent) Widget.jsonToObjectByMapStr(response.getData(), AdContent.class);
            return adContent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public RedSpotBean getRedSpotState(Context context, String userId) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("userId", userId + "");
        }};
        Response response = mRequest.start(context, params, ActionType.redSpotState, mRequestListener, false);
        try {
            RedSpotBean redSpotBean = (RedSpotBean) Widget.jsonToObjectByMapStr(response.getData(), RedSpotBean.class);
            return redSpotBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean getBookCover(Context context, List<BookShelfItem> list) {
        boolean flag = false;
        try {
            for (BookShelfItem item : list) {
                File cover = BookFileEngine.getBookCover(context, item.getBookId());
                if (cover != null && cover.exists()) continue;
                downloadCover(context, item.getBookCover(), item.getBookId(), false);
                flag = true;
            }
        } catch (Exception e) {
            ThirdAnalytics.reportError(context, "getBookCover error : %s", e.getMessage(), e);
        }
        return flag;
    }

    public boolean getBookDetail(Context context, String bookId, String chapterId) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("bookId", bookId + "");
            put("chapterId", chapterId + "");
        }};
        Response response = mRequest.start(context, params, ActionType.getBookDetail, mRequestListener, false);
        try {
            BookDetail bookDetail = (BookDetail) Widget.jsonToObjectByMapStr(response.getData(), BookDetail.class);
            BookInfo bookInfo = new BookInfo();
            bookInfo.setName(bookDetail.getBookName());
            bookInfo.setSiteBookID(bookDetail.getBookId());
            bookInfo.setImageUrl(bookDetail.getBookCover());
            int cid = Integer.parseInt(bookDetail.getCurReadChapterId());
            ((YueYouApplication) context.getApplicationContext()).getMainActivity().bookshelfFrament().addBook(bookInfo, cid, true, true, true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getBook(Context context, String bookId, String chapterId) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("bookId", bookId);
            put("chapterId", chapterId);
        }};
        Response response = null;
        for (int i = 0; i < 3; i++) {
            response = mRequest.start(context, params, ActionType.getBook, mRequestListener, true);
            if (response.getCode() == ResponseCode.SUCCESS)
                break;
        }
        if (response.getCode() != ResponseCode.SUCCESS) {
            return false;
        }
        try {
            GetBookResponse getBookResponse = (GetBookResponse) Widget.jsonToObjectByMapStr(response.getData(), GetBookResponse.class);
            BookInfo bookInfo = new BookInfo();
            bookInfo.setName(getBookResponse.getBookName());
            bookInfo.setSiteBookID(getBookResponse.getBookId());
            bookInfo.setImageUrl(getBookResponse.getBookCover());
            int cid = Integer.parseInt(getBookResponse.getCurReadChapterId());
            ((YueYouApplication) context.getApplicationContext()).getMainActivity().bookshelfFrament().addBook(bookInfo, cid, true, true, true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getBuildingBookNew(Context context, String bookName, String bookId, String chapterId, int cover) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("bookName", bookName);
            put("bookId", bookId);
            put("chapterId", chapterId);
            put("isConPic", cover + "");
        }};
        Response response = null;
        for (int i = 0; i < 3; i++) {
            response = mRequest.start(context, params, ActionType.getBuildinBookNew, mRequestListener, true);
            if (response.getCode() == ResponseCode.SUCCESS)
                break;
        }
        if (response.getCode() != ResponseCode.SUCCESS) {
            AnalyticsEngine.addBuildinBookFinish(context, false, response.getCode() + "");
            return false;
        }
        try {
            List<BuildinBookInfo> buildinBookInfos = (List<BuildinBookInfo>) Widget.jsonToObjectByMapStr(response.getData(), new TypeToken<List<BuildinBookInfo>>() {
            }.getType());
            for (BuildinBookInfo item : buildinBookInfos) {
                BookInfo bookInfo = new BookInfo();
                bookInfo.setName(item.getBookName());
                bookInfo.setSiteBookID(item.getBookId());
                bookInfo.setImageUrl(item.getBookCover());
                bookInfo.setAuthor(item.getAuthor());
                bookInfo.setCopyrightName(item.getCopyrightName());
                int cid = Integer.parseInt(item.getFirstChapterId());
                if (!Widget.isBlank(item.getCurReadChapterId())) {
                    cid = Integer.parseInt(item.getCurReadChapterId());
                }
                if (item.getCoverContent() != null) {
                    byte[] bytes = android.util.Base64.decode(item.getCoverContent(), android.util.Base64.DEFAULT);
                    BookFileEngine.saveBookCover(context, bookInfo.getSiteBookID(), bytes);
                }
                ((YueYouApplication) context.getApplicationContext()).getMainActivity().bookshelfFrament().addBook(bookInfo, cid, false, false, false);
                if (item.getDftOpen() == 1) {
                    ((YueYouApplication) context.getApplicationContext()).getMainActivity().bookshelfFrament().defaultOpenFirstBook();
                }
            }
            AnalyticsEngine.addBuildinBookFinish(context, true, "");
            ((YueYouApplication) context.getApplicationContext()).getMainActivity().bookshelfFrament().getBuildinBookFinish();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (response.getData() == null) {
                AnalyticsEngine.addBuildinBookFinish(context, false, e.getMessage());
            } else {
                AnalyticsEngine.addBuildinBookFinish(context, false, e.getMessage() + " data:" + response.getData());
            }
        }
        return false;
    }

    public boolean getShelfBookPull(Context context, List<Integer> bookIds) {
        String bookListStr = bookIds.toString().replace("[", "").replace("]", "");
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("book_ids", bookListStr);
        }};
        Response response = mRequest.start(context, params, ActionType.ShelfBookPull, mRequestListener, true);
        if (response == null || response.getCode() != ResponseCode.SUCCESS) {
            return false;
        }
        try {
//            Utils.logNoTag("getShelfBookPull result :  %s ", response.getData());
            List<BuildinBookInfo> buildinBookInfos = (List<BuildinBookInfo>) Widget.jsonToObjectByMapStr(response.getData(), new TypeToken<List<BuildinBookInfo>>() {
            }.getType());
            if (buildinBookInfos == null) {
                return false;
            }
            for (BuildinBookInfo item : buildinBookInfos) {
                BookInfo bookInfo = new BookInfo();
                bookInfo.setName(item.getBookName());
                bookInfo.setSiteBookID(item.getBookId());
                bookInfo.setImageUrl(item.getBookCover());
                int cid = Integer.parseInt(item.getFirstChapterId());
                if (item.getBookCover() != null) {
                    downloadCover(context, item.getBookCover(), item.getBookId(), false);
                }
                ((YueYouApplication) context.getApplicationContext()).getMainActivity().bookshelfFrament().addBook(bookInfo, cid, true, false, true);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getPopupWindow(Context context, int alertPos, ActionListener listener) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("alertPos", alertPos + "");
        }};
        mRequest.startAsync(context, params, ActionType.getPopupWindow, listener, false);
    }

    public void getUpdateBook(Context context, String books, ActionListener listener) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("param", books);
        }};
        mRequest.startAsync(context, params, ActionType.checkUpdateBook, listener, false);
    }

    public void uploadBookIds(Context context, String bookIds) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("book_ids", bookIds);
        }};
        mRequest.startAsync(context, params, ActionType.uploadBookId, null, false);
    }

    public void checkAppUpdate(Context context, ActionListener listener) {
        mRequest.startAsync(context, null, ActionType.checkAppUpdate, listener, false);
    }

    public void postWechatOpenId(Context context, String openId, String accessToken, boolean isLogin) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("wechatId", openId);
            put("wechatToken", accessToken);
        }};
        ActionType type = ActionType.wechatLogin;
        if (!isLogin)
            type = ActionType.wechatBind;
        mRequest.startAsync(context, params, type, new ActionListener() {
            @Override
            public void onResponse(Object object) {
                UserInfoWithRedirectUrl userInfo = (UserInfoWithRedirectUrl) Widget.jsonToObjectByMapStr(object, UserInfoWithRedirectUrl.class);
                DataSHP.saveUserInfo(context, userInfo.getUserId(), userInfo.getToken());
                ((Activity) context).runOnUiThread(() -> {
                    if (isLogin) {
                        UserEvent.getInstance().loginSuccess();
                    } else {
                        UserEvent.getInstance().bindSuccess();
                    }
                });
            }
        }, true);
    }

    public void userCheckBind(Context context) {
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("userId", DataSHP.getUserId(context));
        }};
        ActionType type = ActionType.userCheckBind;
        mRequest.startAsync(context, params, type, (ActionListener) object -> {
            try {
                JSONObject jo = new JSONObject(object.toString());
                DataSHP.saveUserIsBind(context, jo.optInt("is_bind"));
                DataSHP.saveUserIsVIP(context, jo.optInt("is_vip"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, false);
    }

    public void getCtlContent(Context context) {
        mRequest.startAsync(context, null, ActionType.ctlContent, (ActionListener) object -> {
            try {
                JSONObject jo = new JSONObject(object.toString());
                int jl_btn_show = jo.optInt(Const.KEY_JL_BTN_SHOW);
                if (jl_btn_show == 1) {
                    DataSHP.saveCtlContent(context, Const.KEY_JL_BTN_SHOW);
                } else { //不显示
                    DataSHP.removeCtlContent(context, Const.KEY_JL_BTN_SHOW);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, false);
    }

    private Request.RequestListener mRequestListener = (Context context, Object resultData, Object userData) -> {
        if ("String".equals(resultData.getClass().getSimpleName())) {
            Response response = (Response) Widget.stringToObject((String) resultData, Response.class);
            if (response.getCode() == ResponseCode.SAVE_USER_INFO) {
                UserInfoWithRedirectUrl userInfoWithRedirectUrl = (UserInfoWithRedirectUrl) Widget.jsonToObjectByMapStr(response.getData(), UserInfoWithRedirectUrl.class);
                if (userInfoWithRedirectUrl != null) {
                    DataSHP.saveUserInfo(context, userInfoWithRedirectUrl.getUserId(), userInfoWithRedirectUrl.getToken());
                }
                if (userData != null)
                    ((ActionListener) userData).onResponse(response);
                return;
            }

            if (response.getCode() == ResponseCode.SUCCESS) {
                if (userData != null)
                    ((ActionListener) userData).onResponse(response.getData());
            } else {
//                ThirdAnalytics.reportError(context,"request error %s",response.getMsg());
                if (context instanceof Activity) {
                    ((Activity) context).runOnUiThread(() -> {
                        try {
                            if (!Widget.isBlank(response.getMsg()))
                                Toast.makeText(context, response.getMsg(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    });
                }
            }
        } else {

        }
    };

    public interface ActionListener {
        void onResponse(Object object);
    }

    public enum ActionType {
        unkonw,
        downloadChapter,
        downloadChapterList,
        getChapterCount,
        checkUpdateBook,
        getBuildinBook,
        getBuildinBookNew,
        ShelfBookPull,
        login,
        checkAppUpdate,
        wechatLogin,
        userCheckBind,
        wechatBind,
        getPopupWindow,
        getBook,
        adContent,
        adContentList,
        ctlContent,
        adLog,
        uploadBookId,
        redSpotState,
        getBookDetail
    }
}
