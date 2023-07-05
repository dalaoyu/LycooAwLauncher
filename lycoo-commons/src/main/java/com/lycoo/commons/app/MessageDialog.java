package com.lycoo.commons.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lycoo.commons.R;
import com.lycoo.commons.domain.CommonConstants;

/**
 * 信息提示框，类似Toast
 *
 * Created by lancy on 2017/10/30
 */
public class MessageDialog extends Dialog {
    private static final String TAG = MessageDialog.class.getSimpleName();

    private static final int MILLIS_INFUTURE = 3000;
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private String mMessage;

    private CountDownTimer mCountDownTimer;

    public MessageDialog(Context context, int themeResId, String msg) {
        this(context, themeResId, 0, 0, msg);
    }

    public MessageDialog(Context context, int themeResId, int width, int height, String msg) {
        super(context, themeResId);
        this.mContext = context;
        this.mMessage = msg;
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_message);

        setupDialog();
        initView();
        initData();
    }

    private void setupDialog() {
        // 1. 对话框尺寸和位置设置
        Window window = this.getWindow();
        // 保证dialog的宽高和设置的一致
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (mWidth == 0) {
            mWidth = mContext.getResources().getDimensionPixelSize(R.dimen.c_message_dialog_width);
        }
        if (mHeight == 0) {
            mHeight = mContext.getResources().getDimensionPixelSize(R.dimen.c_message_dialog_height);
        }
        layoutParams.width = mWidth;
        layoutParams.height = mHeight;
        window.setAttributes(layoutParams);

        // 2. 对话框特殊属性设置
    }

    private void initView() {
        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), CommonConstants.CUSTOM_FONT_FLFBLS);
        TextView tv_message = findViewById(R.id.tv_msg);
        tv_message.setTypeface(tf);
        tv_message.setText(mMessage);
    }

    private void initData() {
        mCountDownTimer = new CountDownTimer(MILLIS_INFUTURE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                LogUtils.debug(TAG, "onTick： " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                dismiss();
            }
        };
        mCountDownTimer.start();
    }
}
