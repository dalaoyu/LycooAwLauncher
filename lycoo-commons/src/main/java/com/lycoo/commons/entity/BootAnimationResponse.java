package com.lycoo.commons.entity;

import com.lycoo.commons.base.BaseResponse;

/**
 * 开机动画
 *
 * Created by lancy on 2018/7/17
 */
public class BootAnimationResponse extends BaseResponse {

    private BootAnimation data;

    public BootAnimation getData() {
        return data;
    }

    @Override
    public String toString() {
        return "BootAnimationResponse{" +
                "data=" + data +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
