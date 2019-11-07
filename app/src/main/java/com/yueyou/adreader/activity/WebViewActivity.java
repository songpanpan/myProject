package com.yueyou.adreader.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.qq.e.comm.util.StringUtil;
import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.base.BaseActivity;
import com.yueyou.adreader.activity.refreshload.RefreshLoadLayout;
import com.yueyou.adreader.service.Action;
import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.Event.BuyBookEvent;
import com.yueyou.adreader.view.Event.CloseNewBookEvent;
import com.yueyou.adreader.view.Event.UserEvent;
import com.yueyou.adreader.view.dlg.TitleMessageCloseDlg;
import com.yueyou.adreader.view.webview.CustomWebView;
import com.yueyou.adreader.view.webview.JavascriptAction;
import com.yueyou.adreader.view.webview.PullToRefreshWebView;

import java.util.Map;
import java.util.Set;

public class WebViewActivity extends BaseActivity implements CustomWebView.CustomWebViewListener, BuyBookEvent.BuyBookEventListener, CloseNewBookEvent {
    public static final String UNKNOW = "unknow";
    public static final String CLOSED = "closed";
    public static final String BIND = "bind";
    public static final String CHECKBING = "checkBing";
    public static final String LOGIN = "login";
    public static final String PAY = "pay";
    public static final String RECHARGE = "recharge";
    public static final String ACCOUNT = "account";
    public static final String RECOMMEND_ENDPAGE = "recommend_endpage";

    public static final String KEY_IS_TMPBOOK = "is_tmp_book";
    public static final String KEY_BOOK_ID = "book_id";
    protected static final String URL_DATA = "url_data";
    protected static final String ACTION_DATA = "action_data";
    protected static final String TITLE_DATA = "title_data";
    protected static final String IS_NIGHT = "is_night";
    protected static final String FROM = "from";
    protected static final int REQUEST_CODE = 0x100808;
    protected CustomWebView mCustomWebView;
    private RefreshLoadLayout refreshLoadLayout;
    private String mAction;
    private String mBookId;
    private String mIsTmpBook;
    private boolean mIsNeight;
    private String mFrom;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    public static void show(Activity activity, String url, String action, String title) {
        show(activity, url, action, title, null);
        //activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static void show(Activity activity, String url, String action, String title, Map<String, Object> data) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(TITLE_DATA, title);
        intent.putExtra(URL_DATA, url);
        intent.putExtra(ACTION_DATA, action);

        if (data != null && !data.isEmpty()) {
            Set<String> keys = data.keySet();
            for (String key : keys) {
                intent.putExtra(key, Widget.objectToString(data.get(key)));
            }
        }
        activity.startActivity(intent);
        //activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static void show(Activity activity, String url, String action, String title, boolean isNight, String from, Map<String, Object> data) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(TITLE_DATA, title);
        intent.putExtra(URL_DATA, url);
        intent.putExtra(ACTION_DATA, action);
        intent.putExtra(IS_NIGHT, isNight);
        intent.putExtra(FROM, from);

        if (data != null && !data.isEmpty()) {
            Set<String> keys = data.keySet();
            for (String key : keys) {
                intent.putExtra(key, Widget.objectToString(data.get(key)));
            }
        }
        activity.startActivity(intent);
        //activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserEvent.getInstance().add(this);
        init();
    }

    @Override
    protected void onDestroy() {
        UserEvent.getInstance().remove(this);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCustomWebView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCustomWebView.resume();
        if (JavascriptAction.rewardVerify) {
            mCustomWebView.loadUrl("javascript:" + JavascriptAction.callBack);
            JavascriptAction.rewardVerify = false;
        }
    }

