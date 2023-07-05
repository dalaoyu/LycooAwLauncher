package com.lycoo.desktop.helper;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.Toast;

import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.helper.RxBus;
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
import com.lycoo.desktop.bean.DesktopContainerItemInfo;
import com.lycoo.desktop.bean.DesktopItemInfo;
import com.lycoo.desktop.boosj.BoosjConstants;
import com.lycoo.desktop.config.DesktopConstants;
import com.lycoo.desktop.db.DesktopDbManager;
import com.lycoo.desktop.qipo.QipoConstants;
import com.lycoo.desktop.qipo.QipoManager;
import com.lycoo.desktop.qiyi.QiyiManager;
import com.lycoo.desktop.ui.DesktopItem;
import com.lycoo.desktop.ui.DesktopItemProgressBar;

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
 * 桌面管理器
 * <p>
 * Created by lancy on 2017/12/14
 */
public class DesktopItemManager {
    private static final String TAG = DesktopItemManager.class.getSimpleName();

    private static final String DOWNLOAD_PREFIX = "desktopItem_";
    private static final String DOWNLOAD_SUFFIX = ".apk";

    private Context mContext;
    private SparseArray<String> mContainerActions;
    private SparseArray<DesktopItem> mDesktopItems;        // 存储所有坑位
    private List<InstallDesktopItem> mInstallDesktopItems;
    private CompositeDisposable mCompositeDisposable;

    @SuppressLint("StaticFieldLeak")
    private static DesktopItemManager mInstance;

    private DesktopItemManager(Context context) {
        mContext = context;
        mDesktopItems = new SparseArray<>();
        mInstallDesktopItems = new ArrayList<>();
        mCompositeDisposable = new CompositeDisposable();

        String[] array = context.getResources().getStringArray(R.array.container_actions);
        if (array.length > 0) {
            mContainerActions = new SparseArray<>();
            for (String res : array) {
                String[] splits = res.split("__");
                mContainerActions.put(Integer.parseInt(splits[0]), splits[1]);
            }
        }
    }

