package com.way.view;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Scroller;

public class DragTest extends ViewGroup implements OnLongClickListener,
		OnTouchListener {

	int width,height;
	int padding = 20;
	int childLeft = 0;
	int row = 0;
	int childSize = 200;
	int mLastX = 0, mLastY = 0;
	private RowListener mRowListener;
	float longX = 0, longY = 0;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	int dragged = -1, index = -1, lastTarget = -1;
	public static int animT = 150;
	int standL=0,standT=0,standR=0,standB=0;
	boolean flag=true;
	
	float distanceX=0,distanceY=0,firstX=0,firstY=0;
	
	protected ArrayList<Integer> newPositions = new ArrayList<Integer>();

	public void setRowListener(RowListener rowlistener) {
		this.mRowListener = rowlistener;
	}

	private OnRearrangeListener onRearrangeListener;

	public void setOnRearrangeListener(OnRearrangeListener onRearrangeListener) {
		onRearrangeListener = onRearrangeListener;
	}

	/**
	 * 拖动item的接口
	 */
	public interface OnRearrangeListener {

		public abstract void onRearrange(int oldIndex, int newIndex);
	}

	public DragTest(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public DragTest(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		if (mScroller == null) {
			mScroller = new Scroller(getContext());
			mVelocityTracker = VelocityTracker.obtain();
		}
		setOnLongClickListener(this);
		setOnTouchListener(this);
		WindowManager wm = (WindowManager) getContext()

                .getSystemService(Context.WINDOW_SERVICE);

		width = wm.getDefaultDisplay().getWidth();
 		height = wm.getDefaultDisplay().getHeight();
	}

	@Override
	protected void onLayout(boolean arg0, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		for (int i = 0; i < getChildCount(); i++) {
			Log.i("sdf", "asdf" + mRowListener.getRow());
			childLeft = padding;
			if (i >= mRowListener.getRow()) {
				l = padding * (i / mRowListener.getRow() + 1) + childSize
						* (i / mRowListener.getRow());
				t = padding * (i % mRowListener.getRow() + 1) + childSize
						* (i % mRowListener.getRow());
				b = padding * (i % mRowListener.getRow() + 1) + childSize
						* (i % mRowListener.getRow() + 1);
				r = padding * (i / mRowListener.getRow() + 1) + childSize
						* (i / mRowListener.getRow() + 1);
				getChildAt(i).layout(l, t, r, b);
			} else {
				getChildAt(i).layout(childLeft,
						padding * (i + 1) + childSize * i,
						childLeft + childSize,
						padding * (i + 1) + childSize * (i + 1));
			}
		}
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	public void addView(View child) {
		// TODO Auto-generated method stub
		super.addView(child);
		newPositions.add(-1);
		if(getChildCount()>0 && flag){
			int posfirst[]=new int[2];
			getChildAt(0).getLocationOnScreen(posfirst);
			firstX=posfirst[0];
			firstY=posfirst[1];
			Log.i("firstX",firstX+"=="+getLeft()+"==="+getChildAt(0).getLeft()+"==="+getChildAt(0).getWidth());
			flag=false;
		}
	}

	public interface RowListener {
		public abstract int getRow();
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		index = getViewIndex((int)longX, (int)longY);
		if (index != -1) {
			dragged = index;
			animateDragged();
			return true;
		}
		return false;
	}

	public int getViewIndex(int x, int y) {
		int indexx = -1;
		int indexy = -1;
		int col = x - padding;

		int row = y - padding;
		for (int i = 0; col > 0; i++) {
			if (col < childSize)
				indexx = i;
			col -= (childSize + padding);
		}
		for (int i = 0; row > 0; i++) {
			if (row < childSize)
				indexy = i;
			row -= (childSize + padding);
		}
		if (indexy > 3) {
			return -1;
		}
		index = indexx * mRowListener.getRow() + indexy;
		return index;
	}

	protected int getTargetFromCoor(int x, int y) {
		/*
		 * if (getColOrRowFromCoor(y + scroll) == -1) // touch is between rows
		 * return -1;
		 */
		// if (getIndexFromCoor(x, y) != -1) //touch on top of another visual
		// return -1;

		int leftPos = getViewIndex(x-(childSize / 4)  , y);
		int rightPos = getViewIndex(x+ (childSize / 4) , y);
		if (leftPos == -1 && rightPos == -1) // touch is in the middle of
			// nowhere
			return -1;
		if (leftPos == rightPos) // touch is in the middle of a visual
			return -1;

		int target = -1;
		if (rightPos > -1)
			target = rightPos;
		else if (leftPos > -1)
			target = leftPos + 1;
		if (dragged < target)
			return target - 1;

		// Toast.makeText(getContext(), "Target: " + target + ".",
		// Toast.LENGTH_SHORT).show();
		return target;
	}

	protected Point getCoorFromIndex(int index) {
		int col = index % mRowListener.getRow();
		int row = index / mRowListener.getRow();
		return new Point(padding + (childSize + padding) * col, padding
				+ (childSize + padding) * row);
	}

	private void animateDragged() {
		View v = getChildAt(dragged);
		if (v != null) {
			Log.i("getleft", v.getLeft()+"++++");
			int l = (int) v.getLeft() - padding, t = (int) v.getTop() - padding;
			v.layout(l, t, l + (childSize + padding * 2), t
					+ (childSize + padding * 2));
			AnimationSet animSet = new AnimationSet(true);
			ScaleAnimation scale = new ScaleAnimation(.667f, 1, .667f, 1,
					childSize * 3 / 4, childSize * 3 / 4);
			scale.setDuration(animT);
			AlphaAnimation alpha = new AlphaAnimation(1, .5f);
			alpha.setDuration(animT);

			animSet.addAnimation(scale);
			animSet.addAnimation(alpha);
			animSet.setFillEnabled(true);
			animSet.setFillAfter(true);

			v.clearAnimation();
			v.startAnimation(animSet);
		}

	}

	protected void animateGap(int target) {
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (i == dragged)
				continue;
			int newPos = i;
			if (dragged < target && i >= dragged + 1 && i <= target)
				newPos--;
			else if (target < dragged && i >= target && i < dragged)
				newPos++;

			// animate
			int oldPos = i;
			if (newPositions.get(i) != -1)
				oldPos = newPositions.get(i);
			if (oldPos == newPos)
				continue;

			Point oldXY = getCoorFromIndex(oldPos);
			Point newXY = getCoorFromIndex(newPos);
			Point oldOffset = new Point(oldXY.y - v.getLeft(), oldXY.x
					- v.getTop());
			Point newOffset = new Point(newXY.y - v.getLeft(), newXY.x
					- v.getTop());

			TranslateAnimation translate = new TranslateAnimation(
					Animation.ABSOLUTE, oldOffset.x, Animation.ABSOLUTE,
					newOffset.x, Animation.ABSOLUTE, oldXY.y, Animation.ABSOLUTE, newXY.y);
			translate.setDuration(animT);
			translate.setFillEnabled(true);
			translate.setFillAfter(true);
			v.clearAnimation();
			v.startAnimation(translate);

			newPositions.set(i, newPos);
		}
	}

	@SuppressLint("WrongCall")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mVelocityTracker.addMovement(event);
		int x = (int) event.getX();
		int y = (int) event.getY();
		int deltaX = 0;
		int deltaY;
		float moveX = 0, moveY=0,movelastX=0,movelastY=0;
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			longX=event.getX()-distanceX;
			longY=event.getY()-distanceY;

			break;
		case MotionEvent.ACTION_MOVE:
			deltaX = x - mLastX;
			deltaY = y - mLastY;
			
			if (dragged != -1) {
				View v2 = getChildAt(dragged);
				if (v2 != null) {
					v2.getParent().getParent().requestDisallowInterceptTouchEvent(true);
					int x2 = (int) event.getX(), y2 = (int) event.getY();
					int l = (x-(int)distanceX) - (3 * childSize / 4), t = (y-(int)distanceY)
							- (3 * childSize / 4);
					getChildAt(dragged).layout(l, t, l + (childSize * 3 / 2),
							t + (childSize * 3 / 2));
									
					if(longX>getChildAt(getChildCount()-1).getLeft()){//表示点击的是最后一列
						
					}else{
						if((x2+childSize)>width && getChildAt(getChildCount()-1).getRight()>width){//表示整个屏幕可以滑动
							int poslast[]=new int[2];
							getChildAt(getChildCount()-1).getLocationOnScreen(poslast);
							movelastX=poslast[0];
							movelastY=poslast[1];
							if((movelastX+padding+childSize-width)<Math.abs(10)){
								if(standL==0){
									standL=l;
									standT=t;
									standR=l+(childSize * 3 / 2);
									standB=t + (childSize * 3 / 2);
								}
								getChildAt(dragged).layout(standL, standT,standR,
										standB);
								
							}else{
								scrollBy(10, 0);
								if(standL==0){
									standL=l;
									standT=t;
									standR=l+(childSize * 3 / 2);
									standB=t + (childSize * 3 / 2);
								}
								standL+=10;
								standR+=10;
								getChildAt(dragged).layout(standL, standT,standR,
										standB);
							}
							return true;
						} else if((x2-childSize<0)){
							int posfirst[]=new int[2];
							getChildAt(0).getLocationOnScreen(posfirst);
							movelastX=posfirst[0];
							movelastY=posfirst[1];	
							if((movelastX-50-padding)>0){
								if(standL==0){
									standL=l;
									standT=t;
									standR=l+(childSize * 3 / 2);
									standB=t + (childSize * 3 / 2);
								}
								getChildAt(dragged).layout(standL, standT,standR,
										standB);
							}else{
								scrollBy(-10, 0);
								if(standL==0){
									standL=l;
									standT=t;
									standR=l+(childSize * 3 / 2);
									standB=t + (childSize * 3 / 2);
								}
								standL-=10;
								standR-=10;
								getChildAt(dragged).layout(standL, standT,standR,
										standB);
							}
							return true;
						}
						else{
							standL=l;
							standT=t;
							standR=l+(childSize * 3 / 2);
							standB=t + (childSize * 3 / 2);
						}
					}
					
					
				}
			} else {
				if (Math.abs(deltaY) > Math.abs(deltaX)) {
					return false;
				}
				int pos2[]=new int[2];
				
				getChildAt(0).getLocationOnScreen(pos2);
				moveX=pos2[0];
				moveY=pos2[1];
				
				int poslast[]=new int[2];
				
				getChildAt(getChildCount()-1).getLocationOnScreen(poslast);
				movelastX=poslast[0];
				movelastY=poslast[1];
				
				Log.i("moveX", movelastX+"=="+getLeft()+"=="+width);
				if(Math.abs((moveX-(getLeft()+padding)))<Math.abs(deltaX) && deltaX>0){
					
				}else if((movelastX+padding+childSize-width)<Math.abs(deltaX) && deltaX<0){
					
				}else{
					
					scrollBy(-deltaX, 0);
				}
				
				
			}
			break;
		case MotionEvent.ACTION_UP:
			int pos3[]=new int[2];
			
			getChildAt(0).getLocationOnScreen(pos3);
			float moveX2=pos3[0];
			float moveY2=pos3[1];
			distanceX=moveX2-firstX;
			distanceY=moveY2-firstY;
			Log.i("distance", moveX2+"==="+distanceX+"+++"+firstX);
			if (dragged != -1) {
                View view = getChildAt(dragged);
                
                int exchangeX=(int) event.getX();
                int exchangeY=(int) event.getY();
                Log.i("activity", exchangeX+"交换位置"+(longX+distanceX));
                if(view!=null){
                	if(exchangeX-(longX+distanceX)>=childSize/2){
                		int pre=getViewIndex(exchangeX, exchangeY);
                		int target=getViewIndex((int)longX, (int)longY);
                		if(pre>0 && target>0){
                			reorderChildren(pre,target);
                		}
                		
                	}else{
                		view.requestLayout();
                	}
                }
                
                v.clearAnimation();
                if (v instanceof ImageView)
                    ((ImageView) v).setAlpha(255);
                lastTarget = -1;
                dragged = -1;
            }

			break;
		}
		mLastX = x;
		mLastY = y;
		return false;
	}

	@SuppressLint("WrongCall")
	protected void reorderChildren(int pre,int target) {
		// FIGURE OUT HOW TO REORDER CHILDREN WITHOUT REMOVING THEM ALL AND
		// RECONSTRUCTING THE LIST!!!
		
		
		if (onRearrangeListener != null)
			onRearrangeListener.onRearrange(dragged, lastTarget); 
		ArrayList<View> children = new ArrayList<View>();
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).clearAnimation();
			children.add(getChildAt(i));
		}
		removeAllViews();
		 
		View preview=children.get(pre);
		View targetview=children.get(target);
		
		children.add(pre+1, targetview);
		children.remove(target);
		for(int i=0;i<children.size();i++){
			newPositions.set(i, -1);
		
			addView(children.get(i));
		}
		onLayout(true, getLeft(), getTop(), getRight(), getBottom());
		
	}
}
