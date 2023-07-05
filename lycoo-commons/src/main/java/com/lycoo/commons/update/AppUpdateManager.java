package com.lycoo.commons.update;

import android.content.Context;
import android.net.NetworkInfo;

import com.lycoo.commons.R;
import com.lycoo.commons.base.BaseService;
import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.entity.AppUpdate;
import com.lycoo.commons.entity.Version;
import com.lycoo.commons.http.HttpHelper;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.LogUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 应用更新辅助类
 *
 * Created by lancy on 2018/1/5
 */
public class AppUpdateManager {
    private static final String TAG = AppUpdateManager.class.getSimpleName();
    private static final boolean DEBUG_UI = false;

    private Context mContext;
    private static AppUpdateManager mInstance;
    private CompositeDisposable mCompositeDisposable;

    private AppUpdateManager(Context context) {
        mContext = context;
        mCompositeDisposable = new CompositeDisposable();
    }

    public static AppUpdateManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AppUpdateManager.class) {
                if (mInstance == null) {
                    mInstance = new AppUpdateManager(context);
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
     *                    Created by lancy on 2018/1/5 18:58
     */
    public void onNetworkChange(NetworkInfo networkInfo) {
        if (networkInfo == null) {
            return;
        }

        if (DEBUG_UI) {
            testUpdateApp();
        } else {
            updateApp();
        }
    }

    private void updateApp() {
        mCompositeDisposable.add(
                HttpHelper
                        .getInstance(mContext)
                        .getService(BaseService.class)
                        .getAppUpdateInfo(
                                ApplicationUtils.getApplicationMetaData(mContext, CommonConstants.APP_KEY),
                                ApplicationUtils.getVersionName(mContext),
                                DeviceUtils.getEthernetMacBySeparator(""),
                                DeviceUtils.getCustomerCode())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response == null) {
                                LogUtils.error(TAG, "Response is null......");
                                return;
                            }

                            if (response.getStatusCode() == CommonConstants.STATUS_CODE_ERROR) {
                                LogUtils.error(TAG, "Response message = " + response.getMessage());
                                return;
                            }

                            AppUpdate appUpdate = response.getData();
                            LogUtils.debug(TAG, "appUpdate = " + appUpdate);
                            if (appUpdate == null) {
                                LogUtils.error(TAG, "data is null......");
                                return;
                            }

                            if (appUpdate.getVersion().getCode() <= ApplicationUtils.getVersionCode(mContext)) {
                                LogUtils.info(TAG, "Latest app ......");
                                return;
                            }

                            showAppUpdateDialog(appUpdate);
                        }, throwable -> {
                            LogUtils.error(TAG, "failed to updateApp, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    private void testUpdateApp() {
        AppUpdate appUpdate = new AppUpdate();
        appUpdate.setUrl("");
        appUpdate.setMd5("");
        appUpdate.setName("");
        Version version = new Version();
        version.setCode(101);
        version.setName("1.0.1");
        version.setMajorUpdate(
                "1. 不将房地产作为短期刺激经济的手段\n " +
                        "2. 部分经济下行压力大的城市\n" +
                        "3. 不能直接放松房地产政策\n" +
                        "4. 以刺激的手段来对待房地产");
        appUpdate.setVersion(version);

        showAppUpdateDialog(appUpdate);
    }


    private void showAppUpdateDialog(AppUpdate appUpdate) {
        new AppUpdateDialog(mContext, R.style.DialogStyle, appUpdate).show();
    }

    public void onDestroy() {
        if (mCompositeDisposable != null && mCompositeDisposable.size() > 0) {
            mCompositeDisposable.clear();
        }

        mInstance = null;
    }
}
