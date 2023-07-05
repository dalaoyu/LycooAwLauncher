/*
 * Copyright (C) 2016 hejunlin <hejunlin2013@gmail.com>
 * Github:https://github.com/hejunlin2013/TVSample
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lycoo.commons.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.RequiresApi;

/**
 * Created by hejunlin on 2015/10/19.
 * blog: http://blog.csdn.net/hejjunlin
 */
public class MetroViewBorderHandler<X extends View> implements
        ViewTreeObserver.OnGlobalFocusChangeListener,
        ViewTreeObserver.OnScrollChangedListener,
        ViewTreeObserver.OnGlobalLayoutListener,
        ViewTreeObserver.OnTouchModeChangeListener {

    private static final String TAG = MetroViewBorderHandler.class.getSimpleName();

    private ViewGroup mViewGroup;
    private IMetroViewBorder mMetroViewBorder;

    private X mView;
    private View mLastView;

    public MetroViewBorderHandler(Context context) {
        this(context, null, 0);
    }

    public MetroViewBorderHandler(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MetroViewBorderHandler(Context context, AttributeSet attrs, int defStyleAttr) {
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mMetroViewBorder = new MetroViewBorder();
        mView = (X) new View(context, attrs, defStyleAttr);
    }

    public MetroViewBorderHandler(X view) {
        this.mView = view;
        mMetroViewBorder = new MetroViewBorder();
    }

    public MetroViewBorderHandler(X view, IMetroViewBorder border) {
        this.mView = view;
        mMetroViewBorder = border;
    }

    public MetroViewBorderHandler(Context context, int resId) {
        this((X) LayoutInflater.from(context).inflate(resId, null, false));
    }

    public X getView() {
        return mView;
    }


    public void setBackgroundResource(int resId) {
        if (mView != null)
            mView.setBackgroundResource(resId);
    }

    @Override
    public void onScrollChanged() {
        mMetroViewBorder.onScrollChanged(mView, mViewGroup);
    }

    @Override
    public void onGlobalLayout() {
        mMetroViewBorder.onLayout(mView, mViewGroup);
    }

    @Override
    public void onTouchModeChanged(boolean isInTouchMode) {
        mMetroViewBorder.onTouchModeChanged(mView, mViewGroup, isInTouchMode);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
//        LogUtils.error(TAG, ">>>>>>>>>>>>>>>>> onGlobalFocusChanged(), oldFocus = " + oldFocus + ", newFocus = " + newFocus);
//        if (oldFocus != null)
//            LogUtils.info(TAG, " oldFocus x = " + oldFocus.getX() + ", y = " + newFocus.getY() + ", id = " + oldFocus.getId());
//        if (newFocus != null)
//            LogUtils.info(TAG, " newFocus x = " + newFocus.getX() + ", y = " + newFocus.getY() + ", id = " + newFocus.getId());
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (oldFocus == null && mLastView != null) {
                    oldFocus = mLastView;
                }
            }

            if (mMetroViewBorder != null) {
                mMetroViewBorder.onFocusChanged(mView, oldFocus, newFocus);
            }

            mLastView = newFocus;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public <T extends MetroViewBorder> T getViewBorder() {
        return (T) mMetroViewBorder;
    }

    public void setBorder(IMetroViewBorder border) {
        this.mMetroViewBorder = border;
    }

    public void attachTo(ViewGroup viewGroup) {
        try {
            if (viewGroup == null) {
                if (mView.getContext() instanceof Activity) {
                    Activity activity = (Activity) mView.getContext();
                    viewGroup = (ViewGroup) activity.getWindow().getDecorView().getRootView();
                }
            }

            if (mViewGroup != viewGroup) {
                ViewTreeObserver viewTreeObserver = viewGroup.getViewTreeObserver();
                if (viewTreeObserver.isAlive() && mViewGroup == null) {
                    viewTreeObserver.addOnGlobalFocusChangeListener(this);
                    viewTreeObserver.addOnScrollChangedListener(this);
                    viewTreeObserver.addOnGlobalLayoutListener(this);
                    viewTreeObserver.addOnTouchModeChangeListener(this);
                }
                mViewGroup = viewGroup;
            }

            mMetroViewBorder.onAttach(mView, mViewGroup);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void detach() {
        detachFrom(mViewGroup);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void detachFrom(ViewGroup viewGroup) {
        try {
            if (viewGroup == mViewGroup) {
                ViewTreeObserver viewTreeObserver = mViewGroup.getViewTreeObserver();
                viewTreeObserver.removeOnGlobalFocusChangeListener(this);
                viewTreeObserver.removeOnScrollChangedListener(this);
                viewTreeObserver.removeOnGlobalLayoutListener(this);
                viewTreeObserver.removeOnTouchModeChangeListener(this);
                mMetroViewBorder.OnDetach(mView, viewGroup);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setAnimationDuration(long duration){
        mMetroViewBorder.setDuration(duration);
    }
}
