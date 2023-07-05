package com.lycoo.desktop.dialog;

import android.content.Context;

import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.desktop.R;
import com.lycoo.desktop.bean.DockItemInfo;
import com.lycoo.desktop.config.DesktopConstants;
import com.lycoo.desktop.helper.DockItemManager;

import org.apache.commons.lang3.StringUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lancy on 2018/5/11
 */
public class DockItemMenuDialog extends AppMenuDialog {
    private static final String TAG = DockItemMenuDialog.class.getSimpleName();

    private int mTag;

    public DockItemMenuDialog(Context context, int themeResId, int tag) {
        super(context, themeResId);
        this.mTag = tag;
    }

    @Override
    protected void initView() {
        super.initView();
        ViewUtils.setViewShown(false, mUninstallMenuItem);

        mCompositeDisposable.add(
                DockItemManager
                        .getInstance(mContext)
                        .getItemInfoByTag(mTag)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(desktopItemInfo -> {
                            if (StringUtils.isEmpty(desktopItemInfo.getPackageName())) {
                                mBindMenuItem.setFocusable(true);
                                mReplaceMenuItem.setFocusable(false);
                                mUnbindMenuItem.setFocusable(false);
                            } else {
                                mBindMenuItem.setFocusable(false);
                                mReplaceMenuItem.setFocusable(true);
                                mUnbindMenuItem.setFocusable(true);
                            }
                        }, throwable -> {
                            LogUtils.error(TAG, "init view failed, error message: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }


    @Override
    protected void unbind() {
        mCompositeDisposable.add(
                DockItemManager
                        .getInstance(mContext)
                        .getItemInfoByTag(mTag)
                        .map(itemInfo -> {
                            DockItemInfo dockItemInfo = new DockItemInfo();
                            dockItemInfo.setTag(mTag);
                            dockItemInfo.setType(DesktopConstants.REPLACEABLE_APP);
                            dockItemInfo.setImageUrl(itemInfo.getImageUrl());

                            // 更新数据库
                            DockItemManager.getInstance(mContext).updateItemInfo(dockItemInfo);
                            return dockItemInfo;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(itemInfo -> {
                            // 更新UI
                            DockItemManager.getInstance(mContext).getItem(mTag).update(itemInfo);
                            dismiss();
                        }, throwable -> {
                            LogUtils.error(TAG, "unbind failed, error message: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    protected void showAppsDialog() {
        if (mAppsDialog != null && mAppsDialog.isShowing()) {
            return;
        }

        mAppsDialog = new DockAppsDialog(mContext, R.style.AppsDialogStyle, 5, mTag);
        mAppsDialog.show();
    }
}