    public static DesktopItemManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DesktopItemManager.class) {
                if (mInstance == null) {
                    mInstance = new DesktopItemManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 注册坑位， 方便统一管理
     *
     * @param tag         坑位标识
     * @param desktopItem 坑位
     *                    <p>
     *                    Created by lancy on 2017/12/30 15:58
     */
    public void registerItem(int tag, DesktopItem desktopItem) {
        mDesktopItems.put(tag, desktopItem);
    }

    /**
     * 获取坑位
     *
     * @param tag 坑位标识
     *            <p>
     *            Created by lancy on 2017/12/30 15:58
     */
    public DesktopItem getItem(int tag) {
        return mDesktopItems.get(tag);
    }

    /**
     * 保存桌面坑位信息
     *
     * @param itemInfos 要保存的坑位信息
     *                  <p>
     *                  Created by lancy on 2017/12/14 18:07
     */
    public void saveItemInfos(List<DesktopItemInfo> itemInfos) {
        DesktopDbManager.getInstance(mContext).saveDesktopItemInfos(itemInfos);
    }

    /**
     * 删除所有坑位信息
     * <p>
     * Created by lancy on 2017/12/14 18:08
     */
    public void clearItemInfos() {
        DesktopDbManager.getInstance(mContext).removeAllDesktopItemInfos();
    }

    /**
     * 查询桌面坑位信息
     *
     * @param startTag 开始tag
     * @param endTag   结束tag
     * @return 返回tag值介于startTag（包含）和endTag（包含）的坑位信息
     * <p>
     * Created by lancy on 2017/12/14 19:28
     */
    public Observable<List<DesktopItemInfo>> getItemInfos(final int startTag, final int endTag) {
        return Observable
                .create(e -> {
                    List<DesktopItemInfo> desktopItemInfos =
                            DesktopDbManager
                                    .getInstance(mContext)
                                    .getDesktopItemInfos(
                                            DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TAG + " BETWEEN ? AND ?",
                                            new String[]{String.valueOf(startTag), String.valueOf(endTag)});
                    e.onNext(desktopItemInfos);
                    e.onComplete();
                });
    }

    /**
     * 查询桌面坑位包名
     *
     * @return 坑位包名
     * <p>
     * Created by lancy on 2018/1/6 12:37
     */
    public Observable<List<String>> getPackageNames() {
        return Observable
                .create(e -> {
                    List<String> packageNames =
                            DesktopDbManager
                                    .getInstance(mContext)
                                    .getDesktopItemsColumn(DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PACKAGENAME);
                    e.onNext(packageNames);
                    e.onComplete();
                });
    }

    /**
     * 查询桌面坑位信息
     *
     * @param tag 坑位Tag
     * @return 对应坑位的信息
     * <p>
     * Created by lancy on 2017/12/15 11:13
     */
    public Observable<DesktopItemInfo> getItemInfoByTag(final int tag) {
        return Observable
                .create(e -> {
                    DesktopItemInfo itemInfo =
                            DesktopDbManager
                                    .getInstance(mContext)
                                    .getDesktopItemInfo(
                                            DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TAG + " = ?",
                                            new String[]{String.valueOf(tag)});
                    e.onNext(itemInfo);
                    e.onComplete();
                });
    }

    /**
     * 查询桌面坑位信息
     *
     * @param type 坑位类型
     * @return 对应类的坑位
     * <p>
     * Created by lancy on 2017/12/30 15:43
     */
    public Observable<List<DesktopItemInfo>> getItemInfosByType(final int type) {
        return Observable
                .create(e -> {
                    List<DesktopItemInfo> itemInfos =
                            DesktopDbManager
                                    .getInstance(mContext)
                                    .getDesktopItemInfos(
                                            DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_TYPE + " = ?",
                                            new String[]{String.valueOf(type)});
                    e.onNext(itemInfos);
                    e.onComplete();
                });
    }

    /**
     * 查询指定包名的坑位信息
     *
     * @param packageName 要查询的包名
     * @return 指定包名对应的坑位信息
     * <p>
     * Created by lancy on 2018/1/8 12:45
     */
    public Observable<DesktopItemInfo> getItemInfoByPackageName(final String packageName) {
        return Observable
                .create(e -> {
                    DesktopItemInfo itemInfo =
                            DesktopDbManager
                                    .getInstance(mContext)
                                    .getDesktopItemInfo(
                                            DesktopConstants.DESKTOP_ITEM_TABLE.COLUMN_PACKAGENAME + " = ?",
                                            new String[]{packageName});
                    if (itemInfo == null) {
                        itemInfo = new DesktopItemInfo();
                    }
                    e.onNext(itemInfo);
                    e.onComplete();
                });
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
        return DesktopDbManager.getInstance(mContext).getDesktopItemUpdateTime(tag);
    }


    /**
     * 更新坑位信息
     *
     * @param itemInfos 要更新的坑位信息
     *                  <p>
     *                  Created by lancy on 2017/12/16 14:12
     */
    public void updateItemInfos(List<DesktopItemInfo> itemInfos) {
        DesktopDbManager.getInstance(mContext).updateDesktopItemInfos(itemInfos);
    }

    /**
     * 更新坑位信息
     *
     * @param itemInfo 坑位信息
     *                 <p>
     *                 Created by lancy on 2018/1/6 15:27
     */
    public void updateItemInfo(DesktopItemInfo itemInfo) {
        DesktopDbManager.getInstance(mContext).updateDesktopItemInfo(itemInfo);
    }

    /**
     * 保存桌面容器子坑位信息
     *
     * @param itemInfos 容器子坑位信息
     *                  <p>
     *                  Created by lancy on 2018/6/18 22:31
     */
    public void saveContainerItemInfos(List<DesktopContainerItemInfo> itemInfos) {
        DesktopDbManager.getInstance(mContext).saveDesktopContainerItemInfos(itemInfos);
    }

    /**
     * 保存桌面容器子坑位信息
     *
     * @param containerItemInfo 容器子坑位信息
     *                          <p>
     *                          Created by lancy on 2018/6/20 16:40
     */
    public void saveContainerItemInfo(DesktopContainerItemInfo containerItemInfo) {
        DesktopDbManager.getInstance(mContext).saveDesktopContainerItemInfo(containerItemInfo);
    }

    /**
     * 删除容器子坑位信息
     *
     * @param containerType 坑位类型
     * @param packageName   坑位子坑位应用包名
     * @return 影响的行数
     * <p>
     * Created by lancy on 2018/6/20 16:41
     */
    public int removeContainerItemInfos(int containerType, String packageName) {
        return DesktopDbManager
                .getInstance(mContext)
                .removeContainerItemInfos(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_CONTAINER_TYPE + " = ?"
                                + " AND "
                                + DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_PACKAGENAME + " = ?",
                        new String[]{String.valueOf(containerType), packageName});
    }

    /**
     * 删除容器子坑位信息
     *
     * @param packageName 坑位子坑位应用包名
     * @return 影响的行数
     * <p>
     * Created by lancy on 2018/6/20 16:41
     */
    public int removeContainerItemInfos(String packageName) {
        return DesktopDbManager
                .getInstance(mContext)
                .removeContainerItemInfos(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_PACKAGENAME + " = ?",
                        new String[]{packageName});
    }

    /**
     * 获取具体容器下的应用包名列表
     *
     * @param containerType 容器类型
     * @return 具体容器下的应用包名列表
     * <p>
     * Created by lancy on 2018/6/20 16:44
     */
    public Observable<List<String>> getContainerItemPackageNames(int containerType) {
        return Observable
                .create(e -> {
                    List<String> packageNames =
                            DesktopDbManager
                                    .getInstance(mContext)
                                    .getContainerItemPackageNames(
                                            DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_CONTAINER_TYPE + " = ?",
                                            new String[]{String.valueOf(containerType)});
                    e.onNext(packageNames);
                    e.onComplete();
                });
    }

    /**
     * 获取容器子坑位
     *
     * @param containerType 容器类型
     * @param packageName   容器子坑位包名
     * @return 具体容器下的对应包名的坑位信息
     * <p>
     * Created by lancy on 2018/6/20 16:45
     */
    public Observable<DesktopContainerItemInfo> getContainerItemInfo(int containerType, String packageName) {
        return Observable
                .create(emitter -> {
                    DesktopContainerItemInfo containerItemInfo =
                            DesktopDbManager
                                    .getInstance(mContext)
                                    .getContainerItemInfo(DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_CONTAINER_TYPE + " = ?"
                                                    + " AND "
                                                    + DesktopConstants.CONTAINER_ITEM_TABLE.COLUMN_PACKAGENAME + " = ?",
                                            new String[]{String.valueOf(containerType), packageName});
                    if (containerItemInfo == null) {
                        emitter.onNext(new DesktopContainerItemInfo());
                    } else {
                        emitter.onNext(containerItemInfo);
                    }
                });
    }


    /**
     * 打开坑位
     *
     * @param desktopItem 对应的坑位
     *                    <p>
     *                    Created by lancy on 2017/12/26 18:14
     */
    @SuppressLint("CheckResult")
    public void openItem(final DesktopItem desktopItem) {
        getItemInfoByTag(desktopItem.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(desktopItemInfo -> {
                    LogUtils.debug(TAG, "desktopItemInfo : " + desktopItemInfo);
                    if (desktopItemInfo != null) {
                        switch (desktopItemInfo.getType()) {
                            // 可替换应用
                            case DesktopConstants.REPLACEABLE_APP:
                                if (TextUtils.isEmpty(desktopItemInfo.getPackageName())) {
                                    CustomToast.makeText(mContext, R.string.msg_edit_desktop_item, Toast.LENGTH_LONG).show();
                                    break;
                                }

                                try {
                                    ApplicationUtils.openApplication(mContext, desktopItemInfo.getPackageName());
                                } catch (Exception e) {
                                    CustomToast.makeText(mContext, R.string.msg_failed_to_open_desktop_item, CustomToast.MessageType.ERROR).show();
                                    e.printStackTrace();
                                }
                                break;

                            // 固定应用
                            case DesktopConstants.SPECIALIZED_APP:
                                if (TextUtils.isEmpty(desktopItemInfo.getPackageName())) {
                                    CustomToast.makeText(mContext, R.string.msg_desktop_item_config_error, CustomToast.MessageType.ERROR).show();
                                    break;
                                }

                                try {
                                    ApplicationUtils.openApplication(mContext, desktopItemInfo.getPackageName());
                                } catch (Exception e) {
                                    CustomToast.makeText(mContext, R.string.msg_failed_to_open_desktop_item, CustomToast.MessageType.ERROR).show();
                                    e.printStackTrace();
                                }
                                break;

                            // 网络站点
                            case DesktopConstants.WEBSITE:
                                if (TextUtils.isEmpty(desktopItemInfo.getWebsiteUrl())) {
                                    CustomToast.makeText(mContext, R.string.msg_desktop_item_config_error, CustomToast.MessageType.ERROR).show();
                                    break;
                                }

                                try {
                                    ApplicationUtils.openApplication(mContext, Uri.parse(desktopItemInfo.getWebsiteUrl()));
                                } catch (Exception e) {
                                    CustomToast.makeText(mContext, R.string.msg_failed_to_open_desktop_item, CustomToast.MessageType.ERROR).show();
                                    e.printStackTrace();
                                }
                                break;

                            // 固定页面
                            case DesktopConstants.SPECIALIZED_PAGE:
                                if (TextUtils.isEmpty(desktopItemInfo.getAction())) {
                                    CustomToast.makeText(mContext, R.string.msg_desktop_item_config_error, CustomToast.MessageType.ERROR).show();
                                    break;
                                }

                                try {
                                    Intent intent = new Intent();
                                    intent.setAction(desktopItemInfo.getAction());
                                    intent.putExtra(DesktopConstants.PARAM1, desktopItemInfo.getParam1());
                                    intent.putExtra(DesktopConstants.PARAM2, desktopItemInfo.getParam2());
                                    intent.putExtra(DesktopConstants.PARAM3, desktopItemInfo.getParam3());
                                    mContext.startActivity(intent);
                                } catch (Exception e) {
                                    CustomToast.makeText(mContext, R.string.msg_failed_to_open_desktop_item, CustomToast.MessageType.ERROR).show();
                                    e.printStackTrace();
                                }
                                break;

                            // 定制坑位
                            case DesktopConstants.CUSTOM_ITEM:
                                desktopItem.doClick();
                                break;

                            // 容器坑位
                            case DesktopConstants.TV_CONTAINER:
                            case DesktopConstants.AOD_CONTAINER:
                            case DesktopConstants.MUSIC_CONTAINER:
                            case DesktopConstants.GAME_CONTAINER:
                            case DesktopConstants.EDUCATION_CONTAINER:
                            case DesktopConstants.APP_CONTAINER:
                            case DesktopConstants.SETUP_CONTAINER:
                            case DesktopConstants.TOOLS_CONTAINER:
                            case DesktopConstants.EXTRUDE_RECOMMENDATION_CONTAINER:
                            case DesktopConstants.COMMON_RECOMMENDATION_CONTAINER:
                                try {
                                    Intent intent = new Intent();
                                    intent.setAction(DesktopConstants.ACTION_BROWSE_CONTAINER_ITEMS);
                                    intent.putExtra(DesktopConstants.TYPE, desktopItemInfo.getType());
//                                    intent.putExtra(DesktopConstants.PARAM1, desktopItemInfo.getParam1());
//                                    intent.putExtra(DesktopConstants.PARAM2, desktopItemInfo.getParam2());
//                                    intent.putExtra(DesktopConstants.PARAM3, desktopItemInfo.getParam3());
                                    mContext.startActivity(intent);
                                } catch (Exception e) {
                                    CustomToast.makeText(mContext, R.string.msg_failed_to_open_desktop_item, CustomToast.MessageType.ERROR).show();
                                    e.printStackTrace();
                                }
                                break;

                            // 爱奇艺类型
                            case DesktopConstants.QIYI_EXTRUDE_RECOMMENDATION:
                            case DesktopConstants.QIYI_COMMON_RECOMMENDATION:
                            case DesktopConstants.QIYI_CHANNEL:
                            case DesktopConstants.QIYI_SPECIALIZED_PAGE:
                                QiyiManager.getInstance(mContext).openItem(desktopItemInfo);
                                break;

                            // 播视广场舞类型
                            case DesktopConstants.BOOSJ_DANCE_EXCHANGE:
                            case DesktopConstants.BOOSJ_DANCE_RECOMMEND:
                            case DesktopConstants.BOOSJ_DANCE_EXCLUSIVE:
                            case DesktopConstants.BOOSJ_DANCE_ACTIVITY:
                            case DesktopConstants.BOOSJ_DANCE_SQUARE:
                            case DesktopConstants.BOOSJ_DANCE_HEALTH:
                            case DesktopConstants.BOOSJ_DANCE_GOLD_TEACHER:
                            case DesktopConstants.BOOSJ_DANCE_DAREN:
                            case DesktopConstants.BOOSJ_DANCE_CLASSIFICATION:
                                openDanceItem(desktopItemInfo);
                                break;

                            // KTV类型
                            case DesktopConstants.IKTV_ITEM_SINGERS:             // 歌手点歌
                            case DesktopConstants.IKTV_ITEM_HOT_NEW_SONGS:       // 新歌热歌
                            case DesktopConstants.IKTV_ITEM_LOCAL_SONGS:         // 本地歌曲
                            case DesktopConstants.IKTV_ITEM_SONGS:               // 歌名点歌
                            case DesktopConstants.IKTV_ITEM_FAVORITE_SONGS:      // 收藏歌曲
                            case DesktopConstants.IKTV_ITEM_LANGUAGE:            // 语种点歌
                            case DesktopConstants.IKTV_ITEM_TOPIC:               // 主题点歌
                            case DesktopConstants.IKTV_ITEM_VARIETY:             // 综艺点歌
                            case DesktopConstants.IKTV_ITEM_TIKTOK_SONGS:        // 抖音神曲
                            case DesktopConstants.IKTV_ITEM_RADITIONAL_OPERA:    // 戏曲
                                openIktvItem(desktopItemInfo);
                                break;
                            case DesktopConstants.JSYX_RADITIONAL_OPERA:    // 江苏有线戏曲
                                openOpera();
                                break;
                            case DesktopConstants.IKTV_ITEM_BL:         //百灵
                                openBLKtv();
                                break;
                            case DesktopConstants.HEALTH_SCYD:          //身材有道
                                openSCYDHealth();
                                break;
                            // 可配置应用
                            case DesktopConstants.CONFIG_APP:
                                if (TextUtils.isEmpty(desktopItemInfo.getPackageName())) {
                                    CustomToast.makeText(mContext, R.string.msg_desktop_item_config_error, CustomToast.MessageType.ERROR).show();
                                    break;
                                }

                                if (!desktopItemInfo.getPackageName().contains("com.android.settings")) {
                                    // 如果没有安装应用则下载安装
                                    if (!ApplicationUtils.isAppInstalled(mContext, desktopItemInfo.getPackageName())
                                            || desktopItemInfo.getAppVersion() > ApplicationUtils.getVersionCode(mContext, desktopItemInfo.getPackageName())) {
                                        LogUtils.info(TAG, "getAppPackageName is " + desktopItemInfo.getPackageName());
                                        LogUtils.info(TAG, "getAppVersion is " + desktopItemInfo.getAppVersion());
                                        LogUtils.info(TAG, "currentAppVersion is " + ApplicationUtils.getVersionCode(mContext, desktopItemInfo.getPackageName()));
                                        return true;
                                    }
                                }
                                try {
                                    ApplicationUtils.openApplication(mContext, desktopItemInfo.getPackageName());
                                } catch (Exception e) {
                                    CustomToast.makeText(mContext, R.string.msg_failed_to_open_desktop_item, CustomToast.MessageType.ERROR).show();
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                    return false;
                })
                .subscribe(desktopItemInfo -> {
                    LogUtils.debug(TAG, "=== accept(), thread : " + Thread.currentThread().getName());
                    if (desktopItemInfo == null) {
                        return;
                    }

                    if (!DeviceUtils.isNetworkConnected(mContext)) {
                        CustomToast
                                .makeText(mContext, R.string.msg_network_unconnected, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                .show();
                        return;
                    }

                    LogUtils.debug(TAG, "status = " + desktopItem.getStatus());
                    switch (desktopItem.getStatus()) {
                        // 正在准备， 取消检索
                        case PREPAREING:
                            DownloadManager
                                    .getInstance(mContext)
                                    .stopDownload(Environment.getExternalStorageDirectory()
                                            + File.separator
                                            + DOWNLOAD_PREFIX
                                            + desktopItem.getId()
                                            + DOWNLOAD_SUFFIX);
                            desktopItem.getProgressBar().updateStatus(DesktopItemProgressBar.Status.CANCELED);
                            return;

                        // 正在下载，取消下载
                        case DOWNLOADING:
                            DownloadManager
                                    .getInstance(mContext)
                                    .stopDownload(Environment.getExternalStorageDirectory()
                                            + File.separator
                                            + DOWNLOAD_PREFIX
                                            + desktopItem.getId()
                                            + DOWNLOAD_SUFFIX);
                            desktopItem.getProgressBar().updateStatus(DesktopItemProgressBar.Status.CANCELED);
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

                    // 状态->准备
                    desktopItem.setStatus(DesktopItem.Status.PREPAREING);
                    desktopItem.showProgressBar();
                    desktopItem.getProgressBar().updateStatus(DesktopItemProgressBar.Status.PREPAREING);
                    if (!StringUtils.isEmpty(desktopItemInfo.getAppUrl())) {
                        downloadApp(desktopItem, desktopItemInfo);
                    } else {
                        QipoManager
                                .getInstance(mContext)
                                .query(desktopItemInfo.getPackageName())
                                .subscribeOn(Schedulers.io())
//                                    .delay(5, TimeUnit.SECONDS) // for test prepareing
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(qipoInfo -> {
                                    LogUtils.debug(TAG, "qipoInfo = " + qipoInfo);
                                    if (qipoInfo != null && qipoInfo.getFind() == QipoConstants.APP_FIND) {
                                        desktopItemInfo.setAppUrl(qipoInfo.getUrl());
                                        desktopItemInfo.setAppMd5(qipoInfo.getMd5());
                                    }
                                    downloadApp(desktopItem, desktopItemInfo);
                                }, throwable -> {
                                    LogUtils.error(TAG, "error message: " + throwable.getMessage());
                                    throwable.printStackTrace();

                                    resetItemProgressBar(desktopItem);
                                    CustomToast
                                            .makeText(mContext, R.string.msg_prepare_desktop_item_info_failed, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                            .show();
                                });
                    }
                }, throwable -> {
                    LogUtils.error(TAG, "error message: " + throwable.getMessage());
                    throwable.printStackTrace();
                });
    }


    /**
     * Open Dance Item
     *
     * @param desktopItemInfo DesktopItemInfo
     *                        <p>
     *                        Created by lancy on 2018/6/12 14:40
     */
    private void openDanceItem(DesktopItemInfo desktopItemInfo) {
        Intent intent = new Intent();
        switch (desktopItemInfo.getType()) {
            case DesktopConstants.BOOSJ_DANCE_EXCHANGE:             // 今日头条
            case DesktopConstants.BOOSJ_DANCE_RECOMMEND:            // 人气视频
            case DesktopConstants.BOOSJ_DANCE_EXCLUSIVE:            // 独家精品
            case DesktopConstants.BOOSJ_DANCE_SQUARE:               // 舞友广场
            case DesktopConstants.BOOSJ_DANCE_HEALTH:               // 养生健康
            case DesktopConstants.BOOSJ_DANCE_CLASSIFICATION:       // 具体分类
                intent.setAction(BoosjConstants.ACTION_BROWSE_BOOSJ_DANCE_VIDEOS);
                if (desktopItemInfo.getType() == DesktopConstants.BOOSJ_DANCE_CLASSIFICATION) {
                    intent.putExtra(BoosjConstants.CONDITION, desktopItemInfo.getParam1());
                    intent.putExtra(BoosjConstants.CONDITION_ARGS, desktopItemInfo.getParam2());
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
        intent.putExtra(BoosjConstants.TYPE, desktopItemInfo.getType());
        intent.putExtra(BoosjConstants.TITLE, desktopItemInfo.getLabel());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * Open Iktv Item
     *
     * @param desktopItemInfo DesktopItemInfo
     *                        <p>
     *                        Created by keily on 2022/2/28 18:40
     */
    private void openIktvItem(DesktopItemInfo desktopItemInfo) {
        Intent intent = new Intent(DesktopConstants.ACTION_LAUNCH_KTV);
        intent.putExtra(DesktopConstants.KEY_ITEM_TYPE, desktopItemInfo.getType() - DesktopConstants.IKTV_ITEM);
        int i = desktopItemInfo.getType() - DesktopConstants.IKTV_ITEM;
        mContext.startActivity(intent);
    }

    /**
     * Open 百灵 item
     * Create by HonceH on 23/4/1 16:20
     */
    private void openBLKtv() {
//        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(DesktopConstants.ACTION_BL_KTV);
//        intent.setAction("android.intent.action.VIEW");
//        intent.putExtra(DesktopConstants.KEY_BL_CODE,"mine");
//        intent.putExtra(DesktopConstants.KEY_BL_TYPE,"mainPage");

//        LogUtils.info("BL1",intent.getDataString());
//        LogUtils.info("BL2",intent.getData().toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("lutong://" + DesktopConstants.PACK_BL_KTV + "/subpage?" +
                DesktopConstants.KEY_BL_CODE + "=" + DesktopConstants.BL_CODE + "&" +
                DesktopConstants.KEY_BL_TYPE + "=" + DesktopConstants.BL_TYPE);
        intent.setData(uri);
        mContext.startActivity(intent);
    }

    /**
     * Create by HonceH on 23/4/6
     */
    private void openOpera(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("lutong://" + DesktopConstants.PACK_BL_KTV + "/subpage?" +
                DesktopConstants.KEY_BL_CODE + "=" + DesktopConstants.Opera_CODE + "&" +
                DesktopConstants.KEY_BL_TYPE + "=" + DesktopConstants.Opera_TYPE);
        intent.setData(uri);
        mContext.startActivity(intent);
    }
    /**
     * Open 身材有道 item
     * Create by HonceH on 23/4/1 16:20
     */
    public void openSCYDHealth() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("lutong://" + DesktopConstants.PACK_SCYD_JS + "/subpage?" +
                DesktopConstants.KEY_SCYD_SOURCE + "=" + DesktopConstants.SCYD_SOURCE + "&" +
                DesktopConstants.KEY_SCYD_PAGETYPE + "=" + DesktopConstants.SCYD_PAGETYPE);
        intent.setData(uri);
        mContext.startActivity(intent);
    }

    /**
     * 下载应用
     *
     * @param desktopItem     对应坑位
     * @param desktopItemInfo 对应坑位信息
     *                        <p>
     *                        Created by lancy on 2017/12/26 14:17
     */
    private void downloadApp(final DesktopItem desktopItem,
                             final DesktopItemInfo desktopItemInfo) {
        if (desktopItemInfo == null
                || StringUtils.isEmpty(desktopItemInfo.getAppUrl())
                || StringUtils.isEmpty(desktopItemInfo.getAppMd5())) {
            resetItemProgressBar(desktopItem);
            CustomToast
                    .makeText(mContext, R.string.msg_prepare_desktop_item_info_failed, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR).
                    show();
            return;
        }

        DownloadManager
                .getInstance(mContext)
                .download(new DownloadTask(
                        desktopItemInfo.getAppUrl(),
                        new File(Environment.getExternalStorageDirectory(), DOWNLOAD_PREFIX + desktopItem.getId() + DOWNLOAD_SUFFIX),
                        null,
                        null,
                        null,
                        true,
                        new DownloadCallBack<File>() {
                            @Override
                            public void onCheck(int code) {
                                switch (code) {
                                    case DownloadManager.DOWNLOAD_FAILED_REPEAT_TASK:
                                        resetItemProgressBar(desktopItem);
                                        CustomToast
                                                .makeText(mContext, R.string.msg_check_download_failed, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                                .show();
                                        break;
                                    case DownloadManager.DOWNLOAD_FAILED_NETWORK_UNCONNECTED:
                                        resetItemProgressBar(desktopItem);
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
                                desktopItem.setStatus(DesktopItem.Status.DOWNLOADING);
                                desktopItem.getProgressBar().updateStatus(DesktopItemProgressBar.Status.START_DOWNLOAD);
                            }

                            @Override
                            public void onProgress(int progress) {
                                LogUtils.debug(TAG, "222 onLoading(), thread's name : " + Thread.currentThread().getName()
                                        + ", progress = " + progress + "%");
                                desktopItem.getProgressBar().updateProgress(progress);
                            }

                            @Override
                            public void onError(int errorCode, Throwable t) {
                                super.onError(errorCode, t);
                                LogUtils.error(TAG, "download error message : " + t.getMessage());
                                t.printStackTrace();

                                resetItemProgressBar(desktopItem);
                                CustomToast
                                        .makeText(mContext, R.string.msg_download_failed, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                        .show();
                            }

                            @Override
                            public void onStop() {
                                super.onStop();
                                LogUtils.error(TAG, "stop download ......");
                                resetItemProgressBar(desktopItem);
                                CustomToast
                                        .makeText(mContext, R.string.msg_stop_download, Toast.LENGTH_SHORT, CustomToast.MessageType.WARN)
                                        .show();
                            }

                            @Override
                            public void onSuccess(File file) {
                                super.onSuccess(file);
                                LogUtils.info(TAG, "download success : file = " + file.getPath());
                                // 状态->安装
                                desktopItem.setStatus(DesktopItem.Status.INSTALLING);
                                desktopItem.getProgressBar().updateStatus(DesktopItemProgressBar.Status.DOWNLOAD_COMPLETE);

                                installApp(file, desktopItemInfo, desktopItem);
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
    private void installApp(final File file, final DesktopItemInfo itemInfo,
                            final DesktopItem desktopItem) {
        // 校验
        desktopItem.getProgressBar().updateStatus(DesktopItemProgressBar.Status.VERIFYING);
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
                                resetItemProgressBar(desktopItem);
                                CustomToast
                                        .makeText(mContext, R.string.msg_check_md5_failed, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                        .show();
                                return;
                            }

                            // 安装
                            desktopItem.getProgressBar().updateStatus(DesktopItemProgressBar.Status.INSTALLING);
                            LogUtils.debug(TAG, "start install package, file : " + file + ", packageName : " + itemInfo.getPackageName());

                            // 保存当前坑位对应的信息，以便回调时使用
                            mInstallDesktopItems.add(new InstallDesktopItem(itemInfo.getPackageName(), file.getPath(), desktopItem));

                            // 发送安装应用命令
                            // TODO: 2017/12/30 如果安装完成之后没有成功发送广播会导致界面没法更新，下载文件没法清除
                            ArrayList<String> data = new ArrayList<>();
                            data.add(file.getPath());
                            CommonUtils.sendInstallPackageBroadcast(mContext, data);
                        }));
    }


    /**
     * 注册广播接受者
     * <p>
     * Created by lancy on 2017/12/26 18:03
     */
    private void registerReceivers() {
        /*
        // 应用安装
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonConstants.ACTION_PACKAGEINSTALL_COMPLETE);
        mPackageInstallerReceiver = new PackageInstallerReceiver();
        mContext.registerReceiver(mPackageInstallerReceiver, filter);

        // 注册app 安装、 卸载、 替换 接收者
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        mPackageChangedBroadcastReceiver = new PackageChangedBroadcastReceiver();
        mContext.registerReceiver(mPackageChangedBroadcastReceiver, filter);
        */
    }

    /**
     * 注销广播接受者
     * <p>
     * Created by lancy on 2017/12/26 18:03
     */
    public void unregisterReceivers() {
        /*
        if (mPackageInstallerReceiver != null) {
            mContext.unregisterReceiver(mPackageInstallerReceiver);
        }

        if (mPackageChangedBroadcastReceiver != null) {
            mContext.unregisterReceiver(mPackageChangedBroadcastReceiver);
        }
        */
    }

    /**
     * 应用安装回调
     *
     * @param context 上下文
     * @param intent  数据
     *                <p>
     *                Created by lancy on 2017/12/26 18:04
     */
    @SuppressWarnings("unused")
    public void onPackageInstall(Context context, final Intent intent) {
        LogUtils.verbose(TAG, "InstallPackageReceiver, onReceive()......");
        DockItemManager.getInstance(mContext).onPackageInstall(context, intent);
        mCompositeDisposable.add(
                Observable
                        .create((ObservableOnSubscribe<DesktopItem>) e -> {
                            String packageName = intent.getStringExtra(CommonConstants.PACKAGEINSTALL_PACKAGENAME);
                            String file = intent.getStringExtra(CommonConstants.PACKAGEINSTALL_FILE);
                            LogUtils.debug(TAG, "onPackageInstall(), packageName : " + packageName);
                            LogUtils.debug(TAG, "onPackageInstall(), file : " + file);
                            DesktopItem desktopItem = null;
                            if (!mInstallDesktopItems.isEmpty()) {
                                synchronized (mInstallDesktopItems) {
                                    for (InstallDesktopItem item : mInstallDesktopItems) {
                                        if ((!StringUtils.isEmpty(packageName) && packageName.equals(item.getPackageName()))
                                                || (!StringUtils.isEmpty(file) && file.equals(item.getFile()))) {
                                            // 删除安装包
                                            FileUtils.deleteFile(item.getFile());

                                            desktopItem = item.getDesktopItem();

                                            // 移除安装完成坑位
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
                        .subscribe(desktopItem -> {
                                    if (desktopItem == null) {
                                        return;
                                    }

                                    // TODO: 2017/12/26 提示具体错误信息
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

                                    resetItemProgressBar(desktopItem);
                                },
                                throwable -> {
                                    LogUtils.debug(TAG, "onPackageInstall, error message : " + throwable.getMessage());
                                    throwable.printStackTrace();
                                }));
    }


    /**
     * 封装安装应用的信息
     * <p>
     * Created by lancy on 2017/12/29 10:18
     */
    private class InstallDesktopItem {
        private String packageName;
        private String file;
        private DesktopItem desktopItem;

        public InstallDesktopItem(String packageName, String file, DesktopItem desktopItem) {
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

        public DesktopItem getDesktopItem() {
            return desktopItem;
        }

        public void setDesktopItem(DesktopItem desktopItem) {
            this.desktopItem = desktopItem;
        }
    }

    /**
     * 重置坑位状态
     *
     * @param desktopItem 对应坑位
     *                    <p>
     *                    Created by lancy on 2017/12/29 10:17
     */
    private void resetItemProgressBar(DesktopItem desktopItem) {
        // 状态->空闲
        desktopItem.setStatus(DesktopItem.Status.IDLE);
        desktopItem.hideProgressBar();
    }

    /**
     * 网络状态改变回调
     *
     * @param networkInfo 网络信息
     *                    <p>
     *                    Created by lancy on 2018/1/2 20:56
     */
    public void onNetworkChange(NetworkInfo networkInfo) {
        if (networkInfo == null) {
            return;
        }

        // 更新坑位信息
        DesktopItemUpdateManager.getInstance(mContext).updateItemInfos();
    }

    /**
     * 应用状态改变广播
     * 目前桌面的设计只针对可替换应用
     * 广播原则：
     * >>> 安装
     * action : android.intent.action.PACKAGE_ADDED
     * <p>
     * >>> 升级
     * action : android.intent.action.PACKAGE_REMOVED
     * action : android.intent.action.PACKAGE_ADDED
     * action : android.intent.action.PACKAGE_REPLACED
     * <p>
     * >>> 卸载
     * action : android.intent.action.PACKAGE_REMOVED
     * action : android.intent.action.PACKAGE_FULLY_REMOVED
     * <p>
     * Created by lancy on 2018/1/8 12:38
     */
    private final class PackageChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
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
                    }

                }
            }
        }
    }

    /**
     * 系统中有应用状态发生改变时回调
     * 目前桌面的设计只针对可替换应用
     *
     * @param intent BroadcastReceiver回传的Intent
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

    /*public void onPackageChanged(Intent intent,String image,String label) {
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
                    onKTVAppUpdateData(packageName,image,label);

                }

            }
        }
    }*/


    /**
     * 桌面软件替换KTV
     *
     * @param packageName
     */
    private void onKTVAppUpdateData(String packageName, String image, String label) {
        LogUtils.debug(TAG, "onKTVAppUpdateData(), packageName : " + packageName);
        if (!packageName.contains("com.lycoo.lancy.ktv")) {
            return;
        }
        mCompositeDisposable.add(
                getItemInfoByPackageName("com.android.settings/.wifi.WifiSettings")
                        .subscribeOn(Schedulers.io())
                        .filter(itemInfo -> {
                            // 只处理可替换应用
                            return itemInfo != null
                                    && itemInfo.isPersistent();
                        })
                        .doOnNext(itemInfo -> {
                            itemInfo.setPackageName("com.lycoo.lancy.ktv");
                            itemInfo.setLabel(TextUtils.isEmpty(label) ? "\u0020" : label);
                            itemInfo.setImageUrl(image);
                            updateItemInfo(itemInfo);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(itemInfo -> {
                            LogUtils.info(TAG, "app updated, itemInfo : " + itemInfo);
                            // 更新UI,
                            getItem(itemInfo.getTag()).update(itemInfo);
                        }, throwable -> {
                            LogUtils.error(TAG, "onKTVAppUpdateData(), error message: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    /**
     * 当应用卸载时回调
     *
     * @param packageName 卸载应用的包名
     *                    <p>
     *                    Created by lancy on 2018/1/8 14:52
     */
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
                            LogUtils.info(TAG, "app removed, update DesktopItem..., itemInfo : " + itemInfo);
                            getItem(itemInfo.getTag()).update(itemInfo);
                        }, throwable -> {
                            LogUtils.error(TAG, "onAppRemoved(), error message: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));

        // 处理DesktopContainerItem
        mCompositeDisposable.add(
                Observable
                        .create((ObservableOnSubscribe<Integer>) emitter -> {
                            int rows = removeContainerItemInfos(packageName);
                            emitter.onNext(rows);
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe(rows -> {
                            if (rows > 0) {
                                RxBus.getInstance().post(new DesktopEvent.RemoveAppEvent(packageName));
                            }
                        }, throwable -> {
                            LogUtils.error(TAG, "failed to remove containerItemInfo recrods, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
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
                            // 更新UI,
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
                            // 只处理可替换应用
                            return itemInfo != null
                                    && itemInfo.isPersistent();
                        }).doOnNext(itemInfo -> {
                            // 更新数据库
                            updateItemInfo(itemInfo);
                        }).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(itemInfo -> {
                            LogUtils.info(TAG, "app updated, itemInfo : " + itemInfo);
                            // 更新UI,
                            getItem(itemInfo.getTag()).update(itemInfo);
                        }, throwable -> {
                            LogUtils.error(TAG, "onAppReplaced(), error message: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    public void onDestroy() {
        if (mCompositeDisposable != null && mCompositeDisposable.size() > 0) {
            mCompositeDisposable.clear();
        }
    }

}