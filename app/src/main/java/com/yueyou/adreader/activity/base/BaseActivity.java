package com.yueyou.adreader.activity.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.yueyou.adreader.R;
import com.yueyou.adreader.view.TopBar;
import com.yueyou.adreader.view.dlg.ProgressDlg;


/**
 * Created by zy on 2017/3/31.
 */

public class BaseActivity extends FragmentActivity {
    protected TopBar mTopBar;
    private ProgressDlg mProgressDlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mProgressDlg = new ProgressDlg(this);
        mProgressDlg.setOwnerActivity(this);
    }

    /**
     * Android 6.0 以上设置状态栏颜色
     */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏底色颜色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    protected void initTop(String title, int leftButtonImgId, int rightButtonImgId){
        mTopBar = (TopBar)findViewById(R.id.top_bar);
        mTopBar.init(title, leftButtonImgId, rightButtonImgId, mTopBarOnClickListener);
    }

    public ProgressDlg progressDlg() {
        return mProgressDlg;
    }

    public void onPause() {
        super.onPause();
        mProgressDlg.hide();
//        ThirdAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ThirdAnalytics.onResume(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                onClickTopBarLeft(null);
                return true;
            default:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    protected void onClickView(View v){

    }

    protected void onClickTopBarLeft(View v){
        finish();
    }

    protected void onClickTopBarRight(View v){

    }
    protected void onClickSign(View v){

    }

    protected View.OnClickListener mOnClickListener = (View v)->{
        onClickView(v);
    };

    protected View.OnClickListener mTopBarOnClickListener = (View v)-> {
        if (v.getId() == R.id.top_bar_l_button) {
            onClickTopBarLeft(v);
        } else if (v.getId() == R.id.top_bar_r_button){
            onClickTopBarRight(v);
        }else if (v.getId() == R.id.top_bar_close_button){
            finish();
        }else if(v.getId()==R.id.iv_top_sign){
            onClickSign(v);
        }
    };
}
