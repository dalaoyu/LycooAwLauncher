package com.lycoo.commons.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.lycoo.commons.R;
import com.lycoo.commons.util.LogUtils;


/**
 * 自定义卫星菜单
 *
 * Created by lancy on 2017/3/10
 */

public class ArcMenu extends ViewGroup implements View.OnClickListener {
    private static final String TAG = ArcMenu.class.getSimpleName();

    private static final int POSITION_LEFT_TOP = 0;
    private static final int POSITION_LEFT_BOTTOM = 1;
    private static final int POSITION_RIGHT_TOP = 2;
    private static final int POSITION_RIGHT_BOTTOM = 3;

    private static final int DEF_DURATION = 300;
    private static final int BASE_ID = 2000;

    /**
     * 菜单的位置
     */
    public enum Position {
        LEFT_TOP,
        LEFT_BOTTOM,
        RIGHT_TOP,
        RIGHT_BOTTOM
    }

    /**
     * 菜单的状态
     */
    public enum Status {
        OPEN,
        CLOSE
    }

    private int mRadius;                                    // 半径
    private Position mPosition = Position.RIGHT_BOTTOM;     // 位置默认在右下角
    private Status mStatus = Status.CLOSE;                  // 状态默认关闭

    private OnMenuItemClickListener mOnMenuItemClickListener;
    private OnStatusChangeListener mOnStatusChangeListener;
    private int mDuration = DEF_DURATION;

