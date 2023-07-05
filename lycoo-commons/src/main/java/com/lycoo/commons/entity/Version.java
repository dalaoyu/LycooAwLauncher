package com.lycoo.commons.entity;

/**
 * 版本信息
 *
 * Created by lancy on 2018/7/18
 */
public class Version {

    private String name;
    private int code;
    private String majorUpdate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMajorUpdate() {
        return majorUpdate;
    }

    public void setMajorUpdate(String majorUpdate) {
        this.majorUpdate = majorUpdate;
    }

    @Override
    public String toString() {
        return "Version{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", majorUpdate='" + majorUpdate + '\'' +
                '}';
    }
}
