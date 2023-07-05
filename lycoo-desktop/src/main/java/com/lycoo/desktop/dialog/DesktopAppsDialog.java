package com.lycoo.desktop.dialog;

import android.content.Context;
import android.content.pm.ResolveInfo;

import com.lycoo.commons.util.LogUtils;
import com.lycoo.desktop.helper.DesktopItemManager;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lancy on 2018/5/11
 */
public class DesktopAppsDialog extends AppsDialog {
    private static final String TAG = DesktopAppsDialog.class.getSimpleName();

    private int mTag;

    public DesktopAppsDialog(Context context, int themeResId, int numColumns, int tag) {
        super(context, themeResId, numColumns);
        this.mTag = tag;
    }

    @Override
    Observable<List<String>> getPackageNames() {
        return DesktopItemManager.getInstance(mContext).getPackageNames();
    }

    @Override
    void doUpdate(ResolveInfo resolveInfo) {
        mCompositeDisposable.add(
                DesktopItemManager
                        .getInstance(mContext)
                        .getItemInfoByTag(mTag)
                        .doOnNext(desktopItemInfo -> {
                            desktopItemInfo.setPackageName(resolveInfo.activityInfo.packageName);
                            // 更新数据库
                            DesktopItemManager.getInstance(mContext).updateItemInfo(desktopItemInfo);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(desktopItemInfo -> {
                            // 更新UI
                            DesktopItemManager.getInstance(mContext).getItem(mTag).update(desktopItemInfo);
                            dismiss();
                        }, throwable -> {
                            LogUtils.error(TAG, "bind or replace failed, error message: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }


}
