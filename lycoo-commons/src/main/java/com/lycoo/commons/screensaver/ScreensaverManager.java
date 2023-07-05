package com.lycoo.commons.screensaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.CountDownTimer;

import com.lycoo.commons.base.BaseService;
import com.lycoo.commons.db.CommonDbManager;
import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.entity.Screensaver;
import com.lycoo.commons.entity.ScreensaverImage;
import com.lycoo.commons.entity.ScreensaverResponse;
import com.lycoo.commons.http.HttpHelper;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.CollectionUtils;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 屏保辅助类
 *
 * Created by lancy on 2018/1/2
 */
public class ScreensaverManager {
    private static final String TAG = ScreensaverManager.class.getSimpleName();

    private Context mContext;
    private boolean mShow;
    private SharedPreferences mScreenSp;
    private static ScreensaverManager mInstance;
    private CompositeDisposable mCompositeDisposable;

    private ScreensaverManager(Context context) {
        this.mContext = context;
        mScreenSp = context.getSharedPreferences(ScreensaverConstants.SP_NAME, Context.MODE_PRIVATE);
        mShow = mScreenSp.getBoolean(ScreensaverConstants.SHOW, false);
        mCompositeDisposable = new CompositeDisposable();
    }

    public static ScreensaverManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ScreensaverManager.class) {
                if (mInstance == null) {
                    mInstance = new ScreensaverManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 网络状态改变回调
     *
     * @param networkInfo 网络信息
     *
     *                    Created by lancy on 2018/1/4 18:34
     */
    public void onNetworkChange(NetworkInfo networkInfo) {
        if (networkInfo == null) {
            return;
        }

        getScreensaverInfo();
    }

    /**
     * 更新屏保信息
     *
     * Created by lancy on 2018/1/4 17:33
     */
    public void getScreensaverInfo() {
        mCompositeDisposable.add(
                HttpHelper
                        .getInstance(mContext)
                        .getService(BaseService.class)
                        .getScreensaverInfo(
                                ApplicationUtils.getApplicationMetaData(mContext, CommonConstants.APP_KEY),
                                DeviceUtils.getEthernetMacBySeparator(""),
                                DeviceUtils.getCustomerCode())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(this::parseScreensaverInfo, throwable -> {
                            LogUtils.error(TAG, "failed to getScreensaverInfo , error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    /**
     * 解析屏保信息
     *
     * @param response 服务端返回信息
     * @throws ParseException 异常
     *
     *                        Created by lancy on 2018/1/4 17:36
     */
    private void parseScreensaverInfo(ScreensaverResponse response) throws ParseException {
        LogUtils.debug(TAG, "response : " + response);
        if (response == null) {
            LogUtils.error(TAG, "response is null......");
            return;
        }

        if (response.getStatusCode() == CommonConstants.STATUS_CODE_ERROR) {
            LogUtils.error(TAG, "responseMessage = " + response.getMessage());
            return;
        }

        Screensaver screensaver = response.getData();
        if (screensaver == null) {
            LogUtils.error(TAG, "screensaver is null ......");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        if (!dateFormat.parse(getUpdateTime()).before(dateFormat.parse(screensaver.getUpdateTime()))) {
            LogUtils.info(TAG, "screensave is the latest version......");
            return;
        }

        // 不允许显示屏保
        if (!screensaver.isShow()) {
            mScreenSp
                    .edit()
                    .putBoolean(ScreensaverConstants.SHOW, false)
                    .putString(ScreensaverConstants.UPDATETIME, screensaver.getUpdateTime())
                    .apply();

            if (isShow()) {
                stopCount();
                setShow(false);
                // 如果当前已经在显示屏保，没必要马上关掉， 如果你想关可以使用RxBus
            }
            return;
        }

        List<ScreensaverImage> images = screensaver.getImages();
        // 没有配置屏保图片，按不允许显示屏保处理
        if (CollectionUtils.isEmpty(images)) {
            mScreenSp
                    .edit()
                    .putBoolean(ScreensaverConstants.SHOW, false)
                    .putString(ScreensaverConstants.UPDATETIME, screensaver.getUpdateTime())
                    .apply();

            if (isShow()) {
                stopCount();
                setShow(false);
                // 如果当前已经在显示屏保，没必要马上关掉， 如果你想关可以使用RxBus
            }
            return;
        }

        // 持久化屏保信息
        // 更新屏保信息
        mScreenSp
                .edit()
                .putBoolean(ScreensaverConstants.SHOW, screensaver.isShow())
                .putString(ScreensaverConstants.UPDATETIME, screensaver.getUpdateTime())
                .putString(ScreensaverConstants.NAME, screensaver.getName())
                .putInt(ScreensaverConstants.PERIOD, screensaver.getPeriod())
                .apply();

        // 更新屏保图片信息
        updateScreensaverImages(images);

        // 如果当前没有开始计数， do it now
        if (!mShow) {
            setShow(true);
            restartCount();
        }
    }

    /**
     * 获取屏保更新时间戳
     *
     * @return 屏保更新时间戳
     *
     * Created by lancy on 2018/1/4 17:33
     */
    private String getUpdateTime() {
        return mScreenSp.getString(ScreensaverConstants.UPDATETIME, ScreensaverConstants.DEFAULT_UPDATETIME);
    }

    /**
     * 查询图片切换间隔
     *
     * @return 图片切换间隔
     *
     * Created by lancy on 2018/1/4 18:14
     */
    public int getPeriod() {
        return mScreenSp.getInt(ScreensaverConstants.PERIOD, ScreensaverConstants.DEFAULT_PERIOD);
    }

    /**
     * 更新屏保图片信息
     *
     * @param images 更新的图片
     *
     *               Created by lancy on 2018/1/4 17:32
     */
    private void updateScreensaverImages(List<ScreensaverImage> images) {
        CommonDbManager.getInstance(mContext).removeAllScreensaverImages();
        CommonDbManager.getInstance(mContext).saveScreensaverImages(images);
    }

    /**
     * 获取屏保图片地址
     *
     * @return 所有屏保图片地址
     *
     * Created by lancy on 2018/1/4 18:06
     */
    public Observable<List<String>> getScreensaverImageUrls() {
        return Observable
                .create(e -> {
                    List<String> urls = CommonDbManager
                            .getInstance(mContext)
                            .getScreensaverImageColumn(ScreensaverConstants.SCREENSAVER_IMAGE_TABLE.COLUMN_URL);
                    e.onNext(urls);
                    e.onComplete();
                });
    }

    /**
     * 是否显示屏保
     *
     * @return true: 显示， false：不显示
     *
     * Created by lancy on 2018/1/4 17:31
     */
    private boolean isShow() {
        return mShow;
    }

    /**
     * 设置屏保显示状态
     *
     * @param show true:允许显示， false: 不允许显示
     *
     *             Created by lancy on 2018/1/4 19:18
     */
    public void setShow(boolean show) {
        this.mShow = show;
    }

    /**
     * 屏保计数器
     *
     * Created by lancy on 2018/1/4 17:43
     */
    private final CountDownTimer mCountDownTimer = new CountDownTimer(ScreensaverConstants.DEFAULT_SILENT_TIME, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            LogUtils.info(TAG, "showScreensaver()......");
            showScreensaver();
        }
    };

    /**
     * 停止计数
     *
     * Created by lancy on 2018/1/4 17:42
     */
    public void stopCount() {
        LogUtils.verbose(TAG, "stopCount()......");
        if (isShow()) {
            mCountDownTimer.cancel();
        }
    }

    /**
     * 开始计数
     *
     * Created by lancy on 2018/1/4 17:26
     */
    public void startCount() {
        LogUtils.verbose(TAG, "startCount()......");
        if (isShow()) {
            mCountDownTimer.start();
        }
    }

    /**
     * 重新开始计数
     *
     * Created by lancy on 2018/1/4 17:26
     */
    public void restartCount() {
        LogUtils.verbose(TAG, "restartCount()......");
        if (isShow()) {
            mCountDownTimer.cancel();
            mCountDownTimer.start();
        }
    }

    /**
     * 显示屏保
     *
     * Created by lancy on 2018/1/4 17:29
     */
    private void showScreensaver() {
        mContext.startActivity(new Intent(mContext, ScreensaverImageActivity.class));
    }


    /**
     * 销毁回调
     *
     * Created by lancy on 2019/8/13 16:26
     */
    public void onDestroy() {
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }

        mInstance = null;
    }
}
