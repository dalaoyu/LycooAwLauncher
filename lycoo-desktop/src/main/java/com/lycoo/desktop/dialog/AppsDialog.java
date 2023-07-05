package com.lycoo.desktop.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lycoo.commons.base.BaseDialog;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.ResourceUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.desktop.R;
import com.lycoo.desktop.R2;
import com.lycoo.desktop.adapter.AppsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 应用对话框
 *
 * Created by lancy on 2018/1/6 15:55
 */
public abstract class AppsDialog extends BaseDialog implements AdapterView.OnItemClickListener {
    private static final String TAG = AppsDialog.class.getSimpleName();

    @BindView(R2.id.tv_prompts) TextView tv_prompts;
    @BindView(R2.id.gv_apps) GridView gv_apps;
    @BindView(R2.id.tv_empty) TextView tv_empty;
    @BindView(R2.id.pb_loading) ProgressBar pb_loading;

    private AppsAdapter mAppsAdapter;

    protected Context mContext;
    private List<ResolveInfo> mResolveInfos;
    private int mNumColumns;
    protected CompositeDisposable mCompositeDisposable;

    AppsDialog(Context context, int themeResId, int numColumns) {
        super(context, themeResId);
        this.mContext = context;
        this.mNumColumns = numColumns;

        mCompositeDisposable = new CompositeDisposable();
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_apps);
        ButterKnife.bind(this);

        setupDialog();
        initData();
        initView();
        loadData();
    }

    @Override
    protected void setupFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @SuppressWarnings("ConstantConditions")
    private void setupDialog() {
        Window window = this.getWindow();
        // 设置dialog 显示和退出动画
        window.setGravity(Gravity.START);
        window.setWindowAnimations(R.style.AppsDialogAnimationLRStyle);
        // 保证dialog的宽高和设置的一致
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.apps_dialog_width);
        window.setAttributes(layoutParams);
    }

    /**
     * 初始化数据
     *
     * Created by lancy on 2018/1/6 15:40
     */
    private void initData() {
        mResolveInfos = new ArrayList<>();
    }

    /**
     * 初始化控件
     *
     * Created by lancy on 2018/1/6 15:39
     */
    private void initView() {
        tv_prompts.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        tv_empty.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        tv_empty.setText(R.string.no_more_apps);
        gv_apps.setEmptyView(tv_empty);

        mAppsAdapter = new AppsAdapter(mContext, mResolveInfos);
        gv_apps.setAdapter(mAppsAdapter);
        gv_apps.setNumColumns(mNumColumns);
        gv_apps.setOnItemClickListener(this);
    }

    /**
     * 获取已经显示在界面上的应用包名
     *
     * Created by lancy on 2018/5/11 15:58
     */
    abstract Observable<List<String>> getPackageNames();

    /**
     * 更新
     * 1. 更新数据库
     * 2. 更新界面
     *
     * @param resolveInfo 选中应用的信息
     *
     *                    Created by lancy on 2018/5/11 16:02
     */
    abstract void doUpdate(ResolveInfo resolveInfo);

    /**
     * 加载应用
     *
     * Created by lancy on 2018/1/6 13:12
     */
    protected void loadData() {
        mCompositeDisposable.add(
                getPackageNames()
                        .subscribeOn(Schedulers.io())
                        .map(packageNames -> {
                            List<String> ignorePackageNameList = new ArrayList<>();

                            // 过滤系统之不允许显示的应用
                            Resources resources = mContext.getResources();
                            ignorePackageNameList.addAll(Arrays.asList(resources.getStringArray(ResourceUtils.getIdByName(
                                    mContext,
                                    "array",
                                    "translate_apps"
                            ))));

                            // 过滤掉已经显示在MainUI上面的应用
                            ignorePackageNameList.addAll(packageNames);

                            List<ResolveInfo> resolveInfos = new ArrayList<>();
                            List<ResolveInfo> launcherResolveInfos = ApplicationUtils.getAllLauncherResolveInfos(mContext);
                            for (ResolveInfo resolveInfo : launcherResolveInfos) {
                                String packageName = resolveInfo.activityInfo.applicationInfo.packageName;
                                if (ignorePackageNameList.contains(packageName)) {
                                    continue;
                                }
                                resolveInfos.add(resolveInfo);
                            }

                            return resolveInfos;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(resolveInfos -> {
                            mResolveInfos.clear();
                            mResolveInfos.addAll(resolveInfos);
                            mAppsAdapter.notifyDataSetChanged();
                            gv_apps.requestFocus();

                            ViewUtils.setViewShown(false, pb_loading);
                        }));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ResolveInfo resolveInfo = mResolveInfos.get(position);
        if (resolveInfo == null) {
            return;
        }
        doUpdate(resolveInfo);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }
    }
}
