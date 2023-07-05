package com.lycoo.desktop.base;

import com.lycoo.desktop.bean.response.DesktopItemResponse;
import com.lycoo.desktop.qipo.QipoInfo;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * xxx
 *
 * Created by lancy on 2017/12/15
 */
public interface DesktopService {
//    String BASE_URL = "http://114.55.115.128/lycoocms/";
//    String BASE_URL = "http://other.21dtv.com/lycoocms/";
    String BASE_URL = "http://cms.yml688.com/lycoocms/";
    String BASE_URL_DEBUG = "http://192.168.1.137:8080/lycoocms/";

    /**
     * 获取坑位信息
     *
     * @param appKey       应用标识符
     * @param mac          mac地址
     * @param customerCode 客户码
     * @return DesktopItemResponse
     *
     * Created by lancy on 2017/12/16 18:11
     */
    @POST("client/desktop/item/update")
    @FormUrlEncoded
    Observable<DesktopItemResponse> getDesktopItemInfos(@Field("appKey") String appKey,
                                                        @Field("mac") String mac,
                                                        @Field("customerCode") String customerCode);

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
