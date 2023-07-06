package com.lycoo.lancy.launcher.activity;

import android.annotation.SuppressLint;
import android.app.LycooManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.lycoo.commons.app.CustomAlertDialog;
import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.helper.RxBus;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.helper.SystemPropertiesManager;
import com.lycoo.commons.screensaver.ScreensaverManager;
import com.lycoo.commons.update.AppUpdateManager;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.CollectionUtils;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.SecurityUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;
import com.lycoo.commons.widget.CustomToast;
import com.lycoo.desktop.helper.DesktopItemManager;
import com.lycoo.desktop.qiyi.QiyiManager;
import com.lycoo.lancy.launcher.R;
import com.lycoo.lancy.launcher.base.BaseActivity;
import com.lycoo.lancy.launcher.config.Constants;
import com.lycoo.lancy.launcher.ui.Marquee;
import com.lycoo.lancy.launcher.ui.PolymericPage;
import com.lycoo.lancy.launcher.ui.StatusBar;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindArray;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

@SuppressLint("RtlHardcoded")
public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindArray(R.array.hosts)
    String[] mHosts;
    private RelativeLayout mRoot;
    private StatusBar mStatusBar;
    private Marquee mMarquee;
    private View mLoadingView;

    private Context mContext = this;
    private BroadcastReceiver mNetworkChangeBroadcastReceiver;
    private BroadcastReceiver mPackageInstallerReceiver;
    private BroadcastReceiver mPackageChangedBroadcastReceiver;
    private Handler mBackgroundHandler;

    private Disposable mAgingTestDisposable;
    private Disposable mAgingTimeDisposable;
    private SharedPreferences mPublicSp;
    private static int mPressKeyCode;
    private CustomAlertDialog mLowBatteryDialog;
    private LycooManager mLycooManager;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.verbose(TAG, "onCreate()..................................");
        mRoot = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.activity_main, null);
        setContentView(mRoot);
        ButterKnife.bind(this);
