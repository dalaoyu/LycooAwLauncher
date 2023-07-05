package com.lycoo.commons.entity;

/**
 * 授权信息
 *
 * Created by lancy on 2019/1/8
 */
public class DynamicAuthority {
    private String mac;
    private String authorizationCode;
    private String dynamicCode;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getDynamicCode() {
        return dynamicCode;
    }

    public void setDynamicCode(String dynamicCode) {
        this.dynamicCode = dynamicCode;
    }

    @Override
    public String toString() {
        return "DynamicAuthority{" +
                "mac='" + mac + '\'' +
                ", authorizationCode='" + authorizationCode + '\'' +
                ", dynamicCode='" + dynamicCode + '\'' +
                '}';
    }
}
