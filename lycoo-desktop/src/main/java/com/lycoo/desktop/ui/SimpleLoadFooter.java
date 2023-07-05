package com.lycoo.desktop.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.view.LoadingIndicatorView;
import com.lycoo.commons.view.ProgressStyle;
import com.lycoo.commons.view.SimpleViewSwitcher;
import com.lycoo.commons.widget.BaseLoadFooter;
import com.lycoo.desktop.R;

/**
 * 底部加载提示
 *
 * Created by lancy on 2018/3/15
 */
public class SimpleLoadFooter extends BaseLoadFooter {
    private SimpleViewSwitcher mProgressSwitcher;
    private TextView mLoadingText;
    private TextView mEndText;
    private TextView mErrorText;


    public SimpleLoadFooter(Context context) {
        this(context, null);
    }

    public SimpleLoadFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleLoadFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.footer_simple_load;
    }

    @Override
    protected void showLoadingView() {
        if (mLoadingView == null) {
            ViewStub viewStub = findViewById(R.id.vs_loading);
            mLoadingView = viewStub.inflate();
            mProgressSwitcher = mLoadingView.findViewById(R.id.svs_progress);
            mLoadingText = mLoadingView.findViewById(R.id.tv_loading);
        }
        ViewUtils.setViewShown(true, mLoadingView);
        ViewUtils.setViewShown(true, mProgressSwitcher);
        mProgressSwitcher.removeAllViews();
        mProgressSwitcher.setView(createIndicatorView(mStyle));
        mLoadingText.setText(TextUtils.isEmpty(mLoadingHint)
                ? getResources().getString(R.string.hint_loading)
                : mLoadingHint);
        mLoadingText.setTextColor(mHintColor);
        mLoadingText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
    }

    @Override
    protected void showEndView() {
        if (mEndView == null) {
            ViewStub viewStub = findViewById(R.id.vs_end);
            mEndView = viewStub.inflate();
            mEndText = mEndView.findViewById(R.id.tv_end);
        }

        ViewUtils.setViewShown(true, mEndView);
        mEndText.setText(TextUtils.isEmpty(mEndHint)
                ? getResources().getString(R.string.hint_load_end)
                : mEndHint);
        mEndText.setTextColor(mHintColor);
        mEndText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
    }


    @Override
    protected void showErrorView() {
        if (mErrorView == null) {
            ViewStub viewStub = findViewById(R.id.vs_error);
            mErrorView = viewStub.inflate();
            mErrorText = mErrorView.findViewById(R.id.tv_error);
        }

        ViewUtils.setViewShown(true, mErrorView);
        mErrorText.setText(TextUtils.isEmpty(mErrorHint)
                ? getResources().getString(R.string.hint_load_error)
                : mErrorHint);
        mErrorText.setTextColor(mHintColor);
        mErrorText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
    }


    /**
     * 创建指示器
     *
     * @param style 进度条样式
     * @return 指示器
     *
     * Created by lancy on 2018/3/13 17:03
     */
    private View createIndicatorView(int style) {
        if (style == ProgressStyle.SysProgress) {
            return new ProgressBar(getContext(), null, android.R.attr.progressBarStyle);
        } else {
            LoadingIndicatorView progressView = (LoadingIndicatorView) LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.indicator_view, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.loading_indicator_widht),
                    getResources().getDimensionPixelSize(R.dimen.loading_indicator_height));
            progressView.setLayoutParams(params);
            progressView.setIndicatorId(style);
            progressView.setIndicatorColor(mIndicatorColor);
            return progressView;
        }
    }

}
