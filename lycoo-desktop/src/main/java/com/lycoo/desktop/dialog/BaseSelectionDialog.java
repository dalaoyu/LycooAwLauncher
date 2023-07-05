package com.lycoo.desktop.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lycoo.commons.base.BaseDialog;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.desktop.R;
import com.lycoo.desktop.R2;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 选择对话框
 *
 * Created by lancy on 2017/10/27
 */
public class BaseSelectionDialog extends BaseDialog {
    private static final String TAG = BaseSelectionDialog.class.getSimpleName();

    @BindView(R2.id.tv_title) protected TextView mTitleText;
    @BindView(R2.id.radio_group) protected RadioGroup mRadioGroup;
    @BindView(R2.id.btn_ok) protected Button mOkBtn;
    @BindView(R2.id.btn_cancel) protected Button mCancelBtn;

    protected Context mContext;
    private int mTitleId;

    public BaseSelectionDialog(Context context, int themeResId, int titleId) {
        super(context, themeResId);
        mContext = context;
        mTitleId = titleId;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_selection);
        ButterKnife.bind(this);

        setupDialog();
        initView();
    }

    private void setupDialog() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.SelectionDialogAnimationStyle);
        // 保证dialog的宽高和设置的一致
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = mContext.getResources().getDimensionPixelSize(R.dimen.selection_dialog_width);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    private void initView() {
        mTitleText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mTitleText.setText(mTitleId);

        mCancelBtn.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mCancelBtn.setOnClickListener(v -> dismiss());

        mOkBtn.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mOkBtn.setOnClickListener(v -> doSelect());
        // 添加单选按钮
        createRadioButtons();
    }

    protected void createRadioButtons() {
    }

    protected void doSelect() {
    }

    /**
     * 初始化RadioButton
     *
     * @param radioButton    目标RadioButton
     * @param labelId        名称
     * @param leftDrawableId 图标
     *
     *                       Created by lancy on 2018/4/20 11:04
     */
    protected void initRadioButton(RadioButton radioButton, String label, int leftDrawableId) {
        // 自定义RadioButton样式
        try {
            Field field = radioButton.getClass().getSuperclass().getDeclaredField("mButtonDrawable");
            field.setAccessible(true);
            field.set(radioButton, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 设置字体
        radioButton.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        radioButton.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.selection_dialog_item_label_size));
        radioButton.setTextColor(mContext.getResources().getColor(R.color.c_def_textview));
        radioButton.setText(label);

        // 设置左边图标
        Drawable leftDrawble = mContext.getResources().getDrawable(leftDrawableId);
        leftDrawble.setBounds(0, 0, leftDrawble.getMinimumWidth(), leftDrawble.getMinimumHeight());

        // 设置右边图标， 注意:如果不调用Drawable的setBounds()方法，setCompoundDrawables之后不显示图标
        Drawable rightDrawable = mContext.getResources().getDrawable(R.drawable.radio_button);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());

        radioButton.setCompoundDrawables(leftDrawble, null, rightDrawable, null);
        radioButton.setBackgroundResource(R.drawable.bg_list_item);
    }


}
