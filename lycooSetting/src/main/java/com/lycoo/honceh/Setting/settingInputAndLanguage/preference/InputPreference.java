package com.lycoo.honceh.Setting.settingInputAndLanguage.preference;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.lycoo.commons.util.LogUtils;
import com.lycoo.honceh.R;

import java.util.List;

public class InputPreference extends PreferenceFragmentCompat {
    private static final String TAG = LanguagePreference.class.getSimpleName();
    private static final String KEY_INPUT_PREFERENCE = "input_preference";
    private InputMethodManager mInputMethodManager;
    private List<InputMethodInfo> enabledInputMethodList;
    private PreferenceScreen input_preference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.input_setting);
        initData();
        initView();
    }

    private void initView() {
        // 获取系统当前正在使用的输入法
        String currentInputMethodId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        LogUtils.info("InputMethod", "当前输入法 ：" + currentInputMethodId);
        for (InputMethodInfo info : enabledInputMethodList) {
            // 输入法的包名
            String packageName = info.getPackageName();

            // 输入法的名称
            String name = info.loadLabel(getContext().getPackageManager()).toString();

            // 输入法的当前状态
            boolean isCurrentInputMethod = currentInputMethodId.contains(info.getPackageName());

            //mInputMethodManager.getCurrentInputMethodSubtype().getMode();获取当前使用输入法类型 keyboard : 键盘
            Preference preference = new Preference(getContext());
            preference.setTitle(name);
            if (isCurrentInputMethod) {
                preference.setSummary("当前");
            }
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (!isCurrentInputMethod) {
//                            for (int i=0;i<info.getSubtypeCount();i++){
//                                LogUtils.info(TAG ,"getSubtype"+info.getSubtypeAt(i).toString());
//                            }
//                            mInputMethodManager.setInputMethodAndSubtype(getView().getWindowToken(), info.getId(),info.getSubtypeAt(0));
                        mInputMethodManager.setInputMethod(getView().getWindowToken(), info.getId());
                        if (getView().getWindowToken() != null) {
                            LogUtils.info(TAG, "getToken" + getView().getWindowToken().toString());
                        } else {
                            LogUtils.info(TAG, "getToken is null");
                        }
                        Settings.Secure.putString(getContext().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, info.getId());
                        Toast.makeText(getContext(), "切换输入法为 ： " + name, Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    } else {
                        Toast.makeText(getContext(), "当前输入法已为 ： " + name, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
            input_preference.addPreference(preference);
            // 输出输入法信息
            LogUtils.info("InputMethod", "Package: " + packageName + ", Name: " + name + ", Current: " + isCurrentInputMethod);
        }
    }

    private void initData() {
        input_preference = findPreference(KEY_INPUT_PREFERENCE);
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        enabledInputMethodList = mInputMethodManager.getInputMethodList();
    }
}
