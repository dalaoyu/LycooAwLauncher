package com.lycoo.commons.widget.recycler;


import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by lancy on 2018/3/17
 */
public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = CustomRecyclerViewAdapter.class.getSimpleName();

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_REFRESH_HEADER = 1;
    private static final int TYPE_FOOTER = 2;
    private static final int BASE_HEADER_INDEX = 10000;

    private View mRefreshHeaderView;
    private View mFooterView;
    private List<Integer> mHeaderTypes = new ArrayList<>();
    private List<View> mHeaderViews = new ArrayList<>();

    private RecyclerView.Adapter mWrappedAdapter;
    private CustomRecyclerView.OnItemClickListener mOnItemClickListener;
    private CustomRecyclerView.OnItemLongClickListener mOnItemLongClickListener;
    private CustomRecyclerView.OnItemFocusChangeListener mOnItemFocusChangeListener;

    private SpanSizeLookup mSpanSizeLookup;

    public CustomRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        this.mWrappedAdapter = adapter;
    }

    /* Header -------------------------------------------------------------------------- */
    public void addHeaderView(View view) {
        if (view == null) {
            throw new RuntimeException("view is null");
        }
        mHeaderTypes.add(BASE_HEADER_INDEX + mHeaderViews.size());
        mHeaderViews.add(view);
    }

    public void removeHeaderView(View view) {
        if (mHeaderViews.contains(view)) {
            mHeaderViews.remove(view);
            // TODO: 2018/3/17 process mHeaderTypes
        }
        this.notifyDataSetChanged();
    }

    private boolean isHeaderType(int viewType) {
        return mHeaderViews.size() > 0
                && mHeaderTypes.contains(viewType);
    }

    public boolean isHeader(int position) {
        return position >= getRefreshHeaderViewCount()
                && position < mHeaderViews.size() + getRefreshHeaderViewCount();
    }

    public int getHeaderViewsCount() {
        return mHeaderViews.size();
    }

    private View getHeaderViewByType(int itemType) {
        if (!isHeaderType(itemType)) {
            return null;
        }
        return mHeaderViews.get(itemType - BASE_HEADER_INDEX);
    }

    /* Refresh Header -------------------------------------------------------------------------- */
    public void setRefreshHeaderView(View view) {
        if (view == null) {
            throw new RuntimeException("can't set refresh header view, becuase view is null");
        }
        mRefreshHeaderView = view;
    }

    public boolean isRefreshHeader(int position) {
        return mRefreshHeaderView != null && position == 0;
    }

    public int getRefreshHeaderViewCount() {
        return mRefreshHeaderView != null ? 1 : 0;
    }

    private View getRefreshHeaderView() {
        return mRefreshHeaderView;
    }

    /* Footer --------------------------------------------------------------------------------- */
    public void setFooterView(View view) {
        if (view == null) {
            throw new RuntimeException("can't set footer view, becuase view is null");
        }
        mFooterView = view;
    }

    public boolean isFooter(int position) {
        return mFooterView != null && position >= (getItemCount() - 1);
    }

    private int getFooterViewCount() {
        return mFooterView != null ? 1 : 0;
    }


    private View getFooterView() {
        return mFooterView;
    }

    /* Override --------------------------------------------------------------------------------- */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_REFRESH_HEADER) {
            return new ViewHolder(getRefreshHeaderView());
        } else if (isHeaderType(viewType)) {
            return new ViewHolder(getHeaderViewByType(viewType));
        } else if (viewType == TYPE_FOOTER) {
            return new ViewHolder(getFooterView());
        }
        return mWrappedAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isRefreshHeader(position) || isHeader(position)) {
            return;
        }

        if (mWrappedAdapter != null) {
            final int adjPosition = position - getHeaderViewsCount() - getRefreshHeaderViewCount();
            int adapterCount = mWrappedAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                mWrappedAdapter.onBindViewHolder(holder, adjPosition);

                if (mOnItemClickListener != null) {
                    holder.itemView.setOnClickListener(v ->
                            mOnItemClickListener.onItemClick(holder.itemView, adjPosition));
                }

                if (mOnItemLongClickListener != null) {
                    holder.itemView.setOnLongClickListener(v -> {
                        mOnItemLongClickListener.onItemLongClick(holder.itemView, adjPosition);
                        return true;
                    });
                }

                if (mOnItemFocusChangeListener != null) {
                    holder.itemView.setOnFocusChangeListener((v, hasFocus) ->
                            mOnItemFocusChangeListener.onItemFocusChange(holder.itemView, holder.itemView.hasFocus())
                    );
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            if (isRefreshHeader(position) || isHeader(position)) {
                return;
            }

            if (mWrappedAdapter != null) {
                final int adjPosition = position - getHeaderViewsCount() - getRefreshHeaderViewCount();
                int adapterCount = mWrappedAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    mWrappedAdapter.onBindViewHolder(holder, adjPosition, payloads);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mWrappedAdapter != null) {
            return getRefreshHeaderViewCount() + getHeaderViewsCount() + getFooterViewCount() + mWrappedAdapter.getItemCount();
        } else {
            return getRefreshHeaderViewCount() + getHeaderViewsCount() + getFooterViewCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isRefreshHeader(position)) {
            return TYPE_REFRESH_HEADER;
        }

        if (isHeader(position)) {
            return mHeaderTypes.get(position - getRefreshHeaderViewCount());
        }

        if (isFooter(position)) {
            return TYPE_FOOTER;
        }

        if (mWrappedAdapter != null) {
            int adapterCount = mWrappedAdapter.getItemCount();
            int adjPosition = position - getHeaderViewsCount() - getRefreshHeaderViewCount();
            if (adjPosition < adapterCount) {
                return mWrappedAdapter.getItemViewType(adjPosition);
            }
        }
        return TYPE_NORMAL;
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mSpanSizeLookup == null) {
                        return (isHeader(position) || isFooter(position) || isRefreshHeader(position))
                                ? gridManager.getSpanCount()
                                : 1;
                    } else {
                        return (isHeader(position) || isFooter(position) || isRefreshHeader(position))
                                ? gridManager.getSpanCount()
                                : mSpanSizeLookup.getSpanSize(gridManager, (position - (getHeaderViewsCount() + 1)));
                    }
                }
            });
        }
        mWrappedAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mWrappedAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            if (isHeader(holder.getLayoutPosition())
                    || isRefreshHeader(holder.getLayoutPosition())
                    || isFooter(holder.getLayoutPosition())) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
        mWrappedAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        mWrappedAdapter.onViewDetachedFromWindow(holder);
    }

    public RecyclerView.Adapter getWrappedAdapter() {
        return mWrappedAdapter;
    }

    public void setmOnItemClickListener(CustomRecyclerView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setmOnItemLongClickListener(CustomRecyclerView.OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemFocusChangeListener(CustomRecyclerView.OnItemFocusChangeListener onItemFocusChangeListener) {
        this.mOnItemFocusChangeListener = onItemFocusChangeListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface SpanSizeLookup {
        int getSpanSize(GridLayoutManager gridLayoutManager, int position);
    }
}
