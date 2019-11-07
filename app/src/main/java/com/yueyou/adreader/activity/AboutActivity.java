package com.yueyou.adreader.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.base.BaseActivity;
import com.yueyou.adreader.util.Widget;

public class AboutActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initTop(getResources().getString(R.string.activity_title_about), R.drawable.back, 0);
        TextView version = findViewById(R.id.version);
        version.setText(getResources().getString(R.string.app_copyright) + Widget.getAppVersionName(this));
    }
}
