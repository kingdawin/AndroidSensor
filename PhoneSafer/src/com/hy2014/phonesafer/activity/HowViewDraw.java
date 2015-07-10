/*
 *@author Dawin,2015-1-28
 *
 *
 *
 */
package com.hy2014.phonesafer.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

public class HowViewDraw extends View
{

	public HowViewDraw(Context context) {
		super(context);
	}

	public HowViewDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HowViewDraw(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	//LayoutInflater
	public void getInstanceLayoutInflater(Context context){
		//method 1
		LayoutInflater layoutInflater=LayoutInflater.from(context);
		//method 2
		layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	//LayoutInflater如何工作
	//pull解析xml文件

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//setMeasuredDimension(measuredWidth, measuredHeight)
	}
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		getWidth();
		getHeight();
		//canvas.drawArc(oval, startAngle, sweepAngle, useCenter, paint)
	}
	public void drawView(){
		requestFocus();		
		setPressed(true);
		setEnabled(false);	
	}
}
