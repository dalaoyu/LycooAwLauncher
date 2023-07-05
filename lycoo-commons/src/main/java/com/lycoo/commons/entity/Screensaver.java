package com.lycoo.commons.entity;

import java.util.List;

/**
 * 屏保信息
 *
 * Created by lancy on 2018/1/2
 */
public class Screensaver {
    /**
     * 名称
     */
    private String name;

    /**
     * 是否显示
     */
    private boolean show;

    /**
     * 切换间隔
     */
    private int period;

    /**
     * 时间戳
     */
    private String updateTime;

    private List<ScreensaverImage> images;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<ScreensaverImage> getImages() {
        return images;
    }

    public void setImages(List<ScreensaverImage> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Screensaver{" +
                "name='" + name + '\'' +
                ", show=" + show +
                ", period=" + period +
                ", updateTime='" + updateTime + '\'' +
                ", images=" + images +
                '}';
    }
}
