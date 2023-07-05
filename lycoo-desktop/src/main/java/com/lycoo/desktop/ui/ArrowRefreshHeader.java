package com.lycoo.desktop.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.helper.WeakHandler;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.view.LoadingIndicatorView;
import com.lycoo.commons.view.ProgressStyle;
import com.lycoo.commons.view.SimpleViewSwitcher;
import com.lycoo.commons.widget.RefreshHeader;
import com.lycoo.desktop.R;

import butterknife.ButterKnife;


/**
 * Created by lancy on 2018/3/19
 */
public class ArrowRefreshHeader extends LinearLayout implements RefreshHeader {
    private static final String TAG = ArrowRefreshHeader.class.getSimpleName();

    private static final boolean DEBUG = false;
    private static final int ROTATE_ANIM_DURATION = 180;

    LinearLayout mContainer;
    ImageView mArrowImage;
    SimpleViewSwitcher mProgressSwitcher;
    TextView mStatusText;
    TextView mTimeText;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private int mHintColor;
    public int mMeasuredHeight;

    private int mState = STATE_NORMAL;

    private Context mContext;

    private WeakHandler mHandler = new WeakHandler();

    public ArrowRefreshHeader(Context context) {
        this(context, null);
    }

    public ArrowRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        // 初始情况，设置下拉刷新view高度为0
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.recycler_arrow_refresh_header, null);
        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);
        ButterKnife.bind(mContainer);

        mArrowImage = findViewById(R.id.iv_arrow);
        mStatusText = findViewById(R.id.tv_status);
        mStatusText.setTypeface(StyleManager.getInstance(mContext).getTypeface());

        //init the progress view
        mProgressSwitcher = findViewById(R.id.switcher_progress);
        mProgressSwitcher.setView(createIndicatorView(ProgressStyle.BallSpinFadeLoader));

        mRotateUpAnim = new RotateAnimation(
                0.0f,
                -180.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);

        mRotateDownAnim = new RotateAnimation(
                -180.0f,
                0.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

//        mTimeText = findViewById(R.id.last_refresh_time);
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
        mHintColor = ContextCompat.getColor(getContext(), android.R.color.darker_gray);
    }

    @Override
    public void onMove(float offSet, float sumOffSet) {
        if (getVisibleHeight() > 0 || offSet > 0) {
            setVisibleHeight((int) offSet + getVisibleHeight());
//            if (mState <= STATE_RELEASE_TO_REFRESH) {
            if (getVisibleHeight() > mMeasuredHeight) {
                setState(STATE_RELEASE_TO_REFRESH);
            } else {
                setState(STATE_NORMAL);
            }
//            }
        }
    }

    @Override
    public boolean onRelease() {
        if (getVisibleHeight() > mMeasuredHeight) {
            setState(STATE_REFRESHING);
            smoothScrollTo(mMeasuredHeight);
            return true;
        }

        smoothScrollTo(0);
        return false;
    }

    @Override
    public void onRefreshing() {
        setState(STATE_REFRESHING);
        smoothScrollTo(mMeasuredHeight);
    }

    @Override
    public void refreshComplete() {
        setState(STATE_DONE);
        mHandler.postDelayed(() -> {
            smoothScrollTo(0);
//            setOk(STATE_NORMAL);
        }, 500);
    }

    @Override
    public View getHeaderView() {
        return this;
    }

    @Override
    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }

    private void setVisibleHeight(int height) {
        if (height < 0) {
            height = 0;
        }
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    private void setState(int state) {
        if (state == mState) {
            return;
        }

        if (DEBUG) {
            LogUtils.debug(TAG, "setOk, mState = " + getLogState(mState) + ", ok = " + getLogState(state));
        }
        switch (state) {
            case STATE_NORMAL:
                setViewShown(true, mArrowImage);
                setViewShown(false, mProgressSwitcher);
                // 下拉超过临界线，但是又上拉回临界线
                if (mState == STATE_RELEASE_TO_REFRESH) {
                    mArrowImage.startAnimation(mRotateDownAnim);
                }
                mStatusText.setText(R.string.hint_arrow_refresh_header_normal);
                break;

            case STATE_RELEASE_TO_REFRESH:
                setViewShown(true, mArrowImage);
                setViewShown(false, mProgressSwitcher);
                mArrowImage.clearAnimation();
                mArrowImage.startAnimation(mRotateUpAnim);
                mStatusText.setText(R.string.hint_arrow_refresh_header_release);
                break;

            case STATE_REFRESHING:
                mArrowImage.clearAnimation();
                setViewShown(false, mArrowImage);
                setViewShown(true, mProgressSwitcher);
                mStatusText.setText(R.string.hint_refreshing);
                break;

            case STATE_DONE:
                setViewShown(false, mArrowImage);
                setViewShown(false, mProgressSwitcher);
                mStatusText.setText(R.string.hint_refresh_done);
                break;
        }
        mStatusText.setTextColor(mHintColor);

        mState = state;
    }

    private void setViewShown(boolean show, View view) {
        if (view != null) {
            if (show) {
                if (view.getVisibility() != VISIBLE) {
                    view.setVisibility(VISIBLE);
                }
            } else {
                if (view.getVisibility() != INVISIBLE) {
                    view.setVisibility(INVISIBLE);
                }
            }
        }
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(animation ->
                setVisibleHeight((int) animation.getAnimatedValue()));
        animator.start();
    }

    private View createIndicatorView(int style) {
        LoadingIndicatorView progressView = (LoadingIndicatorView) LayoutInflater.from(getContext()).
                inflate(R.layout.indicator_view, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.loading_indicator_widht),
                getResources().getDimensionPixelSize(R.dimen.loading_indicator_height));
        progressView.setLayoutParams(params);
        progressView.setIndicatorId(style);
        progressView.setIndicatorColor(Color.GRAY);
        return progressView;
    }

    public void setProgressStyle(int style) {
        if (style == ProgressStyle.SysProgress) {
            ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyle);
            mProgressSwitcher.setView(progressBar);
        } else {
            mProgressSwitcher.setView(createIndicatorView(style));
        }
    }

    public void setIndicatorColor(int colorId) {
        if (mProgressSwitcher.getChildAt(0) instanceof LoadingIndicatorView) {
            LoadingIndicatorView progressView = (LoadingIndicatorView) mProgressSwitcher.getChildAt(0);
            progressView.setIndicatorColor(ContextCompat.getColor(getContext(), colorId));
        }
    }

    public void setHintColor(int colorId) {
        this.mHintColor = ContextCompat.getColor(getContext(), colorId);
    }

    public void setBgColor(int colorId) {
        this.setBackgroundColor(ContextCompat.getColor(getContext(), colorId));
    }

    public void setArrowImageView(int resid) {
        mArrowImage.setImageResource(resid);
    }

    private String getLogState(int state) {
        switch (state) {
            case STATE_NORMAL:
                return " normal";

            case STATE_REFRESHING:
                return " refreshing";

            case STATE_RELEASE_TO_REFRESH:
                return " release_to_refresh";

            case STATE_DONE:
                return " done";
        }
        return "";
    }
}
