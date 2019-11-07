package com.yueyou.adreader.frament;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yueyou.adreader.R;
import com.yueyou.adreader.view.webview.CustomWebView;
import com.yueyou.adreader.view.webview.PullToRefreshWebView;

/**
 * Created by zy on 2018/5/23.
 */

public class WebViewFrament extends Fragment implements CustomWebView.CustomWebViewListener {
    protected CustomWebView mCustomWebView;
    private View mView;
    private String mUrl;
    private boolean mPreload;

    private String mPageName;

    public WebViewFrament setmPageName(String mPageName) {
        this.mPageName = mPageName;
        return this;
    }

    public WebViewFrament setPreload(boolean preload) {
        mPreload = preload;
        return this;
    }

    public View init(LayoutInflater inflater, ViewGroup container, String url) {
        mView = inflater.inflate(R.layout.webview_frament, container, false);
        mCustomWebView = mView.findViewById(R.id.webview);
        mCustomWebView.init(this);
        if (mPreload) {
            mCustomWebView.loadUrl(url);
        } else {
            mUrl = url;
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mUrl != null) {
            mCustomWebView.loadUrl(mUrl);
            mUrl = null;
        }
    }

    @Override
    public void onWebViewProgressChanged(int progress) {
        if (progress >= 100) {
            ((PullToRefreshWebView) mView.findViewById(R.id.webview)).onRefreshComplete();
        }
    }

    @Override
    public void onPageFinished(String title, boolean canGoBack) {

    }

    @Override
    public void onRecvError() {

    }
}
