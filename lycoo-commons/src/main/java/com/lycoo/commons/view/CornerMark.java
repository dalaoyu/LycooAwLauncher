package com.lycoo.commons.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.lycoo.commons.R;
import com.lycoo.commons.domain.CommonConstants;


/**
 * 角标
 *
 * Created by lancy on 2017/12/14
 */
public class CornerMark extends View {

    public static final int ROTATE_LEFT = -45;
    public static final int ROTATE_RIGHT = 45;

    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_ITALIC = 1;
    private static final int STYLE_BOLD = 2;

    private Paint mTextTitlePaint;
    private int mTextTitleColor;
    private float mTextTitleSize;
    private Rect mTextTitleRect;
    private int mTextTitleStyle;

    private Paint mTextContentPaint;
    private int mTextContentColor;
    private float mTextContentSize;
    private Rect mTextContentRect;
    private int mTextContentStyle;

    private Paint mBgTrianglePaint;
    private int mBgTriangleColor;

    private float mTopPadding;
    private float mBottomPadding;
    private float mCenterPadding;
    private float mTopDistance;

    private float mRouteDegrees;

    private String mTextTitle;
    private String mTextContent;

    private int mBgTriangleWidth;
    private int mBgTriangleHeight;

    private Context mContext;

    public CornerMark(Context context) {
        this(context, null);
    }

    public CornerMark(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerMark(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CornerMark,
                defStyleAttr,
                0);
        mTopPadding = typedArray.getDimension(
                R.styleable.CornerMark_labelTopPadding,
                context.getResources().getDimensionPixelSize(R.dimen.def_corner_mark_label_top_padding));
        mCenterPadding = typedArray.getDimension(
                R.styleable.CornerMark_labelCenterPadding,
                0);
        mBottomPadding = typedArray.getDimension(
                R.styleable.CornerMark_labelBottomPadding,
                context.getResources().getDimensionPixelSize(R.dimen.def_corner_mark_label_bottom_padding));
        mTopDistance = typedArray.getDimension(
                R.styleable.CornerMark_labelTopDistance,
                0);
        mBgTriangleColor = typedArray.getColor(
                R.styleable.CornerMark_backgroundColor,
                Color.BLUE);
        mTextTitleColor = typedArray.getColor(
                R.styleable.CornerMark_textTitleColor,
                Color.WHITE);
        mTextContentColor = typedArray.getColor(
                R.styleable.CornerMark_textContentColor,
                Color.WHITE);
        mTextTitleSize = typedArray.getDimension(
                R.styleable.CornerMark_textTitleSize,
                context.getResources().getDimensionPixelSize(R.dimen.def_corner_mark_label_title_size));
        mTextContentSize = typedArray.getDimension(
                R.styleable.CornerMark_textContentSize,
                context.getResources().getDimensionPixelSize(R.dimen.def_corner_mark_label_content_size));
        mTextTitle = typedArray.getString(
                R.styleable.CornerMark_textTitle);
        mTextContent = typedArray.getString(
                R.styleable.CornerMark_textContent);
        mTextTitleStyle = typedArray.getInt(
                R.styleable.CornerMark_textTitleStyle,
                STYLE_NORMAL);
        mTextContentStyle = typedArray.getInt(
                R.styleable.CornerMark_textContentStyle,
                STYLE_NORMAL);
        mRouteDegrees = typedArray.getInt(
                R.styleable.CornerMark_direction,
                ROTATE_LEFT);

        typedArray.recycle();

        initArt();
        resetAllMeasureSize();
    }

    private void initArt() {
        mTextTitleRect = new Rect();
        mTextContentRect = new Rect();

        mTextTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextTitlePaint.setColor(mTextTitleColor);
        mTextTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTextTitlePaint.setTextSize(mTextTitleSize);
        mTextTitlePaint.setTypeface(Typeface.createFromAsset(mContext.getAssets(), CommonConstants.CUSTOM_FONT_FLFBLS));

        mTextContentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextContentPaint.setColor(mTextContentColor);
        mTextContentPaint.setTextAlign(Paint.Align.CENTER);
        mTextContentPaint.setTextSize(mTextContentSize);
        mTextContentPaint.setTypeface(Typeface.createFromAsset(mContext.getAssets(), CommonConstants.CUSTOM_FONT_FLFBLS));

        mBgTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgTrianglePaint.setColor(mBgTriangleColor);
    }

