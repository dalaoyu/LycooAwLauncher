package com.lycoo.desktop.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lycoo.commons.helper.StyleManager;
import com.lycoo.commons.util.ViewUtils;
import com.lycoo.commons.view.RingProgressBar;
import com.lycoo.desktop.R;


/**
 * 桌面坑位进度条
 *
 * Created by lancy on 2017/12/23
 */
public class DockItemProgressBar extends FrameLayout {

    RingProgressBar mDownloadProgressBar;
    ProgressBar mInstallProgressBar;
    TextView mStatusLabel;

    public enum Status {
        PREPAREING,
        CANCELED,
        START_DOWNLOAD,
        DOWNLOAD_COMPLETE,
        VERIFYING,
        INSTALLING
    }

    private Context mContext;

    public DockItemProgressBar(@NonNull Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        View view = LayoutInflater.from(mContext).inflate(R.layout.desktop_item_progress_bar, null);
        mDownloadProgressBar = view.findViewById(R.id.pb_download);
        mInstallProgressBar = view.findViewById(R.id.pb_install);
        mStatusLabel = view.findViewById(R.id.tv_status);
        view.setLayoutParams(params);
        addView(view);

        Typeface tf = StyleManager.getInstance(mContext).getTypeface();
        mStatusLabel.setTypeface(tf);
    }

    public ProgressBar getInstallProgressBar() {
        return mInstallProgressBar;
    }

    public RingProgressBar getDownloadProgressBar() {
        return mDownloadProgressBar;
    }

    public void updateStatus(Status status) {
        switch (status) {
            case PREPAREING:
                mStatusLabel.setText(R.string.status_prepareing);
                ViewUtils.setViewShown(true, mInstallProgressBar);
                break;

            case CANCELED:
                mStatusLabel.setText(R.string.status_canceled);
                break;

            case START_DOWNLOAD:
                mStatusLabel.setText(R.string.status_downloading);
                ViewUtils.setViewShown(false, mInstallProgressBar);
                ViewUtils.setViewShown(true, mDownloadProgressBar);
                break;

            case DOWNLOAD_COMPLETE:
                ViewUtils.setViewShown(false, mDownloadProgressBar);
                break;

            case VERIFYING:
                mStatusLabel.setText(R.string.status_verifying);
                ViewUtils.setViewShown(true, mInstallProgressBar);
                break;

            case INSTALLING:
                mStatusLabel.setText(R.string.status_installing);
                ViewUtils.setViewShown(true, mInstallProgressBar);
                break;
        }
    }

    public void updateProgress(int progress) {
        mDownloadProgressBar.setProgress(progress);
    }

}
