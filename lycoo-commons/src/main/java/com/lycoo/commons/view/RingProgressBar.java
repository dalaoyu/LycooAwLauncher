package com.lycoo.commons.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.lycoo.commons.util.ResourceUtils;

/**
 * 圆环精度条
 *
 * Created by lancy on 2017/12/27
 */
public class RingProgressBar extends View {
    private static final String TAG = RingProgressBar.class.getSimpleName();

    private static final int TEXT_VISIBLE = 0;

    /**
     * 实心圆的画笔
     */
    private Paint mCirclePaint;

    /**
     * 圆环的画笔
     */
    private Paint mRingPaint;

    /**
     * 圆环背景色的画笔
     */
    private Paint mRingBackgroundPaint;

    /**
     * 字体的画笔
     */
    private Paint mTextPaint;

    /**
     * 圆形颜色
     */
    private int mCircleColor;

    /**
     * 圆环颜色
     */
    private int mRingColor;

    /**
     * 圆环背景颜色
     */
    private int mRingBackgroundColor;

    /**
     * 半径
     */
    private float mRadius;

    /**
     * 圆环半径
     */
    private float mRingRadius;

    /**
     * 圆环宽度
     */
    private float mStrokeWidth;

    /**
     * 圆心x坐标
     */
    private int mXCenter;

    /**
     * 圆心y坐标
     */
    private int mYCenter;

    /**
     * 字体长度
     */
    private float mTextWidth;

    /**
     * 字体高度
     */
    private float mTextHeight;

    /**
     * 字体大小
     */
    private float mTextSize;

    /**
     * 总进度
     */
    private int mMax;

    /**
     * 当前进度
     */
    private int mProgress;

    private boolean mTextVisible;

    public enum TextVisibility {
        VISIBLE,
        UNVISIBLE
    }

    private long mUiThreadId;


    public RingProgressBar(Context context) {
        this(context, null);
    }

    public RingProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mUiThreadId = Thread.currentThread().getId();

        initAttrs(context, attrs, defStyleAttr);
        initPainters();
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr 默认样式
     *                     Created by lancy on 2017/12/27 11:49
     */
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        // 设置默认值
        float defaultRadius = dp2px(80f);
        float defaultStrokeWidth = dp2px(10f);
        float defaultTextSize = dp2px(20f);
        int defaultCircleColor = Color.rgb(66, 145, 241);
        int defaultRingColor = Color.rgb(225, 0, 0);
        int defaultRingBackgroundColor = Color.rgb(66, 145, 241);
        int defaultTextVisibility = 0;

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                ResourceUtils.getIdArrayByName(
                        context,
                        "styleable",
                        "RingProgressBar"),
                defStyleAttr, 0);

        mRadius = attributes.getDimension(
                ResourceUtils.getIdByName(
                        context,
                        "styleable",
                        "RingProgressBar_radius"),
                defaultRadius);

        mStrokeWidth = attributes.getDimension(
                ResourceUtils.getIdByName(
                        context,
                        "styleable",
                        "RingProgressBar_strokeWidth"),
                defaultStrokeWidth);

        mTextSize = attributes.getDimension(
                ResourceUtils.getIdByName(
                        context,
                        "styleable",
                        "RingProgressBar_textSize"),
                defaultTextSize);

        mCircleColor = attributes.getColor(
                ResourceUtils.getIdByName(
                        context,
                        "styleable",
                        "RingProgressBar_circleColor"),
                defaultCircleColor);

        mRingColor = attributes.getColor(
                ResourceUtils.getIdByName(
                        context,
                        "styleable",
                        "RingProgressBar_ringColor"),
                defaultRingColor);

        mRingBackgroundColor = attributes.getColor(
                ResourceUtils.getIdByName(
                        context,
                        "styleable",
                        "RingProgressBar_ringBackgroundColor"),
                defaultRingBackgroundColor);


        int textVisible = attributes.getInt(
                ResourceUtils.getIdByName(
                        context,
                        "styleable",
                        "RingProgressBar_textVisibility"),
                TEXT_VISIBLE);
        mTextVisible = textVisible == TEXT_VISIBLE;

        setProgress(attributes.getInt(
                ResourceUtils.getIdByName(
                        context,
                        "styleable",
                        "RingProgressBar_progress"),
                0));

        setMax(attributes.getInt(
                ResourceUtils.getIdByName(
                        context,
                        "styleable",
                        "RingProgressBar_max"),
                100));

        attributes.recycle();

        mRingRadius = mRadius + mStrokeWidth / 2;
    }

    /**
     * 初始化画笔
     *
     * Created by lancy on 2017/12/27 11:51
     */
    private void initPainters() {
        //内圆
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCircleColor);
//        mCirclePaint.applyStyleColor(Paint.Style.STROKE);

        //外圆弧背景
        mRingBackgroundPaint = new Paint();
        mRingBackgroundPaint.setAntiAlias(true);
        mRingBackgroundPaint.setColor(mRingBackgroundColor);
        mRingBackgroundPaint.setStyle(Paint.Style.STROKE);
        mRingBackgroundPaint.setStrokeWidth(mStrokeWidth);

        //外圆弧
        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mStrokeWidth);
        //mRingPaint.setStrokeCap(Paint.Cap.ROUND);//设置线冒样式，有圆 有方

        //中间字
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mRingColor);
//        mTextPaint.setTextSize(mRadius / 2);
        mTextPaint.setTextSize(mTextSize);

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTextHeight = (int) Math.ceil(fm.descent - fm.ascent);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;

        //内圆
        canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);

        //外圆弧背景
        RectF oval1 = new RectF();
        oval1.left = (mXCenter - mRingRadius);
        oval1.top = (mYCenter - mRingRadius);
        oval1.right = mRingRadius * 2 + (mXCenter - mRingRadius);
        oval1.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
        canvas.drawArc(oval1, 0, 360, false, mRingBackgroundPaint); //圆弧所在的椭圆对象、圆弧的起始角度、圆弧的角度、是否显示半径连线

        //外圆弧
        if (mProgress > 0) {
            RectF oval = new RectF();
            oval.left = (mXCenter - mRingRadius);
            oval.top = (mYCenter - mRingRadius);
            oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
            oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
            canvas.drawArc(oval, -90, ((float) mProgress / mMax) * 360, false, mRingPaint); //

            //字体
            if (mTextVisible) {
                String txt = mProgress + "%";
                mTextWidth = mTextPaint.measureText(txt, 0, txt.length());
                canvas.drawText(txt, mXCenter - mTextWidth / 2, mYCenter + mTextHeight / 4, mTextPaint);
            }
        }
    }

    /**
     * 设置进度
     *
     * @param progress 当前进度
     *
     *                 Created by lancy on 2017/12/27 19:42
     */
    public void setProgress(int progress) {
        if (progress <= getMax() && progress >= 0) {
            mProgress = progress;
            if (mUiThreadId == Thread.currentThread().getId()) {
                invalidate();
            } else {
                postInvalidate();
            }
        }
    }

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        if (max > 0) {
            mMax = max;
        }

        invalidate();
    }

    public boolean isTextVisible() {
        return mTextVisible;
    }

    public void setTextVisible(TextVisibility textVisibility) {
        this.mTextVisible = textVisibility == TextVisibility.VISIBLE;
        invalidate();
    }
}
