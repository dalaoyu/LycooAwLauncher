package com.lycoo.commons.app;

import android.content.Context;
import android.widget.Toast;

import com.lycoo.commons.R;
import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;

import java.util.regex.Pattern;

/**
 * log打印级别设置框
 *
 * Created by lancy on 2017/6/27
 */
public class LogLevelDialog extends SingleInputDialog {

    public LogLevelDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void initData() {
        et_data.setHint(R.string.c_log_level_hint);
        et_data.setText(String.valueOf(DeviceUtils.getLogLevel()));
    }

    @Override
    protected void execute() {
        String regix = "^[1-5]$";
        String data = et_data.getText().toString();
        boolean matches = Pattern.compile(regix).matcher(data).matches();
        if (matches) {
            SystemPropertiesUtils.set(mContext, CommonConstants.PROPERTY_LOG_LEVEL, data);
            dismiss();
        } else {
            Toast.makeText(mContext, R.string.c_invalid_log_level, Toast.LENGTH_SHORT).show();
        }
    }
}
