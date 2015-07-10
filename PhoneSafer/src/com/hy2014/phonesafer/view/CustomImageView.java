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
 * 圆角图片，圆形图片
 * 
 * @author zhy
 * 
 * 说明
 * 实现的方法是：
 * 圆形和图片的交集就构成了圆形的图片：SRC_IN模式。
 * 设置画笔：
 * paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
 * 先画一个圆形
 * 然后用画笔画图片
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
	 * 图片
	 */
	private Bitmap mBitmap;

	/**
	 * 圆角的大小
	 */
	private int mRadius;

	/**
	 * 控件的宽度
	 */
	private int mWidth;
	/**
	 * 控件的高度
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
	 * 初始化一些自定义的参数
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
        //获取自定义的属性,自定义的属性在attr文件中
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyle, 0);

		int n = a.getIndexCount();
		for (int i = 0; i < n; i++)
		{
			int attr = a.getIndex(i);
			switch (attr)
			{
				//declare-styleable的name值_属性名
			case R.styleable.CustomImageView_src:
				mBitmap = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
				break;
			case R.styleable.CustomImageView_type:
				type = a.getInt(attr, 0);// 默认为Circle
				break;
			case R.styleable.CustomImageView_borderRadius:
				type = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,
						getResources().getDisplayMetrics()));// 默认为10DP
				break;
			}
		}
		a.recycle();
	}

	/**
	 * 计算控件的高度和宽度
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		/**
		 * 设置宽度
		 */
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);

		if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
			{
				mWidth = specSize;
			} else
			{
				// 由图片决定的宽
				int desireByImg = getPaddingLeft() + getPaddingRight() + mBitmap.getWidth();
				if (specMode == MeasureSpec.AT_MOST)// wrap_content
					{
						mWidth = Math.min(desireByImg, specSize);
					}
			}

		/***
		 * 设置高度
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
	 * 绘制
	 */
	@Override
	protected void onDraw(Canvas canvas)
	{

		switch (type)
		{
		// 如果是TYPE_CIRCLE绘制圆形
		case TYPE_CIRCLE:

			int min = Math.min(mWidth, mHeight);
			/**
			 * 长度如果不一致，按小的值进行压缩
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
	 * 根据原图和变长绘制圆形图片
	 * 
	 * @param source
	 * @param min
	 * @return
	 * 核心代码
	 */
	private Bitmap createCircleImage(Bitmap source, int min)
	{
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
		/**
		 * 产生一个同样大小的画布
		 */
		Canvas canvas = new Canvas(target);
		/**
		 * 首先绘制圆形
		 */
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		/**
		 * 使用SRC_IN，参考上面的说明
		 * 
		 * 
		 * 其实主要靠：paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		 * 这行代码，为什么呢，
		 * SRC_IN这种模式，两个绘制的效果叠加后取交集展现后图，
		 * 怎么说呢，咱们第一个绘制的是个圆形，第二个绘制的是个Bitmap，于是交集为圆形，展现的是BItmap，就实现了圆形图片效果。
		 * 圆角，其实就是先绘制圆角矩形，是不是很简单，以后别人再说实现圆角，你就把这一行代码给他就行了。
		 */
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		/**
		 * 绘制图片
		 */
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

	/**
	 * 原理同上
	 * 
	 * 根据原图添加圆角
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
		//圆角矩形
		canvas.drawRoundRect(rect, 50f, 50f, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		//用画笔将图片source画成圆角矩形图片
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}
}
