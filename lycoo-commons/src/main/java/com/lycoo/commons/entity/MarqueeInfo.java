package com.lycoo.commons.entity;

/**
 * 跑马灯
 *
 * Created by lancy on 2018/1/2
 */
public class MarqueeInfo {
    /**
     * 是否显示
     */
    private boolean show;

    /**
     * 名称
     */
    private String name;

    /**
     * 循环次数
     */
    private int count;

    /**
     * 循环间隔
     */
    private int period;

    /**
     * 内容
     */
    private String data;

    /**
     * 时间戳
     */
    private String updateTime;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "MarqueeInfo{" +
                "show=" + show +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", period=" + period +
                ", data='" + data + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
