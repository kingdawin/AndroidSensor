/*
 *@author Dawin,2015-1-30 
 */
package com.hy2014.phonesafer.activity;

import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.utils.LogUtil;

import android.os.Bundle;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 混合模式设置
 * 
 * @author DaWin
 * 重力感应模式，口袋模式，USB 任意模式组合
 */
public class SettingModelActivity extends BaseActivity
{
	private CheckBox cbAcceleration;
	private CheckBox cbPocket;
	private CheckBox cbUsb;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_model_setting);
		findViewById();
		
		sp = getSharedPreferences(FILE_NAME, 1);
		editor = sp.edit();
		setChecked();
		setListener();		
	}

	public void findViewById()
	{
		cbUsb = (CheckBox) findViewById(R.id.cb_usb);
		cbAcceleration = (CheckBox) findViewById(R.id.cb_acceleration);
		cbPocket = (CheckBox) findViewById(R.id.cb_pocket);
	}

	public void setListener()
	{
		cbUsb.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				isUsb = isChecked;
				LogUtil.i("[ModelSettingActivity] isUsb=" + isUsb);
			}
		});

		cbAcceleration.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{

				isAcceleration = isChecked;
				LogUtil.i("[ModelSettingActivity] isAcceleration=" + isAcceleration);
			}
		});

		cbPocket.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{

				isPocket = isChecked;
				LogUtil.i("[ModelSettingActivity] isPocket=" + isPocket);
			}
		});

	}
	public void setChecked(){
		cbAcceleration.setChecked(isAcceleration);
		cbPocket.setChecked(isPocket);
		cbUsb.setChecked(isUsb);
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		editor.putBoolean("isUsb", isUsb);
		editor.putBoolean("isAcceleration", isAcceleration);
		editor.putBoolean("isPocket", isPocket);
		editor.commit();
	}
}
