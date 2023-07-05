package com.lycoo.commons.charge;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import com.lycoo.commons.util.LogUtils;

import java.io.IOException;
import java.text.ParseException;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppChargeManager {
    private static final String TAG = AppChargeManager.class.getSimpleName();
    private static final boolean DEBUG_UI = false;

    private Context mContext;
    private static AppChargeManager mInstance;
    private CompositeDisposable mCompositeDisposable;
    private AppChargeDialog appChargeDialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            appChargeDialog.dismiss();
            appChargeDialog.cancel();
        }
    };

    private AppChargeManager(Context context) {
        mContext = context;
        mCompositeDisposable = new CompositeDisposable();
    }

    public static AppChargeManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AppChargeManager.class) {
                if (mInstance == null) {
                    mInstance = new AppChargeManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 网络状态改变回调
     *
     * @param networkInfo 网络信息
     *                    <p>
     *                    Created by lancy on 2018/1/5 18:58
     */
    public void onNetworkChange(NetworkInfo networkInfo, boolean charge) {
        if (networkInfo != null && charge) {
            updateWebView();
        }
    }

    private void updateWebView() {
        appChargeDialog = new AppChargeDialog(mContext);
        appChargeDialog.show();

    }

    public boolean chargeQuery() {
        LogUtils.error(TAG, "定时任务请求数据！！！");
        OkHttpClient okHttpClient = new OkHttpClient();
        Request build = new Request.Builder()
                .url("http://kge.cn9441.cn/class.aspx?id=68-54-5A-89-93-BF")
                .build();
        try {
            Response execute = okHttpClient.newCall(build).execute();
            String string = execute.body().string();
            LogUtils.error(TAG, "chargeQuery :" + string.contains("False"));
            if (string.contains("False")) {
                return true;
            } else {
                if (appChargeDialog != null) {
                    handler.sendMessage(new Message());
                }
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void onDestroy() {
        if (mCompositeDisposable != null && mCompositeDisposable.size() > 0) {
            mCompositeDisposable.clear();
        }

        mInstance = null;

        if (appChargeDialog != null) {
            appChargeDialog.dismiss();
        }

    }
}
