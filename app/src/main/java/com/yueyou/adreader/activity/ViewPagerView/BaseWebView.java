package com.yueyou.adreader.activity.ViewPagerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.refreshload.RefreshLoadLayout;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.view.webview.CustomWebView;
import com.yueyou.adreader.view.webview.PullToRefreshWebView;

public class BaseWebView extends ViewPagerBase implements CustomWebView.CustomWebViewListener {
    protected CustomWebView mCustomWebView;
    protected RefreshLoadLayout refreshLoadLayout;
    private String mUrl;
    private TextView mReload;
    private LinearLayout mErrorLayout;

    public BaseWebView(final Context context, boolean preload, String url) {
        super(context);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.webview_frament, (ViewGroup) this);
        mCustomWebView = findViewById(R.id.webview);
        mErrorLayout = findViewById(R.id.ll_error);
        refreshLoadLayout = findViewById(R.id.rll_sj);
        mCustomWebView.init(this,mErrorLayout,refreshLoadLayout);

        mReload = findViewById(R.id.tv_reload_fram);
        mReload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("mReload");
                mCustomWebView.reload();
            }
        });


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
        if (preload) {
            mCustomWebView.loadUrl(url);
        } else {
            mUrl = url;
        }
    }

    @Override
    public boolean enter() {
        if (!super.enter())
            return false;
        if (mUrl != null) {
            mCustomWebView.loadUrl(mUrl);
            mUrl = null;
        }
        return true;
    }

    @Override
    public void onWebViewProgressChanged(int progress) {
        if (progress >= 100) {
//            ((PullToRefreshWebView)findViewById(R.id.webview)).onRefreshComplete();
        }
    }

    @Override
    public void onPageFinished(String title, boolean canGoBack) {

    }

    @Override
    public void onRecvError() {

    }
}
