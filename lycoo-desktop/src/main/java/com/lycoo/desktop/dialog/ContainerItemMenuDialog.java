package com.lycoo.desktop.dialog;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

import com.lycoo.commons.helper.RxBus;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.widget.CustomToast;
import com.lycoo.desktop.R;
import com.lycoo.desktop.helper.DesktopEvent;
import com.lycoo.desktop.helper.DesktopItemManager;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * 容器子坑位操作对话框
 *
 * Created by lancy on 2018/6/19
 */
public class ContainerItemMenuDialog extends AppMenuDialog {
    private static final String TAG = ContainerItemMenuDialog.class.getSimpleName();

    private int mContainerType;
    private ResolveInfo mResolveInfo;

    public ContainerItemMenuDialog(Context context, int themeResId, int containerType, ResolveInfo resolveInfo) {
        super(context, themeResId);
        this.mContainerType = containerType;
        this.mResolveInfo = resolveInfo;
    }

    @Override
    protected void initView() {
        super.initView();
        ViewUtils.setViewShown(false, mReplaceMenuItem);

        if (mResolveInfo == null) {
            mUnbindMenuItem.setFocusable(false);
            mUninstallMenuItem.setFocusable(false);
        }
    }

    @Override
    protected void unbind() {
        super.unbind();

        mCompositeDisposable.add(
                Observable
                        .create((ObservableOnSubscribe<Integer>) emitter -> {
                            int rows = DesktopItemManager
                                    .getInstance(mContext)
                                    .removeContainerItemInfos(mContainerType, mResolveInfo.activityInfo.packageName);
                            emitter.onNext(rows);
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe(rows -> {
                            if (rows > 0) {
                                RxBus.getInstance().post(new DesktopEvent.RemoveAppEvent(mResolveInfo.activityInfo.packageName));
                            }
                        }, throwable -> {
                            LogUtils.error(TAG, "failed to unbind, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    @Override
    protected void uninstall() {
        super.uninstall();

        if (ApplicationUtils.categorizeAppByLevel(mResolveInfo.activityInfo.applicationInfo) == 0) {
            CustomToast
                    .makeText(mContext, R.string.msg_uninstall_system_app, Toast.LENGTH_SHORT, CustomToast.MessageType.WARN)
                    .show();
        } else {
            ApplicationUtils.uninstallApp(mContext, mResolveInfo.activityInfo.packageName);
        }
    }

    @Override
    protected void showAppsDialog() {
        if (mAppsDialog != null && mAppsDialog.isShowing()) {
            return;
        }

        mAppsDialog = new ContainerAppsDialog(mContext, R.style.AppsDialogStyle, 5, mContainerType);
        mAppsDialog.show();
    }
}
