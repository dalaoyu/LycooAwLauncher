package com.lycoo.commons.screensaver;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * 屏保
 * 使用屏保的界面请继承该Activity
 *
 * Created by lancy on 2018/1/4
 */
public class ScreensaverActivity extends Activity {
    private static final String TAG = ScreensaverActivity.class.getSimpleName();

    private Context mContext = this;

    @Override
    protected void onResume() {
        super.onResume();
        ScreensaverManager.getInstance(mContext).restartCount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScreensaverManager.getInstance(mContext).stopCount();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN){
//            LogUtils.verbose(TAG, "dispatchKeyEvent()......");
            ScreensaverManager.getInstance(mContext).restartCount();
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
//            LogUtils.verbose(TAG, "dispatchTouchEvent()......");
            ScreensaverManager.getInstance(mContext).restartCount();
        }

        return super.dispatchTouchEvent(ev);
    }
}
