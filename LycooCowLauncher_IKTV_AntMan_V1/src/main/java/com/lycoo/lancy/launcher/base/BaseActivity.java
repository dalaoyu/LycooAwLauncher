package com.lycoo.lancy.launcher.base;

import android.annotation.SuppressLint;
import android.media.CustomAudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lycoo.commons.event.CommonEvent;
import com.lycoo.commons.helper.RxBus;

import io.reactivex.disposables.Disposable;

/**
 * Created by lancy on 2018/5/19
 */
public class BaseActivity extends AppCompatActivity {

    private CustomAudioManager mCustomAudioManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindow();
        subscribeHideNavigationBarEvent();
    }

    protected void setupWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    protected void hideNavigationBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    protected void subscribeHideNavigationBarEvent() {
        Disposable disposable = RxBus.getInstance()
                .registerSubscribe(
                        CommonEvent.HideNavigationBarEvent.class,
                        event -> hideNavigationBar(),
                        throwable -> {

                        });
        RxBus.getInstance().addDisposable(this, disposable);
    }

    @SuppressLint("WrongConstant")
    protected CustomAudioManager getCustomAudioManager() {
        if (mCustomAudioManager == null) {
            mCustomAudioManager = (CustomAudioManager) getSystemService("custom_audio");
        }

        return mCustomAudioManager;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.getInstance().unRegisterSubscribe(this);
    }
}
