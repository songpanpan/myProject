package com.yueyou.adreader.view.ViewPager;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import java.util.Arrays;

/**
 * ViewGuestureHelper是一个手势工具类用类。
 * @author huangjian
 */
public class ViewGuestureHelper {
	public static final int MAX_POINTER_ID 				= 15; //最多支持15点触摸
	/**
     * 初始触摸手指个数
     */
    public static final int INVALID_POINTER 			= -1;

    public static final int STATE_IDLE 					= 0;

    public static final int GRADIENT 					= 2;
    
    public static final float LOCK_GRADIENT				= 0.8f;//当水平与垂直方向的移动比值小于该值，则锁住当前手势，即在此触摸事件下不会响应手势操作
    /**
     * 拖动状态
     */
    public static final int STATE_DRAGGING 				= 1;

    public static final int STATE_SETTLING 				= 2;

    public static final int EDGE_LEFT 					= 1 << 0;

    public static final int EDGE_RIGHT 					= 1 << 1;

    public static final int EDGE_TOP 					= 1 << 2;

    public static final int EDGE_BOTTOM 				= 1 << 3;

    public static final int EDGE_ALL 					= EDGE_LEFT | EDGE_TOP | EDGE_RIGHT | EDGE_BOTTOM;

    /**
     * 水平方向检查位移
     */
    public static final int DIRECTION_HORIZONTAL 		= 1 << 0;

    /**
     * 垂直方向检查位移
     */
    public static final int DIRECTION_VERTICAL 			= 1 << 1;

    /**
     * 水平与垂直方向都检查位移
     */
    public static final int DIRECTION_ALL 				= DIRECTION_HORIZONTAL | DIRECTION_VERTICAL;

    /**
     * 边缘宽度
     */
    public static final int EDGE_SIZE 					= 20;  // 单位dp

    private static final int BASE_SETTLE_DURATION 		= 256; // 单位ms

    private static final int MAX_SETTLE_DURATION 		= 600; // 单位ms

    private int mDragState;

    private int mTouchSlop;

    private int mActivePointerId 						= INVALID_POINTER;

    private float[] mInitialMotionX;

    private float[] mInitialMotionY;

    private float[] mLastMotionX;

    private float[] mLastMotionY;

    private int[] mInitialEdgesTouched;

    private int[] mEdgeDragsInProgress;

    private int[] mEdgeDragsLocked;

    private int mPointersDown;

    private VelocityTracker mVelocityTracker;

    private float mMaxVelocity;

    private float mMinVelocity;

    private int mEdgeSize;

    private int mTrackingEdges;

    private ScrollerCompat mScroller;

    private final Callback mCallback;

    private View mCapturedView;

    private boolean mReleaseInProgress;

    private final ViewGroup mParentView;

    public static abstract class Callback {
        /**
         * 当拖动状态改变时调用
         * 可能的状态改变有：
         * @param state The new drag state
         * @see #STATE_IDLE
         * @see #STATE_DRAGGING
         * @see #STATE_SETTLING
         */
        public void onViewDragStateChanged(int state) {
        }

        /**
         * 当view的位置发生变化时调用
         * 
         * @param changedView 位置发生变化的view
         * @param left 
         * @param top 
         * @param dx 与上次调用相比发生的x偏移量
         * @param dy 与上次调用相比发生的y偏移量
         */
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
        }

        /**
         * 当view获取到焦点时调用。
         * 
         * @param capturedChild 获取到焦点的view
         * @param activePointerId 当前的多指个数
         */
        public void onViewCaptured(View capturedChild, int activePointerId) {
        }

