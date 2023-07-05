package com.lycoo.desktop.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lycoo.commons.util.LogUtils;
import com.lycoo.desktop.R;

/**
 * 进度条选项
 *
 * Created by lancy on 2019/8/1
 */
public class SeekBarMenuItem extends MenuItem {
    private static final String TAG = SeekBarMenuItem.class.getSimpleName();

    private int mMin = 0;
    private int mMax = 100;
    private int mProgress;

    private SeekBar mSeekBar;
    private TextView mValueText;

    protected SeekBarMenuItem(String title) {
        super(Type.SEEK_BAR, title);
    }

    protected SeekBarMenuItem(String title, int icon) {
        super(Type.SEEK_BAR, title, icon);
    }

    /**
     * 获取进度
     *
     * @return 当前进度
     *
     * Created by lancy on 2019/8/1 19:08
     */
    public int getCurrentProgress() {
        return mProgress;
    }

    /**
     * 设置进度
     *
     * @param progress 进度
     *
     *                 Created by lancy on 2019/8/1 19:08
     */
    public SeekBarMenuItem setCurrentProgress(int progress) {
        return setCurrentProgress(progress, false);
    }

    /**
     * 设置进度
     *
     * @param progress 进度
     * @param notify   是否通知更新
     *
     *                 Created by lancy on 2019/8/1 19:08
     */
    public SeekBarMenuItem setCurrentProgress(int progress, boolean notify) {
        this.mProgress = progress;
        if (notify) {
            notifyValueChange(progress);
        }
        updateContent();
        return this;
    }

    /**
     * 设置边界
     *
     * @param min 最小值
     * @param max 最大值
     *
     *            Created by lancy on 2019/8/1 19:07
     */
    public SeekBarMenuItem setBoundary(int min, int max) {
        this.mMin = min;
        this.mMax = max;
        return this;
    }

    @Override
    protected void onBindView(LayoutInflater inflater, ViewGroup container, boolean inflate) {
        if (inflate) {
            inflater.inflate(R.layout.menu_item_seekbar, container);
        }
        mSeekBar = container.findViewById(R.id.seekbar);
        mValueText = container.findViewById(R.id.tv_value);
        updateContent();
    }

    @Override
    protected void onUnbindView() {
        super.onUnbindView();
        mSeekBar = null;
    }

    private void updateContent() {
        if (mSeekBar == null) {
            return;
        }
        mSeekBar.setOnSeekBarChangeListener(null);
        mSeekBar.setMax(mMax - mMin);
        mSeekBar.setProgress(mProgress - mMin);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LogUtils.debug(TAG, "onProgressChanged, progress = " + progress);
                mProgress = progress + mMin;
                mValueText.setText(String.valueOf(mProgress));
                notifyValueChange(mProgress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mValueText.setText(String.valueOf(mProgress));
    }

    @Override
    public void increase() {
        mProgress++;
        if (mProgress > mMax) {
            mProgress = mMax;
        }
        mSeekBar.setProgress(mProgress - mMin);
    }

    @Override
    public void decrease() {
        mProgress--;
        if (mProgress < mMin) {
            mProgress = mMin;
        }
        mSeekBar.setProgress(mProgress - mMin);
    }
}
