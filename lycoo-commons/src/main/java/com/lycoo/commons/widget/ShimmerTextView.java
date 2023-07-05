package com.lycoo.commons.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import com.lycoo.commons.util.ResourceUtils;


@SuppressLint("AppCompatCustomView")
public class ShimmerTextView extends TextView {

	private LinearGradient mLinearGradient;
	private Matrix mGradientMatrix;
	private Paint mPaint;
	private int mViewWidth = 0;
	private int mTranslate = 0;

	private boolean mAnimating = true;

	private final int default_begin_color = 0x33ffffff;
	private final int default_middle_color = 0xffffffff;
	private final int default_end_color = 0x33ffffff;
	
	private int mBeginColor;
	private int mMiddleColor;
	private int mEndColro;
	

	public ShimmerTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, ResourceUtils.getIdArrayByName(context, "styleable", "ShimmerTextView"));
		mBeginColor = typedArray.getColor(ResourceUtils.getIdByName(context, "styleable", "ShimmerTextView_begin_color"), default_begin_color);
		mMiddleColor = typedArray.getColor(ResourceUtils.getIdByName(context, "styleable", "ShimmerTextView_middle_color"), default_middle_color);
		mEndColro = typedArray.getColor(ResourceUtils.getIdByName(context, "styleable", "ShimmerTextView_end_color"), default_end_color);

		typedArray.recycle();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mViewWidth == 0) {
			mViewWidth = getMeasuredWidth();
			if (mViewWidth > 0) {
				mPaint = getPaint();
				mLinearGradient = new LinearGradient(//
						-mViewWidth, //
						0, //
						0, //
						0, //
						new int[] { mBeginColor, mMiddleColor, mEndColro }, //
						new float[] { 0, 0.5f, 1 }, //
						Shader.TileMode.CLAMP);
				mPaint.setShader(mLinearGradient);
				mGradientMatrix = new Matrix();
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mAnimating && mGradientMatrix != null) {
			mTranslate += mViewWidth / 10;
			if (mTranslate > 2 * mViewWidth) {
				mTranslate = -mViewWidth;
			}
			mGradientMatrix.setTranslate(mTranslate, 0);
			mLinearGradient.setLocalMatrix(mGradientMatrix);
			postInvalidateDelayed(50);
		}
	}

}
