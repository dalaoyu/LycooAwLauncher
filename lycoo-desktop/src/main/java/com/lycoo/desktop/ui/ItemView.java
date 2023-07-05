package com.lycoo.desktop.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.view.RippleView;
import com.lycoo.desktop.R;

import lombok.Getter;


/**
 * 坑位
 * 基础桌面坑位
 *
 * Created by lancy on 2019/5/15
 */
public abstract class ItemView extends FrameLayout implements View.OnFocusChangeListener {
    private static final String TAG = ItemView.class.getSimpleName();
    private static final boolean DEBUG_UI = false;
    private static final boolean DEBUG = true;

    public enum LabelPosition {
        START,       // 靠左
        CENTER,      // 居中
        END          // 靠右
    }

    protected RelativeLayout mContainer;
    protected RelativeLayout.LayoutParams mParams;
    protected ImageView mBackground;
    protected ImageView mIcon;
    protected TextView mLabel;
    protected RippleView mRippleView;
    protected ImageView mMarkView;
    protected ImageView mCover;

    public ItemView(Builder builder) {
        super(builder.getContext());
        initView(builder);
    }

    /**
     * 初始化
     *
     * @param builder 构造器
     *
     *                Created by lancy on 2019/5/15 11:59
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initView(Builder builder) {
        // 布局
        // 不是WRAP_CONTENT， 设置ITEM背景的时候会挤压
//        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setLayoutParams(new ViewGroup.LayoutParams(builder.getBgWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));

        // 容器
        mContainer = new RelativeLayout(builder.getContext());
        mParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(mContainer, mParams);
        if (DEBUG_UI) {
            mContainer.setBackgroundColor(Color.RED);
        }

        // 背景
        initBg(builder);

        // 图标
        if (builder.isShowIcon()) {
            initIcon(builder);
        }

        // 标题
        if (builder.isShowLabel()) {
            initLabel(builder);
        }

        // 水波纹
        if (builder.isShowRipple()) {
            initRippleView(builder);
        }

        // 标记
        if (builder.isShowMark()) {
            initMarkView(builder);
        }

        // 遮盖物
        if (builder.isShowCover()) {
            initCover(builder);
        }

        // id
        if (builder.getId() > 0) {
            setId(builder.getId());
        }

        // tag
        if (builder.getTag() != null) {
            setTag(builder.getTag());
        }

        // 背景
        if (builder.getItemBackgroundResource() > 0) {
            setBackgroundResource(builder.getItemBackgroundResource());
        }

        // ClickListener
        if (builder.getOnClickListener() != null) {
            setOnClickListener(builder.getOnClickListener());
        }

        /*
         ************************************************************************
         * 光标处理
         ************************************************************************
         */
        if (builder.getNextFocusUpId() != View.NO_ID) {
            setNextFocusUpId(builder.getNextFocusUpId());
        }

        if (builder.getNextFocusDownId() != View.NO_ID) {
            setNextFocusDownId(builder.getNextFocusDownId());
        }

        if (builder.getNextFocusLeftId() != View.NO_ID) {
            setNextFocusLeftId(builder.getNextFocusLeftId());
        }

        if (builder.getNextFocusRightId() != View.NO_ID) {
            setNextFocusRightId(builder.getNextFocusRightId());
        }

        setFocusable(true);
        setFocusableInTouchMode(false);
        setOnFocusChangeListener(this);

