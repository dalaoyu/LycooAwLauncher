package com.lycoo.lancy.launcher.activity;

import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lycoo.commons.util.LogUtils;
import com.lycoo.commons.view.SpectrumView;
import com.lycoo.lancy.launcher.R;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.internal.disposables.EmptyDisposable;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = TestActivity.class.getSimpleName();

    private RelativeLayout mRoot;
    private SpectrumView mSpectrumView;

    @BindView(R.id.iv_image) ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindow();
        mRoot = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.activity_test, null);
        setContentView(mRoot);
        ButterKnife.bind(this);

        initView();
//        startVibrate();asdf
        printScreenParams();
        File path = new File(Environment.getRootDirectory().getPath()+"/media/welcome.mp4");
        LogUtils.error(TAG,"path.exists()::"+path.exists());
    }

    private void setupWindow() {
        // 第一阶段 ======================================================================================
        /*
         * 11
         * statusBar    ： 显示(statusBar覆盖View)
         * navigationBar： 显示(navigationBar未覆盖)
         */
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        );

        /*
         * 12
         * statusBar    ： 隐藏（View被拉伸）
         * navigationBar： 显示(navigationBar未覆盖)
         */
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_FULLSCREEN
//        );

        /*
         * 13
         * statusBar    ： 显示(statusBar覆盖View)
         * navigationBar： 显示(navigationBar覆盖View)
         */
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        );

        /*
         * 14
         * statusBar    ： 显示(statusBar未覆盖)
         * navigationBar： 隐藏(View被拉伸)
         */
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//        );

        // 第二阶段 ======================================================================================
        /*
         * 21
         * statusBar    ： 显示(statusBar覆盖View)
         * navigationBar： 显示(navigationBar未覆盖)
         */
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        );

        /*
         * 22
         * statusBar    ： 隐藏（View未被拉伸，statusBar区域空白 ）
         * navigationBar： 显示(navigationBar未覆盖)
         */
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//        );

        /*
         * 23
         * statusBar    ： 显示(statusBar覆盖View)
         * navigationBar： 显示(navigationBar覆盖View)
         */
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );

        /*
         * 24
         * statusBar    ： 显示(statusBar未覆盖)
         * navigationBar： 隐藏(View未被拉伸，navigationBar区域空白)
         */
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        // 第三阶段 ======================================================================================
        /*
         * 31
         * statusBar    ： 隐藏(弹出->statusBar覆盖View)
         * navigationBar： 显示(navigationBar未覆盖View)
         *
         * View.SYSTEM_UI_FLAG_LAYOUT_STABLE: 无影响
         */
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        /*
         * 32
         * statusBar    ： 显示(statusBar覆盖View)
         * navigationBar： 隐藏(弹出->navigationBar覆盖View)
         *
         * View.SYSTEM_UI_FLAG_LAYOUT_STABLE: 无影响
         */
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        /*
         * 33
         * statusBar    ： 隐藏(弹出->statusBar覆盖View)
         * navigationBar： 隐藏(弹出->navigationBar覆盖View)
         *
         * View.SYSTEM_UI_FLAG_LAYOUT_STABLE: 无影响
         */
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        // 第四阶段 ======================================================================================
        /**
         * View.SYSTEM_UI_FLAG_IMMERSIVE:
         * 这个flag只有当设置了SYSTEM_UI_FLAG_HIDE_NAVIGATION才起作用。
         * 如果没有设置这个flag，任意的View相互动作都退出SYSTEM_UI_FLAG_HIDE_NAVIGATION模式。如果设置就不会退出。
         */

        /**
         * View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY:
         * 这个flag只有当设置了SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION 时才起作用。如果没有设置这个flag，
         * 任意的View相互动作都坏退出SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION模式。如果设置就不受影响。
         */

        /*
         * 41
         * statusBar    ： 隐藏(弹出->statusBar覆盖View    ，颜色为黑色， 不会自动消失， 触摸->不会弹出)
         * navigationBar： 隐藏(弹出->navigationBar覆盖View, 颜色为黑色， 不会自动消失， 触摸->不会弹出)
         *
         * View.SYSTEM_UI_FLAG_LAYOUT_STABLE: 无影响
         */
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );

        /*
         * 42
         * statusBar    ： 隐藏(弹出->statusBar覆盖View    ，颜色为半透明， 会自动消失， 触摸->不会弹出)
         * navigationBar： 隐藏(弹出->navigationBar覆盖View, 颜色为半透明， 会自动消失， 触摸->不会弹出)
         *
         *
         * View.SYSTEM_UI_FLAG_LAYOUT_STABLE: 无影响
         * View.SYSTEM_UI_FLAG_IMMERSIVE    ：
         */
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

    }

    private void setupWindow2() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void initView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                100
        );
        mSpectrumView = new SpectrumView(this);
        mSpectrumView.setLayoutParams(params);
        mRoot.addView(mSpectrumView);
    }

    private void printScreenParams() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        LogUtils.debug(TAG, "widthPixels = " + widthPixels + ", heightPixels = " + heightPixels);
    }

    private void startVibrate() {
        Observable
                .interval(300, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> mSpectrumView.vibrate());
    }

    @OnClick(R.id.iv_image)
    public void showImageParam() {
        Toast.makeText(this, "width = " + mImageView.getWidth() + ", height = " + mImageView.getHeight(), Toast.LENGTH_SHORT).show();
        LogUtils.debug(TAG, "width = " + mImageView.getWidth() + ", height = " + mImageView.getHeight());
    }
}
