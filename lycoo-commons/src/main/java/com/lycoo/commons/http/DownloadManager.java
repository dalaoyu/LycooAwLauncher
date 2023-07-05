package com.lycoo.commons.http;

import android.content.Context;
import android.text.TextUtils;

import com.lycoo.commons.entity.CheckResult;
import com.lycoo.commons.util.CollectionUtils;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 下载管理器
 * 重要概念：
 * 1. 下载队列： 存放正在下载的任务
 * 2. 等待队列： 存放正在等待下载的任务
 * 3. 下载列表： 指的是 下载队列 + 等待队列 的任务
 *
 * Created by lancy on 2019/5/30
 */
public class DownloadManager implements IDownloadManager {
    private static final String TAG = DownloadManager.class.getSimpleName();
    // DEBUG-LOG: 数据调试，打包时关闭
    private static final boolean DEBUG = true;

    /** 无法下载， 网络未连接 */
    public static final int DOWNLOAD_FAILED_NETWORK_UNCONNECTED = 0;
    /** 无法下载， 超过最大任务数 */
    public static final int DOWNLOAD_FAILED_BEYOND_MAX_TASK_COUNT = 1;
    /** 无法下载， 任务重复 */
    public static final int DOWNLOAD_FAILED_REPEAT_TASK = 2;

    /** 默认最大同时下载任务数 */
    public static final int DEF_MAX_DOWNLOADING_TASK_COUNT = 3;
    /** 默认最大下载任务数 */
    public static final int DEF_MAX_TASK_COUNT = 30;

    private int mMaxDownloadingTaskCount = DEF_MAX_DOWNLOADING_TASK_COUNT;  // 最大同时下载任务数
    private int mMaxTaskCount = DEF_MAX_TASK_COUNT;                         // 最大下载任务数
    private boolean mCheckable = true;                                      // 默认下载前进行检查

    /**
     * 等待队列
     * 排队要下载的任务，不包含正在下载的任务
     */
    private List<DownloadTask> mDownloadTasks;

    /**
     * 下载队列
     * 正在下载的任务
     */
    private Map<String, DownloadTask> mDownloadingTaskMap;


    private Context mContext;
    private static DownloadManager mInstance;

    private DownloadManager(Context context) {
        mContext = context;
        mDownloadTasks = new ArrayList<>();
        mDownloadingTaskMap = new HashMap<>();
    }

