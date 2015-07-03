package com.willard.picturesplayer;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * @author willard
 * @2015-7-3
 * @上午9:35:37
 */
public class MyViewPager extends ViewPager {
	//默认不能手动滑动Pager
	private boolean canScroll = false;

	public boolean isCanScroll() {
		return canScroll;
	}

	public void setCanScroll(boolean canScroll) {
		this.canScroll = canScroll;
	}

	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (canScroll) {
			return super.onInterceptTouchEvent(arg0);
		} else {
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (canScroll) {
			return super.onTouchEvent(arg0);
		} else {
			return false;
		}
	}

}
