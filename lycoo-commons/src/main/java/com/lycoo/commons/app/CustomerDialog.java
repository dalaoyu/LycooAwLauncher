package com.lycoo.commons.app;

import android.content.Context;
import android.widget.Toast;

import com.lycoo.commons.R;
import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;

import java.util.regex.Pattern;

/**
 * xxx
 *
 * Created by lancy on 2017/6/27
 */

public class CustomerDialog extends SingleInputDialog {

    public CustomerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void initData() {
        et_data.setHint(R.string.c_customer_code_hint);
        et_data.setText(DeviceUtils.getCustomerCode());
    }

    @Override
    protected void execute() {
        String regix = "^\\d{12}$";
        String data = et_data.getText().toString();
        boolean matches = Pattern.compile(regix).matcher(data).matches();
        if (matches) {
            SystemPropertiesUtils.set(mContext, CommonConstants.PROPERTY_CUSTOMER_CODE, data);
            dismiss();
        } else {
            Toast.makeText(mContext, R.string.c_invalid_customer_code, Toast.LENGTH_SHORT).show();
        }
    }
}
