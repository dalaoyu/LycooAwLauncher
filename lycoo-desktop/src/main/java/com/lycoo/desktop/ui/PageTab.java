package com.lycoo.desktop.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.widget.CustomViewPageScroller;
import com.lycoo.desktop.R;
import com.lycoo.desktop.adapter.CustomPageAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * xxx
 *
 * Created by lancy on 2017/12/12
 */
public class PageTab extends FrameLayout implements ViewPager.OnPageChangeListener {
    private static final String TAG = PageTab.class.getSimpleName();
    private static final boolean DEBUG_UI = false;

    private RelativeLayout mRoot;
    private LinearLayout mTabContainer;
    private View mTabIndicator;
    private ViewPager mViewPager;

    private Context mContext;
    private List<Button> mTabs;
    private List<View> mPages;

    public PageTab(@NonNull Context context) {
        super(context);

        mContext = context;
        mPages = new ArrayList<>();
        initView();
    }

    /**
     * 初始化布局
     * 注意：
     * initTabComponent() 需要在 initViewPage()后面执行，保证Tab在ViewPage的上层，否则导致tab点击不了。
     *
     * Created by lancy on 2018/4/16 11:49
     */
    private void initView() {
        initRootContainer();
        initViewPage();
        initTabComponent();
    }

    /**
     * 初始化根容器
     *
     * Created by lancy on 2017/12/12 17:40
     */
    private void initRootContainer() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mRoot = new RelativeLayout(mContext);
        mRoot.setLayoutParams(params);
        addView(mRoot);
    }

    /**
     * 初始化tab相关控件
     * 包括容器，指示器等
     *
     * Created by lancy on 2017/12/12 17:41
     */
    @SuppressLint("ResourceType")
    private void initTabComponent() {
        // 初始化tab root
        RelativeLayout.LayoutParams rootParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rootParams.topMargin = getResources().getDimensionPixelSize(R.dimen.tab_container_margin_top);
        rootParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.tab_container_margin_left);
        LinearLayout tabRoot = new LinearLayout(mContext);
        tabRoot.setLayoutParams(rootParams);
        tabRoot.setOrientation(LinearLayout.VERTICAL);
        mRoot.addView(tabRoot);

        // 初始化tab container
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.tab_container_height));
        mTabContainer = new LinearLayout(mContext);
        mTabContainer.setLayoutParams(params);
        mTabContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabRoot.addView(mTabContainer);

        // 初始化tab指示器
        params = new LinearLayout.LayoutParams(
                mContext.getResources().getDimensionPixelSize(R.dimen.tab_indicator_width),
                mContext.getResources().getDimensionPixelSize(R.dimen.tab_indicator_height));
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.tab_indicator_margin_left);
        mTabIndicator = new View(mContext);
        mTabIndicator.setLayoutParams(params);
        mTabIndicator.setBackgroundResource(R.color.tab_focus);
        tabRoot.addView(mTabIndicator);
    }


    /**
     * 初始化Tabs
     * 根据页面动态生成tab
     *
     * @param pages 页面集合
     *
     *              Created by lancy on 2017/12/12 18:04
     */
    private void initTabs(List<Page> pages) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                mContext.getResources().getDimensionPixelSize(R.dimen.tab_width),
                RadioGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_VERTICAL;

        mTabs = new ArrayList<>();
        Button tab;
        for (Page page : pages) {
            tab = new Button(mContext);
            tab.setId(page.getNumber());
            tab.setPadding(
                    getResources().getDimensionPixelSize(R.dimen.tab_padding_left),
                    0,
                    getResources().getDimensionPixelSize(R.dimen.tab_padding_left),
                    0);
            tab.setTypeface(StyleManager.getInstance(mContext).getTypeface());
            tab.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.tab_text_size));
            tab.setText(page.getLabel());
            tab.setTextColor(getResources().getColor(R.color.tab_normal));
            if (DEBUG_UI) {
                if (page.getNumber() == 200) {
                    tab.setBackgroundColor(Color.RED);
                } else {
                    tab.setBackgroundColor(Color.BLUE);
                }
            }
            tab.setFocusable(true);
            tab.setFocusableInTouchMode(true);
            tab.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    int id = v.getId();
                    mViewPager.setCurrentItem(id / 100 - 1);

//                    v.setNextFocusDownId(((Page) mPages.get(id / 100 - 1)).getFocusItemId());
                    v.setNextFocusDownId(((Page) mPages.get(id / 100 - 1)).getNumber() + 1);
                }
                ViewUtils.setViewShown(hasFocus, mTabIndicator);
            });
            tab.setBackgroundColor(Color.TRANSPARENT);
            tab.bringToFront();
            mTabContainer.addView(tab, params);

            mTabs.add(tab);
        }
    }


    /**
     * 初始化ViewPage
     *
     * Created by lancy on 2017/12/12 17:41
     */
    private void initViewPage() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mViewPager = new ViewPager(mContext);
        mViewPager.setLayoutParams(params);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setFocusable(false);
        mViewPager.setFocusableInTouchMode(false);
        mRoot.addView(mViewPager);
        if (DEBUG_UI) {
            mViewPager.setBackgroundColor(Color.MAGENTA);
        }
    }

    /**
     * 添加页面
     *
     * @param pages 要添加的页面
     *
     *              Created by lancy on 2017/12/12 18:20
     */
    public void addPages(List<Page> pages) {
        // 初始化tabs
        initTabs(pages);

        // ViewPage set adapter
        mPages.addAll(pages);
        mViewPager.setAdapter(new CustomPageAdapter(mPages));
        try {
            // 通过反射设置ViewPage动画时间
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            CustomViewPageScroller customViewPageScroller = new CustomViewPageScroller(mViewPager.getContext(), new AccelerateInterpolator());
            scroller.set(mViewPager, customViewPageScroller);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        mTabs.get(0).requestFocus();
        mTabs.get(0).setTextColor(getResources().getColor(R.color.tab_focus));
    }

    /**
     * 更新Tab显示
     * 更新Tab的字体,颜色以及指示器
     *
     * @param position 选中tab的索引
     *
     *                 Created by lancy on 2017/12/12 18:21
     */
    private void updateTab(int position) {
        for (int i = 0; i < mPages.size(); i++) {
            if (i == position) {
                mTabs.get(i).setTextColor(getResources().getColor(R.color.tab_focus));
                slideIndicator(mTabIndicator, mTabs.get(i));
            } else {
                mTabs.get(i).setTextColor(getResources().getColor(R.color.tab_normal));
            }
        }
    }

    private void slideIndicator(View indicator, View focusView) {
        ViewPropertyAnimator animator = indicator.animate();
        animator.setDuration(300);
        animator.x(focusView.getX() + indicator.getLeft());
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        updateTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