//
        if (!SecurityUtils.verifySpecialstr(mHosts)) {
            finish();
        } else {
            launchAgingTest();
            printScreenParams();
            createBackgroundHandlerThread();
            initData();
            initView();
            registerReceivers();
            checkDevice();
            sendCustomBootBroadcast();
        }
    }


    /**
     * 检查硬件是否合法
     * <p>
     * Created by lancy on 2019/3/30 15:56
     */
    private void checkDevice() {
        if (!DeviceUtils.checkDevice(mContext)) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            ImageView view = new ImageView(mContext);
            view.setLayoutParams(params);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setImageResource(R.drawable.exception);
            mRoot.addView(view);
            view.bringToFront();
        }
    }

    private void printScreenParams() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        LogUtils.debug(TAG, "density = " + displayMetrics.density + ", densityDpi = " + displayMetrics.densityDpi);
        LogUtils.debug(TAG, "widthPixels = " + widthPixels + ", heightPixels = " + heightPixels);
    }

    /**
     * 创建后台线程
     * <p>
     * Created by lancy on 2018/6/7 17:34
     */
    private void createBackgroundHandlerThread() {
        HandlerThread handlerThread = new HandlerThread("background");
        handlerThread.start();
        mBackgroundHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
            }
        };
    }

    /**
     * 初始化数据
     */
    private void initData() {
        QiyiManager.getInstance(mContext).init();
    }


    /**
     * 初始化布局
     * <p>
     * Created by lancy on 2017/12/30 15:09
     */
    @SuppressLint("CheckResult")
    private void initView() {
/*        if (Build.MODEL.contains("AILIPU") || Build.MODEL.contains("M18")  ) {
            initLogo();
        }*/

        initMarquee();
        initLoadingView();
        initPages();
        hideLoadingView();
        initScreenRotation();
        initStatusBar();
    }

    /**
     * 初始化进度显示
     * <p>
     * Created by lancy on 2018/5/18 9:55
     */
    @SuppressLint("InflateParams")
    private void initLoadingView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mLoadingView = new View(mContext);
        mLoadingView.setLayoutParams(params);
        mLoadingView.setBackgroundResource(R.drawable.bg_main_loading);
        mRoot.addView(mLoadingView);
    }

    /**
     * 隐藏进度条
     * <p>
     * Created by lancy on 2018/5/18 9:55
     */
    private void hideLoadingView() {
        if (mLoadingView != null) {
            mRoot.removeView(mLoadingView);
        }
    }


    /**
     * 初始化状态栏
     * <p>
     * Created by lancy on 2017/12/30 15:09
     */
    private void initStatusBar() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.statusbar_height));
        params.topMargin = mContext.getResources().getDimensionPixelSize(R.dimen.statusbar_margin_top);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mStatusBar = new StatusBar(mContext);
        mStatusBar.setLayoutParams(params);
        mRoot.addView(mStatusBar);
    }

    /**
     * 初始化内容页
     * <p>
     * Created by lancy on 2017/12/30 15:09
     */
    private void initPages() {
        PolymericPage recommendPage = new PolymericPage(
                mContext,
                getString(R.string.page_polymeric),
                Constants.POLYMERIC_PAGE_NUMBER,
                Constants.POLYMERIC_PAGE_ITEM_COUNT);
        mRoot.addView(recommendPage);
    }

    /**
     * 初始化跑马灯
     * <p>
     * Created by lancy on 2018/1/2 19:58
     */
    private void initMarquee() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.marguee_width),
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.marquee_margin_left);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.marquee_margin_top);
        mMarquee = new Marquee(mContext);
        mMarquee.setLayoutParams(params);
        mRoot.addView(mMarquee);
    }

    /**
     * 初始化Logo
     * <p>
     * Created by lancy on 2018/6/20 20:44
     */
    private void initLogo() {
        Resources resources = getResources();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.logo_width) + 40,
                resources.getDimensionPixelSize(R.dimen.logo_margin_left) + 20);
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.logo_margin_left);
        params.topMargin = resources.getDimensionPixelSize(R.dimen.logo_margin_top);
        ImageView logo = new ImageView(mContext);
        logo.setFocusable(false);
        logo.setFocusableInTouchMode(false);
        logo.setScaleType(ImageView.ScaleType.CENTER);
        if (Build.MODEL.contains("M18")) {
            params.leftMargin = resources.getDimensionPixelSize(R.dimen.logo_margin_left) - 110;
            params.width = resources.getDimensionPixelSize(R.dimen.logo_width) + 90;
            logo.setImageResource(R.drawable.logo_djm);
        } else {
            logo.setImageResource(R.drawable.logo_ailipu);
        }
        logo.setLayoutParams(params);
        mRoot.addView(logo);
    }

    /**
     * 初始化搜索功能
     * <p>
     * Created by lancy on 2018/7/12 18:45
     */
    @SuppressLint("InflateParams")
    private void initSearchPiece() {
        Resources resources = getResources();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                resources.getDimensionPixelSize(R.dimen.search_piece_height));
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.search_piece_margin_left);
        params.topMargin = resources.getDimensionPixelSize(R.dimen.search_piece_margin_top);
        LinearLayout parent = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.search_piece, null);
        parent.setFocusable(true);
        parent.setFocusableInTouchMode(false);
        TextView searchText = parent.findViewById(R.id.tv_search);
        searchText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        parent.setLayoutParams(params);
        parent.setOnClickListener(v -> QiyiManager.getInstance(mContext).openItem(10008));
        mRoot.addView(parent);
    }


    /**
     * 初始化转屏功能
     * <p>
     * Created by keily on 2021/5/20 18:45
     */
    @SuppressLint("InflateParams")
    private void initScreenRotation() {
        boolean enableRotation = SystemPropertiesUtils.getBoolean("ro.sys.enable.rotation", false);
        if (enableRotation) {
            Resources resources = getResources();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.def_icon_size),
                    resources.getDimensionPixelSize(R.dimen.def_icon_size));
            params.leftMargin = resources.getDimensionPixelSize(R.dimen.setup_menu_icon_marin_left);
            params.topMargin = resources.getDimensionPixelSize(R.dimen.setup_menu_icon_marin_top);
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(params);
            frameLayout.setBackgroundResource(R.drawable.bg_oval_frosted_purple);
            mRoot.addView(frameLayout);

            FrameLayout.LayoutParams setupParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            ImageView setupIcon = new ImageView(mContext);
            setupIcon.setFocusable(true);
            setupIcon.setFocusableInTouchMode(false);
            setupIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            setupIcon.setImageResource(R.drawable.ic_screen_ration);
            setupIcon.setBackgroundResource(R.drawable.def_bg_ring);
            setupIcon.setLayoutParams(setupParams);
            frameLayout.addView(setupIcon);
            setupIcon.setOnClickListener(v -> getLycooManager().changeScreenRotation());
        }
    }

    public LycooManager getLycooManager() {
        if (mLycooManager == null) {
            mLycooManager = (LycooManager) mContext.getSystemService("lycoo");
        }
        return mLycooManager;
    }

    /**
     * 注册广播接受者
     * <p>
     * Created by lancy on 2017/12/30 15:08
     */
    private void registerReceivers() {
        // Network
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkChangeBroadcastReceiver = new NetworkChangeBroadcastReceiver();
        registerReceiver(mNetworkChangeBroadcastReceiver, filter);

        // 应用安装(LycooPackageInstaller发出的广播)
        filter = new IntentFilter();
        filter.addAction(CommonConstants.ACTION_PACKAGEINSTALL_COMPLETE);
        mPackageInstallerReceiver = new PackageInstallerReceiver();
        registerReceiver(mPackageInstallerReceiver, filter, null, mBackgroundHandler);

        // 注册app 安装、 卸载、 替换 接收者
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        mPackageChangedBroadcastReceiver = new PackageChangedBroadcastReceiver();
        registerReceiver(mPackageChangedBroadcastReceiver, filter, null, mBackgroundHandler);
    }

    /**
     * 注销广播接受者
     * <p>
     * Created by lancy on 2017/12/30 15:08
     */
    private void unregisterReceivers() {
        // Network
        if (mNetworkChangeBroadcastReceiver != null) {
            unregisterReceiver(mNetworkChangeBroadcastReceiver);
        }

        if (mPackageInstallerReceiver != null) {
            unregisterReceiver(mPackageInstallerReceiver);
        }

        if (mPackageChangedBroadcastReceiver != null) {
            unregisterReceiver(mPackageChangedBroadcastReceiver);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存爱奇艺数据
        QiyiManager.getInstance(mContext).onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 恢复爱奇艺数据
        QiyiManager.getInstance(mContext).onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 发送自定义开机启动广播
     * <p>
     * Created by lancy on 2019/8/14 11:36
     */
    private void sendCustomBootBroadcast() {
        // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean bootCompleted = SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_BOOT_COMPLETED, false);
        LogUtils.debug(TAG, ">>> bootCompleted : " + bootCompleted);
        if (!bootCompleted) {
            // LogUtils.error(TAG, "111 " + dateFormat.format(new Date()));
            // 设置“系统可以处理外设热插拔事件”属性
            SystemPropertiesManager.getInstance(mContext).set(Constants.PROPERTY_MEDIA_BOOT_DISABLE, false);
            // LogUtils.error(TAG, "222 " + dateFormat.format(new Date()));
            // 设置“系统启动完成”属性
            SystemPropertiesManager.getInstance(mContext).set(CommonConstants.PROPERTY_BOOT_COMPLETED, true);
            // LogUtils.error(TAG, "333 " + dateFormat.format(new Date()));
            LogUtils.debug(TAG, "boot.disable : " + SystemPropertiesUtils.getBoolean(Constants.PROPERTY_MEDIA_BOOT_DISABLE, true));
            LogUtils.debug(TAG, "boot_completed : " + SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_BOOT_COMPLETED, false));

            // 发送自定义开机广播
            Intent intent = new Intent(CommonConstants.ACTION_BOOT_COMPLETED);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);  //未启动的应用可接收此广播
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);       //接收者运行在前台，接收延迟减小
            sendBroadcast(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceivers();
        RxBus.getInstance().unRegisterSubscribe(this);

        mStatusBar.onDestroy();
        mMarquee.onDestroy();

        DesktopItemManager.getInstance(mContext).onDestroy();
        AppUpdateManager.getInstance(mContext).onDestroy();
        QiyiManager.getInstance(mContext).onDestroy();

        if (mAgingTimeDisposable != null && !mAgingTimeDisposable.isDisposed()) {
            mAgingTimeDisposable.dispose();
        }

        if (mAgingTestDisposable != null && !mAgingTestDisposable.isDisposed()) {
            mAgingTestDisposable.dispose();
        }
    }

    private final class NetworkChangeBroadcastReceiver extends BroadcastReceiver {
        @SuppressWarnings("ConstantConditions")
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.verbose(TAG, "NetworkChangeBroadcastReceiver......");

            ConnectivityManager manager = (ConnectivityManager) getSystemService(Service.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            // StatusBar
            mStatusBar.onNetworkChange(networkInfo);

            // Marquee
            mMarquee.onNetworkChange(networkInfo);

            // QiyiManager
            QiyiManager.getInstance(mContext).onNetworkChange(networkInfo);

            // DesktopItem
            DesktopItemManager.getInstance(mContext).onNetworkChange(networkInfo);

            // Screensaver
            ScreensaverManager.getInstance(mContext).onNetworkChange(networkInfo);

            // app update
            AppUpdateManager.getInstance(mContext).onNetworkChange(networkInfo);
        }
    }

    /**
     * 应用状态改变广播
     * <p>
     * 广播原则：
     * >>> 安装
     * action : android.intent.action.PACKAGE_ADDED
     * <p>
     * >>> 升级
     * action : android.intent.action.PACKAGE_REMOVED
     * action : android.intent.action.PACKAGE_ADDED
     * action : android.intent.action.PACKAGE_REPLACED
     * <p>
     * >>> 卸载
     * action : android.intent.action.PACKAGE_REMOVED
     * action : android.intent.action.PACKAGE_FULLY_REMOVED
     * <p>
     * Created by lancy on 2018/1/8 12:38
     */
    private final class PackageChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            DesktopItemManager.getInstance(mContext).onPackageChanged(intent);
        }
    }

    /**
     * 应用安装完成回调（LycooPackageInstaller发出的广播）
     * <p>
     * Created by lancy on 2018/6/7 17:13
     */
    private final class PackageInstallerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //USB安装KTV
