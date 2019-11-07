package com.yueyou.adreader.view.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.refreshload.RefreshLoadLayout;
import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.service.advertisement.adObject.AdWebViewBanner;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.UserInfoWithRedirectUrl;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.Event.CloseNewBookEvent;

/**
 * Created by zy on 2017/4/7.
 */

public class CustomWebView extends WebView implements NestedScrollingChild {
    private CustomWebViewListener mCustomWebViewListener;
    private boolean mClearHistory;
    private View mErrorToastView;
    private View mErrorReload;
    private JavascriptAction mJavascriptAction;
    private UrlInterceptInterface mUrlInterceptInterface;
    private View mAdView;
    private AdWebViewBanner mAdWebViewBanner;
    private boolean mDragEvent;
    private float mDownTouchX;
    private float mDownTouchY;
    private int mTouchSlop;
    int mTopBounds;
    int mBottomBounds;
    int mAdViewHeight;
    private NestedScrollingChildHelper mChildHelper;

    private int mLastMotionY;
    private int mNestedYOffset;
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];

    public CustomWebView(Context context) {
        super(context);
        init();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mErrorToastView = ((Activity) context).getLayoutInflater().inflate(R.layout.webview_error, null);
        this.addView(mErrorToastView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0, 0));
        mErrorReload = mErrorToastView.findViewById(R.id.tv_reload);
        LogUtil.e("CustomWebView mErrorReload:" + mErrorReload);
        mErrorToastView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("CustomWebView onclick");
                reload();
            }
        });
        mErrorReload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("CustomWebView onclick");
                reload();
            }
        });
        mErrorToastView.setVisibility(View.GONE);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    private void init() {
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    private boolean mTouchAdView = false;

    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        if (!mDragEvent && mAdView != null && event.getAction() == MotionEvent.ACTION_UP) {
            if (mTouchAdView) {
                mTouchAdView = false;
                return true;
            }
            int loction[] = new int[2];
            mAdView.getLocationInWindow(loction);
            if (loction[1] > event.getRawY() || (loction[1] + mAdView.getHeight() < event.getRawY()))
                return super.onTouchEvent(event);
            mTouchAdView = true;
            MotionEvent evenDownt = MotionEvent.obtain(System.currentTimeMillis(),
                    System.currentTimeMillis() + 100, MotionEvent.ACTION_DOWN, event.getX(), event.getY(), 0);
            dispatchTouchEvent(evenDownt);
            MotionEvent eventUp = MotionEvent.obtain(System.currentTimeMillis(),
                    System.currentTimeMillis() + 100, MotionEvent.ACTION_UP, event.getX(), event.getY(), 0);
            dispatchTouchEvent(eventUp);
            evenDownt.recycle();
            eventUp.recycle();
            return true;
        }
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            mDragEvent = false;
            mDownTouchY = event.getY();
            mDownTouchX = event.getX();
        } else if (MotionEvent.ACTION_MOVE == event.getAction()) {
            float y = event.getY();
            float dy = y - mDownTouchY;
            float yDiff = Math.abs(dy);
            float xDiff = Math.abs(event.getX() - mDownTouchX);
            if (yDiff > mTouchSlop && yDiff > xDiff) {
                mDragEvent = true;
            }
        }
        boolean result = false;
        MotionEvent trackedEvent = MotionEvent.obtain(event);

        final int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }

        int y = (int) event.getY();

        event.offsetLocation(0, mNestedYOffset);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                result = super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastMotionY - y;

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    trackedEvent.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }

                int oldY = getScrollY();
                mLastMotionY = y - mScrollOffset[1];
                int newScrollY = Math.max(0, oldY + deltaY);
                deltaY -= newScrollY - oldY;
                if (dispatchNestedScroll(0, newScrollY - deltaY, 0, deltaY, mScrollOffset)) {
                    mLastMotionY -= mScrollOffset[1];
                    trackedEvent.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }
                if (mScrollConsumed[1] == 0 && mScrollOffset[1] == 0) {
                    trackedEvent.recycle();
                    result = super.onTouchEvent(trackedEvent);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                result = super.onTouchEvent(event);
                break;
        }
        return result;

//        return super.onTouchEvent(event);
    }

    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        if (mTouchAdView) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                mTouchAdView = false;
            return false;
        }
        return true;
    }
