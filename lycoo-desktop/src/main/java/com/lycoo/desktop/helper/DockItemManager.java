package com.lycoo.desktop.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.Toast;

import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.http.DownloadCallBack;
import com.lycoo.commons.http.DownloadManager;
import com.lycoo.commons.http.DownloadTask;
import com.lycoo.commons.util.ApplicationUtils;

import com.lycoo.commons.util.CommonUtils;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.FileUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.MD5Utils;
import com.lycoo.commons.widget.CustomToast;
import com.lycoo.desktop.R;
import com.lycoo.desktop.bean.DesktopItemInfo;
import com.lycoo.desktop.bean.DockItemInfo;
import com.lycoo.desktop.boosj.BoosjConstants;
import com.lycoo.desktop.config.DesktopConstants;
import com.lycoo.desktop.config.DockConstants;
import com.lycoo.desktop.db.DesktopDbManager;
import com.lycoo.desktop.qipo.QipoConstants;
import com.lycoo.desktop.qipo.QipoManager;
import com.lycoo.desktop.qiyi.QiyiManager;
import com.lycoo.desktop.ui.DesktopItem;
import com.lycoo.desktop.ui.DesktopItemProgressBar;
import com.lycoo.desktop.ui.DockItem;
import com.lycoo.desktop.ui.DockItemProgressBar;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lancy on 2018/5/8
 */
public class DockItemManager {
    private static final String TAG = DockItemManager.class.getSimpleName();

    private Context mContext;
    private static DockItemManager mInstance;
    private SparseArray<DockItem> mDockItems;        // 存储所有坑位
    private CompositeDisposable mCompositeDisposable;
    private static final String DOWNLOAD_PREFIX = "desktopItem_";
    private static final String DOWNLOAD_SUFFIX = ".apk";
    private List<InstallDockItem> mInstallDesktopItems;

    private DockItemManager(Context context) {
        mContext = context;
        mDockItems = new SparseArray<>();
        mInstallDesktopItems = new ArrayList<>();
        mCompositeDisposable = new CompositeDisposable();
    }

