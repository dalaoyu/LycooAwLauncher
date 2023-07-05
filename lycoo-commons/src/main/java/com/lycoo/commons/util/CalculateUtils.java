package com.lycoo.commons.util;

import android.content.Context;

import java.util.Locale;

/**
 * 计算相关工具类
 *
 * Created by lancy on 2018/3/22
 */
public class CalculateUtils {

    /**
     * 查找最大值
     *
     * @param array 数组
     * @return 数组中的最大值
     *
     * Created by lancy on 2018/3/22 23:07
     */
    public static int findMax(int[] array) {
        int max = array[0];
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * Dp转Px
     *
     * @param context 上下文
     * @param dp      大小
     * @return dp转换的px
     *
     * Created by lancy on 2019/4/29 11:07
     */
    public static int Dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 格式化float
     *
     * @param value 数值
     * @return 格式化后的float
     *
     * Created by lancy on 2019/4/29 11:08
     */
    public static String formatFloat(float value) {
        return String.format(Locale.getDefault(), "%.3f", value);
    }
}
