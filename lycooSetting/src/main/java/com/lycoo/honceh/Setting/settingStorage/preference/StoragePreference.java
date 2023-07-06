package com.lycoo.honceh.Setting.settingStorage.preference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.lycoo.commons.helper.DeviceManager;
import com.lycoo.commons.helper.SystemPropertiesManager;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;
import com.lycoo.honceh.R;
import com.lycoo.honceh.Setting.settingStorage.util.DeviceStorage;


import java.util.List;

public class StoragePreference extends PreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener {
    private static final String TAG = StoragePreference.class.getSimpleName();
    private MediaBroadcastReceiver mMediaBroadcastReceiver;
    private PreferenceScreen mPreferenceCategory_setting;
    private PreferenceCategory mPreferenceCategory_external;
    private Preference mPreference_localdata;
    private final String KEY_STORAGE_EXTERNAL = "storage_external";
    private final String KEY_STORAGE_SETTING = "storage_setting";
    private final String KEY_LOCAL_DATA_RESET = "localdata_reset";
    private List<String> mountedDevices;
    private boolean has_external;
    private int mDataResetHitCountdown = 3;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.storage_setting, rootKey);
        init();
    }

    private void init() {
        mPreferenceCategory_setting = findPreference(KEY_STORAGE_SETTING);
        mPreference_localdata = findPreference(KEY_LOCAL_DATA_RESET);
        mPreference_localdata.setOnPreferenceClickListener(this);
//        mPreferenceCategory_external = findPreference(KEY_STORAGE_EXTERNAL);
        mPreferenceCategory_external = new PreferenceCategory(getContext());
        mPreferenceCategory_external.setTitle("外部存储结构");
        LogUtils.info(TAG, "test");
        mountedDevices = DeviceManager.getMountedDevices(getContext());
        changeCategory_external(mountedDevices);
    }

    /**
     * 外接设备接入状态发送变化修改ui
     *
     * @param mountedDevices create by Honceh 23/6/14
     */
    private void changeCategory_external(List<String> mountedDevices) {
        if (mountedDevices.size() < 2 && has_external) {
            mPreferenceCategory_setting.removePreference(mPreferenceCategory_external);
            has_external = false;
        } else if (mountedDevices.size() > 1) {
            if (!has_external) {
                mPreferenceCategory_setting.addPreference(mPreferenceCategory_external);
            }
            has_external = true;
            mPreferenceCategory_external.removeAll();
            for (int i = 1; i < mountedDevices.size(); i++) {
                Preference preference = new Preference(getContext());
                preference.setTitle(DeviceStorage.getDevices(mountedDevices.get(i)));
                preference.setSummary(DeviceStorage.getStorageSize(mountedDevices.get(i)));
                mPreferenceCategory_external.addPreference(preference);
                LogUtils.info(TAG, mountedDevices.get(i));
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerMediaReceiver();
    }

    /**
     * 注册外接设备接入广播
     * create by Honceh 23/6/14
     */
    private void registerMediaReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");
        mMediaBroadcastReceiver = new MediaBroadcastReceiver();
        getContext().registerReceiver(mMediaBroadcastReceiver, filter);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.equals(mPreference_localdata)) {
            if (mDataResetHitCountdown > 0) {
                mDataResetHitCountdown--;
            } else {
                if (SystemPropertiesManager.getInstance(this.getContext()).getInt("sys.lycoo.localdata_reset", 0) == 1) {
                    Toast.makeText(getActivity(), R.string.local_reset_completed, Toast.LENGTH_LONG).show();
                } else {
                    SystemPropertiesManager.getInstance(this.getContext()).set("sys.lycoo.localdata_reset", "1");
                    Toast.makeText(getActivity(), R.string.local_reset_doing, Toast.LENGTH_LONG).show();
                }
            }
        }
        return true;
    }

    /**
     * 外接设备广播
     * create by Honceh 23/6/14
     */
    private final class MediaBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.info(TAG, "设备广播");
            String action = intent.getAction();
            List<String> mountedDevices = DeviceManager.getMountedDevices(getContext());
            switch (action) {
                case Intent.ACTION_MEDIA_MOUNTED:
                    LogUtils.info(TAG, "设备插入");
                    for (int i = 0; i < mountedDevices.size(); i++) {
                        LogUtils.info(TAG, mountedDevices.get(i));
                    }
                    changeCategory_external(mountedDevices);
                    break;
                case Intent.ACTION_MEDIA_REMOVED:
                case Intent.ACTION_MEDIA_EJECT:
                    LogUtils.info(TAG, "设备拔出");
                    changeCategory_external(mountedDevices);
                    break;
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mMediaBroadcastReceiver);
    }
}
