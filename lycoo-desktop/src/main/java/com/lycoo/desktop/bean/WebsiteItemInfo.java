package com.lycoo.desktop.bean;

/**
 * 封装网络站点坑位信息
 *
 * Created by lancy on 2017/12/15
 */
public class WebsiteItemInfo {

    private String websiteUrl;

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    @Override
    public String toString() {
        return "WebsiteItemInfo{" +
                "websiteUrl='" + websiteUrl + '\'' +
                '}';
    }
}
