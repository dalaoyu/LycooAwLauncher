package com.lycoo.lancy.launcher.ui;

import android.content.Context;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.lycoo.commons.helper.RxBus;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.marquee.MarqueeEvent;
import com.lycoo.commons.entity.MarqueeInfo;
import com.lycoo.commons.marquee.MarqueeManager;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.widget.AutoScrollTextView;
import com.lycoo.lancy.launcher.R;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 滚动字幕
 *
 * Created by lancy on 2018/1/2
 */
public class Marquee extends FrameLayout {
    private static final String TAG = Marquee.class.getSimpleName();
    private static final boolean DEBUG_UI = false;

    private AutoScrollTextView mScrollTextView;

    private Context mContext;
    private MarqueeInfo mMarqueeInfo;
    private Disposable mMarqueeDisposable;

    public Marquee(@NonNull Context context) {
        super(context);
        mContext = context;

        initView();
        startMarquee();
        subscribeUpdateEvent();
    }

    /**
     * 初始化
     *
     * Created by lancy on 2018/1/2 18:54
     */
    private void initView() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mScrollTextView = new AutoScrollTextView(mContext);
        mScrollTextView.setPadding(
                0,
                getResources().getDimensionPixelSize(R.dimen.marquee_padding_top),
                0,
                getResources().getDimensionPixelSize(R.dimen.marquee_padding_bottom));
        mScrollTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.marquee_text_size));
        mScrollTextView.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mScrollTextView.setMaxLines(1);
        mScrollTextView.setLayoutParams(params);
        addView(mScrollTextView);

        if (DEBUG_UI) {
            mScrollTextView.setBackgroundColor(Color.GREEN);
        }

        if (DEBUG_UI) {
            setBackgroundColor(Color.RED);
        }
    }

    /**
     * 订阅更新事件
     *
     * Created by lancy on 2018/1/2 18:55
     */
    private void subscribeUpdateEvent() {
        Disposable disposable = RxBus.getInstance()
                .registerSubscribe(
                        MarqueeEvent.class,
                        marqueeEvent -> restartMarquee(),
                        throwable -> LogUtils.error(TAG, "")
                );
        RxBus.getInstance().addDisposable(this, disposable);
    }

    /**
     * 开始跑马灯
     *
     * Created by lancy on 2018/1/2 18:55
     */
    private void startMarquee() {
        //interval and timer默认是在新的线程中执行
        if (mMarqueeInfo == null) {
            mMarqueeInfo = MarqueeManager.getInstance(mContext).getMargueeInfo();
        }

        if (!mMarqueeInfo.isShow()) {
            return;
        }

        mMarqueeDisposable = Observable
                .interval(0, mMarqueeInfo.getPeriod(), TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (mMarqueeInfo != null
                            && mMarqueeInfo.isShow()
                            && !StringUtils.isEmpty(mMarqueeInfo.getData())) {
                        ViewUtils.setViewShown(true, Marquee.this);
                        mScrollTextView.setText(mMarqueeInfo.getData());
                        mScrollTextView.init(getResources().getDimensionPixelSize(R.dimen.marguee_width));
                        mScrollTextView.startScroll();
                        mScrollTextView.setOnCircleRollListener(count -> {
                            if (count >= mMarqueeInfo.getCount()) {
                                mScrollTextView.stopScroll();
                                mScrollTextView.setCount(0);

                                ViewUtils.setViewShown(false, Marquee.this);
                            }
                        });
                    }
                });
    }

    /**
     * 停止跑马灯
     *
     * Created by lancy on 2018/1/2 18:55
     */
    private void stopMarquee() {
        if (mMarqueeDisposable != null && !mMarqueeDisposable.isDisposed()) {
            mMarqueeDisposable.dispose();
        }

        mScrollTextView.stopScroll();
        mScrollTextView.setCount(0);
        ViewUtils.setViewShown(false, Marquee.this);

        mMarqueeInfo = null;
    }

    /**
     * 重新开始跑马灯
     *
     * Created by lancy on 2018/1/2 18:55
     */
    private void restartMarquee() {
        stopMarquee();
        startMarquee();
    }

    /**
     * 网络状态回调
     * 当网络状态发生变化时调用此方法
     *
     * @param networkInfo 网络信息
     *
     *                    Created by lancy on 2018/1/2 18:58
     */
    public void onNetworkChange(NetworkInfo networkInfo) {
        if (networkInfo == null) {
            return;
        }

        // 更新跑马灯
        MarqueeManager.getInstance(mContext).getMarqueeInfo();
    }

    /**
     * 销毁
     *
     * Created by lancy on 2018/1/2 19:03
     */
    public void onDestroy() {
        RxBus.getInstance().unRegisterSubscribe(this);
    }


}