//
//    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
//        if (mAdView == null)
//            return false;
//        int action = motionEvent.getAction();
//        if (action == MotionEvent.ACTION_DOWN)
//            return false;
//        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
//            return mDragEvent;
//        }
//        if (action != MotionEvent.ACTION_DOWN && mDragEvent) {
//            return true;
//        }
//        switch (action) {
//            case MotionEvent.ACTION_MOVE:
//                final float y = motionEvent.getY();
//                final float dy = y - mDownTouchY;
//                final float yDiff = Math.abs(dy);
//                final float xDiff = Math.abs(motionEvent.getX() - mDownTouchX);
//                if (yDiff > mTouchSlop && yDiff > xDiff) {
//                    mDownTouchY = y;
//                    mDragEvent = true;
//                    return false;
//                }
//                break;
//            case MotionEvent.ACTION_DOWN:
//                mDownTouchY = motionEvent.getY();
//                mDownTouchX = motionEvent.getX();
//                mDragEvent = false;
//                break;
//            default:break;
//        }
//        return false;
//    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mAdView == null)
            return;
        resetAdViewHeight();
    }

    public void pause() {
        if (mAdView != null)
            mAdWebViewBanner.release();
    }

    public void resume() {
        if (mAdView != null)
            mAdWebViewBanner.resume();
    }

    public void loadAd(int x, int y, int width, int height, int topBounds, int bottomBounds) {
        ((Activity) getContext()).runOnUiThread(() -> {
            if (mAdView == null) {
                mAdView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.ad_webview_banner_container, null);
                this.addView(mAdView, new LayoutParams(Widget.dip2px(getContext(), width), Widget.dip2px(getContext(), height),
                        Widget.dip2px(getContext(), x), Widget.dip2px(getContext(), y)));
                mAdWebViewBanner = new AdWebViewBanner();
                mAdWebViewBanner.init((ViewGroup) mAdView, width, height);
                mAdWebViewBanner.load();
                mTopBounds = Widget.dip2px(getContext(), topBounds);
                mBottomBounds = Widget.dip2px(getContext(), bottomBounds);
                mAdViewHeight = Widget.dip2px(getContext(), height);
            } else {
                mAdView.setY(Widget.dip2px(getContext(), y));
                mAdView.setX(Widget.dip2px(getContext(), x));
                resetAdViewHeight();
            }
        });

    }

    public void refreshAdOffset(int x, int y) {
        if (mAdView == null)
            return;
        ((Activity) getContext()).runOnUiThread(() -> {
            mAdView.setY(Widget.dip2px(getContext(), y));
            resetAdViewHeight();
        });
    }

    private void resetAdViewHeight() {
        if (mAdView.getY() - getScrollY() + mAdView.getHeight() < mTopBounds && mAdView.getHeight() >= mAdViewHeight)
            return;
        WebView.LayoutParams layoutParams = (WebView.LayoutParams) mAdView.getLayoutParams();
        layoutParams.height = mTopBounds - (int) (mAdView.getY() - getScrollY());
        if (layoutParams.height > mAdViewHeight)
            layoutParams.height = mAdViewHeight;
        if (layoutParams.height < 0)
            layoutParams.height = 0;
        mAdView.setLayoutParams(layoutParams);
    }

    public void setCloseNewBookEvent(CloseNewBookEvent closeNewBookEvent) {
        this.mJavascriptAction.setCloseNewBookEvent(closeNewBookEvent);
    }

    @Override
    public void loadUrl(String url) {
        LogUtil.e("1025url:"+url);
//        if (url.startsWith(Url.URL_BASE)||url.contains(Url.URL_MATCH)) {
        if (!url.startsWith("javascript")) {
            url = Url.signUrl(this.getContext(), url);
//        } else if (url.startsWith(Bi.BI_URL_BASE)) {
//            url = Bi.signUrl(getContext(), url);
//        }
            Utils.logNoTag("url %s", url);
        }
        super.loadUrl(url);
    }

    public void postUrl(String url, String data) {
        super.postUrl(url, data.getBytes());
    }

    public boolean isError() {
        return mErrorToastView.getVisibility() == VISIBLE;
    }

    public void init(CustomWebViewListener customWebViewListener) {
        mCustomWebViewListener = customWebViewListener;
        WebSettings settings = getSettings();
        settings.setAppCacheEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheMaxSize(200 * 1024 * 1024);
        settings.setDomStorageEnabled(true);
        settings.setSaveFormData(false);
        mJavascriptAction = new JavascriptAction(this);
        this.addJavascriptInterface(mJavascriptAction, "nativeObj");
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isNotLoadUrl(url)) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //ToastDlg.show(getContext(), "shouldOverrideUrlLoading");
                if (isNotLoadUrl(request.getUrl().toString())) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mErrorToastView.setVisibility(View.GONE);
