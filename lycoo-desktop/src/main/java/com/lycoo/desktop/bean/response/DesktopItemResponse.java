package com.lycoo.desktop.bean.response;

import com.lycoo.commons.base.BaseResponse;
import com.lycoo.desktop.bean.CommonDesktopItemInfo;

import java.util.List;

/**
 * 封装桌面坑位更新返回结果
 *
 * Created by lancy on 2017/12/15
 */
public class DesktopItemResponse extends BaseResponse {

    private List<CommonDesktopItemInfo> data;

    public List<CommonDesktopItemInfo> getData() {
        return data;
    }

    public void setData(List<CommonDesktopItemInfo> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DesktopItemResponse{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
