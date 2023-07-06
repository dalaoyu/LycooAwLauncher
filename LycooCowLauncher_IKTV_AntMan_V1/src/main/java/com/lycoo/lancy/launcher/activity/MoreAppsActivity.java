package com.lycoo.lancy.launcher.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.lycoo.commons.app.LastingCustomerDialog;
import com.lycoo.commons.app.LogLevelDialog;
import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.widget.CustomToast;
import com.lycoo.desktop.base.BaseAppsActivity;
import com.lycoo.desktop.helper.DesktopItemManager;
import com.lycoo.lancy.launcher.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MoreAppsActivity extends BaseAppsActivity {
    private static final String TAG = MoreAppsActivity.class.getSimpleName();

    private BroadcastReceiver mPackageChangedBroadcastReceiver;

    private static int mCustomerCount = 0;
    private static int mFirmwareModeCount = 0;
    private static int mLogLevelCount = 0;
    private static int mBootManagerCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceivers();
    }

    @Override
    protected void initView() {
        super.initView();
        mPromptText.setText(R.string.prompt_uninstall_app);
        ViewUtils.setViewShown(false, mTitleText);
        ViewUtils.setViewShown(false, mBackButton);
    }

    @Override
    protected void loadData() {
        mCompositeDisposable.add(
                Observable
                        .zip(   // 已经显示在Dock上的应用
                                DesktopItemManager.getInstance(mContext).getPackageNames(),
                                // 不必要的系统应用
                                Observable.create((ObservableOnSubscribe<List<String>>) emitter -> {
                                    List<String> translatePackageNames = Arrays.asList(getResources().getStringArray(R.array.translate_apps));
                                    emitter.onNext(translatePackageNames);
                                }),
                                // 设置应用
                                Observable.create((ObservableOnSubscribe<List<String>>) emitter -> {
                                    String[] array = mContext.getResources().getStringArray(R.array.setup_packagenames);
                                    List<String> setupPackageNames = new ArrayList<>();
                                    if (array.length > 0) {
                                        for (String resource : array) {
                                            String[] split = resource.split("__");
                                            setupPackageNames.add(split[1]);
                                        }
                                    }
                                    emitter.onNext(setupPackageNames);
                                }),
                                (dockItemPackageNames, translatePackageNames, setupPackageNames) -> {
                                    Set<String> packageNames = new HashSet<>();
                                    packageNames.addAll(dockItemPackageNames);
                                    packageNames.addAll(translatePackageNames);
                                    packageNames.addAll(setupPackageNames);
                                    return packageNames;
                                })
                        .map(ignorePackageNames -> {
                            List<ResolveInfo> resolveInfos = new ArrayList<>();
                            List<ResolveInfo> launcherResolveInfos = ApplicationUtils.getAllLauncherResolveInfos(mContext);
                            for (ResolveInfo resolveInfo : launcherResolveInfos) {
                                String packageName = resolveInfo.activityInfo.applicationInfo.packageName;
                                if (ignorePackageNames.contains(packageName)) {
                                    continue;
                                }
                                resolveInfos.add(resolveInfo);
                            }
                            return resolveInfos;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resolveInfos -> {
                            mResolveInfos.clear();
                            mResolveInfos.addAll(resolveInfos);
                            mAppsAdapter.notifyDataSetChanged();
                            mAppsGridView.requestFocus();

                            ViewUtils.setViewShown(false, mLoadingProgressBar);
                        }, throwable -> {
                            LogUtils.error(TAG, "failed to load data, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addDataScheme("package");
        mPackageChangedBroadcastReceiver = new PackageChangedBroadcastReceiver();
        registerReceiver(mPackageChangedBroadcastReceiver, filter);
    }

    private void unregisterReceivers() {
        if (mPackageChangedBroadcastReceiver != null) {
            unregisterReceiver(mPackageChangedBroadcastReceiver);
        }
    }

    @Override
    protected void onItemLongClick(int position) {
        ResolveInfo resolveInfo = mResolveInfos.get(position);
        if (ApplicationUtils.categorizeAppByLevel(resolveInfo.activityInfo.applicationInfo) == 0) {
            CustomToast
                    .makeText(mContext, R.string.msg_uninstall_system_app, Toast.LENGTH_SHORT, CustomToast.MessageType.WARN)
                    .show();
        } else {
            ApplicationUtils.uninstallApp(mContext, resolveInfo.activityInfo.packageName);
        }
    }

    private final class PackageChangedBroadcastReceiver extends BroadcastReceiver {
        @SuppressLint("StaticFieldLeak")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String packageName = intent.getDataString().substring(8);
            LogUtils.debug(TAG, "action = " + action + ", packageName = " + packageName);
            if (StringUtils.isEmpty(action) || StringUtils.isEmpty(packageName)) {
                return;
            }

            if (action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
                ResolveInfo uninstalledResolveInfo = null;
                for (ResolveInfo resolveInfo : mResolveInfos) {
                    if (resolveInfo.activityInfo.packageName.equals(packageName)) {
                        uninstalledResolveInfo = resolveInfo;
                        break;
                    }
                }

                if (uninstalledResolveInfo != null) {
                    mResolveInfos.remove(uninstalledResolveInfo);
                    mAppsAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.debug(TAG, "keyCode = " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                if (++mCustomerCount == 5) {
                    mCustomerCount = 0;
                    showCustomerDialog();
                }
                break;
            case KeyEvent.KEYCODE_1:
                if (++mLogLevelCount == 5) {
                    mLogLevelCount = 0;
                    showLogLevelDialog();
                }
                break;
            case KeyEvent.KEYCODE_2:
                if (++mFirmwareModeCount == 5) {
                    mFirmwareModeCount = 0;
                    int mode = DeviceUtils.getFirmwareMode() == CommonConstants.FIRMWARE_MODE_DEBUG
                            ? CommonConstants.FIRMWARE_MODE_RELEASE
                            : CommonConstants.FIRMWARE_MODE_DEBUG;
                    int msgId = mode == CommonConstants.FIRMWARE_MODE_DEBUG
                            ? R.string.c_debug_mode
                            : R.string.c_release_mode;
                    Toast.makeText(mContext, msgId, Toast.LENGTH_LONG).show();
                    SystemPropertiesUtils.set(mContext, CommonConstants.PROPERTY_FIRMWARE_MODE, String.valueOf(mode));
                }
                break;

            case KeyEvent.KEYCODE_9:
                if (++mBootManagerCount == 5) {
                    mBootManagerCount = 0;
                    launchBootManager();
                }
                break;
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            default:
                mCustomerCount = 0;
                mLogLevelCount = 0;
                mFirmwareModeCount = 0;
                mBootManagerCount = 0;
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示"客户码"对话框
     *
     * Created by lancy on 2018/6/25 22:17
     */
    public void showCustomerDialog() {
        // Dialog dialog = new CustomerDialog(mContext, R.style.DarkDialogStyle);
        Dialog dialog = new LastingCustomerDialog(mContext, R.style.DarkDialogStyle);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    /**
     * 显示log级别对话框
     *
     * Created by lancy on 2018/6/25 22:17
     */
    private void showLogLevelDialog() {
        Dialog dialog = new LogLevelDialog(mContext, R.style.DarkDialogStyle);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    /**
     * 打开开机启动
     *
     * Created by lancy on 2018/6/26 19:18
     */
    private void launchBootManager() {
        try {
            ApplicationUtils.openApplication(mContext, "com.keily.bootstartmanage", "com.keily.bootstartmanage.MainActivity");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceivers();
        mCompositeDisposable.clear();
    }
}
