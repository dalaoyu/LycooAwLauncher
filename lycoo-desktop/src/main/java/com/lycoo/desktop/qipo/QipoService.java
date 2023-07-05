package com.lycoo.desktop.qipo;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * xxx
 *
 * Created by lancy on 2017/12/19
 */

public interface QipoService {
    /**
     * 查询应用信息
     *
     * @param channel     厂商标识符
     * @param packageName 应用包名
     * @param ip          本机ip
     * @return QipoInfo
     *
     * Created by lancy on 2017/12/16 18:12
     */
    @GET("search")
    Observable<QipoInfo> getAppFromQipo(@Query("channel") String channel,
                                        @Query("package") String packageName,
                                        @Query("host_ip") String ip);
}
