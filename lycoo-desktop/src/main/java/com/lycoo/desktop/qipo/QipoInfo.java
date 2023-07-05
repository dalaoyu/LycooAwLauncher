package com.lycoo.desktop.qipo;

import com.google.gson.annotations.SerializedName;

/**
 * 奇珀市场查询应用返回结果
 *
 * Created by lancy on 2017/12/16
 */
public class QipoInfo {
    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("is_find")
    private int find;

    @SerializedName("error_reason")
    private String reason;

    @SerializedName("name")
    private String name;

    @SerializedName("package")
    private String packageName;

    @SerializedName("down_url")
    private String url;

    @SerializedName("apk_md5")
    private String md5;

    public int getFind() {
        return find;
    }

    public void setFind(int find) {
        this.find = find;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "QipoInfo{" +
                "statusCode=" + statusCode +
                ", find=" + find +
                ", reason='" + reason + '\'' +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", url='" + url + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
