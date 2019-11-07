package com.yueyou.adreader.view.dlg;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yueyou.adreader.R;
import com.yueyou.adreader.view.Event.UserEvent;
import com.yueyou.adreader.view.webview.CustomWebView;
import com.yueyou.adreader.view.webview.PullToRefreshWebView;


/**
 * Created by zy on 2017/4/1.
 */

public class WebViewDlg{
    private PopupWindow mPopupWindow;
    private View mPopupWindowView;
    private CustomWebView mCustomWebView;
    private WebViewDlgListener mWebViewDlgListener;
    public static WebViewDlg show(Activity activity, String title, String url, WebViewDlgListener webViewDlgListener){
        WebViewDlg webViewDlg = new WebViewDlg();
        webViewDlg.initWindow(activity, title, url, webViewDlgListener);
        return webViewDlg;
    }

    protected void initWindow(Activity activity, String title, String url, WebViewDlgListener webViewDlgListener){
        mWebViewDlgListener = webViewDlgListener;
        View rootView = activity.getWindow().getDecorView();
        mPopupWindowView = activity.getLayoutInflater().inflate(R.layout.webview_dlg, null);
        mPopupWindow = new PopupWindow(mPopupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindow.showAtLocation(rootView, Gravity.TOP, 0, 0);
        setWebviewRect(title);
        UserEvent.getInstance().add(this);
        mPopupWindow.setOnDismissListener(()->{
            UserEvent.getInstance().remove(this);
        });
        mPopupWindowView.findViewById(R.id.close).setOnClickListener((View v)->{
            if (mWebViewDlgListener != null)
                mWebViewDlgListener.close();
            mPopupWindow.dismiss();
        });
        mPopupWindowView.findViewById(R.id.back).setOnClickListener((View v)->{
            mCustomWebView.goBack();
        });
        ((TextView)mPopupWindowView.findViewById(R.id.title)).setText(title);
        mCustomWebView = ((PullToRefreshWebView)mPopupWindowView.findViewById(R.id.webview)).getRefreshableView();
        mCustomWebView.init(new CustomWebView.CustomWebViewListener() {
            @Override
            public void onWebViewProgressChanged(int progress) {
                if (progress >= 100){
                    ((PullToRefreshWebView)mPopupWindowView.findViewById(R.id.webview)).onRefreshComplete();
                }
            }

            @Override
            public void onPageFinished(String title, boolean canGoBack) {
                ((TextView)mPopupWindowView.findViewById(R.id.title)).setText(title);
                if (mCustomWebView.canGoBack()){
                    mPopupWindowView.findViewById(R.id.back).setVisibility(View.VISIBLE);
                }else {
                    mPopupWindowView.findViewById(R.id.back).setVisibility(View.GONE);
                }
            }

            @Override
            public void onRecvError() {

            }
        });
        mCustomWebView.loadUrl(url);
    }

    public void hideTopBar() {
        mPopupWindowView.findViewById(R.id.top_bar).setVisibility(View.GONE);
    }

    public void transparent() {
        mCustomWebView.setBackgroundColor(0);
//        mCustomWebView.getBackground().setAlpha(0);
        mCustomWebView.getBackground().mutate().setAlpha(0);
    }

    public void setWebviewRect(String title) {
        LinearLayout.LayoutParams bgLp = (LinearLayout.LayoutParams) mPopupWindowView.findViewById(R.id.bg).getLayoutParams();
        LinearLayout.LayoutParams bodyLp = (LinearLayout.LayoutParams) mPopupWindowView.findViewById(R.id.body).getLayoutParams();
        if("充值".equals(title)) {
            bgLp.weight = 1.5f;
            bodyLp.weight = 1.0f ;
            mPopupWindowView.findViewById(R.id.bg).setLayoutParams(bgLp);
            mPopupWindowView.findViewById(R.id.body).setLayoutParams(bodyLp);
        } else if("购买".equals(title)){
            bgLp.weight = 1.0f;
            bodyLp.weight = 1.5f ;
            mPopupWindowView.findViewById(R.id.bg).setLayoutParams(bgLp);
            mPopupWindowView.findViewById(R.id.body).setLayoutParams(bodyLp);
        }
    }

    public void buy() {
        UserEvent.getInstance().remove(this);
        mPopupWindow.dismiss();
    }

    public void rechargeSuccess() {
        UserEvent.getInstance().remove(this);
        mPopupWindow.dismiss();
    }

    public void closeView() {
        UserEvent.getInstance().remove(this);
        mPopupWindow.dismiss();
    }

    public interface WebViewDlgListener{
        void close();
    }
}