        /**
         * 当view失去焦点时调用。
         * 
         * @param releasedChild 失去焦点的view
         * @param xvel x方向的速度 
         * @param yvel y方向的速度
         */
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
        }

        /**
         * 当触摸在边缘时调用
         * 
         * @param edgeFlags 边缘的flag，可能的值如下
         * @see #EDGE_LEFT
         * @see #EDGE_TOP
         * @see #EDGE_RIGHT
         * @see #EDGE_BOTTOM
         * @param pointerId 触摸手指个数
         */
        public void onEdgeTouched(int edgeFlags, int pointerId) {
        }

        public boolean onEdgeLock(int edgeFlags) {
            return false;
        }

        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
        }

        public int getOrderedChildIndex(int index) {
            return index;
        }

        public int getViewHorizontalDragRange(View child) {
            return 0;
        }

        public int getViewVerticalDragRange(View child) {
            return 0;
        }

        public abstract boolean tryCaptureView(View child, int pointerId);

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return 0;
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }
    }

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    private final Runnable mSetIdleRunnable = new Runnable() {
        public void run() {
            setDragState(STATE_IDLE);
        }
    };

    public static ViewGuestureHelper create(ViewGroup forParent, Callback cb) {
        return new ViewGuestureHelper(forParent.getContext(), forParent, cb);
    }

    public static ViewGuestureHelper create(ViewGroup forParent, float sensitivity, Callback cb) {
        final ViewGuestureHelper helper = create(forParent, cb);
        helper.mTouchSlop = (int) (helper.mTouchSlop * (1 / sensitivity));
        return helper;
    }

    private ViewGuestureHelper(Context context, ViewGroup forParent, Callback cb) {
        if (forParent == null) {
            throw new IllegalArgumentException("Parent view may not be null");
        }
        if (cb == null) {
            throw new IllegalArgumentException("Callback may not be null");
        }

        mParentView = forParent;
        mCallback = cb;

        final ViewConfiguration vc = ViewConfiguration.get(context);
        final float density = context.getResources().getDisplayMetrics().density;
        mEdgeSize = (int) (EDGE_SIZE * density + 0.5f);

        mTouchSlop = vc.getScaledTouchSlop()/2;//解决有些手机中存在触摸焦点被其他控件获取的情况
        mMaxVelocity = vc.getScaledMaximumFlingVelocity();
        mMinVelocity = vc.getScaledMinimumFlingVelocity();
        mScroller = ScrollerCompat.create(context, sInterpolator);
    }

    public void setSensitivity(Context context, float sensitivity) {
        float s = Math.max(0f, Math.min(1.0f, sensitivity));
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = (int) (viewConfiguration.getScaledTouchSlop() * (1 / s));
    }

    public void setMinVelocity(float minVel) {
        mMinVelocity = minVel;
    }

    public float getMinVelocity() {
        return mMinVelocity;
    }

    public int getViewDragState() {
        return mDragState;
    }

    public void setEdgeTrackingEnabled(int edgeFlags) {
        mTrackingEdges = edgeFlags;
    }

    /**
     * 获取边缘宽度，单位px
     */
    public int getEdgeSize() {
        return mEdgeSize;
    }

    /**
     * 设置边缘宽度
     * 
     * @param size 边缘宽度，单位px
     */
    public void setEdgeSize(int size) {
        mEdgeSize = size;
    }

    public void captureChildView(View childView, int activePointerId) {
        if (childView.getParent() != mParentView) {
            throw new IllegalArgumentException("captureChildView: parameter must be a descendant "
                    + "of the ViewDragHelper's tracked parent view (" + mParentView + ")");
        }

        mCapturedView = childView;
        mActivePointerId = activePointerId;
        mCallback.onViewCaptured(childView, activePointerId);
        setDragState(STATE_DRAGGING);
    }

    /**
     * 返回获取到焦点的view
     */
    public View getCapturedView() {
        return mCapturedView;
    }
    
    public int getActivePointerId() {
        return mActivePointerId;
    }

    public int getTouchSlop() {
        return mTouchSlop;
    }

    public void cancel() {
        mActivePointerId = INVALID_POINTER;
        clearMotionHistory();

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void abort() {
        cancel();
        if (mDragState == STATE_SETTLING) {
            final int oldX = mScroller.getCurrX();
            final int oldY = mScroller.getCurrY();
            mScroller.abortAnimation();
            final int newX = mScroller.getCurrX();
            final int newY = mScroller.getCurrY();
            mCallback.onViewPositionChanged(mCapturedView, newX, newY, newX - oldX, newY - oldY);
        }
        setDragState(STATE_IDLE);
    }

    public boolean smoothSlideViewTo(View child, int finalLeft, int finalTop) {
        mCapturedView = child;
        mActivePointerId = INVALID_POINTER;

        return forceSettleCapturedViewAt(finalLeft, finalTop, 0, 0);
    }

    public boolean settleCapturedViewAt(int finalLeft, int finalTop) {
        if (!mReleaseInProgress) {
            throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to "
                    + "Callback#onViewReleased");
        }

        return forceSettleCapturedViewAt(finalLeft, finalTop,
                (int) VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId),
                (int) VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId));
    }

    private boolean forceSettleCapturedViewAt(int finalLeft, int finalTop, int xvel, int yvel) {
        final int startLeft = mCapturedView.getLeft();
        final int startTop = mCapturedView.getTop();
        final int dx = finalLeft - startLeft;
        final int dy = finalTop - startTop;

        if (dx == 0 && dy == 0) {
            mScroller.abortAnimation();
            setDragState(STATE_IDLE);
            return false;
        }

        final int duration = computeSettleDuration(mCapturedView, dx, dy, xvel, yvel);
        mScroller.startScroll(startLeft, startTop, dx, dy, duration);

        setDragState(STATE_SETTLING);
        return true;
    }

    private int computeSettleDuration(View child, int dx, int dy, int xvel, int yvel) {
        xvel = clampMag(xvel, (int) mMinVelocity, (int) mMaxVelocity);
        yvel = clampMag(yvel, (int) mMinVelocity, (int) mMaxVelocity);
        final int absDx = Math.abs(dx);
        final int absDy = Math.abs(dy);
        final int absXVel = Math.abs(xvel);
        final int absYVel = Math.abs(yvel);
        final int addedVel = absXVel + absYVel;
        final int addedDistance = absDx + absDy;

        final float xweight = xvel != 0 ? (float) absXVel / addedVel : (float) absDx
                / addedDistance;
        final float yweight = yvel != 0 ? (float) absYVel / addedVel : (float) absDy
                / addedDistance;

        int xduration = computeAxisDuration(dx, xvel, mCallback.getViewHorizontalDragRange(child));
        int yduration = computeAxisDuration(dy, yvel, mCallback.getViewVerticalDragRange(child));

        return (int) (xduration * xweight + yduration * yweight);
    }

    private int computeAxisDuration(int delta, int velocity, int motionRange) {
        if (delta == 0) {
            return 0;
        }

        final int width = mParentView.getWidth();
        final int halfWidth = width / 2;
        final float distanceRatio = Math.min(1f, (float) Math.abs(delta) / width);
        final float distance = halfWidth + halfWidth
                * distanceInfluenceForSnapDuration(distanceRatio);

        int duration;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float range = (float) Math.abs(delta) / motionRange;
            duration = (int) ((range + 1) * BASE_SETTLE_DURATION);
        }
        return Math.min(duration, MAX_SETTLE_DURATION);
    }

    private int clampMag(int value, int absMin, int absMax) {
        final int absValue = Math.abs(value);
        if (absValue < absMin)
            return 0;
        if (absValue > absMax)
            return value > 0 ? absMax : -absMax;
        return value;
    }

    private float clampMag(float value, float absMin, float absMax) {
        final float absValue = Math.abs(value);
        if (absValue < absMin)
            return 0;
        if (absValue > absMax)
            return value > 0 ? absMax : -absMax;
        return value;
    }

    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; 
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    public void flingCapturedView(int minLeft, int minTop, int maxLeft, int maxTop) {
        if (!mReleaseInProgress) {
            throw new IllegalStateException("Cannot flingCapturedView outside of a call to "
                    + "Callback#onViewReleased");
        }

        mScroller.fling(mCapturedView.getLeft(), mCapturedView.getTop(),
                (int) VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId),
                (int) VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId),
                minLeft, maxLeft, minTop, maxTop);

        setDragState(STATE_SETTLING);
    }

    public boolean continueSettling(boolean deferCallbacks) {
        if (mDragState == STATE_SETTLING) {
            boolean keepGoing = mScroller.computeScrollOffset();
            final int x = mScroller.getCurrX();
            final int y = mScroller.getCurrY();
            final int dx = x - mCapturedView.getLeft();
            final int dy = y - mCapturedView.getTop();

            if (dx != 0) {
                mCapturedView.offsetLeftAndRight(dx);
            }
            if (dy != 0) {
                mCapturedView.offsetTopAndBottom(dy);
            }

            if (dx != 0 || dy != 0) {
                mCallback.onViewPositionChanged(mCapturedView, x, y, dx, dy);
            }

            if (keepGoing && x == mScroller.getFinalX() && y == mScroller.getFinalY()) {
                mScroller.abortAnimation();
                keepGoing = mScroller.isFinished();
            }

            if (!keepGoing) {
                if (deferCallbacks) {
                    mParentView.post(mSetIdleRunnable);
                } else {
                    setDragState(STATE_IDLE);
                }
            }
        }

        return mDragState == STATE_SETTLING;
    }

    private void dispatchViewReleased(float xvel, float yvel) {
        mReleaseInProgress = true;
        mCallback.onViewReleased(mCapturedView, xvel, yvel);
        mReleaseInProgress = false;

        if (mDragState == STATE_DRAGGING) {
            setDragState(STATE_IDLE);
        }
    }

    private void clearMotionHistory() {
        if (mInitialMotionX == null) {
            return;
        }
        Arrays.fill(mInitialMotionX, 0);
        Arrays.fill(mInitialMotionY, 0);
        Arrays.fill(mLastMotionX, 0);
        Arrays.fill(mLastMotionY, 0);
        Arrays.fill(mInitialEdgesTouched, 0);
        Arrays.fill(mEdgeDragsInProgress, 0);
        Arrays.fill(mEdgeDragsLocked, 0);
        mPointersDown = 0;
    }

    private void clearMotionHistory(int pointerId) {
        if (mInitialMotionX == null) {
            return;
        }
        if(mInitialMotionX.length >= pointerId|| 
        		mLastMotionX.length >= pointerId||
        		mInitialEdgesTouched.length >= pointerId||
        		mEdgeDragsInProgress.length >= pointerId||
        		mEdgeDragsLocked.length >= pointerId) 
        	return;
        mInitialMotionX[pointerId] = 0;
        mInitialMotionY[pointerId] = 0;
        mLastMotionX[pointerId] = 0;
        mLastMotionY[pointerId] = 0;
        mInitialEdgesTouched[pointerId] = 0;
        mEdgeDragsInProgress[pointerId] = 0;
        mEdgeDragsLocked[pointerId] = 0;
        mPointersDown &= ~(1 << pointerId);
    }

    private void ensureMotionHistorySizeForId(int pointerId) {
    	if(mInitialMotionX == null){
	    	mInitialMotionX = new float[MAX_POINTER_ID];
	    	mInitialMotionY = new float[MAX_POINTER_ID];
	    	mLastMotionX = new float[MAX_POINTER_ID];
	    	mLastMotionY = new float[MAX_POINTER_ID];
	    	mInitialEdgesTouched = new int[MAX_POINTER_ID];
	    	mEdgeDragsInProgress = new int[MAX_POINTER_ID];
	    	mEdgeDragsLocked = new int[MAX_POINTER_ID];
    	}
    	
    	
//        if (mInitialMotionX == null || mInitialMotionX.length <= pointerId) {
//            float[] imx = new float[pointerId + 1];
//            float[] imy = new float[pointerId + 1];
//            float[] lmx = new float[pointerId + 1];
//            float[] lmy = new float[pointerId + 1];
//            int[] iit = new int[pointerId + 1];
//            int[] edip = new int[pointerId + 1];
//            int[] edl = new int[pointerId + 1];
//
//            if (mInitialMotionX != null) {
//                System.arraycopy(mInitialMotionX, 0, imx, 0, mInitialMotionX.length);
//                System.arraycopy(mInitialMotionY, 0, imy, 0, mInitialMotionY.length);
//                System.arraycopy(mLastMotionX, 0, lmx, 0, mLastMotionX.length);
//                System.arraycopy(mLastMotionY, 0, lmy, 0, mLastMotionY.length);
//                System.arraycopy(mInitialEdgesTouched, 0, iit, 0, mInitialEdgesTouched.length);
//                System.arraycopy(mEdgeDragsInProgress, 0, edip, 0, mEdgeDragsInProgress.length);
//                System.arraycopy(mEdgeDragsLocked, 0, edl, 0, mEdgeDragsLocked.length);
//            }
//
//            mInitialMotionX = imx;
//            mInitialMotionY = imy;
//            mLastMotionX = lmx;
//            mLastMotionY = lmy;
//            mInitialEdgesTouched = iit;
//            mEdgeDragsInProgress = edip;
//            mEdgeDragsLocked = edl;
//        }
    }

    private void saveInitialMotion(float x, float y, int pointerId) {
        ensureMotionHistorySizeForId(pointerId);
        mInitialMotionX[pointerId] = mLastMotionX[pointerId] = x;
        mInitialMotionY[pointerId] = mLastMotionY[pointerId] = y;
        mInitialEdgesTouched[pointerId] = getEdgesTouched((int) x, (int) y);
        mPointersDown |= 1 << pointerId;
    }

    private void saveLastMotion(MotionEvent ev) {
        int pointerCount = MotionEventCompat.getPointerCount(ev);
        if(mLastMotionX == null)return;
        int length = mLastMotionX.length;
        pointerCount = pointerCount < length ? pointerCount : length;
        for (int i = 0; i < pointerCount; i++) {
            final int pointerId = MotionEventCompat.getPointerId(ev, i);
            final float x = MotionEventCompat.getX(ev, i);
            final float y = MotionEventCompat.getY(ev, i);
            mLastMotionX[pointerId] = x;
            mLastMotionY[pointerId] = y;
        }
    }

    public boolean isPointerDown(int pointerId) {
        return (mPointersDown & 1 << pointerId) != 0;
    }

    void setDragState(int state) {
        if (mDragState != state) {
            mDragState = state;
            mCallback.onViewDragStateChanged(state);
            if (state == STATE_IDLE) {
                mCapturedView = null;
            }
        }
    }

    boolean tryCaptureViewForDrag(View toCapture, int pointerId) {
        if (toCapture == mCapturedView && mActivePointerId == pointerId) {
            return true;
        }
        if (toCapture != null && mCallback.tryCaptureView(toCapture, pointerId)) {
            mActivePointerId = pointerId;
            captureChildView(toCapture, pointerId);
            return true;
        }
        return false;
    }

    protected boolean canScroll(View v, boolean checkV, int dx, int dy, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft()
                        && x + scrollX < child.getRight()
                        && y + scrollY >= child.getTop()
                        && y + scrollY < child.getBottom()
                        && canScroll(child, true, dx, dy, x + scrollX - child.getLeft(), y
                                + scrollY - child.getTop())) {
                    return true;
                }
            }
        }

        return checkV
                && (ViewCompat.canScrollHorizontally(v, -dx) || ViewCompat.canScrollVertically(v,
                        -dy));
    }

    public boolean shouldInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        final int actionIndex = MotionEventCompat.getActionIndex(ev);

        if (action == MotionEvent.ACTION_DOWN) {
            cancel();
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                final int pointerId = MotionEventCompat.getPointerId(ev, 0);
                saveInitialMotion(x, y, pointerId);

                final View toCapture = findTopChildUnder((int) x, (int) y);

                if (toCapture == mCapturedView && mDragState == STATE_SETTLING) {
                    tryCaptureViewForDrag(toCapture, pointerId);
                }

                final int edgesTouched = mInitialEdgesTouched[pointerId];
                if ((edgesTouched & mTrackingEdges) != 0) {
                    mCallback.onEdgeTouched(edgesTouched & mTrackingEdges, pointerId);
                }
                break;
            }

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int pointerId = MotionEventCompat.getPointerId(ev, actionIndex);
                final float x = MotionEventCompat.getX(ev, actionIndex);
                final float y = MotionEventCompat.getY(ev, actionIndex);

                saveInitialMotion(x, y, pointerId);

                if (mDragState == STATE_IDLE) {
                    final int edgesTouched = mInitialEdgesTouched[pointerId];
                    if ((edgesTouched & mTrackingEdges) != 0) {
                        mCallback.onEdgeTouched(edgesTouched & mTrackingEdges, pointerId);
                    }
                } else if (mDragState == STATE_SETTLING) {
                    final View toCapture = findTopChildUnder((int) x, (int) y);
                    if (toCapture == mCapturedView) {
                        tryCaptureViewForDrag(toCapture, pointerId);
                    }
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
            	if(mInitialMotionX == null || mInitialMotionY == null)
            		break;
                final int pointerCount = MotionEventCompat.getPointerCount(ev);
                for (int i = 0; i < pointerCount; i++) {
                    final int pointerId = MotionEventCompat.getPointerId(ev, i);
                    final float x = MotionEventCompat.getX(ev, i);
                    final float y = MotionEventCompat.getY(ev, i);
                    final float dx = x - mInitialMotionX[pointerId];
                    final float dy = y - mInitialMotionY[pointerId];

                    reportNewEdgeDrags(dx, dy, pointerId);
                    if (mDragState == STATE_DRAGGING) {
                        break;
                    }

                    final View toCapture = findTopChildUnder((int) x, (int) y);
                    
        			
                    if (toCapture != null && checkTouchSlop(toCapture, dx, dy)&&checkTouchGradient(dx, dy)
                            && tryCaptureViewForDrag(toCapture, pointerId)) {
                        break;
                    }
                }
                saveLastMotion(ev);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP: {
                final int pointerId = MotionEventCompat.getPointerId(ev, actionIndex);
                clearMotionHistory(pointerId);
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                cancel();
                break;
            }
        }

        return mDragState == STATE_DRAGGING;
    }

    public void processTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        final int actionIndex = MotionEventCompat.getActionIndex(ev);

        if (action == MotionEvent.ACTION_DOWN) {
            cancel();
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                final int pointerId = MotionEventCompat.getPointerId(ev, 0);
                final View toCapture = findTopChildUnder((int) x, (int) y);

                saveInitialMotion(x, y, pointerId);

//                if (toCapture == mCapturedView && mDragState == STATE_SETTLING) {
                    tryCaptureViewForDrag(toCapture, pointerId);
//                }

                final int edgesTouched = mInitialEdgesTouched[pointerId];
                if ((edgesTouched & mTrackingEdges) != 0) {
                    mCallback.onEdgeTouched(edgesTouched & mTrackingEdges, pointerId);
                }
                break;
            }

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int pointerId = MotionEventCompat.getPointerId(ev, actionIndex);
                final float x = MotionEventCompat.getX(ev, actionIndex);
                final float y = MotionEventCompat.getY(ev, actionIndex);

                saveInitialMotion(x, y, pointerId);

                if (mDragState == STATE_IDLE) {

                    final View toCapture = findTopChildUnder((int) x, (int) y);
                    tryCaptureViewForDrag(toCapture, pointerId);

                    final int edgesTouched = mInitialEdgesTouched[pointerId];
                    if ((edgesTouched & mTrackingEdges) != 0) {
                        mCallback.onEdgeTouched(edgesTouched & mTrackingEdges, pointerId);
                    }
                } else if (isCapturedViewUnder((int) x, (int) y)) {
                    tryCaptureViewForDrag(mCapturedView, pointerId);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (mDragState == STATE_DRAGGING) {
                	if(mActivePointerId <0)break;
                    int index = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    if(index<0){
                    	index =0;
                    }else if(index > ev.getPointerCount()){
                    	index=ev.getPointerCount()-1;
                    }
                    final float x = MotionEventCompat.getX(ev, index);
                    final float y = MotionEventCompat.getY(ev, index);
                    final int idx = (int) (x - mLastMotionX[mActivePointerId]);
                    final int idy = (int) (y - mLastMotionY[mActivePointerId]);

                    dragTo(mCapturedView.getLeft() + idx, mCapturedView.getTop() + idy, idx, idy);

                    saveLastMotion(ev);
                } else {
                    final int pointerCount = MotionEventCompat.getPointerCount(ev);
                    for (int i = 0; i < pointerCount; i++) {
                        final int pointerId = MotionEventCompat.getPointerId(ev, i);
                        final float x = MotionEventCompat.getX(ev, i);
                        final float y = MotionEventCompat.getY(ev, i);
                        final float dx = x - mInitialMotionX[pointerId];
                        final float dy = y - mInitialMotionY[pointerId];

                        reportNewEdgeDrags(dx, dy, pointerId);
                        if (mDragState == STATE_DRAGGING) {
                            break;
                        }

                        final View toCapture = findTopChildUnder((int) x, (int) y);
                        if (checkTouchSlop(toCapture, dx, dy)
                                && tryCaptureViewForDrag(toCapture, pointerId)) {
                            break;
                        }
                    }
                    saveLastMotion(ev);
                }
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP: {
                final int pointerId = MotionEventCompat.getPointerId(ev, actionIndex);
                if (mDragState == STATE_DRAGGING && pointerId == mActivePointerId) {
                    int newActivePointer = INVALID_POINTER;
                    final int pointerCount = MotionEventCompat.getPointerCount(ev);
                    for (int i = 0; i < pointerCount; i++) {
                        final int id = MotionEventCompat.getPointerId(ev, i);
                        if (id == mActivePointerId) {
                            continue;
                        }

                        final float x = MotionEventCompat.getX(ev, i);
                        final float y = MotionEventCompat.getY(ev, i);
                        if (findTopChildUnder((int) x, (int) y) == mCapturedView
                                && tryCaptureViewForDrag(mCapturedView, id)) {
                            newActivePointer = mActivePointerId;
                            break;
                        }
                    }

                    if (newActivePointer == INVALID_POINTER) {
                        releaseViewForPointerUp();
                    }
                }
                clearMotionHistory(pointerId);
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (mDragState == STATE_DRAGGING) {
                    releaseViewForPointerUp();
                }
                cancel();
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                if (mDragState == STATE_DRAGGING) {
                    dispatchViewReleased(0, 0);
                }
                cancel();
                break;
            }
        }
    }

    private void reportNewEdgeDrags(float dx, float dy, int pointerId) {
        int dragsStarted = 0;
        if (checkNewEdgeDrag(dx, dy, pointerId, EDGE_LEFT)) {
            dragsStarted |= EDGE_LEFT;
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, EDGE_TOP)) {
            dragsStarted |= EDGE_TOP;
        }
        if (checkNewEdgeDrag(dx, dy, pointerId, EDGE_RIGHT)) {
            dragsStarted |= EDGE_RIGHT;
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, EDGE_BOTTOM)) {
            dragsStarted |= EDGE_BOTTOM;
        }

        if (dragsStarted != 0) {
            mEdgeDragsInProgress[pointerId] |= dragsStarted;
            mCallback.onEdgeDragStarted(dragsStarted, pointerId);
        }
    }

    private boolean checkNewEdgeDrag(float delta, float odelta, int pointerId, int edge) {
        final float absDelta = Math.abs(delta);
        final float absODelta = Math.abs(odelta);
        if ((mInitialEdgesTouched[pointerId] & edge) != edge || (mTrackingEdges & edge) == 0
                || (mEdgeDragsLocked[pointerId] & edge) == edge
                || (mEdgeDragsInProgress[pointerId] & edge) == edge
                || (absDelta <= mTouchSlop && absODelta <= mTouchSlop)) {
            return false;
        }
        if (absDelta < absODelta * LOCK_GRADIENT && mCallback.onEdgeLock(edge)) {
            mEdgeDragsLocked[pointerId] |= edge;
            return false;
        }
        return (mEdgeDragsInProgress[pointerId] & edge) == 0 && absDelta > mTouchSlop && absDelta/absODelta > GRADIENT;
    }

    private boolean checkTouchSlop(View child, float dx, float dy) {
        if (child == null) {
            return false;
        }
        final boolean checkHorizontal = mCallback.getViewHorizontalDragRange(child) > 0;
        final boolean checkVertical = mCallback.getViewVerticalDragRange(child) > 0;

        if (checkHorizontal && checkVertical) {
            return dx * dx + dy * dy > mTouchSlop * mTouchSlop;
        } else if (checkHorizontal) {
            return Math.abs(dx) > mTouchSlop;
        } else if (checkVertical) {
            return Math.abs(dy) > mTouchSlop;
        }
        return false;
    }

    private boolean checkTouchGradient(float dx, float dy){
    	if(((mTrackingEdges & EDGE_LEFT) == EDGE_LEFT || (mTrackingEdges & EDGE_RIGHT) == EDGE_RIGHT) && Math.abs(dx/dy)>GRADIENT){
    		return true;
    	}
    	
    	if(((mTrackingEdges & EDGE_BOTTOM) == EDGE_BOTTOM || (mTrackingEdges & EDGE_TOP) == EDGE_TOP) && Math.abs(dy/dx)>GRADIENT){
    		return true;
    	}
    	
    	return false;
    }
    
    public boolean checkTouchSlop(int directions) {
        final int count = mInitialMotionX.length;
        for (int i = 0; i < count; i++) {
            if (checkTouchSlop(directions, i)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkTouchSlop(int directions, int pointerId) {
        if (!isPointerDown(pointerId)) {
            return false;
        }

        final boolean checkHorizontal = (directions & DIRECTION_HORIZONTAL) == DIRECTION_HORIZONTAL;
        final boolean checkVertical = (directions & DIRECTION_VERTICAL) == DIRECTION_VERTICAL;

        final float dx = mLastMotionX[pointerId] - mInitialMotionX[pointerId];
        final float dy = mLastMotionY[pointerId] - mInitialMotionY[pointerId];

        if (checkHorizontal && checkVertical) {
            return dx * dx + dy * dy > mTouchSlop * mTouchSlop;
        } else if (checkHorizontal) {
            return Math.abs(dx) > mTouchSlop;
        } else if (checkVertical) {
            return Math.abs(dy) > mTouchSlop;
        }
        return false;
    }

    public boolean isEdgeTouched(int edges) {
        final int count = mInitialEdgesTouched.length;
        for (int i = 0; i < count; i++) {
            if (isEdgeTouched(edges, i)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEdgeTouched(int edges, int pointerId) {
        return isPointerDown(pointerId) && (mInitialEdgesTouched[pointerId] & edges) != 0;
    }

    private void releaseViewForPointerUp() {
        mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
        final float xvel = clampMag(
                VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId),
                mMinVelocity, mMaxVelocity);
        final float yvel = clampMag(
                VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId),
                mMinVelocity, mMaxVelocity);
        dispatchViewReleased(xvel, yvel);
    }

    private void dragTo(int left, int top, int dx, int dy) {
        int clampedX = left;
        int clampedY = top;
        final int oldLeft = mCapturedView.getLeft();
        final int oldTop = mCapturedView.getTop();
        if (dx != 0) {
            clampedX = mCallback.clampViewPositionHorizontal(mCapturedView, left, dx);
            mCapturedView.offsetLeftAndRight(clampedX - oldLeft);
        }
        if (dy != 0) {
            clampedY = mCallback.clampViewPositionVertical(mCapturedView, top, dy);
            mCapturedView.offsetTopAndBottom(clampedY - oldTop);
        }

        if (dx != 0 || dy != 0) {
            final int clampedDx = clampedX - oldLeft;
            final int clampedDy = clampedY - oldTop;
            mCallback
                    .onViewPositionChanged(mCapturedView, clampedX, clampedY, clampedDx, clampedDy);
        }
    }

    public boolean isCapturedViewUnder(int x, int y) {
        return isViewUnder(mCapturedView, x, y);
    }

    public boolean isViewUnder(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        return x >= view.getLeft() && x < view.getRight() && y >= view.getTop()
                && y < view.getBottom();
    }

    public View findTopChildUnder(int x, int y) {
        final int childCount = mParentView.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = mParentView.getChildAt(mCallback.getOrderedChildIndex(i));
            if (x >= child.getLeft() && x < child.getRight() && y >= child.getTop()
                    && y < child.getBottom()) {
                return child;
            }
        }
        return null;
    }

    private int getEdgesTouched(int x, int y) {
        int result = 0;

        if (x < mParentView.getLeft() + mEdgeSize)
            result |= EDGE_LEFT;
        if (y < mParentView.getTop() + mEdgeSize)
            result |= EDGE_TOP;
        if (x > mParentView.getRight() - mEdgeSize)
            result |= EDGE_RIGHT;
        if (y > mParentView.getBottom() - mEdgeSize)
            result |= EDGE_BOTTOM;

        return result;
    }
}
