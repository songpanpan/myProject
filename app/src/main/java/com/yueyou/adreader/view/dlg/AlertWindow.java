package com.yueyou.adreader.view.dlg;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebBackForwardList;
import android.widget.PopupWindow;

import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.WebViewActivity;
import com.yueyou.adreader.view.Event.UserEvent;
import com.yueyou.adreader.view.webview.CustomWebView;
import com.yueyou.adreader.view.webview.PullToRefreshWebView;

/**
 * Created by zy on 2017/4/1.
 */

public class AlertWindow {
    public static AlertWindow mAlertWindow = null;
    private PopupWindow mPopupWindow;
    private View mPopupWindowView;
    private CustomWebView mCustomWebView;
    private boolean mPageLoaded;
    private AlertWindowListener mAlertWindowListener;

    public interface AlertWindowListener {
        boolean canShow();

        void show();

        void hide();
    }

    public static AlertWindow show(Activity activity, String url, AlertWindowListener alertWindowListener) {
        AlertWindow alertWindow = new AlertWindow();
        alertWindow.initWindow(activity, url, alertWindowListener);
        return alertWindow;
    }

    protected void initWindow(Activity activity, String url, AlertWindowListener alertWindowListener) {
        mAlertWindow = this;
        mAlertWindowListener = alertWindowListener;
        mPopupWindowView = activity.getLayoutInflater().inflate(R.layout.alertwindow_dlg, null);
        addToPopupWindow(activity);
        mPopupWindowView.findViewById(R.id.close).setOnClickListener((View v) -> {
            mPopupWindow.dismiss();
        });
        mCustomWebView = ((PullToRefreshWebView) mPopupWindowView.findViewById(R.id.webview)).getRefreshableView();
        mCustomWebView.init(new CustomWebView.CustomWebViewListener() {
            @Override
            public void onWebViewProgressChanged(int progress) {
                if (progress >= 100) {
                    ((PullToRefreshWebView) mPopupWindowView.findViewById(R.id.webview)).onRefreshComplete();
                }
            }

            @Override
            public void onPageFinished(String title, boolean canGoBack) {
                activity.runOnUiThread(() -> {
                    mPageLoaded = true;
                    if (mAlertWindowListener != null && !mAlertWindowListener.canShow())
                        return;
                    show();
                });
            }

            @Override
            public void onRecvError() {

            }
        });
        mCustomWebView.setUrlInterceptInterface((String webUrl) -> {
            WebBackForwardList webBackForwardList = mCustomWebView.copyBackForwardList();
            if (webBackForwardList.getSize() > 0) {
                activity.runOnUiThread(() -> {
                    WebViewActivity.show(activity, webUrl, WebViewActivity.CLOSED, "");
                });
                return true;
            }
            return false;
        });
        mPageLoaded = false;
        mCustomWebView.loadUrl(url);
        mCustomWebView.setBackgroundColor(0);
//        mCustomWebView.getBackground().setAlpha(0);
        mCustomWebView.getBackground().mutate().setAlpha(0);
    }

    private void addToPopupWindow(Activity activity) {
        if (mPopupWindow != null)
            return;
        mPopupWindow = new PopupWindow(mPopupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        UserEvent.getInstance().add(this);
        mPopupWindow.setOnDismissListener(() -> {
            mAlertWindow = null;
            UserEvent.getInstance().remove(this);
            if (mAlertWindowListener != null)
                mAlertWindowListener.hide();
        });
    }

    public synchronized void show() {
        if (mPopupWindow.isShowing() || !mPageLoaded) {
            return;
        }
        Activity activity = ((Activity) mPopupWindowView.getContext());
        if (activity == null || activity.isFinishing()) return;
        View rootView = activity.getWindow().getDecorView();
        mPopupWindow.showAtLocation(rootView, Gravity.TOP, 0, 0);
        if (mAlertWindowListener != null)
            mAlertWindowListener.show();
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

    public interface WebViewDlgListener {
        void close();
    }
}


