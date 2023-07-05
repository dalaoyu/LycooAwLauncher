package com.lycoo.desktop.qiyi;

/**
 * 封装爱奇艺坑位信息
 *
 * Created by lancy on 2017/12/15
 */
public class QiyiItemInfo {

    private int qiyiType;
    private String qiyiData;

    public int getQiyiType() {
        return qiyiType;
    }

    public void setQiyiType(int qiyiType) {
        this.qiyiType = qiyiType;
    }

    public String getQiyiData() {
        return qiyiData;
    }

    public void setQiyiData(String qiyiData) {
        this.qiyiData = qiyiData;
    }

    @Override
    public String toString() {
        return "QiyiItemInfo{" +
                "qiyiType=" + qiyiType +
                ", qiyiData='" + qiyiData + '\'' +
                '}';
    }
}
