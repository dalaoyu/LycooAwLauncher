package com.lycoo.commons.widget.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.lycoo.commons.util.CalculateUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.widget.LoadFooter;
import com.lycoo.commons.widget.RefreshHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义CustomRecyclerView
 * 支持：
 * 1. 下拉刷新
 * 2. 分页加载
 *
 * Created by lancy on 2018/3/17
 */
public class CustomRecyclerView extends RecyclerView {
    private static final String TAG = CustomRecyclerView.class.getSimpleName();
    private static final boolean DEBUG_ADAPTERDATAOBSERVER = false;

    private static final int HIDE_THRESHOLD = 20;
    private static final float DRAG_RATE = 2.0f;

    public enum LayoutManagerType {
        LINEAR_LAYOUT,          // Linear
        GRID_LAYOUT,            // Grid
        STAGGERED_GRID_LAYOUT   // straggred grid（瀑布流）
    }

    private LayoutManagerType mLayoutManagerType;

    /**
     * 加载数据方向
     */
    public enum LoadOrientation {
        VERTICAL,    // 垂直，使用LoadFooter
        HORIZONTAL   // 左右, 使用PageIndicator
    }
    private LoadOrientation mLoadOrientation = LoadOrientation.VERTICAL;

    /**
     * 是否支持分页
     */
    private boolean mPageable = false;

    /**
     * 单页记录数
     */
    private int mPageSize;

    /**
     * 是否正在加载数据
     */
    private boolean mLoading = false;

    /**
     * 是否已加载全部数据
     */
    private boolean mEnd = false;

    /**
     * 加载接口
     */
    private LoadFooter mLoadFooter;

    /**
     * 是否支持下拉刷新
     */
    private boolean mPullRefreshable = false;

    /**
     * 是否正在刷新
     */
    private boolean mRefreshing = false;

    /**
     * 刷新接口
     */
    private RefreshHeader mRefreshHeader;

    private OnRefreshListener mOnRefreshListener;
    private OnLoadListener mOnLoadListener;
    private OnErrorListener mOnErrorListener;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private int mLastVisibleItemPosition;
    private int[] mLastVisibleItemPositions;
    private int mDistance = 0;
    private int mCurScrollState = SCROLL_STATE_IDLE;
    private int mScrolledYDistance = 0;
    private int mScrolledXDistance = 0;
    private boolean mScrollDown = true;
    private OnScrollListener mOnScrollListener;
    private boolean mIsVpDragger;
    private int mTouchSlop;
    private float mStartY;
    private float mLastY = -1;
    private float mStartX;
    private float mSumOffSet;

    private final CustomAdapterDataObserver mDataObserver = new CustomAdapterDataObserver();
    private CustomRecyclerViewAdapter mAdapter;
    private View mRefreshHeaderView;
    private View mFooterView;
    private List<View> mHeaderViews = new ArrayList<>();
    private View mEmptyView;


