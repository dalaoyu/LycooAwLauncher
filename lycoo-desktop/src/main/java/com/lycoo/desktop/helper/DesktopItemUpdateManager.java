package com.lycoo.desktop.helper;

import android.content.Context;
import android.text.TextUtils;

import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.http.HttpHelper;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.CollectionUtils;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.desktop.base.BaseBean;
import com.lycoo.desktop.base.DesktopService;
import com.lycoo.desktop.bean.CommonDesktopItemInfo;
import com.lycoo.desktop.bean.ConfigAppItemInfo;
import com.lycoo.desktop.bean.ContainerItemInfo;
import com.lycoo.desktop.bean.DesktopItemInfo;
import com.lycoo.desktop.bean.DockItemInfo;
import com.lycoo.desktop.bean.QiyiItemInfo;
import com.lycoo.desktop.bean.SpecializedAppItemInfo;
import com.lycoo.desktop.bean.SpecializedPageItemInfo;
import com.lycoo.desktop.bean.WebsiteItemInfo;
import com.lycoo.desktop.bean.response.DesktopItemResponse;
import com.lycoo.desktop.config.DesktopConstants;
import com.lycoo.desktop.config.DockConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 桌面坑位信息更新辅助类
 * <p>
 * Created by lancy on 2018/1/9
 */
public class DesktopItemUpdateManager {
    private static final String TAG = DesktopItemUpdateManager.class.getSimpleName();

    private Context mContext;
    private static DesktopItemUpdateManager mInstance;
    private CompositeDisposable mCompositeDisposable;

    private DesktopItemUpdateManager(Context context) {
        this.mContext = context;
        mCompositeDisposable = new CompositeDisposable();
    }

