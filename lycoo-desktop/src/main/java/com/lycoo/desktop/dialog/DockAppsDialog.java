package com.lycoo.desktop.dialog;

import android.content.Context;
import android.content.pm.ResolveInfo;

import com.lycoo.commons.util.LogUtils;
import com.lycoo.desktop.helper.DockItemManager;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lancy on 2018/5/11
 */
public class DockAppsDialog extends AppsDialog {
    private static final String TAG = DockAppsDialog.class.getSimpleName();

    private int mTag;

    public DockAppsDialog(Context context, int themeResId, int numColumns, int tag) {
        super(context, themeResId, numColumns);
        this.mTag = tag;
    }

    @Override
    Observable<List<String>> getPackageNames() {
        return DockItemManager.getInstance(mContext).getPackageNames();
    }

    @Override
    void doUpdate(ResolveInfo resolveInfo) {
        mCompositeDisposable.add(
                DockItemManager
                        .getInstance(mContext)
                        .getItemInfoByTag(mTag)
                        .doOnNext(dockItemInfo -> {
                            dockItemInfo.setPackageName(resolveInfo.activityInfo.packageName);
                            // 更新数据库
                            DockItemManager.getInstance(mContext).updateItemInfo(dockItemInfo);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(dockItemInfo -> {
                            // 更新UI
                            DockItemManager.getInstance(mContext).getItem(mTag).update(dockItemInfo);
                            dismiss();
                        }, throwable -> {
                            LogUtils.error(TAG, "bind or replace failed, error message: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));

    }


}
