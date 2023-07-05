package com.lycoo.commons.helper;

import android.annotation.SuppressLint;
import android.app.LycooManager;
import android.content.Context;

import com.lycoo.commons.util.SystemPropertiesUtils;

/**
 * 系统属性管理器
 *
 * Created by lancy on 2019/8/15
 */
public class SystemPropertiesManager {

    private static SystemPropertiesManager mInstance;
    private LycooManager mLycooManager;

    @SuppressLint("WrongConstant")
    public SystemPropertiesManager(Context context) {
        mLycooManager = (LycooManager) context.getSystemService("lycoo");
    }

    public static SystemPropertiesManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SystemPropertiesUtils.class) {
                if (mInstance == null) {
                    mInstance = new SystemPropertiesManager(context);
                }
            }
        }
        return mInstance;
    }


    public String get(String key, String def) {
        return mLycooManager.getProperty(key, def);
    }

    public int getInt(String key, int def) {
        String val = mLycooManager.getProperty(key, String.valueOf(def));
        return Integer.parseInt(val);
    }

    public long getLong(String key, long def) {
        String val = mLycooManager.getProperty(key, String.valueOf(def));
        return Long.parseLong(val);
    }

    public boolean getBoolean(String key, boolean def) {
        String val = mLycooManager.getProperty(key, String.valueOf(def));
        return Boolean.parseBoolean(val);
    }

    public void set(String key, Object val) {
        if (val instanceof Boolean
                || val instanceof Integer
                || val instanceof Long) {
            mLycooManager.setProperty(key, String.valueOf(val));
        } else {
            throw new RuntimeException("invalid value......");
        }
    }

    public void set(String key, String val) {
        mLycooManager.setProperty(key, val);
    }

}
