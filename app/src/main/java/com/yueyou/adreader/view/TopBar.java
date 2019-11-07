package com.yueyou.adreader.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yueyou.adreader.R;

/**
 * Created by zy on 2017/4/10.
 */

public class TopBar extends LinearLayout {
    private boolean mAdVisible;

    public TopBar(Context context) {
        super(context);
    }

    public TopBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.top_bar, (ViewGroup) this);
    }

    public void init(String title, int leftButtonImgId, int rightButtonImgId, OnClickListener onClickListener) {
        if (leftButtonImgId != 0) {
            ((TextView) findViewById(R.id.top_bar_title)).setGravity(Gravity.CENTER_VERTICAL);
        }
        setTitle(title);
        setLeftButtonImageId(leftButtonImgId);
        setRightButtonImageId(rightButtonImgId);
        findViewById(R.id.top_bar_l_button).setOnClickListener(onClickListener);
        findViewById(R.id.top_bar_r_button).setOnClickListener(onClickListener);
        findViewById(R.id.iv_top_sign).setOnClickListener(onClickListener);
        findViewById(R.id.top_bar_close_button).setOnClickListener(onClickListener);
    }

    public void setCloseEnable() {
        findViewById(R.id.top_bar_close_button).setVisibility(VISIBLE);
        ((TextView) findViewById(R.id.top_bar_title)).setGravity(Gravity.CENTER);
    }

    public void setTitle(String title) {
        ((TextView) findViewById(R.id.top_bar_title)).setText(title);
    }

    public String getTitle() {
        return ((TextView) findViewById(R.id.top_bar_title)).getText().toString();
    }

    public void setTitleSizeMax() {
        ((TextView) findViewById(R.id.top_bar_title)).setTextSize(17.f);
        ((TextView) findViewById(R.id.top_bar_title)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
    }

    public void setLeftButtonImageId(int id) {
        if (id == 0) {
            findViewById(R.id.top_bar_l_button).setVisibility(GONE);
            return;
        }
        ((ImageView) findViewById(R.id.top_bar_l_button)).setImageResource(id);
        findViewById(R.id.top_bar_l_button).setVisibility(VISIBLE);
    }

    public void setRightButtonImageId(int id) {
        if (id == 0) {
            findViewById(R.id.top_bar_r_button).setVisibility(GONE);
            return;
        }
        ((ImageView) findViewById(R.id.top_bar_r_button)).setImageResource(id);
        findViewById(R.id.top_bar_r_button).setVisibility(VISIBLE);
    }
}
