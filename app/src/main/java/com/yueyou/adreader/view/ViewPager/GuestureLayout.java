package com.yueyou.adreader.view.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.yueyou.adreader.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 监听手势的view
 * 当创建时，会将activity的contentView移动到此view下，并将此view作为activity的contentView，从而实现全局手势
 * @author huangjian
 *
 */
public class GuestureLayout extends FrameLayout {
    /**
     * 最小速度,每秒移动的dip
     */
    private static final int MIN_FLING_VELOCITY 	= 400;

    private static final int DEFAULT_SCRIM_COLOR 	= 0x99000000;
    
    /**
     * 默认的边缘宽度
     */
    private static final int DEFAULT_EDGE_SIZE		= 50 ;

    private static final int FULL_ALPHA 			= 255;

    /**
     * 左边缘滑动有效
     */
    public static final int EDGE_LEFT 				= ViewGuestureHelper.EDGE_LEFT;

    /**
     * 右边缘滑动有效
     */
    public static final int EDGE_RIGHT 				= ViewGuestureHelper.EDGE_RIGHT;

    /**
     * 下边缘滑动有效
     */
    public static final int EDGE_BOTTOM 			= ViewGuestureHelper.EDGE_BOTTOM;

    /**
     * 左、右和下边缘滑动都有效
     */
    public static final int EDGE_ALL 				= EDGE_LEFT | EDGE_RIGHT | EDGE_BOTTOM;

    /**
     * 初始状态，没有拖动和处于动画状态
     */
    public static final int STATE_IDLE 				= ViewGuestureHelper.STATE_IDLE;

    /**
     * 当前处于拖动状态
     */
    public static final int STATE_DRAGGING 			= ViewGuestureHelper.STATE_DRAGGING;

    /**
     * 将要移动到被设置的地点
     */
    public static final int STATE_SETTLING 			= ViewGuestureHelper.STATE_SETTLING;

    /**
     * 默认的滑动临界值，滑动超过这个值并释放时，则认为滑动完成
     */
    private static final float DEFAULT_SCROLL_THRESHOLD = 0.3f;

    private static final int OVERSCROLL_DISTANCE 	= 10;

    private static final int[] EDGE_FLAGS = {
            EDGE_LEFT, EDGE_RIGHT, EDGE_BOTTOM, EDGE_ALL
    };

    private int mEdgeFlag;

    private float mScrollThreshold = DEFAULT_SCROLL_THRESHOLD;

    private Activity mActivity;

    private boolean mIsEnable			= true; // 此控件是否有效，为长期性
    private static boolean isJustEnable = true; // 此时控件是否有效，为短暂性

    private View mContentView;

    private ViewGuestureHelper mViewGuestureHelper;

    private float mScrollPercent;

    private int mContentLeft;

    private int mContentTop;

    private List<GuestListener> mListeners;

    private Drawable mShadowLeft;

    private Drawable mShadowRight;

    private Drawable mShadowBottom;

    private float mScrimOpacity;

    private int mScrimColor = DEFAULT_SCRIM_COLOR;

    private boolean mInLayout;

    private Rect mTmpRect = new Rect();

    private int mTrackingEdge;

    public GuestureLayout(Context context) {
        super(context);
        init(context);
    }

    public GuestureLayout(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	init(context);
    }

    public GuestureLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        mViewGuestureHelper = ViewGuestureHelper.create(this, new ViewDragCallback());
        
        setEdgeTrackingEnabled(EDGE_LEFT);//默认支持从左边缘右滑
        setEdgeSize(getResources().getDisplayMetrics().widthPixels);//设置边缘的宽度为整个屏幕宽度
        
        //设置边缘的阴影
        int shadowLeft = R.drawable.guesture_shadow_left;
        int shadowRight = R.drawable.guesture_shadow_right;
        int shadowBottom = R.drawable.guesture_shadow_bottom;
        
        setShadow(shadowLeft, EDGE_LEFT);
        setShadow(shadowRight, EDGE_RIGHT);
        setShadow(shadowBottom, EDGE_BOTTOM);
        
