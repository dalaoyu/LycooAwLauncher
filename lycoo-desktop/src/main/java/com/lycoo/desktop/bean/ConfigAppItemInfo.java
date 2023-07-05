package com.lycoo.desktop.bean;

/**
 * 封装可配置应用坑位信息
 *
 * Created by lancy on 2017/12/15
 */
public class ConfigAppItemInfo {

    private String packageName;
    private int appVersion;
    private String appUrl;
    private String appMd5;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAppMd5() {
        return appMd5;
    }

    public void setAppMd5(String appMd5) {
        this.appMd5 = appMd5;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    @Override
    public String toString() {
        return "ConfigAppItemInfo{" +
                "packageName='" + packageName + '\'' +
                ", appUrl='" + appUrl + '\'' +
                ", appMd5='" + appMd5 + '\'' +
                '}';
    }
}
