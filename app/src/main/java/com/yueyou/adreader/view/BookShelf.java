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

import com.linc.foldingmenu.FoldingLayout;
import com.linc.foldingmenu.OnFoldListener;
import com.yueyou.adreader.R;
import com.yueyou.adreader.service.advertisement.adObject.AdBookShelfBanner;
import com.yueyou.adreader.service.advertisement.adObject.AdBookShelfHeader;
import com.yueyou.adreader.service.advertisement.service.AdEventObject;
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
    private FoldingLayout mHeaderBannerView;
    private int mGridViewHeigth;
    private float mGridViewOffset;
    private boolean mAdShow;
    private View mHeaderView;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                setmGridViewHeigth();
                mHeaderBannerView.setFoldFactor(0.6f);
                animateFold(mHeaderBannerView, 0, false);
            }
        };
    };

    public BookShelf(Context context) {
        super(context);
    }

    public BookShelf(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.book_shelf, (ViewGroup)this);
        mAdBanner.init(findViewById(R.id.ad_container_bookshelf));
        mHeaderBannerView = findViewById(R.id.header);
        mHeaderBannerView.setNumberOfFolds(3);
        mGridView = findViewById(R.id.gridView);
        addGridViewHeader();
        mTouchSlop =  ViewConfiguration.get(context).getScaledTouchSlop();
        mHeaderBannerView.setFoldListener(new OnFoldListener() {
            @Override
            public void onStartFold(float foldFactor) {
                scrollGridView((1-foldFactor) * mGridViewOffset);
            }

            @Override
            public void onFoldingState(float foldFactor, float foldDrawHeight) {
                scrollGridView((1-foldFactor) * mGridViewOffset);
            }

            @Override
            public void onEndFold(float foldFactor) {
                scrollGridView((1-foldFactor) * mGridViewOffset);
            }
        });

    }

    public void resume() {
        Log.i("BookShelf resume", "BookShelf resume: " + mGridViewHeigth + "offset" + mGridViewOffset);
        if (mGridViewHeigth == 0)
            mHandler.sendEmptyMessageDelayed(1, 10);
    }
    private AdBookShelfHeader mAdBookShelfHeader;
    private void addGridViewHeader() {
        mHeaderView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.gridview_header, null);
        mGridView.addHeaderView(mHeaderView);
        mHeaderView.setVisibility(GONE);
        if (mAdBookShelfHeader == null) {
            mAdBookShelfHeader = new AdBookShelfHeader();
            mAdBookShelfHeader.load((ViewGroup) mHeaderView);
        }
    }

    public void getBannerAd() {
        mAdBanner.load();
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mHeaderBannerView.getElevation() != 0) {
                        mHeaderBannerView.setElevation(0);
                    }
                    float y = event.getY();
                    float factor = (mDownTouchY - y) / mHeaderBannerView.getHeight() + mHeaderBannerView.getFoldFactor() ;
                    if (factor <= 0)
                        factor = 0;
                    if (factor >= 1)
                        factor = 1;
                    mHeaderBannerView.setFoldFactor(factor);
                    mDownTouchY = event.getY();
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
                    animateFold(mHeaderBannerView, 100, false);
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
        if (mHeaderBannerView.getVisibility() == GONE || !mAdShow)
            return false;
        if (mHeaderBannerView.getFoldFactor() == 1 && mGridView.canScrollVertically(-1))
            return false;
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
                if ((dy > 0 && mHeaderBannerView.getFoldFactor() == 0) || (dy < 0 && mHeaderBannerView.getFoldFactor() == 1))
                    return false;
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

    public void animateFold(FoldingLayout foldLayout, int duration, boolean fromAd) {
        float foldFactor = foldLayout.getFoldFactor();
        ObjectAnimator animator = ObjectAnimator.ofFloat(foldLayout, "foldFactor", foldFactor, foldFactor > 0.5 ? 1 : 0);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(0);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mHeaderBannerView.getFoldFactor() == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mHeaderBannerView.setElevation(Widget.dip2px(BookShelf.this.getContext(), 5));
                }
                if (mHeaderBannerView.getFoldFactor() == 0 && !fromAd)
                    getBannerAd();
                scrollGridView((1 - mHeaderBannerView.getFoldFactor()) * mGridViewOffset);
                if (mHeaderBannerView.getFoldFactor() == 1 && mGridView.getHeaderViewCount() > 0) {
                    scrollGridView(0 - Widget.dip2px(getContext(), 14));
                }
            }
        });
    }

    public void expand() {
        if (mHeaderBannerView.getFoldFactor() < 0.5)
            return;
        mAdShow = true;
        setmGridViewHeigth();
        mHeaderBannerView.setFoldFactor(0.1f);
        animateFold(mHeaderBannerView, 200, true);
    }

    public void fold() {
        mAdShow = false;
        if (mHeaderBannerView.getFoldFactor() > 0.5)
            return;
        setmGridViewHeigth();
        mHeaderBannerView.setFoldFactor(0.9f);
        animateFold(mHeaderBannerView, 200, true);
    }
    private AdBookShelfBanner mAdBanner = new AdBookShelfBanner(new AdEventObject.AdEventObjectListener() {
        @Override
        public void showed(AdContent adContent) {
            expand();
        }

        @Override
        public void closed() {
            fold();
        }
    });
}
