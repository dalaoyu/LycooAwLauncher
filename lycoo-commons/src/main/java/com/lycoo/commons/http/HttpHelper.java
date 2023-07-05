package com.lycoo.commons.http;

import android.content.Context;
import android.text.TextUtils;

import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.LogUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Http请求辅助类
 * 1. 采用Retrofit + rxjava模式。
 * 2. 注意base_url问题（只适用于lycoocms模式）
 * 3. 未添加添加Dagger支持
 *
 * Created by lancy on 2017/12/16
 */
public class HttpHelper {
    private static final String TAG = HttpHelper.class.getSimpleName();

    private static final int DEFAULT_TIMEOUT = 20;
    private HashMap<String, Object> mServiceMap;
    private Context mContext;

    private static HttpHelper mInstance;

    public static HttpHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (HttpHelper.class) {
                if (mInstance == null) {
                    mInstance = new HttpHelper(context);
                }
            }
        }
        return mInstance;
    }

    public HttpHelper(Context context) {
        mContext = context;
        mServiceMap = new HashMap<>();
    }

    /**
     * 获取接口服务
     *
     * @param clazz 接口服务类型
     * @param <T>   接口服务类
     * @return 接口服务
     *
     * Created by lancy on 2017/12/16 12:02
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> clazz) {
        if (mServiceMap.containsKey(clazz.getName())) {
            return (T) mServiceMap.get(clazz.getName());
        } else {
            Object obj = createService(clazz);
            mServiceMap.put(clazz.getName(), obj);
            return (T) obj;
        }
    }

    /**
     * 创建接口服务
     *
     * @param clazz 接口服务类型
     * @param <T>   接口服务类
     * @return 接口服务
     *
     * Created by lancy on 2017/12/16 12:03
     */
    public <T> T createService(Class<T> clazz, Interceptor... interceptors) {
        // 设置访问请求参数
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 超时设置
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        // 缓存设置
//        File cacheDir = new File(mContext.getCacheDir(), "OkHttpCache");
//        builder.cache(new Cache(cacheDir, 10 * 1024 * 1024));

        // 拦截器设置(日志， 缓存处理等...)
//        builder.addNetworkInterceptor()
//        builder.addInterceptor();
        String baseUrl = null;
        try {
            boolean debug = DeviceUtils.isDebugMode();
            if (debug) {
                // 在调试模式，如果系统配置了测试服务器地址，则使用系统配置，否则使用默认
                String debugHost = DeviceUtils.getDebugHost();
                if (!TextUtils.isEmpty(debugHost)) {
                    baseUrl = debugHost.trim();
                }
            }

            if (TextUtils.isEmpty(baseUrl)) {
                Field field = clazz.getDeclaredField(debug ? "BASE_URL_DEBUG" : "BASE_URL");
                baseUrl = (String) field.get(clazz);
            }
            LogUtils.debug(TAG, "baseUrl = " + baseUrl);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (baseUrl == null) {
            return null;
        }

        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }

        /*
        String baseUrl = DeviceUtils.getFirmwareMode() == CommonConstants.FIRMWARE_MODE_DEBUG
                ? CommonConstants.BASE_URL_DEBUG
                : CommonConstants.BASE_URL;
        */
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(clazz);
    }
}
