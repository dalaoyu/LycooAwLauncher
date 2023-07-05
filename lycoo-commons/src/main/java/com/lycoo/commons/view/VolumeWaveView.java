package com.lycoo.commons.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.lycoo.commons.R;
import com.lycoo.commons.util.LogUtils;


/**
 * 仿百度的语音助手--波浪动画控件
 * 1.用五个不同颜色贝塞尔曲线,分三层，前后，错开排列
 * 2.每个曲线利用属性动画也都错开波动
 *
 * @author helang
 */
public class VolumeWaveView extends View {
    private static final String TAG = VolumeWaveView.class.getSimpleName();

//    private static final int HEIGHT = 400;// 整个控件的高度
//    private static final int HEIGHT1 = 60;//第一层曲线的高度
//    private static final int HEIGHT2 = 40;//第二层曲线的高度
//    private static final int HEIGHT3 = 50;//第三层曲线的高度

    private static final float DEFAULT_HEIGHT1 = 60;//第一层曲线的高度
    private static final float DEFAULT_HEIGHT2 = 40;//第二层曲线的高度
    private static final float DEFAULT_HEIGHT3 = 50;//第三层曲线的高度

    private static final int DEFAULT_GRADIENT1_COLOR0 = Color.parseColor("#e652a6d2");
    private static final int DEFAULT_GRADIENT1_COLOR1 = Color.parseColor("#e652d5a1");
    private static final int DEFAULT_GRADIENT2_COLOR0 = Color.parseColor("#e68952d5");
    private static final int DEFAULT_GRADIENT2_COLOR1 = Color.parseColor("#e6525dd5");
    private static final int DEFAULT_GRADIENT3_COLOR0 = Color.parseColor("#e66852d5");
    private static final int DEFAULT_GRADIENT3_COLOR1 = Color.parseColor("#e651b9d2");
    private static final int DEFAULT_GRADIENT4_COLOR0 = Color.parseColor("#e6d5527e");
    private static final int DEFAULT_GRADIENT4_COLOR1 = Color.parseColor("#e6bf52d5");


    private int h1 = 0, h2 = 0, h3 = 0, h4 = 0, h5 = 0; //每个贝塞尔曲线的实时高度

    private Path mPath;
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;
    private Paint mPaint4;

    // 四种渐变色
    private LinearGradient mLinearGradient1;
    private LinearGradient mLinearGradient2;
    private LinearGradient mLinearGradient3;
    private LinearGradient mLinearGradient4;

    // 五种动画
    private ValueAnimator mAnimator1;
    private ValueAnimator mAnimator2;
    private ValueAnimator mAnimator3;
    private ValueAnimator mAnimator4;
    private ValueAnimator mAnimator5;

    private float mHeight;  // 整个控件的高度
    private float mHeight1; //第一层曲线的高度
    private float mHeight2; //第二层曲线的高度
    private float mHeight3; //第三层曲线的高度

    private int mGradient1Color0;
    private int mGradient1Color1;
    private int mGradient2Color0;
    private int mGradient2Color1;
    private int mGradient3Color0;
    private int mGradient3Color1;
    private int mGradient4Color0;
    private int mGradient4Color1;


    public VolumeWaveView(Context context) {
        this(context, null);
    }

