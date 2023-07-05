package com.lycoo.commons.base;

/**
 * 基础Response
 *
 * Created by lancy on 2017/12/15
 */
public abstract class BaseResponse {
    /**
     * 响应状态
     * 1： success
     * 0: fail
     */
    protected int statusCode;

    /**
     * 响应消息，主要显示错误信息
     */
    protected String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
