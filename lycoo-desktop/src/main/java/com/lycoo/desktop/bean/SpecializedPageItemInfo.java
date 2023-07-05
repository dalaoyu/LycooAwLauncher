package com.lycoo.desktop.bean;

/**
 * 封装固定页面坑位信息
 * 代表当前应用的某一个页面
 *
 * Created by lancy on 2017/12/15
 */
public class SpecializedPageItemInfo {

    private String simpleClassName;
    private String param1;
    private String param2;
    private String param3;

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
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

    @Override
    public String toString() {
        return "SpecializedPageItemInfo{" +
                "simpleClassName='" + simpleClassName + '\'' +
                ", param1='" + param1 + '\'' +
                ", param2='" + param2 + '\'' +
                ", param3='" + param3 + '\'' +
                '}';
    }
}
