package com.lycoo.lancy.launcher.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.helper.SystemPropertiesManager;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.lancy.launcher.R;
import com.lycoo.lancy.launcher.base.BaseActivity;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 欢迎页面
 * <p>
 * Created by lancy on 2019/8/8
 */
public class BootVideoActivity extends BaseActivity {
    private static final String TAG = BootVideoActivity.class.getSimpleName();

    // private static final String MEDIA_PATH = "/system/media/welcome.mp4";
    // private static final String KTV_PACKAGENAME = "com.lycoo.lancy.ktv";

    @BindString(R.string.boot_video_path)
    String mVideoPath;
    @BindString(R.string.ktv_packagename)
    String mKtvPackageName;

    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;

    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;
    private Context mContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot_video);
        ButterKnife.bind(this);

        initView();
        openVideo(mVideoPath);
    }

    private void initView() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSHCallback);
    }

    private final SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtils.verbose(TAG, "surfaceCreated()......");
            mSurfaceHolder = holder;
            openVideo(mVideoPath);
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtils.verbose(TAG, "surfaceChanged()......");
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtils.verbose(TAG, "surfaceDestroyed()......");
            mSurfaceHolder = null;
            release();
        }
    };

    private void openVideo(String path) {
        LogUtils.debug(TAG, "path = " + path);
        if (!new File(path).exists()) {
            showMainUI();
            return;
        }


        release();
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            // 网络资源采用异步方式
            if (path.startsWith("http://") || path.startsWith("https://")) {
                mMediaPlayer.prepareAsync();
            }
            // 本地资源采用同步方式
            else {
                mMediaPlayer.prepare();
            }
        } catch (IllegalArgumentException | SecurityException | IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void release() {
        LogUtils.debug(TAG, ">>>>>>>>>>>>>>>>> release");
        if (null != mMediaPlayer) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private final MediaPlayer.OnPreparedListener mPreparedListener = this::onPrepared;
    private final MediaPlayer.OnErrorListener mErrorListener = this::onError;
    private final MediaPlayer.OnCompletionListener mCompletionListener = this::onCompletion;

    protected void onPrepared(MediaPlayer mp) {
        LogUtils.verbose(TAG, "onPrepared()......");
        mp.start();
    }

    protected boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtils.error(TAG, "onError...... what = " + what + ", extra = " + extra);
        showMainUI();
        return true;
    }

    protected void onCompletion(MediaPlayer mp) {
        LogUtils.verbose(TAG, "onCompletion......");
        showMainUI();
    }

    /**
     * 进入主界面 或者 进入KTV系统
     * 当系统未安装KTV, 则进入主界面
     * <p>
     * Created by lancy on 2019/8/15 12:28
     */
    private void showMainUI() {
        // 设置 “开机视频播放完成” 属性
        SystemPropertiesManager.getInstance(mContext).set(CommonConstants.PROPERTY_BOOT_VAIDEO_FINISHED, true);

        // 创建“视频播放完成”文件
        File dir = new File(Environment.getExternalStorageDirectory(), CommonConstants.MARK_FILE_DIR);
        if (!dir.exists()) {
            boolean result = dir.mkdir();
            LogUtils.debug(TAG, "Create MARK_FILE_DIR : " + result);
        }
        File bootVideoMarkFile = new File(dir, CommonConstants.FILE_BOOT_VIDEO_FINISH);
        if (!bootVideoMarkFile.exists()) {
            boolean result = false;
            try {
                result = bootVideoMarkFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LogUtils.debug(TAG, "Create FILE_BOOT_VIDEO_FINISH : " + result);
        }


        // 进入系统
        if (SystemPropertiesManager.getInstance(mContext).getBoolean(CommonConstants.PROPERTY_DUAL_BOOT_ENABLE, false)) {
            try {
                ApplicationUtils.openApplication(mContext, mKtvPackageName);
                finish();
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 启动主Activity
        mContext.startActivity(new Intent(mContext, MainActivity.class));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 屏蔽所有按键
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }
}
