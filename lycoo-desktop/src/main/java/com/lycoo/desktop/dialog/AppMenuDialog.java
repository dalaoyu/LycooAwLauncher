package com.lycoo.desktop.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lycoo.commons.base.BaseDialog;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.desktop.R;
import com.lycoo.desktop.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * 操作对话框
 *
 * Created by lancy on 2018/1/6 15:56
 */
@SuppressWarnings("ALL")
public abstract class AppMenuDialog extends BaseDialog {
    private static final String TAG = AppMenuDialog.class.getSimpleName();

    @BindView(R2.id.bind) RelativeLayout mBindMenuItem;
    @BindView(R2.id.replace) RelativeLayout mReplaceMenuItem;
    @BindView(R2.id.unbind) RelativeLayout mUnbindMenuItem;
    @BindView(R2.id.uninstall) RelativeLayout mUninstallMenuItem;
    @BindView(R2.id.tv_bind) TextView mBindText;
    @BindView(R2.id.tv_replace) TextView mReplaceText;
    @BindView(R2.id.tv_unbind) TextView mUnbindText;
    @BindView(R2.id.tv_uninstall) TextView mUninstallText;

    protected Context mContext;
    protected AppsDialog mAppsDialog;
    protected CompositeDisposable mCompositeDisposable;

    public AppMenuDialog(Context context, int themeResId) {
        super(context, themeResId);

        this.mContext = context;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_app_menu);
        ButterKnife.bind(this);

        setupDialog();
        initView();
    }

    private void setupDialog() {
        Window window = this.getWindow();
        // 设置dialog 显示和退出动画
        window.setGravity(Gravity.END);
        window.setWindowAnimations(R.style.DesktopItemMenuDialogAnimationStyle);
        // 保证dialog的宽高和设置的一致
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.app_menu_dialog_width);
        window.setAttributes(layoutParams);
    }

    @SuppressLint("StaticFieldLeak")
    protected void initView() {
        mBindText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mReplaceText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mUnbindText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mUninstallText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
    }

    /**
     * 添加或替换
     *
     * Created by lancy on 2017/3/14
     */
    @OnClick({R2.id.bind, R2.id.replace})
    public void bindOrReplace() {
        showAppsDialog();
        dismiss();
    }

    /**
     * 移除
     *
     * Created by lancy on 2018/1/6 15:30
     */
    @OnClick(R2.id.unbind)
    protected void unbind() {
        dismiss();
    }

    /**
     * 卸载应用
     *
     * Created by lancy on 2018/6/20 16:04
     */
    @OnClick(R2.id.uninstall)
    protected void uninstall() {
        dismiss();
    }


    /**
     * 显示应用对话框
     *
     * Created by lancy on 2018/1/6 15:47
     */
    protected void showAppsDialog() {
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.verbose(TAG, "onStop()......");

        mCompositeDisposable.clear();
    }
}
