package com.lycoo.commons.util;

import android.view.View;

public class ViewUtils {

    /**
     * View是否可见
     *
     * @param view 目标View
     * @return true: 可见， false： 不可见
     *
     * Created by lancy on 2017/6/9 16:44
     */
    public static boolean isVisible(View view) {
        if (view == null){
            throw new RuntimeException("view is null");
        }

        return View.VISIBLE == view.getVisibility();
    }

    /**
     * 改变View显示状态
     *
     * @param show 状态, true: 可见， false： 不可见
     * @param view 目标View
     *
     *             Created by lancy on 2017/6/9 16:46
     */
    public static void setViewShown(boolean show, View view) {
        if (view == null){
            return;
        }

        if (show) {
            if (View.VISIBLE != view.getVisibility())
                view.setVisibility(View.VISIBLE);
        } else {
            if (View.GONE != view.getVisibility())
                view.setVisibility(View.GONE);
        }

    }

    public static void setViewShownInvisible(boolean show, View view) {
        if (view == null){
            return;
        }

        if (show) {
            if (View.VISIBLE != view.getVisibility())
                view.setVisibility(View.VISIBLE);
        } else {
            if (View.GONE != view.getVisibility())
                view.setVisibility(View.INVISIBLE);
        }

    }
}
