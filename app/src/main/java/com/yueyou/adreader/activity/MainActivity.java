package com.yueyou.adreader.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;


import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.ViewPagerView.Adapter;
import com.yueyou.adreader.activity.ViewPagerView.BookSelectedView;
import com.yueyou.adreader.activity.ViewPagerView.BookStoreView;
import com.yueyou.adreader.activity.ViewPagerView.BookshelfView;
import com.yueyou.adreader.activity.ViewPagerView.PersionalView;
import com.yueyou.adreader.activity.base.BaseActivity;
import com.yueyou.adreader.service.Action;
import com.yueyou.adreader.service.UpgradeEngine;
import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.service.analytics.AnalyticsEngine;
import com.yueyou.adreader.service.db.DBEngine;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.RedSpotBean;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.Event.UserEvent;
import com.yueyou.adreader.view.MainPreView;
import com.yueyou.adreader.view.ToolBar;
import com.yueyou.adreader.view.ViewPager.ZYViewPager;
import com.yueyou.adreader.view.dlg.AlertWindow;
import com.yueyou.adreader.view.dlg.MessageDlg;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    public static final String NO_FROM_INIT = "no_from_init";
    private ZYViewPager mViewPager;
    private Adapter mAdapter;
    private UpgradeEngine mUpgradeEngine = new UpgradeEngine();
    private boolean mLoadAd;
    private MainPreView mMainPreView;
    private String mBookName = "";
    private String mBookId = "";
    private String mChapterId = "";
    private String mSiteId = "";
    private boolean mDeviceActivity;
    private BookshelfView mBookshelfView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("blank screen", "blank screen onCreate: " + this);
