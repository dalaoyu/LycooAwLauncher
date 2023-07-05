package com.lycoo.commons.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lycoo.commons.R;
import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.helper.PrivateStorageManager;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;
import com.lycoo.commons.widget.CustomToast;

import java.util.regex.Pattern;

/**
 * 固件默认客户码：ro.customer.default.code
 * 如果用户写入新的客户码或者重置客户码， 有两个地方会被修改：
 * 1. 将新的客户码写入特定分区（全志的写入Private分区）
 * 2. 修改系统属性：persist.sys.customer.code
 *
 * 为防止系统恢复出厂设置之后persist.sys.customer.code 和 特定分区的值不同步，所以在系统启动时进行同步操作。
 *
 * Created by lancy on 2019/1/5
 */
public class LastingCustomerDialog extends Dialog {
    private static final String TAG = LastingCustomerDialog.class.getSimpleName();

    private EditText et_data;
    private Context mContext;
    private int mWidth;
    private int mHeight;

    public LastingCustomerDialog(Context context, int themeResId) {
        this(context, themeResId, 0, 0);
    }

    public LastingCustomerDialog(Context context, int themeResId, int width, int height) {
        super(context, themeResId);
        this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_lasting_customer);
        setupDialog();

        initView();
        initData();
    }

    private void initView() {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), CommonConstants.CUSTOM_FONT_FLFBLS);
        et_data = findViewById(R.id.et_data);
        et_data.setTypeface(typeface);
        Button btn_ok = findViewById(R.id.btn_ok);
        Button btn_reset = findViewById(R.id.btn_reset);
        btn_ok.setTypeface(typeface);
        btn_reset.setTypeface(typeface);

        btn_reset.setOnClickListener(v -> reset());
        btn_ok.setOnClickListener(v -> execute());
    }

    private void setupDialog() {
        // 1. 对话框尺寸和位置设置
        Window window = this.getWindow();
        if (window == null) {
            return;
        }

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

    /**
     * 初始化数据
     *
     * Created by lancy on 2019/1/5 18:47
     */
    private void initData() {
        et_data.setHint(R.string.c_customer_code_hint);
        et_data.setText(DeviceUtils.getCustomerCode());
    }

    /**
     * 重置
     * 将客户码重置为固件默认的客户码
     *
     * Created by lancy on 2019/1/5 18:47
     */
    private void reset() {
        String defCustomerCode = SystemPropertiesUtils.get(CommonConstants.PROPERTY_CUSTOMER_DEFAULT_CODE);
        if (TextUtils.isEmpty(defCustomerCode)) {
            CustomToast.makeText(mContext, R.string.c_msg_failed_to_reset_customer_code, CustomToast.MessageType.ERROR).show();
            return;
        }

        String regix = "^\\d{12}$";
        boolean matches = Pattern.compile(regix).matcher(defCustomerCode).matches();
        if (matches) {
            // 1. 将CustomerCode写入特定分区
            PrivateStorageManager.getInstance().write(CommonConstants.CUSTOMER_CODE, defCustomerCode);
            // 2. 修改系统属性
            SystemPropertiesUtils.set(mContext, CommonConstants.PROPERTY_CUSTOMER_CODE, defCustomerCode);
            dismiss();
        } else {
            Toast.makeText(mContext, R.string.c_invalid_customer_code, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 按确定后执行
     *
     * Created by lancy on 2019/1/5 18:51
     */
    protected void execute() {
        String regix = "^\\d{12}$";
        String data = et_data.getText().toString();
        boolean matches = Pattern.compile(regix).matcher(data).matches();
        if (matches) {
            // 1. 将CustomerCode写入特定分区
            PrivateStorageManager.getInstance().write(CommonConstants.CUSTOMER_CODE, data);
            // 2. 修改系统属性
            SystemPropertiesUtils.set(mContext, CommonConstants.PROPERTY_CUSTOMER_CODE, data);
            dismiss();
        } else {
            Toast.makeText(mContext, R.string.c_invalid_customer_code, Toast.LENGTH_SHORT).show();
        }
    }
}
