package com.yueyou.adreader.service.advertisement.partner.ChuangShen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.yueyou.adreader.R;

public class AdWebViewActivity extends Activity implements DownloadListener {
    private static final String PARAMS_URL = "PARAMS_URL";
    private String originUrl;
    private int backCount = 0;

    private TextView mTitleView;
    private ImageView mBackView;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        originUrl = getIntent().getStringExtra(PARAMS_URL);
        setContentView(R.layout.activity_ad_webview);
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebView.loadUrl(originUrl);
    }

    @Override
    protected void onDestroy() {
        WebUtil.destroyWebView(mWebView);
        super.onDestroy();
    }

    private void initView() {
        //
        mWebView = findViewById(R.id.mWebView);
        initWebView(mWebView);
        //
        mTitleView = findViewById(R.id.mTitleView);
        mBackView = findViewById(R.id.mBackImageView);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBack();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initWebView(WebView wv) {
//        wv.clearCache(true);
//        wv.clearHistory();
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setAppCacheEnabled(true);
        wv.getSettings().setDatabaseEnabled(true);
        wv.getSettings().setGeolocationEnabled(true);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.setWebViewClient(new YueYouWebviewClient());
        wv.setWebChromeClient(new ChouyuWebChromeClient());
        wv.setDownloadListener(this);
    }

    /**
     * 处理关闭事件，包括关闭按钮和按下回退键
     */
    private void handleBack() {
        backCount++;
//        if (backCount == 1) {
//            mBackView.setImageResource(R.drawable.close);
//            mWebView.loadUrl(originUrl);
//        } else {
            finish();
//        }
    }


    public static void invoke(Context context, String url) {
        try {
            if (!WebUtil.route(context, url)) {
                Intent intent = new Intent(context, AdWebViewActivity.class)
                        .putExtra(PARAMS_URL, url).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if (mimetype.equals("application/vnd.android.package-archive")) {
            WebUtil.handleAPK(this, url);
        }
    }

    class YueYouWebviewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            if (WebUtil.route(AdWebViewActivity.this, url)) {
                return true;
            } else {
                return super.shouldOverrideUrlLoading(view, url);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }

    class ChouyuWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mTitleView.setText(title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    }
}
