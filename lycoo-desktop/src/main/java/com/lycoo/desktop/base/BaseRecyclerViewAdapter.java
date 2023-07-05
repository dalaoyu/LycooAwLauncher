package com.lycoo.desktop.base;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.lycoo.commons.util.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lancy on 2018/3/14
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<SuperViewHolder> {
    private static final String TAG = BaseRecyclerViewAdapter.class.getSimpleName();

    private Map<Object, Integer> mMarkMap; // Map<标识，position>
    private boolean mMarkable;
    private List<T> mDataList;

    public BaseRecyclerViewAdapter() {
        this(false);
    }

    public BaseRecyclerViewAdapter(boolean markable) {
        mDataList = new ArrayList<>();
        mMarkable = markable;
        if (mMarkable) {
            mMarkMap = new HashMap<>();
        }
    }

    protected abstract SuperViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindItemViewHolder(SuperViewHolder holder, int position);

    protected void onBindItemViewHolder(SuperViewHolder holder, int position, List<Object> payloads) {
        // do nothing
    }

    @Override
    public SuperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateItemViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(SuperViewHolder holder, int position) {
        onBindItemViewHolder(holder, position);
    }

    @Override
    public void onBindViewHolder(SuperViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            onBindItemViewHolder(holder, position, payloads);
        }
    }

    @Override
    public int getItemCount() {
        return this.mDataList.size();
    }

    public List<T> getDataList() {
        return this.mDataList;
    }

    public void setData(Collection<T> data) {
        this.mDataList.clear();
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    public void add(T obj) {
        int lastIndex = this.mDataList.size();
        if (mDataList.add(obj)) {
            notifyItemRangeInserted(lastIndex, 1);
        }
    }

    public void add(Collection<T> list) {
        int lastIndex = this.mDataList.size();
        if (this.mDataList.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }

    public void remove(int position) {
        this.mDataList.remove(position);
        notifyItemRemoved(position);

        if (position != (this.mDataList.size())) { // 如果移除的是最后一个，忽略
            LogUtils.debug(TAG, "moved " + position + " , so notifyItemRangeChanged......");
            notifyItemRangeChanged(position, this.mDataList.size() - position);
        }
    }

    public void remove(T object) {
        int position = this.mDataList.indexOf(object);
        this.mDataList.remove(position);
        notifyItemRemoved(position);

        if (position != (this.mDataList.size())) { // 如果移除的是最后一个，忽略
            LogUtils.debug(TAG, "moved " + position + " , so notifyItemRangeChanged......");
            notifyItemRangeChanged(position, this.mDataList.size() - position);
        }
    }

    public void clear() {
        this.mDataList.clear();
        notifyDataSetChanged();
    }

    protected boolean isMarkable() {
        return mMarkable;
    }

    protected void mark(Object obj, int position) {
        mMarkMap.put(obj, position);
    }

    /**
     * 获取Item的位置
     * 在绑定数据的时候， 用一个Map来维护Item和Position
     *
     * @param obj Map的key
     * @return Item的位置
     *
     * Created by lancy on 2018/4/25 10:35
     */
    public Integer getPosition(Object obj) {
        return mMarkMap.get(obj);
    }

    public void onDestroy() {

    }
}
