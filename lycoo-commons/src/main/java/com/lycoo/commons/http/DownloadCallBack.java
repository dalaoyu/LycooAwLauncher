package com.lycoo.commons.http;

import android.text.TextUtils;

import com.lycoo.commons.util.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;

import okhttp3.ResponseBody;

/**
 * 下载任务
 *
 * Created by lancy on 2017/12/22
 */
public abstract class DownloadCallBack<T> implements DownloadListener<T> {
    private static final String TAG = DownloadCallBack.class.getSimpleName();
    private static final boolean DEBUG_DOWNLOAD = false;

    public static final int STATUS_IDLE = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_STOP = 2;
    public static final int STATUS_SUCCESS = 3;
    public static final int STATUS_ERROR = 4;

    private int mStatus = STATUS_IDLE;

    /**
     * 是否维护下载进度
     */
    private boolean mUpdateProgress;

    /**
     * 上一次进度
     */
    private int mLatestProgress;

    /**
     * 目标文件
     */
    private String mFile;

    /**
     * 下载管理器
     * 引入因为当下载完成之后，需要清除下载标记，如果不引入清除工作必须由用户显示的维护,
     * 这个工作最好也应该由Manager维护，而且自己能做到千万不要让别人做~~
     */
    private IDownloadManager mDownloadManager;

    @Override
    public void onCheck(int code) {
    }

    @Override
    public void onPrepared() {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStart(long total) {
    }

    @Override
    public void onProgress(long current, long total) {
    }

    @Override
    public void onProgress(int progress) {
    }

    @Override
    public void onError(int errorCode, Throwable t) {
        if (mDownloadManager != null && !StringUtils.isEmpty(mFile)) {
            mDownloadManager.updateTask(mFile);
        }
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onStop() {
        if (mDownloadManager != null && !StringUtils.isEmpty(mFile)) {
            mDownloadManager.updateTask(mFile);
        }
    }

    @Override
    public void onSuccess(T t) {
        if (mDownloadManager != null && !StringUtils.isEmpty(mFile)) {
            mDownloadManager.updateTask(mFile);
        }
    }

    /**
     * 保存文件
     *
     * @param responseBody 数据流
     * @param targetFile   目标文件
     * @param extraWords   加密关键字
     * @return 目标文件
     *
     * Created by lancy on 2019/6/3 22:55
     */
    public File saveFile(ResponseBody responseBody, File targetFile, String extraWords) throws Exception {
//        mFile = targetFile.getPath();
        mStatus = STATUS_RUNNING;

        long position = targetFile.length();
        long total = position + responseBody.contentLength();
        LogUtils.debug(TAG, "position = " + position + ", total = " + total);

        RandomAccessFile accessFile = null;
        BufferedInputStream bis = null;
        byte[] buf = new byte[1024 * 4];
        int len;
        try {
            accessFile = new RandomAccessFile(targetFile, "rw");
            accessFile.seek(position);
            bis = new BufferedInputStream(responseBody.byteStream());
            long count = position;
            // 写入加密串
            if (count == 0 && !TextUtils.isEmpty(extraWords)) {
                accessFile.write(extraWords.getBytes());
            }
            while ((len = bis.read(buf)) != -1) {
                while (mStatus == STATUS_STOP) {
                    bis.close();
                    accessFile.close();
                    LogUtils.warn(TAG, "stop download......");
                    return targetFile;
                }

                synchronized (accessFile) {
                    if (DEBUG_DOWNLOAD) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    accessFile.write(buf, 0, len);
                    count += len;

                    if (mUpdateProgress) {
                        onProgress(count, total);

                        int progress = ((int) (100 * count / total));
                        if (mLatestProgress != progress) {
                            mLatestProgress = progress;
                            onProgress(progress);
                        }
                    }
                }
            }
            accessFile.close();
            bis.close();
        } finally {
            if (accessFile != null) {
                accessFile.close();
            }

            if (bis != null) {
                bis.close();
            }
        }
        mStatus = STATUS_SUCCESS;

        return targetFile;
    }

    /**
     * 获取状态
     *
     * @return 当前状态
     *
     * Created by lancy on 2017/12/29 9:14
     */
    public int getStatus() {
        return mStatus;
    }

    /**
     * 停止下载
     *
     * Created by lancy on 2017/12/28 10:07
     */
    public void stop() {
        mStatus = STATUS_STOP;
    }

    /**
     * 设置是否更新下载进度
     *
     * @param updateProgress true: 更新下载进度, false: 不更新下载进度
     *
     *                       Created by lancy on 2017/12/28 10:08
     */
    public void setUpdateProgress(boolean updateProgress) {
        this.mUpdateProgress = updateProgress;
    }

    /**
     * 设置下载管理器
     *
     * @param manager 下载管理器
     *
     *                Created by lancy on 2019/6/17 0:56
     */
    public void setManager(IDownloadManager manager) {
        this.mDownloadManager = manager;
    }

    /**
     * 设置目标文件
     *
     * @param targetFile 目标文件
     */
    public void setTargetFile(String targetFile) {
        this.mFile = targetFile;
    }
}
