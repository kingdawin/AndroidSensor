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
 * ���أ���iOS
 * 
 * @describe ʹ���˵������� SwitchButton�������������Ե�ע�ͣ������׿���
 * @author https://github.com/Issacw0ng/SwitchButton
 *
 */
public class CheckSwitchButton extends CheckBox {
	/** ���� */
	private Paint mPaint;
	private ViewParent mParent;
	private Bitmap mBottom;
	private Bitmap mCurBtnPic;
	private Bitmap mBtnPressed;
	private Bitmap mBtnNormal;
	/** ������ */
	private Bitmap mFrame;
	/** ��Ӱ�㣬���ܵ����ʱ����ʾ */
	private Bitmap mMask;
	/** ���沼�ֵľ��� */
	private RectF mSaveLayerRectF;
	/** ����֮����㣬�ñȱ����ͱ����ϵ�ͼƬЧ�� */
	private PorterDuffXfermode mXfermode;
	/** �״ΰ��µ�Y */
	private float mFirstDownY;
	/** �״ΰ��µ�X */
	private float mFirstDownX;
	/** ͼƬ�Ļ���λ�� */
	private float mRealPos;
	/** ��ť��λ�� */
	private float mBtnPos;
	/** ���ش򿪵�λ�� */
	private float mBtnOnPos;
	/** ���عرյ�λ�� */
	private float mBtnOffPos;
	/** ��Ӱ�Ŀ�� */
	private float mMaskWidth;
	/** ��Ӱ�ĸ߶� */
	private float mMaskHeight;
	/** ����Բ�ΰ�ť�Ŀ�� */
	private float mBtnWidth;
	/** ���س�ʼ���� */
	private float mBtnInitPos;

	private int mClickTimeout;

	private int mTouchSlop;
	/** ���͸���ȣ����ǲ�͸�� */
	private final int MAX_ALPHA = 255;
	/** ��ǰ͸���ȣ�������Ҫ��������ؼ���enable����Ϊfalseʱ�����ð�͸�� ���������Ե�� */
	private int mAlpha = MAX_ALPHA;

	private boolean mChecked = false;

	private boolean mBroadcasting;

	private boolean mTurningOn;

	private PerformClick mPerformClick;
	/** ����״̬�л������ӿ� */
	private OnCheckedChangeListener mOnCheckedChangeListener;

	private OnCheckedChangeListener mOnCheckedChangeWidgetListener;
	/** �ж��Ƿ��ڽ��ж���  */
	private boolean mAnimating;

	private final float VELOCITY = 350;
	/** �����ٶ� */
	private float mVelocity;

	private final float EXTENDED_OFFSET_Y = 15;
	/** Y�᷽�����������,���������� */
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
		//���µİ�ť
		mBtnPressed = BitmapFactory.decodeResource(resources,R.drawable.checkswitch_btn_pressed);
		//�ſ��İ�ť
		mBtnNormal = BitmapFactory.decodeResource(resources,R.drawable.checkswitch_btn_unpressed);
		mFrame = BitmapFactory.decodeResource(resources, R.drawable.checkswitch_frame);
		mMask = BitmapFactory.decodeResource(resources, R.drawable.checkswitch_mask);
		mCurBtnPic = mBtnNormal;

		mBtnWidth = mBtnPressed.getWidth();
		mMaskWidth = mMask.getWidth();
		mMaskHeight = mMask.getHeight();

		mBtnOffPos = mBtnWidth / 2;
		mBtnOnPos = mMaskWidth - mBtnWidth / 2;
		// �ж���ʼλ��,����趨��mCheckedΪtrue����ʼλ��Ϊ mBtnOnPos
		mBtnPos = mChecked ? mBtnOnPos : mBtnOffPos;
		mRealPos = getRealPos(mBtnPos);
		// density �ܶ�
		final float density = getResources().getDisplayMetrics().density;// �����ǻ�ȡ��Դ�ܶȣ�Density��
		mVelocity = (int) (VELOCITY * density + 0.5f);
		mExtendOffsetY = (int) (EXTENDED_OFFSET_Y * density + 0.5f);
		// ����һ���µľ�����ָ�������ꡣ
		mSaveLayerRectF = new RectF(0, mExtendOffsetY, mMask.getWidth(),
				mMask.getHeight() + mExtendOffsetY);
		mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);// PorterDuff.Mode.SRC_IN
																	// ��������Դ���
																	// ȡ������ƽ�������ʾ�ϲ㡣
	}

	@Override
	public void setEnabled(boolean enabled) {
		mAlpha = enabled ? MAX_ALPHA : MAX_ALPHA / 2;
		super.setEnabled(enabled);
	}

	public boolean isChecked() {
		return mChecked;
	}

	/** �Զ��ж��л����෴������ : true -->false ;false -->true */
	public void toggle() {
		setChecked(!mChecked);
	}

	/**
	 * �ڲ����ô˷�������checked״̬���˷������ӳ�ִ�и��ֻص���������֤������������
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
			// �϶��ŵ�ʱ��
			float time = event.getEventTime() - event.getDownTime();
			// ��ǰ��ť��λ��
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
	 * ֪ͨ���಻Ҫ����touch�¼� Tries to claim the user's drag motion, and requests
	 * disallowing any ancestors from stealing events in the drag.
	 */
	private void attemptClaimDrag() {
		mParent = getParent();
		if (mParent != null) {
			// ֪ͨ���಻Ҫ����touch�¼�
			mParent.requestDisallowInterceptTouchEvent(true);
		}
	}

	/**
	 * ��btnPosת����RealPos
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
		// �����ɰ�
		canvas.drawBitmap(mMask, 0, mExtendOffsetY, mPaint);
		mPaint.setXfermode(mXfermode);

		// ���Ƶײ�ͼƬ
		canvas.drawBitmap(mBottom, mRealPos, mExtendOffsetY, mPaint);
		mPaint.setXfermode(null);
		// ���Ʊ߿�
		canvas.drawBitmap(mFrame, 0, mExtendOffsetY, mPaint);

		// ���ư�ť
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
