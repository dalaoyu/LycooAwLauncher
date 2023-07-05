package com.lycoo.desktop.bean;

/**
 * 封装桌面坑位更新返回信息
 *
 * Created by lancy on 2017/12/15
 */
public class CommonDesktopItemInfo {
    private String tag;
    private int type;
    private String label;
    private String imageUrl;
    private String iconUrl;
    private boolean iconVisible;
    private String updateTime;

    private ConfigAppItemInfo configAppItem;
    private WebsiteItemInfo websiteItem;
    private SpecializedPageItemInfo specializedPageItem;
    private SpecializedAppItemInfo specializedAppItem;
    private ContainerItemInfo containerItem;
    private QiyiItemInfo qiyiItem;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
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

    public ConfigAppItemInfo getConfigAppItem() {
        return configAppItem;
    }

    public void setConfigAppItem(ConfigAppItemInfo configAppItem) {
        this.configAppItem = configAppItem;
    }

    public WebsiteItemInfo getWebsiteItem() {
        return websiteItem;
    }

    public void setWebsiteItem(WebsiteItemInfo websiteItem) {
        this.websiteItem = websiteItem;
    }

    public SpecializedPageItemInfo getSpecializedPageItem() {
        return specializedPageItem;
    }

    public void setSpecializedPageItem(SpecializedPageItemInfo specializedPageItem) {
        this.specializedPageItem = specializedPageItem;
    }

    public SpecializedAppItemInfo getSpecializedAppItem() {
        return specializedAppItem;
    }

    public void setSpecializedAppItem(SpecializedAppItemInfo specializedAppItem) {
        this.specializedAppItem = specializedAppItem;
    }

    public ContainerItemInfo getContainerItem() {
        return containerItem;
    }

    public void setContainerItem(ContainerItemInfo containerItem) {
        this.containerItem = containerItem;
    }

    public QiyiItemInfo getQiyiItem() {
        return qiyiItem;
    }

    public void setQiyiItem(QiyiItemInfo qiyiItem) {
        this.qiyiItem = qiyiItem;
    }

    @Override
    public String toString() {
        return "CommonDesktopItemInfo{" +
                "tag='" + tag + '\'' +
                ", type=" + type +
                ", label='" + label + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", configAppItem=" + configAppItem +
                ", websiteItem=" + websiteItem +
                ", specializedPageItem=" + specializedPageItem +
                ", specializedAppItem=" + specializedAppItem +
                ", containerItem=" + containerItem +
                ", qiyiItem=" + qiyiItem +
                '}';
    }
}