////        打印推送信息
//        Intent intent = new Intent();
//        intent.setComponent(new ComponentName("com.yueyou.adreader", "com.yueyou.adreader.activity.MainActivity"));
//        intent.putExtra("t", "tab");
////        intent.putExtra("data", "龙血战神:119793:0");"至尊修罗:203001:0"
//        intent.putExtra("d", "4");
//        intent.setAction("android.intent.action.oppopush");
//        String intentStr = intent.toUri(Intent.URI_INTENT_SCHEME);
//        LogUtil.e("intentStr   " + intentStr);
//      intent:#Intent;action=android.intent.acti61.50.130.242:20185/h5/act/signinon.oppopush;component=com.yueyou.adreader/.activity.MainActivity;S.t=tab;S.data=2;end
//      intent:#Intent;action=android.intent.action.oppopush;component=com.yueyou.adreader/.activity.MainActivity;S.t=tab;S.data=4;end

        mDeviceActivity = DataSHP.getSexType(this) == null;
        setContentView(R.layout.activity_main);
        initTop("", 0, R.drawable.search);
        mBookshelfView = new BookshelfView(this);
        mTopBar.setTitleSizeMax();
        mTopBar.setTitle("");
        mTopBar.findViewById(R.id.icon_bookshelf).setVisibility(View.VISIBLE);
        mTopBar.findViewById(R.id.iv_top_sign).setVisibility(View.VISIBLE);
        mTopBar.findViewById(R.id.iv_red_dot).setVisibility(View.VISIBLE);

        DBEngine.getInstens(this);
        findViewById(R.id.tool_bar_book_select).setOnClickListener(mOnClickListener);
        findViewById(R.id.tool_bar_book_store).setOnClickListener(mOnClickListener);
        findViewById(R.id.tool_bar_bookshelf).setOnClickListener(mOnClickListener);
        findViewById(R.id.tool_bar_persional).setOnClickListener(mOnClickListener);
        mMainPreView = findViewById(R.id.main_pre_view);
        mMainPreView.setListener(mMainPreViewListener);
        if (!mDeviceActivity) {
            initViewPager(true, 0);
            getUri(getIntent());
        }
        setFullScreen(true);
        try {
            String dataType = getIntent().getStringExtra("t");
            String data = getIntent().getStringExtra("d");
            dealWithPushData(dataType, data);
        } catch (Exception e) {

        }
    }

    public void dealWithPushData(String dataType, String data) {
        LogUtil.e("MainActivity dataType:" + dataType + " data:" + data);
        try {
            if (dataType != null && data != null && data.length() > 0) {
                if (dataType.equals("web")) {
                    WebViewActivity.show(this, data, WebViewActivity.UNKNOW, "");
                } else if (dataType.equals("read")) {
                    String[] bookInfo = data.split(":");
                    mBookId = bookInfo[0];
                    mChapterId = bookInfo[1];
                    if (mBookId != null && mChapterId != null) {
                        new Thread(() -> {
                            Looper.prepare();
                            if (Action.getInstance().getBookDetail(this, mBookId, mChapterId)) {
                                runOnUiThread(() -> {
                                    mBookshelfView.readBook(0);
                                });
                                Action.getInstance().downloadCover(this, mBookshelfView.getBookByIndex(0).getBookCover(),
                                        mBookshelfView.getBookByIndex(0).getBookId(), false);
                            }
                        }).start();
                    }
                } else if (dataType.equals("tab")) {
                    int itemNum = Integer.parseInt(data);
                    if (itemNum > 0 && itemNum < 5) {
                        initViewPager(true, (itemNum - 1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getRedSpotState() {
        new Thread(() -> {
            String userId = DataSHP.getUserId(MainActivity.this);
            if (userId == null)
                return;
            RedSpotBean redSpotBean = Action.getInstance().getRedSpotState(MainActivity.this, userId);
            Message message = handler.obtainMessage();
            if (redSpotBean != null && redSpotBean.getIsShow() == 1) {
                message.what = 1;
            } else {
                message.what = 0;
            }
            handler.sendMessage(message);
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mTopBar.findViewById(R.id.iv_red_dot).setVisibility(View.VISIBLE);
            } else {
                mTopBar.findViewById(R.id.iv_red_dot).setVisibility(View.GONE);
            }
        }
    };

    private void getActivateInfo() {

        String comment = Widget.getTextFromClip(this);
        LogUtil.e("getActivateInfo comment" + comment);
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

    private MainPreView.MainPreViewListener mMainPreViewListener = new MainPreView.MainPreViewListener() {
        @Override
        public synchronized void finish(boolean resume) {
            if (resume && !mDeviceActivity)
                return;
            try {
                setFullScreen(false);
                if (mDeviceActivity) {
                    getBuildinBook();
                    AnalyticsEngine.activate(MainActivity.this, mSiteId, mBookId, mBookName);
                    initViewPager(false, 0);
                    mDeviceActivity = false;
                }
                AnalyticsEngine.login(MainActivity.this);
                mUpgradeEngine.check(MainActivity.this);
                if (mBookshelfView.BookShelf() != null) {
                    mBookshelfView.BookShelf().getBannerAd();
                }
                if (AlertWindow.mAlertWindow != null) {
                    AlertWindow.mAlertWindow.show();
                }
                mBookshelfView.mainPreViewFinished();
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
        mBookshelfView.getBuildinBook(true, mBookName, mBookId, mChapterId);
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

    public BookshelfView bookshelfFrament() {
        Log.i("blank screen", "blank screen bookshelfFrament: " + mBookshelfView);
        return mBookshelfView;
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
        try {
            String dataType = getIntent().getStringExtra("t");
            String data = getIntent().getStringExtra("d");
            dealWithPushData(dataType, data);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("blank screen", "blank screen onResume");
        Utils.logNoTag("onResume");
        getRedSpotState();
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
        if (mAdapter != null)
            mAdapter.setAllLeave();
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
        UserEvent.getInstance().release();
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
                        mBookshelfView.readBook(0);
                    });
                    Action.getInstance().downloadCover(this, mBookshelfView.getBookByIndex(0).getBookCover(),
                            mBookshelfView.getBookByIndex(0).getBookId(), false);
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
            mViewPager.setCurrentItem(0);
            SetMenuTabChecked(0);
            return true;
        }
        return false;
    }

    private void initViewPager(boolean preload, int currentItem) {
        mViewPager = (ZYViewPager) findViewById(R.id.viewpager);
        mViewPager.setDisableScroll(true);
        List<View> views = new ArrayList<>();
        views.add(mBookshelfView);
        views.add(new BookStoreView(this, preload));
        views.add(new BookSelectedView(this, preload));
        views.add(new PersionalView(this, preload));
        mAdapter = new Adapter(views);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(views.size());
        mTopBar.setTitle("");
        mBookshelfView.activite();
        mAdapter.setActivity();
        mViewPager.setCurrentItem(currentItem);
        SetMenuTabChecked(currentItem);
        mViewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener) new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int position) {

            }
        });
        mTopBar.setRightButtonImageId(R.drawable.search);
        mTopBar.setVisibility(View.GONE);
        mTopBar.findViewById(R.id.icon_bookshelf).setVisibility(View.GONE);
        mTopBar.findViewById(R.id.iv_top_sign).setVisibility(View.GONE);
        switch (currentItem) {
            case 0:
                mTopBar.setVisibility(View.VISIBLE);
                mTopBar.setTitle("");
                mTopBar.findViewById(R.id.icon_bookshelf).setVisibility(View.VISIBLE);
                mTopBar.findViewById(R.id.iv_top_sign).setVisibility(View.VISIBLE);
                //mTopBar.setTitle(Widget.getAppName(this));
                break;
            case 1:
                mTopBar.setVisibility(View.GONE);
                break;
            case 2:
                mTopBar.setVisibility(View.GONE);
                break;
            case 3:
                mTopBar.setVisibility(View.GONE);
                mTopBar.setRightButtonImageId(0);
                break;
        }
    }


    private AlertWindow.AlertWindowListener mAlertWindowListener = new AlertWindow.AlertWindowListener() {
        @Override
        public boolean canShow() {
            return mMainPreView.getVisibility() != View.VISIBLE
                    && findViewById(R.id.sex).getVisibility() != View.VISIBLE;
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
    public void onClickView(View v) {
        mTopBar.setRightButtonImageId(R.drawable.search);
        mTopBar.setVisibility(View.GONE);
        mTopBar.findViewById(R.id.icon_bookshelf).setVisibility(View.GONE);
        mTopBar.findViewById(R.id.iv_top_sign).setVisibility(View.GONE);
        if (v.getId() == R.id.tool_bar_book_select) {
            mTopBar.setTitle(getResources().getString(R.string.main_tab_title_book_select));
            mViewPager.setCurrentItem(2);
            SetMenuTabChecked(2);
        } else if (v.getId() == R.id.tool_bar_book_store) {
            mTopBar.setTitle(getResources().getString(R.string.main_tab_title_book_store));
            mViewPager.setCurrentItem(1);
            SetMenuTabChecked(1);
        } else if (v.getId() == R.id.tool_bar_bookshelf) {
            mTopBar.setVisibility(View.VISIBLE);
            mTopBar.setTitle("");
            mTopBar.findViewById(R.id.icon_bookshelf).setVisibility(View.VISIBLE);
            mTopBar.findViewById(R.id.iv_top_sign).setVisibility(View.VISIBLE);
            //mTopBar.setTitle(Widget.getAppName(this));
            SetMenuTabChecked(0);
            mViewPager.setCurrentItem(0);
        } else if (v.getId() == R.id.tool_bar_persional) {
            mTopBar.setTitle(getResources().getString(R.string.main_tab_title_book_persional));
            mViewPager.setCurrentItem(3);
            SetMenuTabChecked(3);
            mTopBar.setRightButtonImageId(0);
        }
    }

    private void SetMenuTabChecked(int index) {
        final LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.tool_bar);
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = linearLayout.getChildAt(i);
            if (index == i) {
                ((ToolBar) child).setChecked(true);
            } else {
                ((ToolBar) child).setChecked(false);
            }
        }
    }

    @Override
    public void onClickTopBarLeft(View view) {
        if (mMainPreView.getVisibility() == View.VISIBLE)
            return;
        MessageDlg.show(this, getResources().getString(R.string.tip_confirm_exit), (boolean result) -> {
            if (result) {
                //System.exit(0);
                finish();
            }
        });

    }

    @Override
    public void onClickTopBarRight(View v) {
        Widget.startActivity(this, SearchActivity.class);
    }

    @Override
    protected void onClickSign(View v) {
        WebViewActivity.show(this, Url.URL_SIGNIN, WebViewActivity.UNKNOW, "");
        mTopBar.findViewById(R.id.iv_red_dot).setVisibility(View.INVISIBLE);
    }
}
