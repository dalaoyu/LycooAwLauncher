package com.lycoo.commons.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.view.ProgressStyle;

/**
 * Base Load Footer
 *
 * Created by lancy on 2018/3/15
 */
public abstract class BaseLoadFooter extends RelativeLayout implements LoadFooter {
    protected View mLoadingView;    // 正在加载视图
    protected View mEndView;        // 全部加载完成视图
    protected View mErrorView;      // 加载错误视图
    protected Context mContext;

    /**
     * 加载中提示
     */
    protected String mLoadingHint;
    /**
     * 全部加载完成提示
     */
    protected String mEndHint;
    /**
     * 异常提示
     */
    protected String mErrorHint;
    /**
     * 进度条样式
     */
    protected int mStyle;
    /**
     * 进度条颜色
     */
    protected int mIndicatorColor;
    /**
     * 提示颜色
     */
    protected int mHintColor;

    /**
     * Footer状态定义
     */
    public enum State {
        NORMAL,         // 默认
        LOADING,        // 加载中
        END,            // 全部加载完成，没有更多数据了
        ERROR           // 异常
    }

    private State mState;

    public BaseLoadFooter(Context context) {
        this(context, null);
    }

    public BaseLoadFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLoadFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    /**
     * Return layout id
     *
     * @return layout id
     *
     * Created by lancy on 2018/3/15 14:58
     */
    protected abstract int getLayoutId();

    /**
     * Show LOADING view
     *
     * Created by lancy on 2018/3/13 17:01
     */
    protected abstract void showLoadingView();

    /**
     * Show end view
     *
     * Created by lancy on 2018/3/13 17:01
     */
    protected abstract void showEndView();

    /**
     * Show error view
     *
     * Created by lancy on 2018/3/13 17:02
     */
    protected abstract void showErrorView();


    private void init() {
        // 构造Footer
        inflate(getContext(), getLayoutId(), this);
        setState(State.NORMAL);

        // 初始化默认参数
        mIndicatorColor = ContextCompat.getColor(getContext(), android.R.color.darker_gray);
        mHintColor = ContextCompat.getColor(getContext(), android.R.color.darker_gray);
        mStyle = ProgressStyle.BallPulse;
    }

    /**
     * Set footer ok
     *
     * @param state new State
     *
     *              Created by lancy on 2018/3/15 14:21
     */
    private void setState(State state) {
        if (mState == state) {
            return;
        }
        mState = state;
        switch (state) {
            case NORMAL:
                setOnClickListener(null);
                setVisibility(GONE);
                break;

            case LOADING:
                setVisibility(VISIBLE);
                setOnClickListener(null);
                ViewUtils.setViewShown(false, mEndView);
                ViewUtils.setViewShown(false, mErrorView);
                showLoadingView();
                break;

            case END:
                setVisibility(VISIBLE);
                setOnClickListener(null);
                ViewUtils.setViewShown(false, mLoadingView);
                ViewUtils.setViewShown(false, mErrorView);
                showEndView();
                break;

            case ERROR:
                setVisibility(VISIBLE);
                ViewUtils.setViewShown(false, mLoadingView);
                ViewUtils.setViewShown(false, mEndView);
                showErrorView();
                break;
        }
    }

    @Override
    public void onNormal() {
        setState(State.NORMAL);
    }

    @Override
    public void onLoading() {
        setState(State.LOADING);
    }

    @Override
    public void onComplete() {
        setState(State.NORMAL);
    }

    @Override
    public void onEnd() {
        setState(State.END);
    }

    @Override
    public void onError() {
        setState(State.ERROR);
    }

    @Override
    public View getFooterView() {
        return this;
    }

    public void setLoadingHint(String hint) {
        this.mLoadingHint = hint;
    }

    public void setEndHint(String hint) {
        this.mEndHint = hint;
    }

    public void setErrorHint(String hint) {
        this.mErrorHint = hint;
    }

    public void setIndicatorColor(int color) {
        this.mIndicatorColor = color;
    }

    public void setHintColor(int color) {
        this.mHintColor = color;
    }

    public void setBgColor(int color) {
        setBackgroundColor(color);
    }

    public void setProgressStyle(int style) {
        this.mStyle = style;
    }
}
