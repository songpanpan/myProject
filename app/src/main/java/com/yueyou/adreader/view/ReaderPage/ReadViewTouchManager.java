package com.yueyou.adreader.view.ReaderPage;

import android.view.MotionEvent;
import android.view.View;

public class ReadViewTouchManager implements View.OnTouchListener{
    private float mDownTouchX;
    private float mDownTouchY;
    private boolean mDragStart;
    private boolean mDragDirect;
    private boolean mChangeDirect;
    private View mBgView;
    private int mFlipMode;//0：没有翻页效果 1：覆盖
    private boolean mDisFlip;
    private SurfaceViewAnimator mSurfaceViewAnimator;
    private ReadViewTouchManagerListener mReadViewTouchManagerListener;
    private boolean mMask;
    public void release() {
        mSurfaceViewAnimator.release();
    }

    public ReadViewTouchManager(ReadViewTouchManagerListener readViewTouchManagerListener, View bgView, SurfaceViewAnimator surfaceViewAnimator) {
        mBgView = bgView;
        mSurfaceViewAnimator = surfaceViewAnimator;
        mReadViewTouchManagerListener = readViewTouchManagerListener;
    }

    public void setShadowColor(int color){
        mSurfaceViewAnimator.setShadowColor(color);
    }

    public void setMask(boolean mask){
        mMask = mask;
    }
    @Override
    public synchronized boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            mChangeDirect = false;
            flipStart(event);
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
            flipChange(event.getX());
        }else if ((event.getAction() == MotionEvent.ACTION_UP)){
            flipEnd(event.getDownTime(), event.getEventTime(), event.getX());
        }
        return true;
    }

    public void setFlipMode(int flipMode) {
        mFlipMode = flipMode;
    }

    private void flipStart(MotionEvent event) {
        mDragStart = false;
        mDisFlip = false;
        mDownTouchX = event.getX();
        mDownTouchY = event.getY();
    }

    private void flipChange(float x) {
        if (mFlipMode == 0 || mReadViewTouchManagerListener.menuShowed())
            return;
        if (!mDragStart && Math.abs(x - mDownTouchX) < 10){
            return;
        }
        preFlip(x);
        mDownTouchX = x;
    }

    private void preFlip(float x) {
        if (mDisFlip)
            return;
        if (!mDragStart){
            if (x < mDownTouchX) {
                mDragDirect = true;
                mSurfaceViewAnimator.refreshPreBitmap(mBgView, mMask);
                if (!mReadViewTouchManagerListener.nextPage(false)){
                    mDisFlip = true;
                    return;
                }
                mSurfaceViewAnimator.refreshCurBitmap(mBgView, false, mMask);
                mSurfaceViewAnimator.prepare((int)(mDownTouchX - x), true);
            } else {
                mDragDirect = false;
                mSurfaceViewAnimator.refreshCurBitmap(mBgView, true, mMask);
                if (!mReadViewTouchManagerListener.prePage(false)){
                    mDisFlip = true;
                    return;
                }
                mSurfaceViewAnimator.refreshPreBitmap(mBgView, mMask);
                mSurfaceViewAnimator.prepare((int)(mDownTouchX - x), false);
            }
            mDragStart = true;
            return;
        }
        if (mDragDirect) {
            mSurfaceViewAnimator.drag((int) (mDownTouchX - x));
        }else {
            mSurfaceViewAnimator.drag((int) (x - mDownTouchX));
        }
        mChangeDirect = false;
        if (x > (mDownTouchX) && mDragDirect) {
            mChangeDirect = true;
        }else if(x < (mDownTouchX) && !mDragDirect){
            mChangeDirect = true;
        }
    }

    private void flipEnd(long downTime, long upTime, float x) {
        if (mDisFlip)
            return;
        long diff = upTime - downTime;
        if (diff < 1000 && !mDragStart) {//点击事件
            if (mFlipMode == 0 || mReadViewTouchManagerListener.menuShowed()){//菜单显示
                mReadViewTouchManagerListener.click(mDownTouchX, mDownTouchY, true);
                return;
            }else {
                int result = mReadViewTouchManagerListener.click(mDownTouchX, mDownTouchY, false);
                if (result == 0) {
                    return;
                }
                else if (result == 1)
                    preFlip(mDownTouchX + 1);
                else if (result == 2)
                    preFlip(mDownTouchX - 1);
            }
        }
        if (!mDragStart)//长按事件
            return;
        surfaceViewAniatorStart((int)(mDownTouchX - x));
    }
    private void surfaceViewAniatorStart(int start) {
        if (mChangeDirect) {
            if (mDragDirect) {
                mSurfaceViewAnimator.refreshCurBitmap(mBgView, true, mMask);
                mReadViewTouchManagerListener.prePage(true);
            }else {
                mReadViewTouchManagerListener.nextPage(true);
            }
            mDragDirect = !mDragDirect;
        }
        mSurfaceViewAnimator.start(start, mDragDirect, mChangeDirect, ()->{

        });
    }

    public void refreshCurPageView() {

    }

    public interface ReadViewTouchManagerListener {
        boolean nextPage(boolean reflip);
        boolean prePage(boolean reflip);
        int click(float x, float y, boolean flip);
        boolean menuShowed();
    }
}
