package com.lycoo.lancy.launcher.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.CustomAudioManager;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.util.SystemPropertiesUtils;
import com.lycoo.desktop.ui.DesktopItem;
import com.lycoo.desktop.ui.Page;
import com.lycoo.lancy.launcher.R;
import com.lycoo.lancy.launcher.config.Constants;

/**
 * Created by lancy on 2018/5/7
 */
public class PolymericPage extends Page {
    private static final String TAG = PolymericPage.class.getSimpleName();

    private static final int DEFAULT_SCALE_SIZE = 16;
    private CustomAudioManager mCustomAudioManager;

    public PolymericPage(Context context, String pageLabel, int pageNumber, int pageCount) {
        super(context, pageLabel, pageNumber, pageCount);
    }

    @Override
    protected void initItems() {
        // 创建Items
        int itemsMarginTop = getResources().getDimensionPixelSize(R.dimen.desktop_items_margin_top);
        int itemsMarginLeft = getResources().getDimensionPixelSize(R.dimen.desktop_items_margin_left);
        int itemsMarginRight = getResources().getDimensionPixelSize(R.dimen.desktop_items_margin_right);
        int itemHorizontalSpace = getResources().getDimensionPixelOffset(R.dimen.desktop_item_horizontal_space);
        int itemBigHorizontalSpace = getResources().getDimensionPixelOffset(R.dimen.desktop_item_big_horizontal_space);
        int itemVerticalSpace = getResources().getDimensionPixelSize(R.dimen.desktop_item_vertical_space);
        DesktopItem desktopItem;
        for (int i = 1; i <= mMaxTag - mMinTag + 1; i++) {
            desktopItem = new DesktopItem(mContext, true, true, true, false, true);
            desktopItem.iconWidth = mContext.getResources().getDimensionPixelSize(R.dimen.desktop_item_icon_width);
            desktopItem.iconHeight = mContext.getResources().getDimensionPixelSize(R.dimen.desktop_item_icon_height);
            desktopItem.overlayWidth = mContext.getResources().getDimensionPixelSize(R.dimen.desktop_item_overlay_width);
            desktopItem.overlayHeight = mContext.getResources().getDimensionPixelSize(R.dimen.desktop_item_overlay_height);
            desktopItem.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.desktop_item_corner_radius));
            if(Build.MODEL.contains(Constants.MODEL_FENWEI)){
                desktopItem.setLabelSize(getResources().getDimensionPixelSize(R.dimen.desktop_item_label_text_size_fenwei));
            }else{
                desktopItem.setLabelSize(getResources().getDimensionPixelSize(R.dimen.desktop_item_label_text_size));
            }
            desktopItem.setId(i + mPageNumber);
            desktopItem.setTag(i + mPageNumber);
            desktopItem.setFocusable(true);
            desktopItem.setFocusableInTouchMode(false);

            if (i == 1 || i == 4 || i == 5 || i == 6) {
                desktopItem.topMargin = itemsMarginTop;
                desktopItem.belowId = View.NO_ID;
                desktopItem.nextFocusUpId = mPageNumber;
            } else {
                if (i == 2 || i == 3) {
                    desktopItem.belowId = i - 1 + mPageNumber;
                } else {
                    desktopItem.belowId = 3 + mPageNumber;
                }
                desktopItem.topMargin = itemVerticalSpace;
                desktopItem.nextFocusUpId = View.NO_ID;
            }

            if (i == 6 || i == 12) {
                desktopItem.rightMargin = itemsMarginRight;
                desktopItem.nextFocusRightId = i + mPageNumber;
            }

            if (i == 4) {
                desktopItem.nextFocusLeftId = 1 + mPageNumber;
            }

            if (i <= 3 || i == 7) {
                desktopItem.leftMargin = itemsMarginLeft;
                desktopItem.nextFocusLeftId = i + mPageNumber;
                desktopItem.rightOfId = View.NO_ID;
            } else {
                if (i == 4 || i == 8) {
                    desktopItem.leftMargin = itemBigHorizontalSpace;
                } else {
                    desktopItem.leftMargin = itemHorizontalSpace;
                }
                desktopItem.rightOfId = i - 1 + mPageNumber;
            }

