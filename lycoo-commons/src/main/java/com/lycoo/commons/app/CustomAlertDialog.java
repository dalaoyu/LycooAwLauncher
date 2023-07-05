package com.lycoo.commons.app;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.lycoo.commons.R;
import com.lycoo.commons.base.BaseDialog;
import com.lycoo.commons.domain.CommonConstants;

/**
 * 提示对话框
 * <p>
 * Created by lancy on 2017/6/20
 */

public class CustomAlertDialog extends BaseDialog {

    private Context mContext;
    private int mWidth;
    private int mHeight;
    private String mMessage;
    private View.OnClickListener mPositiveButtonClickListener;
    private View.OnClickListener mNegativeButtonClickListener;

    private String positiveText;
    private String negativeText;

    public CustomAlertDialog(Context context, int themeResId, String message) {
        this(context, themeResId, 0, 0, message, null, null);
    }

    public CustomAlertDialog(Context context, int themeResId, int width, int height, String message) {
        this(context, themeResId, width, height, message, null, null);
    }

    public CustomAlertDialog(Context context, int themeResId, String message, View.OnClickListener positiveButtonClickListener) {
        this(context, themeResId, 0, 0, message, positiveButtonClickListener, null);
    }

    public CustomAlertDialog(Context context, int themeResId, String message, String positiveText, String negativeText, View.OnClickListener positiveButtonClickListener) {
        this(context, themeResId, 0, 0, message, positiveButtonClickListener, null);
        this.positiveText = positiveText;
        this.negativeText = negativeText;
    }

    public CustomAlertDialog(Context context, int themeResId, String message, String positiveText, String negativeText, View.OnClickListener positiveButtonClickListener, View.OnClickListener negativeButtonClickListener) {
        this(context, themeResId, 0, 0, message, positiveButtonClickListener, negativeButtonClickListener);
        this.positiveText = positiveText;
        this.negativeText = negativeText;
    }

    public CustomAlertDialog(Context context,
                             int themeResId,
                             String message,
                             View.OnClickListener positiveButtonClickListener,
                             View.OnClickListener negativeButtonClickListener) {
        this(context, themeResId, 0, 0, message, positiveButtonClickListener, negativeButtonClickListener);
    }

    public CustomAlertDialog(Context context,
                             int themeResId,
                             int width,
                             int height,
                             String message,
                             View.OnClickListener positiveButtonClickListener,
                             View.OnClickListener negativeButtonClickListener) {
        super(context, themeResId);
        mContext = context;
        mWidth = width;
        mHeight = height;
        mMessage = message;
        mPositiveButtonClickListener = positiveButtonClickListener;
        mNegativeButtonClickListener = negativeButtonClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_alert_dialog_custom);

        setupDialog();
        initView();
    }

    private void initView() {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), CommonConstants.CUSTOM_FONT_FLFBLS);
        TextView tv_msg = findViewById(R.id.tv_msg);
        tv_msg.setTypeface(typeface);
        tv_msg.setText(mMessage);

        Button btn_ok = findViewById(R.id.btn_ok);
        if (!TextUtils.isEmpty(positiveText)) {
            btn_ok.setText(positiveText);
        }
        btn_ok.setTypeface(typeface);
        btn_ok.setOnClickListener(mPositiveButtonClickListener != null ? mPositiveButtonClickListener : mDefaultOnClickListener);

        Button btn_canel = findViewById(R.id.btn_cancel);
        if (!TextUtils.isEmpty(negativeText)) {
            btn_canel.setText(negativeText);
        }
        btn_canel.setTypeface(typeface);
        btn_canel.setOnClickListener(mNegativeButtonClickListener != null ? mNegativeButtonClickListener : mDefaultOnClickListener);
    }

    private final View.OnClickListener mDefaultOnClickListener = v -> dismiss();

    private void setupDialog() {
        // 1. 对话框尺寸和位置设置
        Window window = this.getWindow();
        // 保证dialog的宽高和设置的一致
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (mWidth == 0) {
            mWidth = mContext.getResources().getDimensionPixelSize(R.dimen.c_custom_alert_dialog_width);
        }
        if (mHeight == 0) {
            mHeight = mContext.getResources().getDimensionPixelSize(R.dimen.c_custom_alert_dialog_height);
        }
        layoutParams.width = mWidth;
        layoutParams.height = mHeight;
        window.setAttributes(layoutParams);

        // 2. 对话框特殊属性设置
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }
}
