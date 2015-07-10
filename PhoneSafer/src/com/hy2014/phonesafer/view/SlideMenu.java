package com.hy2014.phonesafer.view;

import com.hy2014.phonesafer.utils.LogUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 左右滑动式菜单
 * @author Dawin
 *
 */
public class SlideMenu extends ViewGroup
{
	//菜单界面
	public static final int SCREEN_MENU = 0;
	//主界面
	public static final int SCREEN_MAIN = 1;	
	private static final int SCREEN_INVALID = -1;
	//当前界面
	private int mCurrentScreen;
	private int mNextScreen = SCREEN_INVALID;
	//这个类封装了滚动操作。滚动的持续时间可以通过构造函数传递，并且可以指定滚动动作的持续的最长时间。
	//经过这段时间，滚动会自动定位到最终位置，并且通过computeScrollOffset()会得到的返回值为false，表明滚动动作已经结束。
	private Scroller mScroller;
	//跟踪滑动速度
	private VelocityTracker mVelocityTracker;
	//用户滑动
	private int mTouchSlop;

	private float mLastMotionX;
	private float mLastMotionY;

	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	//速度
	private static final int SNAP_VELOCITY = 1000;

	public int mTouchState = TOUCH_STATE_REST;
	private boolean mLocked;
	private boolean mAllowLongPress;

	public SlideMenu(Context context) {
		this(context, null, 0);
	}