        final float density = getResources().getDisplayMetrics().density;
        final float minVel = MIN_FLING_VELOCITY * density;
        mViewGuestureHelper.setMinVelocity(minVel);
    }
    /**
     * 设置灵敏度
     * 
     * @param context
     * @param sensitivity 灵敏度，从0到1最后的值为 ViewConfiguration.getScaledTouchSlop * (1 / sensitivity);
     */
    public void setSensitivity(Context context, float sensitivity) {
        mViewGuestureHelper.setSensitivity(context, sensitivity);
    }

    /**
     * 设置contentView
     * 
     * @param view
     */
    private void setContentView(View view) {
        mContentView = view;
    }

    public void setEnableGesture(boolean enable) {
    	mIsEnable= enable;
    }

    public static void setJustEnableGesture(boolean enable) {
    	isJustEnable = enable;
    }
    /**
     * 设置从哪个边缘滑动有效
     * 
     * @param edgeFlags 边缘的flag，可能的值如下
     * @see #EDGE_LEFT
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     */
    public void setEdgeTrackingEnabled(int edgeFlags) {
        mEdgeFlag = edgeFlags;
        mViewGuestureHelper.setEdgeTrackingEnabled(mEdgeFlag);
    }

    /**
     * 设置蒙板颜色
     * 
     * @param color 蒙板颜色
     */
    public void setScrimColor(int color) {
        mScrimColor = color;
        invalidate();
    }

    /**
     * 设置边缘宽度
     * 
     * @param size 边缘宽度，单位为px
     */
    public void setEdgeSize(int size) {
        mViewGuestureHelper.setEdgeSize(size);
    }

    /**
     * 添加监听
     * 
     * @param listener
     */
    public void addGuestureListener(GuestListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<GuestListener>();
        }
        mListeners.add(listener);
    }

    /**
     * 移除监听
     * 
     * @param listener
     */
    public void removeGuestListener(GuestListener listener) {
        if (mListeners == null) {
            return;
        }
        mListeners.remove(listener);
    }

    public static interface GuestListener {
        /**
         * 状态改变时调用
         * 
         * @param state 改变后的状态，可能的值如下
         * @see #STATE_IDLE
         * @see #STATE_DRAGGING
         * @see #STATE_SETTLING
         * @param scrollPercent 滑动的百分比
         */
        public void onScrollStateChange(int state, float scrollPercent);

        /**
         * 手指碰到边缘时调用
         * 
         * @param edgeFlag 边缘的flag，可能的值如下
         * @see #EDGE_LEFT
         * @see #EDGE_RIGHT
         * @see #EDGE_BOTTOM
         */
        public void onEdgeTouch(int edgeFlag);

        /**
         * 当滑动首次到达临界值时调用
         */
        public void onScrollOverThreshold();
    }

    /**
     * 设置滑动的临界值
     * 
     * @param threshold 临界值
     */
    public void setScrollThresHold(float threshold) {
        if (threshold >= 1.0f || threshold <= 0) {
            throw new IllegalArgumentException("Threshold value should be between 0 and 1.0");
        }
        mScrollThreshold = threshold;
    }

    /**
     * 设置边缘阴影图片
     * 
     * @param shadow 边缘的Drawable
     * @param edgeFlags 哪个边缘的flag,具体如下
     * @see #EDGE_LEFT
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     */
    public void setShadow(Drawable shadow, int edgeFlag) {
        if ((edgeFlag & EDGE_LEFT) != 0) {
            mShadowLeft = shadow;
        } else if ((edgeFlag & EDGE_RIGHT) != 0) {
            mShadowRight = shadow;
        } else if ((edgeFlag & EDGE_BOTTOM) != 0) {
            mShadowBottom = shadow;
        }
        invalidate();
    }

    /**
     * 设置边缘阴影图片
     * 
     * @param resId 边缘图片的资源id
     * @param edgeFlags 哪个边缘的flag,具体如下
     * @see #EDGE_LEFT
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     */
    public void setShadow(int resId, int edgeFlag) {
        setShadow(getResources().getDrawable(resId), edgeFlag);
    }

    /**
     * 拖出并结束当前activity
     */
    public void scrollToFinishActivity() {
        final int childWidth = mContentView.getWidth();
        final int childHeight = mContentView.getHeight();

        int left = 0, top = 0;
        if ((mEdgeFlag & EDGE_LEFT) != 0) {
            left = childWidth + mShadowLeft.getIntrinsicWidth() + OVERSCROLL_DISTANCE;
            mTrackingEdge = EDGE_LEFT;
        } else if ((mEdgeFlag & EDGE_RIGHT) != 0) {
            left = -childWidth - mShadowRight.getIntrinsicWidth() - OVERSCROLL_DISTANCE;
            mTrackingEdge = EDGE_RIGHT;
        } else if ((mEdgeFlag & EDGE_BOTTOM) != 0) {
            top = -childHeight - mShadowBottom.getIntrinsicHeight() - OVERSCROLL_DISTANCE;
            mTrackingEdge = EDGE_BOTTOM;
        }

        mViewGuestureHelper.smoothSlideViewTo(mContentView, left, top);
        invalidate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mIsEnable||!isJustEnable) {
        	if(event.getAction()!=MotionEvent.ACTION_MOVE){
        		isJustEnable=true;
			}
            return super.onInterceptTouchEvent(event);
        }
        try {
            return mViewGuestureHelper.shouldInterceptTouchEvent(event);
        } catch (ArrayIndexOutOfBoundsException e) {
        	e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsEnable||!isJustEnable) {
            return false;
        }
        mViewGuestureHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mInLayout = true;
        if (mContentView != null)
            mContentView.layout(mContentLeft, mContentTop,
                    mContentLeft + mContentView.getMeasuredWidth(),
                    mContentTop + mContentView.getMeasuredHeight());
        mInLayout = false;
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean drawContent = child == mContentView;

        boolean ret = super.drawChild(canvas, child, drawingTime);
        if (mScrimOpacity > 0 && drawContent
                && mViewGuestureHelper.getViewDragState() != ViewGuestureHelper.STATE_IDLE) {
            drawShadow(canvas, child);
            drawScrim(canvas, child);
        }
        return ret;
    }

    private void drawScrim(Canvas canvas, View child) {
        final int baseAlpha = (mScrimColor & 0xff000000) >>> 24;
        final int alpha = (int) (baseAlpha * mScrimOpacity);
        final int color = alpha << 24 | (mScrimColor & 0xffffff);

        if ((mTrackingEdge & EDGE_LEFT) != 0) {
            canvas.clipRect(0, 0, child.getLeft(), getHeight());
        } else if ((mTrackingEdge & EDGE_RIGHT) != 0) {
            canvas.clipRect(child.getRight(), 0, getRight(), getHeight());
        } else if ((mTrackingEdge & EDGE_BOTTOM) != 0) {
            canvas.clipRect(child.getLeft(), child.getBottom(), getRight(), getHeight());
        }
        canvas.drawColor(color);
    }

    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mTmpRect;
        child.getHitRect(childRect);

        if ((mEdgeFlag & EDGE_LEFT) != 0) {
            mShadowLeft.setBounds(childRect.left - mShadowLeft.getIntrinsicWidth(), childRect.top,
                    childRect.left, childRect.bottom);
            mShadowLeft.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowLeft.draw(canvas);
        }

        if ((mEdgeFlag & EDGE_RIGHT) != 0) {
            mShadowRight.setBounds(childRect.right, childRect.top,
                    childRect.right + mShadowRight.getIntrinsicWidth(), childRect.bottom);
            mShadowRight.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowRight.draw(canvas);
        }

        if ((mEdgeFlag & EDGE_BOTTOM) != 0) {
            mShadowBottom.setBounds(childRect.left, childRect.bottom, childRect.right,
                    childRect.bottom + mShadowBottom.getIntrinsicHeight());
            mShadowBottom.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowBottom.draw(canvas);
        }
    }

    public void attachToActivity(Activity activity) {
        mActivity = activity;
        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[] {
            android.R.attr.windowBackground
        });
        int background = a.getResourceId(0, 0);
        a.recycle();

        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decor.removeView(decorChild);
        addView(decorChild);
        setContentView(decorChild);
        decor.addView(this);
    }

    @Override
    public void computeScroll() {
        mScrimOpacity = 1 - mScrollPercent;
        if (mViewGuestureHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private class ViewDragCallback extends ViewGuestureHelper.Callback {
        private boolean mIsScrollOverValid;

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
        	super.onEdgeDragStarted(edgeFlags, pointerId);
        }
        
        @Override
        public boolean tryCaptureView(View view, int i) {
            boolean ret = mViewGuestureHelper.isEdgeTouched(mEdgeFlag, i);
            if (ret) {
                if (mViewGuestureHelper.isEdgeTouched(EDGE_LEFT, i)) {
                    mTrackingEdge = EDGE_LEFT;
                } else if (mViewGuestureHelper.isEdgeTouched(EDGE_RIGHT, i)) {
                    mTrackingEdge = EDGE_RIGHT;
                } else if (mViewGuestureHelper.isEdgeTouched(EDGE_BOTTOM, i)) {
                    mTrackingEdge = EDGE_BOTTOM;
                }
                if (mListeners != null && !mListeners.isEmpty()) {
                    for (GuestListener listener : mListeners) {
                        listener.onEdgeTouch(mTrackingEdge);
                    }
                }
                mIsScrollOverValid = true;
            }
            return ret;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mEdgeFlag & (EDGE_LEFT | EDGE_RIGHT);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mEdgeFlag & EDGE_BOTTOM;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //隐藏虚拟键盘
        	InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm.isActive()&&mActivity.getCurrentFocus()!=null){
				imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
			}
			
            if ((mTrackingEdge & EDGE_LEFT) != 0) {
                mScrollPercent = Math.abs((float) left
                        / (mContentView.getWidth() + mShadowLeft.getIntrinsicWidth()));
            } else if ((mTrackingEdge & EDGE_RIGHT) != 0) {
                mScrollPercent = Math.abs((float) left
                        / (mContentView.getWidth() + mShadowRight.getIntrinsicWidth()));
            } else if ((mTrackingEdge & EDGE_BOTTOM) != 0) {
                mScrollPercent = Math.abs((float) top
                        / (mContentView.getHeight() + mShadowBottom.getIntrinsicHeight()));
            }
            mContentLeft = left;
            mContentTop = top;
            invalidate();
            if (mScrollPercent < mScrollThreshold && !mIsScrollOverValid) {
                mIsScrollOverValid = true;
            }
            if (mListeners != null && !mListeners.isEmpty()
                    && mViewGuestureHelper.getViewDragState() == STATE_DRAGGING
                    && mScrollPercent >= mScrollThreshold && mIsScrollOverValid) {
                mIsScrollOverValid = false;
                for (GuestListener listener : mListeners) {
                    listener.onScrollOverThreshold();
                }
            }

            if (mScrollPercent >= 1) {
                if (!mActivity.isFinishing()){
                	//BEvent.event(BID.ID_SLIDE_FINISH);
                    mActivity.finish();
                }
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final int childWidth = releasedChild.getWidth();
            final int childHeight = releasedChild.getHeight();

            int left = 0, top = 0;
            if ((mTrackingEdge & EDGE_LEFT) != 0) {
                left = xvel > 0 || xvel == 0 && mScrollPercent > mScrollThreshold ? childWidth
                        + mShadowLeft.getIntrinsicWidth() + OVERSCROLL_DISTANCE : 0;
            } else if ((mTrackingEdge & EDGE_RIGHT) != 0) {
                left = xvel < 0 || xvel == 0 && mScrollPercent > mScrollThreshold ? -(childWidth
                        + mShadowLeft.getIntrinsicWidth() + OVERSCROLL_DISTANCE) : 0;
            } else if ((mTrackingEdge & EDGE_BOTTOM) != 0) {
                top = yvel < 0 || yvel == 0 && mScrollPercent > mScrollThreshold ? -(childHeight
                        + mShadowBottom.getIntrinsicHeight() + OVERSCROLL_DISTANCE) : 0;
            }

            mViewGuestureHelper.settleCapturedViewAt(left, top);
            invalidate();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int ret = 0;
            if ((mTrackingEdge & EDGE_LEFT) != 0) {
                ret = Math.min(child.getWidth(), Math.max(left, 0));
            } else if ((mTrackingEdge & EDGE_RIGHT) != 0) {
                ret = Math.min(0, Math.max(left, -child.getWidth()));
            }
            return ret;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int ret = 0;
            if ((mTrackingEdge & EDGE_BOTTOM) != 0) {
                ret = Math.min(0, Math.max(top, -child.getHeight()));
            }
            return ret;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mListeners != null && !mListeners.isEmpty()) {
                for (GuestListener listener : mListeners) {
                    listener.onScrollStateChange(state, mScrollPercent);
                }
            }
        }
    }
}
