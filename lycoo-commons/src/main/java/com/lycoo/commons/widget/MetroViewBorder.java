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

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;

import androidx.recyclerview.widget.RecyclerView;

import com.lycoo.commons.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejunlin on 2015/10/19.
 * blog: http://blog.csdn.net/hejjunlin
 */
public class MetroViewBorder implements IMetroViewBorder {

    private String TAG = MetroViewBorder.class.getSimpleName();
    private boolean mScalable = false;
    private float mScale = 1.1f;
    private long mDurationTraslate = 800;
    private int mMargin = 0;
    private View lastFocus, oldLastFocus;
    private AnimatorSet mAnimatorSet;
    private List<Animator> mAnimatorList = new ArrayList<>();
    private View mTarget;
    private boolean mEnableTouch = true;

    public MetroViewBorder() {
        mFocusListener.add(mFocusMoveListener);
        mFocusListener.add(mFocusScaleListener);
        mFocusListener.add(mFocusPlayListener);
        mFocusListener.add(mAbsListViewFocusListener);
    }

    public interface FocusListener {
        void onFocusChanged(View oldFocus, View newFocus);
    }

    protected List<FocusListener> mFocusListener = new ArrayList<>(1);
    protected List<Animator.AnimatorListener> mAnimatorListener = new ArrayList<>(1);

    public FocusListener mFocusScaleListener = new FocusListener() {
        @Override
        public void onFocusChanged(View oldFocus, View newFocus) {
            mAnimatorList.addAll(getScaleAnimator(newFocus, true));
            if (oldFocus != null) {
                mAnimatorList.addAll(getScaleAnimator(oldFocus, false));
            }
        }
    };

