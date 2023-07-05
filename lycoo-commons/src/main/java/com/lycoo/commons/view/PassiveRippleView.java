package com.lycoo.commons.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.lycoo.commons.util.LogUtils;

/**
 * 水波纹
 * 目前需要外部触发，显式的调用ripple方法， 这个后面看有没有更好的解决办法。
 * onTouchEvent方法没有搞清楚为什么ACTION_UP不执行，后面看源码.
 *
 * Created by lancy on 2018/4/10
 */
public class PassiveRippleView extends View {
    private static final String TAG = PassiveRippleView.class.getSimpleName();

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

    public PassiveRippleView(Context context) {
        super(context);
        initView(context);
    }

    public PassiveRippleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PassiveRippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    protected void onDraw(Canvas canvas) {
        if (mDrawFinish) {
            super.onDraw(canvas);
            return;
        }
        canvas.drawColor(0x00000000);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    private void updateDrawData() {
        // 最大半径
        mRadius = (float) Math.sqrt(getRectWidth() / 2 * getRectWidth() / 2 + getRectHeight() / 2 * getRectHeight() / 2);
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

    public void setRippleColor(int color) {
        mColor = color;
        mRevealPaint.setColor(mColor);
    }

    public void ripple(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_DPAD_CENTER) {
            return;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!mCenterKeyDown) {
                mCenterKeyDown = true;

                mPressUp = false;
                mDrawFinish = false;
                mInitX = getWidth() / 2;
                mInitY = getHeight() / 2;
                updateDrawData();
                invalidate();
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            mCenterKeyDown = false;

            mStepRadius = (int) (5 * mStepRadius);
            mStepOriginX = (int) (5 * mStepOriginX);
            mStepOriginY = (int) (5 * mStepOriginY);
            mPressUp = true;
            invalidate();
        }
    }

    public void ripple(MotionEvent event) {
        int flags = event.getFlags();
        long downTime = event.getDownTime();
        long eventTime = event.getEventTime();
        float pressure = event.getPressure();
        int action1 = event.getAction();
        LogUtils.debug(TAG, "action = " + action1);
        LogUtils.debug(TAG, "flags = " + flags + ", downTime = " + downTime + ", eventTime = " + eventTime + ", pressure = " + pressure);
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                mPressUp = false;
//                mDrawFinish = false;
//                int index = MotionEventCompat.getActionIndex(event);
//                int eventId = MotionEventCompat.getPointerId(event, index);
//                if (eventId != View.NO_ID) {
//                    mInitX = (int) MotionEventCompat.getX(event, index);
//                    mInitY = (int) MotionEventCompat.getY(event, index);
//                    updateDrawData();
//                    invalidate();
//                }
                break;

            case MotionEvent.ACTION_UP:
                // 长按不显示动画
                if (event.getEventTime() - event.getDownTime() >= 500) {
                    break;
                }

                mPressUp = false;
                mDrawFinish = false;
                int index = MotionEventCompat.getActionIndex(event);
                int eventId = MotionEventCompat.getPointerId(event, index);
                if (eventId != View.NO_ID) {
                    mInitX = (int) MotionEventCompat.getX(event, index);
                    mInitY = (int) MotionEventCompat.getY(event, index);
                    updateDrawData();
                    invalidate();
                }


                mStepRadius = (int) (5 * mStepRadius);
                mStepOriginX = (int) (5 * mStepOriginX);
                mStepOriginY = (int) (5 * mStepOriginY);
                mPressUp = true;
                invalidate();
                break;
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        LogUtils.verbose(TAG, "onTouchEvent()......");
//        final int action = MotionEventCompat.getActionMasked(event);
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mPressUp = false;
//                mDrawFinish = false;
//                int index = MotionEventCompat.getActionIndex(event);
//                int eventId = MotionEventCompat.getPointerId(event, index);
//                if (eventId != View.NO_ID) {
//                    mInitX = (int) MotionEventCompat.getX(event, index);
//                    mInitY = (int) MotionEventCompat.getY(event, index);
//                    updateDrawData();
//                    invalidate();
//                }
//                break;
//
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                mStepRadius = (int) (5 * mStepRadius);
//                mStepOriginX = (int) (5 * mStepOriginX);
//                mStepOriginY = (int) (5 * mStepOriginY);
//                mPressUp = true;
//                invalidate();
//                break;
//        }
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        LogUtils.verbose(TAG, "onKeyDown()......");
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_DPAD_CENTER:
//                if (!mCenterKeyDown) {
//                    mCenterKeyDown = true;
//
//                    mPressUp = false;
//                    mDrawFinish = false;
//                    mInitX = getWidth() / 2;
//                    mInitY = getHeight() / 2;
//                    updateDrawData();
//                    invalidate();
//                }
//                break;
//            default:
//                break;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        LogUtils.verbose(TAG, "onKeyUp()......");
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_DPAD_CENTER:
//                mCenterKeyDown = false;
//
//                mStepRadius = (int) (5 * mStepRadius);
//                mStepOriginX = (int) (5 * mStepOriginX);
//                mStepOriginY = (int) (5 * mStepOriginY);
//                mPressUp = true;
//                invalidate();
//                break;
//        }
//        return super.onKeyUp(keyCode, event);
//    }

}
