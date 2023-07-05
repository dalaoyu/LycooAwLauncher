package com.lycoo.commons.entity;

import lombok.Data;

/**
 * 检查校验结果
 *
 * Created by lancy on 2019/11/16
 */
@Data
public class CheckResult {
    /**
     * 结果
     */
    private boolean ok;

    /**
     * 错误码
     */
    private int code;

    /**
     * 信息
     */
    private String message;

    public CheckResult(boolean ok) {
        this.ok = ok;
    }

    public CheckResult(boolean ok, int code) {
        this.ok = ok;
        this.code = code;
    }
}
