package com.lycoo.desktop.base;

import android.app.Application;

import com.lycoo.commons.util.LogUtils;
import com.lycoo.desktop.qiyi.QiyiManager;

/**
 * xxx
 *
 * Created by lancy on 2017/12/14
 */
public class BaseApplication extends Application {
    private static final String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.verbose(TAG, "onCreate()......");

        // 初始化QiClient, 保证只初始化一次
        QiyiManager.getInstance(this).init();
    }
}
