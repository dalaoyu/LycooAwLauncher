package com.lycoo.commons.entity;

import com.lycoo.commons.base.BaseResponse;

/**
 * 应用升级
 *
 * Created by lancy on 2018/1/5
 */
public class AppUpdateResponse extends BaseResponse {

    private AppUpdate data;

    public AppUpdate getData() {
        return data;
    }

    public void setData(AppUpdate data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AppUpdateResponse{" +
                "data=" + data +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