    public static DesktopItemUpdateManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DesktopItemUpdateManager.class) {
                if (mInstance == null) {
                    mInstance = new DesktopItemUpdateManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 更新坑位信息
     * <p>
     * Created by lancy on 2017/12/16 16:30
     */
/*    public void updateItemInfos() {
        mCompositeDisposable.add(
                HttpHelper.getInstance(mContext)
                        .getService(DesktopService.class)
                        .getDesktopItemInfos(
                                ApplicationUtils.getApplicationMetaData(mContext, CommonConstants.APP_KEY),
                                DeviceUtils.getEthernetMacBySeparator(""),
                                DeviceUtils.getCustomerCode())
                        .subscribeOn(Schedulers.io())
                        .map(desktopItemResponse -> {
                            List<DesktopItemInfo> desktopItemInfos = parseItemInfos(desktopItemResponse);
                            if (CollectionUtils.isEmpty(desktopItemInfos)) {
                                desktopItemInfos = Collections.emptyList();
                            }
                            // 更新数据库
                            DesktopItemManager.getInstance(mContext).updateItemInfos(desktopItemInfos);
                            return desktopItemInfos;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(itemInfos -> {
                            if (!CollectionUtils.isEmpty(itemInfos)) {
                                for (DesktopItemInfo desktopItemInfo : itemInfos) {
                                    DesktopItemManager.getInstance(mContext).getItem(desktopItemInfo.getTag()).update(desktopItemInfo);
                                }
                            }
                        }, throwable -> {
                            LogUtils.error(TAG, "failed to update desktop item infos, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }*/
    public void updateItemInfos() {
        //System.out.println("liu::::1    "+ApplicationUtils.getApplicationMetaData(mContext, CommonConstants.APP_KEY)+"   "+DeviceUtils.getEthernetMacBySeparator("")
        //+"    "+DeviceUtils.getCustomerCode());
        mCompositeDisposable.add(
                HttpHelper.getInstance(mContext)
                        .getService(DesktopService.class)
                        .getDesktopItemInfos(
                                ApplicationUtils.getApplicationMetaData(mContext, CommonConstants.APP_KEY),
                                DeviceUtils.getEthernetMacBySeparator(""),
                                DeviceUtils.getCustomerCode())
                        .subscribeOn(Schedulers.io())
                        .map(desktopItemResponse -> {
                            List<BaseBean> desktopItemInfos = parseDockItemInfos(desktopItemResponse);
                            List<DesktopItemInfo> infos=new ArrayList<>();
                            List<DockItemInfo> infos1=new ArrayList<>();
                            for (BaseBean bean:desktopItemInfos){
                                if (bean.getTag()>999){
                                    infos1.add((DockItemInfo)bean);
                                }else {
                                    infos.add((DesktopItemInfo)bean);
                                }
                            }
                            // 更新数据库
                            if (infos.size()>0){
                                DesktopItemManager.getInstance(mContext).updateItemInfos(infos);
                            }else{
                                DockItemManager.getInstance(mContext).updateItemInfos(infos1);
                            }

                            return desktopItemInfos;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(itemInfos -> {
                            if (!CollectionUtils.isEmpty(itemInfos)) {
                                for (BaseBean desktopItemInfo : itemInfos) {
                                    if (desktopItemInfo.getTag()>999){
                                        DockItemInfo desktopItemInfo1=(DockItemInfo)desktopItemInfo;
                                        DockItemManager.getInstance(mContext).getItem(desktopItemInfo.getTag()).update(desktopItemInfo1);
                                    }else{
                                        DesktopItemInfo desktopItemInfo1=(DesktopItemInfo)desktopItemInfo;
                                        DesktopItemManager.getInstance(mContext).getItem(desktopItemInfo.getTag()).update(desktopItemInfo1);
                                    }

                                }
                            }
                        }, throwable -> {
                            //System.out.println("liu::::1    failed to update desktop item infos, error message  "+ throwable.getMessage());
                            LogUtils.error(TAG, "failed to update desktop item infos, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    /**
     * 解析坑位信息
     *
     * @param desktopItemResponse 服务端返回坑位信息
     * @return 需要更新的坑位信息
     * @throws ParseException 解析异常
     *
     *                        Created by lancy on 2018/1/9 17:04
     */
    private List<BaseBean> parseDockItemInfos(DesktopItemResponse desktopItemResponse) throws ParseException {
        if (desktopItemResponse == null) {
            LogUtils.error(TAG, "DesktopItemResponse is null......");
            return null;
        }

        int statusCode = desktopItemResponse.getStatusCode();
        if (statusCode == CommonConstants.STATUS_CODE_ERROR) {
            LogUtils.error(TAG, "responseMessage = " + desktopItemResponse.getMessage());
            return null;
        }

        List<CommonDesktopItemInfo> itemInfos = desktopItemResponse.getData();
        if (CollectionUtils.isEmpty(itemInfos)) {
            LogUtils.warn(TAG, "no desktop items......");
            return null;
        }

        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        List<BaseBean> desktopItemInfos = new ArrayList<>();
        // 遍历封装DesktopItemInfo
        for (CommonDesktopItemInfo itemInfo : itemInfos) {
            LogUtils.debug(TAG, "CommonDesktopItemInfo = " + itemInfo);
            String tag = itemInfo.getTag();
            String updateTime = itemInfo.getUpdateTime();
            String localUpdateTime;
            BaseBean desktopItemInfo = null;
            if (tag.length()==3){
                localUpdateTime = DesktopItemManager.getInstance(mContext).getItemInfoUpdateTime(tag);
                desktopItemInfo=new DesktopItemInfo();
            }else{
                localUpdateTime = DockItemManager.getInstance(mContext).getItemInfoUpdateTime(tag);
                desktopItemInfo=new DockItemInfo();
            }

            if (localUpdateTime==null)
                localUpdateTime= DockConstants.DEF_UPDATETIME;

            LogUtils.debug(TAG, "updateTime = " + updateTime + ", localUpdateTime = " + localUpdateTime);
            if (!mDateFormat.parse(localUpdateTime).before(mDateFormat.parse(updateTime))) {
                LogUtils.info(TAG, tag + " is the latest version......");
                continue;
            }


            desktopItemInfo.setTag(Integer.parseInt(tag));
            desktopItemInfo.setLabel(itemInfo.getLabel());
            desktopItemInfo.setImageUrl(itemInfo.getImageUrl());
            desktopItemInfo.setIconUrl(itemInfo.getIconUrl());
            desktopItemInfo.setIconVisible(itemInfo.isIconVisible());
            desktopItemInfo.setUpdateTime(updateTime);

            int type = itemInfo.getType();
            switch (type) {
                // 配置应用
                case DesktopConstants.CONFIG_APP:
                    desktopItemInfo.setType(type);
                    ConfigAppItemInfo configAppItem = itemInfo.getConfigAppItem();
                    if (configAppItem != null) {
                        desktopItemInfo.setPackageName(configAppItem.getPackageName());
                        desktopItemInfo.setAppUrl(configAppItem.getAppUrl());
                        desktopItemInfo.setAppMd5(configAppItem.getAppMd5());
                        if(configAppItem.getAppVersion() > 0){
                            desktopItemInfo.setAppVersion(configAppItem.getAppVersion());
                        } else {
                            String[] versions = configAppItem.getAppUrl().split("___");
                            for (String version : versions) {
                                //LogUtils.info(TAG, "version is " + version);
                                if(!TextUtils.isEmpty(version) && version.contains(".apk")){
                                    try {
                                        LogUtils.error(TAG,"version:::="+version);
                                        int appVersion = Integer.parseInt(version.replace(".apk", ""));
                                        desktopItemInfo.setAppVersion(appVersion);
                                        LogUtils.info(TAG, "appVersion is " + appVersion);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    break;

                // 替换应用
                case DesktopConstants.REPLACEABLE_APP:
                    desktopItemInfo.setType(type);
                    break;

                // 固定应用
                case DesktopConstants.SPECIALIZED_APP:
                    desktopItemInfo.setType(type);
                    SpecializedAppItemInfo specializedAppItem = itemInfo.getSpecializedAppItem();
                    if (specializedAppItem != null) {
                        desktopItemInfo.setPackageName(specializedAppItem.getPackageName());
                        desktopItemInfo.setParam1(specializedAppItem.getParam1());
                        desktopItemInfo.setParam2(specializedAppItem.getParam2());
                        desktopItemInfo.setParam3(specializedAppItem.getParam3());
                    }
                    break;

                // 固定页面
                case DesktopConstants.SPECIALIZED_PAGE:
                    desktopItemInfo.setType(type);
                    SpecializedPageItemInfo specializedPageItem = itemInfo.getSpecializedPageItem();
                    if (specializedPageItem != null) {
                        desktopItemInfo.setClassName(specializedPageItem.getSimpleClassName());
                        desktopItemInfo.setParam1(specializedPageItem.getParam1());
                        desktopItemInfo.setParam2(specializedPageItem.getParam2());
                        desktopItemInfo.setParam3(specializedPageItem.getParam3());
                    }
                    break;

                // 网络站点
                case DesktopConstants.WEBSITE:
                    desktopItemInfo.setType(type);
                    WebsiteItemInfo websiteItem = itemInfo.getWebsiteItem();
                    if (websiteItem != null) {
                        desktopItemInfo.setWebsiteUrl(websiteItem.getWebsiteUrl());
                    }
                    break;

                // 奇艺坑位
                case DesktopConstants.QIYI_ITEM:
                    QiyiItemInfo qiyiItem = itemInfo.getQiyiItem();
                    if (qiyiItem != null) {
                        desktopItemInfo.setType(qiyiItem.getQiyiType());
                        desktopItemInfo.setQiyiData(qiyiItem.getQiyiData());
                    }

                    break;

                // 容器
                case DesktopConstants.CONTAINER_ITEM:
                    ContainerItemInfo containerItem = itemInfo.getContainerItem();
                    if (containerItem != null) {
                        desktopItemInfo.setType(containerItem.getContainerType());
                        desktopItemInfo.setParam1(containerItem.getParam1());
                        desktopItemInfo.setParam2(containerItem.getParam2());
                        desktopItemInfo.setParam3(containerItem.getParam3());
                    }
                    break;
            }
            desktopItemInfos.add(desktopItemInfo);
        }

        return desktopItemInfos;
    }



    /**
     * 解析坑位信息
     *
     * @param desktopItemResponse 服务端返回坑位信息
     * @return 需要更新的坑位信息
     * @throws ParseException 解析异常
     *                        <p>
     *                        Created by lancy on 2018/1/9 17:04
     */
    private List<DesktopItemInfo> parseItemInfos(DesktopItemResponse desktopItemResponse) throws ParseException {
        if (desktopItemResponse == null) {
            LogUtils.error(TAG, "DesktopItemResponse is null......");
            return null;
        }

        int statusCode = desktopItemResponse.getStatusCode();
        if (statusCode == CommonConstants.STATUS_CODE_ERROR) {
            LogUtils.error(TAG, "responseMessage = " + desktopItemResponse.getMessage());
            return null;
        }

        List<CommonDesktopItemInfo> itemInfos = desktopItemResponse.getData();
        if (CollectionUtils.isEmpty(itemInfos)) {
            LogUtils.warn(TAG, "no desktop items......");
            return null;
        }

        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        List<DesktopItemInfo> desktopItemInfos = new ArrayList<>();
        // 遍历封装DesktopItemInfo
        for (CommonDesktopItemInfo itemInfo : itemInfos) {
            LogUtils.debug(TAG, "CommonDesktopItemInfo = " + itemInfo);
            String tag = itemInfo.getTag();
            String updateTime = itemInfo.getUpdateTime();
            String localUpdateTime = DesktopItemManager.getInstance(mContext).getItemInfoUpdateTime(tag);
            LogUtils.debug(TAG, "updateTime = " + updateTime + ", localUpdateTime = " + localUpdateTime);
            if (!mDateFormat.parse(localUpdateTime).before(mDateFormat.parse(updateTime))) {
                LogUtils.info(TAG, tag + " is the latest version......");
                continue;
            }

            DesktopItemInfo desktopItemInfo = new DesktopItemInfo();
            desktopItemInfo.setTag(Integer.parseInt(tag));
            desktopItemInfo.setLabel(itemInfo.getLabel());
            desktopItemInfo.setImageUrl(itemInfo.getImageUrl());
            desktopItemInfo.setIconUrl(itemInfo.getIconUrl());
            desktopItemInfo.setIconVisible(itemInfo.isIconVisible());
            desktopItemInfo.setUpdateTime(updateTime);

            int type = itemInfo.getType();
            switch (type) {
                // 配置应用
                case DesktopConstants.CONFIG_APP:
                    desktopItemInfo.setType(type);
                    ConfigAppItemInfo configAppItem = itemInfo.getConfigAppItem();
                    if (configAppItem != null) {
                        desktopItemInfo.setPackageName(configAppItem.getPackageName());
                        desktopItemInfo.setAppUrl(configAppItem.getAppUrl());
                        desktopItemInfo.setAppMd5(configAppItem.getAppMd5());
                        if (configAppItem.getAppVersion() > 0) {
                            desktopItemInfo.setAppVersion(configAppItem.getAppVersion());
                        } else {
                            String[] versions = configAppItem.getAppUrl().split("___");
                            for (String version : versions) {
                                //LogUtils.info(TAG, "version is " + version);
                                if (!TextUtils.isEmpty(version) && version.contains(".apk")) {
                                    try {
                                        LogUtils.error(TAG,"version:::"+version);
                                        int appVersion = Integer.parseInt(version.replace(".apk", ""));
                                        desktopItemInfo.setAppVersion(appVersion);
                                        LogUtils.info(TAG, "appVersion is " + appVersion);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    break;

                // 替换应用
                case DesktopConstants.REPLACEABLE_APP:
                    desktopItemInfo.setType(type);
                    break;

                // 固定应用
                case DesktopConstants.SPECIALIZED_APP:
                    desktopItemInfo.setType(type);
                    SpecializedAppItemInfo specializedAppItem = itemInfo.getSpecializedAppItem();
                    if (specializedAppItem != null) {
                        desktopItemInfo.setPackageName(specializedAppItem.getPackageName());
                        desktopItemInfo.setParam1(specializedAppItem.getParam1());
                        desktopItemInfo.setParam2(specializedAppItem.getParam2());
                        desktopItemInfo.setParam3(specializedAppItem.getParam3());
                    }
                    break;

                // 固定页面
                case DesktopConstants.SPECIALIZED_PAGE:
                    desktopItemInfo.setType(type);
                    SpecializedPageItemInfo specializedPageItem = itemInfo.getSpecializedPageItem();
                    if (specializedPageItem != null) {
                        desktopItemInfo.setClassName(specializedPageItem.getSimpleClassName());
                        desktopItemInfo.setParam1(specializedPageItem.getParam1());
                        desktopItemInfo.setParam2(specializedPageItem.getParam2());
                        desktopItemInfo.setParam3(specializedPageItem.getParam3());
                    }
                    break;

                // 网络站点
                case DesktopConstants.WEBSITE:
                    desktopItemInfo.setType(type);
                    WebsiteItemInfo websiteItem = itemInfo.getWebsiteItem();
                    if (websiteItem != null) {
                        desktopItemInfo.setWebsiteUrl(websiteItem.getWebsiteUrl());
                    }
                    break;

                // 奇艺坑位
                case DesktopConstants.QIYI_ITEM:
                    QiyiItemInfo qiyiItem = itemInfo.getQiyiItem();
                    if (qiyiItem != null) {
                        desktopItemInfo.setType(qiyiItem.getQiyiType());
                        desktopItemInfo.setQiyiData(qiyiItem.getQiyiData());
                    }

                    break;

                // 容器
                case DesktopConstants.CONTAINER_ITEM:
                    ContainerItemInfo containerItem = itemInfo.getContainerItem();
                    if (containerItem != null) {
                        desktopItemInfo.setType(containerItem.getContainerType());
                        desktopItemInfo.setParam1(containerItem.getParam1());
                        desktopItemInfo.setParam2(containerItem.getParam2());
                        desktopItemInfo.setParam3(containerItem.getParam3());
                    }
                    break;
            }
            desktopItemInfos.add(desktopItemInfo);
        }

        return desktopItemInfos;
    }

    public void onDestroy() {
        if (mCompositeDisposable != null && mCompositeDisposable.size() > 0) {
            mCompositeDisposable.clear();
        }
    }
}