    protected void init() {
        setContentView(R.layout.activity_webview);
        initTop("", R.drawable.back, 0);
        mCustomWebView = (CustomWebView) findViewById(R.id.webview);
        mCustomWebView.init(this);
        mCustomWebView.setCloseNewBookEvent(this);
        mCustomWebView.setWebChromeClient(new WebChromeClient() {

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
        });
        refreshLoadLayout = findViewById(R.id.rll_sj);
        refreshLoadLayout.setRefreshLoadListener(new RefreshLoadLayout.SimpleRefreshLoadListener() {
            @Override
            public void onRefresh() {
                super.onRefresh();
                mCustomWebView.reload();
                refreshLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLoadLayout.finish();
                    }
                }, 600);
            }
        });
        String title = getIntent().getStringExtra(TITLE_DATA);
        String url = getIntent().getStringExtra(URL_DATA);
        mAction = getIntent().getStringExtra(ACTION_DATA);
        mBookId = getIntent().getStringExtra(KEY_BOOK_ID);
        mIsTmpBook = getIntent().getStringExtra(KEY_IS_TMPBOOK);
        mFrom = getIntent().getStringExtra(FROM);
        mIsNeight = getIntent().getBooleanExtra(IS_NIGHT, false);
        if (mIsNeight) {
            findViewById(R.id.banner_mask).setVisibility(View.VISIBLE);
            setNavigationBarColor(0xff000000);
            setWindowStatusBarColor(0xff000000);
        } else {
            findViewById(R.id.banner_mask).setVisibility(View.GONE);
        }
        if (CLOSED.equals(mAction))
            mTopBar.setCloseEnable();
        mCustomWebView.loadUrl(url);
        if (url.contains(Url.URL_AD_VIP_BASE)) {
            if (DataSHP.getUserIsBind(this) != 1) {
                runOnUiThread(() -> Action.getInstance().userCheckBind(this));
            }
        }
        progressDlg().show("正在获取数据");
        //NullPointerException
        if (StringUtil.isEmpty(mCustomWebView.getUrl())) {
            finish();
            return;
        }
        if (mCustomWebView.getUrl().contains("YYFullScreen=1")) {
            mTopBar.setVisibility(View.GONE);
        }
        if (mFrom != null && mFrom.equals("readbook"))
            setFullScreen(true);
    }

    @Override
    protected void onClickTopBarLeft(View v) {
        if (mCustomWebView.canGoBack()) {
            mCustomWebView.goBackOrForward(-1);
            return;
        }
        finish();
    }

    @Override
    public void onWebViewProgressChanged(int progress) {
        if (progress >= 100) {
//            ((PullToRefreshWebView) findViewById(R.id.webview)).onRefreshComplete();
            progressDlg().hide();
        }
    }

    @Override
    public void onPageFinished(String title, boolean canGoBack) {
        progressDlg().hide();
        if (mTopBar == null)
            return;
        if (mCustomWebView.isError()) {
            mTopBar.setVisibility(View.VISIBLE);
            return;
        } else {
            if (!StringUtil.isEmpty(mCustomWebView.getUrl()) && mCustomWebView.getUrl().contains("YYFullScreen=1")) {
                mTopBar.setVisibility(View.GONE);
            }
        }
        mTopBar.setTitle(title);
    }

    @Override
    public void onRecvError() {
        progressDlg().hide();
    }

    @Override
    public void buyBook() {
        mCustomWebView.buyBook();
    }

    public void loginSuccess() {
        if (LOGIN.equals(mAction)) {
            finish();
        }
    }

    public void bindSuccess() {
        if (BIND.equals(mAction)) {
            mCustomWebView.reload();
        } else {
            finish();
        }
    }

    public void rechargeSuccess() {
        if (RECHARGE.equals(mAction)) {
            finish();
        } else if (ACCOUNT.equals(mAction)) {
            mCustomWebView.reload();
            checkBing();
        }
    }

    public void closeView() {
        finish();
    }

    public void goBack() {
        onClickTopBarLeft(null);
    }

    private void checkBing() {
        if (DataSHP.getUserIsBind(this) != 1) {
            String dlg_content = getResources().getString(R.string.bind_dlg_content);
            TitleMessageCloseDlg.show(this, getResources().getString(R.string.bind_dlg_title), dlg_content, result -> {
                if (result) {
                    WebViewActivity.show(this, Url.URL_UCENTER_BIND, WebViewActivity.BIND, "账户绑定");
                    finish();
                }
            });
        }
    }

    @Override
    public void close() {
        if ("true".equals(mIsTmpBook) && !StringUtil.isEmpty(mBookId)) {
            ((YueYouApplication) getApplicationContext()).getMainActivity().bookshelfFrament().deleteBook(Integer.parseInt(mBookId));
        }
    }

    private void setNavigationBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(color);
        }
    }

    private void setWindowStatusBarColor(int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFullScreen(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        } else {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && mFrom != null && mFrom.equals("readbook")) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    // 2.回调方法触发本地选择文件
    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");//图片上传
//        i.setType("file/*");//文件上传
        i.setType("*/*");//文件上传
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    // 3.选择图片后处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            // Uri result = (((data == null) || (resultCode != RESULT_OK)) ? null : data.getData());
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        } else {
            //这里uploadMessage跟uploadMessageAboveL在不同系统版本下分别持有了
            //WebView对象，在用户取消文件选择器的情况下，需给onReceiveValue传null返回值
            //否则WebView在未收到返回值的情况下，无法进行任何操作，文件选择器会失效
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            } else if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(null);
                uploadMessageAboveL = null;
            }
        }
    }

    // 4. 选择内容回调到Html页面
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;

    }

}
