package com.lycoo.honceh.Setting.settingStorage.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lycoo.honceh.R;
import com.lycoo.honceh.Setting.settingStorage.preference.StoragePreference;

public class StorageSettingActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_def);
        StoragePreference fragment=new StoragePreference();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }
}