        setOnKeyListener((v, keyCode, event) -> {
            if (builder.isShowRipple()) {
                mRippleView.ripple(keyCode, event);
            }

            if (builder.isShowCover()) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_DOWN:
                            ViewUtils.setViewShown(true, mCover);
                            break;
                        case KeyEvent.ACTION_UP:
                            ViewUtils.setViewShown(false, mCover);
                            break;
                    }
                }
            }
            return false;
        });

        setOnTouchListener((v, event) -> {
            if (builder.isShowRipple()) {
                mRippleView.ripple(event);
            }

            if (builder.isShowCover()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ViewUtils.setViewShown(true, mCover);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        ViewUtils.setViewShown(false, mCover);
                        break;
                }
            }
            return super.onTouchEvent(event);
        });
    }


    /**
     * 初始化背景
     *
     * Created by lancy on 2019/6/27 17:07
     */
    private void initBg(Builder builder) {
        mBackground = new ImageView(builder.getContext());
        mBackground.setId(View.generateViewId());
        mBackground.setBackgroundResource(builder.getBgBackgroundResource());
        mBackground.setImageResource(builder.getBgImageResource());
        mBackground.setScaleType(ImageView.ScaleType.FIT_XY);
        mBackground.setFocusable(false);
        mBackground.setFocusableInTouchMode(false);
        mParams = new RelativeLayout.LayoutParams(builder.getBgWidth(), builder.getBgHeight());
        mContainer.addView(mBackground, mParams);
        if (DEBUG_UI) {
            mBackground.setBackgroundColor(Color.BLUE);
        }
    }

    /**
     * 初始化图标
     *
     * Created by lancy on 2019/6/27 17:07
     */
    private void initIcon(Builder builder) {
        mIcon = new ImageView(builder.getContext());
        mIcon.setId(View.generateViewId());
        mIcon.setImageResource(builder.getIconImageResource());
        mIcon.setFocusable(false);
        mParams = new RelativeLayout.LayoutParams(builder.getIconWidth(), builder.getIconHeight());
        // 水平方向不居中
        if (builder.getIconLeftMargin() > 0) {
            mParams.leftMargin = builder.getIconLeftMargin();
        } else {
            // 上下方向不居中
            if (builder.getIconTopMargin() > 0) {
                mParams.topMargin = builder.getIconTopMargin();
                mParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            }
            // 水平&上下均居中
            else {
                mParams.leftMargin = (builder.getBgWidth() - builder.getIconWidth()) >> 1;
                mParams.topMargin = (builder.getBgHeight() - builder.getIconHeight()) >> 1;
            }
        }
        mContainer.addView(mIcon, mParams);
    }

    /**
     * 初始化标题
     *
     * Created by lancy on 2019/6/27 17:09
     */
    private void initLabel(Builder builder) {
        mLabel = new TextView(builder.getContext());
        mLabel.setId(View.generateViewId());
        if (builder.isLableBold()) {
            mLabel.setTypeface(StyleManager.getInstance(builder.getContext()).getTypeface(), Typeface.BOLD);
        } else {
            mLabel.setTypeface(StyleManager.getInstance(builder.getContext()).getTypeface());
        }
        mLabel.setLines(1);
        mLabel.setTextSize(builder.getLabelSize());
        mLabel.setTextColor(builder.getLabelColor());
        if (builder.getLabelText() != null) {
            mLabel.setText(builder.getLabelText());
        } else if (builder.getLabelResource() > 0) {
            mLabel.setText(builder.getLabelResource());
        }
        mLabel.setPadding(
                getResources().getDimensionPixelSize(R.dimen.item_view_label_padding_lr),
                builder.getLabelHeight() == 0 ? getResources().getDimensionPixelSize(R.dimen.item_view_label_padding_tb) : 0,
                getResources().getDimensionPixelSize(R.dimen.item_view_label_padding_lr),
                builder.getLabelHeight() == 0 ? getResources().getDimensionPixelSize(R.dimen.item_view_label_padding_tb) : 0);
        int gravity;
        switch (builder.getLabelPosition()) {
            case START:
                gravity = Gravity.START;
                break;

            case END:
                gravity = Gravity.END;
                break;

            case CENTER:
            default:
                gravity = Gravity.CENTER_HORIZONTAL;
                break;
        }
        mLabel.setGravity(Gravity.CENTER_VERTICAL | gravity);

        // 定制Label
        customizeLabel(builder);
        mContainer.addView(mLabel, mParams);
        if (DEBUG_UI) {
            mLabel.setBackgroundColor(Color.GREEN);
        }
    }

    /**
     * 初始化水波纹
     *
     * Created by lancy on 2019/6/27 17:11
     */
    private void initRippleView(Builder builder) {
        mRippleView = new RippleView(builder.getContext());
        mRippleView.setOvalShape(builder.isOval());
        mRippleView.setRippleColor(getResources().getColor(R.color.item_ripple));
        mRippleView.setFocusable(false);
        mRippleView.setFocusableInTouchMode(false);
        mParams = new RelativeLayout.LayoutParams(builder.getBgWidth(), builder.getBgHeight());
        mContainer.addView(mRippleView, mParams);
    }

    /**
     * 初始化标记View
     *
     * Created by lancy on 2019/8/8 16:09
     */
    private void initMarkView(Builder builder) {
        mMarkView = new ImageView(builder.getContext());
        mMarkView.setId(View.generateViewId());
        mMarkView.setBackgroundResource(builder.isOval() ? R.drawable.bg_oval_mark : R.drawable.bg_mark);
        mMarkView.getBackground().setAlpha(150);
        mMarkView.setImageResource(builder.getMarkImageResource());
        mMarkView.setScaleType(ImageView.ScaleType.FIT_XY);
        mMarkView.setFocusable(false);
        mMarkView.setFocusableInTouchMode(false);
        mMarkView.setVisibility(View.GONE);
        mParams = new RelativeLayout.LayoutParams(builder.getBgWidth(), builder.getBgHeight());
        mContainer.addView(mMarkView, mParams);
        if (DEBUG_UI) {
            mBackground.setBackgroundColor(Color.DKGRAY);
        }
    }

    /**
     * 初始化覆盖物
     *
     * Created by lancy on 2019/8/11 16:35
     */
    private void initCover(Builder builder) {
        mCover = new ImageView(builder.getContext());
        mCover.setBackgroundResource(builder.getCoverResource());
        mCover.setFocusable(false);
        mCover.setFocusableInTouchMode(false);
        mCover.setVisibility(View.GONE);
        mParams = new RelativeLayout.LayoutParams(builder.getBgWidth(), builder.getBgHeight());
        mContainer.addView(mCover, mParams);
    }

    /**
     * 定制化Label
     * NestedItemView 和 SeparatedItemView 背景，图标处理都是一致的，唯一有区别的是Label的展示
     *
     * Created by lancy on 2019/5/20 19:04
     */
    protected abstract void customizeLabel(Builder builder);

    public void setLabelText(int resid) {
        if (mLabel != null) {
            mLabel.setText(resid);
        }
    }

    public void setLabelText(String label) {
        if (mLabel != null) {
            mLabel.setText(label);
        }
    }

    public void setIconResource(int iconResid) {
        if (mIcon != null) {
            mIcon.setImageResource(iconResid);
        }
    }

    public void setBgImageResource(int resid) {
        if (mBackground != null) {
            mBackground.setImageResource(resid);
        }
    }

    public void setMarked(boolean marked) {
        if (mMarkView != null) {
            mMarkView.setVisibility(marked ? View.VISIBLE : View.GONE);
        }
    }

    public ImageView getBg() {
        return mBackground;
    }

    public ImageView getIcon() {
        return mIcon;
    }

    public TextView getLabel() {
        return mLabel;
    }

    public static class Builder {
        @Getter
        private Context context;

        /**
         * ID
         */
        @Getter
        private int id;

        /**
         * tag
         */
        @Getter
        private Object tag;

        /**
         * 背景宽度
         */
        @Getter
        private int bgWidth;

        /**
         * 背景高度
         */
        @Getter
        private int bgHeight;

        /**
         * 背景的内容
         * 用于mBackground.setImageResource()
         */
        @Getter
        private int bgImageResource;

        /**
         * 背景的背景
         * 用于mBackground.setBackgroundResource()
         * 主要为SeprateItemView使用
         */
        @Getter
        private int bgBackgroundResource;

        /**
         * 是否显示图标
         */
        @Getter
        private boolean showIcon;

        /**
         * 图标宽度
         */
        @Getter
        private int iconWidth;

        /**
         * 图标高度
         */
        @Getter
        private int iconHeight;

        /**
         * 图标到顶部的距离
         */
        @Getter
        private int iconTopMargin;

        /**
         * 图标到左侧的距离
         */
        @Getter
        private int iconLeftMargin;

        /**
         * 图标
         * setImageResource()
         */
        @Getter
        private int iconImageResource;

        /**
         * 是否显示标题
         */
        @Getter
        private boolean showLabel;

        /**
         * 标题高度
         */
        @Getter
        private int labelHeight;

        /**
         * 标题位置
         * 只对NestedItemView起作用，标识标题的位置
         */
        @Getter
        private int labelGravity;

        /**
         * 标题字体大小
         */
        @Getter
        private float labelSize;

        /**
         * 标题字体是否加粗
         */
        @Getter
        private boolean lableBold;

        /**
         * 标题字体颜色
         */
        @Getter
        private int labelColor;

        /**
         * 标题文字显示位置
         * 标识文字在TextView中的显示位置，左，右， 中间
         */
        @Getter
        private LabelPosition labelPosition;

        /**
         * 标题
         */
        @Getter
        private int labelResource;

        /**
         * 标题
         */
        @Getter
        private String labelText;

        /**
         * 是否显示水波纹
         */
        @Getter
        private boolean showRipple;

        /**
         * 是否为圆形
         */
        @Getter
        private boolean oval;

        /**
         * 整个Item背景
         */
        @Getter
        private int itemBackgroundResource;

        /**
         * 获得光标时背景
         */
        @Getter
        private int focusBgBackgroundResource;

        /**
         * 失去光标时背景
         */
        @Getter
        private int unfocusBgBackgroundResource;

        /**
         * 是否启动标记
         */
        @Getter
        private boolean showMark;

        /**
         * 标记
         * 例如要标记ItemView选中状态，或者出错状态等
         */
        @Getter
        private int markImageResource;

        /**
         * 自定义遮盖物
         * 解决光标，按钮效果
         */
        @Getter
        private boolean showCover;

        /**
         * 资源图片
         */
        @Getter
        private int coverResource;

        /**
         * 点击监听器
         */
        @Getter
        private OnClickListener onClickListener;

        /**
         * 向上获的光标的控件
         */
        @Getter
        private int nextFocusUpId;

        /**
         * 向下获的光标的控件
         */
        @Getter
        private int nextFocusDownId;

        /**
         * 向左获的光标的控件
         */
        @Getter
        private int nextFocusLeftId;

        /**
         * 向右获的光标的控件
         */
        @Getter
        private int nextFocusRightId;

        public Builder(Context context) {
            this.context = context;
            this.bgWidth = 0;
            this.bgHeight = 0;

            this.showIcon = false;
            this.iconWidth = 0;
            this.iconHeight = 0;
            this.iconTopMargin = 0;
            this.iconLeftMargin = 0;

            this.showLabel = false;
            this.labelHeight = 0;
            this.labelGravity = Gravity.BOTTOM;
            this.labelSize = context.getResources().getDimensionPixelSize(R.dimen.item_view_label_text_size);
            this.lableBold = false;
            this.labelColor = Color.WHITE;
            this.labelText = null;
            this.labelPosition = LabelPosition.CENTER;

            this.showRipple = false;
            this.oval = false;

            this.showMark = false;

            this.showCover = false;
        }

        // Setter. BEGIN *****************************************************************************************************
        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setTag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder setBgWidth(int bgWidth) {
            this.bgWidth = bgWidth;
            return this;
        }

        public Builder setBgHeight(int bgHeight) {
            this.bgHeight = bgHeight;
            return this;
        }

        public Builder setBgImageResource(int bgImageResource) {
            this.bgImageResource = bgImageResource;
            return this;
        }

        public Builder setBgBackgroundResource(int bgBackgroundResource) {
            this.bgBackgroundResource = bgBackgroundResource;
            return this;
        }

        public Builder setShowIcon(boolean showIcon) {
            this.showIcon = showIcon;
            return this;
        }

        public Builder setIconWidth(int iconWidth) {
            this.iconWidth = iconWidth;
            return this;
        }

        public Builder setIconHeight(int iconHeight) {
            this.iconHeight = iconHeight;
            return this;
        }

        public Builder setIconTopMargin(int iconTopMargin) {
            this.iconTopMargin = iconTopMargin;
            return this;
        }

        public Builder setIconLeftMargin(int iconLeftMargin) {
            this.iconLeftMargin = iconLeftMargin;
            return this;
        }

        public Builder setIconImageResource(int iconImageResource) {
            this.iconImageResource = iconImageResource;
            return this;
        }

        public Builder setShowLabel(boolean showLabel) {
            this.showLabel = showLabel;
            return this;
        }

        public Builder setLabelHeight(int labelHeight) {
            this.labelHeight = labelHeight;
            return this;
        }

        public Builder setLabelGravity(int labelGravity) {
            this.labelGravity = labelGravity;
            return this;
        }

        public Builder setLabelSize(float labelSize) {
            this.labelSize = labelSize;
            return this;
        }

        public Builder setLableBold(boolean lableBold) {
            this.lableBold = lableBold;
            return this;
        }

        public Builder setLabelColor(int labelColor) {
            this.labelColor = labelColor;
            return this;
        }

        public Builder setLabelPosition(LabelPosition labelPosition) {
            this.labelPosition = labelPosition;
            return this;
        }

        public Builder setLabelResource(int labelResource) {
            this.labelResource = labelResource;
            return this;
        }

        public Builder setLabelText(String labelText) {
            this.labelText = labelText;
            return this;
        }

        public Builder setShowRipple(boolean showRipple) {
            this.showRipple = showRipple;
            return this;
        }

        public Builder setOval(boolean oval) {
            this.oval = oval;
            return this;
        }

        public Builder setItemBackgroundResource(int itemBackgroundResource) {
            this.itemBackgroundResource = itemBackgroundResource;
            return this;
        }

        public Builder setFocusBgBackgroundResource(int focusBgBackgroundResource) {
            this.focusBgBackgroundResource = focusBgBackgroundResource;
            return this;
        }

        public Builder setUnfocusBgBackgroundResource(int unfocusBgBackgroundResource) {
            this.unfocusBgBackgroundResource = unfocusBgBackgroundResource;
            return this;
        }

        public Builder setShowMark(boolean showMark) {
            this.showMark = showMark;
            return this;
        }

        public Builder setMarkImageResource(int markImageResource) {
            this.markImageResource = markImageResource;
            return this;
        }

        public Builder setShowCover(boolean showCover) {
            this.showCover = showCover;
            return this;
        }

        public Builder setCoverResource(int coverResource) {
            this.coverResource = coverResource;
            return this;
        }

        public Builder setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        public Builder setNextFocusUpId(int nextFocusUpId) {
            this.nextFocusUpId = nextFocusUpId;
            return this;
        }

        public Builder setNextFocusDownId(int nextFocusDownId) {
            this.nextFocusDownId = nextFocusDownId;
            return this;
        }

        public Builder setNextFocusLeftId(int nextFocusLeftId) {
            this.nextFocusLeftId = nextFocusLeftId;
            return this;
        }

        public Builder setNextFocusRightId(int nextFocusRightId) {
            this.nextFocusRightId = nextFocusRightId;
            return this;
        }

        // Setter. END   *****************************************************************************************************
    }

//    @Override
//    public boolean performClick() {
//        if (DEBUG) {
//            LogUtils.info(TAG, "performClick....");
//        }
//        postDelayed(ItemView.super::performClick, getResources().getInteger(R.integer.extra_ripple_duration) + AutoRippleView.DURATION);
//        return true;
//    }
}