    public FocusListener mFocusPlayListener = new FocusListener() {
        @Override
        public void onFocusChanged(View oldFocus, View newFocus) {
            try {
                if (newFocus instanceof AbsListView) {
                    return;
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setInterpolator(new DecelerateInterpolator(1));
                animatorSet.setDuration(mDurationTraslate);
                animatorSet.playTogether(mAnimatorList);
                for (Animator.AnimatorListener listener : mAnimatorListener) {
                    animatorSet.addListener(listener);
                }
                mAnimatorSet = animatorSet;
                if (oldFocus == null) {
                    animatorSet.setDuration(0);
                    mTarget.setVisibility(View.VISIBLE);
                }
                animatorSet.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    public FocusListener mFocusMoveListener = new FocusListener() {
        @Override
        public void onFocusChanged(View oldFocus, View newFocus) {
            if (newFocus == null) return;
            try {
                mAnimatorList.addAll(getMoveAnimator(newFocus, 0, 0));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    public FocusListener mAbsListViewFocusListener = new FocusListener() {

        @Override
        public void onFocusChanged(View oldFocus, View newFocus) {
            try {
                if (oldFocus == null) {
                    for (int i = 0; i < attacheViews.size(); i++) {
                        View view = attacheViews.get(i);
                        if (view instanceof AbsListView) {
                            final AbsListView absListView = (AbsListView) view;
                            mTarget.setVisibility(View.INVISIBLE);
                            if (mFirstFocus) {
                                absListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                    @Override
                                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                        try {
                                            absListView.removeOnLayoutChangeListener(this);
                                            int factorX = 0, factorY = 0;
                                            Rect rect = new Rect();
                                            View firstView = absListView.getSelectedView();
                                            firstView.getLocalVisibleRect(rect);
                                            if (Math.abs(rect.left - rect.right) > firstView.getMeasuredWidth()) {
                                                factorX = (Math.abs(rect.left - rect.right) - firstView.getMeasuredWidth()) / 2 - 1;
                                                factorY = (Math.abs(rect.top - rect.bottom) - firstView.getMeasuredHeight()) / 2;
                                            }
                                            List<Animator> animatorList = new ArrayList<Animator>(3);
                                            animatorList.addAll(getScaleAnimator(firstView, true));
                                            animatorList.addAll(getMoveAnimator(firstView, factorX, factorY));
                                            mTarget.setVisibility(View.VISIBLE);
                                            AnimatorSet animatorSet = new AnimatorSet();
                                            animatorSet.setDuration(0);
                                            animatorSet.playTogether(animatorList);
                                            animatorSet.start();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                });


                            }
                            break;
                        }
                    }
                } else if (oldFocus instanceof AbsListView && newFocus instanceof AbsListView) {
                    if (attacheViews.indexOf(oldFocus) >= 0 && attacheViews.indexOf(newFocus) >= 0) {

                        AbsListView a = (AbsListView) oldFocus;
                        AbsListView b = (AbsListView) newFocus;

                        MyOnItemSelectedListener oldOn = (MyOnItemSelectedListener) onItemSelectedListenerList.get(oldFocus);
                        MyOnItemSelectedListener newOn = (MyOnItemSelectedListener) onItemSelectedListenerList.get(newFocus);


                        int factorX = 0, factorY = 0;
                        Rect rect = new Rect();
                        View firstView = b.getSelectedView();
                        firstView.getLocalVisibleRect(rect);
                        if (Math.abs(rect.left - rect.right) > firstView.getMeasuredWidth()) {
                            factorX = (Math.abs(rect.left - rect.right) - firstView.getMeasuredWidth()) / 2 - 1;
                            factorY = (Math.abs(rect.top - rect.bottom) - firstView.getMeasuredHeight()) / 2;
                        }

                        List<Animator> animatorList = new ArrayList<Animator>(3);
                        animatorList.addAll(getScaleAnimator(firstView, true));
                        animatorList.addAll(getScaleAnimator(a.getSelectedView(), false));

                        animatorList.addAll(getMoveAnimator(firstView, factorX, factorY));
                        mTarget.setVisibility(View.VISIBLE);

                        mAnimatorSet = new AnimatorSet();


                        mAnimatorSet.setDuration(mDurationTraslate);
                        mAnimatorSet.playTogether(animatorList);
                        mAnimatorSet.start();

                        oldOn.oldFocus = null;
                        oldOn.newFocus = null;

                        newOn.oldFocus = null;
                        if (newOn.newFocus != null && newOn.oldFocus != null) {
                            newOn.newFocus = null;
                        } else {
                            newOn.newFocus = b.getSelectedView();
                        }

                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    protected List<Animator> getScaleAnimator(View view, boolean isScale) {

        List<Animator> animatorList = new ArrayList<Animator>(2);
        if (!mScalable) return animatorList;
        try {
            float scaleBefore = 1.0f;
            float scaleAfter = mScale;
            if (!isScale) {
                scaleBefore = mScale;
                scaleAfter = 1.0f;
            }
            ObjectAnimator scaleX = new ObjectAnimator().ofFloat(view, "scaleX", scaleBefore, scaleAfter);
            ObjectAnimator scaleY = new ObjectAnimator().ofFloat(view, "scaleY", scaleBefore, scaleAfter);
            animatorList.add(scaleX);
            animatorList.add(scaleY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return animatorList;
    }

    protected List<Animator> getMoveAnimator(View newFocus, int factorX, int factorY) {

        List<Animator> animatorList = new ArrayList<>();
        int newXY[];
        int oldXY[];

        try {
            float newX = newFocus.getX();
            float newY = newFocus.getY();
            float oldX = mTarget.getX();
            float oldY = mTarget.getY();

            newXY = getLocation(newFocus);
            oldXY = getLocation(mTarget);

            int newWidth;
            int newHeight;
            int oldWidth = mTarget.getMeasuredWidth();
            int oldHeight = mTarget.getMeasuredHeight();

            if (mScalable) {
                float scaleWidth = newFocus.getMeasuredWidth() * mScale;
                float scaleHeight = newFocus.getMeasuredHeight() * mScale;
                newWidth = (int) (scaleWidth + mMargin * 2 + 0.5);
                newHeight = (int) (scaleHeight + mMargin * 2 + 0.5);
                newX = (int) (newX - (newWidth - newFocus.getMeasuredWidth()) / 2.0f) + factorX;
                newY = (int) (newY - (newHeight - newFocus.getMeasuredHeight()) / 2.0f + 0.5 + factorY);
            } else {
                newWidth = newFocus.getWidth();
                newHeight = newFocus.getHeight();
            }

            if (oldHeight == 0 && oldWidth == 0) {
                oldHeight = newHeight;
                oldWidth = newWidth;
            }

            PropertyValuesHolder valuesWithdHolder = PropertyValuesHolder.ofInt("width", oldWidth, newWidth);
            PropertyValuesHolder valuesHeightHolder = PropertyValuesHolder.ofInt("height", oldHeight, newHeight);
//            PropertyValuesHolder valuesXHolder = PropertyValuesHolder.ofFloat("translationX", oldXY[0], newXY[0]);
//            PropertyValuesHolder valuesYHolder = PropertyValuesHolder.ofFloat("translationY", oldXY[1], newXY[1]);
            PropertyValuesHolder valuesXHolder = PropertyValuesHolder.ofFloat("translationX", oldX, newX);
            PropertyValuesHolder valuesYHolder = PropertyValuesHolder.ofFloat("translationY", oldY, newY);
            final ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(
                    mTarget,
                    valuesWithdHolder,
                    valuesHeightHolder,
                    valuesYHolder,
                    valuesXHolder);

            scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public synchronized void onAnimationUpdate(ValueAnimator animation) {
                    int width = (int) animation.getAnimatedValue("width");
                    int height = (int) animation.getAnimatedValue("height");
                    float translationX = (float) animation.getAnimatedValue("translationX");
                    float translationY = (float) animation.getAnimatedValue("translationY");
                    View view = (View) scaleAnimator.getTarget();
                    assert view != null;
                    int w = view.getLayoutParams().width;
                    view.getLayoutParams().width = width;
                    view.getLayoutParams().height = height;
                    if (width > 0) {
                        view.requestLayout();
                        view.postInvalidate();

                    }
                }
            });
            animatorList.add(scaleAnimator);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return animatorList;
    }

    protected int[] getLocation(View view) {
        int[] location = new int[2];
        try {
            view.getLocationOnScreen(location);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return location;
    }

    public void addOnFocusChanged(FocusListener focusListener) {
        this.mFocusListener.add(focusListener);
    }

    public void removeOnFocusChanged(FocusListener focusListener) {
        this.mFocusListener.remove(focusListener);
    }

    public void addAnimatorListener(Animator.AnimatorListener animatorListener) {
        this.mAnimatorListener.add(animatorListener);
    }

    public void removeAnimatorListener(Animator.AnimatorListener animatorListener) {
        this.mAnimatorListener.remove(animatorListener);
    }

    private class VisibleScope {
        public boolean isVisible;
        public View oldFocus;
        public View newFocus;
    }

    protected VisibleScope checkVisibleScope(View oldFocus, View newFocus) {
        VisibleScope scope = new VisibleScope();
        try {
            scope.oldFocus = oldFocus;
            scope.newFocus = newFocus;
            scope.isVisible = true;
            if (attacheViews.indexOf(oldFocus) >= 0 && attacheViews.indexOf(newFocus) >= 0) {
                return scope;
            }

            if (oldFocus != null && newFocus != null) {
                if (oldFocus.getParent() != newFocus.getParent()) {
                    if ((attacheViews.indexOf(newFocus.getParent()) < 0) || (attacheViews.indexOf(oldFocus.getParent()) < 0 && attacheViews.indexOf(newFocus.getParent()) > 0)) {
                        mTarget.setVisibility(View.INVISIBLE);
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(getScaleAnimator(oldFocus, false));
                        animatorSet.setDuration(0).start();
                        scope.isVisible = false;
                        return scope;
                    } else {
                        mTarget.setVisibility(View.VISIBLE);
                    }
                    if (attacheViews.indexOf(oldFocus.getParent()) < 0) {
                        scope.oldFocus = null;
                    }

                } else {
                    if (attacheViews.indexOf(newFocus.getParent()) < 0) {
                        mTarget.setVisibility(View.INVISIBLE);
                        scope.isVisible = false;
                        return scope;
                    }
                }
            }
            mTarget.setVisibility(View.VISIBLE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return scope;
    }

    @Override
    public void onFocusChanged(View target, View oldFocus, View newFocus) {
        try {
            Log.d(TAG, "onFocusChanged:" + oldFocus + "=" + newFocus);

            if (newFocus == null && attacheViews.indexOf(newFocus) >= 0) {
                return;
            }

            if (oldFocus == newFocus)
                return;

            if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
                mAnimatorSet.end();
            }

            lastFocus = newFocus;
            oldLastFocus = oldFocus;
            mTarget = target;

            VisibleScope scope = checkVisibleScope(oldFocus, newFocus);
            if (!scope.isVisible) {
                return;
            } else {
                oldFocus = scope.oldFocus;
                newFocus = scope.newFocus;
                oldLastFocus = scope.oldFocus;
            }

            if (isScrolling || newFocus == null || newFocus.getWidth() <= 0 || newFocus.getHeight() <= 0)
                return;

            mAnimatorList.clear();

            for (FocusListener f : this.mFocusListener) {
                f.onFocusChanged(oldFocus, newFocus);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onScrollChanged(View target, View attachView) {
        try {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onLayout(View target, View attachView) {
        try {
//
//            LogUtils.info(TAG, "----------------------- attachView.getRootView() = " + attachView.getRootView());
//            LogUtils.info(TAG, "----------------------- target.getParent()" + target.getParent());
//            LogUtils.info(TAG, "----------------------- attachView = " + attachView);
//            ViewGroup viewGroup = (ViewGroup) attachView.getRootView();
//            ViewGroup viewGroup = (ViewGroup) attachView.getParent();
//            if (target.getParent() != null && target.getParent() != viewGroup) {
//                LogUtils.info(TAG, "onLayout......");
//                target.setVisibility(View.VISIBLE);
//                if (mFirstFocus) {
//                    mFirstFocus = false;
////                    viewGroup.requestFocus();
//                    ((ViewGroup) attachView.getParent()).requestFocus();
//                }
//            }

            if (mFirstFocus) {
                mFirstFocus = false;
//                    viewGroup.requestFocus();
                ((ViewGroup) attachView.getParent()).requestFocus();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected boolean mFirstFocus = true;

    public void setFirstFocus(boolean b) {
        this.mFirstFocus = b;
    }

    @Override
    public void onTouchModeChanged(View target, View attachView, boolean isInTouchMode) {
        try {
            if (mEnableTouch && isInTouchMode) {
                target.setVisibility(View.INVISIBLE);
                if (lastFocus != null) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(getScaleAnimator(lastFocus, false));
                    animatorSet.setDuration(0).start();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    protected boolean isScrolling = false;

    protected List<View> attacheViews = new ArrayList<>();
    protected Map<View, AdapterView.OnItemSelectedListener> onItemSelectedListenerList = new HashMap<>();


    @Override
    public void onAttach(View target, View attachView) {
        try {
            mTarget = target;

            LogUtils.debug(TAG, "===================== target.getParent() = " + target.getParent());
            if (target.getParent() != null && (target.getParent() instanceof ViewGroup)) {
                ViewGroup vg = (ViewGroup) target.getParent();
                vg.removeView(target);
            }

//            LogUtils.error(TAG, " AAA attachView = " + attachView);
//            LogUtils.error(TAG, " AAA attachView.getRootView() = " + attachView.getRootView());
//            ViewGroup vg = (ViewGroup) attachView.getRootView();
//            vg.addView(target);

//            ViewGroup vg = (ViewGroup) attachView.getParent();
//            vg.addView(target);

//
            Activity activity = (Activity) target.getContext();
            ViewGroup vg = (ViewGroup) activity.getWindow().getDecorView().getRootView();
            vg.addView(target);

            target.setVisibility(View.GONE);
            if (attachView instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) attachView;
                RecyclerView.OnScrollListener recyclerViewOnScrollListener = null;
                recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        try {
                            super.onScrollStateChanged(recyclerView, newState);

                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                isScrolling = false;
                                View oldFocus = oldLastFocus;
                                View newFocus = lastFocus;
                                VisibleScope scope = checkVisibleScope(oldFocus, newFocus);
                                if (!scope.isVisible) {
                                    return;
                                } else {
                                    oldFocus = scope.oldFocus;
                                    newFocus = scope.newFocus;
                                }
                                AnimatorSet animatorSet = new AnimatorSet();
                                List<Animator> list = new ArrayList<>();
                                list.addAll(getScaleAnimator(newFocus, true));
                                list.addAll(getMoveAnimator(newFocus, 0, 0));
                                animatorSet.setDuration(mDurationTraslate);
                                animatorSet.playTogether(list);
                                animatorSet.start();


                            } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                                isScrolling = true;
                                if (lastFocus != null) {
                                    List<Animator> list = getScaleAnimator(lastFocus, false);
                                    AnimatorSet animatorSet = new AnimatorSet();
                                    animatorSet.setDuration(150);
                                    animatorSet.playTogether(list);
                                    animatorSet.start();
                                }
                            }
                        } catch (Exception ex) {

                        }
                    }
                };
                recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
            } else if (attachView instanceof AbsListView) {

                final AbsListView absListView = (AbsListView) attachView;
                final AdapterView.OnItemSelectedListener onItemSelectedListener = absListView.getOnItemSelectedListener();

                View temp = null;
                if (absListView.getChildCount() > 0) {
                    temp = absListView.getChildAt(0);
                }
                final View tempFocus = temp;
                MyOnItemSelectedListener myOnItemSelectedListener = new MyOnItemSelectedListener();
                myOnItemSelectedListener.onItemSelectedListener = onItemSelectedListener;
                myOnItemSelectedListener.oldFocus = temp;
                absListView.setOnItemSelectedListener(myOnItemSelectedListener);
                onItemSelectedListenerList.put(attachView, myOnItemSelectedListener);

            }

            attacheViews.add(attachView);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    protected class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public View oldFocus = null;
        public View newFocus = null;
        public AnimatorSet animatorSet;
        public AdapterView.OnItemSelectedListener onItemSelectedListener;

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try {
                if (onItemSelectedListener != null && parent != null) {
                    onItemSelectedListener.onItemSelected(parent, view, position, id);
                }
                if (newFocus == null)
                    return;
                newFocus = view;

                Rect rect = new Rect();
                view.getLocalVisibleRect(rect);
                ViewGroup vg = (ViewGroup) newFocus.getParent();

                int factorX = 0, factorY = 0;
                if (Math.abs(rect.left - rect.right) > newFocus.getMeasuredWidth()) {
                    factorX = (Math.abs(rect.left - rect.right) - newFocus.getMeasuredWidth()) / 2 - 1;
                    factorY = (Math.abs(rect.top - rect.bottom) - newFocus.getMeasuredHeight()) / 2;

                }


                List<Animator> animatorList = new ArrayList<Animator>(3);
                animatorList.addAll(getScaleAnimator(newFocus, true));
                if (oldFocus != null)
                    animatorList.addAll(getScaleAnimator(oldFocus, false));
                animatorList.addAll(getMoveAnimator(newFocus, factorX, factorY));
                mTarget.setVisibility(View.VISIBLE);

                if (animatorSet != null && animatorSet.isRunning()){
                    animatorSet.end();
                }
                animatorSet = new AnimatorSet();
                animatorSet.setDuration(mDurationTraslate);
                animatorSet.playTogether(animatorList);
                animatorSet.start();


                oldFocus = newFocus;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onNothingSelected(parent);
            }
        }
    }


    @Override
    public void OnDetach(View targe, View view) {
        if (targe.getParent() == view) {
            ((ViewGroup) view).removeView(targe);
        }

        attacheViews.remove(view);
    }

    public void setEnableTouch(boolean enableTouch) {
        this.mEnableTouch = enableTouch;
    }

    public boolean isScalable() {
        return mScalable;
    }

    public void setScalable(boolean scalable) {
        this.mScalable = scalable;
    }

    public float getScale() {
        return mScale;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public int getMargin() {
        return mMargin;
    }

    public void setMargin(int mMargin) {
        this.mMargin = mMargin;
    }

    /**
     * 设置动画时间
     * @param duration 动画时间，单位毫秒
     *
     * Created by lancy on 2017/6/26 15:57
     */
    public void setDuration(long duration) {
        this.mDurationTraslate = duration;
    }

}
