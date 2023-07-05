package com.lycoo.desktop.base;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.desktop.R;
import com.lycoo.desktop.R2;
import com.lycoo.desktop.adapter.AppsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * 应用列表基类
 *
 * Created by lancy on 2018/6/19
 */
public class BaseAppsActivity extends BaseActivity {
    private static final String TAG = BaseAppsActivity.class.getSimpleName();

    @BindView(R2.id.tv_prompts) protected TextView mPromptText;
    @BindView(R2.id.ib_back) protected ImageButton mBackButton;
    @BindView(R2.id.tv_title) protected TextView mTitleText;
    @BindView(R2.id.gv_apps) protected GridView mAppsGridView;
    @BindView(R2.id.tv_empty) protected TextView mEmptyText;
    @BindView(R2.id.pb_loading) protected ProgressBar mLoadingProgressBar;

    protected Context mContext = this;
    protected AppsAdapter mAppsAdapter;
    protected List<ResolveInfo> mResolveInfos;
    protected CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_apps);
        ButterKnife.bind(this);

        initData();
        initView();
        loadData();
    }

    protected void initData() {
        mCompositeDisposable = new CompositeDisposable();
    }

    protected void initView() {
        mPromptText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mTitleText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mEmptyText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mEmptyText.setText(R.string.no_more_apps);

        mResolveInfos = new ArrayList<>();
        mAppsAdapter = new AppsAdapter(mContext, mResolveInfos);
        mAppsGridView.setAdapter(mAppsAdapter);
        mAppsGridView.setEmptyView(mEmptyText);
        mAppsGridView.setNumColumns(5);
        mAppsGridView.setOnItemClickListener((parent, view, position, id) -> ApplicationUtils.openApplication(mContext, mResolveInfos.get(position).activityInfo.packageName));
        mAppsGridView.setOnItemLongClickListener((parent, view, position, id) -> {
            onItemLongClick(position);
            // 如果return false 则会调用onItemClick
            return true;
        });
    }

    protected void loadData() {

    }

    protected void onItemLongClick(int position) {

    }

    protected void refresh() {
        mAppsAdapter.notifyDataSetChanged();
        mAppsGridView.requestFocus();
        ViewUtils.setViewShown(false, mLoadingProgressBar);
    }


    @OnClick(R2.id.ib_back)
    public void back() {
        finish();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

}
