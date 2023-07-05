package com.lycoo.desktop.ui;

import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 图片和标题嵌套的坑位
 *
 * Created by lancy on 2019/5/15
 */
public class NestedItemView extends ItemView {
    private static final String TAG = NestedItemView.class.getSimpleName();

    public NestedItemView(Builder builder) {
        super(builder);
    }

    public static ItemView create(Builder builder) {
        return new NestedItemView(builder);
    }

    @Override
    protected void customizeLabel(Builder builder) {
        /* 这种写法是因为没有搞清楚View的背景drawable机制 */
        // int gap = getResources().getDimensionPixelSize(R.dimen.item_view_gap);
        // label的宽度 = Bg的宽度 - Bg's marginLeft - Bg's marginRight - 背景左右两边的padding， 因为 Bg's marginLeft = Bg's marginRight = 背景左右两边的padding
        // 所以 label的宽度 = Bg的宽度 - gap * 4
        // mParams = new RelativeLayout.LayoutParams(builder.getBgWidth() - gap * 4, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // mParams.addRule(RelativeLayout.ALIGN_BOTTOM, mBackground.getId());
        // mParams.leftMargin = gap * 2;
        // mParams.bottomMargin = gap;

        // 新式的做法
        mParams = new RelativeLayout.LayoutParams(builder.getBgWidth(), RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (builder.getLabelGravity() == Gravity.CENTER) {
            mParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else {
            mParams.addRule(RelativeLayout.ALIGN_BOTTOM, mBackground.getId());

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
}
