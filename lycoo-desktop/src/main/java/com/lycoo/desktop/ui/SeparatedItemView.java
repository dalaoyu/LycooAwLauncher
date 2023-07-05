package com.lycoo.desktop.ui;

import android.view.View;
import android.widget.RelativeLayout;

import com.lycoo.desktop.R;


/**
 * 图片和标题分离的坑位
 *
 * Created by lancy on 2019/5/15
 */
public class SeparatedItemView extends ItemView {
    private static final String TAG = SeparatedItemView.class.getSimpleName();
    private Builder mBuilder;

    public SeparatedItemView(Builder builder) {
        super(builder);
        mBuilder = builder;
    }

    public static ItemView create(Builder builder) {
        return new SeparatedItemView(builder);
    }

    @Override
    protected void customizeLabel(Builder builder) {
        mParams = new RelativeLayout.LayoutParams(builder.getBgWidth(), builder.getLabelHeight() == 0 ? RelativeLayout.LayoutParams.WRAP_CONTENT : builder.getLabelHeight());
        mParams.addRule(RelativeLayout.BELOW, mBackground.getId());
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mBackground.setBackgroundResource(hasFocus
                ? mBuilder.getFocusBgBackgroundResource() > 0 ? mBuilder.getFocusBgBackgroundResource() : R.drawable.bg_item_border_focus
                : mBuilder.getUnfocusBgBackgroundResource() > 0 ? mBuilder.getUnfocusBgBackgroundResource() : R.drawable.bg_item_border_normal);
    }
}
