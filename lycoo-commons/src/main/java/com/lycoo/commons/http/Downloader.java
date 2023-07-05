package com.lycoo.commons.http;

import android.content.Context;
import android.os.StatFs;
import android.text.TextUtils;

import com.lycoo.commons.domain.ErrorCode;
import com.lycoo.commons.util.LogUtils;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 下载器
 *
 * Created by lancy on 2018/3/31
 */
public class Downloader {
    private static final String TAG = Downloader.class.getSimpleName();

    /**
     * 下载
     * 不支持断点续传，每次下载会删除之前的文件
     *
     * @param context          上下文
     * @param url              下载地址
     * @param file             目标文件
     * @param downloadCallBack 下载回调
     *
     *                         Created by lancy on 2018/3/31 17:15
     */
    public static void doDownload(Context context, String url, File file, String statFsPath, String encryptWords, DownloadCallBack<File> downloadCallBack) {
        HttpHelper
                .getInstance(context)
                .getService(DownloadService.class)
                .download(url)
                .subscribeOn(Schedulers.io())
                .map(responseBody -> {
                    if (file.exists()) {
                        boolean deleted = file.delete();
                        LogUtils.info(TAG, "[ " + file.getPath() + " ] already exists, so delete : " + deleted);
                        file.createNewFile();
                    }
                    return downloadCallBack.saveFile(responseBody, file, encryptWords);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DownloadObserver<>(downloadCallBack));
    }


    /**
     * 下载
     * 支持断点续传
     *
     * @param context          上下文
     * @param url              下载地址
     * @param file             目标文件
     * @param statFsPath       要检查的挂载路径， 例如/cache, /system， 该参数主要用于检查目标分区是否有足够的空间下载文件
     * @param encryptWords     加密字符串
     * @param downloadCallBack 下载回调
     *
     *                         Created by lancy on 2018/3/31 17:14
     */
    public static void doDownloadAutoResume(Context context, String url, File file, String statFsPath, Long thresholdSpace, String encryptWords, DownloadCallBack<File> downloadCallBack) {
        HttpHelper
                .getInstance(context)
                .getService(DownloadService.class)
                .getFileSize(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(responseBody -> {
                    LogUtils.debug(TAG, "start download, contentLength = " + responseBody.contentLength() + ", file length = " + file.length());
                    // 如果statFsPath不为空，则检查分区是否有空间下载文件
                    if (!TextUtils.isEmpty(statFsPath)) {
                        long requestSpace = file.exists() ? responseBody.contentLength() - file.length() : responseBody.contentLength();
                        long availableSpace = new StatFs(statFsPath).getAvailableBytes() - (thresholdSpace == null ? 0 : thresholdSpace);
                        LogUtils.debug(TAG, "requestSpace = " + requestSpace + ", availableSpace = " + availableSpace);
                        if (requestSpace > availableSpace) {
                            downloadCallBack.onError(ErrorCode.NO_ENOUGH_SPACE, new Throwable(statFsPath + " has no enough space......"));
                            return false;
                        }
                    }

                    // 有些下载在真正下载前需要知道下载文件的大小，这里回调
                    downloadCallBack.onStart(responseBody.contentLength() + encryptWords.length());

                    // 已下载文件大小 == contentLength， 说明文件已经下载成功，直接回调onSuccess()
                    if (file.exists() && file.length() == responseBody.contentLength() + encryptWords.length()) {
                        downloadCallBack.onSuccess(file);
                        return false;
                    }

                    return true;
                })
                .observeOn(Schedulers.io())
                .flatMap(responseBody -> {
                    // 已下载文件大小 > contentLength + encryptWords.length(), 说明已下载文件有问题，删除重新下载
                    if (file.exists() && file.length() > responseBody.contentLength() + encryptWords.length()) {
                        boolean deleted = file.delete();
                        LogUtils.info(TAG, "delete invalid file : " + file + " : " + deleted);
                        file.createNewFile();
                    }

                    long start = file.length() - encryptWords.length();
                    if (start < 0) {
                        start = 0;
                    }
                    LogUtils.debug(TAG, "resume download, start = " + start);
                    return HttpHelper
                            .getInstance(context)
                            .getService(DownloadService.class)
                            .download("bytes=" + start + "-", url);
                })
                .map(responseBody -> downloadCallBack.saveFile(responseBody, file, encryptWords))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DownloadObserver<>(downloadCallBack));
    }
}