    private void resetAllMeasureSize() {
        if (!TextUtils.isEmpty(mTextTitle)) {
            mTextTitlePaint.getTextBounds(mTextTitle, 0, mTextTitle.length(), mTextTitleRect);
        }

        if (!TextUtils.isEmpty(mTextContent)) {
            mTextContentPaint.getTextBounds(mTextContent, 0, mTextContent.length(), mTextContentRect);
        }

        mBgTriangleHeight = (int) (mTopDistance
                + mTopPadding + mCenterPadding + mBottomPadding
                + mTextTitleRect.height() + mTextContentRect.height());
        mBgTriangleWidth = 2 * mBgTriangleHeight;
    }

    public void drawLabel(View view, Canvas canvas) {
        if (canvas == null || view == null) {
            throw new IllegalArgumentException("LabelViewHelper draw canvas or view cant't be null!");
        }

        canvas.save();
        if (mRouteDegrees == ROTATE_LEFT) {
            canvas.translate(-mBgTriangleWidth / 2, 0);
            canvas.rotate(mRouteDegrees, mBgTriangleWidth / 2, 0);
        } else if (mRouteDegrees == ROTATE_RIGHT) {
            int rotateViewWH = (int) (mBgTriangleHeight * Math.sqrt(2));
            canvas.translate(view.getMeasuredWidth() - rotateViewWH, -mBgTriangleHeight);
            canvas.rotate(mRouteDegrees, 0, mBgTriangleHeight);
        }

        Path path = new Path();
        path.moveTo(0, mBgTriangleHeight);
        if (mTopDistance < 0) {
            // mTopDistance > 0 represents a trapezoid, otherwise represents a triangle.
            mTopDistance = 0;
        }
        path.lineTo(mBgTriangleWidth / 2 - mTopDistance, mTopDistance);
        path.lineTo(mBgTriangleWidth / 2 + mTopDistance, mTopDistance);
        path.lineTo(mBgTriangleWidth, mBgTriangleHeight);
        path.close();
        canvas.drawPath(path, mBgTrianglePaint);

        if (!TextUtils.isEmpty(mTextTitle)) {
            canvas.drawText(mTextTitle, (mBgTriangleWidth) / 2, mTopDistance + mTopPadding + mTextTitleRect.height(), mTextTitlePaint);
        }
        if (!TextUtils.isEmpty(mTextContent)) {
            canvas.drawText(mTextContent, (mBgTriangleWidth) / 2, (mTopDistance + mTopPadding + mTextTitleRect.height() + mCenterPadding + mTextContentRect.height()), mTextContentPaint);
        }

        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLabel(this, canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int rotateViewWH = (int) (mBgTriangleWidth * Math.sqrt(2));
        setMeasuredDimension(rotateViewWH, rotateViewWH);
    }

    public void setTextContent(String content) {
        mTextContent = content;

        resetAllMeasureSize();
        invalidate();
    }

    public void setTextContentSize(int size) {
        mTextContentSize = size;
        mTextContentPaint.setTextSize(size);

        resetAllMeasureSize();
        invalidate();
    }

    public void setTextContentColor(int color) {
        mTextContentColor = color;

        invalidate();
    }

    public void setTextTitle(String title) {
        mTextTitle = title;

        resetAllMeasureSize();
        invalidate();
    }

    public void setmTextTitleSize(int size) {
        mTextTitleSize = size;
        mTextTitlePaint.setTextSize(size);

        resetAllMeasureSize();
        invalidate();
    }

    public void setTextTitleColor(int color) {
        mTextTitleColor = color;

        invalidate();
    }

    public void setBgColor(int color) {
        mBgTrianglePaint.setColor(color);

        invalidate();
    }

    public void setTopPadding(int topPadding) {
        mTopPadding = topPadding;

        resetAllMeasureSize();
        invalidate();
    }

    public void setCenterPadding(int centerPadding) {
        mCenterPadding = centerPadding;

        resetAllMeasureSize();
        invalidate();
    }

    public void setBottomPadding(int bottomPadding) {
        mBottomPadding = bottomPadding;

        resetAllMeasureSize();
        invalidate();
    }

    public void setDirection(int direction) {
        mRouteDegrees = direction;

        resetAllMeasureSize();
        invalidate();
    }

}
