package com.yueyou.adreader.view.ViewPager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yueyou.adreader.R;
import com.yueyou.adreader.util.Widget;

/**
 * Tab切换
 *@作者：chao.ch
 *@创建时间 2012 2012-11-28 下午4:00:53
 */
public class LineSwitchHLinearLayout extends LinearLayout {
	private int mCurrLineIndex = -1;
	private LinearLayout mContentLinearLayout;
	private LinearLayout mLineLinearLayout;
	private int mEnableColor;
	private int mSelectedColor;
	private String[] mShowArray;
	private int mLineColor =-1;
	private ListenerSwitchTab mListenerSwitchTab;
	public LineSwitchHLinearLayout(Context context) {
		super(context);
		init();
	}

	public LineSwitchHLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		mContentLinearLayout = new LinearLayout(getContext());
		mLineLinearLayout 	 = new LinearLayout(getContext());
		LayoutParams params = getLinLayoutParams();
		params.gravity = Gravity.CENTER;
		mContentLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		mLineLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		mContentLinearLayout.setLayoutParams(params);
		LayoutParams params2 = getLinLayoutParams();
		params2.height	= Widget.dip2px(getContext(), 1.5f);
		params2.gravity = Gravity.CENTER;
		params2.width   = LayoutParams.FILL_PARENT;
		mLineLinearLayout.setLayoutParams(params2);
		addView(mContentLinearLayout);
		addView(mLineLinearLayout);
	}
	
	private LayoutParams getLinLayoutParams() {
		LayoutParams params  = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		return params;
	}
	
	/**
	 * @param arrayTextId:对应显示的名称资源ID
	 * @param textColor：显示文字的颜色
	 * @param colorState：显示文字颜色带状态 
	 * <br>colorState != null ? 忽视textColor 用colorState ：textColor</br>
	 */
	public void show(String[] textArray , int selectedColor , int enableColor, ColorStateList colorState) {
		
		mSelectedColor 	 = selectedColor;
		mEnableColor	 = enableColor;
		mLineColor 		 = 0x26000000;
		
		if(textArray == null || textArray.length == 0) return ;
		mShowArray = textArray;
		LayoutParams params =getLinLayoutParams();
		params.gravity = Gravity.CENTER;
		Context context = getContext();
		int count = textArray.length;
		for(int i = 0 ; i < count ; i++) {
			TextView textView = new TextView(context);
			textView.setLayoutParams(params);
			textView.setText(textArray[i]);
			if(colorState != null)textView.setTextColor(colorState);
			else textView.setTextColor(mEnableColor);
			textView.setTag(i);
			textView.setGravity(Gravity.CENTER);
			textView.setOnClickListener(mOnClickListener);
			textView.setTextSize(18.0f);
			textView.setPadding(0, 15, 0, 15);
			mContentLinearLayout.addView(textView);
		}
		
		mLineLinearLayout.setBackgroundColor(mLineColor);
	}

	public void show(String[] textArray , int selectedColor , int enableColor, ColorStateList colorState , int drawableId , int lineColor) {
		mLineColor 		 = lineColor;
		mSelectedColor 	 = selectedColor;
		mEnableColor	 = enableColor;
		if(textArray == null || textArray.length == 0) return ;
		mShowArray = textArray;
		LayoutParams params =getLinLayoutParams();
		params.gravity = Gravity.CENTER;
		Context context = getContext();
		int count = textArray.length;
		for(int i = 0 ; i < count ; i++) {
			TextView textView = new TextView(context);
			textView.setLayoutParams(params);
			textView.setText(textArray[i]);
			if(colorState != null)textView.setTextColor(colorState);
			else textView.setTextColor(mLineColor);
			textView.setTag(i);
			textView.setGravity(Gravity.CENTER);
			textView.setOnClickListener(mOnClickListener);
			textView.setTextSize(17.0f);
			textView.setPadding(0, (int)getResources().getDimension(R.dimen.padding_top), 0, (int)getResources().getDimension(R.dimen.padding_bottom));
			mLineLinearLayout.setBackgroundColor(mLineColor);
			if(i != count - 1) {
				Drawable drawable = getResources().getDrawable(drawableId);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				
				textView.setCompoundDrawables(null, null, drawable, null);
			}
			mContentLinearLayout.addView(textView);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		
		if(mLineLinearLayout.getChildCount() == 0) {
			int childCount = mContentLinearLayout.getChildCount();
			View view = new View(getContext());
			LayoutParams params2 = new LayoutParams(r / childCount , mLineLinearLayout.getHeight());
			view.setLayoutParams(params2);
			view.setBackgroundColor(mSelectedColor);
			mLineLinearLayout.addView(view);
		}
	}

	/**
	 * 切换
	 * @param tagId
	 */
	public void switchView(int tagId) {
		int count 	= mContentLinearLayout.getChildCount();
		TextView textView = null;
		for(int i = 0 ; i < count ; i++) {
			textView = (TextView)mContentLinearLayout.getChildAt(i);
			if(i == tagId) {
				textView.setTextColor(mSelectedColor);
				mCurrLineIndex = i;
			} else {
				textView.setTextColor(mEnableColor);
			}
		}
		animation();
	}

	private void animation() {
		View toView = mContentLinearLayout.getChildAt(mCurrLineIndex);
		if(toView == null) return ;
		int toX = toView.getLeft();
		final View view = mLineLinearLayout.getChildAt(0);
		if(view == null) return ;
//		view.scrollTo(view.getWidth(), 0);
//		view.postInvalidate();
		int left = view.getLeft();
		toX = toX - left;
		TranslateAnimation translateAnimation = new TranslateAnimation(left, toX, 0, 0);
		translateAnimation.setDuration(300);
		translateAnimation.setFillAfter(true);
		view.startAnimation(translateAnimation);
//		translateAnimation.setAnimationListener(new AnimationListener() {
//			
//			@Override
//			public void onAnimationStart(Animation animation) {
//
//			}
//			
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//
//			}
//			
//			@Override
//			public void onAnimationEnd(Animation animation) {
//	            int top = view.getTop();
//	            int width = view.getWidth();
//	            int height = view.getHeight();
//	            view.scrollTo(width, 0);
////	            view.layout(width * mCurrLineIndex, top, width * (mCurrLineIndex + 1), top+height);
////	            mLineLinearLayout.setBackgroundColor(mLineColor);
//	            view.clearAnimation();
//			}
//		});
	}
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int tagId = (Integer)v.getTag();
			mCurrLineIndex= tagId;
//			switchView(tagId);
			if(mListenerSwitchTab != null) mListenerSwitchTab.onSwitchTab(mCurrLineIndex);
		}
	};
	
	public void invalidateChild() {
		int count = mContentLinearLayout.getChildCount();
		for(int i = 0 ;  i < count ; i++) {
			TextView textView = (TextView) mContentLinearLayout.getChildAt(i);
			textView.setText(mShowArray[i]);
			textView.invalidate();
		}
	}
	
	public interface ListenerSwitchTab {
		
		public void onSwitchTab(int index);
	}
	
	public void setListenerSwitchTab(ListenerSwitchTab listener) {
		mListenerSwitchTab = listener;
	}
}
