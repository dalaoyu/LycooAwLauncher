package com.lycoo.commons.entity;

/**
 * xxx
 *
 * Created by lancy on 2017/6/16
 */

public class NecessaryApp {

    /**
     * 应用名称
     */
    private String label;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 下载地址
     */
    private String url;

    /**
     * MD5
     */
    private String md5;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
        return "NecessaryApp{" +
                "label='" + label + '\'' +
                ", packageName='" + packageName + '\'' +
                ", url='" + url + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
