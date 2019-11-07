package com.yueyou.adreader.service.advertisement.partner.ChuangShen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;


public class WebUtil {

    public static void destroyWebView(WebView webView) {
        if (webView == null) return;
        try {
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.stopLoading();
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.clearView();
            webView.removeAllViews();
        } catch (Exception e) {
        }
    }

    public static boolean route(Context context, final String url) {
        if (url == null) {//不处理空
            return false;
        }
        Uri uri = null;
        try {
            uri = Uri.parse(url);
        } catch (Exception e) {
        }
        if (uri == null) {//url格式错误，不处理
            return false;
        }
        if (uri.getPath().endsWith(".apk")) {//手动处理apk下载
            handleAPK(context, url);
            return true;
        } else if (url.startsWith("http")) {//不处理http/https
            return false;
        } else {//其余的扔出去
            handleOther(context, url);
            return true;
        }
    }

    public static void handleAPK(final Context context, final String url) {
        DownService.invoke(context, url, "", 0);
    }

    private static void handleOther(final Context context, final String url) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }
}
