package com.lycoo.commons.base;

import com.lycoo.commons.entity.AppUpdate;
import com.lycoo.commons.entity.BootAnimationResponse;
import com.lycoo.commons.entity.DynamicAuthorityResponse;
import com.lycoo.commons.entity.MarqueeResponse;
import com.lycoo.commons.entity.NecessaryAppResponse;
import com.lycoo.commons.entity.OtaUpdateResponse;
import com.lycoo.commons.entity.ProhibitiveAppResponse;
import com.lycoo.commons.entity.ScreensaverResponse;
import com.lycoo.commons.entity.ShortcutKeyResponse;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 基础服务接口定义
 *
 * Created by lancy on 2017/12/15
 */
public interface BaseService {
    // String BASE_URL = "http://114.55.115.128/lycoocms/";
    // String BASE_URL = "http://other.21dtv.com/lycoocms/";
    String BASE_URL = "http://cms.yml688.com/lycoocms/";
    String BASE_URL_DEBUG = "http://192.168.1.137:8080/lycoocms/";


    /**
     * 查询升级信息
     *
     * @param appKey       应用标识
     * @param versionName  版本号
     * @param mac          mac地址
     * @param customerCode 客户编码
     * @return 应用升级信息
     *
     * Created by lancy on 2018/7/24 4:15
     */
    @POST("client/app/update")
    @FormUrlEncoded
    Observable<CommonResponse<AppUpdate>> getAppUpdateInfo(@Field("appKey") String appKey,
                                                           @Field("versionName") String versionName,
                                                           @Field("mac") String mac,
                                                           @Field("customerCode") String customerCode);

    /**
     * 获取跑马灯信息
     *
     * @param appKey       应用标识符
     * @param mac          mac地址
     * @param customerCode 客户码
     * @return 跑马灯信息
     *
     * Created by lancy on 2018/1/2 21:49
     */
    @POST("client/marquee/update")
    @FormUrlEncoded
    Observable<MarqueeResponse> getMarqueeInfo(@Field("appKey") String appKey,
                                               @Field("mac") String mac,
                                               @Field("customerCode") String customerCode);

    /**
     * 获取屏保信息
     *
     * @param appKey       应用标识符
     * @param mac          mac地址
     * @param customerCode 客户码
     * @return 屏保信息
     *
     * Created by lancy on 2018/1/2 21:49
     */
    @POST("client/screensaver/update")
    @FormUrlEncoded
    Observable<ScreensaverResponse> getScreensaverInfo(@Field("appKey") String appKey,
                                                       @Field("mac") String mac,
                                                       @Field("customerCode") String customerCode);




    /**
     * 获取OTA升级包信息
     *
     * @param firmwareKey  固件标识符
     * @param versionName  固件版本名称
     * @param mac          mac地址
     * @param customerCode 客户编号
     * @return Ota升级信息
     *
     * Created by lancy on 2018/7/18 15:56
     */
    @POST("client/firmware/update")
    @FormUrlEncoded
    Observable<OtaUpdateResponse> getOtaUpdateInfo(@Field("firmwareKey") String firmwareKey,
                                                   @Field("versionName") String versionName,
                                                   @Field("mac") String mac,
                                                   @Field("customerCode") String customerCode);

    /**
     * 获取动画信息
     *
     * @param firmwareKey  固件标识符
     * @param mac          ac地址
     * @param customerCode 客户码
     * @return 动画信息
     *
     * Created by lancy on 2018/7/17 16:47
     */
    @POST("client/bootanimation/update")
    @FormUrlEncoded
    Observable<BootAnimationResponse> getBootAnimationInfo(@Field("firmwareKey") String firmwareKey,
                                                           @Field("mac") String mac,
                                                           @Field("customerCode") String customerCode);

    /**
     * 获取黑名单应用
     *
     * @param firmwareKey  固件标识符
     * @param mac          ac地址
     * @param customerCode 客户码
     * @return 黑名单应用列表信息
     *
     * Created by lancy on 2018/7/17 16:47
     */
    @POST("client/prohibitive_app/update")
    @FormUrlEncoded
    Observable<ProhibitiveAppResponse> getProhibitiveApps(@Field("firmwareKey") String firmwareKey,
                                                          @Field("mac") String mac,
                                                          @Field("customerCode") String customerCode);

    /**
     * 获取白名单应用
     *
     * @param firmwareKey  固件标识符
     * @param mac          mac地址
     * @param customerCode 客户码
     * @return 黑名单应用列表信息
     *
     * Created by lancy on 2018/7/17 16:47
     */
    @POST("client/necessary_app/update")
    @FormUrlEncoded
    Observable<NecessaryAppResponse> getNecessaryApps(@Field("firmwareKey") String firmwareKey,
                                                      @Field("mac") String mac,
                                                      @Field("customerCode") String customerCode);

    /**
     * 获取遥控器快捷键配置
     *
     * @param firmwareKey  固件标识符
     * @param mac          ac地址
     * @param customerCode 客户码
     * @return 遥控器快捷键配置
     *
     * Created by lancy on 2018/7/17 21:02
     */
    @POST("client/shortcut_key/update")
    @FormUrlEncoded
    Observable<ShortcutKeyResponse> getShortcutKeys(@Field("firmwareKey") String firmwareKey,
                                                    @Field("mac") String mac,
                                                    @Field("customerCode") String customerCode);

    /**
     * 获取授权结果
     *
     * @param mac               设备mac地址
     * @param authorizationCode 授权码
     * @param dynamicCode       授权编号（授权成功后服务端返回给客户端的授权编号，没有授权成功之前传000000000000）
     * @return 授权结果
     *
     * Created by lancy on 2019/1/8 16:30
     */
    @POST("authority/dynamic/authorize")
    @FormUrlEncoded
    Observable<DynamicAuthorityResponse> getAuthorization(@Field("mac") String mac,
                                                          @Field("authorizationCode") String authorizationCode,
                                                          @Field("dynamicCode") String dynamicCode);

}
