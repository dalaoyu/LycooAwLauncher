package com.lycoo.commons.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.lycoo.commons.util.LogUtils;

/**
 * 自定义ImageView， 加速内存回收
 *
 * Created by lancy on 2017/6/29
 */
@SuppressLint("AppCompatCustomView")
public class CustomImageView extends ImageView {

    private static final String TAG = CustomImageView.class.getSimpleName();

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        LogUtils.info(TAG, "CustomImageView->onDetachedFromWindow......");
        // 在它从屏幕中消失时回调，去掉drawable引用，能加快内存的回收
        setImageDrawable(null);
    }


//    @Override
//    public void onVisibilityAggregated(boolean isVisible) {
//        super.onVisibilityAggregated(isVisible);
//        // 7.0 以后用
//        LogUtils.info(TAG, "CustomImageView->onVisibilityAggregated......");
//        if (!isVisible) {
//            setImageDrawable(null);
//        }
//    }
}
