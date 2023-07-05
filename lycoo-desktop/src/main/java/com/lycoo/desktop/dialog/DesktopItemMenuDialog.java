package com.lycoo.desktop.dialog;

import android.content.Context;

import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.desktop.R;
import com.lycoo.desktop.bean.DesktopItemInfo;
import com.lycoo.desktop.config.DesktopConstants;
import com.lycoo.desktop.helper.DesktopItemManager;

import org.apache.commons.lang3.StringUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lancy on 2018/5/11
 */
public class DesktopItemMenuDialog extends AppMenuDialog {
    private static final String TAG = DesktopItemMenuDialog.class.getSimpleName();

    private int mTag;

    public DesktopItemMenuDialog(Context context, int themeResId, int tag) {
        super(context, themeResId);
        this.mTag = tag;
    }

    @Override
    protected void initView() {
        super.initView();
        ViewUtils.setViewShown(false, mUninstallMenuItem);

        mCompositeDisposable.add(
                DesktopItemManager
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
                DesktopItemManager
                        .getInstance(mContext)
                        .getItemInfoByTag(mTag)
                        .map(itemInfo -> {
                            DesktopItemInfo desktopItemInfo = new DesktopItemInfo();
                            desktopItemInfo.setTag(mTag);
                            desktopItemInfo.setType(DesktopConstants.REPLACEABLE_APP);
                            desktopItemInfo.setImageUrl(itemInfo.getImageUrl());
                            desktopItemInfo.setUpdateTime(itemInfo.getUpdateTime());
                            desktopItemInfo.setIconVisible(itemInfo.isIconVisible());

                            // 更新数据库
                            DesktopItemManager.getInstance(mContext).updateItemInfo(desktopItemInfo);

                            return desktopItemInfo;
                        })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(itemInfo -> {
                            // 更新UI
                            DesktopItemManager.getInstance(mContext).getItem(mTag).update(itemInfo);
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

        mAppsDialog = new DesktopAppsDialog(mContext, R.style.AppsDialogStyle, 5, mTag);
        mAppsDialog.show();
    }
}
