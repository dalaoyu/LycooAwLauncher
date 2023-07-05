package com.lycoo.commons.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.lycoo.commons.util.LogUtils;

/**
 * 滚动TextView
 * <p>
 * Created by lancy on 2017/3/16
 */
@SuppressLint("AppCompatCustomView")
public class AutoScrollTextView extends TextView {

    public final static String TAG = AutoScrollTextView.class.getSimpleName();

    private float textLength = 0f;
    private float viewWidth = 0f;
    private float step = 0f;
    private float y = 0f;
    private float temp_view_plus_text_length = 0.0f;
    private float temp_view_plus_two_text_length = 0.0f;
    public boolean isStarting = false;
    private Paint paint = null;
    private String text = "";
    private int color = Color.WHITE;

    public void setColor(int color) {
        this.color = color;
    }

    private int count;

    private OnCircleRollListener mOnCircleRollListener;

    public AutoScrollTextView(Context context) {
        super(context);
        initView();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        onClick(this);
    }

    /**
     * 初始化AutoScrollTextView
     *
     * @Param width AutoScrollTextView的长度
     * <p>
     * Created by lancy on 2017/3/17
     */
    public void init(int width) {
        paint = getPaint();
        paint.setColor(color);

        text = getText().toString();
        textLength = paint.measureText(text);
        viewWidth = width;
//        viewWidth = getWidth();
//        LogUtils.debug(TAG, "AutoScrollTextView's width = " + viewWidth);
//        if (viewWidth == 0) {
//            if (windowManager != null) {
//                Display display = windowManager.getDefaultDisplay();
//                viewWidth = display.getWidth();
//            }
//        }
        step = textLength;
        temp_view_plus_text_length = viewWidth + textLength;
        temp_view_plus_two_text_length = viewWidth + textLength * 2;
        y = getTextSize() + getPaddingTop();

        count = 0;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        LogUtils.verbose(TAG, "onSaveInstanceState()......");
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState ss = new SavedState(parcelable);
        LogUtils.debug(TAG, "onSaveInstanceState()->ss = " + ss);
        ss.step = step;
        ss.isStarting = isStarting;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        LogUtils.debug(TAG, "onRestoreInstanceState()->ok = " + state);
        try {
            super.onRestoreInstanceState(state);
        } catch (Exception e) {
            e.printStackTrace();
            state = null;
        }

        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        step = ss.step;
        isStarting = ss.isStarting;
    }

    public static class SavedState extends BaseSavedState {
        public boolean isStarting = false;
        public float step = 0.0f;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            if (parcel != null) {
                boolean[] b = new boolean[1];
                parcel.readBooleanArray(b);
                if (b.length > 0)
                    isStarting = b[0];
                step = parcel.readFloat();
            }
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBooleanArray(new boolean[]{isStarting});
            out.writeFloat(step);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        @Override
        public String toString() {
            return "SavedState{" +
                    "isStarting=" + isStarting +
                    ", step=" + step +
                    '}';
        }
    }

    public void startScroll() {
        isStarting = true;
        invalidate();
    }

    public void stopScroll() {
        isStarting = false;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (paint == null)
            return;

        canvas.drawText(text, temp_view_plus_text_length - step, y, paint);

        if (!isStarting)
            return;

        step += 0.8;
        if (step > temp_view_plus_two_text_length) {
            mOnCircleRollListener.rollCount(++count);

            step = textLength;
        }

        invalidate();
    }

    public void onClick(View v) {
        if (isStarting)
            stopScroll();
        else
            startScroll();
    }

    public interface OnCircleRollListener {
        void rollCount(int count);
    }

    public void setOnCircleRollListener(OnCircleRollListener onCircleRollListener) {
        this.mOnCircleRollListener = onCircleRollListener;
    }

    public void setCount(int count) {
        this.count = count;
    }

}