package com.lycoo.commons.util;

import android.util.Log;

/**
 * Log工具
 *
 * Created by lancy on 2017/3/12
 */
public class LogUtils {
    private static final String TAG_PREFIX = "lycoo-app-";

    private static final int LEVEL_VERBOSE = 1;
    private static final int LEVEL_DEBUG = 2;
    private static final int LEVEL_INFO = 3;
    private static final int LEVEL_WARN = 4;
    private static final int LEVEL_ERROR = 5;

    private static int logLevel;

    static {
        logLevel = DeviceUtils.getLogLevel();
    }

    public static int getLogLevel() {
        return logLevel;
    }

    public static void setLogLevel(int logLevel) {
        LogUtils.logLevel = logLevel;
    }

    public static void verbose(String tag, String msg) {
        if (logLevel <= LEVEL_VERBOSE) {
            Log.v(TAG_PREFIX + tag, msg);
        }
    }

    public static void debug(String tag, String msg) {
        if (logLevel <= LEVEL_DEBUG) {
            Log.d(TAG_PREFIX + tag, msg);
        }
    }

    public static void info(String tag, String msg) {
        if (logLevel <= LEVEL_INFO) {
            Log.i(TAG_PREFIX + tag, msg);
        }
    }

    public static void warn(String tag, String msg) {
        if (logLevel <= LEVEL_WARN) {
            Log.w(TAG_PREFIX + tag, msg);
        }
    }

    public static void error(String tag, String msg) {
        if (logLevel <= LEVEL_ERROR) {
            Log.e(TAG_PREFIX + tag, msg);
        }
    }

    public static void error(String tag, String msg,Throwable throwable) {
        if (logLevel <= LEVEL_ERROR) {
            Log.e(TAG_PREFIX + tag, msg,throwable);
        }
    }
}
