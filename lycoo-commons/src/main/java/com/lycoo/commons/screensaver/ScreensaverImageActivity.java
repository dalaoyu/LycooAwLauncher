package com.lycoo.commons.screensaver;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lycoo.commons.R;
import com.lycoo.commons.util.CollectionUtils;
import com.lycoo.commons.util.LogUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 显示屏保界面
 *
 * Created by lancy on 2018/1/4
 */
public class ScreensaverImageActivity extends Activity {
    private static final String TAG = ScreensaverImageActivity.class.getSimpleName();

    ImageView iv_image;
    private Context mContext = this;
    private int mIndex;
    private Disposable mDisposable;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screensaver_image);

        initView();
        initData();
    }

    private void initView() {
        iv_image = findViewById(R.id.iv_screensaver_image);
    }

    private void initData() {
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(
                ScreensaverManager
                        .getInstance(mContext)
                        .getScreensaverImageUrls()
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(urls -> {
                            LogUtils.debug(TAG, "urls : " + urls);
                            showImages(urls);
                        }, throwable -> LogUtils.error(TAG, "get image urls failed, error message : " + throwable.getMessage())));
    }

    /**
     * 显示图片
     *
     * @param urls 图片地址
     *
     *             Created by lancy on 2018/1/4 18:24
     */
    private void showImages(final List<String> urls) {
        if (CollectionUtils.isEmpty(urls)) {
            return;
        }

        int period = ScreensaverManager.getInstance(mContext).getPeriod();
        mDisposable = Observable
                .interval(period, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (++mIndex > urls.size() - 1) {
                        mIndex = 0;
                    }

                    Glide.with(mContext)
                            .load(urls.get(mIndex))
                            .into(iv_image);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }

        mCompositeDisposable.clear();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            LogUtils.verbose(TAG, "dispatchKeyEvent()......");
            finish();
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            LogUtils.verbose(TAG, "dispatchTouchEvent()......");
            finish();
        }

        return super.dispatchTouchEvent(ev);
    }
}