    private View mController;       // 控制器（中心控制控件）
    private ViewGroup mContainer;   // item容器
    private Drawable mExpandBackground;

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取自定义属性的值
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ArcMenu,
                defStyleAttr,
                0);

        // 获取位置
        int postion = typedArray.getInt(R.styleable.ArcMenu_position, POSITION_RIGHT_BOTTOM);
        switch (postion) {
            case POSITION_LEFT_TOP:
                mPosition = Position.LEFT_TOP;
                break;

            case POSITION_LEFT_BOTTOM:
                mPosition = Position.LEFT_BOTTOM;
                break;

            case POSITION_RIGHT_TOP:
                mPosition = Position.RIGHT_TOP;
                break;

            case POSITION_RIGHT_BOTTOM:
                mPosition = Position.RIGHT_BOTTOM;
                break;
        }

        // 获取半径
        mRadius = (int) typedArray.getDimension(R.styleable.ArcMenu_radius,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 100, getResources().getDisplayMetrics()));

        typedArray.recycle();


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            // 测量child
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);

            if (i == 1) {
                ViewGroup container = (ViewGroup) getChildAt(i);
                int childCount = container.getChildCount();
                for (int j = 0; j < childCount; j++) {
                    measureChild(container.getChildAt(j), widthMeasureSpec, heightMeasureSpec);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            // 定位主控制器
            layoutController();

            // 定位容器
            layoutContainer();

            // 定位items
            layoutItems();
        }
    }


    /**
     * 定位主控制器
     */
    private void layoutController() {
        mController = getChildAt(0);
        mController.setOnClickListener(this);

        int l = 0;
        int t = 0;
        int width = mController.getMeasuredWidth();
        int height = mController.getMeasuredHeight();
        switch (mPosition) {
            case LEFT_TOP:
//                l = 0;
//                t = 0;
                l = getPaddingLeft();
                t = getPaddingTop();
                break;
            case LEFT_BOTTOM:
//                l = 0;
//                t = getMeasuredHeight() - height;
                l = getPaddingLeft();
                t = getMeasuredHeight() - height - getPaddingBottom();
                break;
            case RIGHT_TOP:
//                l = getMeasuredWidth() - width;
//                t = 0;
                l = getMeasuredWidth() - width - getPaddingRight();
                t = getPaddingTop();
                break;
            case RIGHT_BOTTOM:
//                l = getMeasuredWidth() - width;
//                t = getMeasuredHeight() - height;
                l = getMeasuredWidth() - width - getPaddingRight();
                t = getMeasuredHeight() - height - getPaddingBottom();
                break;
        }

        LogUtils.debug(TAG, "layoutController padding top = " + getPaddingTop());
        LogUtils.debug(TAG, "layoutController padding bottom = " + getPaddingBottom());
        LogUtils.debug(TAG, "layoutController padding left = " + getPaddingLeft());
        LogUtils.debug(TAG, "layoutController padding right = " + getPaddingRight());

//        mController.layout(l, t, l + width, t + height);
        mController.layout(l, t, l + width, t + height);
    }

    /**
     * 定位items容器
     */
    private void layoutContainer() {
        mContainer = (ViewGroup) getChildAt(1);
        int l = 0;
        int t = 0;
//        int width = mContainer.getMeasuredWidth();
//        int height = mContainer.getMeasuredHeight();
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        LogUtils.debug(TAG, "item's container width = " + mContainer.getMeasuredWidth());
        LogUtils.debug(TAG, "item's container height = " + mContainer.getMeasuredHeight());
//        int l = getPaddingLeft();
//        int t = getPaddingTop();
//        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
//        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        mContainer.layout(l, t, l + width, t + height);
    }

    /**
     * 定位items
     */
    @SuppressLint("ResourceType")
    private void layoutItems() {
        int count = mContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mContainer.getChildAt(i);
            LogUtils.debug(TAG, "child's tag = " + child.getTag());
            child.setId(BASE_ID + i);
            child.setVisibility(View.GONE);

            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 1) * i));
            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 1) * i));


            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();

            if (mPosition == Position.LEFT_TOP || mPosition == Position.RIGHT_TOP) {
                ct = getPaddingTop() + ct;
            } else {
                ct = getMeasuredHeight() - height - ct - getPaddingBottom();
            }

            if (mPosition == Position.LEFT_TOP || mPosition == Position.LEFT_BOTTOM) {
                cl = getPaddingLeft() + cl;
            } else {
                cl = getMeasuredWidth() - width - cl - getPaddingRight();
            }

            /*
            // 如果菜单在左下或者右下
            if (mPosition == Position.LEFT_BOTTOM || mPosition == Position.RIGHT_BOTTOM) {
                ct = getMeasuredHeight() - height - ct;
            }

            if (mPosition == Position.RIGHT_BOTTOM || mPosition == Position.RIGHT_TOP) {
                cl = getMeasuredWidth() - width - cl;
            }
            */

            if (mPosition == Position.RIGHT_BOTTOM) {
                child.setNextFocusLeftId(child.getId());
                child.setNextFocusRightId(mController.getId());
                if (child.getId() == BASE_ID) {
                    child.setNextFocusUpId(child.getId());
                } else {
                    child.setNextFocusUpId(child.getId() - 1);
                }
                if (child.getId() == BASE_ID + count - 1) {
                    child.setNextFocusDownId(child.getId());
                } else {
                    child.setNextFocusDownId(child.getId() + 1);
                }
            } else if (mPosition == Position.LEFT_BOTTOM) {
                child.setNextFocusLeftId(mController.getId());
                child.setNextFocusRightId(child.getId());
                if (child.getId() == BASE_ID) {
                    child.setNextFocusUpId(child.getId());
                } else {
                    child.setNextFocusUpId(child.getId() - 1);
                }
                if (child.getId() == BASE_ID + count - 1) {
                    child.setNextFocusDownId(child.getId());
                } else {
                    child.setNextFocusDownId(child.getId() + 1);
                }
            } else if (mPosition == Position.RIGHT_TOP) {
                child.setNextFocusLeftId(child.getId());
                child.setNextFocusRightId(mController.getId());
                if (child.getId() == BASE_ID) {
                    child.setNextFocusDownId(child.getId());
                } else {
                    child.setNextFocusDownId(child.getId() - 1);
                }
                if (child.getId() == BASE_ID + count - 1) {
                    child.setNextFocusUpId(child.getId());
                } else {
                    child.setNextFocusUpId(child.getId() + 1);
                }
            } else if (mPosition == Position.LEFT_TOP) {
                child.setNextFocusLeftId(mController.getId());
                child.setNextFocusRightId(child.getId());
                if (child.getId() == BASE_ID) {
                    child.setNextFocusDownId(child.getId());
                } else {
                    child.setNextFocusDownId(child.getId() - 1);
                }
                if (child.getId() == BASE_ID + count - 1) {
                    child.setNextFocusUpId(child.getId());
                } else {
                    child.setNextFocusUpId(child.getId() + 1);
                }
            }
            child.layout(cl, ct, cl + width, ct + height);
        }
    }

    public void toggleMenu() {
        // 创建旋转动画
        Animation rotateAnimation = createRotateAnimation();
        int count = mContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = mContainer.getChildAt(i);
            child.setVisibility(View.VISIBLE);

            // 创建平移动画
            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 1) * i));
            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 1) * i));
            int xflag = 1;
            int yflag = 1;
            if (mPosition == Position.LEFT_TOP || mPosition == Position.LEFT_BOTTOM) {
                xflag = -1;
            }

            if (mPosition == Position.LEFT_TOP || mPosition == Position.RIGHT_TOP) {
                yflag = -1;
            }

            AnimationSet animationSet = new AnimationSet(true);
            Animation translateAnimation;
            // open
            if (mStatus == Status.CLOSE) {
                translateAnimation = new TranslateAnimation(
                        xflag * cl,
                        0,
                        yflag * ct,
                        0);
                child.setClickable(true);
                child.setFocusable(true);
            }
            // close
            else {
                translateAnimation = new TranslateAnimation(
                        0,
                        xflag * cl,
                        0,
                        yflag * ct);
                child.setClickable(false);
                child.setFocusable(false);
            }
            translateAnimation.setDuration(mDuration);
            translateAnimation.setFillAfter(true);
            translateAnimation.setStartOffset(i * 80);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mStatus == Status.CLOSE) {
                        child.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            animationSet.addAnimation(rotateAnimation);
            animationSet.addAnimation(translateAnimation);
            child.startAnimation(animationSet);

            final int position = i;
            child.setOnClickListener(v -> {
                changeStatus();
                startItemClickAnimation(v, position);
            });
        }

        changeStatus();
    }

    private Animation createRotateAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(
                0,
                720,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setDuration(mDuration);
        rotateAnimation.setFillAfter(true);
        return rotateAnimation;
    }


    /**
     * menuItem点击动画
     *
     * Created by lancy on 2017/3/17
     */
    private void startItemClickAnimation(final View v, final int position) {
        Animation scaleBigAnimation = new ScaleAnimation(
                1.0f,
                4.0f,
                1.0f,
                4.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );

        Animation scaleSmallAnimation = new ScaleAnimation(
                1.0f,
                0.0f,
                1.0f,
                0.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );

        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.0f);

        AnimationSet bigAnimationSet = new AnimationSet(true);
        bigAnimationSet.addAnimation(scaleBigAnimation);
        bigAnimationSet.addAnimation(alphaAnimation);
        bigAnimationSet.setDuration(mDuration);
        bigAnimationSet.setFillAfter(true);
        bigAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LogUtils.debug(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> bigAnimationSet onAnimationEnd");
                if (mOnMenuItemClickListener != null) {
                    mOnMenuItemClickListener.onClick(v, position);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        AnimationSet smallAnimationSet = new AnimationSet(true);
        smallAnimationSet.addAnimation(scaleSmallAnimation);
        smallAnimationSet.addAnimation(alphaAnimation);
        smallAnimationSet.setDuration(mDuration);
        smallAnimationSet.setFillAfter(true);

        int itemCount = mContainer.getChildCount();
        for (int i = 0; i < itemCount; i++) {
            View child = mContainer.getChildAt(i);
            if (i == position) {
                child.startAnimation(bigAnimationSet);
            } else {
                child.startAnimation(smallAnimationSet);
            }

            child.setFocusable(false);
            child.setClickable(false);
        }
    }

    private Animation createControllerRotateAnimation() {
        Animation anim = new RotateAnimation(
                0,
                360,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(mDuration);
        anim.setFillAfter(true);
        return anim;
    }


    private void changeStatus() {
        mStatus = (mStatus == Status.CLOSE) ? Status.OPEN : Status.CLOSE;
        if (mOnStatusChangeListener != null) {
            mOnStatusChangeListener.onStatusChange(mStatus);
        }

        if (mExpandBackground != null) {
            if (mStatus == Status.CLOSE) {
                setBackgroundColor(Color.TRANSPARENT);
                mController.requestFocus();
            } else {
                setBackground(mExpandBackground);
            }
        }

        int count = mContainer.getChildCount();
        if (isOpen()) {
            if (mPosition == Position.RIGHT_BOTTOM) {
                mController.setNextFocusUpId(BASE_ID);
                mController.setNextFocusLeftId(BASE_ID + -1);
            } else if (mPosition == Position.LEFT_BOTTOM) {
                mController.setNextFocusUpId(BASE_ID);
                mController.setNextFocusRightId(BASE_ID + count - 1);
            } else if (mPosition == Position.RIGHT_TOP) {
                mController.setNextFocusDownId(BASE_ID);
                mController.setNextFocusLeftId(BASE_ID + count - 1);
            } else if (mPosition == Position.LEFT_TOP) {
                mController.setNextFocusDownId(BASE_ID);
                mController.setNextFocusRightId(BASE_ID + count - 1);
            }
        } else {
            mController.setNextFocusDownId(View.NO_ID);
            mController.setNextFocusUpId(View.NO_ID);
            mController.setNextFocusLeftId(View.NO_ID);
            mController.setNextFocusRightId(View.NO_ID);
        }
    }

    @Override
    public void onClick(View v) {
        v.setAnimation(createControllerRotateAnimation());
        toggleMenu();
    }

    /**
     * 状态改变回调
     */
    public interface OnStatusChangeListener {
        void onStatusChange(Status status);
    }

    /**
     * 菜单Item点击回调
     */
    public interface OnMenuItemClickListener {
        void onClick(View view, int position);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }


    public void setOnStatusChangeListener(OnStatusChangeListener onStatusChangeListener) {
        this.mOnStatusChangeListener = onStatusChangeListener;
    }


    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public Status getStatus() {
        return this.mStatus;
    }

    public View getController() {
        return this.mController;
    }

    public ViewGroup getContainer() {
        return this.mContainer;
    }

    /**
     * 设置位置
     *
     * @param position 位置
     *
     *                 Created by lancy on 2018/4/17 11:41
     */
    public void setPosition(Position position) {
        this.mPosition = position;
    }


    public void setExpandBackground(Drawable drawable) {
        mExpandBackground = drawable;
    }

    public boolean isOpen() {
        return mStatus == Status.OPEN;
    }
}
