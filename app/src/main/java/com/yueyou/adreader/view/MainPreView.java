package com.yueyou.adreader.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yueyou.adreader.R;
import com.yueyou.adreader.service.Action;
import com.yueyou.adreader.service.PermissionManager;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.dlg.MessageDlg;

@SuppressLint("AppCompatCustomView")
public class MainPreView extends RelativeLayout {
    private final int MSG_FINISH = 1;
    private final int MSG_NEXT = 2;
    private MainPreViewListener mMainPreViewListener;
    private ViewGroup mAdView;
    private TextView mSkipView;
    private boolean resume;
    public interface MainPreViewListener {
        void finish(boolean resume);
        void loginFinish();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_FINISH) {
                mHandler.removeCallbacksAndMessages(null);
                finish();
            }else if (msg.what == MSG_NEXT) {
                next();
            }
        };
    };

    private synchronized void finish() {
        if (getVisibility() == GONE)
            return;
        setVisibility(GONE);
        DataSHP.saveSexType(getContext(), "boy");
        if (mMainPreViewListener != null)
            mMainPreViewListener.finish(resume);
    }

    public MainPreView(final Context context) {
        super(context);
    }

    public MainPreView(final Context context, final AttributeSet set) {
        super(context, set);
        Log.i("blank screen", "blank screen MainPreView: " + this);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.main_pre_view, (ViewGroup)this);
        mAdView = findViewById(R.id.ad_container);
        mSkipView = findViewById(R.id.skip_view);
        if (Build.VERSION.SDK_INT >= 23
                && !PermissionManager.checkAndRequestPermission((Activity) context)) {
            return;
        }
        Widget.sendEmptyMessageDelayed(mHandler, MSG_NEXT, 100);
    }

    public void resumeShow() {
        resume = true;
        setVisibility(VISIBLE);
        Widget.sendEmptyMessageDelayed(mHandler, MSG_FINISH, 3000);
    }

    private void next() {
        Action.getInstance().login(getContext(), (object)->{
            if (mMainPreViewListener != null)
                mMainPreViewListener.loginFinish();
        });
        Widget.sendEmptyMessageDelayed(mHandler, MSG_FINISH, 3000);
    }

    public void setListener(MainPreViewListener mainPreViewListener) {
        mMainPreViewListener = mainPreViewListener;
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if(!PermissionManager.onRequestPermissionsResult(activity, requestCode, permissions, grantResults)) {
            showPermissionsToast(activity);
        }else {
            Widget.sendEmptyMessageDelayed(mHandler, MSG_NEXT, 100);
        }
    }

    private void showPermissionsToast(Activity activity) {
        int count = DataSHP.getPermissionsCount(activity);
        DataSHP.savePermissionsCount(activity, count + 1);
        if (count < 3) {
            MessageDlg.show(activity, "为了不影响您阅读体验，请打开相关权限！", (boolean result) -> {
                if (result) {
                    PermissionManager.startPermissionSet(activity);
                }
                Widget.sendEmptyMessageDelayed(mHandler, MSG_NEXT, 100);
            });
        } else {
            MessageDlg.showOnlyOk(activity, "应用所需权限未打开，不能启动，请打开相关权限！", (boolean result) -> {
                PermissionManager.startPermissionSet(activity);
                activity.finish();
            });
        }
    }
}
