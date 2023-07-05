package com.lycoo.commons.helper;

import android.content.Context;
import android.net.TrafficStats;

import com.lycoo.commons.util.LogUtils;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * 网速管理器
 *
 * Created by lancy on 2019/11/18
 */
public class NetSpeedManager {
    private static final String TAG = NetSpeedManager.class.getSimpleName();

    /**
     * 默认更新时间，3秒
     */
    private static final int DEF_UPDATE_PERIOD = 3000;

    private static NetSpeedManager mInstance;

    private long mLastTotalRxBytes = 0;
    private long mLastTimeStamp = 0;
    private int mUid;
    private DecimalFormat mDecimalFormat;

    private Disposable mNetSpeedDisposable;
    private long mUpdatePeriod = DEF_UPDATE_PERIOD;
    private String mSpeed;

    public NetSpeedManager(Context context) {
        mUid = context.getApplicationInfo().uid;
        mDecimalFormat = new DecimalFormat("#.0");
    }

    public static NetSpeedManager getInstance(Context context) {
        synchronized (NetSpeedManager.class) {
            if (mInstance == null) {
                synchronized (NetSpeedManager.class) {
                    mInstance = new NetSpeedManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 查询当前网速
     *
     * @return 当前网速
     * Created by lancy on 2019/11/18 2:48
     */
    public String getNetSpeed() {
        return mSpeed;
    }

    /**
     * 设置网速更新间隔
     *
     * @param period 更新间隔， 单位为毫秒
     *               Created by lancy on 2019/11/18 2:47
     */
    public void setUpdatePeriod(long period) {
        this.mUpdatePeriod = period;
    }

    /**
     * 开始计算网速
     *
     * Created by lancy on 2019/11/18 2:32
     */
    public void start() {
        stop();
        mNetSpeedDisposable = Observable
                .interval(1000, mUpdatePeriod, TimeUnit.MILLISECONDS)
                .subscribe(count -> {
                    mSpeed = calculateNetSpeed();
                    LogUtils.debug(TAG, ">>> Current NetSpeed: " + mSpeed);
                }, throwable -> {
                    LogUtils.error(TAG, "Failed to calcute NetSpeed, error msg: " + throwable.getMessage());
                    throwable.printStackTrace();
                });
    }

    /**
     * 停止计算网速
     *
     * Created by lancy on 2019/11/18 2:32
     */
    public void stop() {
        if (mNetSpeedDisposable != null && !mNetSpeedDisposable.isDisposed()) {
            mNetSpeedDisposable.dispose();
        }

        mLastTimeStamp = 0;
        mLastTotalRxBytes = 0;
    }

    /**
     * 计算当前网速
     *
     * @return 当前设备网速
     * Created by lancy on 2019/11/18 2:33
     */
    private String calculateNetSpeed() {
        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - mLastTotalRxBytes) * 1000 / (nowTimeStamp - mLastTimeStamp));//毫秒转换
        mLastTimeStamp = nowTimeStamp;
        mLastTotalRxBytes = nowTotalRxBytes;
        if (speed >= 1024) {
            return mDecimalFormat.format(speed / 1024f) + " MB/s";
        } else {
            return speed + " KB/s";
        }
    }

    private long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(mUid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    /**
     * 销毁
     *
     * Created by lancy on 2019/11/18 2:48
     */
    public void onDestroy() {
        stop();
    }


}
