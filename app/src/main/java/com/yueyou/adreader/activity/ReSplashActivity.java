package com.yueyou.adreader.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.base.BaseActivity;
import com.yueyou.adreader.service.Action;
import com.yueyou.adreader.service.UpgradeEngine;
import com.yueyou.adreader.service.analytics.AnalyticsEngine;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.ReSplashPreView;
import com.yueyou.adreader.view.dlg.AlertWindow;
import com.yueyou.adreader.view.dlg.MessageDlg;

public class ReSplashActivity extends BaseActivity {
    public static final String NO_FROM_INIT = "no_from_init";
    private UpgradeEngine mUpgradeEngine = new UpgradeEngine();
    private boolean mLoadAd;
    private ReSplashPreView mMainPreView;
    private String mBookName = "";
    private String mBookId = "";
    private String mChapterId = "";
    private String mSiteId = "";
    private boolean mDeviceActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("blank screen", "blank screen onCreate: " + this);
//        AdEngine.getInstance().setContext(this);
        mDeviceActivity = DataSHP.getSexType(this) == null;
        setContentView(R.layout.activity_resplash);


//        DBEngine.getInstens(this);
        mMainPreView = findViewById(R.id.main_pre_view);
        mMainPreView.setListener(mMainPreViewListener);
        if (!mDeviceActivity) {
            getUri(getIntent());
        }
        setFullScreen(true);

    }

    private void getActivateInfo() {

        String comment = Widget.getTextFromClip(this);
        if (Widget.isBlank(comment))
            comment = Widget.getApkCommentInfo(this);
        try {
            String[] bookInfo = comment.split("<;>");
            mBookName = bookInfo[0];
            mBookId = bookInfo[1];
            mChapterId = bookInfo[2];
            mSiteId = bookInfo[3];
        } catch (Exception e) {
            e.printStackTrace();
            mBookName = "";
            mBookId = "";
            mChapterId = "";
            mSiteId = "";
        }
    }

    private ReSplashPreView.MainPreViewListener mMainPreViewListener = new ReSplashPreView.MainPreViewListener() {
        @Override
        public synchronized void finish(boolean resume) {
            ReSplashActivity.this.finish();
            if (resume && !mDeviceActivity)
                return;
            try {
                setFullScreen(false);
                if (mDeviceActivity) {
                    getBuildinBook();
                    AnalyticsEngine.activate(ReSplashActivity.this, mSiteId, mBookId, mBookName);
                    mDeviceActivity = false;
                }
                AnalyticsEngine.login(ReSplashActivity.this);
                mUpgradeEngine.check(ReSplashActivity.this);

                if (AlertWindow.mAlertWindow != null) {
                    AlertWindow.mAlertWindow.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public synchronized void loginFinish() {
            if (mDeviceActivity) {
                runOnUiThread(() -> {
                    getBuildinBook();
                });
            }
        }
    };

    private synchronized void getBuildinBook() {
        getActivateInfo();
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
    public void onPause() {
        super.onPause();
        Log.i("blank screen", "blank screen onPause");
        Utils.logNoTag("onPause");
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("blank screen", "blank screen onNewIntent");
        setIntent(intent);
        if (getUri(intent))
            return;
        if (getIntent().getBooleanExtra(NO_FROM_INIT, false)) {
            mMainPreView.resumeShow();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("blank screen", "blank screen onResume");
        Utils.logNoTag("onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("blank screen", "blank screen onRestart");
        Utils.logNoTag("onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("blank screen", "blank screen onStop");
        Utils.logNoTag("onStop");
    }

    @Override
    protected void onDestroy() {
        Log.i("blank screen", "blank screen onDestroy");
        if (AlertWindow.mAlertWindow != null) {
            AlertWindow.mAlertWindow.closeView();
        }
        super.onDestroy();
        Utils.logNoTag("onDestroy");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UpgradeEngine.REQUEST_CODE_APP_INSTALL) {
            mUpgradeEngine.callBackInstall(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mMainPreView != null)
            mMainPreView.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public boolean getUri(Intent intent) {
        String action = intent.getAction();
        if (!Intent.ACTION_VIEW.equals(action))
            return false;
        Uri uri = intent.getData();
        if (uri == null)
            return false;
        String path = uri.getPath();
        if (path == null)
            return false;
        if (path.equals("/start")) {
            return false;
        } else if (path.equals("/read")) {
            Utils.logNoTag("uri -->  %s", uri);
            String text = uri.getQueryParameter("bookInfo");
            text = Widget.decodeClipText(text);
            if (Widget.isBlank(text))
                return false;
            String[] bookInfo = text.split("<;>");
            if (bookInfo.length < 4)
                return false;
            String bookId = bookInfo[1];
            String chapterId = bookInfo[2];
            new Thread(() -> {
                Looper.prepare();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Action.getInstance().getBook(this, bookId, chapterId)) {
                    runOnUiThread(() -> {
                    });
                }
            }).start();
            return true;
        } else if (path.equals("/web")) {
            String url = uri.getQueryParameter("url");
            if (Widget.isBlank(url))
                return false;
            WebViewActivity.show(this, url, WebViewActivity.UNKNOW, "");
            return true;
        } else if (path.equals("/bookStore/recommend")) {
            mTopBar.setTitle(getResources().getString(R.string.main_tab_title_book_store));
            return true;
        }
        return false;
    }

    private AlertWindow.AlertWindowListener mAlertWindowListener = new AlertWindow.AlertWindowListener() {
        @Override
        public boolean canShow() {
            try {
                return mMainPreView.getVisibility() != View.VISIBLE
                        && findViewById(R.id.sex).getVisibility() != View.VISIBLE;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public void show() {

        }

        @Override
        public void hide() {

        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!mLoadAd && hasFocus) {
            if (!mDeviceActivity) {
            }
            mLoadAd = true;
        }
    }


    @Override
    public void onClickTopBarLeft(View view) {
        try {
            if (mMainPreView.getVisibility() == View.VISIBLE)
                return;
        } catch (Exception e) {
            return;
        }

        MessageDlg.show(this, getResources().getString(R.string.tip_confirm_exit), (boolean result) -> {
            if (result) {
                //System.exit(0);
                finish();
            }
        });

    }

}
