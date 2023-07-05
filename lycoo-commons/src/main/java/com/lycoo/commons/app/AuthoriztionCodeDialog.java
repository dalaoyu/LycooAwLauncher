package com.lycoo.commons.app;

import android.content.Context;
import android.widget.Toast;

import com.lycoo.commons.R;
import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.helper.PrivateStorageManager;

import java.util.regex.Pattern;

/**
 * 授权码对话框
 *
 * Created by lancy on 2019/1/8
 */
public class AuthoriztionCodeDialog extends SingleInputDialog {

    public AuthoriztionCodeDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void initData() {
        et_data.setHint(R.string.c_authorization_code_hint);
        et_data.setText(PrivateStorageManager.getInstance().read(CommonConstants.SN));
    }

    @Override
    protected void execute() {
        String regix = "^\\d{12}$";
        String data = et_data.getText().toString();
        boolean matches = Pattern.compile(regix).matcher(data).matches();
        if (matches) {
            PrivateStorageManager.getInstance().write(CommonConstants.SN, data);
            dismiss();
        } else {
            Toast.makeText(mContext, R.string.c_msg_invalid_authorization_code, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        PrivateStorageManager.getInstance().release();
    }
}
