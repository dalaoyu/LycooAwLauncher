package com.lycoo.commons.http;

import com.lycoo.commons.entity.CheckResult;

/**
 * Created by lancy on 2018/4/3
 */
public interface IDownloadManager {

    /**
     * 设置允许同时下载最大任务数
     *
     * @param count 允许同时下载最大任务数
     *              Created by lancy on 2019/6/3 23:11
     */
    IDownloadManager setMaxDownloadingTaskCount(int count);

    /**
     * 设置最大任务数
     * 总任务数 = “下载队列”任务数 + “等待队列”任务数
     * 如果 总任务数 >= mMaxTaskCount, 则不允许添加新的任务
     *
     * @param count 最大任务数
     *
     *              Created by lancy on 2019/11/16 15:40
     */
    IDownloadManager setMaxTaskCount(int count);

    /**
     * 下载
     *
     * @param downloadTask 下载任务
     *
     *                     Created by lancy on 2018/7/24 0:10
     */
    void download(DownloadTask downloadTask);

    /**
     * 停止下载
     *
     * @param file 目标文件（作为下载的任务的标识）
     *
     *             Created by lancy on 2018/4/3 15:53
     */
    void stopDownload(String file);

    /**
     * 查询任务的下载状态
     *
     * @return 任务的状态
     *
     * Created by lancy on 2019/6/3 23:05
     */
    Integer getTaskStatus(String file);

    /**
     * 检查任务
     * 1. 检查网络
     * 2. 当前任务数是否已经达到最大值
     * 3. 目标任务是否已存在“下载队列”或者“等待队列”中
     *
     * @param file 目标文件
     * @return 检查结果
     *
     * Created by lancy on 2019/6/3 23:07
     */
    CheckResult checkTask(String file);

    /**
     * 更新任务列表
     *
     * -- 下载完成
     * -- 下载出错
     * -- 停止下载
     * 以上几种情况发生都需要调用updateDownloadTask()更新DownloadManager的任务列表
     *
     * 1. 移除已下载任务， 下载完成之后，无论成功与否都要移除下载任务
     * 2. 下载已排队的任务
     *
     * @param file 目标文件（作为下载的任务的标识）
     *
     *             Created by lancy on 2018/7/24 0:09
     */
    void updateTask(String file);


}
