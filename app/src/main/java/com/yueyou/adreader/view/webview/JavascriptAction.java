package com.yueyou.adreader.view.webview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.yueyou.adreader.activity.AboutActivity;
import com.yueyou.adreader.activity.ReadActivity;
import com.yueyou.adreader.activity.SearchActivity;
import com.yueyou.adreader.activity.WebViewActivity;
import com.yueyou.adreader.activity.YueYouApplication;
import com.yueyou.adreader.service.Action;
import com.yueyou.adreader.service.RechargeAndBuyListener;
import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.service.advertisement.adObject.AdSignRewardVideo;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.BookInfo;
import com.yueyou.adreader.service.model.BookShelfItem;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.Event.BuyBookEvent;
import com.yueyou.adreader.view.Event.CloseNewBookEvent;
import com.yueyou.adreader.view.Event.UserEvent;
import com.yueyou.adreader.view.dlg.MessageDlg;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zy on 2017/4/7.
 */

public class JavascriptAction {
    private CustomWebView mCustomWebView;
    private String mBookInfoStr;
    private int mChapterId;
    private String mFrom;
    private boolean mRead;
    private Map<String, String> mPageParam;
    private CloseNewBookEvent closeNewBookEvent;
    public static String callBack = "";
    public static boolean rewardVerify = false;

    public JavascriptAction(CustomWebView webView) {
        mCustomWebView = webView;
        mPageParam = new HashMap<String, String>();
    }

    public void setCloseNewBookEvent(CloseNewBookEvent closeNewBookEvent) {
        this.closeNewBookEvent = closeNewBookEvent;
    }

