package com.lycoo.desktop.base;


import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by lancy on 2018/3/14
 */
public class SuperViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;

    public SuperViewHolder(View itemView) {
        super(itemView);
        this.mViews = new SparseArray<>();
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


}
