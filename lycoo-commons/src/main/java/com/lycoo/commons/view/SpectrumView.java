package com.lycoo.commons.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by lancy on 2018/5/12
 */
public class SpectrumView extends View {
    private static final int DEFAULT_COUNT = 10;
    private static final int DEFAULT_OFFSET = 2;
    private static final int DEFAULT_COLOR_0 = Color.GREEN;
    private static final int DEFAULT_COLOR_1 = Color.BLUE;

    private int mCount = DEFAULT_COUNT;
    private int mOffset = DEFAULT_OFFSET;
    private int mColor0 = DEFAULT_COLOR_0;
    private int mColor1 = DEFAULT_COLOR_1;
    private LinearGradient mLinearGradient;

    private Paint mPaint;
    private int mWidth;
    private float mRectWidth;
    private float mRectHeight;

    private double mRandom;

    public SpectrumView(Context context) {
        super(context);
        initView();
    }

    public SpectrumView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SpectrumView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = getWidth();
        mRectHeight = getHeight();
        mRectWidth = (int) (mWidth * 0.6 / mCount);
        mLinearGradient = new LinearGradient(
                0,
                0,
                mRectWidth,
                mRectHeight,
                Color.GREEN,
                Color.CYAN,
                Shader.TileMode.CLAMP);
//        mLinearGradient = new LinearGradient(
//                0,
//                0,
//                mRectWidth,
//                mRectHeight,
//                new int[]{Color.YELLOW, Color.GREEN, Color.BLUE},
//                new float[]{0.2f, 0.5f, 0.8f},
//                Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mCount; i++) {
            mRandom = Math.random();
            float currentHeight = (float) (mRectHeight * mRandom);
            canvas.drawRect(
                    (float) (mWidth * 0.4 / 2 + mRectWidth * i + mOffset),
                    currentHeight,
                    (float) (mWidth * 0.4 / 2 + mRectWidth * (i + 1)),
                    mRectHeight,
                    mPaint);
        }

    }

    public void setCount(int count) {
        this.mCount = count;
        invalidate();
    }

    public void setOffset(int offset) {
        this.mOffset = offset;
    }

    public void setColor0(int color) {
        this.mColor0 = color;
    }

    public void setColor1(int color) {
        this.mColor1 = color;
    }

    public void vibrate() {
        postInvalidateDelayed(300);
    }
}
