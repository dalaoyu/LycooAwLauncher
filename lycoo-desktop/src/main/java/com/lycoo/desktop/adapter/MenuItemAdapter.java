package com.lycoo.desktop.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lycoo.desktop.ui.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lancy on 2018/4/18
 */
public class MenuItemAdapter extends BaseAdapter {

    private List<MenuItem> mMenuItems = new ArrayList<>();

    public MenuItemAdapter(List<MenuItem> menuItems) {
        mMenuItems.addAll(menuItems);
    }

    @Override
    public int getCount() {
        return mMenuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mMenuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mMenuItems.get(position).getView(convertView, parent);
        view.setEnabled(isEnabled(position));
        return view;
    }


}