//            ApplicationUtils.showInstallDialog(intent,context);
            DesktopItemManager.getInstance(mContext).onPackageInstall(context, intent);
        }

    }


    @Override
    public void onBackPressed() {
    }

    /**
     * 启动老化测试
     * <p>
     * Created by lancy on 2018/6/26 10:35
     */
    private void launchAgingTest() {
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            CustomToast
                    .makeText(mContext, R.string.msg_failed_to_auto_launch_agingtest, CustomToast.MessageType.ERROR)
                    .show();
            return;
        }

        mAgingTestDisposable = Observable
                .interval(1, 2, TimeUnit.SECONDS)
                .subscribe(count -> {
                    LogUtils.info(TAG, "scan wifi count = " + count);
                    if (count >= mContext.getResources().getInteger(R.integer.aging_test_scan_max_count)) {
                        // 取消订阅
                        if (mAgingTestDisposable != null && !mAgingTestDisposable.isDisposed()) {
                            mAgingTestDisposable.dispose();
                        }
                    }

                    wifiManager.startScan();
                    List<ScanResult> scanResults = wifiManager.getScanResults();
                    if (!CollectionUtils.isEmpty(scanResults)) {
                        String ssid_test = mContext.getString(R.string.aging_test_ssid);
                        String ssid_ktv = mContext.getString(R.string.aging_ktv_ssid);
                        for (ScanResult scanResult : scanResults) {
                            if (ssid_ktv.equals(scanResult.SSID)) {
                                // 启动agingKtv
  /*                              String agingKtvPackageName = SystemPropertiesUtils.get(CommonConstants.PROPERTY_SHORTCUT_KEY_AGING_KTV);
                                if (!TextUtils.isEmpty(agingKtvPackageName)) {
                                    try {
                                        ApplicationUtils.openApplication(mContext, agingKtvPackageName);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }*/
                                try {
                                    if (ApplicationUtils.isAppInstalled(mContext, CommonConstants.PACKAGE_AGING_APP)) {
                                        Intent intent = new Intent(CommonConstants.PACKAGE_AGING_APP);
                                        intent.setComponent(new ComponentName(CommonConstants.PACKAGE_AGING_APP, CommonConstants.PACKAGE_AGING_APP_ACTIVITY));
                                        startActivity(intent);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // 取消订阅
                                if (mAgingTestDisposable != null && !mAgingTestDisposable.isDisposed()) {
                                    mAgingTestDisposable.dispose();
                                }

                                // 启动老化计时功能
                                persistAgingTime();
                                break;
                            } else if (ssid_test.equals(scanResult.SSID)) {
                                // 启动agingTest
                                String agingTestPackageName = SystemPropertiesUtils.get(CommonConstants.PROPERTY_SHORTCUT_KEY_AGING_TEST);
                                if (!TextUtils.isEmpty(agingTestPackageName)) {
                                    try {
                                        ApplicationUtils.openApplication(mContext, agingTestPackageName);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                                // 取消订阅
                                if (mAgingTestDisposable != null && !mAgingTestDisposable.isDisposed()) {
                                    mAgingTestDisposable.dispose();
                                }

                                // 启动老化计时功能
                                persistAgingTime();
                                break;
                            }
                        }
                        DeviceUtils.checkWifiInstallAPK(mContext, mAgingTestDisposable, scanResults);
                    }
                }, throwable -> {
                    LogUtils.error(TAG, "failed to launchAgingTest, error message : " + throwable.getMessage());
                    throwable.printStackTrace();
                });
    }

    private int mAgingtestCount = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_8:
                mPressKeyCode = keyCode;
                LogUtils.error(TAG, "KEYCODE_8::" + mAgingtestCount);
                if (++mAgingtestCount == 5) {
                    mAgingtestCount = 0;
                    try {
                        if (ApplicationUtils.isAppInstalled(mContext, CommonConstants.PACKAGE_AGING_APP)) {
                            Intent intent = new Intent(CommonConstants.PACKAGE_AGING_APP);
                            intent.setComponent(new ComponentName(CommonConstants.PACKAGE_AGING_APP, CommonConstants.PACKAGE_AGING_APP_ACTIVITY));
                            startActivity(intent);
//                           ApplicationUtils.openApplication(mContext,CommonConstants.PACKAGE_AGING_APP);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case KeyEvent.KEYCODE_9:
                if (mPressKeyCode == KeyEvent.KEYCODE_8) {
                    mPressKeyCode = keyCode;

                    int count = getSharedPreferences(Constants.SP_DESKTOP, Context.MODE_PRIVATE).getInt(Constants.INTERFACE_TEST_COUNT, 0);
                    LogUtils.debug(TAG, "interface test count = " + count);
                    if (count < 5) {
                        String packageName = SystemPropertiesUtils.get(CommonConstants.PROPERTY_SHORTCUT_KEY_INTERFACE_TEST);
                        LogUtils.debug(TAG, "interface test packageName : " + packageName);
                        if (!TextUtils.isEmpty(packageName)) {
                            getSharedPreferences(Constants.SP_DESKTOP, Context.MODE_PRIVATE)
                                    .edit()
                                    .putInt(Constants.INTERFACE_TEST_COUNT, ++count)
                                    .apply();
                            try {
                                ApplicationUtils.openApplication(mContext, packageName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }


    /**
     * 持久化老化时间
     * 每隔6分钟记录一次
     * <p>
     * Created by lancy on 2018/7/19 16:29
     */
    private void persistAgingTime() {
        String agingTestComponent = SystemPropertiesUtils.get(CommonConstants.PROPERTY_SHORTCUT_KEY_AGING_TEST);
        if (TextUtils.isEmpty(agingTestComponent)) {
            return;
        }

        if (agingTestComponent.contains("/")) {
            agingTestComponent = agingTestComponent.substring(0, agingTestComponent.indexOf("/"));
        }

        String agingTestPackageName = agingTestComponent;
        LogUtils.debug(TAG, "agingTestPackageName : " + agingTestPackageName);

        mAgingTimeDisposable = Observable
                .interval(6, 6, TimeUnit.MINUTES)
                .subscribe(count -> {
                    boolean isAagingAppRunning = false;
                    // 启动agingTest
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        List<AndroidAppProcess> runningAppProcesses = AndroidProcesses.getRunningAppProcesses();
                        if (!runningAppProcesses.isEmpty()) {
                            for (AndroidAppProcess runningAppProcess : runningAppProcesses) {
                                LogUtils.debug(TAG, "foreground [ " + runningAppProcess.foreground + " ] : " + runningAppProcess.getPackageName());
                                if (runningAppProcess.foreground
                                        && !TextUtils.isEmpty(runningAppProcess.getPackageName())
                                        && runningAppProcess.getPackageName().equals(agingTestPackageName)) {
                                    isAagingAppRunning = true;
                                    break;
                                }
                            }
                        }
                    } else {
                        String topAppPackageName = ApplicationUtils.getTopAppPackageName(mContext);
                        LogUtils.debug(TAG, "topAppPackageName : " + topAppPackageName);
                        if (!TextUtils.isEmpty(topAppPackageName)
                                && topAppPackageName.equals(agingTestPackageName)) {
                            isAagingAppRunning = true;
                        }
                    }
                    LogUtils.info(TAG, "isAagingAppRunning : " + isAagingAppRunning);
                    if (isAagingAppRunning) {
                        updateAgingTime();
                        return;
                    }

                    // 取消订阅
                    if (mAgingTimeDisposable != null && !mAgingTimeDisposable.isDisposed()) {
                        mAgingTimeDisposable.dispose();
                    }
                }, throwable -> {
                    LogUtils.error(TAG, "failed to persistAgingTime, error message : " + throwable.getMessage());
                    throwable.printStackTrace();
                });
    }

    /**
     * 更新老化时间
     * agingTime记录老化时间， 以分为单位
     * <p>
     * Created by lancy on 2018/7/17 0:09
     */
    @SuppressLint("WorldReadableFiles")
    private void updateAgingTime() {
        if (mPublicSp == null) {
            mPublicSp = getSharedPreferences(Constants.SP_PUBLIC, Context.MODE_WORLD_READABLE);
        }
        int agingTime = mPublicSp.getInt(getString(R.string.aging_time), 0);
        agingTime += 6;
        LogUtils.debug(TAG, "agingTime = " + agingTime);
        mPublicSp
                .edit()
                .putInt(getString(R.string.aging_time), agingTime)
                .apply();
    }

    /**
     * 查询老化时间
     *
     * @return 全部老化时间， 以小时为单位
     * <p>
     * Created by lancy on 2018/7/19 17:13
     */
    @SuppressLint("WorldWriteableFiles")
    private String queryAgaingTime() {
        String agingTime = "未读取到老化信息, 请联系工程人员";
        try {
            Context launcherContext = createPackageContext(SystemPropertiesUtils.get(CommonConstants.PROPERTY_DEFAULT_LAUNCHER_PACKAGENAME), Context.CONTEXT_IGNORE_SECURITY);
            if (launcherContext != null) {
                SharedPreferences sp_public = launcherContext.getSharedPreferences("sp_public", Context.MODE_WORLD_WRITEABLE);
                int minutes = sp_public.getInt(getString(R.string.aging_time), -1);
                LogUtils.info(TAG, "agingTime = " + agingTime);
                if (minutes >= 0) {
                    float hours = minutes / 60;
                    agingTime = String.valueOf(hours);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return agingTime;
    }

    /**
     * 更新老化时间文件
     *
     * @param time 老化时间（分钟）
     *             <p>
     *             Created by lancy on 2018/6/27 22:29
     */
    @Deprecated
    private void updateAgingTime(String time) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(Environment.getExternalStorageDirectory(), getString(R.string.aging_time)));
            fileWriter.write(time);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 查询老化时间
     *
     * @return 老化时间（小时）
     * <p>
     * Created by lancy on 2018/6/27 22:30
     */
    @Deprecated
    private String queryAgingTime() {
        String agingTime = "0";
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(new File(Environment.getExternalStorageDirectory(), getString(R.string.aging_time)));
            char[] buf = new char[64];
            int len;
            while ((len = fileReader.read(buf)) != -1) {
                float minutes = Float.parseFloat(new String(buf, 0, len));
                float hour = minutes / 60;
                LogUtils.debug(TAG, "minutes : " + minutes + ", hour(agingTime) = " + hour);
                agingTime = String.valueOf(hour);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return agingTime;
    }

    private String getImageBg() {
        if (Build.MODEL.contains(Constants.MODEL_AIBOSHENG)) {
            return "bg_104_aibosheng";
        } else if (Build.MODEL.contains(Constants.MODEL_AILIPU)) {
            return "bg_104_ailipu";
        } else if (Build.MODEL.contains(Constants.MODEL_AIXIANG)) {
            return "bg_104_aixiang";
        } else if (Build.MODEL.contains(Constants.MODEL_BOK)) {
            return "bg_104_bok";
        } else if (Build.MODEL.contains(Constants.MODEL_FENWEI)) {
            return "bg_104_fenwei";
        } else if (Build.MODEL.contains(Constants.MODEL_GAV)) {
            return "bg_104_gav";
        } else if (Build.MODEL.contains(Constants.MODEL_HUAWEI)) {
            return "bg_104_huawei";
        } else if (Build.MODEL.contains(Constants.MODEL_JBA)) {
            return "bg_104_jba";
        } else if (Build.MODEL.contains(Constants.MODEL_LICHAO)) {
            return "bg_104_lichao";
        } else if (Build.MODEL.contains(Constants.MODEL_MINGDU)) {
            return "bg_104_mingdu";
        } else if (Build.MODEL.contains(Constants.MODEL_MINGGE)) {
            return "bg_104_mingde";
        } else if (Build.MODEL.contains(Constants.MODEL_OUBO)) {
            return "bg_104_oubo";
        } else if (Build.MODEL.contains(Constants.MODEL_SHANGQI)) {
            return "bg_104_shangqi";
        } else if (Build.MODEL.contains(Constants.MODEL_SHANGJIN)) {
            return "bg_104_shangjin";
        } else if (Build.MODEL.contains(Constants.MODEL_SHANLING)) {
            return "bg_104_shanling";
        } else if (Build.MODEL.contains(Constants.MODEL_XIANDAI)) {
            return "bg_104_xiandai";
        } else if (Build.MODEL.contains(Constants.MODEL_YEXIANG)) {
            return "bg_104_yexiang";
        } else if (Build.MODEL.contains(Constants.MODEL_YIWEI)) {
            return "bg_104_yiwei";
        } else {
            return "bg_104";
        }
    }
}
