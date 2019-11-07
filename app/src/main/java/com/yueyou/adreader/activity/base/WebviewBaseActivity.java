package com.yueyou.adreader.activity.base;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.yueyou.adreader.view.dlg.ProgressDlg;
import com.yueyou.adreader.view.webview.CustomWebView;



/**
 * Created by zy on 2017/4/12.
 */

public class WebviewBaseActivity extends BaseActivity implements CustomWebView.CustomWebViewListener {
    protected CustomWebView mCustomWebView;
    private ProgressDlg mProgressDlg;
    private Handler mHandler = new Handler() {
        public void handleMessage(final Message message) {

        }
    };
    @Override
    public void setContentView(int layoutResID){
        super.setContentView(layoutResID);
        //###mCustomWebView = ((PullToRefreshWebView)findViewById(R.id.webview)).getRefreshableView();
        mCustomWebView.init(this);
        mProgressDlg = new ProgressDlg(this);
    }

    @Override
    protected void onClickTopBarLeft(View v){
        if (mCustomWebView.canGoBack()) {
            mCustomWebView.goBack();
            return;
        }
        finish();
    }

    @Override
    public void onWebViewProgressChanged(int progress){
        if (progress >= 100){
           //### ((PullToRefreshWebView)findViewById(R.id.webview)).onRefreshComplete();
        }
    }

    @Override
    public void onRecvError(){
        //ToastDlg.show(this, "网络未连接");
    }

    @Override
    public void onPageFinished(String tilte, boolean canGoBack){
        mTopBar.setTitle(tilte);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0)
            return;
    }
}
