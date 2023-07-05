package com.lycoo.desktop.dialog;

import android.content.Context;
import android.content.pm.ResolveInfo;

import com.lycoo.commons.helper.RxBus;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.desktop.helper.DesktopEvent;
import com.lycoo.desktop.helper.DesktopItemManager;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * xxx
 *
 * Created by lancy on 2018/6/19
 */
public class ContainerAppsDialog extends AppsDialog {
    private static final String TAG = ContainerAppsDialog.class.getSimpleName();

    private int mContainerType;

    ContainerAppsDialog(Context context, int themeResId, int numColumns, int containerType) {
        super(context, themeResId, numColumns);
        this.mContainerType = containerType;
    }

    @Override
    protected Observable<List<String>> getPackageNames() {
        return DesktopItemManager.getInstance(mContext).getContainerItemPackageNames(mContainerType);
    }

    @Override
    protected void doUpdate(ResolveInfo resolveInfo) {
        mCompositeDisposable.add(
                DesktopItemManager
                        .getInstance(mContext)
                        .getContainerItemInfo(mContainerType, resolveInfo.activityInfo.packageName)
                        .subscribeOn(Schedulers.io())
                        .subscribe(containerItemInfo -> {
                            // 当前应用没有归类到当前分类
                            if (StringUtils.isEmpty(containerItemInfo.getPackageName())) {
                                containerItemInfo.setContainerType(mContainerType);
                                containerItemInfo.setPackageName(resolveInfo.activityInfo.packageName);
                                // 持久化
                                DesktopItemManager.getInstance(mContext).saveContainerItemInfo(containerItemInfo);
                                // 更新显示
                                RxBus.getInstance().post(new DesktopEvent.AddAppEvent(resolveInfo));
                            }
                            dismiss();
                        }, throwable -> {
                            LogUtils.error(TAG, "failed to saveContainerItemInfo, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }
}
