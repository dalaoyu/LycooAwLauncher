package com.lycoo.desktop.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lycoo.commons.event.CommonEvent;
import com.lycoo.commons.helper.RxBus;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.desktop.R;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by lancy on 2018/4/10
 */
public class BaseActivity extends Activity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    protected ViewGroup mHeader;
    protected TextView mTitleText;
    protected ImageButton mDownloadListBtn;
    protected Context mContext = this;


    private MediaBroadcastReceiver mMediaBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullscreen();
        subscribeHideNavigationBarEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    protected void setupFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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

    protected void hideNavigationBar(){
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
        RxBus.getInstance().addDisposable(this, RxBus.getInstance()
                .registerSubscribe(
                        CommonEvent.HideNavigationBarEvent.class,
                        event -> hideNavigationBar(),
                        throwable -> {

                        }));
    }

    protected void initHeader() {
        mHeader = findViewById(R.id.header);
        if (DeviceUtils.isTpEnable()) {
            mHeader.setPadding(
                    mHeader.getPaddingLeft() + getResources().getDimensionPixelSize(R.dimen.global_arc_menu_icon_size),
                    mHeader.getPaddingTop(),
                    mHeader.getPaddingRight(),
                    mHeader.getPaddingBottom());
        }
        mTitleText = findViewById(R.id.tv_title);
        mTitleText.setTypeface(StyleManager.getInstance(mContext).getTypeface());

        mDownloadListBtn = findViewById(R.id.ib_download_list);
        mDownloadListBtn.setOnClickListener(v -> browseDownloadList());
    }

    protected void back() {
        finish();
    }

    protected void browseDownloadList() {
    }

    @Override
    public void onBackPressed() {
        back();
    }

    /**
     * 注册外设状态改变接收者
     *
     * Created by lancy on 2017/12/12 14:39
     */
    protected void registerMediaReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addDataScheme("file");
        mMediaBroadcastReceiver = new MediaBroadcastReceiver();
        mContext.registerReceiver(mMediaBroadcastReceiver, filter);
    }

    /**
     * 设备挂载
     *
     * @param path 设备路径
     *
     *             Created by lancy on 2018/4/28 0:54
     */
    protected void onMediaMounted(String path) {

    }

    /**
     * 设备卸载
     *
     * @param path 设备路径
     *
     *             Created by lancy on 2018/4/28 0:55
     */
    protected void onMediaUnMounted(String path) {

    }

    /**
     * 注销外设状态改变接收者
     *
     * Created by lancy on 2017/12/12 14:39
     */
    protected void unRegisterMediaReceiver() {
        if (mMediaBroadcastReceiver != null) {
            mContext.unregisterReceiver(mMediaBroadcastReceiver);
        }
    }

    /**
     * 外设状态改变接收者
     * 外设包括usb和card
     *
     * Created by lancy on 2017/12/12 15:02
     */
    private final class MediaBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String data = intent.getDataString();
            LogUtils.debug(TAG, "MediaBroadcastReceiver, onReceive(): action = " + action + ", data = " + data);
            if (StringUtils.isEmpty(action) || StringUtils.isEmpty(data)) {
                return;
            }

            // 截取设备真实路径
            String mountedPoint = data.substring(data.indexOf(":///") + 3).trim();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                onMediaMounted(mountedPoint);
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                onMediaUnMounted(mountedPoint);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.getInstance().unRegisterSubscribe(this);
    }

}
