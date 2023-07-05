package com.lycoo.desktop.base;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.DeviceUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.widget.CustomToast;
import com.lycoo.desktop.R;
import com.lycoo.desktop.R2;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Basic media player activity, support basic functions for mediaplayer
 *
 * Created by lancy on 2018/4/8
 */
@SuppressLint("Registered")
public class BaseVideoPlayerActivity<T> extends BaseActivity {
    private static final String TAG = BaseVideoPlayerActivity.class.getSimpleName();

    private static final int ADJUST_VOLUME_UP = 100;
    private static final int ADJUST_VOLUME_DOWN = 101;

    private static final long HIDE_PANEL_DELAY_LONG = 5000;      // 控制条悬浮时间
    private static final long HIDE_PANEL_DELAY_SHORT = 0;        // 控制条悬浮时间
    private static final float SCROLL_MIN_DISTANCE = 100;        // 滑动最新距离
    private static final float START_MIN_DISTANCE = 50;          // 边距有效范围
    private static final long NUMBER_COUNT_DOWN_INTERVAL = 3000;       // 数字悬浮时间

    @BindView(R2.id.loading_panel) protected View mLoadingPanel;
    @BindView(R2.id.tv_loading) protected TextView mLoadingText;
    @BindView(R2.id.buffering_panel) protected View mBufferingPanel;
    @BindView(R2.id.tv_buffering) protected TextView mBufferingText;
    @BindView(R2.id.header) protected View mHeader;
    @BindView(R2.id.tv_title) protected TextView mTitleText;
    @BindView(R2.id.control_panel) protected View mControlPanel;
    @BindView(R2.id.iv_download) protected ImageView mDownloadButton;
    @BindView(R2.id.iv_prev) protected ImageView mPlayPrevButton;
    @BindView(R2.id.iv_play_pause) protected ImageView mPlayPauseButton;
    @BindView(R2.id.iv_next) protected ImageView mPlayNextButton;
    @BindView(R2.id.tv_duration) protected TextView mDurationText;
    @BindView(R2.id.tv_cur_position) protected TextView mCurPositionText;
    @BindView(R2.id.sb_progress) protected SeekBar mSeekBar;
    @BindView(R2.id.surface_view) protected SurfaceView mSurfaceView;
    @BindView(R2.id.iv_drawer) protected ImageView mDrawerButton;
    @BindView(R2.id.tv_number) protected TextView mNumberText;

    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;
    private static AudioManager mAudioManager;
    private File mPlayFile;
    protected String mEncryptWords;

    private int mNewPostion;
    protected Context mContext = this;

    private Animation mShowControlPanelAnimation;
    private Animation mHideControlPanelAnimation;
    private Animation mShowHeaderAnimation;
    private Animation mHideHeaderAnimation;
    private Animation mShowDrawerAnimation;
    private Animation mHideDrawerAnimation;
    Handler mHandler = new Handler();
    private Handler mControlHandler;
    private Instrumentation mInstrumentation;
    private boolean mNumberEnable = false;
    private String mNumber = "";
    protected List<T> mFiles = new ArrayList<>();

    private float mRawX;
    private float mRawY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);
        initData();
        initView();
        createControlThread();
    }