    public CustomRecyclerView(Context context) {
        this(context, null);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext().getApplicationContext()).getScaledTouchSlop();
    }

    public void setAdapter(CustomRecyclerViewAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.getWrappedAdapter().unregisterAdapterDataObserver(mDataObserver);
        }
        mAdapter = adapter;
        super.setAdapter(mAdapter);
        mAdapter.getWrappedAdapter().registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();

        // Set RefreshHeader2 view if necessary
        if (mRefreshHeaderView != null) {
            mAdapter.setRefreshHeaderView(mRefreshHeaderView);
        }

        // Set Footer view if necessary
        if (mFooterView != null) {
            mAdapter.setFooterView(mFooterView);
        }

        // Set Header views if necessary
        if (!mHeaderViews.isEmpty()) {
            for (View headerView : mHeaderViews) {
                mAdapter.addHeaderView(headerView);
            }
        }

        // Set OnItemClickListener if necessary
        if (mOnItemClickListener != null) {
            mAdapter.setmOnItemClickListener(mOnItemClickListener);
        }

        // Set OnItemLongClickListener if necessary
        if (mOnItemLongClickListener != null) {
            mAdapter.setmOnItemLongClickListener(mOnItemLongClickListener);
        }
    }

    public void setRefreshHeader(RefreshHeader refreshHeader) {
        mRefreshHeader = refreshHeader;
        mRefreshHeaderView = refreshHeader.getHeaderView();
        if (mAdapter != null) {
            mAdapter.setRefreshHeaderView(mRefreshHeaderView);
        }
    }

    public void setLoadFooter(LoadFooter footer) {
        mLoadFooter = footer;
        mFooterView = footer.getFooterView();
        if (mAdapter != null) {
            mAdapter.setFooterView(mFooterView);
        }
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
        if (mAdapter != null) {
            mAdapter.addHeaderView(view);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        if (mAdapter != null) {
            mAdapter.setmOnItemClickListener(mOnItemClickListener);
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
        if (mAdapter != null) {
            mAdapter.setmOnItemLongClickListener(mOnItemLongClickListener);
        }
    }

    public void setPageable(boolean pageable) {
        this.mPageable = pageable;
    }

    public void setPageSize(int pageSize) {
        this.mPageSize = pageSize;
    }

    public void setLoadOrientation(LoadOrientation loadOrientation) {
        this.mLoadOrientation = loadOrientation;
    }

    public void setPullRefreshable(boolean pullRefreshable) {
        this.mPullRefreshable = pullRefreshable;
    }

    public void setRefreshing(boolean refreshing) {
        this.mRefreshing = refreshing;
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.mOnLoadListener = onLoadListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setEmptyView(View view) {
        this.mEmptyView = view;
//        if (mAdapter != null) {
//            mAdapter.setEmptyView(view);
//        }
    }

    /**
     * 刷新数据
     *
     * Created by lancy on 2019/9/4 16:14
     */
    public void refresh() {
        if (mRefreshing/* || mRefreshHeader.getVisibleHeight() > 0*/) { // if RefreshHeader2 is Refreshing, return
            return;
        }

        if (mPullRefreshable && mOnRefreshListener != null) {
            mRefreshing = true;

            if (mRefreshHeader != null) {
                mRefreshHeader.onRefreshing();
//                int offSet = mRefreshHeader.getHeaderView().getMeasuredHeight();
//                mRefreshHeader.onMove(offSet, offSet);
            }

            if (mLoadFooter != null) {
                mLoadFooter.onNormal();
            }

            mOnRefreshListener.onRefresh();
        }
    }

    /**
     * 强制刷新数据
     *
     * Created by lancy on 2019/9/4 16:14
     */
    public void forceToRefresh() {
        if (mLoading) {
            return;
        }

        refresh();
    }

    /**
     * 数据加载完成
     *
     * Created by lancy on 2019/9/4 16:14
     */
    public void loadComplete() {
        if (mRefreshing) {
            mRefreshing = false;
            mEnd = false;
            if (mRefreshHeader != null) {
                mRefreshHeader.refreshComplete();
            }

            if (mLoadFooter != null && mAdapter.getWrappedAdapter().getItemCount() < mPageSize) {
                mLoadFooter.onNormal();
            }
        } else if (mLoading) {
            mLoading = false;
            if (mLoadFooter != null) {
                mLoadFooter.onComplete();
            }
        }

        checkEmptyView();
    }

    /**
     * 数据加载出错
     *
     * Created by lancy on 2019/9/4 16:15
     */
    public void loadError() {
        if (mLoadFooter != null) {
            // Change ok
            mLoadFooter.onError();
            // Set OnclickListener
            mLoadFooter.getFooterView().setOnClickListener(v -> {
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError();
                }
            });
        }
    }

    /**
     * 所有数据加载完成
     *
     * Created by lancy on 2019/9/4 16:16
     */
    public void loadAllDataDone() {
        mEnd = true;
        mLoading = false;
        if (mLoadFooter != null) {
            mLoadFooter.onEnd();
        }
    }

    /**
     * 解决嵌套RecyclerView滑动冲突问题
     *
     * @param motionEvent 触摸事件
     *
     *                    Created by lancy on 2019/9/4 16:16
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下的位置
                mStartY = motionEvent.getY();
                mStartX = motionEvent.getX();
                // 初始化标记
                mIsVpDragger = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if (mIsVpDragger) {
                    return false;
                }

                // 获取当前手指位置
                float endY = motionEvent.getY();
                float endX = motionEvent.getX();
                float distanceX = Math.abs(endX - mStartX);
                float distanceY = Math.abs(endY - mStartY);
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDragger = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 初始化标记
                mIsVpDragger = false;
                break;
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(motionEvent);
    }

    /**
     * 处理下拉刷新
     *
     * @param motionEvent 触摸事件
     *
     *                    Created by lancy on 2019/9/4 16:11
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (mPullRefreshable && mRefreshHeader != null) {
            if (mLastY == -1) {
                mLastY = motionEvent.getRawY();
            }
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastY = motionEvent.getRawY();
                    mSumOffSet = 0;
                    break;

                case MotionEvent.ACTION_MOVE:
                    final float deltaY = (motionEvent.getRawY() - mLastY) / DRAG_RATE;
                    mLastY = motionEvent.getRawY();
                    mSumOffSet += deltaY;
                    if (isOnTop() && !mRefreshing) {
                        mRefreshHeader.onMove(deltaY, mSumOffSet);
                        /*
                        if (mRefreshHeader.getVisibleHeight() > 0) {
                            return false;
                        }
                        */
                    }
                    break;

                default:
                    mLastY = -1; // reset
                    if (isOnTop() && !mRefreshing && mRefreshHeader.onRelease()) {
                        if (mOnRefreshListener != null) {
                            mRefreshing = true;

                            if (mLoadFooter != null) {
                                mLoadFooter.onNormal();
                            }

                            mOnRefreshListener.onRefresh();
                        }
                    }
                    break;
            }
        }

        return super.onTouchEvent(motionEvent);
    }

    public boolean isOnTop() {
        return mRefreshHeader != null && mRefreshHeader.getHeaderView().getParent() != null;
    }

    /**
     * 处理数据加载
     *
     * @param dx x轴滚动距离
     * @param dy y轴滚动距离
     *
     *           Created by lancy on 2019/9/4 16:12
     */
    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        int firstVisibleItemPosition = 0;
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (mLayoutManagerType == null) {
            if (layoutManager instanceof GridLayoutManager) {
                mLayoutManagerType = LayoutManagerType.GRID_LAYOUT;
            } else if (layoutManager instanceof LinearLayoutManager) {
                mLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                mLayoutManagerType = LayoutManagerType.STAGGERED_GRID_LAYOUT;
            } else {
                throw new RuntimeException(
                        "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }
        switch (mLayoutManagerType) {
            case LINEAR_LAYOUT:
                firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                mLastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GRID_LAYOUT:
                firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                mLastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case STAGGERED_GRID_LAYOUT:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (mLastVisibleItemPositions == null) {
                    mLastVisibleItemPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(mLastVisibleItemPositions);
                mLastVisibleItemPosition = CalculateUtils.findMax(mLastVisibleItemPositions);
                staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(mLastVisibleItemPositions);
                firstVisibleItemPosition = CalculateUtils.findMax(mLastVisibleItemPositions);
                break;
        }

        // 根据类型来计算出第一个可见的item的位置，由此判断是否触发到底部的监听器
        // 计算并判断当前是向上滑动还是向下滑动
        calculateScrollUpOrDown(firstVisibleItemPosition, dy);

        // 移动距离超过一定的范围，我们监听就没有啥实际的意义了
        mScrolledXDistance += dx;
        mScrolledYDistance += dy;
        mScrolledXDistance = (mScrolledXDistance < 0) ? 0 : mScrolledXDistance;
        mScrolledYDistance = (mScrolledYDistance < 0) ? 0 : mScrolledYDistance;
        if (mScrollDown && (dy == 0)) {
            mScrolledYDistance = 0;
        }

        // Be careful in here
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrolled(mScrolledXDistance, mScrolledYDistance);
        }

        if (mOnLoadListener != null && mPageable) {
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            if (visibleItemCount > 0
                    && mLastVisibleItemPosition >= totalItemCount - 1
                    && totalItemCount > visibleItemCount
                    && !mEnd
                    && !mRefreshing) {
                if (!mLoading) {
                    LogUtils.error(TAG, "show footer......");
                    mLoading = true;
                    // Show Footer
                    if (mLoadFooter != null) {
                        mLoadFooter.onLoading();
                    }

                    // Invoke onLoad
                    mOnLoadListener.onLoad();
                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        mCurScrollState = state;

        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(state);
        }
    }

    private void calculateScrollUpOrDown(int firstVisibleItemPosition, int dy) {
        if (mOnScrollListener != null) {
            if (firstVisibleItemPosition == 0) {
                if (!mScrollDown) {
                    mScrollDown = true;
                    mOnScrollListener.onScrollDown();
                }
            } else {
                if (mDistance > HIDE_THRESHOLD && mScrollDown) {
                    mScrollDown = false;
                    mOnScrollListener.onScrollUp();
                    mDistance = 0;
                } else if (mDistance < -HIDE_THRESHOLD && !mScrollDown) {
                    mScrollDown = true;
                    mOnScrollListener.onScrollDown();
                    mDistance = 0;
                }
            }
        }

        if ((mScrollDown && dy > 0) || (!mScrollDown && dy < 0)) {
            mDistance += dy;
        }
    }

    private class CustomAdapterDataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            if (DEBUG_ADAPTERDATAOBSERVER) {
                LogUtils.info(TAG, "onChanged()......................");
            }
            checkEmptyView();

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
                if (mLoadFooter != null
                        && mAdapter.getWrappedAdapter().getItemCount() < mPageSize) {
                    mLoadFooter.onNormal();
                }
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (DEBUG_ADAPTERDATAOBSERVER) {
                LogUtils.info(TAG, "onItemRangeChanged()......................");
            }
            checkEmptyView();

            mAdapter.notifyItemRangeChanged(
                    positionStart
                            + mAdapter.getHeaderViewsCount()
                            + mAdapter.getRefreshHeaderViewCount(),
                    itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
//            checkEmptyView();
            if (DEBUG_ADAPTERDATAOBSERVER) {
                LogUtils.verbose(TAG, "onItemRangeChanged(payload)......................");
            }
            mAdapter.notifyItemRangeChanged(
                    positionStart
                            + mAdapter.getHeaderViewsCount()
                            + mAdapter.getRefreshHeaderViewCount(),
                    itemCount,
                    payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (DEBUG_ADAPTERDATAOBSERVER) {
                LogUtils.verbose(TAG, "onItemRangeInserted......................");
            }
            checkEmptyView();

            mAdapter.notifyItemRangeInserted(
                    positionStart
                            + mAdapter.getHeaderViewsCount()
                            + mAdapter.getRefreshHeaderViewCount(),
                    itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (DEBUG_ADAPTERDATAOBSERVER) {
                LogUtils.verbose(TAG, "onItemRangeRemoved......................");
            }
            checkEmptyView();

            mAdapter.notifyItemRangeRemoved(
                    positionStart
                            + mAdapter.getHeaderViewsCount()
                            + mAdapter.getRefreshHeaderViewCount(),
                    itemCount);
            if (mLoadFooter != null && mAdapter.getWrappedAdapter().getItemCount() < mPageSize) {
                mLoadFooter.onNormal();
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (DEBUG_ADAPTERDATAOBSERVER) {
                LogUtils.verbose(TAG, "onItemRangeMoved......................");
            }
            checkEmptyView();

            mAdapter.notifyItemRangeChanged(
                    fromPosition
                            + mAdapter.getHeaderViewsCount()
                            + mAdapter.getRefreshHeaderViewCount(),
                    toPosition
                            + mAdapter.getHeaderViewsCount()
                            + mAdapter.getRefreshHeaderViewCount()
                            + itemCount);
            if (mLoadFooter != null && mAdapter.getWrappedAdapter().getItemCount() < mPageSize) {
                mLoadFooter.onNormal();
            }
        }
    }

    /**
     * 处理EmptyView
     *
     * Created by lancy on 2019/9/4 16:18
     */
    private void checkEmptyView() {
        // 正在刷新时不处理EmptyView
        if (mRefreshing) {
            return;
        }

        Adapter<?> adapter = getAdapter();
        if (adapter instanceof CustomRecyclerViewAdapter) {
            CustomRecyclerViewAdapter customRecyclerViewAdapter = (CustomRecyclerViewAdapter) adapter;
            if (customRecyclerViewAdapter.getWrappedAdapter() != null && mEmptyView != null) {
                int adapterCount = customRecyclerViewAdapter.getWrappedAdapter().getItemCount();
                LogUtils.debug(TAG, "onChanged, adapterCount = " + adapterCount);
                ViewUtils.setViewShown(adapterCount == 0, mEmptyView);
//                ViewUtils.setViewShown(adapterCount != 0, CustomRecyclerView.this);
            }
        } else {
            if (adapter != null && mEmptyView != null) {
                ViewUtils.setViewShown(adapter.getItemCount() == 0, mEmptyView);
//                ViewUtils.setViewShown(adapter.getItemCount() != 0, CustomRecyclerView.this);
            }
        }
    }

    public boolean isRefreshing() {
        return mRefreshing;
    }

    /* interface ---------------------------------------------------------------------------- */
    public interface OnLoadListener {
        void onLoad();
    }

    public interface OnErrorListener {
        void onError();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public interface OnItemFocusChangeListener {
        void onItemFocusChange(View v, boolean hasFocus);
    }

    public interface OnScrollListener {

        void onScrollUp();//scroll down to up

        void onScrollDown();//scroll from up to down

        void onScrolled(int distanceX, int distanceY);// moving ok,you can get the move distance

        void onScrollStateChanged(int state);
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

}
