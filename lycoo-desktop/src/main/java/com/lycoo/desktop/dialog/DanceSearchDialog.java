package com.lycoo.desktop.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lycoo.commons.base.BaseDialog;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.desktop.R;
import com.lycoo.desktop.R2;
import com.lycoo.desktop.boosj.BoosjConstants;
import com.lycoo.desktop.config.DesktopConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索对话框
 *
 * Created by lancy on 2018/11/14
 */
public class DanceSearchDialog extends BaseDialog {

    @BindView(R2.id.et_data) EditText mDataText;
    @BindView(R2.id.btn_ok) Button mOKButton;
    @BindView(R2.id.btn_cancel) Button mCancelButton;

    private Context mContext;

    public DanceSearchDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_dance_search);
        ButterKnife.bind(this);

        setupDialog();
        initView();
    }

    private void initView() {
        mDataText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mOKButton.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mCancelButton.setTypeface(StyleManager.getInstance(mContext).getTypeface());
    }

    private void setupDialog() {
        Window window = getWindow();
        if (window == null) {
            return;
        }

        // 保证dialog的宽高和设置的一致
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.dance_search_dialog_height);
        layoutParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.dance_search_dialog_width);
        window.setAttributes(layoutParams);
        window.setWindowAnimations(R.style.DanceSearchDialogAnimationStyle);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @OnClick(R2.id.btn_cancel)
    public void hideDialog() {
        dismiss();
    }

    @OnClick(R2.id.btn_ok)
    public void search() {
        String keyword = mDataText.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            Toast.makeText(mContext, R.string.msg_empty_keyword, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.setAction(BoosjConstants.ACTION_BROWSE_BOOSJ_DANCE_VIDEOS);
        intent.putExtra(BoosjConstants.TYPE, DesktopConstants.BOOSJ_DANCE_SEARCH);
        intent.putExtra(BoosjConstants.TITLE, mContext.getString(R.string.title_video_list));
        intent.putExtra(BoosjConstants.KEYWORD, keyword);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);

        dismiss();
    }
}
