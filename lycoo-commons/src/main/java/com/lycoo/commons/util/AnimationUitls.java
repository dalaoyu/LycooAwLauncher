package com.lycoo.commons.util;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

public class AnimationUitls {

    /**
     * alpha animation
     *
     * Created by lancy on 2017/6/8 18:15
     */
    public static void setAlphaAnimation(View view, boolean show) {
        if (show) {
            Animation alphaAnimation = new AlphaAnimation(1, 0.3f);
            alphaAnimation.setDuration(1500);
            alphaAnimation.setInterpolator(new LinearInterpolator());
            alphaAnimation.setRepeatCount(Animation.INFINITE);
            alphaAnimation.setRepeatMode(Animation.REVERSE);
            view.startAnimation(alphaAnimation);
        } else {
            view.clearAnimation();
        }
    }
}
