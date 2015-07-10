package com.hy2014.phonesafer.view;



import com.hy2014.phonesafer.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;

import android.util.TypedValue;
import android.view.View;

/**
 * Բ��ͼƬ��Բ��ͼƬ
 * 
 * @author zhy
 * 
 * ˵��
 * ʵ�ֵķ����ǣ�
 * Բ�κ�ͼƬ�Ľ����͹�����Բ�ε�ͼƬ��SRC_INģʽ��
 * ���û��ʣ�
 * paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
 * �Ȼ�һ��Բ��
 * Ȼ���û��ʻ�ͼƬ
 */
public class CustomImageView extends View
{

	
	
	
	/**
	 * TYPE_CIRCLE / TYPE_ROUND
	 */
	private int type;
	private static final int TYPE_CIRCLE = 0;
	private static final int TYPE_ROUND = 1;

	/**
	 * ͼƬ
	 */
	private Bitmap mBitmap;

	/**
	 * Բ�ǵĴ�С
	 */
	private int mRadius;

	/**
	 * �ؼ��Ŀ��
	 */
	private int mWidth;
	/**
	 * �ؼ��ĸ߶�
	 */
	private int mHeight;

	public CustomImageView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public CustomImageView(Context context)
	{
		this(context, null);
	}

	/**
	 * ��ʼ��һЩ�Զ���Ĳ���
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 * 
	 * 
	 */
	public CustomImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
        //��ȡ�Զ��������,�Զ����������attr�ļ���
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyle, 0);

		int n = a.getIndexCount();
		for (int i = 0; i < n; i++)
		{
			int attr = a.getIndex(i);
			switch (attr)
			{
				//declare-styleable��nameֵ_������
			case R.styleable.CustomImageView_src:
				mBitmap = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
				break;
			case R.styleable.CustomImageView_type:
				type = a.getInt(attr, 0);// Ĭ��ΪCircle
				break;
			case R.styleable.CustomImageView_borderRadius:
				type = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,
						getResources().getDisplayMetrics()));// Ĭ��Ϊ10DP
				break;
			}
		}
		a.recycle();
	}

	/**
	 * ����ؼ��ĸ߶ȺͿ��
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		/**
		 * ���ÿ��
		 */
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);

		if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
			{
				mWidth = specSize;
			} else
			{
				// ��ͼƬ�����Ŀ�
				int desireByImg = getPaddingLeft() + getPaddingRight() + mBitmap.getWidth();
				if (specMode == MeasureSpec.AT_MOST)// wrap_content
					{
						mWidth = Math.min(desireByImg, specSize);
					}
			}

		/***
		 * ���ø߶�
		 */

		specMode = MeasureSpec.getMode(heightMeasureSpec);
		specSize = MeasureSpec.getSize(heightMeasureSpec);
		if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
			{
				mHeight = specSize;
			} else
			{
				int desire = getPaddingTop() + getPaddingBottom() + mBitmap.getHeight();
				if (specMode == MeasureSpec.AT_MOST)// wrap_content
					{
						mHeight = Math.min(desire, specSize);
					}
			}
		setMeasuredDimension(mWidth, mHeight);
	}

	/**
	 * ����
	 */
	@Override
	protected void onDraw(Canvas canvas)
	{

		switch (type)
		{
		// �����TYPE_CIRCLE����Բ��
		case TYPE_CIRCLE:

			int min = Math.min(mWidth, mHeight);
			/**
			 * ���������һ�£���С��ֵ����ѹ��
			 */
			mBitmap = Bitmap.createScaledBitmap(mBitmap, min, min, false);

			canvas.drawBitmap(createCircleImage(mBitmap, min), 0, 0, null);
			break;
		case TYPE_ROUND:
			canvas.drawBitmap(createRoundConerImage(mBitmap), 0, 0, null);
			break;

		}

	}

	/**
	 * ����ԭͼ�ͱ䳤����Բ��ͼƬ
	 * 
	 * @param source
	 * @param min
	 * @return
	 * ���Ĵ���
	 */
	private Bitmap createCircleImage(Bitmap source, int min)
	{
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
		/**
		 * ����һ��ͬ����С�Ļ���
		 */
		Canvas canvas = new Canvas(target);
		/**
		 * ���Ȼ���Բ��
		 */
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		/**
		 * ʹ��SRC_IN���ο������˵��
		 * 
		 * 
		 * ��ʵ��Ҫ����paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		 * ���д��룬Ϊʲô�أ�
		 * SRC_IN����ģʽ���������Ƶ�Ч�����Ӻ�ȡ����չ�ֺ�ͼ��
		 * ��ô˵�أ����ǵ�һ�����Ƶ��Ǹ�Բ�Σ��ڶ������Ƶ��Ǹ�Bitmap�����ǽ���ΪԲ�Σ�չ�ֵ���BItmap����ʵ����Բ��ͼƬЧ����
		 * Բ�ǣ���ʵ�����Ȼ���Բ�Ǿ��Σ��ǲ��Ǻܼ򵥣��Ժ������˵ʵ��Բ�ǣ���Ͱ���һ�д�����������ˡ�
		 */
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		/**
		 * ����ͼƬ
		 */
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

	/**
	 * ԭ��ͬ��
	 * 
	 * ����ԭͼ���Բ��
	 * 
	 * @param source
	 * @return
	 */
	private Bitmap createRoundConerImage(Bitmap source)
	{
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
		//Բ�Ǿ���
		canvas.drawRoundRect(rect, 50f, 50f, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		//�û��ʽ�ͼƬsource����Բ�Ǿ���ͼƬ
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}
}
