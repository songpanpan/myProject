package com.yueyou.adreader.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.yueyou.adreader.R;
import com.yueyou.adreader.service.model.AdContent;
import com.yueyou.adreader.util.Widget;

/**
 * Created by zy on 2017/4/10.
 */

public class BookShelf extends LinearLayout{
    private float mDownTouchY;
    private float mDownTouchX;
    private int mTouchSlop;
    private boolean mDragStart;
    private HeaderGridView mGridView;
//    private FoldingLayout mHeaderBannerView;
    private int mGridViewHeigth;
    private float mGridViewOffset;
    private View mHeaderView;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                setmGridViewHeigth();
            }
        };
    };

    public BookShelf(Context context) {
        super(context);
    }

    public BookShelf(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.book_shelf, (ViewGroup)this);

        mGridView = findViewById(R.id.gridView);
        addGridViewHeader();
        mTouchSlop =  ViewConfiguration.get(context).getScaledTouchSlop();


    }

    public void resume() {
        Log.i("BookShelf resume", "BookShelf resume: " + mGridViewHeigth + "offset" + mGridViewOffset);
        if (mGridViewHeigth == 0)
            mHandler.sendEmptyMessageDelayed(1, 10);
    }
    private void addGridViewHeader() {
        mHeaderView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.gridview_header, null);
        mGridView.addHeaderView(mHeaderView);
        mHeaderView.setVisibility(GONE);
    }

    public void getBannerAd() {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                if (mDragStart) {

                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_DOWN:
                mDownTouchY = event.getY();
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (mDragStart) {
                    mDragStart = false;
                    return true;
                }
                break;
            }
        }
        return false;
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        setmGridViewHeigth();

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragStart = false;
            return false;
        }
        if (action != MotionEvent.ACTION_DOWN && mDragStart) {
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final float y = event.getY();
                final float dy = y - mDownTouchY;
                final float yDiff = Math.abs(dy);
                final float xDiff = Math.abs(event.getX() - mDownTouchX);

                if (yDiff > mTouchSlop && yDiff > xDiff) {
                    mDownTouchY = y;
                    mDragStart = true;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mDownTouchY = event.getY();
                mDownTouchX = event.getX();
                mDragStart = false;
                setmGridViewHeigth();
                break;
                default:break;
            }
        return mDragStart;
    }

    private void setmGridViewHeigth() {
        if (mGridViewHeigth == 0){
            mGridViewHeigth = (int) mGridView.getY() + mGridView.getHeight();
            if (mGridViewHeigth == 0)
                return;
        }
        //RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mGridView.getLayoutParams();
        LayoutParams lp = (LayoutParams) mGridView.getLayoutParams();
        lp.height = mGridViewHeigth;
        mGridView.setLayoutParams(lp);
    }

    private void scrollGridView(float offset) {
        if (offset < 0)
            return;
        if (mGridViewOffset == 0) {
            mGridViewOffset = mGridView.getY();
            return;
        }
        mGridView.setY(offset);
    }

    public void expand() {
        setmGridViewHeigth();
    }

    public void fold() {
        setmGridViewHeigth();
    }
}
