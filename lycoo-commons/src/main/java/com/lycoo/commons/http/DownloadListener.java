package com.lycoo.commons.http;

/**
 * xxx
 *
 * Created by lancy on 2017/12/19
 */
public interface DownloadListener<T> {

    /**
     * 检查
     * 在开始准备下载之前，检查下载任务和环境
     * 1. 网络是否OK
     * 2. 任务是否重复
     *
     * @param code 错误码
     *
     *             Created by lancy on 2019/6/3 22:43
     */
    void onCheck(int code);

    /**
     * 准备完毕
     * 任务加入下载队列或者等待队列
     *
     * Created by lancy on 2019/6/3 22:45
     */
    void onPrepared();

    /**
     * 准备下载
     *
     * Created by lancy on 2018/7/18 16:34
     */
    void onStart();

    /**
     * 准备下载(只在允许断点续传的情况下调用)
     *
     * @param total 下载文件的总大小
     *
     *              Created by lancy on 2018/7/18 16:34
     */
    void onStart(long total);

    /**
     * 更新进度
     *
     * @param current 已下载文件大小
     * @param total   文件总大小
     *
     *                Created by lancy on 2019/6/3 22:45
     */
    void onProgress(long current, long total);

    /**
     * 更新进度
     *
     * @param progress 已下载百分比
     *
     *                 Created by lancy on 2019/6/3 22:45
     */
    void onProgress(int progress);

    /**
     * 下载出错
     *
     * @param t 异常
     *
     *          Created by lancy on 2019/6/3 22:46
     */
    void onError(int errorCode, Throwable t);

    /**
     * 取消下载
     * 停止的任务在“等待队列”中， 停止下载时调用onCancel()
     *
     * Created by lancy on 2019/6/3 22:48
     */
    void onCancel();

    /**
     * 停止下载
     * 停止的任务在“下载队列”，停止下载时调用onStop()
     *
     * Created by lancy on 2019/6/3 22:46
     */
    void onStop();

    /**
     * 下载成功
     *
     * @param t 任务
     *
     *          Created by lancy on 2019/6/3 22:49
     */
    void onSuccess(T t);
}
