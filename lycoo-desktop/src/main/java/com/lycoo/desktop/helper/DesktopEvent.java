package com.lycoo.desktop.helper;

import android.content.pm.ResolveInfo;

import com.lycoo.desktop.bean.DesktopItemInfo;

import java.util.List;

/**
 * 应用事件容器
 *
 * Created by lancy on 2017/12/16
 */
public class DesktopEvent {

    public static class UpdateDesktopItemEvent {
        private List<DesktopItemInfo> mItemInfos;

        public UpdateDesktopItemEvent(List<DesktopItemInfo> itemInfos) {
            mItemInfos = itemInfos;
        }

        public List<DesktopItemInfo> getItemInfos() {
            return mItemInfos;
        }
    }

    /**
     * 移除应用显示事件
     * 使用场景：
     * 1. 当容器子坑位被移除后， 通知界面更新
     */
    public static class RemoveAppEvent {
        private String mPackageName;

        public RemoveAppEvent(String packageName) {
            this.mPackageName = packageName;
        }

        public String getPackageName() {
            return mPackageName;
        }
    }

    /**
     * 添加应用事件
     * 使用场景：
     * 1.  当容器子坑位添加应用时， 更新界面
     */
    public static class AddAppEvent {
        private ResolveInfo mResolveInfo;

        public AddAppEvent(ResolveInfo resolveInfo) {
            this.mResolveInfo = resolveInfo;
        }

        public ResolveInfo getResolveInfo() {
            return mResolveInfo;
        }
    }
}
