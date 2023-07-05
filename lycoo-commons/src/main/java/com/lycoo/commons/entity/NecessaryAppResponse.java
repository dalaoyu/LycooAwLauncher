package com.lycoo.commons.entity;

import com.lycoo.commons.base.BaseResponse;

import java.util.List;

/**
 * 白名单应用服务器返回信息
 *
 * Created by lancy on 2018/7/17
 */
public class NecessaryAppResponse extends BaseResponse {

    private List<NecessaryApp> data;

    public List<NecessaryApp> getData() {
        return data;
    }

    public void setData(List<NecessaryApp> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NecessaryAppResponse{" +
                "data=" + data +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
