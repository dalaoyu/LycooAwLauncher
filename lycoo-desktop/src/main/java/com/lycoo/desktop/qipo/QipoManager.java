package com.lycoo.desktop.qipo;

import android.content.Context;

import com.lycoo.desktop.base.DesktopService;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * xxx
 *
 * Created by lancy on 2017/12/19
 */

public class QipoManager {

    private static QipoManager mInstance;
    private Context mContext;

    private QipoManager(Context context) {
        mContext = context;
    }

    public static QipoManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (QipoManager.class) {
                if (mInstance == null) {
                    mInstance = new QipoManager(context);
                }
            }
        }
        return mInstance;
    }

    public Observable<QipoInfo> query(String packageName) {
        // 从奇珀下载
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://down.7po.com/app/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit
                .create(DesktopService.class)
                .getAppFromQipo(QipoConstants.CHANNEL, packageName, "15.45.36.33");
//                .subscribeOn(Schedulers.io());
    }
}
