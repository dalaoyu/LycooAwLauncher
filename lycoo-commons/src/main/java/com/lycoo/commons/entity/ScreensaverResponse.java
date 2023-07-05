package com.lycoo.commons.entity;

import com.lycoo.commons.base.BaseResponse;

/**
 * xxx
 *
 * Created by lancy on 2018/1/2
 */
public class ScreensaverResponse extends BaseResponse {

    private Screensaver data;

    public Screensaver getData() {
        return data;
    }

    public void setData(Screensaver data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ScreensaverResponse{" +
                "statusCode=" + statusCode +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
