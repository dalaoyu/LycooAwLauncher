package com.lycoo.commons.entity;

import com.lycoo.commons.base.BaseResponse;

import java.util.List;

/**
 * 黑名单应用
 *
 * Created by lancy on 2018/7/17
 */
public class ProhibitiveAppResponse extends BaseResponse {

    private List<ProhibitiveApp> data;

    public List<ProhibitiveApp> getData() {
        return data;
    }

    public void setData(List<ProhibitiveApp> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ProhibitiveAppResponse{" +
                "data=" + data +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
