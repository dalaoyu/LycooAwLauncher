package com.lycoo.commons.event;

/**
 * 公共事件
 * <p>
 * Created by lancy on 2018/4/20
 */
public class CommonEvent {

    /**
     * 更新主题颜色事件
     * <p>
     * Created by lancy on 2018/4/20 17:53
     */
    public static class UpdateStyleColorEvent {
        private int mStyleColor;

        public UpdateStyleColorEvent(int styleColor) {
            this.mStyleColor = styleColor;
        }

        public int getStyleColor() {
            return mStyleColor;
        }
    }

    public static class HideNavigationBarEvent {

    }
}
