package com.lycoo.desktop.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.lycoo.commons.helper.RxBus;
import com.lycoo.commons.util.CollectionUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.widget.CustomHorizontalScrollView;
import com.lycoo.commons.widget.MetroViewBorderHandler;
import com.lycoo.desktop.bean.DesktopItemInfo;
import com.lycoo.desktop.helper.DesktopEvent;
import com.lycoo.desktop.helper.DesktopItemManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 页面
 * 代表桌面的一个页面，不同的桌面可能有不同的页，例如 “推荐”, “频道”， “应用” 等等。
 *
 * Created by lancy on 2017/12/9
 */
public class Page extends FrameLayout {
    private static final String TAG = Page.class.getSimpleName();
    private static final boolean DEBUG_UI = false;

    protected CustomHorizontalScrollView mCustomHorizontalScrollView;
    protected ViewGroup mItemsContainer;

    protected Context mContext;
    protected String mPageLabel;
    protected int mPageNumber;
    protected int mMinTag;
    protected int mMaxTag;
    protected SparseArray<DesktopItem> mDesktopItems;
    public int mFocusItemId;

    protected MetroViewBorderHandler mMetroViewBorder;

    public Page(Context context, String pageLabel, int pageNumber, int pageCount) {
        super(context);

        mContext = context;
        mPageLabel = pageLabel;
        mPageNumber = pageNumber;
        mMinTag = pageNumber + 1;
        mMaxTag = pageNumber + pageCount;

        mDesktopItems = new SparseArray<>();

        initView();
        loadItems();
    }

    /**
     * 初始化控件
     *
     * Created by lancy on 2017/12/16 16:47
     */
    private void initView() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initHorizontalScrollView();
        initItemsContainer();
        initItems();
        initItemsBg();
        initMetroViewBorder();
    }

    /**
     * 初始化HorizontalScrollView
     *
     * Created by lancy on 2017/12/11 11:41
     */
    private void initHorizontalScrollView() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mCustomHorizontalScrollView = new CustomHorizontalScrollView(mContext);
        mCustomHorizontalScrollView.setLayoutParams(params);
        mCustomHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        addView(mCustomHorizontalScrollView);
        if (DEBUG_UI) {
            mCustomHorizontalScrollView.setBackgroundColor(Color.RED);
        }
    }

    /**
     * 创建items容器
     *
     * Created by lancy on 2018/5/8 17:50
     */
    private void initItemsContainer() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mItemsContainer = new RelativeLayout(mContext);
        mItemsContainer.setLayoutParams(params);
        mCustomHorizontalScrollView.addView(mItemsContainer);
    }

    /**
     * 初始化坑位
     * 每个页面必须重写该方法，确定坑位的个数及摆放，子类必须首先调用父类方法，否则没有创建item容器
     *
     * Created by lancy on 2017/12/11 10:14
     */
    protected void initItems() {

    }

    /**
     * 创建移动框
     *
     * Created by lancy on 2018/5/8 17:51
     */
    protected void initMetroViewBorder() {

    }

    /**
     * 初始化坑位
     *
     * @param desktopItem 要初始化的坑位
     *
     *                    Created by lancy on 2017/12/13 18:18
     */
    protected void initItem(DesktopItem desktopItem) {
        // 初始化DesktopItem
        desktopItem.initView();
        mItemsContainer.addView(desktopItem);
        desktopItem.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mFocusItemId = v.getId();
            }
        });
        mDesktopItems.put(desktopItem.getId(), desktopItem);

        // 注册DesktopItem,方便统一管理
        DesktopItemManager
                .getInstance(mContext)
                .registerItem(desktopItem.getId(), desktopItem);
    }

    protected void initItemsBg() {

    }

    /**
     * 加载坑位信息
     *
     * Created by lancy on 2017/12/14 19:51
     */
    private void loadItems() {
        DesktopItemManager.getInstance(mContext)
                .getItemInfos(mMinTag, mMaxTag)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemInfos -> updateItems(itemInfos));
    }

    /**
     * 订阅坑位更新事件
     *
     * Created by lancy on 2017/12/16 16:46
     */
    private void subscribeUpdateItemEvent() {
        RxBus.getInstance().addDisposable(this, RxBus.getInstance()
                .registerSubscribe(
                        DesktopEvent.UpdateDesktopItemEvent.class,
                        updateDesktopItemEvent -> {
                            LogUtils.info(TAG, "subscribeUpdateItemEvent(), thread's name : " + Thread.currentThread().getName());
                            updateItems(updateDesktopItemEvent.getItemInfos());
                        },
                        throwable -> {

                        }));
    }

    /**
     * 更新坑位
     *
     * @param itemInfos 坑位信息
     *
     *                  Created by lancy on 2017/12/16 16:46
     */
    private void updateItems(List<DesktopItemInfo> itemInfos) {
        LogUtils.debug(TAG, mPageLabel + " : itemInfos = " + itemInfos);
        if (!CollectionUtils.isEmpty(itemInfos)) {
            for (DesktopItemInfo itemInfo : itemInfos) {
                DesktopItemManager
                        .getInstance(mContext)
                        .getItem(itemInfo.getTag())
                        .update(itemInfo);
            }
        }
    }

    /**
     * 获取当前页名称
     *
     * @return 当前页名称
     *
     * Created by lancy on 2017/12/12 17:48
     */
    public String getLabel() {
        return mPageLabel;
    }

    /**
     * 获取当前页页码
     *
     * @return 当前页页码
     *
     * Created by lancy on 2017/12/12 17:48
     */
    public int getNumber() {
        return mPageNumber;
    }

    /**
     * Set whether DesktopItems in the page can receive the focus.
     *
     * @param focusable If true, this view can receive the focus.
     *
     *                  Created by lancy on 2018/4/17 16:08
     */
    public void setItemsFocusable(boolean focusable) {
        for (int i = mMinTag; i <= mMaxTag; i++) {
            mDesktopItems.get(i).setFocusable(focusable);
        }
    }

    /**
     * 获取当前获得光标的坑位ID
     *
     * @return 当前获得光标的坑位ID
     *
     * Created by lancy on 2018/4/24 1:05
     */
    public int getFocusItemId() {
        return mFocusItemId;
    }
}
