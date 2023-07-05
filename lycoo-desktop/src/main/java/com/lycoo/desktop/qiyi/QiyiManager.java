package com.lycoo.desktop.qiyi;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;

import com.lycoo.commons.util.CollectionUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;
import com.lycoo.desktop.bean.DesktopItemInfo;
import com.lycoo.desktop.config.DesktopConstants;
import com.lycoo.desktop.helper.DesktopItemManager;
import com.lycoo.desktop.ui.DesktopItem;
import com.qiyi.tv.client.ConnectionListener;
import com.qiyi.tv.client.QiyiClient;
import com.qiyi.tv.client.Result;
import com.qiyi.tv.client.data.Channel;
import com.qiyi.tv.client.data.Media;
import com.qiyi.tv.client.feature.common.PageType;
import com.qiyi.tv.client.feature.common.RecommendationType;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 爱奇艺管理类
 *
 * Created by lancy on 2017/12/19
 */
public class QiyiManager {
    private static final String TAG = QiyiManager.class.getSimpleName();

    private static final String EXTRUDE_RECOMMENDATIONS = "qiyiExtrudeRecommendations";
    private static final String COMMON_RECOMMENDATION = "qiyiCommonRecommendations";
    private static final String CHANNELS = "channels";

    private static QiyiManager mInstance;
    private Context mContext;
    private boolean mEnable;
    private List<Media> mQiyiExtrudeMedias;    // 轮播推荐
    private List<Media> mQiyiCommonMedias;     // 普通推荐
    private List<Channel> mQiyiChannels;       // 频道分类
    private CompositeDisposable mCompositeDisposable;

    private QiyiManager(Context context) {
        mContext = context;
        mEnable = SystemPropertiesUtils.getBoolean(DesktopConstants.PROPERTY_QIYI_SWITCH);
        mCompositeDisposable = new CompositeDisposable();
    }

