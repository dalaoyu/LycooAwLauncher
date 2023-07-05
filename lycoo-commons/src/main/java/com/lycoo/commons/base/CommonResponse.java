package com.lycoo.commons.base;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 服务器响应信息
 *
 * Created by lancy on 2019/7/28
 */
@Data
public class CommonResponse<T> {

    /**
     * 响应状态码
     * 1: ok
     * 0: error
     */
    @SerializedName("statusCode")
    private Integer statusCode;

    /**
     * 响应消息
     * 主要显示错误消息， ok的时候为""
     */
    @SerializedName("message")
    private String message;

    /**
     * 响应数据
     */
    @SerializedName("data")
    private T data;
}
