package com.lycoo.commons.widget;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 主要用来控制ViewPage移动动画的时间
 * 
 * michaellancy---------------------------- 2015年6月11日 ----------------------------
 */
public class CustomViewPageScroller extends Scroller {

	private int animDuration = 500;

	public CustomViewPageScroller(Context context) {
		super(context);
	}

	public CustomViewPageScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	public CustomViewPageScroller(Context context, Interpolator interpolator, boolean flywheel) {
		super(context, interpolator, flywheel);
	}

	public int getAnimDuration() {
		return animDuration;
	}

	public void setAnimDuration(int animDuration) {
		this.animDuration = animDuration;
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		super.startScroll(startX, startY, dx, dy, animDuration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, animDuration);
	}

}
