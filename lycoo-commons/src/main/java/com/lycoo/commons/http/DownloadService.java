package com.lycoo.commons.http;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * xxx
 *
 * Created by lancy on 2017/12/19
 */
public interface DownloadService {

    String BASE_URL_DEBUG = "http://www.baidu.com";
    String BASE_URL = "http://www.baidu.com";

    /**
     * 查询文件大小
     *
     * @param url 文件地址
     * @return 文件大小
     *
     * Created by lancy on 2017/12/29 18:15
     */
    @Streaming
    @GET
    Observable<ResponseBody> getFileSize(@Url String url);

    /**
     * 文件下载
     *
     * @param url 下载地址
     * @return 下载结果
     *
     * Created by lancy on 2017/12/19 15:13
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);

    /**
     * 文件下载,支持断点功能
     *
     * @param start 开始位置
     * @param url   下载地址
     * @return 下载结果
     *
     * Created by lancy on 2017/12/19 15:13
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Header("RANGE") String start, @Url String url);
}
