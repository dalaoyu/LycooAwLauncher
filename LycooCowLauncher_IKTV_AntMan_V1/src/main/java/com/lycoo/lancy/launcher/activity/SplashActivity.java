package com.lycoo.lancy.launcher.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.helper.SystemPropertiesManager;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.SecurityUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;
import com.lycoo.desktop.db.DesktopDbManager;
import com.lycoo.desktop.db.DesktopDbOpenHelper;
import com.lycoo.desktop.helper.DesktopItemManager;
import com.lycoo.desktop.helper.GlideApp;
import com.lycoo.lancy.launcher.R;
import com.lycoo.lancy.launcher.base.BaseActivity;
import com.lycoo.lancy.launcher.config.Constants;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class SplashActivity extends BaseActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final boolean DEBUG_BOOT_VIDEO = true;

    @BindArray(R.array.hosts)
    String[] mHosts;
    @BindView(R.id.iv_bg)
    ImageView mBg;

    private Context mContext = this;
    private PreInstallBroadcastReceiver mPreInstallBroadcastReceiver;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        boolean cleanDatabases = SystemPropertiesUtils.getBoolean(CommonConstants.ACTION_CLEAN_DATABASES, false);
//        if (!cleanDatabases){
//            cleanDatabases(this);
//            doInitJob();
//            SystemPropertiesManager.getInstance(mContext).set(CommonConstants.ACTION_CLEAN_DATABASES, true);
//        }
//                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                am.clearApplicationUserData();



        ButterKnife.bind(this);
        if (!SecurityUtils.verifySpecialstr(mHosts)) {
            finish();
        } else {
            // 系统允许播放欢迎词 并且 欢迎词未播放完成
            if (SystemPropertiesManager.getInstance(mContext).getBoolean(CommonConstants.PROPERTY_SALUTATORY_ENABLE, false)
                    && !SystemPropertiesManager.getInstance(mContext).getBoolean(CommonConstants.PROPERTY_SALUTATORY_FINISHED, false)) {
                // 加载背景
                GlideApp.with(mContext)
                        .load(new File(Constants.SALUTATORY_BG))
                        .into(mBg);
                // 播放音频
                if (new File(Constants.SALUTATORY_FILE).exists()){
                    playSalutatory();
                }else {
                    init();
                }
            } else {
                init();
            }
        }
    }





    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * *
     *
     * @param directory
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                LogUtils.error(TAG,"delete DATE " +item);
                item.delete();
            }
        }
    }



    /**
     * 初始化
     * <p>
     * Created by lcchen on 2019/12/03
     */
    private void init() {

        // 处理3128H切边
        /*
         * 判断是否完成预装应用的安装， 不同的方案预装应用的安装方式，
         * 如果不需要LycooPreInstaller安装预置应用，则直接将persist.sys.lycoo.preinstall=true, 例如Rk3128,
         */
        if (SystemPropertiesUtils.getBoolean(CommonConstants.KEY_PREINSTALL_COMPLETED, false)) {
            openMainUI();
        } else {
            // 注册初始化应用监听器
            IntentFilter filter = new IntentFilter();
            filter.addAction(CommonConstants.ACTION_PREINSTALL);
            mPreInstallBroadcastReceiver = new PreInstallBroadcastReceiver();
            registerReceiver(mPreInstallBroadcastReceiver, filter);

            // 调用初始化应用安装器
            launchPreInstaller();
        }
    }


    private void openMainUI() {
        if (!getSharedPreferences(Constants.SP_DESKTOP, Context.MODE_PRIVATE).getBoolean(Constants.DESKTOP_INITIALIZED, false)) {
            doInitJob();
        } else {
            if (DEBUG_BOOT_VIDEO) {
                LogUtils.debug(TAG, "PROPERTY_BOOT_VAIDEO_ENABLE : " + SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_BOOT_VAIDEO_ENABLE, false));
                LogUtils.debug(TAG, "PROPERTY_BOOT_VAIDEO_FINISHED : " + SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_BOOT_VAIDEO_FINISHED, false));
                LogUtils.debug(TAG, "FILE_BOOT_VIDEO_FINISH : " + new File(Environment.getExternalStorageDirectory()
                        + File.separator
                        + CommonConstants.MARK_FILE_DIR, CommonConstants.FILE_BOOT_VIDEO_FINISH).exists());
            }

            if (SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_BOOT_VAIDEO_ENABLE, false)
                    && !SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_BOOT_VAIDEO_FINISHED, false)
