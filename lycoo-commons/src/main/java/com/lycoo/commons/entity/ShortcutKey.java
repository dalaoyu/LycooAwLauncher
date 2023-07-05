package com.lycoo.commons.entity;

/**
 * xxx
 *
 * Created by lancy on 2017/6/22
 */

public class ShortcutKey {

    /**
     * 类型
     */
    private Integer type;

    /**
     * 应用包名
     */
    private String packageName;

    /**
     * 参数1
     */
    private String param1;

    /**
     * 参数2
     */
    private String param2;

    /**
     * 参数3
     */
    private String param3;

    /**
     * 时间戳
     */
    private String updateTime;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
