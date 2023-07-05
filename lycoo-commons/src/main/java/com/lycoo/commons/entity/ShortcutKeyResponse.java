package com.lycoo.commons.entity;

import com.lycoo.commons.base.BaseResponse;

import java.util.List;

/**
 * 遥控器快捷键配置服务器信息
 *
 * Created by lancy on 2018/7/17
 */
public class ShortcutKeyResponse extends BaseResponse {

    private List<ShortcutKey> data;

    public List<ShortcutKey> getData() {
        return data;
    }

    public void setData(List<ShortcutKey> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ShortcutKeyResponse{" +
                "data=" + data +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
