package com.lycoo.desktop.base;

import android.os.Parcel;
import android.os.Parcelable;

import com.lycoo.desktop.bean.DesktopItemInfo;

public class BaseBean implements Parcelable {


    private int id;

    /**
     * 坑位唯一标识符
     */
    private int tag;

    /**
     * 类型
     */
    private int type;

    /**
     * 名称
     */
    private String label;

    /**
     * 背景
     */
    private String imageUrl;

    /**
     * 图标
     */
    private String iconUrl;

    /**
     * 是否显示坑位图标
     */
    private boolean iconVisible;

    /**
     * 更新时间戳
     */
    private String updateTime;

    // ==================================================================================

    /**
     * 包名
     */
    private String packageName;

    /**
     * 页面类名
     */
    private String className;

    /**
     * An action name, such as ACTION_VIEW.  Application-specific
     * actions should be prefixed with the vendor's package name.
     */
    private String action;


    /**
     * 应用版本
     */
    private int appVersion;
    /**
     * 应用地址
     */
    private String appUrl;

    /**
     * 应用MD5
     */
    private String appMd5;

    /**
     * 网络地址
     */
    private String websiteUrl;

    /**
     * 爱奇艺类型数据
     */
    private String qiyiData;

    // 备用属性，当type为不同类型的时候，会有一些特殊的属性，使用下面定义进行封装
    private String param1;
    private String param2;
    private String param3;

    public BaseBean() {
    }


    protected BaseBean(Parcel in) {
        id = in.readInt();
        tag = in.readInt();
        type = in.readInt();
        label = in.readString();
        imageUrl = in.readString();
        iconUrl = in.readString();
        iconVisible = in.readByte() != 0;
        updateTime = in.readString();
        packageName = in.readString();
        className = in.readString();
        action = in.readString();
        appVersion = in.readInt();
        appUrl = in.readString();
        appMd5 = in.readString();
        websiteUrl = in.readString();
        qiyiData = in.readString();
        param1 = in.readString();
        param2 = in.readString();
        param3 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(tag);
        dest.writeInt(type);
        dest.writeString(label);
        dest.writeString(imageUrl);
        dest.writeString(iconUrl);
        dest.writeByte((byte) (iconVisible ? 1 : 0));
        dest.writeString(updateTime);
        dest.writeString(packageName);
        dest.writeString(className);
        dest.writeString(action);
        dest.writeInt(appVersion);
        dest.writeString(appUrl);
        dest.writeString(appMd5);
        dest.writeString(websiteUrl);
        dest.writeString(qiyiData);
        dest.writeString(param1);
        dest.writeString(param2);
        dest.writeString(param3);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseBean> CREATOR = new Creator<BaseBean>() {
        @Override
        public BaseBean createFromParcel(Parcel in) {
            return new BaseBean(in);
        }

        @Override
        public BaseBean[] newArray(int size) {
            return new BaseBean[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isIconVisible() {
        return iconVisible;
    }

    public void setIconVisible(boolean iconVisible) {
        this.iconVisible = iconVisible;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAppMd5() {
        return appMd5;
    }

    public void setAppMd5(String appMd5) {
        this.appMd5 = appMd5;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getQiyiData() {
        return qiyiData;
    }

    public void setQiyiData(String qiyiData) {
        this.qiyiData = qiyiData;
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

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }


    @Override
    public String toString() {
        return "DesktopItemInfo{" +
                "id=" + id +
                ", tag=" + tag +
                ", type=" + type +
                ", label='" + label + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", iconVisible=" + iconVisible +
                ", updateTime='" + updateTime + '\'' +



                ", packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", action='" + action + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", appUrl='" + appUrl + '\'' +
                ", appMd5='" + appMd5 + '\'' +
                ", websiteUrl='" + websiteUrl + '\'' +
                ", qiyiData='" + qiyiData + '\'' +
                ", param1='" + param1 + '\'' +
                ", param2='" + param2 + '\'' +
                ", param3='" + param3 + '\'' +
                '}';
    }

    public boolean isEmpty() {
        return id <= 0;
    }

    public boolean isPersistent() {
        return id > 0;
    }
}
