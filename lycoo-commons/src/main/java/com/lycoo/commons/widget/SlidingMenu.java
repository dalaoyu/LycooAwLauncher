package com.lycoo.commons.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.ResourceUtils;
import com.nineoldandroids.view.ViewHelper;

/**
 * 侧滑菜单
 *
 * Created by Administrator on 2017/3/20.
 */
public class SlidingMenu extends HorizontalScrollView {
    private static final String TAG = SlidingMenu.class.getSimpleName();

    private ViewGroup mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;

    private int mScreenWidth;
    private int mMenuRightPadding;

    private int mMenuWidth;

    private boolean mMeasured = false;

    private boolean mStatus = false;
    private boolean mTouchEanble = true;


    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 当使用了自定义属性时调用
     */
    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                ResourceUtils.getIdArrayByName(
                        context,
                        "styleable",
                        "SlidingMenu"),
                defStyleAttr,
                0);

        mMenuRightPadding = (int) attributes.getDimension(
                ResourceUtils.getIdByName(
                        context,
                        "styleable",
                        "SlidingMenu_rightPadding"),
                mScreenWidth * 2 / 3);
        LogUtils.debug(TAG, "mMenuRightPadding = " + mMenuRightPadding);

        attributes.recycle();
    }

    /**
     * 设置子view的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!mMeasured) {
            // mWapper宽高没必要显示设置，因为它的两个元素已经设置，他自己也就定了
            mWapper = (ViewGroup) getChildAt(0);

            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;

            mContent = (ViewGroup) mWapper.getChildAt(1);
            mContent.getLayoutParams().width = mScreenWidth;

            mMeasured = true;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            this.scrollTo(mMenuWidth, 0); // 隐藏menu到左侧
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        LogUtils.info(TAG, "onInterceptTouchEvent>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        if (!mTouchEanble) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        LogUtils.info(TAG, "onTouchEvent>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        if (!mTouchEanble) {
            return false;
        }

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                // 隐藏在左边的宽度
                int scrollX = getScrollX();
                if (scrollX >= mMenuWidth / 2) {
                    smoothScrollTo(mMenuWidth, 0);
                    mStatus = false;
                } else {
                    smoothScrollTo(0, 0);
                    mStatus = true;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    public void openMenu() {
        if (mStatus)
            return;

        this.smoothScrollTo(0, 0);
        mStatus = true;
        mMenu.requestFocus();
    }

    public void closeMenu() {
        if (!mStatus)
            return;

        this.smoothScrollTo(mMenuWidth, 0);
        mStatus = false;
    }

    public void toggle() {
        if (mStatus) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    /**
     * 抽屉式AND QQ效果
     *
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        // 1. 抽屉式 --------------------------------
        float scale = l * 1.0f / mMenuWidth; // 1~0
        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale); // 抽屉式 调用属性动画

        /*
        scale : 1.0~0.0
         * 区别1：内容区域缩放1.0~0.7
         * 0.7 + 0.3 * scale
         *
         * 区别2：菜单的偏移量需要修改
         *
         * 区别3：菜单的显示时有缩放以及透明度变化
         * 缩放：0.7 ~1.0
         * 1.0 - scale * 0.3
         * 透明度: 0.6 ~ 1.0
         * 0.6+ 0.4 * (1- scale) ;
         *
         */
        /*
        // 1. QQ --------------------------------
        float menuScale = 1.0f - scale * 0.3f;
        float menuAlpha = 0.6f + 0.4f * (1 - scale);
//        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale); // QQ
        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.8F); // QQ
        ViewHelper.setScaleX(mMenu, menuScale);
        ViewHelper.setScaleY(mMenu, menuScale);
        ViewHelper.setAlpha(mMenu, menuAlpha);


        float contentScale = 0.9f + 0.1f * scale;
        // 设置Content缩放的中心点
        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
        // 设置Content缩放比例
        ViewHelper.setScaleX(mContent, contentScale);
        ViewHelper.setScaleY(mContent, contentScale);
   */
    }

    /**
     * Set Whether scroll by touch action
     *
     * @param touchEanble True if scroll by touch, otherwise false
     *
     *                    Created by lancy on 2018/4/18 13:42
     */
    public void setTouchEanble(boolean touchEanble) {
        this.mTouchEanble = touchEanble;
    }
}
