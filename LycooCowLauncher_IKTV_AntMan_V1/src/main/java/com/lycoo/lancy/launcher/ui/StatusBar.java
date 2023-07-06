package com.lycoo.lancy.launcher.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lycoo.commons.app.CustomAlertDialog;
import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.helper.DeviceManager;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.CollectionUtils;
import com.lycoo.commons.util.DateUtils;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.lancy.launcher.R;
import com.lycoo.lancy.launcher.config.Constants;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 状态栏
 * <p>
 * Created by lancy on 2017/12/11
 */
public class StatusBar extends FrameLayout {
    private static final String TAG = StatusBar.class.getSimpleName();
    private static final boolean DEBUG_UI = true;

    private static final String VOLTAGE_LOG_FILE_PREFIX = "voltage-";
    private static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);//日期格式;

    @BindView(R.id.tv_date)
    TextView tv_date;
    @BindView(R.id.tv_weekDay)
    TextView tv_weekDay;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.iv_weather)
    ImageView iv_weather;
    @BindView(R.id.iv_netWork)
    ImageView iv_netWork;
    @BindView(R.id.iv_usb)
    ImageView iv_usb;
    @BindView(R.id.iv_card)
    ImageView iv_card;
    @BindView(R.id.iv_battery)
    ImageView iv_battery;
    @BindView(R.id.tv_debug_battery)
    TextView mDebugBatteryText;

    private Context mContext;
    private Disposable mWeatherDisposable;
    private WeatherBroadcastReceiver mWeatherBroadcastReceiver;
    private MediaBroadcastReceiver mMediaBroadcastReceiver;
    private BatteryBroadcastReceiver mBatteryBroadcastReceiver;
    private CompositeDisposable mCompositeDisposable;

    private boolean mWeatherEnable;
    private boolean mBatteryEnable;
    private boolean mMediaEnable;

    private Handler mHandler = new Handler();
    private String mediaDevice = null;
    private String action = null;

    private static boolean mDebugMode;
    private Integer mBatteryLevel;
    private CustomAlertDialog mLowBatteryDialog;
    private AnimationDrawable animationDrawable;

    private boolean isLowBatteryPrompt;
    private int mLowBatteryPromptNum;


    public StatusBar(@NonNull Context context) {
        this(context, true, true, true);
    }

    public StatusBar(Context context, boolean mediaEnable, boolean weatherEnable, boolean batteryEnable) {
        super(context);
        this.mContext = context;
        this.mWeatherEnable = weatherEnable;
        this.mBatteryEnable =  batteryEnable && SystemPropertiesUtils.getBoolean("ro.lycoo.battery.enable", true);;
        this.mMediaEnable = mediaEnable;

        initData();
        initView();
        startDateTimer();
        registerWeatherReceiver();
        registerMediaReceiver();
        registerBatteryBroadcastReceiver();
        checkMediaDevices();
    }

    private void initData() {
        mDebugMode = DeviceUtils.getFirmwareMode() == CommonConstants.FIRMWARE_MODE_DEBUG;
        mCompositeDisposable = new CompositeDisposable();
        try {
            mBatteryLevel = mContext.getResources().getInteger(Resources.getSystem().getIdentifier(
                    "config_batteryLevel",
                    "integer",
                    "android"));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (mBatteryLevel == null) {
            mBatteryLevel = Constants.BATTERY_LEVEL_4;
        }
        LogUtils.debug(TAG, "mBatteryLevel  = " + mBatteryLevel);
    }

    @SuppressLint("InflateParams")
    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.status_bar, null);
        addView(view);
        ButterKnife.bind(this, view);

        Typeface tf = StyleManager.getInstance(mContext).getTypeface();
        tv_date.setTypeface(tf);
        tv_time.setTypeface(tf);
        tv_weekDay.setTypeface(tf);

        ViewUtils.setViewShown(mDebugMode, mDebugBatteryText);

        animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.bg_battery_charging4);

        initUsbAndTfCard();
        initIconEvent();
    }

    private void initUsbAndTfCard() {
        int usbSize = DeviceManager.getMountedUsbSize(mContext);
        int tFCardSize = DeviceManager.getMountedTFCardSize(mContext);
        ViewUtils.setViewShown(usbSize > 0, iv_usb);
        ViewUtils.setViewShown(tFCardSize > 0, iv_card);
    }

    private void initIconEvent() {
        iv_netWork.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        iv_usb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationUtils.openApplication(mContext, "com.lycoo.lancy.multi.media");
            }
        });
        iv_card.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationUtils.openApplication(mContext, "com.lycoo.lancy.multi.media");
            }
        });
        iv_weather.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationUtils.openApplication(mContext, "com.lycoo.keily.weather");
            }
        });
    }

    /**
     * 启动日期定时器
     * <p>
     * Created by lancy on 2017/12/12 14:30
     */
    private void startDateTimer() {
        //interval and timer默认是在新的线程中执行
        int interval = mContext.getResources().getInteger(R.integer.update_time_interval);
        mCompositeDisposable.add(
                Observable
                        .interval(0, interval, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread()) // 在主线程更新View
                        .subscribe(aLong -> updateDate()));
    }

    /**
     * 更新日期时间
     * <p>
     * Created by lancy on 2017/12/12 14:24
     */
    private void updateDate() {
        tv_date.setText(DateUtils.getDateOfToday("/"));
        int dayOfWeek = DateUtils.getDayOfWeek(DateUtils.getDateOfToday());
        String day = "";
        switch (dayOfWeek) {
            case CommonConstants.DAY_OF_WEEK_MONDAY:
                day = mContext.getString(R.string.monday);
                break;
            case CommonConstants.DAY_OF_WEEK_TUESDAY:
                day = mContext.getString(R.string.tuesday);
                break;
            case CommonConstants.DAY_OF_WEEK_WEDNESDAY:
                day = mContext.getString(R.string.wednesday);
                break;
            case CommonConstants.DAY_OF_WEEK_THURDAY:
                day = mContext.getString(R.string.thurday);
                break;
            case CommonConstants.DAY_OF_WEEK_FRIDAY:
                day = mContext.getString(R.string.friday);
                break;
            case CommonConstants.DAY_OF_WEEK_SATURDAY:
                day = mContext.getString(R.string.saturday);
                break;
            case CommonConstants.DAY_OF_WEEK_SUNDAY:
                day = mContext.getString(R.string.sunday);
                break;
        }
        tv_weekDay.setText(day);
        tv_time.setText(DateUtils.getTiem());
    }

    /**
     * 注册更新天气接收者
     * <p>
     * Created by lancy on 2017/12/12 14:39
     */
    public void registerWeatherReceiver() {
        if (!mWeatherEnable) {
            return;
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonConstants.ACTION_RECEIVE_WEATHER);
        mWeatherBroadcastReceiver = new WeatherBroadcastReceiver();
        mContext.registerReceiver(mWeatherBroadcastReceiver, filter);
    }

    /**
     * 注销更新天气接收者
     * <p>
     * Created by lancy on 2017/12/12 14:39
     */
    public void unregisterWeatherReceiver() {
        if (!mWeatherEnable) {
            return;
        }

        if (mWeatherBroadcastReceiver != null) {
            mContext.unregisterReceiver(mWeatherBroadcastReceiver);
        }
    }

    /**
     * 启动天气定时器
     * <p>
     * Created by lancy on 2017/12/12 14:48
     */
    private void startWeatherTimer() {
        if (!mWeatherEnable) {
            return;
        }

        int interval = mContext.getResources().getInteger(R.integer.update_weather_interval);
        mWeatherDisposable = Observable
                .interval(0, interval, TimeUnit.MINUTES)
                .subscribe(aLong -> {
                    mContext.sendBroadcast(new Intent(CommonConstants.ACTION_GET_WEATHER));
                });
    }

    /**
     * 停止天气定时器
     * 当网络断开后停止天气定时器
     * <p>
     * Created by lancy on 2017/12/12 16:28
     */
    private void stopWeatherTimer() {
        if (!mWeatherEnable) {
            return;
        }

        if (mWeatherDisposable != null && !mWeatherDisposable.isDisposed()) {
            mWeatherDisposable.dispose();
        }
    }

    /**
     * 更新天气显示
     * <p>
     * Created by lancy on 2017/12/12 14:41
     */
    private void updateWeather(String weatherState) {
        ViewUtils.setViewShown(true, iv_weather);
        iv_weather.setImageResource(getWeatherIcon(weatherState));
    }

    /**
     * 根据天气信息获取对应的Icon
     *
     * @param weather 天气信息
     * @return 对应图片id
     * <p>
     * Created by lancy on 2017/12/12 14:41
     */
    private int getWeatherIcon(String weather) {
        if (weather.endsWith("晴")) {
            return R.drawable.ic_weather_sunshine;
        } else if (weather.endsWith("多云")) {
            return R.drawable.ic_weather_sunny_to_cloudy;
        } else if (weather.endsWith("阴")) {
            return R.drawable.ic_weather_cloudy;
        } else if (weather.endsWith("雾")) {
            return R.drawable.ic_weather_fog;
        } else if (weather.endsWith("沙尘暴")) {
            return R.drawable.ic_weather_storm;
        } else if (weather.endsWith("雷阵雨")) {
            return R.drawable.ic_weather_thundershower;
        } else if (weather.endsWith("阵雨")) {
            return R.drawable.ic_weather_shower;
        } else if (weather.endsWith("小雨")) {
            return R.drawable.ic_weather_sprinkle;
        } else if (weather.endsWith("中雨")) {
            return R.drawable.ic_weather_moderate_rain;
        } else if (weather.endsWith("大雨")) {
            return R.drawable.ic_weather_heavy_rain;
        } else if (weather.endsWith("雨")) {
            return R.drawable.ic_weather_moderate_rain;
        } else if (weather.endsWith("小雪")) {
            return R.drawable.ic_weather_scouther;
        } else if (weather.endsWith("大雪")) {
            return R.drawable.ic_weather_heavy_snow;
        } else if (weather.endsWith("雨夹雪")) {
            return R.drawable.ic_weather_sleet;
        } else if (weather.endsWith("雪")) {
            return R.drawable.ic_weather_scouther;
        } else {
            return R.drawable.ic_weather_sunny_to_cloudy;
        }
    }

    /**
     * 更新天气广播接收者
     * <p>
     * Created by lancy on 2017/12/12 14:34
     */
    private final class WeatherBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            LogUtils.verbose(TAG, "WeatherBroadcastReceiver->onReceive()");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String weatherState = bundle.getString(CommonConstants.KEY_WEATHER);
                LogUtils.debug(TAG, "getMarqueeInfo weather, weatherState = " + weatherState);
                updateWeather(weatherState);
            }
        }
    }

    /**
     * 注册外设状态改变接收者
     * <p>
     * Created by lancy on 2017/12/12 14:39
     */
    public void registerMediaReceiver() {
        if (!mMediaEnable) {
            return;
        }

        IntentFilter filter = new IntentFilter();
        /* 调试使用
        filter.addAction(Intent.ACTION_MEDIA_NOFS);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SHARED);
        */
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);


        filter.addDataScheme("file");
        mMediaBroadcastReceiver = new MediaBroadcastReceiver();
        mContext.registerReceiver(mMediaBroadcastReceiver, filter);
    }

    /**
     * 注销外设状态改变接收者
     * <p>
     * Created by lancy on 2017/12/12 14:39
     */
    public void unregisterMediaReceiver() {
        if (!mMediaEnable) {
            return;
        }

        if (mMediaBroadcastReceiver != null) {
            mContext.unregisterReceiver(mMediaBroadcastReceiver);
        }
    }

    /**
     * 外设状态改变接收者
     * 外设包括usb和card
     * <p>
     * Created by lancy on 2017/12/12 15:02
     */
    private final class MediaBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            // TODO: 2018/4/28 适配其他方案
            String action = intent.getAction();
            String data = intent.getDataString();
            LogUtils.debug(TAG, "action : " + action + ", data : " + data);
            if (StringUtils.isEmpty(action) || StringUtils.isEmpty(data)) {
                return;
            }

            String mountedPoint = data.substring(data.indexOf(":///") + 3).trim();
            switch (action) {
                case Intent.ACTION_MEDIA_MOUNTED:
                    if (DeviceManager.isRK3229()
                            && mountedPoint.startsWith("/storage")
                            && !mountedPoint.startsWith("/storage/emulated")
                            && !mountedPoint.startsWith("/storage/self")) {
                        ViewUtils.setViewShown(true, iv_usb);

                        return;
                    }
                    if (DeviceManager.isUsb(mountedPoint)) {
                        ViewUtils.setViewShown(true, iv_usb);
                        //U盘安装KTV
//                        ApplicationUtils.installedUsbKtv(mContext,intent.getData().getPath());

                    } else if (DeviceManager.isExternalCard(mountedPoint)) {
                        ViewUtils.setViewShown(true, iv_card);
                    }
                    break;
                case Intent.ACTION_MEDIA_REMOVED:
                    if (DeviceManager.isRK3229()
                            && mountedPoint.startsWith("/storage")
                            && !mountedPoint.startsWith("/storage/emulated")
                            && !mountedPoint.startsWith("/storage/self")) {

                        ViewUtils.setViewShown(false, iv_usb);
                        return;
                    }

                    ViewUtils.setViewShown(false, iv_usb);
                    break;
                case Intent.ACTION_MEDIA_EJECT:
                    //                    if (DeviceManager.isRK3229()
                    //                            && mountedPoint.startsWith("/storage")
                    //                            && !mountedPoint.startsWith("/storage/emulated")
                    //                            && !mountedPoint.startsWith("/storage/self")) {
                    //                        ViewUtils.setViewShown(false, iv_usb);
                    //                        return;
                    //                    }
                    //                    ViewUtils.setViewShown(false, iv_card);
                    if (DeviceManager.isUsb(mountedPoint)) {

                        ViewUtils.setViewShown(false, iv_usb);
                    } else {
                        ViewUtils.setViewShown(false, iv_card);
                    }
                    break;
            }
        }
    }

    /**
     * 检测外设设备是否挂载
     * <p>
     * Created by keily on 2018/10/08 18:50
     */
    private void checkMediaDevices() {
        List<String> mountedDevices = DeviceManager.getMountedDevices(mContext);
        if (CollectionUtils.isEmpty(mountedDevices)) {
            SystemPropertiesUtils.set(mContext, "sys.lycoo.launcher.completed", "true");
            return;
        }
        for (String device : mountedDevices) {
            LogUtils.debug(TAG, "device : " + device);
            if (DeviceManager.isUsb(device)) {
                ViewUtils.setViewShown(true, iv_usb);
                action = "com.lycoo.action.MEDIA_MOUNTED";
                mediaDevice = device;
            } else if (DeviceManager.isExternalCard(device)) {
                ViewUtils.setViewShown(true, iv_card);
                action = "com.lycoo.action.MEDIA_MOUNTED";
                mediaDevice = device;
            } else {
                continue;
            }
        }

        if (null != mediaDevice && null != action) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!SystemPropertiesUtils.get("sys.lycoo.launcher.completed").equals("true")) {
                        Intent intent = new Intent(action, Uri.parse("file://" + mediaDevice));
                        mContext.sendBroadcast(intent);
                        SystemPropertiesUtils.set(mContext, "sys.lycoo.launcher.completed", "true");
                    }
                }
            }, 8000);
        } else {
            SystemPropertiesUtils.set(mContext, "sys.lycoo.launcher.completed", "true");
        }
    }

    /**
     * 注册电池更新广播
     * <p>
     * Created by lancy on 2018/5/23 10:50
     */
    private void registerBatteryBroadcastReceiver() {
        if (!mBatteryEnable) {
            return;
        }

        // 更新电池状态
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.UPDATE_BATTERY_STATE_ACTION);
        mBatteryBroadcastReceiver = new BatteryBroadcastReceiver();
        mContext.registerReceiver(mBatteryBroadcastReceiver, filter);
    }

    /**
     * 注销电池更新广播
     * <p>
     * Created by lancy on 2018/5/23 10:50
     */
    private void unregisterBatteryBroadcastReceiver() {
        if (!mBatteryEnable) {
            return;
        }

        if (mBatteryBroadcastReceiver != null) {
            mContext.unregisterReceiver(mBatteryBroadcastReceiver);
        }
    }

    private final class BatteryBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            LogUtils.verbose(TAG, "UpdateCustomBatteryBroadcastReceiver->onReceive()");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int state = bundle.getInt(Constants.EXTRA_BATTERY_STATE, -1);
                int voltage = bundle.getInt(Constants.EXTRA_BATTERY_VOLTAGE, -1);
                LogUtils.debug(TAG, "battery voltage = " + voltage + ", battery ok = " + state);
                if (state == -1 || voltage == -1) {
                    return;
                }

                if (mDebugMode) {
                    mDebugBatteryText.setText("" + voltage);
                }

                updateBattery(bundle.getInt(Constants.EXTRA_BATTERY_STATE));
            }
        }
    }

    private void updateBattery(int state) {
        if (!mBatteryEnable) {
            return;
        }
        LogUtils.debug(TAG, "updateBattery()-> ok = " + state);
        if ((state < Constants.BATTERY_CHARGE || state > mBatteryLevel + 1) && state != Constants.BATTERY_FULL) {
            ViewUtils.setViewShown(false, iv_battery);
            return;
        }

        if (mBatteryLevel == Constants.BATTERY_LEVEL_4) {
            switch (state) {
                // 充满
                case Constants.BATTERY_FULL:
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    iv_battery.setBackgroundResource(R.drawable.ic_battery_full_green);
                    mLowBatteryPromptNum = 0;
                    break;
                // 充电
                case Constants.BATTERY_CHARGE:
                    iv_battery.setBackgroundDrawable(animationDrawable);
                    animationDrawable.start();
                    mLowBatteryPromptNum = 0;
                    break;
                // 满格
                case Constants.BATTERY_04:
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    iv_battery.setBackgroundResource(R.drawable.ic_battery_4_04);
                    mLowBatteryPromptNum = 0;
                    break;
                case Constants.BATTERY_03:
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    iv_battery.setBackgroundResource(R.drawable.ic_battery_4_03);
                    mLowBatteryPromptNum = 0;
                    break;
                case Constants.BATTERY_02:
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    iv_battery.setBackgroundResource(R.drawable.ic_battery_4_02);
                    mLowBatteryPromptNum = 0;
                    break;
                case Constants.BATTERY_01:
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    iv_battery.setBackgroundResource(R.drawable.ic_battery_4_01);
                    mLowBatteryPromptNum = 0;
                    break;
                // 低电量
                case Constants.BATTERY_00:
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    iv_battery.setBackgroundResource(R.drawable.ic_battery_low);
                    mLowBatteryPromptNum++;
                    showLowBatteryDialog();
                    break;
            }
            ViewUtils.setViewShown(true, iv_battery);
        }
    }

    private void showLowBatteryDialog() {
        if (Build.MODEL.contains(Constants.MODEL_TONGCHUANG)) {
            if (isLowBatteryPrompt || mLowBatteryPromptNum < 11) {
                return;
            }
        } else {
            if (isLowBatteryPrompt || mLowBatteryPromptNum < 5) {
                return;
            }
        }

        if (mLowBatteryDialog != null && mLowBatteryDialog.isShowing()) {
            return;
        }

        mLowBatteryDialog = new CustomAlertDialog(mContext.getApplicationContext(),
                R.style.DarkDialogStyle,
                mContext.getString(R.string.msg_low_battery),
                mContext.getString(R.string.msg_no_hint),
                mContext.getString(R.string.msg_delay_hint),
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isLowBatteryPrompt = true;
                        LogUtils.debug(TAG, "isLowBatteryPrompt = " + isLowBatteryPrompt);
                        mLowBatteryDialog.dismiss();
                    }
                });
        mLowBatteryDialog.show();
        playLowBatEffect();
        //isLowBatteryPrompt = true;

    }
    private void playLowBatEffect() {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.low_bat_effect);
            mediaPlayer.start();
        } catch (Exception e) {
            Log.w(TAG, "MediaPlayer Exception: " + e);
        }
    }
    /**
     * 网络状态发生改变
     * 当网络状态发生变化时调用此方法
     *
     * @param networkInfo 网络信息
     *                    <p>
     *                    Created by lancy on 2017/12/12 16:07
     */
    public void onNetworkChange(NetworkInfo networkInfo) {
        if (networkInfo == null) {
            LogUtils.error(TAG, "network disconnected ......");
            iv_netWork.setImageResource(R.drawable.wifi_disconnect);
            ViewUtils.setViewShown(true, iv_netWork);

            // 停止天气定时器
            stopWeatherTimer();

            return;
        }

        LogUtils.debug(TAG, "network Type = " + networkInfo.getType() + ", name = " + networkInfo.getTypeName());
        if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {             // wifi连接
            iv_netWork.setImageResource(R.drawable.ic_wifi);
        } else if (ConnectivityManager.TYPE_ETHERNET == networkInfo.getType()) {  // 有线连接
            iv_netWork.setImageResource(R.drawable.ic_ethernet);
        }
        ViewUtils.setViewShown(true, iv_netWork);

        // 强制更新一次日期
        updateDate();

        // 启动天气定时器
        startWeatherTimer();
    }



    public void onDestroy() {
        unregisterWeatherReceiver();
        unregisterMediaReceiver();
        unregisterBatteryBroadcastReceiver();
        mCompositeDisposable.clear();
    }


}