//                if (url.contains("/bookStore")||url.contains("/h5")) {
//                    ThirdAnalytics.onEventPageViewBookStore(view.getContext());
//                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mCustomWebViewListener.onPageFinished(view.getTitle(), view.canGoBack());
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (errorCode == -2) {
                    mErrorToastView.setVisibility(View.VISIBLE);
                } else {
                    mErrorToastView.setVisibility(View.GONE);
                }
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (error.getErrorCode() == -2) {
                    mErrorToastView.setVisibility(View.VISIBLE);
                } else {
                    mErrorToastView.setVisibility(View.GONE);
                }
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                if (mClearHistory) {
                    mClearHistory = false;
                    clearHistory();
                }
            }

        });
        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                ThirdAnalytics.setJavascriptMonitor(view);
                mCustomWebViewListener.onWebViewProgressChanged(newProgress);
            }
        });
        this.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                ((Activity) CustomWebView.this.getContext()).startActivity(intent);
            }
        });
    }

    public void init(CustomWebViewListener customWebViewListener, LinearLayout mErrorLayout, RefreshLoadLayout refreshLoadLayout) {
        mCustomWebViewListener = customWebViewListener;
        WebSettings settings = getSettings();
        settings.setAppCacheEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheMaxSize(200 * 1024 * 1024);
        settings.setDomStorageEnabled(true);
        settings.setSaveFormData(false);
        mJavascriptAction = new JavascriptAction(this);
        this.addJavascriptInterface(mJavascriptAction, "nativeObj");
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isNotLoadUrl(url)) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //ToastDlg.show(getContext(), "shouldOverrideUrlLoading");
                if (isNotLoadUrl(request.getUrl().toString())) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mErrorToastView.setVisibility(View.GONE);
                mErrorLayout.setVisibility(View.GONE);
                refreshLoadLayout.setVisibility(View.VISIBLE);
//                if (url.contains("/bookStore")||url.contains("/h5")) {
//                    ThirdAnalytics.onEventPageViewBookStore(view.getContext());
//                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mCustomWebViewListener.onPageFinished(view.getTitle(), view.canGoBack());
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (errorCode == -2) {
                    mErrorToastView.setVisibility(View.VISIBLE);
                    mErrorLayout.setVisibility(View.VISIBLE);
                    refreshLoadLayout.setVisibility(View.GONE);
                } else {
                    mErrorToastView.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.GONE);
                    refreshLoadLayout.setVisibility(View.VISIBLE);
                }
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (error.getErrorCode() == -2) {
                    mErrorToastView.setVisibility(View.VISIBLE);
                    mErrorLayout.setVisibility(View.VISIBLE);
                    refreshLoadLayout.setVisibility(View.GONE);
                } else {
                    mErrorToastView.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.GONE);
                    refreshLoadLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                if (mClearHistory) {
                    mClearHistory = false;
                    clearHistory();
                }
            }

        });
        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                ThirdAnalytics.setJavascriptMonitor(view);
                mCustomWebViewListener.onWebViewProgressChanged(newProgress);
            }
        });
        this.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                ((Activity) CustomWebView.this.getContext()).startActivity(intent);
            }
        });
    }

    public void setUrlInterceptInterface(UrlInterceptInterface urlInterceptInterface) {
        this.mUrlInterceptInterface = urlInterceptInterface;
    }

    private boolean isNotLoadUrl(String url) {
        if (mUrlInterceptInterface != null) {
            return mUrlInterceptInterface.intercept(url);
        }
        if (url.startsWith("objc://loginSuccess//")) {
            String jsonStr = url.substring("objc://loginSuccess//".length());
            UserInfoWithRedirectUrl userInfoWithRedirectUrl = (UserInfoWithRedirectUrl) Widget.stringToObject(jsonStr, UserInfoWithRedirectUrl.class);
            if (userInfoWithRedirectUrl != null) {
                DataSHP.saveUserInfo(getContext(), userInfoWithRedirectUrl.getUserId(), userInfoWithRedirectUrl.getToken());
                ((Activity) getContext()).runOnUiThread(() -> {
//                    loadUrl(userInfoWithRedirectUrl.getUrl());
                });
            }
            return true;
        } else if (url.startsWith("weixin://wap/pay?")
                || (!url.startsWith("http://")
                && !url.startsWith("https://"))) {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                ((Activity) getContext()).startActivity(intent);
                return true;
            } catch (Exception e) {

            }
        }
        return false;
    }

    public void buyBook() {
        mJavascriptAction.buyBook();
    }

    public interface CustomWebViewListener {
        void onWebViewProgressChanged(int progress);

        void onPageFinished(String title, boolean canGoBack);

        void onRecvError();
    }

    public interface UrlInterceptInterface {
        boolean intercept(String url);
    }


    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}