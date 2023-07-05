package com.lycoo.desktop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lycoo.commons.helper.StyleManager;
import com.lycoo.desktop.R;
import com.lycoo.desktop.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("InflateParams")
public class AppsAdapter extends BaseAdapter {

    private Context mContext;
    private List<ResolveInfo> mResolveInfoList;

    private PackageManager mPackageManager;

    public AppsAdapter(Context context, List<ResolveInfo> resolveInfoList) {
        this.mContext = context;
        this.mResolveInfoList = resolveInfoList;

        mPackageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return mResolveInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mResolveInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    final class ViewHolder {
        @BindView(R2.id.iv_bg) ImageView iv_bg;
        @BindView(R2.id.iv_icon) ImageView iv_icon;
        @BindView(R2.id.tv_label) TextView tv_label;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            tv_label.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (null == convertView) {
            view = newView();
        } else {
            view = convertView;
        }
        bindView(position, view);

        return view;
    }

    private View newView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.app_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    private void bindView(int position, View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.iv_icon.setImageDrawable(mResolveInfoList.get(position).loadIcon(mPackageManager));
        viewHolder.tv_label.setText(mResolveInfoList.get(position).loadLabel(mPackageManager));
    }

}
