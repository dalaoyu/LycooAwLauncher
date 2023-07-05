package com.lycoo.commons.entity;

import com.lycoo.commons.base.BaseResponse;

/**
 * 授权结果
 *
 * Created by lancy on 2019/1/8
 */
public class DynamicAuthorityResponse extends BaseResponse {

    private DynamicAuthority data;

    public DynamicAuthority getData() {
        return data;
    }

    public void setData(DynamicAuthority data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DynamicAuthorityResponse{" +
                "data=" + data +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
