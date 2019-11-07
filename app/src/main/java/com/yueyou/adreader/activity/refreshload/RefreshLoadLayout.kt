package com.yueyou.adreader.activity.refreshload

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.support.annotation.Nullable
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.*
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.util.Log
import android.view.Gravity
import com.yueyou.adreader.BuildConfig
import com.yueyou.adreader.R
import com.yueyou.adreader.util.ScreenUtils
import com.yueyou.adreader.util.SimpleAnimatorListener


/**
 * Created by x on 2017/8/14.
 */
class RefreshLoadLayout(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) :
        FrameLayout(context, attrs, defStyleAttr), NestedScrollingParent, NestedScrollingChild {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, @Nullable attrs: AttributeSet) : this(context, attrs, 0)

    val TAG = "RefreshLoadLayout"

    companion object {
        val MODE_NONE = 0
        /**
         * 下拉模式
         */
        val MODE_REFRESH = 1
        /**
         * 上拉模式
         */
        val MODE_LOAD = 1 shl 1
        /**
         * 上拉和下拉
         */
        val MODE_BOTH = MODE_REFRESH or MODE_LOAD
    }

    enum class ScrollState(val value: Int) {
        STATE_IDLE(1),
        STATE_TOUCH_SCROLL(1 shl 1),
        STATE_FLING_SCROLL(1 shl 2)
    }

    private val SPINNER_WIDTH = ScreenUtils.dpToPxInt(context, 36f)

    private val SPINNER_OFFSET_END = ScreenUtils.dpToPxInt(context, 120f)

    private val mParentHelper = NestedScrollingParentHelper(this)
    /**
     * Default background for the progress spinner
     */
    private var mCircleProgressColor = ResourcesCompat.getColor(resources, R.color.refresh, context.theme)
    private var mCircleProgressBg = ResourcesCompat.getColor(resources, R.color.tt_white, context.theme)
    private var mMode = MODE_BOTH
    /**
     * 滑动状态
     */
    private var mState = ScrollState.STATE_IDLE

    /**
     * 圆形旋转器
     */
    private var mCircleView = CircleImageView(context, mCircleProgressBg)
    /**
     * 进度条
     */
    private var mProgress = MaterialProgressDrawable(context, this)
    /**
     * view拦截的距离
     */
    private var mTotalUnconsumed = 0

    private var mListener: RefreshLoadListener? = null

    internal var mBackAnimator = ObjectAnimator.ofFloat(mCircleView, "translationY", 0f).setDuration(150)

    init {
        attrs?.let {
            var typedArray = context?.obtainStyledAttributes(it, R.styleable.RefreshLoadLayout)
            typedArray?.let {
                if (it.hasValue(R.styleable.RefreshLoadLayout_pColor)) {
                    mCircleProgressColor = it.getColor(R.styleable.RefreshLoadLayout_pColor, mCircleProgressColor)
                }
                if (it.hasValue(R.styleable.RefreshLoadLayout_pBackground)) {
                    mCircleProgressBg = it.getColor(R.styleable.RefreshLoadLayout_pBackground, mCircleProgressBg)
                }
                if (it.hasValue(R.styleable.RefreshLoadLayout_pMode)) {
                    mMode = it.getInt(R.styleable.RefreshLoadLayout_pMode, mMode)
                }
            }
        }
        mProgress.apply {
            setColorSchemeColors(mCircleProgressColor)
            setBackgroundColor(mCircleProgressBg)
            alpha = 255
        }
        mCircleView.apply {
            setImageDrawable(mProgress)
            this@RefreshLoadLayout.addView(this)
            var lp = layoutParams as FrameLayout.LayoutParams
            lp.width = SPINNER_WIDTH
            lp.height = SPINNER_WIDTH
            lp.gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = lp
            translationY = -SPINNER_WIDTH.toFloat()
            visibility = View.GONE
        }
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        //判断要不要嵌套滑动
        var ret = mState == ScrollState.STATE_IDLE //没有在滑动中
                && mMode != MODE_NONE //可滑动状态
                && (nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0 //垂直滑动
                && child == target //嵌套滑动的是直接子view
        if (BuildConfig.DEBUG) Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        if (BuildConfig.DEBUG) Log.d(TAG, """onStartNestedScroll():
            |mState->$mState
            |mMode->$mMode
            |child->$child
            |target->$target
            |ret->$ret""")
        return ret
    }

    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onNestedScrollAccepted()")
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes)
        mState = ScrollState.STATE_TOUCH_SCROLL
        //准备显示进度条
        if (mBackAnimator.isStarted) mBackAnimator.cancel()
        mTotalUnconsumed = 0
        mCircleView.translationY = (-SPINNER_WIDTH).toFloat()
        mCircleView.visibility = View.VISIBLE
        mProgress.setStartEndTrim(0f, 0.8f)
        mProgress.showArrow(true)
        mProgress.setArrowScale(.8f)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onNestedPreScroll() mState->$mState")
        //进度条拉出来时往回撤
        if (((mMode and MODE_REFRESH) != 0 && mTotalUnconsumed < 0 && dy > 0) || ((mMode and MODE_LOAD) != 0 && mTotalUnconsumed > 0 && dy < 0)) {
            if (Math.abs(dy) > Math.abs(mTotalUnconsumed)) {
                consumed[1] = -mTotalUnconsumed
                mTotalUnconsumed = 0
            } else {
                consumed[1] = dy
                mTotalUnconsumed += dy
            }
            moveSpinner(mTotalUnconsumed)
        }
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onNestedScroll() mState->$mState")
        //进度条往外拉
        if ((dyUnconsumed > 0 && (mMode and MODE_LOAD) != 0 || (dyUnconsumed < 0 && (mMode and MODE_REFRESH) != 0))) {
            mTotalUnconsumed += dyUnconsumed
            moveSpinner(mTotalUnconsumed)
        }
    }

    override fun onStopNestedScroll(target: View) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onStopNestedScroll() mState->$mState")
        mParentHelper.onStopNestedScroll(target)
        //放手,判断是不是刷新或者加载
        if (mTotalUnconsumed != 0) {
            mState = ScrollState.STATE_FLING_SCROLL
            finishSpinner(mTotalUnconsumed)
            mTotalUnconsumed = 0
        } else {
            finish()
        }
    }

    override fun getNestedScrollAxes(): Int {
        if (BuildConfig.DEBUG) Log.d(TAG, "getNestedScrollAxes() mState->$mState")
        return ViewCompat.SCROLL_AXIS_VERTICAL
    }


    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return false
    }

    fun finish() {
        try {
            if (BuildConfig.DEBUG) Log.d(TAG, "finish()")
            mState = ScrollState.STATE_IDLE
            mProgress.stop()
            mCircleView.translationY = (-SPINNER_WIDTH).toFloat()
            mCircleView.visibility = View.GONE
            if (mBackAnimator.isStarted) mBackAnimator.end()
            if (BuildConfig.DEBUG) Log.d(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        } catch (e: Exception) {
        }
    }

    private fun moveSpinner(totalUnconsumed: Int) {
        if (BuildConfig.DEBUG) Log.d(TAG, "moveSpinner():totalUnconsumed->$totalUnconsumed  mState->$mState")
        if (mState == ScrollState.STATE_IDLE) return
        var moveDisRatio = Math.log10((Math.abs(totalUnconsumed) + 100f).toDouble()) - 2
        mProgress.setProgressRotation(moveDisRatio.toFloat())
        if (totalUnconsumed > 0) {
            mCircleView.translationY = (bottom - moveDisRatio * SPINNER_OFFSET_END - SPINNER_WIDTH).toFloat()
        } else {
            mCircleView.translationY = (-SPINNER_WIDTH + moveDisRatio * SPINNER_OFFSET_END).toFloat()
        }
    }

    private fun finishSpinner(totalUnconsumed: Int) {
        if (BuildConfig.DEBUG) Log.d(TAG, "finishSpinner():totalUnconsumed->$totalUnconsumed  mState->$mState")
        mProgress.showArrow(false)
        mProgress.start()
        var toFloat = mCircleView.translationY
        if (Math.abs(totalUnconsumed) > SPINNER_OFFSET_END * 0.5) {
            //达到了刷新的距离
            if (totalUnconsumed > 0) {
                toFloat = (bottom - SPINNER_OFFSET_END * 0.5 - SPINNER_WIDTH).toFloat()
                mListener?.onLoad()
            } else {
                toFloat = -SPINNER_WIDTH + SPINNER_OFFSET_END * 0.5f
                mListener?.onRefresh()
            }
        } else {
            //未达到刷新的距离
            toFloat = (if (totalUnconsumed > 0) bottom else -SPINNER_WIDTH).toFloat()
            mBackAnimator.addListener(object : SimpleAnimatorListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    mBackAnimator.removeListener(this)
                    finish()
                }

                override fun onAnimationCancel(animation: Animator?) {
                    mBackAnimator.removeListener(this)
                    finish()
                }
            })
        }
        mBackAnimator.apply {
            setFloatValues(mCircleView.translationY, toFloat)
        }.start()
    }

    fun setMode(m: Int) {
        mMode = m
    }

    fun setRefreshLoadListener(l: RefreshLoadListener) {
        mListener = l
    }

    interface RefreshLoadListener {
        fun onRefresh()
        fun onLoad()
    }

    open class SimpleRefreshLoadListener : RefreshLoadListener {
        override fun onRefresh() = Unit
        override fun onLoad() = Unit
    }

    /*********************NestedScrollingChild**********************/

    private val mNestedScrollingChildHelper = NestedScrollingChildHelper(this)


    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mNestedScrollingChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mNestedScrollingChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mNestedScrollingChildHelper.startNestedScroll(axes)
    }
}