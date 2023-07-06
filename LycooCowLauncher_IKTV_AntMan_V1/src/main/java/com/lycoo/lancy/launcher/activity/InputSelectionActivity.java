package com.lycoo.lancy.launcher.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.CustomBluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.CustomAudioManager;
import android.media.CustomDspManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lycoo.commons.domain.CommonConstants;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.DateUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.SecurityUtils;
import com.lycoo.commons.util.SystemPropertiesUtils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.view.SpectrumView;
import com.lycoo.commons.view.colorfulprogressbar.ColorfulProgressbar;
import com.lycoo.commons.widget.CustomToast;
import com.lycoo.lancy.launcher.R;
import com.lycoo.desktop.ui.LyricsListView;
import com.lycoo.lancy.launcher.config.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class InputSelectionActivity extends Activity {

    private static final String TAG = InputSelectionActivity.class.getSimpleName();

    private static final int SPECTRUM_COUNT = 40;
    /**
     * 蓝牙歌词
     */
    private static final byte OPE_CMD_L_BT_LYRICS = 0x45;
    /**
     * 蓝牙歌名
     */
    private static final byte OPE_CMD_L_BT_TITLE = 0x46;
    /**
     * 目前歌曲处于列表的第几首
     */
    private static final byte OPE_CMD_L_BT_NUMBER = 0x47;
    /**
     * 当前播放列表总数
     */
    private static final byte OPE_CMD_L_BT_TOTLE = 0x48;
    /**
     * 歌曲总时长
     */
    private static final byte OPE_CMD_L_BT_PLAY_TIME = 0x49;
    /**
     * 歌曲播放进度
     */
    private static final byte OPE_CMD_L_BT_PLAYING_TIME = 0x4A;
    @BindView(R.id.tv_title)
    TextView mTitleText;
    @BindView(R.id.root)
    RelativeLayout mRoot;
    @BindView(R.id.iv_bt_image)
    ImageView mBtImage;
    @BindView(R.id.spectrum_view)
    SpectrumView mLineInView;
    @BindView(R.id.iv_prev)
    ImageView mPrevButton;
    @BindView(R.id.iv_next)
    ImageView mNextButton;
    @BindView(R.id.iv_play_pause)
    ImageView mPlayPauseButton;
    @BindView(R.id.iv_volume_up)
    ImageView mVolumeUpButton;
    @BindView(R.id.iv_volume_down)
    ImageView mVolumeDownButton;
    @BindView(R.id.iv_bt_mode)
    ImageView mBtModeButton;
    @BindView(R.id.iv_linein_mode)
    ImageView mLineInButton;
    @BindView(R.id.tv_prompts)
    TextView mPromptsText;

    @BindView(R.id.ic_lyrics)
    ImageView mLyricsImg;
    @BindView(R.id.is_lyrics)
    LinearLayout mLyrics;
    @BindView(R.id.tv_bt_title)
    TextView tvBtTitle;
    @BindView(R.id.startTime)
    TextView startTime;
    @BindView(R.id.endTime)
    TextView endTime;
    @BindView(R.id.progressbar_ll)
    LinearLayout progressbarLl;
    @BindView(R.id.lyrics_control)
    LinearLayout mLyricsControl;
    @BindView(R.id.colorfulProgressbar)
    ColorfulProgressbar mNumberProgressBar;
    @BindView(R.id.lylv)
    LyricsListView mLyricsListView;
    private ImageView mCdImage;
    private registerBtBroadcastReceiver mBtBroadcastReceiver;
    private long intactTime, upDataProgressBarTime;
    private long playTime;
    private Animation mRotateAnimation;
    private Context mContext = this;
    private CustomAudioManager mCustomAudioManager;
    private Disposable mVibrateDisposable;
    private int mMode;
    private int lastStatus = CustomBluetoothManager.STATUS_UNKNOW;
    private CustomDspManager mCustomDspManager;
    private boolean mUseCustomDsp;

    private boolean hasLineIn;
    private boolean mUseSQDSP;

    private boolean mUseCustomBluetooth;
    private CustomBluetoothManager mBluetoothManager;
    private BtStatusChangedBroadcastReceiver mBtStatusChangedBroadcastReceiver;

    private String mLyricsValue = "";
    private boolean lyricsVisible = false;
    int i = 1;
    @SuppressLint("HandlerLeak")
    private final Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OPE_CMD_L_BT_LYRICS:
//                    tvlyrics.setText( SecurityUtils.toStringHex2((String) msg.obj));
                    String value = SecurityUtils.toStringHex2((String) msg.obj);
                    if (mLyricsListView != null && !value.equals(mLyricsValue)) {
                        mLyricsListView.updateItemData(value);
                        mLyricsValue = value;
                        lyricsVisible = true;
                        i = 1;
                        mLyricsListView.setEmptyView(false);
                    } else if (value.equals(mLyricsValue)) {
                        i = ++i;
                        if (i > 40) {
                            mLyricsListView.setEmptyView(true);
                        }
                    }
                    if (lyricsVisible && mLyricsControl.getVisibility() == View.GONE) {
                        visibleBtText();
                    }
                    break;
                case OPE_CMD_L_BT_TITLE:
                    tvBtTitle.setText(SecurityUtils.toStringHex2((String) msg.obj));
                    break;
                case OPE_CMD_L_BT_PLAY_TIME:
                    String endTimeS = SecurityUtils.toStringHex2((String) msg.obj);
                    intactTime = Long.parseLong(endTimeS) / 1000;
                    if (upDataProgressBarTime != intactTime) {
                        mNumberProgressBar.setProgress(0);
                        if (mLyricsListView != null)
                            mLyricsListView.clearAllData();
                        i = 1;
                    }
                    upDataProgressBarTime = intactTime;
                    endTime.setText(DateUtils.convertAll(intactTime));
                    break;
                case OPE_CMD_L_BT_PLAYING_TIME:
//                    ViewUtils.setViewShownInvisible(lyricsVisible, progressbarLl);
                    long result = Math.round((Double.parseDouble(String.valueOf(playTime)) / Double.parseDouble(String.valueOf(intactTime))) * 100);
                    playTime = SecurityUtils.covert((String) msg.obj) / 1000;
                    startTime.setText(DateUtils.convertAll(playTime));
                    mNumberProgressBar.setProgress(result);
                    if (playTime == 0) {
                        if (mLyricsListView != null)
                            mLyricsListView.clearAllData();
                        i = 1;
                    }
                    break;
                default:

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.verbose(TAG, "onCreate()......");
        setContentView(R.layout.activity_input_sel);
        ButterKnife.bind(this);

        initData();
        initView();
        checkData(getIntent());
        registerBroadcastReceiver();
        registerBtBroadcastReceiver();
    }

    @SuppressLint("WrongConstant")
    private void initData() {
        mBluetoothManager = (CustomBluetoothManager) getSystemService("custom_bluetooth");

        try {
            mUseCustomDsp = mContext.getResources().getBoolean(Resources.getSystem().getIdentifier(
                    "config_useCustomDsp",
                    "bool",
                    "android"));
            mUseCustomBluetooth = mContext.getResources().getBoolean(Resources.getSystem().getIdentifier(
                    "config_useCustomBluetooth",
                    "bool",
                    "android"));
            hasLineIn = mContext.getResources().getBoolean(Resources.getSystem().getIdentifier(
                    "config_hasLinein",
                    "bool",
                    "android"));
            mUseSQDSP = mContext.getResources().getBoolean(Resources.getSystem().getIdentifier(
                    "config_useSQDsp",
                    "bool",
                    "android"));
        } catch (Exception e) {
            mUseCustomDsp = false;
            mUseCustomBluetooth = false;
            hasLineIn = true;
            mUseSQDSP = true;
            e.printStackTrace();
        }

        LogUtils.debug(TAG, "config_useCustomDsp  = " + mUseCustomDsp);
        LogUtils.debug(TAG, "config_useCustomBluetooth  = " + mUseCustomBluetooth);
    }

    private void initView() {
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTitleText.getLayoutParams();
//        if (DeviceUtils.isTpEnable()) {
//            params.leftMargin += getResources().getDimensionPixelSize(R.dimen.global_arc_menu_icon_size);
//        } else {
//            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.input_sel_titile_margin_left);
//        }
//        mTitleText.setLayoutParams(params);
//        mTitleText.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mLineInView.setCount(SPECTRUM_COUNT);

        if (Build.MODEL.contains(Constants.MODEL_HUAWEI)) {
            mBtImage.setBackgroundResource(R.drawable.image_input_sel_bt_huawei);
        } else {
            mBtImage.setBackgroundResource(R.drawable.image_input_sel_bt);
        }

        if (Build.MODEL.contains(Constants.MODEL_SHILE)) {
            findViewById(R.id.root).setBackgroundResource(R.drawable.bg_input_sel);
        }
        mPromptsText.setTypeface(StyleManager.getInstance(mContext).getTypeface());

        mRotateAnimation = new RotateAnimation(0,
                360,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateAnimation.setFillAfter(true);
        //        mRotateAnimation.setDuration(4000);
        mRotateAnimation.setDuration(8000);
        mRotateAnimation.setRepeatCount(-1);
        mRotateAnimation.setInterpolator(new LinearInterpolator());

        addCdThumbView();
    }

    private void addCdThumbView(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                mContext.getResources().getDimensionPixelSize(R.dimen.cd_thumb_image_width),
                mContext.getResources().getDimensionPixelSize(R.dimen.cd_thumb_image_height));
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.rightMargin = mContext.getResources().getDimensionPixelSize(R.dimen.cd_thumb_image_margin_right);
        mCdImage = new ImageView(mContext);
        mCdImage.setLayoutParams(params);
        mCdImage.setImageResource(R.drawable.cd_thumb);
        mRoot.addView(mCdImage);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkData(intent);
    }

    private void registerBroadcastReceiver() {
        // 监听蓝牙状态改变
        IntentFilter filter = new IntentFilter();
        filter.addAction(CustomBluetoothManager.ACTION_STATUS_CHANGED);
        mBtStatusChangedBroadcastReceiver = new BtStatusChangedBroadcastReceiver();
        registerReceiver(mBtStatusChangedBroadcastReceiver, filter);
    }

    private void registerBtBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.lycoo.action.MUSIC_DETAIL_BT");
        mBtBroadcastReceiver = new registerBtBroadcastReceiver();
        registerReceiver(mBtBroadcastReceiver, filter);
    }


    private void unregisterBroadcastReceiver() {
        if (mBtStatusChangedBroadcastReceiver != null) {
            unregisterReceiver(mBtStatusChangedBroadcastReceiver);
        }
    }

    private void unregisterBtBroadcastReceiver() {
        if (mBtBroadcastReceiver != null) {
            unregisterReceiver(mBtBroadcastReceiver);
        }
    }

    private void checkData(Intent intent) {
        if (intent != null) {
            if (mUseCustomDsp) {
                mMode = intent.getIntExtra(CustomDspManager.EXTRA_INPUT_SEL, CustomDspManager.INPUT_01);
                LogUtils.info(TAG, "mUseCustomDsp mode = " + mMode);
                switch (mMode) {
                    case CustomDspManager.INPUT_02: // BT
                        switch2Bt();
                        break;

                    case CustomDspManager.INPUT_03: // LINE IN
                        switch2LineIn();
                        break;

                    case CustomDspManager.INPUT_01: // Android
                    default:
                        switch2Android();
                        break;
                }
            } else {
                mMode = intent.getIntExtra(CustomAudioManager.EXTRA_INPUT_SEL, CustomAudioManager.SEL_INPUT_DEF);
                LogUtils.info(TAG, " mode = " + mMode);
                switch (mMode) {
                    case CustomAudioManager.SEL_INPUT_2: // BT
                        switch2Bt();
                        break;

                    case CustomAudioManager.SEL_INPUT_3: // LINE IN
                        if (!hasLineIn)
                            switch2Android();
                        else
                            switch2LineIn();
                        break;

                    case CustomAudioManager.SEL_INPUT_1: // Android
                    default:
                        switch2Android();
                        break;
                }
            }
        }
    }

    private void switch2Bt() {
        ViewUtils.setViewShown(true, mTitleText);
        mTitleText.setText(R.string.sel_input_2);

        // linein
        stopVibrate();
        ViewUtils.setViewShown(false, mLineInView);
        if (!hasLineIn)
            ViewUtils.setViewShown(false, mLineInButton);
        else
            ViewUtils.setViewShown(true, mLineInButton);

        // 蓝牙
        ViewUtils.setViewShown(true, mBtImage);
        ViewUtils.setViewShown(true, mPromptsText);
        ViewUtils.setViewShown(false, mBtModeButton);
        ViewUtils.setViewShown(true, tvBtTitle);
        ViewUtils.setViewShown(true, startTime);
        ViewUtils.setViewShown(true, endTime);
        ViewUtils.setViewShown(true, mNumberProgressBar);
        ViewUtils.setViewShown(false, mCdImage);

        mNumberProgressBar.setMaxProgress(100);
        mBtImage.clearAnimation();
        enableBt();
//        closBtText();
    }

    private void switch2LineIn() {
        ViewUtils.setViewShown(true, mTitleText);
        mTitleText.setText(R.string.sel_input_3);

        // 隐藏蓝牙
        mBtImage.clearAnimation();
        ViewUtils.setViewShown(false, mBtImage);
        ViewUtils.setViewShownInvisible(false, mPromptsText);
        ViewUtils.setViewShown(false, mPrevButton);
        ViewUtils.setViewShown(false, mNextButton);
        ViewUtils.setViewShown(false, mPlayPauseButton);
        ViewUtils.setViewShown(false, mVolumeUpButton);
        ViewUtils.setViewShown(false, mVolumeDownButton);
        ViewUtils.setViewShown(false, mVolumeDownButton);
        ViewUtils.setViewShown(!SystemPropertiesUtils.getBoolean(CommonConstants.PROPERTY_BOOT_MODEL_LINE_IN, false), mBtModeButton);
        ViewUtils.setViewShownInvisible(false, progressbarLl);
        ViewUtils.setViewShown(false, tvBtTitle);
        ViewUtils.setViewShown(false, startTime);
        ViewUtils.setViewShown(false, endTime);
        ViewUtils.setViewShown(false, mNumberProgressBar);
        ViewUtils.setViewShown(false, mLyrics);
        ViewUtils.setViewShown(false, mLyricsControl);
        ViewUtils.setViewShown(false, mCdImage);

        tvBtTitle.setText("");
        disableBt();
        lyricsVisible = false;
        closBtText();
        // 显示linein相关
        ViewUtils.setViewShown(true, mLineInView);
        ViewUtils.setViewShown(false, mLineInButton);
        startVibrate();

        //蒂索纳
        if (Build.MODEL.contains(Constants.MODEL_DISUONA)) {
            ViewUtils.setViewShown(false, mLineInView);
            ViewUtils.setViewShown(true, mCdImage);
            ViewUtils.setViewShown(true, mBtImage);
            mTitleText.setText("唱片模式");
            mBtImage.setBackgroundResource(R.drawable.image_input_sel_linein);
//            ViewUtils.setViewShown(true, mBtImage);
//            mBtImage.startAnimation(mRotateAnimation);
        }

        if (mLyricsListView != null)
            mLyricsListView.clearAllData();
    }

    private void switch2Android() {
        // 释放linein
        stopVibrate();

        // 释放蓝牙
        disableBt();

        // 显示Android
        finish();
    }

    @SuppressLint("WrongConstant")
    @OnClick({R.id.iv_android_mode, R.id.iv_bt_mode, R.id.iv_linein_mode, R.id.is_lyrics})
    public void changeMode(View view) {
        int sel = -1;
        switch (view.getId()) {
            case R.id.iv_android_mode:
                if (mUseCustomDsp) {
                    sel = CustomDspManager.INPUT_01;
                } else {
                    sel = CustomAudioManager.SEL_INPUT_1;
                }
                break;

            case R.id.iv_bt_mode:
                if (mUseCustomDsp) {
                    sel = CustomDspManager.INPUT_02;
                } else {
                    sel = CustomAudioManager.SEL_INPUT_2;
                }
                break;

            case R.id.iv_linein_mode:
                if (mUseCustomDsp) {
                    sel = CustomDspManager.INPUT_03;
                } else {
                    sel = CustomAudioManager.SEL_INPUT_3;
                }
                break;
            case R.id.is_lyrics:
                if (lyricsVisible) {
                    closBtText();
                    mLyricsImg.setImageResource(R.drawable.ic_no_lyrics);
                    lyricsVisible = false;
                } else {
                    mLyricsImg.setImageResource(R.drawable.ic_lyrics);
                    visibleBtText();
                }
                lyricsBt();
                break;
        }

        if (sel == -1) {
            return;
        }

        changeMode(sel);
    }

    /**
     * 切换模式
     *
     * @param sel 模式
     *            <p>
     *            Created by lancy on 2018/10/27 12:25
     */
    private void changeMode(int sel) {
        if (mUseCustomDsp) {
            if (getCustomDspManager() != null) {
                getCustomDspManager().setInput((byte) sel);
            }
        } else {
            if (getCustomAudioManager() != null) {
                LogUtils.error(TAG,"mUseCustomDsp:1:"+mUseCustomDsp);
                LogUtils.error(TAG,"mUseCustomDsp:sel:"+sel);
                getCustomAudioManager().setInputSelector(sel);
            }
        }
    }

    @SuppressLint("WrongConstant")
    private CustomAudioManager getCustomAudioManager() {
        if (mCustomAudioManager == null) {
            mCustomAudioManager = (CustomAudioManager) getSystemService("custom_audio");
        }
        return mCustomAudioManager;
    }

    @SuppressLint("WrongConstant")
    private CustomDspManager getCustomDspManager() {
        if (mCustomDspManager != null) {
            mCustomDspManager = (CustomDspManager) getSystemService("custom_dsp");
        }
        return mCustomDspManager;
    }


    /**
     * 蓝牙歌词
     * <p>
     * Created by lancy on 2018/10/24 11:34
     */
    private void lyricsBt() {
        if (mUseCustomBluetooth && mBluetoothManager != null) {
            if (lyricsVisible) {
                mBluetoothManager.sendOperateCmd(CustomBluetoothManager.CMD_LYRICS_ON);
            } else {
                mBluetoothManager.sendOperateCmd(CustomBluetoothManager.CMD_LYRICS_OFF);
            }
        }
    }

    /**
     * 唤醒蓝牙
     * <p>
     * Created by lancy on 2018/10/24 11:34
     */
    private void enableBt() {
        if (mUseCustomBluetooth && mBluetoothManager != null) {
            mBluetoothManager.sendOperateCmd(CustomBluetoothManager.CMD_OWKUP);
            mBluetoothManager.sendOperateCmd(CustomBluetoothManager.CMD_LYRICS_ON);
        }
    }

    /**
     * 待机蓝牙
     * <p>
     * Created by lancy on 2018/10/24 11:35
     */
    private void disableBt() {
        if (mUseCustomBluetooth && mBluetoothManager != null) {
            mBluetoothManager.sendOperateCmd(CustomBluetoothManager.CMD_OPOFF);
            closBtText();
        }
    }

    private void closBtText() {
        if (SystemPropertiesUtils.getBoolean(Constants.PERSIST_SYS_BT_LYRICS, true)){
            LogUtils.error(TAG, "closBtText: lyricsVisible:::" +lyricsVisible);
            ViewUtils.setViewShownInvisible(false, progressbarLl);
            ViewUtils.setViewShown(false, tvBtTitle);
            ViewUtils.setViewShown(false, mLyricsControl);
            ViewUtils.setViewShown(true, mTitleText);
            ViewUtils.setViewShown(lyricsVisible, mLyrics);
//            mTitleText.setText(isBtMode() ? getString(R.string.sel_input_2) : getString(R.string.sel_input_3));
        }
        /*
        if (mMode == 1) {
            mTitleText.setText(getString(R.string.sel_input_2));
        } else if (mMode == 2) {
            mTitleText.setText(getString(R.string.sel_input_3));
            ViewUtils.setViewShown(false, mLyrics);
        }*/
    }


    private void visibleBtText() {
        if (SystemPropertiesUtils.getBoolean(Constants.PERSIST_SYS_BT_LYRICS, true) && !Build.MODEL.contains("T365") && !Build.BRAND.contains("T365")) {
            mBluetoothManager.sendOperateCmd(CustomBluetoothManager.CMD_LYRICS_ON);
            mLyricsImg.setImageResource(R.drawable.ic_lyrics);
            LogUtils.error(TAG, "visibleBtText:");
            ViewUtils.setViewShown(true, mLyrics);
            mLyrics.setEnabled(true);
            lyricsVisible = true;
            ViewUtils.setViewShownInvisible(true, progressbarLl);
            ViewUtils.setViewShown(true, tvBtTitle);
            ViewUtils.setViewShown(true, mLyricsControl);
            tvBtTitle.requestFocus();
        }
    }

    /**
     * 是否蓝牙模式
     *
     * @return 蓝牙模式返回true, 否则返回false
     * <p>
     * Created by lancy on 2018/10/24 11:41
     */
    private boolean isBtMode() {
        if (mUseCustomDsp) {
            return mMode == CustomDspManager.INPUT_02;
        } else {
            return mMode == CustomAudioManager.SEL_INPUT_2;
        }
    }

    /**
     * 上一曲
     * <p>
     * Created by lancy on 2018/10/24 11:37
     */
    @OnClick(R.id.iv_prev)
    public void playPrev() {
        if (isBtMode() && mUseCustomBluetooth && mBluetoothManager != null) {
            mBluetoothManager.sendOperateCmd(CustomBluetoothManager.CMD_OPREV);
        }
    }

    /**
     * 下一曲
     * <p>
     * Created by lancy on 2018/10/24 11:38
     */
    @OnClick(R.id.iv_next)
    public void playNext() {
        if (isBtMode() && mUseCustomBluetooth && mBluetoothManager != null) {
            mBluetoothManager.sendOperateCmd(CustomBluetoothManager.CMD_ONEXT);
        }
    }

    /**
     * 播放/暂停
     * <p>
     * Created by lancy on 2018/10/24 11:38
     */
    @OnClick(R.id.iv_play_pause)
    public void playOrPause() {
        if (isBtMode() && mUseCustomBluetooth && mBluetoothManager != null) {
            mBluetoothManager.sendOperateCmd(CustomBluetoothManager.CMD_OPLPA);
        }
    }

    /**
     * 增大音量
     * <p>
     * Created by lancy on 2018/10/24 18:54
     */
    @OnClick({R.id.iv_volume_up})
    public void adjustVolumeUp() {
        if (isBtMode() && mUseCustomBluetooth && mBluetoothManager != null) {
            mBluetoothManager.sendOperateCmd(CustomBluetoothManager.CMD_OVOLU);
        }
    }

    /**
     * 减小音量
     * <p>
     * Created by lancy on 2018/10/24 18:54
     */
    @OnClick({R.id.iv_volume_down})
    public void adjustVolumeDown() {
        if (isBtMode() && mUseCustomBluetooth && mBluetoothManager != null) {
            mBluetoothManager.sendOperateCmd(CustomBluetoothManager.CMD_OVOLD);
        }
    }

    /**
     * 刷新蓝牙显示
     *
     * @param status 蓝牙状态
     *               <p>
     *               Created by lancy on 2018/10/24 14:23
     */
    private void refreshBt(int status) {
        if (!mUseSQDSP) {
            switch (status) {
                case CustomBluetoothManager.STATUS_POFF:
                    CustomToast.makeText(mContext, R.string.msg_bt_status_poff).show();
                    lyricsVisible = false;
                    closBtText();
                    break;
                case CustomBluetoothManager.STATUS_DCNT:
                    CustomToast.makeText(mContext, R.string.msg_bt_status_dcnt).show();
                    lyricsVisible = false;
                    closBtText();
                    break;
                case CustomBluetoothManager.STATUS_CNCT:
                    if (lastStatus != CustomBluetoothManager.STATUS_MUSC) {
                        CustomToast.makeText(mContext, R.string.msg_bt_status_cnct, CustomToast.MessageType.INFO).show();
                    }
                    break;
                case CustomBluetoothManager.STATUS_MUSC:
                    CustomToast.makeText(mContext, R.string.msg_bt_status_music, CustomToast.MessageType.INFO).show();
                    break;
                case CustomBluetoothManager.STATUS_UNKNOW:
                default:
                    break;
            }
            if (status == CustomBluetoothManager.STATUS_MUSC) {
//            mBtImage.startAnimation(mRotateAnimation);
                mPlayPauseButton.setImageResource(R.drawable.ic_pause);
            } else {
                mPlayPauseButton.setImageResource(R.drawable.ic_play);
//            mBtImage.clearAnimation();
            }

            if (status >= CustomBluetoothManager.STATUS_CNCT) {
                ViewUtils.setViewShown(true, mPrevButton);
                ViewUtils.setViewShown(true, mNextButton);
                ViewUtils.setViewShown(true, mPlayPauseButton);
                ViewUtils.setViewShown(true, mVolumeUpButton);
                ViewUtils.setViewShown(true, mVolumeDownButton);
                mPlayPauseButton.requestFocus();
            } else {
                ViewUtils.setViewShown(false, mPrevButton);
                ViewUtils.setViewShown(false, mNextButton);
                ViewUtils.setViewShown(false, mPlayPauseButton);
                ViewUtils.setViewShown(false, mVolumeUpButton);
                ViewUtils.setViewShown(false, mVolumeDownButton);
            }
        } else {
            mPlayPauseButton.setImageResource(R.drawable.ic_play_pause);
            ViewUtils.setViewShown(true, mPrevButton);
            ViewUtils.setViewShown(true, mNextButton);
            ViewUtils.setViewShown(true, mPlayPauseButton);
            ViewUtils.setViewShown(true, mVolumeUpButton);
            ViewUtils.setViewShown(true, mVolumeDownButton);
            mPlayPauseButton.requestFocus();
        }
        lastStatus = status;
    }

    /**
     * 蓝牙状态接收者
     * <p>
     * Created by lancy on 2018/10/24 11:51
     */
    private final class BtStatusChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals(CustomBluetoothManager.ACTION_STATUS_CHANGED)) {
                int status = intent.getIntExtra(CustomBluetoothManager.STATUS, CustomBluetoothManager.STATUS_UNKNOW);
                refreshBt(status);
            }
        }
    }

    /**
     * 蓝牙歌曲内容广播
     */
    private class registerBtBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra(CustomBluetoothManager.DATA);
            if (data == null) {
                return;
            }
            int state = intent.getIntExtra(CustomBluetoothManager.STATE, (byte) 100);
            new Thread(() -> {
                try {
                    Message message = Message.obtain();
                    message.what = state;
                    message.obj = data;
                    mHandle.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * 开始跳动
     * <p>
     * Created by lancy on 2018/10/24 11:50
     */
    private void startVibrate() {
        /*
        if (mVibrateDisposable != null && !mVibrateDisposable.isDisposed()) {
            return;
        }

        mVibrateDisposable = Observable
                .interval(300, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> mLineInView.vibrate(), throwable -> {
                    LogUtils.error(TAG, "failed to vibrate, error message : " + throwable.getMessage());
                    throwable.printStackTrace();
                });
                */
    }

    /**
     * 停止跳动
     */
    private void stopVibrate() {
        /*
        if (mVibrateDisposable != null && !mVibrateDisposable.isDisposed()) {
            mVibrateDisposable.dispose();
        }
        */
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.verbose(TAG, "onDestroy()......");
        ViewUtils.setViewShown(false, mLyrics);
        stopVibrate();
        unregisterBroadcastReceiver();
        unregisterBtBroadcastReceiver();
    }

    @Override
    public void onBackPressed() {
        LogUtils.error(TAG,"mUseCustomDsp::"+mUseCustomDsp);
        if (mUseCustomDsp) {
            changeMode(CustomDspManager.INPUT_01);
        } else {
            changeMode(CustomAudioManager.SEL_INPUT_1);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                playPrev();
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                playNext();
                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                playOrPause();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
