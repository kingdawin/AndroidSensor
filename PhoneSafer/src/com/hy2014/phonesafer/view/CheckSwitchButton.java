package com.hy2014.phonesafer.view;


import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.view.FrameAnimationController;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.CheckBox;
/**
 * 开关，仿iOS
 * 
 * @describe 使用了第三方的 SwitchButton，添加了相关属性的注释，更容易看懂
 * @author https://github.com/Issacw0ng/SwitchButton
 *
 */
public class CheckSwitchButton extends CheckBox {
	/** 画笔 */
	private Paint mPaint;
	private ViewParent mParent;
	private Bitmap mBottom;
	private Bitmap mCurBtnPic;
	private Bitmap mBtnPressed;
	private Bitmap mBtnNormal;
	/** 整体框架 */
	private Bitmap mFrame;
	/** 阴影层，不能点击的时候显示 */
	private Bitmap mMask;
	/** 保存布局的矩阵 */
	private RectF mSaveLayerRectF;
	/** 布局之间叠层，好比背景和背景上的图片效果 */
	private PorterDuffXfermode mXfermode;
	/** 首次按下的Y */
	private float mFirstDownY;
	/** 首次按下的X */
	private float mFirstDownX;
	/** 图片的绘制位置 */
	private float mRealPos;
	/** 按钮的位置 */
	private float mBtnPos;
	/** 开关打开的位置 */
	private float mBtnOnPos;
	/** 开关关闭的位置 */
	private float mBtnOffPos;
	/** 阴影的宽度 */
	private float mMaskWidth;
	/** 阴影的高度 */
	private float mMaskHeight;
	/** 开关圆形按钮的宽度 */
	private float mBtnWidth;
	/** 开关初始坐标 */
	private float mBtnInitPos;

	private int mClickTimeout;

	private int mTouchSlop;
	/** 最大透明度，就是不透明 */
	private final int MAX_ALPHA = 255;
	/** 当前透明度，这里主要用于如果控件的enable属性为false时候设置半透明 ，即不可以点击 */
	private int mAlpha = MAX_ALPHA;

	private boolean mChecked = false;

	private boolean mBroadcasting;

	private boolean mTurningOn;

	private PerformClick mPerformClick;
	/** 开关状态切换监听接口 */
	private OnCheckedChangeListener mOnCheckedChangeListener;

	private OnCheckedChangeListener mOnCheckedChangeWidgetListener;
	/** 判断是否在进行动画  */
	private boolean mAnimating;

	private final float VELOCITY = 350;
	/** 滑动速度 */
	private float mVelocity;

	private final float EXTENDED_OFFSET_Y = 15;
	/** Y轴方向扩大的区域,增大点击区域 */
	private float mExtendOffsetY;

	private float mAnimationPosition;

	private float mAnimatedVelocity;