            if (i >= 4 && i <= 6) {
                desktopItem.width = mContext.getResources().getDimensionPixelSize(R.dimen.big_vertical_item_width);
                desktopItem.height = mContext.getResources().getDimensionPixelSize(R.dimen.big_vertical_item_height);
                // for big vertical
                desktopItem.setLabelBottomMargin(mContext.getResources().getDimensionPixelSize(R.dimen.big_vertical_item_label_margin_bottom));
                if(Build.MODEL.contains(Constants.MODEL_FENWEI)){
                    desktopItem.setLabelSize(getResources().getDimensionPixelSize(R.dimen.big_vertical_item_label_text_size_fenwei));
                }else{
                    desktopItem.setLabelSize(mContext.getResources().getDimensionPixelSize(R.dimen.big_vertical_item_label_text_size));
                }
                desktopItem.setLabelGravity(Gravity.CENTER);
            } else if (i == 8) {
                desktopItem.width = mContext.getResources().getDimensionPixelSize(R.dimen.big_horizontal_item_width);
                desktopItem.height = mContext.getResources().getDimensionPixelSize(R.dimen.big_horizontal_item_height);
                desktopItem.setIconLeftMargin(mContext.getResources().getDimensionPixelSize(R.dimen.big_horizontal_item_icon_margin_left));
                desktopItem.setIconTopMargin(mContext.getResources().getDimensionPixelSize(R.dimen.big_horizontal_item_icon_margin_top));
                desktopItem.setLabelBottomMargin(mContext.getResources().getDimensionPixelSize(R.dimen.desktop_item_gap));
            } else {
                desktopItem.width = mContext.getResources().getDimensionPixelSize(R.dimen.big_square_item_width);
                desktopItem.height = mContext.getResources().getDimensionPixelSize(R.dimen.big_square_item_height);
                desktopItem.setIconTopMargin(mContext.getResources().getDimensionPixelSize(R.dimen.big_square_item_icon_margin_top));
                desktopItem.setIconLeftMargin((desktopItem.width - desktopItem.iconWidth) / 2);
                desktopItem.setLabelBottomMargin(mContext.getResources().getDimensionPixelSize(R.dimen.desktop_item_gap));
                desktopItem.setLabelGravity(Gravity.CENTER);
            }

            if (i == 7 ) {
                if (SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_BOOT_MODEL_LINE_IN, false)){
                    desktopItem.setCustomClickListener(v -> getCustomAudioManager().setInputSelector(CustomAudioManager.SEL_INPUT_3));
                }else {
                    desktopItem.setCustomClickListener(v -> getCustomAudioManager().setInputSelector(CustomAudioManager.SEL_INPUT_2));
                }
            }

            if (Build.MODEL.contains("DISUONA")){
                if ( i == 5){
                    desktopItem.setCustomClickListener(v -> getCustomAudioManager().setInputSelector(CustomAudioManager.SEL_INPUT_3));
                }
            }


            /*if (i == 5){
                desktopItem.setCustomClickListener(v -> getCustomAudioManager().setInputSelector(CustomAudioManager.SEL_INPUT_3));
            }*/

            // 动画效果
//            desktopItem.setFocusAnimation(createItemFocusAnimation(desktopItem));
//            desktopItem.setUnFocusAnimation(createItemUnFocusAnimation(desktopItem));

            initItem(desktopItem);
        }
    }

    @Override
    protected void initItemsBg() {
        for (int i = 1; i <= mMaxTag - mMinTag + 1; i++) {
            mDesktopItems.get(mPageNumber + i).setBackgroundResource(R.drawable.bg_desktop_item);
//            mDesktopItems.get(mPageNumber + i).setOnFocusChangeListener((view, hasFocus) -> {
//                if (hasFocus) {
//                    view.startAnimation(((DesktopItem) view).getFocusAnimation());
//                    view.bringToFront();
//                } else {
//                    view.startAnimation(((DesktopItem) view).getUnFocusAnimation());
//                    view.clearAnimation();
//                }
//            });
//            if (i == 1) {
//                mDesktopItems.get(mPageNumber + i).requestFocus();
//            }
        }
    }

    private ScaleAnimation createItemFocusAnimation(DesktopItem desktopItem) {
        ScaleAnimation focusScaleAnimation = new ScaleAnimation(//
                1.0f,
                1 + (16f / desktopItem.width),
                1.0f,
                1 + (16f / desktopItem.height),
                Animation.RELATIVE_TO_SELF,
                0.5F,
                Animation.RELATIVE_TO_SELF,
                0.5F);
        focusScaleAnimation.setFillAfter(true);
        focusScaleAnimation.setInterpolator(new AccelerateInterpolator());
        focusScaleAnimation.setDuration(100L);

        return focusScaleAnimation;
    }

    private ScaleAnimation createItemUnFocusAnimation(DesktopItem desktopItem) {
        ScaleAnimation unFocusScaleAnimation = new ScaleAnimation(//
                1 + (16f / desktopItem.width),
                1.0f,
                1 + (16f / desktopItem.height),
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        unFocusScaleAnimation.setFillAfter(true);
        unFocusScaleAnimation.setInterpolator(new AccelerateInterpolator());
        unFocusScaleAnimation.setDuration(100L);

        return unFocusScaleAnimation;
    }

    @SuppressLint("WrongConstant")
    public CustomAudioManager getCustomAudioManager() {
        if (mCustomAudioManager == null) {
            mCustomAudioManager = (CustomAudioManager) mContext.getSystemService("custom_audio");
        }
        return mCustomAudioManager;
    }
}
