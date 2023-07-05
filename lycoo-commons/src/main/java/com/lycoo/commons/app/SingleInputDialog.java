package com.lycoo.commons.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.lycoo.commons.R;
import com.lycoo.commons.domain.CommonConstants;

/**
 * 单输入框对话框
 *
 * Created by lancy on 2017/6/27
 */
public class SingleInputDialog extends Dialog {

    protected EditText et_data;
    protected Context mContext;
    private int mWidth;
    private int mHeight;

    public SingleInputDialog(Context context, int themeResId) {
        this(context, themeResId, 0, 0);
    }

    public SingleInputDialog(Context context, int themeResId, int width, int height) {
        super(context, themeResId);
        this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_single_input);
        setupDialog();

        initView();
        initData();
    }

    private void initView() {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), CommonConstants.CUSTOM_FONT_FLFBLS);
        et_data = findViewById(R.id.et_data);
        et_data.setTypeface(typeface);
        Button btn_ok = findViewById(R.id.btn_ok);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_ok.setTypeface(typeface);
        btn_cancel.setTypeface(typeface);

        btn_cancel.setOnClickListener(v -> dismiss());
        btn_ok.setOnClickListener(v -> execute());
    }

    /**
     * 初始化数据
     */
    protected void initData() {
        // 空实现
    }

    /**
     * 按确定后执行
     */
    protected void execute() {
        // 空实现
    }

    private void setupDialog() {
        // 1. 对话框尺寸和位置设置
        Window window = this.getWindow();
        // 保证dialog的宽高和设置的一致
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (mWidth == 0) {
            mWidth = mContext.getResources().getDimensionPixelSize(R.dimen.c_single_input_dialog_width);
        }
        if (mHeight == 0) {
            mHeight = mContext.getResources().getDimensionPixelSize(R.dimen.c_single_input_dialog_height);
        }
        layoutParams.width = mWidth;
        layoutParams.height = mHeight;
        window.setAttributes(layoutParams);

        // 2. 对话框特殊属性设置
    }
}