//    private void setupFullscreen() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//    }

    /**
     * Initialize UI
     *
     * Created by lancy on 2018/4/9 0:48
     */
    @SuppressLint("ClickableViewAccessibility")
    protected void initView() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSHCallback);
        if (DeviceUtils.isTpEnable()) {
            mHeader.setPadding(
                    mHeader.getPaddingLeft() + getResources().getDimensionPixelSize(R.dimen.global_arc_menu_icon_size),
                    mHeader.getPaddingTop(),
                    mHeader.getPaddingRight(),
                    mHeader.getPaddingBottom());
        }

        mTitleText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mLoadingText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mBufferingText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mDurationText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mCurPositionText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mSeekBar.setFocusable(true);
        mSeekBar.setFocusableInTouchMode(true);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
    }

    /**
     * Initialize data
     */
    protected void initData() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private void createControlThread() {
        HandlerThread handlerThread = new HandlerThread("control");
        handlerThread.start();
        mControlHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ADJUST_VOLUME_UP:
                        if (mInstrumentation == null) {
                            mInstrumentation = new Instrumentation();
                        }
                        mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_UP);
                        break;
                    case ADJUST_VOLUME_DOWN:
                        if (mInstrumentation == null) {
                            mInstrumentation = new Instrumentation();
                        }
                        mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN);
                        break;
                }
            }
        };
    }

    /**
     * Prepare to start
     *
     * @param path media file's path
     *
     *             Created by lancy on 2018/4/9 0:48
     */
    protected void startPlay(String path) {
        if (requestAudioFocus()) {
            mPlayPauseButton.setImageResource(R.drawable.ic_pause);
            openVideo(path);
        }
    }

    /**
     * Play next media file
     *
     * Created by lancy on 2018/4/9 0:37
     */
    @OnClick(R2.id.iv_next)
    protected void playNext() {
    }

    /**
     * Play previous media file
     *
     * Created by lancy on 2018/4/9 0:37
     */
    @OnClick(R2.id.iv_prev)
    protected void playPrev() {
    }

    /**
     * Play or pause
     *
     * Created by lancy on 2018/4/8 23:42
     */
    @OnClick(R2.id.iv_play_pause)
    protected void playOrPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            showPanels(false);
            mPlayPauseButton.setImageResource(R.drawable.ic_play);
            mMediaPlayer.pause();
        } else {
            if (requestAudioFocus()) {
                hidePanels(HIDE_PANEL_DELAY_SHORT);
                mPlayPauseButton.setImageResource(R.drawable.ic_pause);
                mMediaPlayer.start();
            }
        }
    }

    /**
     * Download the video
     *
     * Created by lancy on 2018/4/12 15:39
     */
    @OnClick(R2.id.iv_download)
    protected void download() {

    }

    /**
     * Adjust volume down
     *
     * Created by lancy on 2018/6/4 10:32
     */
    @OnClick(R2.id.iv_volume_down)
    protected void adjustVolumeDown() {
        mControlHandler.sendEmptyMessage(ADJUST_VOLUME_DOWN);
    }

    /**
     * Adjust volume up
     *
     * Created by lancy on 2018/6/4 10:32
     */
    @OnClick(R2.id.iv_volume_up)
    protected void adjustVolumeUp() {
        mControlHandler.sendEmptyMessage(ADJUST_VOLUME_UP);
    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    protected void onPrepared(MediaPlayer mp) {
        LogUtils.verbose(TAG, "onPrepared()......");
        mp.start();
        ViewUtils.setViewShown(false, mLoadingPanel);
        showPanels(true);
    }

    /**
     * Called to indicate an error.
     *
     * @param mp    the MediaPlayer the error pertains to
     * @param what  the type of error that has occurred:
     *              <ul>
     *              <li>{@link #MEDIA_ERROR_UNKNOWN}
     *              <li>{@link #MEDIA_ERROR_SERVER_DIED}
     *              </ul>
     * @param extra an extra code, specific to the error. Typically
     *              implementation dependent.
     *              <ul>
     *              <li>{@link #MEDIA_ERROR_IO}
     *              <li>{@link #MEDIA_ERROR_MALFORMED}
     *              <li>{@link #MEDIA_ERROR_UNSUPPORTED}
     *              <li>{@link #MEDIA_ERROR_TIMED_OUT}
     *              <li><code>MEDIA_ERROR_SYSTEM (-2147483648)</code> - low-level system error.
     *              </ul>
     * @return True if the method handled the error, false if it didn't.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the OnCompletionListener to be called.
     */
    protected boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtils.error(TAG, "onError...... what = " + what + ", extra = " + extra);
        return false;
    }

    /**
     * Called to indicate an info or a warning.
     *
     * @param mp    the MediaPlayer the info pertains to.
     * @param what  the type of info or warning.
     *              <ul>
     *              <li>{@link #MEDIA_INFO_UNKNOWN}
     *              <li>{@link #MEDIA_INFO_VIDEO_TRACK_LAGGING}
     *              <li>{@link #MEDIA_INFO_VIDEO_RENDERING_START}
     *              <li>{@link #MEDIA_INFO_BUFFERING_START}
     *              <li>{@link #MEDIA_INFO_BUFFERING_END}
     *              <li><code>MEDIA_INFO_NETWORK_BANDWIDTH (703)</code> -
     *              bandwidth information is available (as <code>extra</code> kbps)
     *              <li>{@link #MEDIA_INFO_BAD_INTERLEAVING}
     *              <li>{@link #MEDIA_INFO_NOT_SEEKABLE}
     *              <li>{@link #MEDIA_INFO_METADATA_UPDATE}
     *              <li>{@link #MEDIA_INFO_UNSUPPORTED_SUBTITLE}
     *              <li>{@link #MEDIA_INFO_SUBTITLE_TIMED_OUT}
     *              </ul>
     * @param extra an extra code, specific to the info. Typically
     *              implementation dependent.
     * @return True if the method handled the info, false if it didn't.
     * Returning false, or not having an OnInfoListener at all, will
     * cause the info to be discarded.
     */
    protected boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            // 开始缓冲
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                LogUtils.info(TAG, "buffering start......");
                ViewUtils.setViewShown(true, mBufferingPanel);
                break;

            // 缓冲完成
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                LogUtils.info(TAG, "buffering end......");
                ViewUtils.setViewShown(false, mBufferingPanel);
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    protected void onCompletion(MediaPlayer mp) {
        LogUtils.verbose(TAG, "onCompletion......");
    }

    /**
     * This is called immediately after the surface is first created.
     * Implementations of this should start up whatever rendering code
     * they desire.  Note that only one thread can ever draw into
     * a {@link Surface}, so you should not draw into the Surface here
     * if your normal rendering will be in another thread.
     *
     * @param holder The SurfaceHolder whose surface is being created.
     */
    protected void onSurfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
    }

    /**
     * Show file list dialog
     *
     * Created by lancy on 2018/4/9 0:58
     */
    @OnClick(R2.id.iv_drawer)
    protected void showFilesDialog() {
        hidePanels(HIDE_PANEL_DELAY_SHORT);
    }

    /**
     * Start to media file
     *
     * @param path media file's path
     *
     *             Created by lancy on 2018/4/8 17:41
     */
    private void openVideo(String path) {
        LogUtils.debug(TAG, "path = " + path);
        if (StringUtils.isEmpty(path)) {
            return;
        }

        release();
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            // 如果文件是加密过的， 需要解密
            if (!TextUtils.isEmpty(mEncryptWords)) {
                mPlayFile = new File(path);
                mMediaPlayer.setDataSource(new FileInputStream(mPlayFile).getFD(), (long) mEncryptWords.length(), mPlayFile.length() - mEncryptWords.length());
            } else {
                mMediaPlayer.setDataSource(path);
            }
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

    /**
     * Release Mediaplayer
     *
     * Created by lancy on 2018/4/8 17:41
     */
    private void release() {
        if (null != mMediaPlayer) {
            mMediaPlayer.stop();
//            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * A client may implement this interface to receive information about
     * changes to the surface.  When used with a {@link SurfaceView}, the
     * Surface being held is only available between calls to
     * {@link #surfaceCreated(SurfaceHolder)} and
     * {@link #surfaceDestroyed(SurfaceHolder)}.  The Callback is set with
     * {@link SurfaceHolder#addCallback SurfaceHolder.addCallback} method.
     */
    private final SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        /**
         * This is called immediately after the surface is first created.
         * Implementations of this should start up whatever rendering code
         * they desire.  Note that only one thread can ever draw into
         * a {@link Surface}, so you should not draw into the Surface here
         * if your normal rendering will be in another thread.
         *
         * @param holder The SurfaceHolder whose surface is being created.
         */
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtils.verbose(TAG, "surfaceCreated()......");
            onSurfaceCreated(holder);
        }

        /**
         * This is called immediately after any structural changes (format or
         * size) have been made to the surface.  You should at this point update
         * the imagery in the surface.  This method is always called at least
         * once, after {@link #surfaceCreated}.
         *
         * @param holder The SurfaceHolder whose surface has changed.
         * @param format The new PixelFormat of the surface.
         * @param width The new width of the surface.
         * @param height The new height of the surface.
         */
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            LogUtils.verbose(TAG, "surfaceChanged()......");
        }

        /**
         * This is called immediately before a surface is being destroyed. After
         * returning from this call, you should no longer try to access this
         * surface.  If you have a rendering thread that directly accesses
         * the surface, you must ensure that thread is no longer touching the
         * Surface before returning from this function.
         *
         * @param holder The SurfaceHolder whose surface is being destroyed.
         */
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtils.verbose(TAG, "surfaceDestroyed()......");
            mSurfaceHolder = null;
            release();
        }
    };
    private final MediaPlayer.OnPreparedListener mPreparedListener = this::onPrepared;
    private final MediaPlayer.OnErrorListener mErrorListener = this::onError;
    private final MediaPlayer.OnInfoListener mInfoListener = this::onInfo;
    private final MediaPlayer.OnCompletionListener mCompletionListener = this::onCompletion;

    @OnTouch(R2.id.surface_view)
    protected boolean processSurfaceTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mRawY = event.getRawY();
                mRawX = event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                float yDistance = event.getRawY() - mRawY;
                LogUtils.debug(TAG, "yDistance = " + yDistance);
                // process up-down scroll
                if (Math.abs(yDistance) >= SCROLL_MIN_DISTANCE) {
                    if (yDistance > 0) {
                        playNext();
                    } else {
                        playPrev();
                    }
                    break;
                }

                // 如果是从最左侧向右滑动，则显示文件列表
                if (mRawX < START_MIN_DISTANCE && (event.getRawX() - mRawX >= SCROLL_MIN_DISTANCE)) {
                    showFilesDialog();
                    break;
                }

                // show play controller pane by short press
                togglePanels();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                showFilesDialog();
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                showPanels(true);
                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                playOrPause();
                break;

            case KeyEvent.KEYCODE_DPAD_UP:
                playPrev();
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                playNext();
                break;

            // 数字键切换曲目
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                if (mNumberEnable) {
                    mNumberText.setVisibility(View.VISIBLE);
                    if (mNumber.length() < 4) {
                        mNumber = mNumberText.getText() + String.valueOf(keyCode - 7);
                        mNumberText.setText(mNumber);
                    }
                    mHandler.removeCallbacks(mPlayFileByNumberRunnable);
                    mHandler.postDelayed(mPlayFileByNumberRunnable, NUMBER_COUNT_DOWN_INTERVAL);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }


    /**
     * Toggle panels
     *
     * Created by lancy on 2018/4/13 21:28
     */
    private void togglePanels() {
        if (mHeader.getVisibility() != View.VISIBLE) {
            showPanels(true);
        } else {
            hidePanels(HIDE_PANEL_DELAY_SHORT);
        }
    }

    /**
     * Show panels
     *
     * Created by lancy on 2018/4/8 18:23
     */
    protected void showPanels(boolean autoHide) {
        mHandler.removeCallbacks(mHidePanelsRunnable);
        // Header
        if (mHeader.getVisibility() != View.VISIBLE) {
            if (mShowHeaderAnimation == null) {
                mShowHeaderAnimation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF,
                        0,
                        Animation.RELATIVE_TO_SELF,
                        0,
                        Animation.RELATIVE_TO_SELF,
                        -1.0f,
                        Animation.RELATIVE_TO_SELF,
                        0f);
                mShowHeaderAnimation.setDuration(500);
            }
            ViewUtils.setViewShown(true, mHeader);
            mHeader.startAnimation(mShowHeaderAnimation);
        }

        // Control panel
        if (mControlPanel.getVisibility() != View.VISIBLE) {
            int duration = mMediaPlayer.getDuration();
            int curPosition = mMediaPlayer.getCurrentPosition();
            int progress = duration == 0 ? 0 : curPosition * 100 / duration;
            mDurationText.setText(stringForTime(duration));
            mCurPositionText.setText(stringForTime(curPosition));
            mSeekBar.setProgress(progress);

            if (mShowControlPanelAnimation == null) {
                mShowControlPanelAnimation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF,
                        0,
                        Animation.RELATIVE_TO_SELF,
                        0,
                        Animation.RELATIVE_TO_SELF,
                        1.0f,
                        Animation.RELATIVE_TO_SELF,
                        0f);
                mShowControlPanelAnimation.setDuration(500);
            }
            ViewUtils.setViewShown(true, mControlPanel);
            mControlPanel.startAnimation(mShowControlPanelAnimation);
        }

        // Drawer
        if (mDrawerButton.getVisibility() != View.VISIBLE) {
            if (mShowDrawerAnimation == null) {
                mShowDrawerAnimation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF,
                        -1.0f,
                        Animation.RELATIVE_TO_SELF,
                        0,
                        Animation.RELATIVE_TO_SELF,
                        0,
                        Animation.RELATIVE_TO_SELF,
                        0f);
                mShowDrawerAnimation.setDuration(500);
            }
            ViewUtils.setViewShown(true, mDrawerButton);
            mDrawerButton.startAnimation(mShowDrawerAnimation);
        }

        if (autoHide) {
            hidePanels(HIDE_PANEL_DELAY_LONG);
        }
    }

    /**
     * Hide panels
     *
     * Created by lancy on 2018/4/8 18:23
     */
    private void hidePanels(long delayMillis) {
        mHandler.postDelayed(mHidePanelsRunnable, delayMillis);
    }

    private final Runnable mHidePanelsRunnable = new Runnable() {
        public void run() {
            // Header
            if (mHeader.getVisibility() != View.GONE) {
                if (mHideHeaderAnimation == null) {
                    mHideHeaderAnimation = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF,
                            0,
                            Animation.RELATIVE_TO_SELF,
                            0,
                            Animation.RELATIVE_TO_SELF,
                            0,
                            Animation.RELATIVE_TO_SELF,
                            -1.0f);
                    mHideHeaderAnimation.setDuration(500);
                }
                mHeader.startAnimation(mHideHeaderAnimation);
                mHeader.setVisibility(View.GONE);
            }

            // Control panel
            if (mControlPanel.getVisibility() != View.GONE) {
                if (mHideControlPanelAnimation == null) {
                    mHideControlPanelAnimation = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF,
                            0,
                            Animation.RELATIVE_TO_SELF,
                            0,
                            Animation.RELATIVE_TO_SELF,
                            0,
                            Animation.RELATIVE_TO_SELF,
                            1.0f);
                    mHideControlPanelAnimation.setDuration(500);
                }
                mControlPanel.startAnimation(mHideControlPanelAnimation);
                mControlPanel.setVisibility(View.GONE);
            }

            // Drawer
            if (mDrawerButton.getVisibility() != View.GONE) {
                if (mHideDrawerAnimation == null) {
                    mHideDrawerAnimation = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF,
                            0,
                            Animation.RELATIVE_TO_SELF,
                            -1.0f,
                            Animation.RELATIVE_TO_SELF,
                            0,
                            Animation.RELATIVE_TO_SELF,
                            0);
                    mHideDrawerAnimation.setDuration(500);
                }
                mDrawerButton.startAnimation(mHideDrawerAnimation);
                mDrawerButton.setVisibility(View.GONE);
            }
        }
    };

    /**
     * A callback that notifies clients when the progress level has been
     * changed. This includes changes that were initiated by the user through a
     * touch gesture or arrow key/trackball as well as changes that were initiated
     * programmatically.
     */
    private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            LogUtils.debug(TAG, "onProgressChanged progress = " + progress);
            if (!fromUser) {
                return;
            }

            mHandler.removeCallbacks(mHidePanelsRunnable);
            int duration = mMediaPlayer.getDuration();
            mNewPostion = duration * progress / 100;
            mCurPositionText.setText(stringForTime(mNewPostion));
            mHandler.removeCallbacks(mSeekRunnable);
            mHandler.postDelayed(mSeekRunnable, 1000);
            hidePanels(HIDE_PANEL_DELAY_LONG);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private final Runnable mSeekRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null) {
                mMediaPlayer.seekTo(mNewPostion);
            }
        }
    };

    /**
     * 数字键播放媒体文件任务
     *
     * Created by lancy on 2018/5/22 16:08
     */
    private final Runnable mPlayFileByNumberRunnable = new Runnable() {
        public void run() {
            mNumber = mNumberText.getText().toString();
            int numberIndex = Integer.valueOf(mNumber);

            // 重置TextView的显示
            mNumberText.setVisibility(View.GONE);
            mNumber = "";
            mNumberText.setText(mNumber);

            if (numberIndex < 1 || numberIndex > mFiles.size()) {
                CustomToast.makeText(mContext, R.string.msg_invalid_number, 2000, CustomToast.MessageType.ERROR).show();
                return;
            }

            play(numberIndex - 1);
        }
    };

    /**
     * 播放媒体文件
     *
     * @param index 媒体文件的索引
     *
     *              Created by lancy on 2018/5/22 16:08
     */
    protected void play(int index) {
        LogUtils.debug(TAG, "play index : " + (index + 1));
    }

    private final AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = focusChange -> {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            //失去焦点之后的操作
//                pause();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            //获得焦点之后的操作
//                start();
        }
    };

    protected boolean requestAudioFocus() {
        if (mAudioManager == null) {
            return false;
        }

        int result = mAudioManager.requestAudioFocus(
                mAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        LogUtils.debug(TAG, "request audio focus, ret = " + result);
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result;
    }

    private void abandonAudioFocus() {
        if (mAudioManager != null) {
            LogUtils.debug(TAG, "Abandon audio focus......");
            mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
            mAudioManager = null;
        }
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public void setNumberEnable(boolean numberEnable) {
        this.mNumberEnable = numberEnable;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