	public SlideMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		
	}

	public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mScroller = new Scroller(getContext());
		mCurrentScreen = SCREEN_MAIN;
		//Distance in pixels a touch can wander before we think the user is scrolling
		mTouchSlop =ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measureViews(widthMeasureSpec, heightMeasureSpec);
	}

	public void measureViews(int widthMeasureSpec, int heightMeasureSpec)
	{
		View menuView = getChildAt(0);
		menuView.measure(menuView.getLayoutParams().width + menuView.getLeft() + menuView.getRight(), heightMeasureSpec);

		View contentView = getChildAt(1);
		contentView.measure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int childCount = getChildCount();
		if (childCount != 2)
			{
				throw new IllegalStateException("The childCount of SlidingMenu must be 2");
			}

		View menuView = getChildAt(0);
		final int width = menuView.getMeasuredWidth();
		menuView.layout(-width, 0, 0, menuView.getMeasuredHeight());

		View contentView = getChildAt(1);
		contentView.layout(0, 0, contentView.getMeasuredWidth(), contentView.getMeasuredHeight());
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		View child;
		for (int i = 0; i < getChildCount(); i++)
			{
				child = getChildAt(i);
				child.setFocusable(true);
				child.setClickable(true);
			}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{

		if (mLocked)
			{
				return true;
			}
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST))
			{
				return true;
			}

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action)
		{
		case MotionEvent.ACTION_MOVE:
	
			/*--------------------------原始代码，不判断滑动范围----------------------------*/
			final int xDiff = (int) Math.abs(x - mLastMotionX);
			final int yDiff = (int) Math.abs(y - mLastMotionY);

			final int touchSlop = mTouchSlop;
			boolean xMoved = xDiff > touchSlop;
			boolean yMoved = yDiff > touchSlop;

			if (xMoved || yMoved)
				{

					if (xMoved)
						{
							// Scroll if the user moved far enough along the X
							// axis
							mTouchState = TOUCH_STATE_SCROLLING;
							enableChildrenCache();
						}
					// Either way, cancel any pending longpress
					if (mAllowLongPress)
						{
							mAllowLongPress = false;
							// Try canceling the long press. It could also have
							// been
							// scheduled
							// by a distant descendant, so use the
							// mAllowLongPress flag
							// to block
							// everything
							final View currentScreen = getChildAt(mCurrentScreen);
							currentScreen.cancelLongPress();
						}
				}
			break;

		case MotionEvent.ACTION_DOWN:
			//LogUtil.w("ACTION_DOWN");
			// Remember location of down touch
			mLastMotionX = x;
			mLastMotionY = y;
			mAllowLongPress = true;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			break;		
			
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			//LogUtil.w("ACTION_CANCEL|ACTION_UP");
			// Release the drag
			clearChildrenCache();
			mTouchState = TOUCH_STATE_REST;
			mAllowLongPress = false;
			break;
		}

		/*
		 * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
		return mTouchState != TOUCH_STATE_REST;
	}
	
	
	/**
	 * 打开
	 */
	void enableChildrenCache()
	{
		final int count = getChildCount();
		for (int i = 0; i < count; i++)
			{
				final View layout = (View) getChildAt(i);
				layout.setDrawingCacheEnabled(true);
			}
	}
	/**
	 * 关闭
	 */
	void clearChildrenCache()
	{
		final int count = getChildCount();
		for (int i = 0; i < count; i++)
			{
				final View layout = (View) getChildAt(i);
				layout.setDrawingCacheEnabled(false);
			}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if (mLocked)
			{
				return true;
			}

		if (mVelocityTracker == null)
			{
				mVelocityTracker = VelocityTracker.obtain();
			}
		
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();

		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
			if (!mScroller.isFinished())
				{
					mScroller.abortAnimation();
				}

			// Remember where the motion event started
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mTouchState == TOUCH_STATE_SCROLLING)
				{
					// Scroll to follow the motion event
					final int deltaX = (int) (mLastMotionX - x);
					mLastMotionX = x;

					if (deltaX < 0)
						{
							if (deltaX + getScrollX() >= -getChildAt(0).getWidth())
								{
									scrollBy(deltaX, 0);
								}

						} else if (deltaX > 0)
						{
							final int availableToScroll = getChildAt(getChildCount() - 1).getRight() - getScrollX() - getWidth();

							if (availableToScroll > 0)
								{
									scrollBy(Math.min(availableToScroll, deltaX), 0);
								}
						}
				}
			break;
		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_SCROLLING)
				{
					final VelocityTracker velocityTracker = mVelocityTracker;
					velocityTracker.computeCurrentVelocity(1000);
					int velocityX = (int) velocityTracker.getXVelocity();

					if (velocityX > SNAP_VELOCITY && mCurrentScreen == SCREEN_MAIN)
						{
							// Fling hard enough to move left
							snapToScreen(SCREEN_MENU);
							LogUtil.e("snapToScreen(SCREEN_MENU);");
						} else if (velocityX < -SNAP_VELOCITY && mCurrentScreen == SCREEN_MENU)
						{
							// Fling hard enough to move right
							snapToScreen(SCREEN_MAIN);
							LogUtil.e("snapToScreen(SCREEN_MAIN);");
						} else
						{
							snapToDestination();
						}

					if (mVelocityTracker != null)
						{
							mVelocityTracker.recycle();
							mVelocityTracker = null;
						}
				}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
		}

		return true;
	}

	// 滚动时，被调用
	@Override
	public void computeScroll()
	{
		//未结束滚动
		if (mScroller.computeScrollOffset())
			{
			//	LogUtil.i("computeScroll() scrollTo ");
				scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
				
			} 
		//TODO：mCurrentScreen 不正确 
		else if (mNextScreen != SCREEN_INVALID)
			{
			//	LogUtil.i("computeScroll()   clearChildrenCache()");
				mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
				mNextScreen = SCREEN_INVALID;
				clearChildrenCache();
			}
	}

	@Override
	public void scrollTo(int x, int y)
	{
		super.scrollTo(x, y);
		postInvalidate();
	}

	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		final int scrollX = getScrollX();
		super.dispatchDraw(canvas);
		canvas.translate(scrollX, 0);
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction)
	{
		if (direction == View.FOCUS_LEFT)
			{
				if (getCurrentScreen() > 0)
					{
						snapToScreen(getCurrentScreen() - 1);
						return true;
					}
			} else if (direction == View.FOCUS_RIGHT)
			{
				if (getCurrentScreen() < getChildCount() - 1)
					{
						snapToScreen(getCurrentScreen() + 1);
						return true;
					}
			}
		return super.dispatchUnhandledMove(focused, direction);
	}

	/**
	 * 滑动到指定界面
	 * @param whichScreen
	 */
	protected void snapToScreen(int whichScreen)
	{

		enableChildrenCache();

		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		boolean changingScreens = whichScreen != mCurrentScreen;

		mNextScreen = whichScreen;
	
		View focusedChild = getFocusedChild();
		if (focusedChild != null && changingScreens && focusedChild == getChildAt(mCurrentScreen))
			{
				focusedChild.clearFocus();
			}

		final int newX = (whichScreen - 1) * getChildAt(0).getWidth();
		final int delta = newX - getScrollX();
		mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
		invalidate();
	}

	protected void snapToDestination()
	{
		if (getScrollX() == 0)
			{
				return;
			}
		final int screenWidth = getChildAt(0).getWidth();
		final int whichScreen = (screenWidth + getScrollX() + (screenWidth / 2)) / screenWidth;
		snapToScreen(whichScreen);
	}

	public int getCurrentScreen()
	{
		return mCurrentScreen;
	}

	public boolean isMainScreenShowing()
	{
		return mCurrentScreen == SCREEN_MAIN;
	}

	public void openMenu()
	{
		mCurrentScreen = SCREEN_MENU;
		snapToScreen(mCurrentScreen);
	}

	public void closeMenu()
	{
		mCurrentScreen = SCREEN_MAIN;
		snapToScreen(mCurrentScreen);
	}

	public void unlock()
	{
		mLocked = false;
	}

	public void lock()
	{
		mLocked = true;
	}

}