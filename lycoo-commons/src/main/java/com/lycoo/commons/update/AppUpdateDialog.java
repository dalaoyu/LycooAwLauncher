package com.lycoo.commons.update;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lycoo.commons.R;
import com.lycoo.commons.base.BaseDialog;
import com.lycoo.commons.entity.AppUpdate;
import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.http.DownloadCallBack;
import com.lycoo.commons.http.DownloadManager;
import com.lycoo.commons.http.DownloadTask;
import com.lycoo.commons.util.AnimationUitls;
import com.lycoo.commons.util.ApplicationUtils;
import com.lycoo.commons.util.FileUtils;
import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.util.MD5Utils;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.view.NumberProgressBar;
import com.lycoo.commons.widget.CustomToast;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 应用升级对话框
 *
 * Created by lancy on 2018/1/5
 */
public class AppUpdateDialog extends BaseDialog {

    private static final String TAG = AppUpdateDialog.class.getSimpleName();
    private static final String DOWNLOAD_SUFFIX = ".apk";

    private LinearLayout ll_opratorContainer;
    private LinearLayout ll_downloadContainer;
    private Button btn_ok;
    private TextView tv_tips;
    private NumberProgressBar mProgressBar;

    private Context mContext;
    private AppUpdate mAppUpdate;
    private CompositeDisposable mCompositeDisposable;

    public AppUpdateDialog(@NonNull Context context, int theme, AppUpdate appUpdate) {
        super(context, theme);
        this.mContext = context;
        this.mAppUpdate = appUpdate;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_app_update);

        setupDialog();
        initView();
    }

    private void setupDialog() {
        // 1. 对话框尺寸和位置设置
        Window window = this.getWindow();
        // 保证dialog的宽高和设置的一致
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.START;
        layoutParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.c_app_update_dialog_width);
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setBackgroundDrawableResource(R.drawable.common_bg_app_update_dialog);
        window.setAttributes(layoutParams);

        // 2. 对话框特殊属性设置
        // 屏蔽返回键和点击空白处dismiss掉Dialog
        setCancelable(false);
    }

    /**
     * 初始化控件
     *
     * Created by lancy on 2018/1/6 11:11
     */
    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_version = findViewById(R.id.tv_version);
        TextView tv_majorUpdate = findViewById(R.id.tv_major_update);
        tv_title.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        tv_version.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        tv_majorUpdate.setTypeface(StyleManager.getInstance(mContext).getTypeface());

        ll_opratorContainer = findViewById(R.id.ll_operate_container);
        ll_downloadContainer = findViewById(R.id.ll_download_container);
        tv_tips = findViewById(R.id.tv_tips);
        tv_tips.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        mProgressBar = findViewById(R.id.number_pb);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> dismiss());
        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(v -> {
            ViewUtils.setViewShown(true, ll_downloadContainer);
            ViewUtils.setViewShown(false, ll_opratorContainer);
            downloadApp();
        });
        btn_ok.requestFocus();
        btn_ok.setTypeface(StyleManager.getInstance(mContext).getTypeface());
        btn_cancel.setTypeface(StyleManager.getInstance(mContext).getTypeface());

        tv_version.setText(mAppUpdate.getVersion().getName());
        AnimationUitls.setAlphaAnimation(tv_version, true);

        tv_majorUpdate.setText(mAppUpdate.getVersion().getMajorUpdate());
    }

    /**
     * 下载应用
     *
     * Created by lancy on 2018/1/6 11:11
     */
    private void downloadApp() {
        DownloadManager
                .getInstance(mContext)
                .download(new DownloadTask(
                        mAppUpdate.getUrl(),
                        new File(Environment.getExternalStorageDirectory(), mAppUpdate.getName().endsWith(DOWNLOAD_SUFFIX)
                                ? mAppUpdate.getName()
                                : mAppUpdate.getName() + DOWNLOAD_SUFFIX),
                        null,
                        null,
                        null,
                        false,
                        new DownloadCallBack<File>() {
                            @Override
                            public void onCheck(int code) {
                                switch (code) {
                                    case DownloadManager.DOWNLOAD_FAILED_REPEAT_TASK:
                                        CustomToast
                                                .makeText(mContext, R.string.c_msg_download_failed_repeat_task, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                                .show();
                                        break;
                                    case DownloadManager.DOWNLOAD_FAILED_NETWORK_UNCONNECTED:
                                        CustomToast
                                                .makeText(mContext, R.string.c_msg_network_unconnected, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                                .show();
                                        break;
                                }
                            }

                            @Override
                            public void onStart() {
                                super.onStart();
                                tv_tips.setText(R.string.c_msg_downloading);
                            }

                            @Override
                            public void onProgress(int progress) {
                                LogUtils.debug(TAG, "progress = " + progress + "%");
                                mProgressBar.setProgress(progress);
                            }

                            @Override
                            public void onError(int errorCode, Throwable t) {
                                super.onError(errorCode, t);
                                LogUtils.error(TAG, "failed to download app , error message : " + t.getMessage());
                                t.printStackTrace();

                                // 如果已经有文件，则删除
                                File file = new File(Environment.getExternalStorageDirectory(), mAppUpdate.getName().endsWith(DOWNLOAD_SUFFIX)
                                        ? mAppUpdate.getName()
                                        : mAppUpdate.getName() + DOWNLOAD_SUFFIX);
                                if (file.exists()) {
                                    boolean deleted = file.delete();
                                    LogUtils.debug(TAG, "onError(), deleted = " + deleted);
                                }
                                switchToOperateView();
                            }

                            @Override
                            public void onSuccess(File file) {
                                super.onSuccess(file);
                                LogUtils.info(TAG, "download success : file = " + file.getPath());
                                installApp(file);
                            }
                        }
                ));
    }

    /**
     * 安装应用
     *
     * @param file 安装包
     *
     *             Created by lancy on 2018/1/6 11:12
     */
    private void installApp(final File file) {
        tv_tips.setText(R.string.c_msg_verifying);
        mCompositeDisposable.add(
                Observable
                        .create((ObservableOnSubscribe<Boolean>) emitter -> {
                            boolean result = MD5Utils.checkMd5(mAppUpdate.getMd5(), file);
                            if (!result) {
                                LogUtils.error(TAG, file + " checkMd5 failed......");
                                FileUtils.deleteFile(file);
                            }
                            emitter.onNext(result);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result) {
                                ApplicationUtils.installApp(mContext, Uri.fromFile(file));
                                dismiss();
                            } else {
                                tv_tips.setText(R.string.c_msg_verify_error);
                                CustomToast
                                        .makeText(mContext, R.string.c_msg_verify_error, Toast.LENGTH_SHORT, CustomToast.MessageType.ERROR)
                                        .show();
                                switchToOperateView();
                            }
                        }));
    }


    /**
     * 显示操作控件
     *
     * Created by lancy on 2018/1/6 11:13
     */
    private void switchToOperateView() {
        ViewUtils.setViewShown(false, ll_downloadContainer);
        ViewUtils.setViewShown(true, ll_opratorContainer);
        btn_ok.requestFocus();
    }

    @Override
    public void dismiss() {
        super.dismiss();

        if (mCompositeDisposable != null && mCompositeDisposable.size() > 0) {
            mCompositeDisposable.clear();
        }
    }
}
