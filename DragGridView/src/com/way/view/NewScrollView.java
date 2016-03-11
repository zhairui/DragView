package com.way.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class NewScrollView extends ScrollView{

	private int mLastX=0;
	private int mLastY=0;
	public NewScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public NewScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		int x=(int) ev.getX();
		int y=(int) ev.getY();
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX=x-mLastX;
			int deltaY=y-mLastY;
			if(Math.abs(deltaY)<Math.abs(deltaX)){
				return false;
			}
			
		case MotionEvent.ACTION_UP:
			break;
			
		}
		mLastX=x;
		mLastY=y;
		return super.onInterceptTouchEvent(ev);
	}

}