    @JavascriptInterface
    public void openUrlByNewWebView(String url, String action, String title) {
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            if (action.equals("recharge")) {
                WebViewActivity.show((Activity) mCustomWebView.getContext(), url, WebViewActivity.RECHARGE, title);
            } else if (action.equals("login")) {
                WebViewActivity.show((Activity) mCustomWebView.getContext(), url, WebViewActivity.LOGIN, title);
            } else if (action.equals("bind")) {
                WebViewActivity.show((Activity) mCustomWebView.getContext(), url, WebViewActivity.BIND, title);
            } else if (action.equals("account")) {
                WebViewActivity.show((Activity) mCustomWebView.getContext(), url, WebViewActivity.ACCOUNT, title);
            } else {
                WebViewActivity.show((Activity) mCustomWebView.getContext(), url, WebViewActivity.UNKNOW, title);
            }
        });
    }

    @JavascriptInterface
    public String signUrl(String url) {
        return Url.signUrl(mCustomWebView.getContext(), url);
    }

    @JavascriptInterface
    public void saveUserInfo(String userId, String token) {
        DataSHP.saveUserInfo(mCustomWebView.getContext(), userId, token);
    }

    @JavascriptInterface
    public void loginSuccessByPhone() {
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            UserEvent.getInstance().loginSuccess();
        });
    }

    @JavascriptInterface
    public void bindSuccessByPhone() {
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            UserEvent.getInstance().bindSuccess();
        });
    }


    @JavascriptInterface
    public void gotoSetting() {
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            Widget.startActivity((Activity) mCustomWebView.getContext(), AboutActivity.class);
        });
    }

    @JavascriptInterface
    public void closeView() {
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            UserEvent.getInstance().closeTopWebView();
        });
    }

    @JavascriptInterface
    public void pageGoBack() {
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            //toast("pageGoBack", 2);
            UserEvent.getInstance().pageGoBack();
        });
    }

    @JavascriptInterface
    public void disableWebviewRefresh() {
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            UserEvent.getInstance().disableWebviewRefresh();
        });
    }

    @JavascriptInterface
    public void goSearchPage() {
        this.goSearchPage(null);
    }

    @JavascriptInterface
    public void goSearchPage(String title) {
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            ThirdAnalytics.onEventPageViewSearch(mCustomWebView.getContext(), title);
            Widget.startActivity((Activity) mCustomWebView.getContext(), SearchActivity.class);
        });
    }

    @JavascriptInterface
    public void bookReader(String bookInfoStr, int chapterId, String from) {
        bookAddOrRead(bookInfoStr, chapterId, from, true);
    }

    @JavascriptInterface
    public void addToBookShelf(String bookInfoStr, int chapterId, String from) {
        bookAddOrRead(bookInfoStr, chapterId, from, false);
    }

    @JavascriptInterface
    public void addBookShelfAndRead(String bookInfoStr, int chapterId, String from) {
        andBookshelfAndRead(bookInfoStr, chapterId, from);
    }

    @JavascriptInterface
    public void toast(String title, int showTime) {
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            Toast.makeText(mCustomWebView.getContext(), title, Toast.LENGTH_LONG).show();
        });
    }

    @JavascriptInterface
    public void toast(String title) {
        toast(title, 2);
    }


    @JavascriptInterface
    public void buy(boolean autoBuy) {
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            BuyBookEvent.buyBook(autoBuy);
        });
    }

    @JavascriptInterface
    public void setPageStr(String k, String v) {
        try {
            mPageParam.remove(k);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPageParam.put(k, v);
    }

    @JavascriptInterface
    public String getPageStr(String k) {
        try {
            return mPageParam.get(k);
        } catch (Exception e) {
            return null;
        }
    }

    @JavascriptInterface
    public void openQQ(String qqNum) {
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            if (Widget.checkApkExist(mCustomWebView.getContext(), "com.tencent.mobileqq")) {
                try {
                    ((Activity) mCustomWebView.getContext()).startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + qqNum + "&version=1")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mCustomWebView.getContext(), "本机未安装QQ应用", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @JavascriptInterface
    public void goMain() {
        if (closeNewBookEvent != null) {
            closeNewBookEvent.close();
        }
        Uri uri = Uri.parse("yueyoureader://host:8082/bookStore/recommend");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage(Widget.getPacketName(mCustomWebView.getContext()));
        mCustomWebView.getContext().startActivity(intent);
    }

    @JavascriptInterface
    public void loadAd(int x, int y, int width, int height, int topBounds, int bottomBounds) {
        mCustomWebView.loadAd(x, y, width, height, topBounds, bottomBounds);
    }

    @JavascriptInterface
    public void refreshAdOffset(int x, int y) {
        mCustomWebView.refreshAdOffset(x, y);
    }

    @JavascriptInterface
    public void openUrlByBrowser(String url, boolean needWifi, String toast) {
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            try {
                if (needWifi && Widget.getAPNType(mCustomWebView.getContext()) != 1 && !Widget.isBlank(toast)) {
                    MessageDlg.show(mCustomWebView.getContext(), toast, (boolean result) -> {
                                if (result) {
                                    openUrlByBrowser(url);
                                }
                            }
                    );
                }
                openUrlByBrowser(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @JavascriptInterface
    public void openSignRewardVideo(String extra, String callBack) {
        this.callBack = callBack;
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            try {
                AdSignRewardVideo adSignRewardVideo = new AdSignRewardVideo();
                adSignRewardVideo.show(extra);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @JavascriptInterface
    public String getBookshelfContainerSize() {
        int screenWidth = ScreenUtils.getScreenWidth();
        int containerWidth = screenWidth - ConvertUtils.dp2px(30);
        int containerHeight = ConvertUtils.dp2px(135);
        return containerWidth + "," + containerHeight;
    }

    private void openUrlByBrowser(String url) {
        Widget.downloadApk((Activity) mCustomWebView.getContext(), url);
    }

    private void bookAddOrRead(String bookInfoStr, int chapterId, String from, boolean read) {
        mBookInfoStr = bookInfoStr;
        mChapterId = chapterId;
        mFrom = from;
        mRead = read;
        BookInfo book = (BookInfo) Widget.stringToObject(bookInfoStr, BookInfo.class);
        int result = Action.getInstance().downloadChapter(mCustomWebView.getContext(), book.getSiteBookID(), null, chapterId, false);
        if (result != 1)
            return;
        result = Action.getInstance().downloadCover(mCustomWebView.getContext(), book.getImageUrl(), book.getSiteBookID(), true);
        if (result != 1) {
//            return;
        }
        book.setChapterCount(Action.getInstance().getChapterCount());
        boolean bookExist = !((YueYouApplication) mCustomWebView.getContext().getApplicationContext()).getMainActivity().bookshelfFrament().addBook(book, chapterId, true, false, true);
        if (!read) {
            Toast.makeText(mCustomWebView.getContext(), "书籍已添加到书架", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bookExist) {
            BookShelfItem item = ((YueYouApplication) mCustomWebView.getContext().getApplicationContext()).getMainActivity().bookshelfFrament().getBook(book.getSiteBookID());
            if (!"bookDetail".equals(from) && chapterId != item.getChapterIndex()) {
                item.setChapterIndex(chapterId);
                item.setDataOffset(0);
                item.setDisplayOffset(0);
            }
        }
        if (mCustomWebView == null) {
            Utils.logError("mCustomWebView ->" + mCustomWebView);
            return;
        }
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(ReadActivity.KEY_BOOKID, book.getSiteBookID());
            params.put(ReadActivity.KEY_BOOK_TMP, !bookExist);
            Widget.startActivity((Activity) mCustomWebView.getContext(), ReadActivity.class, params);
        });
    }

    private void andBookshelfAndRead(String bookInfoStr, int chapterId, String from) {
        mBookInfoStr = bookInfoStr;
        mChapterId = chapterId;
        mFrom = from;
        BookInfo book = (BookInfo) Widget.stringToObject(bookInfoStr, BookInfo.class);
        int result = Action.getInstance().downloadChapter(mCustomWebView.getContext(), book.getSiteBookID(), null, chapterId, false);
        if (result != 1)
            return;
        result = Action.getInstance().downloadCover(mCustomWebView.getContext(), book.getImageUrl(), book.getSiteBookID(), true);
        if (result != 1) {
//            return;
        }
        book.setChapterCount(Action.getInstance().getChapterCount());
        boolean bookExist = !((YueYouApplication) mCustomWebView.getContext().getApplicationContext()).getMainActivity().bookshelfFrament().addBook(book, chapterId, true, false, true);
        if (bookExist) {
            BookShelfItem item = ((YueYouApplication) mCustomWebView.getContext().getApplicationContext()).getMainActivity().bookshelfFrament().getBook(book.getSiteBookID());
            if (!"bookShelf".equals(from) && chapterId != item.getChapterIndex()) {
                item.setChapterIndex(chapterId);
                item.setDataOffset(0);
                item.setDisplayOffset(0);
            }
        }
        if (mCustomWebView == null) {
            Utils.logError("mCustomWebView ->" + mCustomWebView);
            return;
        }
        ((Activity) mCustomWebView.getContext()).runOnUiThread(() -> {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(ReadActivity.KEY_BOOKID, book.getSiteBookID());
            params.put(ReadActivity.KEY_BOOK_TMP, false);
            Widget.startActivity((Activity) mCustomWebView.getContext(), ReadActivity.class, params);
            Activity activity = (Activity) mCustomWebView.getContext();
            activity.finish();
        });
    }

    public void buyBook() {
        new Thread(() -> {
            Looper.prepare();
            bookAddOrRead(mBookInfoStr, mChapterId, mFrom, mRead);
        }).start();
    }

    private RechargeAndBuyListener mRechargeAndBuyListener = new RechargeAndBuyListener() {
        @Override
        public void rechargeSuccess(boolean fromBuy, boolean autoBuy) {
            UserEvent.getInstance().rechargeSuccess();
            if (fromBuy) {
                buy(autoBuy);
            }
        }
    };
}
