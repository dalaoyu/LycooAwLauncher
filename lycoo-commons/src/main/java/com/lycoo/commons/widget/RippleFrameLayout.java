package com.lycoo.commons.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.lycoo.commons.util.LogUtils;

/**
 * 水波纹
 * http://blog.csdn.net/jxxfzgy/article/details/45390029
 *
 * Created by lancy on 2017/11/25 16:20
 */
public class RippleFrameLayout extends FrameLayout {
    private static final String TAG = RippleFrameLayout.class.getSimpleName();
    private static final boolean DEBUG_METHOD = false;
    private static final boolean DEBUG_DISTANCE = false;

    public static final int DURATION = 200;
    private static final int FREQUENCY = 3;


    /*起始点*/
    private int mInitX;
    private int mInitY;

    private float mCurrentX;
    private float mCurrentY;

    /*高度和宽度*/
    private int mWidth;
    private int mHeight;

    /*绘制的半径*/
    private float mRadius;
    private float mStepRadius;
    private float mStepOriginX;
    private float mStepOriginY;
    private float mDrawRadius;

    private boolean mDrawFinish;

    private float mCycle;
    private final Rect mRect = new Rect();

    private boolean mPressUp = false;

    private int mColor;
    private Paint mRevealPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean mCenterKeyDown = false;

    private boolean mOvalShape;

    public RippleFrameLayout(Context context) {
        super(context);
        initView(context);
    }

    public RippleFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RippleFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mColor = 0x80FFFFFF;
        mRevealPaint.setColor(mColor);
        mCycle = DURATION / FREQUENCY;
        final float density = getResources().getDisplayMetrics().density;
        mCycle = (density * mCycle);
        mDrawFinish = true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mDrawFinish) {
            super.dispatchDraw(canvas);
            return;
        }
        canvas.drawColor(0x15000000);
        super.dispatchDraw(canvas);
        if (mStepRadius == 0) {
            return;
        }
        mDrawRadius = mDrawRadius + mStepRadius;
        mCurrentX = mCurrentX + mStepOriginX;
        mCurrentY = mCurrentY + mStepOriginY;
        if (mDrawRadius > mRadius) {
            mDrawRadius = 0;
            canvas.drawCircle(getRectWidth() / 2, getRectHeight() / 2, mRadius, mRevealPaint);
//            canvas.drawCircle(getRectWidth() / 2, getRectHeight() / 2, mRadius - 5, mRevealPaint);
            mDrawFinish = true;
            if (mPressUp) {
                invalidate();
            }
            return;
        }

        canvas.drawCircle(mCurrentX, mCurrentY, mDrawRadius, mRevealPaint);
//        canvas.drawCircle(mCurrentX, mCurrentY, mDrawRadius - 5, mRevealPaint);
        ViewCompat.postInvalidateOnAnimation(this);


    }

    /*
        @Override
        protected void onDraw(Canvas canvas) {
            if (mDrawFinish) {
                super.onDraw(canvas);
                return;
            }
            canvas.drawColor(0x15000000);
            super.onDraw(canvas);
            if (mStepRadius == 0) {
                return;
            }
            mDrawRadius = mDrawRadius + mStepRadius;
            mCurrentX = mCurrentX + mStepOriginX;
            mCurrentY = mCurrentY + mStepOriginY;
            if (mDrawRadius > mRadius) {
                mDrawRadius = 0;
                canvas.drawCircle(getRectWidth() / 2, getRectHeight() / 2, mRadius, mRevealPaint);
                mDrawFinish = true;
                if (mPressUp) {
                    invalidate();
                }
                return;
            }

            canvas.drawCircle(mCurrentX, mCurrentY, mDrawRadius, mRevealPaint);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
//        mRect.set(5, 5, getMeasuredWidth() - 5, getMeasuredHeight() - 5);
    }

    private void updateDrawData() {
        // 最大半径
        if (isOvalShape()) {
            mRadius = (float) getRectWidth() / 2;
        } else {
            mRadius = (float) Math.sqrt(getRectWidth() / 2 * getRectWidth() / 2 + getRectHeight() / 2 * getRectHeight() / 2);
        }

        // 半径的偏移量
        mStepRadius = mRadius / mCycle;
        // 圆心X的偏移量
        mStepOriginX = (getRectWidth() / 2 - mInitX) / mCycle;
        // 圆心Y的偏移量
        mStepOriginY = (getRectHeight() / 2 - mInitY) / mCycle;

        mCurrentX = mInitX;
        mCurrentY = mInitY;
    }

    private int getRectWidth() {
        return mRect.width();
    }

    private int getRectHeight() {
        return mRect.height();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (DEBUG_METHOD) {
            LogUtils.verbose(TAG, "onTouchEvent(), hasFocus = " + hasFocus());
        }
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPressUp = false;
                mDrawFinish = false;
                int index = MotionEventCompat.getActionIndex(event);
                int eventId = MotionEventCompat.getPointerId(event, index);
                if (eventId != View.NO_ID) {
                    mInitX = (int) MotionEventCompat.getX(event, index);
                    mInitY = (int) MotionEventCompat.getY(event, index);
                    if (DEBUG_DISTANCE) {
                        LogUtils.debug(TAG, "initX = " + mInitX + ", initY = " + mInitY);
                    }
                    updateDrawData();
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mStepRadius = (int) (5 * mStepRadius);
                mStepOriginX = (int) (5 * mStepOriginX);
                mStepOriginY = (int) (5 * mStepOriginY);
                if (DEBUG_DISTANCE) {
                    LogUtils.debug(TAG, "mStepRadius = " + mStepRadius
                            + ", mStepOriginX = " + mStepOriginX
                            + ", mStepOriginY = " + mStepOriginY);
                }
                mPressUp = true;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (DEBUG_METHOD) {
            LogUtils.verbose(TAG, "onKeyDown......");
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (!mCenterKeyDown) {
                    mCenterKeyDown = true;

                    mPressUp = false;
                    mDrawFinish = false;
                    mInitX = getWidth() / 2;
                    mInitY = getHeight() / 2;
                    if (DEBUG_DISTANCE) {
                        LogUtils.debug(TAG, "onKeyDown(), initX = " + mInitX + ", initY = " + mInitY);
                    }
                    updateDrawData();
                    invalidate();
                }
                break;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (DEBUG_METHOD) {
            LogUtils.verbose(TAG, "onKeyUp()......");
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                mCenterKeyDown = false;

                mStepRadius = (int) (5 * mStepRadius);
                mStepOriginX = (int) (5 * mStepOriginX);
                mStepOriginY = (int) (5 * mStepOriginY);
                LogUtils.debug(TAG, "onKeyUp(), mStepRadius = " + mStepRadius
                        + ", mStepOriginX = " + mStepOriginX
                        + ", mStepOriginY = " + mStepOriginY);
                mPressUp = true;
                invalidate();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private int getMax(int... radius) {
        if (radius.length == 0) {
            return 0;
        }
        int max = radius[0];
        for (int m : radius) {
            if (m > max) {
                max = m;
            }
        }
        return max;
    }

    public void setRippleColor(int color) {
        mColor = color;
        mRevealPaint.setColor(mColor);
    }

    public boolean isOvalShape(){
        return mOvalShape;
    }

    public void setOvalShape(boolean ovalShape) {
        mOvalShape = ovalShape;
    }

    /*
    @Override
    public boolean performClick() {
        postDelayed(
                () -> {
                    LogUtils.info(TAG, "performClick()......");
                    RippleFrameLayout.super.performClick();
                },
                DURATION + 100);
        return true;
    }
*/
}
