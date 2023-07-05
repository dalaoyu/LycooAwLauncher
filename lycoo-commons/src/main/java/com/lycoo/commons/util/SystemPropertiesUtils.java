package com.lycoo.commons.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.lycoo.commons.domain.CommonConstants;

import java.lang.reflect.Method;

/**
 * 系统属性的工具类
 *
 * Created by lancy on 2017/6/15 23:27
 */
public class SystemPropertiesUtils {

    /**
     * 获取系统属性值
     *
     * @param key 属性名
     * @return 属性值
     *
     * Created by lancy on 2017/6/15 23:28
     */
    public static String get(String key) {
        String[] arrays = new String[1];
        arrays[0] = key;

        return (String) load("android.os.SystemProperties", "get", arrays);
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean def) {
        String value = get(key);
        if (TextUtils.isEmpty(value)) {
            return def;
        }
        return Boolean.parseBoolean(value);
    }

    public static int getInt(String key, int defValue) {
        String value = get(key);
        if (!TextUtils.isEmpty(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return defValue;
    }

    /**
     * 修改系统属性
     * 实际上通过LycooPropertyUpdate完成功能
     * 该方法现已不推荐使用， 请使用SystemPropertiesManager
     *
     * @param context 上下文
     * @param key     属性名
     * @param value   属性值
     *
     *                Created by lancy on 2017/6/15 23:26
     */
    @Deprecated
    public static void set(Context context, String key, String value) {
        Intent intent = new Intent();
        intent.setAction(CommonConstants.ACTION_UPDATE_PROPERTY);
        intent.putExtra(CommonConstants.PROPERTY_KEY, key);
        intent.putExtra(CommonConstants.PROPERTY_VALUE, value);
        context.sendBroadcast(intent);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Object load(String className, String methed, Object[] fields) {
        Object result = null;
        try {
            Class localClass = Class.forName(className);
            // Object object = localClass.getConstructor(null).newInstance(null);
            Method method = localClass.getMethod(methed, getParamTypes(localClass, methed));
            method.setAccessible(true);
            result = method.invoke(localClass, fields);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
    private static Class[] getParamTypes(Class className, String method) {
        Class[] arrayOfClass = null;
        Method[] arrayOfMethod = className.getDeclaredMethods();
        for (int i = 0; i < arrayOfMethod.length; i++) {
            if (arrayOfMethod[i].getName().equals(method)) {
                arrayOfClass = arrayOfMethod[i].getParameterTypes();
                break;
            }
        }
        return arrayOfClass;
    }
}
