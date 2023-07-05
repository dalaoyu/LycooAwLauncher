package com.lycoo.commons.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.lycoo.commons.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 垂直滚动显示器
 * 代码太乱，需要重写
 *
 * Created by lancy on 2019/11/18
 */
public class VerticalScrollTextview extends TextSwitcher implements ViewSwitcher.ViewFactory {
    private static final String TAG = VerticalScrollTextview.class.getSimpleName();
    // FIXME: 2019/11/18 代码太乱，需要重写

    private float mTextSize = 16;
    private Typeface mTextTypeface;
    private int mTextColor = Color.WHITE;
    private int mTextPadding = 5;
    private long mPeriod;                   // Text持续显示时间
    private List<String> mData;
    private int currentId = -1;

    private Disposable mScrollDisposable;
    private Context mContext;

    public VerticalScrollTextview(Context context) {
        this(context, null);
    }

    public VerticalScrollTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mData = new ArrayList<>();
    }

    public VerticalScrollTextview setData(List<String> data) {
        mData.clear();
        mData.addAll(data);
        currentId = -1;
        return this;
    }

    public void setData(String text){
        setText(text);
    }

    public VerticalScrollTextview setTextSize(float textSize) {
        this.mTextSize = textSize;
        return this;
    }

    public VerticalScrollTextview setTypeface(Typeface typeface) {
        this.mTextTypeface = typeface;
        return this;
    }

    public VerticalScrollTextview setTextColor(int textColor) {
        this.mTextColor = textColor;
        return this;
    }

    public VerticalScrollTextview setTextPadding(int textPadding) {
        this.mTextPadding = textPadding;
        return this;
    }

    public VerticalScrollTextview setPeriod(long period) {
        this.mPeriod = period;
        return this;
    }

    public VerticalScrollTextview setAnimationDuration(long duration) {
        setFactory(this);
        Animation inAnimation = new TranslateAnimation(0, 0, duration, 0);
        inAnimation.setDuration(duration);
        inAnimation.setInterpolator(new AccelerateInterpolator());
        Animation outAnimation = new TranslateAnimation(0, 0, 0, -duration);
        outAnimation.setDuration(duration);
        outAnimation.setInterpolator(new AccelerateInterpolator());
        setInAnimation(inAnimation);
        setOutAnimation(outAnimation);
        return this;
    }


    public void startScroll() {
        LogUtils.verbose(TAG, "startScroll....................");
        mScrollDisposable = Observable.interval(0, mPeriod, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> {
                    if (mData.size() > 0) {
                        currentId++;
                        setText(mData.get(currentId % mData.size()));
                    }
                }, throwable -> {
                    LogUtils.error(TAG, "Failed to startScroll, error msg: " + throwable.getMessage());
                    throwable.printStackTrace();
                });
    }

    public void stopScroll() {
        LogUtils.verbose(TAG, "stopScroll....................");
        if (mScrollDisposable != null && !mScrollDisposable.isDisposed()) {
            mScrollDisposable.dispose();
        }
    }

    @Override
    public View makeView() {
        TextView textView = new TextView(mContext);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setMaxLines(1);
        textView.setPadding(mTextPadding, mTextPadding, mTextPadding, mTextPadding);
        textView.setTextColor(mTextColor);
        textView.setTextSize(mTextSize);
        if (mTextTypeface != null){
            textView.setTypeface(mTextTypeface);
        }
        textView.setClickable(true);
        return textView;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.warn(TAG, "onDetachedFromWindow....................");
//        if (handler != null) {
//            handler.removeCallbacksAndMessages(null);
//        }
    }
}
