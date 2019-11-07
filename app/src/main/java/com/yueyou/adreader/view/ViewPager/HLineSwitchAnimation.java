package com.yueyou.adreader.view.ViewPager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yueyou.adreader.R;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.ViewPager.LineSwitchHLinearLayout.ListenerSwitchTab;

import java.util.ArrayList;

/**
 *@作者：Super.Ch
 *@创建时间 2013 2013-9-10 上午10:35:41
 */
public class HLineSwitchAnimation extends LinearLayout {
	private int mSelectedIndex;
	private ImageView mImageView;
	private ListenerSwitchTab mListenerSwitchTab;
	private int mInitIndex;
	public HLineSwitchAnimation(Context context) {
		super(context);
	}

	public HLineSwitchAnimation(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void build(ArrayList<Aliquot> lists ,int lineColor) {
		setOrientation(VERTICAL);
		LayoutInflater inflater = LayoutInflater.from(getContext());//.inflate(R.layout.h_scroll_animation, null);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT , LayoutParams.WRAP_CONTENT);
		LinearLayout parentLayout = new LinearLayout(getContext());
		
		params.weight = 1.0f;
		params.gravity = Gravity.CENTER;
		parentLayout.setOrientation(HORIZONTAL);
		int size = lists == null ? 0 : lists.size();
		for(int i = 0 ; i < size ; i++) {
			Aliquot aliquot = lists.get(i);
			FrameLayout layout = (FrameLayout)inflater.inflate(R.layout.h_scroll_animation, null);
			TextView tView = (TextView)layout.findViewById(R.id.scroll_h_item);
			if(!TextUtils.isEmpty(aliquot.mContent))tView.setText(aliquot.mContent);
			if(aliquot.mBackgroundId != 0) tView.setBackgroundResource(aliquot.mBackgroundId);
			if(aliquot.mAliquotColor != 0)tView.setTextColor(aliquot.mAliquotColor);
			layout.setTag(i);
			
			layout.setOnClickListener(mClickListener);
			parentLayout.addView(layout, params);
		}
		
		addView(parentLayout , new LayoutParams(LayoutParams.FILL_PARENT , LayoutParams.WRAP_CONTENT));
		DisplayMetrics dm = getResources().getDisplayMetrics();
		mImageView = new ImageView(getContext());
		LayoutParams lParams = new LayoutParams(dm.widthPixels / size, Widget.dip2px(getContext(), 2));
		lParams.topMargin = -Widget.dip2px(getContext(), 2);
		mImageView.setLayoutParams(lParams);
		mImageView.setBackgroundColor(lineColor);
		addView(mImageView , lParams);
	}
	
	public void build(ArrayList<Aliquot> lists ,int lineColor , int initIndex) {
		setOrientation(VERTICAL);
		LayoutInflater inflater = LayoutInflater.from(getContext());//.inflate(R.layout.h_scroll_animation, null);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT , LayoutParams.WRAP_CONTENT);
		LinearLayout parentLayout = new LinearLayout(getContext());
		
		params.weight = 1.0f;
		params.gravity = Gravity.CENTER;
		parentLayout.setOrientation(HORIZONTAL);
		int size = lists == null ? 0 : lists.size();
		for(int i = 0 ; i < size ; i++) {
			Aliquot aliquot = lists.get(i);
			FrameLayout layout = (FrameLayout)inflater.inflate(R.layout.h_scroll_animation, null);
			TextView tView = (TextView)layout.findViewById(R.id.scroll_h_item);
			if(!TextUtils.isEmpty(aliquot.mContent))tView.setText(aliquot.mContent);
			if(aliquot.mBackgroundId != 0) tView.setBackgroundResource(aliquot.mBackgroundId);
			if(aliquot.mAliquotColor != 0)tView.setTextColor(aliquot.mAliquotColor);
			layout.setTag(i);
			
			layout.setOnClickListener(mClickListener);
			parentLayout.addView(layout, params);
		}
		
		addView(parentLayout , new LayoutParams(LayoutParams.FILL_PARENT , LayoutParams.WRAP_CONTENT));
		DisplayMetrics dm = getResources().getDisplayMetrics();
		mImageView = new ImageView(getContext());
		LayoutParams lParams = new LayoutParams(dm.widthPixels / size, Widget.dip2px(getContext(), 2));
		lParams.topMargin = -Widget.dip2px(getContext(), 2);
		if(initIndex > size){
			initIndex = size - 1;
		}
		lParams.leftMargin = dm.widthPixels / size* initIndex;
		mSelectedIndex = initIndex;
		mInitIndex = initIndex;
		mImageView.setLayoutParams(lParams);
		mImageView.setBackgroundColor(lineColor);
		addView(mImageView , lParams);
	}
	
	public void setListenerSwitchTab(ListenerSwitchTab listener) {
		mListenerSwitchTab = listener;
	}
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			onAnimation((Integer)v.getTag());
			if(mListenerSwitchTab != null) {
				mListenerSwitchTab.onSwitchTab(mSelectedIndex);
			}
		}
	};
	
	synchronized public void onAnimation(int toSelectedIndex) {
		int imageViewWidth = mImageView.getMeasuredWidth();
		Animation animation = new TranslateAnimation(imageViewWidth*(mSelectedIndex-mInitIndex), imageViewWidth*(toSelectedIndex-mInitIndex), 0, 0);
		mSelectedIndex = toSelectedIndex;
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(300);
		mImageView.startAnimation(animation);
	}
}
