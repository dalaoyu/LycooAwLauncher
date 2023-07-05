package com.lycoo.commons.helper;

import android.content.Context;
import android.graphics.Typeface;

import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.event.CommonEvent;

/**
 * xxx
 *
 * Created by lancy on 2017/12/12
 */
public class StyleManager {
    private int mStyleColor = -1;
    private Typeface mTypeface;
    private static StyleManager mInstance;
    private Context mContext;

    public StyleManager(Context context) {
        mContext = context;
    }

    public static StyleManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (StyleManager.class) {
                if (mInstance == null) {
                    mInstance = new StyleManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取默认字体样式
     *
     * @return 默认字体样式
     *
     * Created by lancy on 2018/4/20 18:21
     */
    public Typeface getTypeface() {
        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(mContext.getAssets(), CommonConstants.CUSTOM_FONT_FLFBLS);
        }
        return mTypeface;
    }

    /**
     * 获取当前主题颜色
     *
     * @return 当前主题颜色
     *
     * Created by lancy on 2018/4/20 18:20
     */
    public int getStyleColor() {
        if (mStyleColor == -1) {
            mStyleColor = mContext
                    .getSharedPreferences(CommonConstants.SP_COMMON, Context.MODE_PRIVATE)
                    .getInt(CommonConstants.STYLE_COLOR, CommonConstants.STYLE_COLOR_DEFAULT);
        }
        return mStyleColor;
    }

    /**
     * 设置主题颜色
     *
     * @param styleColor 目标主题颜色
     *
     *                   Created by lancy on 2018/4/20 18:20
     */
    public void setStyleColor(int styleColor) {
        if (styleColor < 0 || styleColor > CommonConstants.STYLE_COLOR_COUNT - 1) {
            return;
        }

        mStyleColor = styleColor;
        mContext.getSharedPreferences(CommonConstants.SP_COMMON, Context.MODE_PRIVATE)
                .edit()
                .putInt(CommonConstants.STYLE_COLOR, styleColor)
                .apply();
        // 发送应用样式通知
        RxBus.getInstance().post(new CommonEvent.UpdateStyleColorEvent(styleColor));
    }

    /**
     * 应用主题颜色
     *
     * @param root 根布局
     *
     *             Created by lancy on 2018/4/20 18:21
     */
//    public void applyStyleColor(ViewGroup root) {
//        int count = root.getChildCount();
//        if (count > 0) {
//            for (int i = 0; i < count; i++) {
//                View child = root.getChildAt(i);
//                if (child.getTag() != null && child.getTag().equals(CommonConstants.STYLE_COLOR_TAG)) {
//                    switch (getStyleColor()) {
//                        case CommonConstants.STYLE_COLOR_DEFAULT:
//                        default:
//                            if (child instanceof ImageView) {
//                                child.setBackgroundResource(R.drawable.common_bg_icon);
//                            } else if (child instanceof Button) {
//                                child.setBackgroundResource(R.drawable.common_bg_button);
//                            } else if (child instanceof RadioButton) {
//                                child.setBackgroundResource(R.drawable.common_bg_list_item);
//                            } else if (child instanceof ListView) {
//                                ((ListView) child).setSelector(R.drawable.common_list_selector);
//                            }
//                            break;
//                        case CommonConstants.STYLE_COLOR_ORANGE:
//                            if (child instanceof ImageView) {
//                                child.setBackgroundResource(R.drawable.common_bg_icon_orange);
//                            } else if (child instanceof Button) {
//                                child.setBackgroundResource(R.drawable.common_bg_button_orange);
//                            } else if (child instanceof RadioButton) {
//                                child.setBackgroundResource(R.drawable.common_bg_list_item_orange);
//                            } else if (child instanceof ListView) {
//                                ((ListView) child).setSelector(R.drawable.common_list_selector_orange);
//                            }
//                            break;
//                        case CommonConstants.STYLE_COLOR_GREEN:
//                            if (child instanceof ImageView) {
//                                child.setBackgroundResource(R.drawable.common_bg_icon_green);
//                            } else if (child instanceof Button) {
//                                child.setBackgroundResource(R.drawable.common_bg_button_green);
//                            } else if (child instanceof RadioButton) {
//                                child.setBackgroundResource(R.drawable.common_bg_list_item_green);
//                            } else if (child instanceof ListView) {
//                                ((ListView) child).setSelector(R.drawable.common_list_selector_green);
//                            }
//                            break;
//                        case CommonConstants.STYLE_COLOR_BLUE:
//                            if (child instanceof ImageView) {
//                                child.setBackgroundResource(R.drawable.common_bg_icon_blue);
//                            } else if (child instanceof Button) {
//                                child.setBackgroundResource(R.drawable.common_bg_button_blue);
//                            } else if (child instanceof RadioButton) {
//                                child.setBackgroundResource(R.drawable.common_bg_list_item_blue);
//                            } else if (child instanceof ListView) {
//                                ((ListView) child).setSelector(R.drawable.common_list_selector_blue);
//                            }
//                            break;
//                    }
//                }
//
//                if (child instanceof ViewGroup) {
//                    applyStyleColor((ViewGroup) child);
//                }
//            }
//        }
//    }
}