    public static DownloadManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new DownloadManager(context);
                }
            }
        }
        return mInstance;
    }


    @Override
    public IDownloadManager setMaxDownloadingTaskCount(int count) {
        mMaxDownloadingTaskCount = count;
        return this;
    }

    @Override
    public IDownloadManager setMaxTaskCount(int count) {
        mMaxTaskCount = count;
        return this;
    }

    @Override
    public void download(DownloadTask downloadTask) {
        LogUtils.debug(TAG, "downloadTask : " + downloadTask);
        if (downloadTask == null
                || downloadTask.getFile() == null
                || downloadTask.getDownloadCallBack() == null
                || TextUtils.isEmpty(downloadTask.getUrl())) {
            throw new RuntimeException("Invalid DownloadTask, please check DownloadTask param.....");
        }

        if (mCheckable) {
            // 检查网络
            if (!DeviceUtils.isNetworkConnected(mContext)) {
                downloadTask.getDownloadCallBack().onCheck(DOWNLOAD_FAILED_NETWORK_UNCONNECTED);
                return;
            }

            // 检查任务数是否超过上限
            if (mDownloadingTaskMap.size() + mDownloadTasks.size() >= mMaxTaskCount) {
                downloadTask.getDownloadCallBack().onCheck(DOWNLOAD_FAILED_BEYOND_MAX_TASK_COUNT);
                return;
            }

            // 检查任务是否重复
            if (getTaskStatus(downloadTask.getFile().getPath()) != Downloads.STATUS_PENDING) {
                downloadTask.getDownloadCallBack().onCheck(DOWNLOAD_FAILED_REPEAT_TASK);
                return;
            }
        }

        // 检查通过，已准备好下载
        downloadTask.getDownloadCallBack().onPrepared();
        synchronized (DownloadManager.class) {
            LogUtils.debug(TAG, "DownloadingTask's size = " + mDownloadingTaskMap.size() + ", MaxDownloadingTaskCount = " + mMaxDownloadingTaskCount);
            // 如果当前正在下载的任务数 >= 允许下载的最大任务数, 则排队（加入到排队任务列表）
            if (mDownloadingTaskMap.size() >= mMaxDownloadingTaskCount) {
                mDownloadTasks.add(downloadTask);
                return;
            }
            // 如果当前正在下载的任务数 < 允许下载的最大任务数， 则下载（加入到正在下载任务列表）
            mDownloadingTaskMap.put(downloadTask.getFile().getPath(), downloadTask);
        }
        doDownload(downloadTask);
    }

    private void doDownload(DownloadTask downloadTask) {
        // DownloadCallBack自己维护进度条
        downloadTask.getDownloadCallBack().setUpdateProgress(true);
        downloadTask.getDownloadCallBack().setManager(this);
        downloadTask.getDownloadCallBack().setTargetFile(downloadTask.getFile().getPath()); // 如果文件已经存在并且下载完成，这里不设置的话导致DownloadManager updateTasks()不会调用
        if (downloadTask.getEncryptWords() == null) {
            downloadTask.setEncryptWords("");
        }
        // 开始下载
        downloadTask.getDownloadCallBack().onStart();
        if (downloadTask.isAutoResume()) {
            Downloader.doDownloadAutoResume(
                    mContext,
                    downloadTask.getUrl(),
                    downloadTask.getFile(),
                    downloadTask.getStatFsPath(),
                    downloadTask.getThresholdSpace(),
                    downloadTask.getEncryptWords(),
                    downloadTask.getDownloadCallBack());
        } else {
            Downloader.doDownload(
                    mContext,
                    downloadTask.getUrl(),
                    downloadTask.getFile(),
                    downloadTask.getStatFsPath(),
                    downloadTask.getEncryptWords(),
                    downloadTask.getDownloadCallBack());
        }
    }

    @Override
    public void stopDownload(String file) {
        synchronized (DownloadManager.class) {
            // file在下载队列中，调用DownloadCallBack的stop()
            if (mDownloadingTaskMap.containsKey(file)) {
                LogUtils.debug(TAG, "stop downloading task : " + file);
                mDownloadingTaskMap.get(file).getDownloadCallBack().stop();
                return;
            }

            // file在等待队列中，则直接从等待队列中移除
            if (!CollectionUtils.isEmpty(mDownloadTasks)) {
                for (DownloadTask task : mDownloadTasks) {
                    if (task.getFile().getPath().equals(file)) {
                        LogUtils.debug(TAG, "stop download task : " + file);
                        mDownloadTasks.remove(task);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public Integer getTaskStatus(String file) {
        synchronized (DownloadManager.class) {
            // file是否在下载队列中
            if (!CollectionUtils.isEmpty(mDownloadingTaskMap) && mDownloadingTaskMap.containsKey(file)) {
                return Downloads.STATUS_DOWNLOADING;
            }

            // file是否在等待队列中
            if (!CollectionUtils.isEmpty(mDownloadTasks)) {
                for (DownloadTask task : mDownloadTasks) {
                    if (task.getFile().getPath().equals(file)) {
                        return Downloads.STATUS_WAITING;
                    }
                }
            }
            return Downloads.STATUS_PENDING;
        }
    }

    @Override
    public CheckResult checkTask(String file) {
        // 检查网络
        if (!DeviceUtils.isNetworkConnected(mContext)) {
            return new CheckResult(false, DOWNLOAD_FAILED_NETWORK_UNCONNECTED);
        }

        // 检查任务数是否超过上限
        if (mDownloadingTaskMap.size() + mDownloadTasks.size() >= mMaxTaskCount) {
            return new CheckResult(false, DOWNLOAD_FAILED_BEYOND_MAX_TASK_COUNT);
        }

        // 检查任务是否重复
        if (getTaskStatus(file) != Downloads.STATUS_PENDING) {
            return new CheckResult(false, DOWNLOAD_FAILED_REPEAT_TASK);
        }

        return new CheckResult(true);
    }

    @Override
    public void updateTask(String file) {
        DownloadTask downloadTask = null;
        if (DEBUG) {
            LogUtils.debug(TAG, "DownloadingTaskMap : " + mDownloadingTaskMap.keySet());
            LogUtils.debug(TAG, "DownloadTasks : " + mDownloadTasks);
        }
        synchronized (DownloadManager.class) {
            // 1. 从下载队列中移除下载完成（下载出错/停止下载）的任务
            if (mDownloadingTaskMap.containsKey(file)) {
                LogUtils.debug(TAG, "remove download mark: " + file);
                mDownloadingTaskMap.remove(file);
            }

            // 2. 从等待队列中取出新的任务，放入下载列表中
            if (!CollectionUtils.isEmpty(mDownloadTasks) && mDownloadingTaskMap.size() < mMaxDownloadingTaskCount) {
                downloadTask = mDownloadTasks.get(0);
                LogUtils.debug(TAG, "download new task: " + downloadTask);
                mDownloadTasks.remove(downloadTask);
                mDownloadingTaskMap.put(downloadTask.getFile().getPath(), downloadTask);
            }
        }
        // 开始下载新的任务
        if (downloadTask != null) {
            doDownload(downloadTask);
        }
    }

    /**
     * 目前任务是否在下载列表
     *
     * @param file 目标文件
     * @return 如果目前任务已经在下载列表中（在 下载队列 或 等待队列 中）返回true， 否则返回false
     *
     * Created by lancy on 2019/11/16 17:38
     */
    public boolean isDownloading(String file) {
        return getTaskStatus(file) != Downloads.STATUS_PENDING;
    }

    /**
     * 设置进行下载的检查工作
     * 有的任务在下载前会做检查工作，所以提供此接口避免重复检查
     *
     * @param able true检查， false不检查
     *
     *             Created by lancy on 2019/11/16 18:03
     */
    public IDownloadManager setCheckable(boolean able) {
        mCheckable = able;
        return this;
    }

    /**
     * 注销时调用
     *
     * Created by lancy on 2019/7/30 12:02
     */
    public void onDestroy() {
        // 清空等待队列
        if (!CollectionUtils.isEmpty(mDownloadTasks)) {
            Iterator<DownloadTask> iterator = mDownloadTasks.iterator();
            while (iterator.hasNext()) {
                DownloadTask downloadTask = iterator.next();
                if (downloadTask != null) {
                    iterator.remove();
                }
            }
        }

        // 停止等待队列中的任务
        for (String file : mDownloadingTaskMap.keySet()) {
            mDownloadingTaskMap.get(file).getDownloadCallBack().stop();
        }
    }
}
