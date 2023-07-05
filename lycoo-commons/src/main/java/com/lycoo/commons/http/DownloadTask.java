package com.lycoo.commons.http;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 下载任务
 *
 * Created by lancy on 2018/7/23
 */
@Getter
@Setter
@ToString
public class DownloadTask {
    /**
     * 下载地址
     */
    private String url;

    /**
     * 目标文件
     */
    private File file;

    /**
     * 下载分区
     */
    private String statFsPath;

    /**
     * 下载分区阀值
     * 如果剩余空间小于thresholdSpace， 则不容许下载， 单位为byte
     */
    private Long thresholdSpace;

    /**
     * 加密关键字
     */
    private String encryptWords;

    /**
     * 回调
     */
    private DownloadCallBack<File> downloadCallBack;

    /**
     * 断点续传
     */
    private boolean autoResume;

    public DownloadTask() {
    }

    public DownloadTask(String url,
                        File file,
                        String statFsPath,
                        Long thresholdSpace,
                        String encryptWords,
                        boolean autoResume,
                        DownloadCallBack<File> downloadCallBack) {
        this.url = url;
        this.file = file;
        this.statFsPath = statFsPath == null ? "" : statFsPath;
        this.thresholdSpace = thresholdSpace;
        this.encryptWords = encryptWords == null ? "" : encryptWords;
        this.downloadCallBack = downloadCallBack;
        this.autoResume = autoResume;
    }
}
