package com.lycoo.commons.entity;

/**
 * 升级信息
 *
 * Created by lancy on 2018/1/5
 */
public class AppUpdate {
    private String name;
    private String url;
    private String md5;
    private Version version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "AppUpdate{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", md5='" + md5 + '\'' +
                ", version=" + version +
                '}';
    }
}