    public VolumeWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VolumeWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
        initPaint();
        startAnimation();
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.VolumeWaveView,
                defStyleAttr,
                0);

        mHeight1 = typedArray.getDimension(R.styleable.VolumeWaveView_wave_height1, DEFAULT_HEIGHT1);
        mHeight2 = typedArray.getDimension(R.styleable.VolumeWaveView_wave_height2, DEFAULT_HEIGHT2);
        mHeight3 = typedArray.getDimension(R.styleable.VolumeWaveView_wave_height3, DEFAULT_HEIGHT3);

        mGradient1Color0 = typedArray.getColor(R.styleable.VolumeWaveView_gradient1_color0, DEFAULT_GRADIENT1_COLOR0);
        mGradient1Color1 = typedArray.getColor(R.styleable.VolumeWaveView_gradient1_color1, DEFAULT_GRADIENT1_COLOR1);
        mGradient2Color0 = typedArray.getColor(R.styleable.VolumeWaveView_gradient2_color0, DEFAULT_GRADIENT2_COLOR0);
        mGradient2Color1 = typedArray.getColor(R.styleable.VolumeWaveView_gradient2_color1, DEFAULT_GRADIENT2_COLOR1);
        mGradient3Color0 = typedArray.getColor(R.styleable.VolumeWaveView_gradient3_color0, DEFAULT_GRADIENT3_COLOR0);
        mGradient3Color1 = typedArray.getColor(R.styleable.VolumeWaveView_gradient3_color1, DEFAULT_GRADIENT3_COLOR1);
        mGradient4Color0 = typedArray.getColor(R.styleable.VolumeWaveView_gradient4_color0, DEFAULT_GRADIENT4_COLOR0);
        mGradient4Color1 = typedArray.getColor(R.styleable.VolumeWaveView_gradient4_color1, DEFAULT_GRADIENT4_COLOR1);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPath = new Path();

        mPaint1 = new Paint();
        mPaint1.setStyle(Paint.Style.FILL);
        mPaint1.setAntiAlias(true);//抗锯齿
        //渐变色1
        mLinearGradient1 = new LinearGradient(0, 0, 0, mHeight1, mGradient1Color0, mGradient1Color1, Shader.TileMode.MIRROR);
        mPaint1.setShader(mLinearGradient1);

        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);//抗锯齿
        mPaint2.setStyle(Paint.Style.FILL);
        //渐变色2
        mLinearGradient2 = new LinearGradient(0, 0, 0, mHeight2, mGradient2Color0, mGradient2Color1, Shader.TileMode.MIRROR);
        mPaint2.setShader(mLinearGradient2);


        mPaint3 = new Paint();
        mPaint3.setAntiAlias(true);//抗锯齿
        mPaint3.setStyle(Paint.Style.FILL);
        //渐变色3
        mLinearGradient3 = new LinearGradient(0, 0, 0, mHeight3, mGradient3Color0, mGradient3Color1, Shader.TileMode.MIRROR);
        mPaint3.setShader(mLinearGradient3);


        mPaint4 = new Paint();
        mPaint4.setAntiAlias(true);//抗锯齿
        mPaint4.setStyle(Paint.Style.FILL);
        //渐变色4
        mLinearGradient4 = new LinearGradient(0, 0, 0, mHeight2, mGradient4Color0, mGradient4Color1, Shader.TileMode.MIRROR);
        mPaint4.setShader(mLinearGradient4);
    }


    /**
     * draw方法中不要创建大量对象，尽量复用对象
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLayer3(canvas);
        drawLayer2(canvas);
        drawLayer1(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHeight = getMeasuredHeight();
        LogUtils.debug(TAG, "view Height: " + getMeasuredHeight());
    }

    /**
     * 绘制第一层
     *
     * @param canvas
     */
    private void drawLayer1(Canvas canvas) {
        drawCurve(mPath, canvas, mPaint1, getWidth() / 5, getWidth() / 3, h1);
        drawCurve(mPath, canvas, mPaint1, getWidth() / 3 + getWidth() / 5, getWidth() / 3, h2);
    }

    /**
     * 绘制第二层
     *
     * @param canvas
     */
    private void drawLayer2(Canvas canvas) {
        drawCurve(mPath, canvas, mPaint2, 0, getWidth() / 2, h3);
        drawCurve(mPath, canvas, mPaint4, getWidth() / 2 - 10, getWidth() / 2, h4);

    }

    /**
     * 绘制第三层
     *
     * @param canvas
     */
    private void drawLayer3(Canvas canvas) {
        drawCurve(mPath, canvas, mPaint3, getWidth() / 4, getWidth() / 2, h5);
    }


    /**
     * 画贝塞尔曲线
     *
     * @param path
     * @param canvas
     * @param x      横向起点的位置(用于摆放曲线的左右的位置)
     * @param width  曲线的整个宽度
     * @param height 曲线的高度
     */
    private void drawCurve(Path path, Canvas canvas, Paint paint, int x, int width, int height) {
        path.reset();
        /*
         * 因为这个弧形（类似一个山峰的形状）
         * 其实就是三个贝塞尔曲线组成；
         * 而每个贝塞尔曲线需要三个点，三个点连接起来也就是两部分构成；
         * 所以，这三个贝塞尔曲线就是由六部分组成了（A，B，C，D，E，F，G），
         * 所以这里就平均分一下，建议用笔在纸上画一下，就晓得了
         */
        int subWidth = width / 6;//每小部分的宽度
        path.moveTo(x, mHeight);//起点 A
        path.quadTo(x + subWidth, mHeight - height, x + subWidth * 2, mHeight - height * 2);//B - C

        path.lineTo(x + subWidth * 2, mHeight - height * 2);//C
        path.quadTo(x + subWidth * 3, mHeight - height * 3, x + subWidth * 4, mHeight - height * 2);//D - E

        path.lineTo(x + subWidth * 4, mHeight - height * 2);// E
        path.quadTo(x + subWidth * 5, mHeight - height, x + subWidth * 6, mHeight);//F - G

        canvas.drawPath(path, paint);
    }

    /**
     * 添加属性动画,每一个动画的变化范围和周期都不一样，这样错开的效果才好看点
     */
    public void startAnimation() {
        mAnimator1 = ValueAnimator.ofInt(0, (int) mHeight1, 0);
        mAnimator1.setDuration(1200);
        mAnimator1.setInterpolator(new DecelerateInterpolator());
        //无限循环
        mAnimator1.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator1.addUpdateListener(animation -> {
            h1 = (int) animation.getAnimatedValue();
            invalidate();
        });
        mAnimator1.start();

        mAnimator2 = ValueAnimator.ofInt(0, (int) mHeight1, 0);
        mAnimator2.setDuration(1500);
        mAnimator2.setInterpolator(new DecelerateInterpolator());
        //无限循环
        mAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator2.addUpdateListener(animation -> {
            h2 = (int) animation.getAnimatedValue();
            invalidate();
        });
        mAnimator2.start();


        mAnimator3 = ValueAnimator.ofInt(0, (int) mHeight2, 0);
        mAnimator3.setDuration(1100);
        mAnimator3.setInterpolator(new DecelerateInterpolator());
        //无限循环
        mAnimator3.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator3.addUpdateListener(animation -> {
            h3 = (int) animation.getAnimatedValue();
            invalidate();
        });
        mAnimator3.start();


        mAnimator4 = ValueAnimator.ofInt(0, (int) mHeight2, 0);
        mAnimator4.setDuration(1360);
        mAnimator4.setInterpolator(new DecelerateInterpolator());
        //无限循环
        mAnimator4.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator4.addUpdateListener(animation -> {
            h4 = (int) animation.getAnimatedValue();
            invalidate();
        });
        mAnimator4.start();


        mAnimator5 = ValueAnimator.ofInt(0, (int) mHeight3, 0);
        mAnimator5.setDuration(1500);
        mAnimator5.setInterpolator(new DecelerateInterpolator());
        //无限循环
        mAnimator5.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator5.addUpdateListener(animation -> {
            h5 = (int) animation.getAnimatedValue();
            invalidate();
        });
        mAnimator5.start();
    }

    /**
     * 关闭动画
     */
    public void removeAnimation() {
        if (mAnimator1 != null) {
            mAnimator1.cancel();
            mAnimator1 = null;
        }
        if (mAnimator2 != null) {
            mAnimator2.cancel();
            mAnimator2 = null;
        }
        if (mAnimator3 != null) {
            mAnimator3.cancel();
            mAnimator3 = null;
        }
        if (mAnimator4 != null) {
            mAnimator4.cancel();
            mAnimator4 = null;
        }
        if (mAnimator5 != null) {
            mAnimator5.cancel();
            mAnimator5 = null;
        }
    }

}