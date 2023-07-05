package com.lycoo.commons.entity;

/**
 * 屏保图片信息
 *
 * Created by lancy on 2018/1/2
 */
public class ScreensaverImage {
    private String name;
    private String url;
    private String md5;
    private long size;
    private String createDate;

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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "ScreensaverImage{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", md5='" + md5 + '\'' +
                ", size=" + size +
                ", createDate='" + createDate + '\'' +
                '}';
    }
}
