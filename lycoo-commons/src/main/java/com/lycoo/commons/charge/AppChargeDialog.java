package com.lycoo.commons.charge;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.lycoo.commons.R;
import com.lycoo.commons.base.BaseDialog;
import com.lycoo.commons.domain.CommonConstants;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 收费对话框
 * <p>
 * Created by lancy on 2018/1/5
 */
public class AppChargeDialog extends BaseDialog {

    private static final String TAG = AppChargeDialog.class.getSimpleName();
    private Context mContext;
    private CompositeDisposable mCompositeDisposable;
    private Window window;
    private WebView mBg;


    public AppChargeDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_app_charge);

        setupDialog();
        initView();
    }

    private void setupDialog() {
        // 1. 对话框尺寸和位置设置
        window = this.getWindow();
        // 保证dialog的宽高和设置的一致
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.START;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setBackgroundDrawableResource(R.drawable.common_bg_app_update_dialog);
        window.setAttributes(layoutParams);

        // 2. 对话框特殊属性设置
        // 屏蔽返回键和点击空白处dismiss掉Dialog
        setCancelable(false);

    }

    /**
     * 初始化控件
     * <p>
     * Created by lancy on 2018/1/6 11:11
     */
    private void initView() {
        mBg = findViewById(R.id.wv_bg);
        mBg.getSettings().setJavaScriptEnabled(true);
        mBg.setWebViewClient(new WebViewClient());

        mBg.loadUrl(CommonConstants.LYCOO_LAUNCHER_CHARGE_WEBVIEW);

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mBg!=null){
            mBg.destroy();
        }

        if (mCompositeDisposable != null && mCompositeDisposable.size() > 0) {
            mCompositeDisposable.clear();
        }
    }
}