	public CheckSwitchButton(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.checkboxStyle);
	}

	public CheckSwitchButton(Context context) {
		this(context, null);
	}

	public CheckSwitchButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		Resources resources = context.getResources();

		// get viewConfiguration
		mClickTimeout = ViewConfiguration.getPressedStateDuration()
				+ ViewConfiguration.getTapTimeout();
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
  
		// get Bitmap
		mBottom = BitmapFactory.decodeResource(resources, R.drawable.checkswitch_bottom);
		//按下的按钮
		mBtnPressed = BitmapFactory.decodeResource(resources,R.drawable.checkswitch_btn_pressed);
		//放开的按钮
		mBtnNormal = BitmapFactory.decodeResource(resources,R.drawable.checkswitch_btn_unpressed);
		mFrame = BitmapFactory.decodeResource(resources, R.drawable.checkswitch_frame);
		mMask = BitmapFactory.decodeResource(resources, R.drawable.checkswitch_mask);
		mCurBtnPic = mBtnNormal;

		mBtnWidth = mBtnPressed.getWidth();
		mMaskWidth = mMask.getWidth();
		mMaskHeight = mMask.getHeight();

		mBtnOffPos = mBtnWidth / 2;
		mBtnOnPos = mMaskWidth - mBtnWidth / 2;
		// 判断起始位置,如果设定了mChecked为true，起始位置为 mBtnOnPos
		mBtnPos = mChecked ? mBtnOnPos : mBtnOffPos;
		mRealPos = getRealPos(mBtnPos);
		// density 密度
		final float density = getResources().getDisplayMetrics().density;// 方法是获取资源密度（Density）
		mVelocity = (int) (VELOCITY * density + 0.5f);
		mExtendOffsetY = (int) (EXTENDED_OFFSET_Y * density + 0.5f);
		// 创建一个新的矩形与指定的坐标。
		mSaveLayerRectF = new RectF(0, mExtendOffsetY, mMask.getWidth(),
				mMask.getHeight() + mExtendOffsetY);
		mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);// PorterDuff.Mode.SRC_IN
																	// ：这个属性代表
																	// 取两层绘制交集。显示上层。
	}

	@Override
	public void setEnabled(boolean enabled) {
		mAlpha = enabled ? MAX_ALPHA : MAX_ALPHA / 2;
		super.setEnabled(enabled);
	}

	public boolean isChecked() {
		return mChecked;
	}

	/** 自动判断切换至相反的属性 : true -->false ;false -->true */
	public void toggle() {
		setChecked(!mChecked);
	}

	/**
	 * 内部调用此方法设置checked状态，此方法会延迟执行各种回调函数，保证动画的流畅度
	 * 
	 * @param checked
	 */
	private void setCheckedDelayed(final boolean checked) {
		this.postDelayed(new Runnable() {

			@Override
			public void run() {
				setChecked(checked);
			}
		}, 10);
	}

	/**
	 * <p>
	 * Changes the checked state of this button.
	 * </p>
	 * 
	 * @param checked
	 *            true to check the button, false to uncheck it
	 */
	public void setChecked(boolean checked) {

		if (mChecked != checked) {
			mChecked = checked;

			mBtnPos = checked ? mBtnOnPos : mBtnOffPos;
			mRealPos = getRealPos(mBtnPos);
			invalidate();

			// Avoid infinite recursions if setChecked() is called from a
			// listener
			if (mBroadcasting) {
				return;
			}

			mBroadcasting = true;
			if (mOnCheckedChangeListener != null) {
				mOnCheckedChangeListener.onCheckedChanged(CheckSwitchButton.this,mChecked);
			}
			if (mOnCheckedChangeWidgetListener != null) {
				mOnCheckedChangeWidgetListener.onCheckedChanged(CheckSwitchButton.this, mChecked);
			}

			mBroadcasting = false;
		}
	}

	/**
	 * Register a callback to be invoked when the checked state of this button
	 * changes.
	 * 
	 * @param listener
	 *            the callback to call on checked state change
	 */
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeListener = listener;
	}

	/**
	 * Register a callback to be invoked when the checked state of this button
	 * changes. This callback is used for internal purpose only.
	 * 
	 * @param listener
	 *            the callback to call on checked state change
	 * @hide
	 */
	void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeWidgetListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		float deltaX = Math.abs(x - mFirstDownX);
		float deltaY = Math.abs(y - mFirstDownY);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			attemptClaimDrag();
			mFirstDownX = x;
			mFirstDownY = y;
			mCurBtnPic = mBtnPressed;
			mBtnInitPos = mChecked ? mBtnOnPos : mBtnOffPos;
			break;
		case MotionEvent.ACTION_MOVE:
			// 拖动着的时间
			float time = event.getEventTime() - event.getDownTime();
			// 当前按钮的位置
			mBtnPos = mBtnInitPos + event.getX() - mFirstDownX;
			if (mBtnPos >= mBtnOffPos) {
				mBtnPos = mBtnOffPos;
			}
			if (mBtnPos <= mBtnOnPos) {
				mBtnPos = mBtnOnPos;
			}
			mTurningOn = mBtnPos > (mBtnOffPos - mBtnOnPos) / 2 + mBtnOnPos;

			mRealPos = getRealPos(mBtnPos);
			break;
		case MotionEvent.ACTION_UP:
			mCurBtnPic = mBtnNormal;
			time = event.getEventTime() - event.getDownTime();
			if (deltaY < mTouchSlop && deltaX < mTouchSlop
					&& time < mClickTimeout) {
				if (mPerformClick == null) {
					mPerformClick = new PerformClick();
				}
				if (!post(mPerformClick)) {
					performClick();
				}
			} else {
				startAnimation(!mTurningOn);
			}
			break;
		}

		invalidate();
		return isEnabled();
	}

	private final class PerformClick implements Runnable {
		public void run() {
			performClick();
		}
	}

	@Override
	public boolean performClick() {
		startAnimation(!mChecked);
		return true;
	}

	/**
	 * 通知父类不要拦截touch事件 Tries to claim the user's drag motion, and requests
	 * disallowing any ancestors from stealing events in the drag.
	 */
	private void attemptClaimDrag() {
		mParent = getParent();
		if (mParent != null) {
			// 通知父类不要拦截touch事件
			mParent.requestDisallowInterceptTouchEvent(true);
		}
	}

	/**
	 * 将btnPos转换成RealPos
	 * 
	 * @param btnPos
	 * @return
	 */
	private float getRealPos(float btnPos) {
		return btnPos - mBtnWidth / 2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.saveLayerAlpha(mSaveLayerRectF, mAlpha, Canvas.MATRIX_SAVE_FLAG
				| Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
				| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
				| Canvas.CLIP_TO_LAYER_SAVE_FLAG);
		// 绘制蒙板
		canvas.drawBitmap(mMask, 0, mExtendOffsetY, mPaint);
		mPaint.setXfermode(mXfermode);

		// 绘制底部图片
		canvas.drawBitmap(mBottom, mRealPos, mExtendOffsetY, mPaint);
		mPaint.setXfermode(null);
		// 绘制边框
		canvas.drawBitmap(mFrame, 0, mExtendOffsetY, mPaint);

		// 绘制按钮
		canvas.drawBitmap(mCurBtnPic, mRealPos, mExtendOffsetY, mPaint);
		canvas.restore();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension((int) mMaskWidth,
				(int) (mMaskHeight + 2 * mExtendOffsetY));
	}

	private void startAnimation(boolean turnOn) {
		mAnimating = true;
		mAnimatedVelocity = turnOn ? -mVelocity : mVelocity;
		mAnimationPosition = mBtnPos;
		new SwitchAnimation().run();
	}

	private void stopAnimation() {
		mAnimating = false;
	}

	private final class SwitchAnimation implements Runnable {

		@Override
		public void run() {
			if (!mAnimating)
				{
					return;
				}
			doAnimation();
			FrameAnimationController.requestAnimationFrame(this);
		}
	}

	private void doAnimation() {
		mAnimationPosition += mAnimatedVelocity * FrameAnimationController.ANIMATION_FRAME_DURATION / 1000;
		if (mAnimationPosition <= mBtnOnPos) {
			stopAnimation();
			mAnimationPosition = mBtnOnPos;
			setCheckedDelayed(true);
		} else if (mAnimationPosition >= mBtnOffPos) {
			stopAnimation();
			mAnimationPosition = mBtnOffPos;
			setCheckedDelayed(false);
		}
		moveView(mAnimationPosition);
	}

	private void moveView(float position) {
		mBtnPos = position;
		mRealPos = getRealPos(mBtnPos);
		invalidate();
	}
}
