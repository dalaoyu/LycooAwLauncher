package com.lycoo.commons.entity;

/**
 * 黑名单应用信息
 *
 * Created by lancy on 2017/6/16
 */
public class ProhibitiveApp {

    private String label;
    private String packageName;

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

    @Override
    public String toString() {
        return "ProhibitiveApp{" +
                "label='" + label + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
