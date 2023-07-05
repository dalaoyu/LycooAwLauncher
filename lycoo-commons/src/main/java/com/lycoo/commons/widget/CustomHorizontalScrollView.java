package com.lycoo.commons.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.lycoo.commons.util.LogUtils;

/**
 * 自定义HorizontalScrollView
 * 主要解决当HorizontalScrollView的内容超过屏幕大小，当光标移动到两边的时候<br>
 * 完全显示出Item,不用移动两次。
 */
public class CustomHorizontalScrollView extends HorizontalScrollView {

	@SuppressWarnings("unused")
	private static final String TAG = CustomHorizontalScrollView.class.getSimpleName();

	private int state = DEFAULT_STATE;
	public static final int DEFAULT_STATE = 0;
	public static final int LEFT_STATE = 1;
	public static final int RIGHT_STATE = 2;

	private Point[] horizontalEdge;
	
	public Point[] getHorizontalEdge() {
		return horizontalEdge;
	}

	public void setHorizontalEdge(Point[] horizontalEdge) {
		this.horizontalEdge = horizontalEdge;
	}

	public CustomHorizontalScrollView(Context context) {
		super(context);
	}

	public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (state == DEFAULT_STATE) {
			super.onScrollChanged(l, t, oldl, oldt);
		} else if (state == LEFT_STATE) {
			scrollTo(horizontalEdge[0].x, horizontalEdge[0].y);
		} else {
			scrollTo(horizontalEdge[1].x, horizontalEdge[1].y);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		LogUtils.info(TAG, "onInterceptTouchEvent>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		LogUtils.info(TAG, "onTouchEvent>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		return super.onTouchEvent(ev);
	}
}
