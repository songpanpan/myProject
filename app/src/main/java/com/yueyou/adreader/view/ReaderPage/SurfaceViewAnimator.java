package com.yueyou.adreader.view.ReaderPage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class SurfaceViewAnimator extends SurfaceView implements SurfaceHolder.Callback, Runnable  {
    private final int MAX_TRANSLATION_TIME = 300;
    private SurfaceHolder mSurHolder = null;
    private int mStartX;
    private Bitmap mBitmapPre;
    private Bitmap mBitmapCur;
    //private Bitmap mBitmap;
    private boolean mBeginDraw;
    private Thread mThread;
    private boolean mThreadStop;
    private int mTranslationTime;
    private boolean mDirect;
    private SurfaceViewAnimatorListener mSurfaceViewAnimatorListener;
    private int mShadowColor;
    public interface SurfaceViewAnimatorListener {
        void finish();
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mSurfaceViewAnimatorListener.finish();
        }
    };

    private void finish(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mBeginDraw = false;
        unlockCanvas(canvas);
        mHandler.sendEmptyMessageDelayed(1, 1);
    }

    private void unlockCanvas(Canvas canvas) {
        try {
            mSurHolder.unlockCanvasAndPost(canvas);
        }catch (Exception e){

        }
    }

    private Canvas lock() {
        try {
            return mSurHolder.lockCanvas();
        }catch (Exception e){

        }
        return null;
    }

    public void setShadowColor(int color) {
        mShadowColor = color;
    }

    public void refreshPreBitmap(View view, boolean mask) {
        mBitmapPre = refreshCurPageView(view, mBitmapPre, mask);
    }

    public void refreshCurBitmap(View view, boolean refresh, boolean mask) {
        if (!refresh && mBitmapCur != null)
            return;
        mBitmapCur = refreshCurPageView(view, mBitmapCur, mask);
    }

    public Bitmap refreshCurPageView(View view, Bitmap bitmap, boolean mask) {
        if (bitmap != null && view.getMeasuredHeight() != bitmap.getHeight()){
            if (!bitmap.isRecycled())
                bitmap.recycle();
            bitmap = null;
        }
        if (bitmap == null){
            bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            //mFrontView.setImageBitmap(mFrontBitmap);
        }
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        drawTextureView(canvas, (ViewGroup) view, mask);
        canvas.setBitmap(null);
        return bitmap;
    }

    private void drawTextureView(Canvas canvas, ViewGroup view, boolean mask) {
        List<Rect> rectList = new ArrayList<>();
        TextureView textureView =  getTextureView( view, rectList);
        if (textureView == null || textureView.getVisibility() == GONE || textureView.getBitmap() == null)
            return;
        if (rectList.size() >= 2) {//是否被imageview遮挡
            Rect rc = rectList.get(rectList.size() - 1);
            Rect rc1 = rectList.get(rectList.size() - 2);
            if (rc.equals(rc1))
                return;
        }
        int[] location = new int[2];
        textureView.getLocationInWindow(location);
        Bitmap bitmap1 = textureView.getBitmap();
        canvas.drawBitmap(bitmap1, location[0], location[1], null);
        if (mask) {
            Paint paint = new Paint();
            paint.setColor(0xa0000000);
            canvas.drawRect(new Rect(location[0], location[1], location[0] + bitmap1.getWidth(),
                    location[1] + bitmap1.getHeight()), paint);
        }
    }

    private TextureView getTextureView(ViewGroup viewGroup, List<Rect> rectList) {
        int count = viewGroup.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View view = viewGroup.getChildAt(i);
            if (view.getVisibility() != VISIBLE)
                continue;
            if (view instanceof ImageView
                    || view instanceof TextureView) {
                Rect viewRect = new Rect();
                view.getGlobalVisibleRect(viewRect);
                rectList.add(viewRect);
            }
            if (view instanceof TextureView){
                return (TextureView) view;
            }
            if (view instanceof ViewGroup) {
                TextureView textureView = getTextureView((ViewGroup) view, rectList);
                if (textureView != null)
                    return textureView;
            }
        }
        return null;
    }

    public void release() {
        if (mBitmapCur != null && !mBitmapCur.isRecycled())
            mBitmapCur.recycle();
        mBitmapCur = null;
        if (mBitmapPre != null && !mBitmapPre.isRecycled())
            mBitmapPre.recycle();
        mBitmapPre = null;
    }

    public SurfaceViewAnimator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurHolder = getHolder();
        mSurHolder.addCallback(this);
        setZOrderOnTop(true);
        //setZOrderOnTop();
    }

    public void prepare(int start, boolean direct) {
        mDirect = direct;
        mStartX = Math.abs(start);
        if (!mBeginDraw) draw(false);
    }

    public void drag(int offset) {
        mStartX += offset;
        if (!mBeginDraw)
            draw(false);
    }

    public void start(int offset, boolean direct, boolean changeDirect, SurfaceViewAnimatorListener listener) {
        mBeginDraw = false;
        mStartX += Math.abs(offset);
        if (mBitmapCur == null)
            return;
        if (changeDirect) {
            mStartX = mBitmapCur.getWidth() - mStartX;
        }
        mDirect = direct;
        mSurfaceViewAnimatorListener = listener;
        int time =  (mBitmapCur.getWidth() - mStartX) * MAX_TRANSLATION_TIME / mBitmapCur.getWidth();
        if (time <= 0)
            time = 10;
        mTranslationTime = time;
        if (mThread == null) {
            mThread = new Thread(this);
            mThreadStop = false;
            mThread.start();
        }
        mBeginDraw = true;
    }

    private synchronized boolean draw(boolean end) {
        long begin = System.currentTimeMillis();
        Canvas canvas = lock();
        if (canvas == null)
            return true;
        if (mBitmapCur == null || mStartX >= mBitmapCur.getWidth()) {
            if (end) {
                finish(canvas);
            }else {
                unlockCanvas(canvas);
            }
            return true;
        }
        Rect srcRect = new Rect(mBitmapCur.getWidth() - mStartX, 0, mBitmapCur.getWidth(), mBitmapCur.getHeight());
        Rect dstRect = new Rect(0, 0, mStartX, mBitmapCur.getHeight());
        if (mDirect) {
            srcRect = new Rect(mStartX, 0, mBitmapCur.getWidth(), mBitmapCur.getHeight());
            dstRect = new Rect(0, 0, mBitmapCur.getWidth() - mStartX, mBitmapCur.getHeight());
        }
        if (mDirect) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }else {
            canvas.drawBitmap(mBitmapCur, 0, 0, null);
        }
        drawShadow(canvas, dstRect);
        canvas.drawBitmap(mBitmapPre, srcRect, dstRect, null);
        Log.i("mStartX", "mStartX : " + mStartX);
        //Log.i("mStartX", "mStartX : " + mStartX);
        if (!end) {
            unlockCanvas(canvas);
            return true;
        }
        int timer = (int) (System.currentTimeMillis() - begin);
        mTranslationTime -= timer;
        if (mTranslationTime <= 0) {
            finish(canvas);
            return true;
        }
        unlockCanvas(canvas);
        mStartX = mStartX + (mBitmapCur.getWidth() - mStartX) * timer / mTranslationTime;
        return false;
    }

    @Override
    public void run() {
        while (!mThreadStop) {
            while (mBeginDraw) {
                if (draw(true)) {
                    break;
                }
            }
            try {
                Thread.sleep(10);
            } catch (Exception ex) {
                break;
            }
        }
        mThread = null;
    }
    private void drawShadow(Canvas canvas, Rect rc) {
        Paint paint = new Paint();
        paint.setShadowLayer(20f, 0, 0, mShadowColor);
        Rect tmp = new Rect();
        tmp.left = rc.right - 20;
        tmp.right = tmp.left + 19;
        tmp.top = rc.top;
        tmp.bottom = rc.bottom;
        canvas.drawRect(tmp, paint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        setZOrderOnTop(true);//使surfaceview放到最顶层
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    // 初始化
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThreadStop = true;
    }
}
