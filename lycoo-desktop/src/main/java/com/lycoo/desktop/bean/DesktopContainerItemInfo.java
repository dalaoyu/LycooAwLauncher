package com.lycoo.desktop.bean;

/**
 * 容器坑位信息
 * 例如 "中国象棋"， 那么它的 containerType == GAME_CONTAINER
 *
 * Created by lancy on 2018/6/18
 */
public class DesktopContainerItemInfo {

    private int id;

    /**
     * 类型
     */
    private int containerType;

    /**
     * 包名
     */
    private String packageName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContainerType() {
        return containerType;
    }

    public void setContainerType(int containerType) {
        this.containerType = containerType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "DesktopContainerItemInfo{" +
                "id=" + id +
                ", containerType=" + containerType +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