    public static DockItemManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DesktopItemManager.class) {
                if (mInstance == null) {
                    mInstance = new DockItemManager(context);
                }
            }
        }
        return mInstance;
    }

    public void registerItem(int tag, DockItem dockItem) {
        mDockItems.put(tag, dockItem);
    }

    public DockItem getItem(int tag) {
        return mDockItems.get(tag);
    }

    public void saveItemInfos(List<DockItemInfo> itemInfos) {
        DesktopDbManager.getInstance(mContext).saveDockItemInfos(itemInfos);
    }

    public void clearItemInfos() {
        DesktopDbManager.getInstance(mContext).removeAllDockItemInfos();
    }

    public void updateItemInfo(DockItemInfo itemInfo) {
        DesktopDbManager.getInstance(mContext).updateDockItemInfo(itemInfo);
    }

    public Observable<List<DockItemInfo>> getItemInfos() {
        return Observable
                .create(e -> {
                    List<DockItemInfo> itemInfos =
                            DesktopDbManager
                                    .getInstance(mContext)
                                    .getDockItemInfos(null, null);
                    e.onNext(itemInfos);
                    e.onComplete();
                });
    }

    public Observable<DockItemInfo> getItemInfoByTag(final int tag) {
        return Observable
                .create(e -> {
                    DockItemInfo itemInfo =
                            DesktopDbManager
                                    .getInstance(mContext)
                                    .getDockItemInfo(
                                            DockConstants.DOCK_ITEM_TABLE.COLUMN_TAG + " = ?",
                                            new String[]{String.valueOf(tag)});
                    if (itemInfo == null) {
                        itemInfo = new DockItemInfo();
                    }
                    e.onNext(itemInfo);
                    e.onComplete();
                });
    }

    public Observable<List<String>> getPackageNames() {
        return Observable
                .create(e -> {
                    List<String> packageNames =
                            DesktopDbManager
                                    .getInstance(mContext)
                                    .getDockItemsColumn(DockConstants.DOCK_ITEM_TABLE.COLUMN_PACKAGENAME);
                    e.onNext(packageNames);
                    e.onComplete();
                });
    }

    /**
     * public String getItemInfoUpdateTime(String tag) {
     * return DesktopDbManager.getInstance(mContext).getDockItemUpdateTime(tag);
     * }
     * public void updateItemInfos(List<DockItemInfo> itemInfos) {
     * DesktopDbManager.getInstance(mContext).updateDockItemInfos(itemInfos);
     * }
     * 查询指定包名的坑位信息
     *
     * @param packageName 要查询的包名
     * @return 指定包名对应的坑位信息
     * <p>
     * Created by lancy on 2018/1/8 12:45
     */
    public Observable<DockItemInfo> getItemInfoByPackageName(final String packageName) {
        return Observable
                .create(e -> {
                    DockItemInfo itemInfo =
                            DesktopDbManager
                                    .getInstance(mContext)
                                    .getDockItemInfo(
                                            DockConstants.DOCK_ITEM_TABLE.COLUMN_PACKAGENAME + " = ?",
                                            new String[]{packageName});
                    if (itemInfo == null) {
                        itemInfo = new DockItemInfo();
                    }
                    e.onNext(itemInfo);
                    e.onComplete();
                });
    }

    @SuppressLint("CheckResult")
    public void openItem(DockItem dockItem) {
        getItemInfoByTag(dockItem.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(dockItemInfo -> {
                    LogUtils.debug(TAG, "desktopItemInfo : " + dockItemInfo);
                    if (dockItemInfo == null) {
                        CustomToast.makeText(mContext, R.string.msg_desktop_item_config_error, CustomToast.MessageType.ERROR).show();
                        return false;
                    }
                    switch (dockItem.getType()) {
                        // 固定页面 and 容器坑位
                        case DesktopConstants.SPECIALIZED_PAGE:
                            if (TextUtils.isEmpty(dockItemInfo.getAction())) {
                                CustomToast.makeText(mContext, R.string.msg_desktop_item_config_error, CustomToast.MessageType.ERROR).show();
                                break;
                            }
                            try {
                                LogUtils.error(TAG,"dockItemInfo =" +dockItemInfo.getParam1());
                                LogUtils.error(TAG,"dockItemInfo =" +dockItemInfo.getParam2());
                                LogUtils.error(TAG,"dockItemInfo =" +dockItemInfo.getParam3());
                                Intent intent = new Intent();
                                intent.setAction(dockItemInfo.getAction());
                                intent.putExtra(DesktopConstants.PARAM1, dockItemInfo.getParam1());
                                intent.putExtra(DesktopConstants.PARAM2, dockItemInfo.getParam2());
                                intent.putExtra(DesktopConstants.PARAM3, dockItemInfo.getParam3());
                                mContext.startActivity(intent);
                            } catch (Exception e) {
                                CustomToast.makeText(mContext, R.string.msg_failed_to_open_desktop_item, CustomToast.MessageType.ERROR).show();
                                e.printStackTrace();
                            }
                            break;
                        // 定制坑位
                        case DesktopConstants.CUSTOM_ITEM:
                            dockItem.doClick();
                            break;

                        // 可替换应用
                        case DesktopConstants.REPLACEABLE_APP:
                            if (TextUtils.isEmpty(dockItemInfo.getPackageName())) {
                                CustomToast.makeText(mContext, R.string.msg_edit_desktop_item, Toast.LENGTH_LONG).show();
                                break;
                            }

                            try {
                                ApplicationUtils.openApplication(mContext, dockItemInfo.getPackageName());
                            } catch (Exception e) {
                                CustomToast.makeText(mContext, R.string.msg_failed_to_open_desktop_item, CustomToast.MessageType.ERROR).show();
                                e.printStackTrace();
                            }
                            break;

                        // 固定应用
                        case DesktopConstants.SPECIALIZED_APP:
                            if (TextUtils.isEmpty(dockItemInfo.getPackageName())) {
                                CustomToast.makeText(mContext, R.string.msg_desktop_item_config_error, CustomToast.MessageType.ERROR).show();
                                break;
                            }

                            try {
                                ApplicationUtils.openApplication(mContext, dockItemInfo.getPackageName());
                            } catch (Exception e) {
                                CustomToast.makeText(mContext, R.string.msg_failed_to_open_desktop_item, CustomToast.MessageType.ERROR).show();
                                e.printStackTrace();
                            }
                            break;

                        // 具体分类，例如现代舞， 民间舞
                        case DesktopConstants.BOOSJ_DANCE_CLASSIFICATION:
                            openDanceItem(dockItemInfo);
                            break;
                        case DesktopConstants.CONFIG_APP:
                            if (TextUtils.isEmpty(dockItemInfo.getPackageName())) {
                                CustomToast.makeText(mContext, R.string.msg_desktop_item_config_error, CustomToast.MessageType.ERROR).show();
                                break;
                            }
                            if (!ApplicationUtils.isAppInstalled(mContext, dockItemInfo.getPackageName())
                                    || dockItemInfo.getAppVersion() > ApplicationUtils.getVersionCode(mContext, dockItemInfo.getPackageName())) {
                                LogUtils.info(TAG, "getAppPackageName is " + dockItemInfo.getPackageName());
                                LogUtils.info(TAG, "getAppVersion is " + dockItemInfo.getAppVersion());
                                LogUtils.info(TAG, "currentAppVersion is " + ApplicationUtils.getVersionCode(mContext, dockItemInfo.getPackageName()));
                                return true;
                            }
                            try {
                                ApplicationUtils.openApplication(mContext, dockItemInfo.getPackageName());
                            } catch (Exception e) {
                                CustomToast.makeText(mContext, R.string.msg_failed_to_open_desktop_item, CustomToast.MessageType.ERROR).show();
                                e.printStackTrace();
                            }
                            break;
                    }
                    return false;
                })
                .subscribe(dockItemInfo -> {

                    LogUtils.debug(TAG, "=== accept(), thread : " + Thread.currentThread().getName());
                    if (dockItemInfo == null) {
                        return;
                    }
                    if (!DeviceUtils.isNetworkConnected(mContext)) {
                        CustomToast
                                .makeText(mContext, R.string.msg_network_unconnected, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                .show();
                        return;
                    }
                    LogUtils.debug(TAG, "status = " + dockItem.getStatus());
                    switch (dockItem.getStatus()) {

                        // 正在准备， 取消检索
                        case PREPAREING:
                            DownloadManager
                                    .getInstance(mContext)
                                    .stopDownload(Environment.getExternalStorageDirectory()
                                            + File.separator
                                            + DOWNLOAD_PREFIX
                                            + dockItem.getId()
                                            + DOWNLOAD_SUFFIX);
                            dockItem.getProgressBar().updateStatus(DockItemProgressBar.Status.CANCELED);
                            return;

                        // 正在下载，取消下载
                        case DOWNLOADING:
                            DownloadManager
                                    .getInstance(mContext)
                                    .stopDownload(Environment.getExternalStorageDirectory()
                                            + File.separator
                                            + DOWNLOAD_PREFIX
                                            + dockItem.getId()
                                            + DOWNLOAD_SUFFIX);
                            dockItem.getProgressBar().updateStatus(DockItemProgressBar.Status.CANCELED);
                            return;

                        // 正在安装, 无法取消安装，善意的提醒一下
                        case INSTALLING:
                            CustomToast
                                    .makeText(mContext, R.string.msg_stop_install, Toast.LENGTH_SHORT, CustomToast.MessageType.WARN)
                                    .show();
                            return;
                        default:
                            break;
                    }
                    dockItem.setStatus(DockItem.Status.PREPAREING);
                    dockItem.showProgressBar();
                    dockItem.getProgressBar().updateStatus(DockItemProgressBar.Status.PREPAREING);
                    if (!StringUtils.isEmpty(dockItemInfo.getAppUrl())) {
                        downloadApp(dockItem, dockItemInfo);
                    } else {
                        QipoManager
                                .getInstance(mContext)
                                .query(dockItemInfo.getPackageName())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(qipoInfo -> {
                                    LogUtils.debug(TAG, "qipoInfo = " + qipoInfo);
                                    if (qipoInfo != null && qipoInfo.getFind() == QipoConstants.APP_FIND) {
                                        dockItemInfo.setAppUrl(qipoInfo.getUrl());
                                        dockItemInfo.setAppMd5(qipoInfo.getMd5());
                                    }
                                    downloadApp(dockItem, dockItemInfo);
                                }, throwable -> {
                                    LogUtils.error(TAG, "error message: " + throwable.getMessage());
                                    throwable.printStackTrace();
                                    resetItemProgressBar(dockItem);
                                    CustomToast
                                            .makeText(mContext, R.string.msg_prepare_desktop_item_info_failed, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                            .show();
                                });
                    }
                }, throwable -> {
                    LogUtils.error(TAG, "failed to open DockItem, error message : " + throwable.getMessage());
                    throwable.printStackTrace();
                });
    }


    /**
     * 下载应用
     *
     * @param dockItem     对应坑位
     * @param dockItemInfo 对应坑位信息
     *                     <p>
     *                     Created by lancy on 2017/12/26 14:17
     */
    private void downloadApp(final DockItem dockItem,
                             final DockItemInfo dockItemInfo) {
        if (dockItemInfo == null
                || StringUtils.isEmpty(dockItemInfo.getAppUrl())
                || StringUtils.isEmpty(dockItemInfo.getAppMd5())) {
            resetItemProgressBar(dockItem);
            CustomToast
                    .makeText(mContext, R.string.msg_prepare_desktop_item_info_failed, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR).
                    show();
            return;
        }

        DownloadManager
                .getInstance(mContext)
                .download(new DownloadTask(
                        dockItemInfo.getAppUrl(),
                        new File(Environment.getExternalStorageDirectory(), DOWNLOAD_PREFIX + dockItem.getId() + DOWNLOAD_SUFFIX),
                        null,
                        null,
                        null,
                        true,
                        new DownloadCallBack<File>() {
                            @Override
                            public void onCheck(int code) {
                                switch (code) {
                                    case DownloadManager.DOWNLOAD_FAILED_REPEAT_TASK:
                                        resetItemProgressBar(dockItem);
                                        CustomToast
                                                .makeText(mContext, R.string.msg_check_download_failed, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                                .show();
                                        break;
                                    case DownloadManager.DOWNLOAD_FAILED_NETWORK_UNCONNECTED:
                                        resetItemProgressBar(dockItem);
                                        CustomToast
                                                .makeText(mContext, R.string.msg_network_unconnected, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                                .show();
                                        break;
                                        /*
                                    case DownloadManager.DOWNLOAD_FAILED_CANCEL_TASK:
                                        resetItemProgressBar(desktopItem);
                                        CustomToast
                                                .makeText(mContext, R.string.msg_cancel_task, Toast.LENGTH_SHORT, CustomToast.MessageType.WARN)
                                                .show();
                                        break;
                                        */
                                }
                            }

                            @Override
                            public void onStart() {
                                // 状态->下载
                                dockItem.setStatus(DockItem.Status.DOWNLOADING);
                                dockItem.getProgressBar().updateStatus(DockItemProgressBar.Status.START_DOWNLOAD);
                            }

                            @Override
                            public void onProgress(int progress) {
                                LogUtils.debug(TAG, "222 onLoading(), thread's name : " + Thread.currentThread().getName()
                                        + ", progress = " + progress + "%");
                                dockItem.getProgressBar().updateProgress(progress);
                            }

                            @Override
                            public void onError(int errorCode, Throwable t) {
                                super.onError(errorCode, t);
                                LogUtils.error(TAG, "download error message : " + t.getMessage());
                                t.printStackTrace();

                                resetItemProgressBar(dockItem);
                                CustomToast
                                        .makeText(mContext, R.string.msg_download_failed, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                        .show();
                            }

                            @Override
                            public void onStop() {
                                super.onStop();
                                LogUtils.error(TAG, "stop download ......");
                                resetItemProgressBar(dockItem);
                                CustomToast
                                        .makeText(mContext, R.string.msg_stop_download, Toast.LENGTH_SHORT, CustomToast.MessageType.WARN)
                                        .show();
                            }

                            @Override
                            public void onSuccess(File file) {
                                super.onSuccess(file);
                                LogUtils.info(TAG, "download success : file = " + file.getPath());
                                // 状态->安装
                                dockItem.setStatus(DockItem.Status.INSTALLING);
                                dockItem.getProgressBar().updateStatus(DockItemProgressBar.Status.DOWNLOAD_COMPLETE);

                                installApp(file, dockItemInfo, dockItem);
                            }
                        }));
    }

    /**
     * 安装应用
     *
     * @param itemInfo 对应坑位信息
     * @param file     安装文件
     *                 <p>
     *                 Created by lancy on 2017/12/26 14:22
     */
    private void installApp(final File file, final DockItemInfo itemInfo,
                            final DockItem dockItem) {
        // 校验
        dockItem.getProgressBar().updateStatus(DockItemProgressBar.Status.VERIFYING);
        mCompositeDisposable.add(
                Observable
                        .create((ObservableOnSubscribe<Boolean>) e -> {
                            boolean result = MD5Utils.checkMd5(itemInfo.getAppMd5(), file);
                            if (!result) {
                                LogUtils.error(TAG, file + " checkMd5 failed......");
                                FileUtils.deleteFile(file);
                            }
                            e.onNext(result);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (!result) {
                                resetItemProgressBar(dockItem);
                                CustomToast
                                        .makeText(mContext, R.string.msg_check_md5_failed, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                        .show();
                                return;
                            }

                            // 安装
                            dockItem.getProgressBar().updateStatus(DockItemProgressBar.Status.INSTALLING);
                            LogUtils.debug(TAG, "start install package, file : " + file + ", packageName : " + itemInfo.getPackageName());

                            // 保存当前坑位对应的信息，以便回调时使用
                            mInstallDesktopItems.add(new InstallDockItem(itemInfo.getPackageName(), file.getPath(), dockItem));

                            // 发送安装应用命令
                            // TODO: 2017/12/30 如果安装完成之后没有成功发送广播会导致界面没法更新，下载文件没法清除
                            ArrayList<String> data = new ArrayList<>();
                            data.add(file.getPath());
                            CommonUtils.sendInstallPackageBroadcast(mContext, data);
                        }));
    }

    @SuppressWarnings("unused")
    public void onPackageInstall(Context context, final Intent intent) {
        LogUtils.verbose(TAG, "InstallPackageReceiver, onReceive()......");
        mCompositeDisposable.add(
                Observable
                        .create((ObservableOnSubscribe<DockItem>) e -> {
                            String packageName = intent.getStringExtra(CommonConstants.PACKAGEINSTALL_PACKAGENAME);
                            String file = intent.getStringExtra(CommonConstants.PACKAGEINSTALL_FILE);
                            LogUtils.debug(TAG, "onPackageInstall(), packageName : " + packageName);
                            LogUtils.debug(TAG, "onPackageInstall(), file : " + file);
                            DockItem desktopItem = null;
                            if (!mInstallDesktopItems.isEmpty()) {
                                synchronized (mInstallDesktopItems) {
                                    for (InstallDockItem item : mInstallDesktopItems) {
                                        if ((!StringUtils.isEmpty(packageName) && packageName.equals(item.getPackageName()))
                                                || (!StringUtils.isEmpty(file) && file.equals(item.getFile()))) {
                                            FileUtils.deleteFile(item.getFile());
                                            desktopItem = item.getDesktopItem();
                                            mInstallDesktopItems.remove(item);
                                            break;
                                        }
                                    }
                                }
                            }
                            e.onNext(desktopItem);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(dockItem -> {
                                    if (dockItem == null) {
                                        return;
                                    }
                                    int resultCode = intent.getIntExtra(CommonConstants.PACKAGEINSTALL_RESULTCODE, -100);
                                    if (resultCode == CommonConstants.INSTALL_SUCCEEDED) {
                                        CustomToast
                                                .makeText(mContext, R.string.msg_install_succeeded, Toast.LENGTH_SHORT, CustomToast.MessageType.INFO)
                                                .show();
                                    } else {
                                        CustomToast
                                                .makeText(mContext, R.string.msg_install_failed, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                                .show();
                                    }
                                    resetItemProgressBar(dockItem);
                                },
                                throwable -> {
                                    LogUtils.debug(TAG, "onPackageInstall, error message : " + throwable.getMessage());
                                    throwable.printStackTrace();
                                }));
    }


    /**
     * 查询坑位更新时间戳
     *
     * @param tag 坑位tag
     * @return 坑位更新时间戳
     * <p>
     * Created by lancy on 2017/12/16 13:10
     */
    public String getItemInfoUpdateTime(String tag) {
        return DesktopDbManager.getInstance(mContext).getDockItemUpdateTime(tag);
    }


    /**
     * 更新坑位信息
     *
     * @param itemInfos 要更新的坑位信息
     *                  <p>
     *                  Created by lancy on 2017/12/16 14:12
     */
    public void updateItemInfos(List<DockItemInfo> itemInfos) {
        DesktopDbManager.getInstance(mContext).updateDockItemInfos(itemInfos);
    }


    /**
     * 封装安装应用的信息
     * <p>
     * Created by lancy on 2017/12/29 10:18
     */
    private class InstallDockItem {
        private String packageName;
        private String file;
        private DockItem desktopItem;

        public InstallDockItem(String packageName, String file, DockItem desktopItem) {
            this.packageName = packageName;
            this.file = file;
            this.desktopItem = desktopItem;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public DockItem getDesktopItem() {
            return desktopItem;
        }

        public void setDockItem(DockItem desktopItem) {
            this.desktopItem = desktopItem;
        }
    }

    /**
     * 重置坑位状态
     *
     * @param dockItem 对应坑位
     *                 <p>
     *                 Created by lancy on 2017/12/29 10:17
     */
    private void resetItemProgressBar(DockItem dockItem) {
        dockItem.setStatus(DockItem.Status.IDLE);
        dockItem.hideProgressBar();
    }

    /**
     * Open Dance Item
     *
     * @param dockItemInfo DesktopItemInfo
     *                     <p>
     *                     Created by lancy on 2018/6/12 14:40
     */
    private void openDanceItem(DockItemInfo dockItemInfo) {
        Intent intent = new Intent();
        switch (dockItemInfo.getType()) {
            case DesktopConstants.BOOSJ_DANCE_EXCHANGE:             // 今日头条
            case DesktopConstants.BOOSJ_DANCE_RECOMMEND:            // 人气视频
            case DesktopConstants.BOOSJ_DANCE_EXCLUSIVE:            // 独家精品
            case DesktopConstants.BOOSJ_DANCE_SQUARE:               // 舞友广场
            case DesktopConstants.BOOSJ_DANCE_HEALTH:               // 养生健康
            case DesktopConstants.BOOSJ_DANCE_CLASSIFICATION:       // 具体分类
                intent.setAction(BoosjConstants.ACTION_BROWSE_BOOSJ_DANCE_VIDEOS);
                if (dockItemInfo.getType() == DesktopConstants.BOOSJ_DANCE_CLASSIFICATION) {
                    intent.putExtra(BoosjConstants.CONDITION, dockItemInfo.getParam1());
                    intent.putExtra(BoosjConstants.CONDITION_ARGS, dockItemInfo.getParam2());
                }
                break;

            case DesktopConstants.BOOSJ_DANCE_GOLD_TEACHER:         // 金牌导师
            case DesktopConstants.BOOSJ_DANCE_DAREN:                // 知名舞队
                intent.setAction(BoosjConstants.ACTION_BROWSE_BOOSJ_DANCE_USERS);
                break;

            case DesktopConstants.BOOSJ_DANCE_ACTIVITY:             // 广场舞活动
                intent.setAction(BoosjConstants.ACTION_BROWSE_BOOSJ_DANCE_ACTIVITIES);
                break;
        }
        intent.putExtra(BoosjConstants.TYPE, dockItemInfo.getType());
        intent.putExtra(BoosjConstants.TITLE, dockItemInfo.getLabel());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * 系统中有应用状态发生改变时回调
     * 目前桌面的设计只针对可替换应用
     *
     * @param intent BroadcastReceiver回传的Intent
     *               <p>
     *               Created by lancy on 2018/6/27 23:05
     */
    public void onPackageChanged(Intent intent) {
        String action = intent.getAction();
        LogUtils.debug(TAG, "action : " + action);
        if (!StringUtils.isEmpty(action)) {
            String data = intent.getDataString();
            if (!StringUtils.isEmpty(data)) {
                String packageName = data.substring(8);
                // 卸载
                if (action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
                    onAppRemoved(packageName);
                }
                // 升级（也可以不做处理，纯粹为了用户体验（因为有的应用升级之后label和icon可能发生变化，让用户第一时间看到））
                else if (action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
                    onAppReplaced(packageName);
                } else if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                    onAppUpdateData(packageName);
                }

            }
        }
    }

    /**
     * 应用替换时调用
     * 也可以不做处理，纯粹为了用户体验（因为有的应用升级之后label和icon可能发生变化，让用户第一时间看到）
     *
     * @param packageName 替换应用的包名
     *                    <p>
     *                    Created by lancy on 2018/1/8 18:04
     */
    private void onAppReplaced(final String packageName) {
        LogUtils.debug(TAG, "onAppReplaced(), packageName : " + packageName);
        mCompositeDisposable.add(
                getItemInfoByPackageName(packageName)
                        .subscribeOn(Schedulers.io())
                        .filter(itemInfo -> {
                            // 只处理可替换应用
                            return itemInfo != null
                                    && itemInfo.isPersistent()
                                    && itemInfo.getType() == DesktopConstants.REPLACEABLE_APP;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(itemInfo -> {
                            LogUtils.info(TAG, "app updated, itemInfo : " + itemInfo);
//                            Log.v("liu","app updated, itemInfo : " + itemInfo);
                            getItem(itemInfo.getTag()).update(itemInfo);
                        }, throwable -> {
                            LogUtils.error(TAG, "onAppReplaced(), error message: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    private void onAppUpdateData(final String packageName) {
        LogUtils.debug(TAG, "onAppUpdateData(), packageName : " + packageName);
        mCompositeDisposable.add(
                getItemInfoByPackageName(packageName)
                        .subscribeOn(Schedulers.io())
                        .filter(itemInfo -> {
                            return itemInfo != null
                                    && itemInfo.isPersistent();
                        }).doOnNext(itemInfo -> {
                    updateItemInfo(itemInfo);
                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(itemInfo -> {
                            LogUtils.info(TAG, "app updated, itemInfo : " + itemInfo);
                            getItem(itemInfo.getTag()).update(itemInfo);
                        }, throwable -> {
                            LogUtils.error(TAG, "onAppReplaced(), error message: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    private void onAppRemoved(String packageName) {
        LogUtils.debug(TAG, "onAppRemoved(), packageName : " + packageName);
        // 处理DesktopItem
        mCompositeDisposable.add(
                getItemInfoByPackageName(packageName)
                        .subscribeOn(Schedulers.io())
                        .filter(itemInfo ->
                                itemInfo != null
                                        && itemInfo.isPersistent()
                                        && itemInfo.getType() == DesktopConstants.REPLACEABLE_APP) // 只处理可替换应用
                        .doOnNext(itemInfo -> {
                            itemInfo.setPackageName(null);
                            // 更新数据库
                            updateItemInfo(itemInfo);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(itemInfo -> {
                            // 更新UI
                            LogUtils.info(TAG, "app removed, update DockItem..., itemInfo : " + itemInfo);
                            getItem(itemInfo.getTag()).update(itemInfo);
                        }, throwable -> {
                            LogUtils.error(TAG, "onAppRemoved(), error message: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    public void onDestroy() {
        if (mCompositeDisposable != null && mCompositeDisposable.size() > 0) {
            mCompositeDisposable.clear();
        }
    }

}
