package com.yueyou.adreader.view.ReaderPage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.yueyou.adreader.R;
import com.yueyou.adreader.util.Widget;

public class Skin extends LinearLayout {
    private SkinListener mSkinListener;
    private int mBgColor;
    private int mTextColor;
    private int mBarBgColor;
    public Skin(final Context context) {
        super(context);
    }

    public Skin(final Context context, final AttributeSet set) {
        super(context, set);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.skin, this);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.skin);
        mBgColor = obtainStyledAttributes.getColor(R.styleable.skin_bg, 0xfff7f3f0);
        mTextColor = obtainStyledAttributes.getColor(R.styleable.skin_text, 0xff3c3d38);
        mBarBgColor = obtainStyledAttributes.getColor(R.styleable.skin_bar_bg, 0xfff7f3f0);
        View view = findViewById(R.id.bg);
        GradientDrawable drawable =(GradientDrawable)view.getBackground();
        drawable.setColor(mBgColor);
        obtainStyledAttributes.recycle();
        findViewById(R.id.bg).setOnClickListener((View v)->{
            try {
                mSkinListener.onClick(mBgColor, mTextColor, mBarBgColor);
            }catch (Exception e){

            }
        });
    }

    public void setSkinListener(SkinListener skinListener){
        mSkinListener = skinListener;
    }

    public interface SkinListener{
        void onClick(int bgColor, int textColor, int barBgColor);
    }

    public int getBgColor(){
        return mBgColor;
    }

    public int getTextColor(){
        return mTextColor;
    }

    public int getBarBgColor() {return mBarBgColor;}

    public void setSelected(boolean selected) {
        View view = findViewById(R.id.bg);
        GradientDrawable drawable =(GradientDrawable)view.getBackground();
        if (selected) {
            drawable.setStroke(Widget.dip2px(getContext(), 2), 0xffe85b62);
        }else {
            drawable.setStroke(Widget.dip2px(getContext(), 2), 0xffffffff);
        }
    }
}
