package com.yueyou.adreader.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yueyou.adreader.R;

public class ToolBar extends LinearLayout
{
    private ImageView mImg;
    private TextView mTitle;
    
    public ToolBar(final Context context) {
        super(context);
    }
    
    public ToolBar(final Context context, final AttributeSet set) {
        super(context, set);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.getLayoutId(), this);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.tool_bar);
        mImg = (ImageView)findViewById(R.id.tool_bar_img);
        mTitle = (TextView)this.findViewById(R.id.tool_bar_title);
        mImg.setBackgroundResource(obtainStyledAttributes.getResourceId(R.styleable.tool_bar_img, 0));
        mTitle.setText((CharSequence)obtainStyledAttributes.getString(R.styleable.tool_bar_title));
        obtainStyledAttributes.recycle();
    }
    
    public int getLayoutId() {
        return R.layout.tool_bar;
    }
    
    public void setChecked(final boolean isChecked) {
        mImg.setSelected(isChecked);
        mTitle.setSelected(isChecked);
    }

    public void setTitle(String title){
        mTitle.setText(title);
    }

    public void setImg(int id){
        mImg.setBackgroundResource(id);
    }
}
