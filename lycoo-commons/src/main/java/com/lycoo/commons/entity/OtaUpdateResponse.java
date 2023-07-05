package com.lycoo.commons.entity;

import com.lycoo.commons.base.BaseResponse;

/**
 * OTA升级包信息服务器返回
 *
 * Created by lancy on 2018/7/18
 */
public class OtaUpdateResponse extends BaseResponse {

    private OtaUpdate data;

    public OtaUpdate getData() {
        return data;
    }

    public void setData(OtaUpdate data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "OtaUpdateResponse{" +
                "data=" + data +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