    public static QiyiManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (QiyiManager.class) {
                if (mInstance == null) {
                    mInstance = new QiyiManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 是否允许加载爱奇艺内容
     *
     * @return true: 允许加载， false: 不加载
     *
     * Created by lancy on 2017/12/30 14:30
     */
    private boolean isEnable() {
        return mEnable;
    }

    /**
     * 初始化
     * 要保证只初始化一次， 最好放在Application中进行
     *
     * Created by lancy on 2017/12/30 14:30
     */
    public void init() {
        if (isEnable()) {
            QiyiClient.instance().initialize(mContext, QiyiConstants.SIGNATURE, QiyiConstants.PACKAGENAME);
            QiyiClient.instance().setListener(mQiyiConnectionListener);
        }
    }

    /**
     * 连接爱奇艺服务回调
     *
     * Created by lancy on 2017/12/30 14:31
     */
    private ConnectionListener mQiyiConnectionListener = new ConnectionListener() {
        @Override
        public void onConnected() {
            LogUtils.info(TAG, "onConnected()......");
        }

        @Override
        public void onAuthSuccess() {
            LogUtils.info(TAG, "onAuthSuccess()......");
            initQiyiExtrudeMedias();
            initQiyiCommonMedias();
            initQiyiChannels();
        }

        @Override
        public void onDisconnected() {
            LogUtils.info(TAG, "onDisconnected()......");
        }

        @Override
        public void onError(int i) {
            LogUtils.error(TAG, "onError()， i = " + i);
        }
    };

    /**
     * 网络变化回调
     *
     * @param networkInfo 网络信息
     *
     *                    Created by lancy on 2017/12/30 14:31
     */
    public void onNetworkChange(NetworkInfo networkInfo) {
        if (!isEnable()) {
            return;
        }

        if (networkInfo == null) {
            return;
        }

        if (!QiyiClient.instance().isConnected()) {
            QiyiClient.instance().connect();
        }
    }

    /**
     * 初始化轮播推荐内容
     *
     * Created by lancy on 2017/12/30 14:49
     */
    private void initQiyiExtrudeMedias() {
        LogUtils.info(TAG, "Qiyi Extrude Medias = " + mQiyiExtrudeMedias);
        if (!CollectionUtils.isEmpty(mQiyiExtrudeMedias)) {
            return;
        }

        mCompositeDisposable.add(
                Observable
                        .create((ObservableOnSubscribe<List<Media>>) emitter -> {
                            Result<List<Media>> result = QiyiClient.instance().getRecommendation(RecommendationType.EXTRUDE);
                            emitter.onNext(result != null
                                    ? result.data
                                    : null);
                            emitter.onComplete();
                        })
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(medias -> {
                            LogUtils.debug(TAG, "query extrude medias : " + medias);
                            mQiyiExtrudeMedias = medias;
                            // TODO: 2017/12/30  开始轮播
                        }, throwable -> {
                            LogUtils.error(TAG, "init qiyi extrude medias error， message ：" + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    /**
     * 初始化普通推荐内容
     *
     * Created by lancy on 2017/12/30 14:49
     */
    private void initQiyiCommonMedias() {
        LogUtils.info(TAG, "Qiyi Common Medias = " + mQiyiCommonMedias);
        if (!CollectionUtils.isEmpty(mQiyiCommonMedias)) {
            return;
        }

        mCompositeDisposable.add(
                Observable
                        .create((ObservableOnSubscribe<List<Media>>) emitter -> {
                            Result<List<Media>> result = QiyiClient.instance().getRecommendation(RecommendationType.COMMON);
                            emitter.onNext(result != null
                                    ? result.data
                                    : null);
                            emitter.onComplete();
                        })
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(medias -> {
                            LogUtils.debug(TAG, "query qiyi common medias : " + medias);
                            mQiyiCommonMedias = medias;
                            // 更新显示
                            updateCommonMediaItems(medias);

                        }, throwable -> {
                            LogUtils.error(TAG, "init qiyi common medias error， message ：" + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    /**
     * 初始化频道分类
     *
     * Created by lancy on 2017/12/30 14:49
     */
    private void initQiyiChannels() {
        LogUtils.info(TAG, "Qiyi Channels = " + mQiyiChannels);
        if (!CollectionUtils.isEmpty(mQiyiChannels)) {
            return;
        }

        mCompositeDisposable.add(
                Observable
                        .create((ObservableOnSubscribe<List<Channel>>) emitter -> {
                            Result<List<Channel>> result = QiyiClient.instance().getChannelList();
                            emitter.onNext(result != null
                                    ? result.data
                                    : null);
                            emitter.onComplete();
                        })
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(channels -> mQiyiChannels = channels, throwable -> {
                            LogUtils.error(TAG, "init qiyi channels error， message ：" + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    /**
     * 保存数据
     * 奇艺对每天获取接口数有限制，同时避免频繁获取冷数据。
     *
     * @param outState outState
     *
     *                 Created by lancy on 2017/12/30 15:06
     */
    public void onSaveInstanceState(Bundle outState) {
        if (!isEnable()) {
            return;
        }

        if (outState != null) {
            outState.putParcelableArrayList(EXTRUDE_RECOMMENDATIONS, (ArrayList<? extends Parcelable>) mQiyiExtrudeMedias);
            outState.putParcelableArrayList(COMMON_RECOMMENDATION, (ArrayList<? extends Parcelable>) mQiyiCommonMedias);
            outState.putParcelableArrayList(CHANNELS, (ArrayList<? extends Parcelable>) mQiyiChannels);
        }
    }

    /**
     * 恢复数据
     *
     * @param savedInstanceState savedInstanceState
     *
     *                           Created by lancy on 2017/12/30 15:06
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (!isEnable()) {
            return;
        }

        if (savedInstanceState != null) {
            mQiyiExtrudeMedias = savedInstanceState.getParcelableArrayList(EXTRUDE_RECOMMENDATIONS);
            mQiyiCommonMedias = savedInstanceState.getParcelableArrayList(COMMON_RECOMMENDATION);
            mQiyiChannels = savedInstanceState.getParcelableArrayList(CHANNELS);
        }
    }

    /**
     * 更新普通推荐位
     *
     * @param medias 普通推荐位内容
     *
     *               Created by lancy on 2017/12/30 15:48
     */
    private void updateCommonMediaItems(final List<Media> medias) {
        if (CollectionUtils.isEmpty(medias)) {
            return;
        }

        mCompositeDisposable.add(
                DesktopItemManager
                        .getInstance(mContext)
                        .getItemInfosByType(DesktopConstants.QIYI_COMMON_RECOMMENDATION)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(itemInfos -> {
                            if (CollectionUtils.isEmpty(itemInfos)) {
                                return;
                            }

                            for (DesktopItemInfo itemInfo : itemInfos) {
                                int index = Integer.parseInt(itemInfo.getQiyiData());
                                if (medias.size() >= index) {
                                    DesktopItem desktopItem = DesktopItemManager.getInstance(mContext).getItem(itemInfo.getTag());
                                    if (desktopItem != null) {
                                        desktopItem.loadImage(medias.get(index).getPicUrl(), desktopItem.getBg());
                                    }
                                }
                            }
                        }, throwable -> {
                            LogUtils.error(TAG, "updateCommonMediaItems(), error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                        }));
    }

    /**
     * 打开奇艺类型坑位
     *
     * @param desktopItemInfo 坑位信息
     *
     *                        Created by lancy on 2017/12/30 16:47
     */
    public void openItem(DesktopItemInfo desktopItemInfo) {
        switch (desktopItemInfo.getType()) {
            case DesktopConstants.QIYI_EXTRUDE_RECOMMENDATION:
                openExtrudeMedia(desktopItemInfo);
                break;

            case DesktopConstants.QIYI_COMMON_RECOMMENDATION:
                openCommonMedia(desktopItemInfo);
                break;

            case DesktopConstants.QIYI_CHANNEL:
                openChannel(desktopItemInfo);
                break;

            case DesktopConstants.QIYI_SPECIALIZED_PAGE:
                try {
                    int type = Integer.parseInt(desktopItemInfo.getQiyiData());
                    openItem(type);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 打开爱奇艺特定页面
     *
     * @param type 页面类型,例如搜素，历史等
     *
     *             Created by lancy on 2017/12/30 16:50
     */
    public void openItem(int type) {
        if (type != PageType.PAGE_UNKNOWN) {
            try {
                QiyiClient.instance().open(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开轮播推荐位
     *
     * @param desktopItemInfo 坑位信息
     *
     *                        Created by lancy on 2017/12/30 16:48
     */
    private void openExtrudeMedia(DesktopItemInfo desktopItemInfo) {
    }

    /**
     * 打开普通推荐位
     *
     * @param desktopItemInfo 坑位信息
     *
     *                        Created by lancy on 2017/12/30 16:46
     */
    private void openCommonMedia(DesktopItemInfo desktopItemInfo) {
        if (CollectionUtils.isEmpty(mQiyiCommonMedias)) {
            return;
        }

        int index = Integer.parseInt(desktopItemInfo.getQiyiData());
        if (mQiyiCommonMedias.size() >= index) {
            try {
                QiyiClient.instance().openMedia(mQiyiCommonMedias.get(index));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开频道
     *
     * @param desktopItemInfo 坑位信息
     *
     *                        Created by lancy on 2017/12/30 16:46
     */
    private void openChannel(DesktopItemInfo desktopItemInfo) {
        if (CollectionUtils.isEmpty(mQiyiChannels)) {
            return;
        }

        int channelId = Integer.parseInt(desktopItemInfo.getQiyiData());
        for (Channel channel : mQiyiChannels) {
            if (channel.getId() == channelId) {
                try {
                    QiyiClient.instance().openChannel(channel, channel.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void onDestroy() {
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }
    }
}