//                    && !new File(Environment.getExternalStorageDirectory() + File.separator + CommonConstants.MARK_FILE_DIR, CommonConstants.FILE_BOOT_VIDEO_FINISH).exists()
            ) {
                showBootVideo();
            } else {
                showMainUI();
            }
        }
    }

    /**
     * 显示欢迎视频
     * <p>
     * Created by lancy on 2019/8/15 12:21
     */
    private void showBootVideo() {
        // 加载背景
        if (new File(Constants.SALUTATORY_BG).exists()){
            GlideApp.with(mContext)
                    .load(new File(Constants.SALUTATORY_BG))
                    .into(mBg);
        }
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(
                Observable
                        .timer(4, TimeUnit.SECONDS)
                        .subscribe(result -> {
                            mContext.startActivity(new Intent(mContext, BootVideoActivity.class));
                            finish();
                        }, throwable -> {
                            LogUtils.error(TAG, "failed to playSalutatory, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                            init();
                        }));
        //mContext.startActivity(new Intent(mContext, BootVideoActivity.class));
        //finish();
    }

    /**
     * 进入主界面
     * <p>
     * Created by lancy on 2017/12/14 16:55
     */
    private void showMainUI() {
        // 启动主Activity
        mContext.startActivity(new Intent(mContext, MainActivity.class));
        this.finish();
    }

    /**
     * 开始桌面初始化
     * <p>
     * Created by lancy on 2017/12/14 16:56
     */
    private void doInitJob() {
        Intent intent = new Intent(mContext, InitActivity.class);
        mContext.startActivity(intent);
        this.finish();
    }

    /**
     * 启动预置应用安装器
     * <p>
     * Created by lancy on 2017/12/14 16:53
     */
    private void launchPreInstaller() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                CommonConstants.LYCOO_PREINSTALLER_PACKAGENAME,
                CommonConstants.LYCOO_PREINSTALLER_LUANCH_CLASSNAME));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放欢迎词
     * <p>
     * Created by lancy on 2018/12/6 18:38
     */
    private void playSalutatory() {
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(
                Observable
                        .timer(10, TimeUnit.SECONDS)
                        .subscribe(result -> {
                            LogUtils.debug(TAG, "do playSalutatory......");
                            MediaPlayer mediaPlayer = MediaPlayer.create(mContext, Uri.parse(Constants.SALUTATORY_FILE));
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setOnCompletionListener(mp -> {
                                SystemPropertiesManager.getInstance(mContext).set(CommonConstants.PROPERTY_SALUTATORY_FINISHED, true);
                                init();
                            });
                            mediaPlayer.start();
                        }, throwable -> {
                            LogUtils.error(TAG, "failed to playSalutatory, error message : " + throwable.getMessage());
                            throwable.printStackTrace();
                            init();
                        }));
    }


    /**
     * 初始化应用监听器
     * 当PreInstaller安装完所有预装应用时会发出广播通知
     * <p>
     * Created by lancy on 2017/12/14 16:11
     */
    private final class PreInstallBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.debug(TAG, "PreinstallBroadcastReceiver, action = " + action);
            if (!StringUtils.isEmpty(action)
                    && action.equals(CommonConstants.ACTION_PREINSTALL)
                    && intent.getStringExtra(CommonConstants.KEY_PREINSTALL).equals(CommonConstants.EXTRA_PREINSTALL_COMPLETED)) {
                openMainUI();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPreInstallBroadcastReceiver != null) {
            unregisterReceiver(mPreInstallBroadcastReceiver);
        }

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }
}
