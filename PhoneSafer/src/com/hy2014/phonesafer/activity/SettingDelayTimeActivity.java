/*
 *@author Dawin,2015-1-10
 *
 *
 *
 */
package com.hy2014.phonesafer.activity;
import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.utils.LogUtil;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * 设置警报延迟时间
 * @author Dawin
 * 
 *子类继承父类后，修改了父类行为，但onCreate()调用了super.结果再次执行了父类不需要的方法
 *finalize是Object类的方法，在垃圾收集器执行时，被回收的对象调用此方法。
 */
public class SettingDelayTimeActivity extends BaseActivity
{
	public TextView tvSpeedValue;
	public TextView tvTip;
	/**提示是什么设置*/
	public String tip;
	// 阀值进度条
	public SeekBar delayTimeSeekBar;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_speedvalue);
		init();
        tip="设置警报延迟时间";
        tvTip.setText(tip);
		//获取设置的延迟时间
		delayOpenTime=sp.getInt("delay_open", 5);
		delayTimeSeekBar.setProgress((int)delayOpenTime);
		delayTimeSeekBar.setMax((int)20);// [0,20]
		tvSpeedValue.setText(delayOpenTime+"");
		delayTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				if (fromUser)
					{
						delayOpenTime = progress;
						tvSpeedValue.setText(progress + "");										
						LogUtil.d("[SettingDelayTimeActivity]" + delayOpenTime);
					}
			}
		});
	}

	public void init()
	{
		sp = getSharedPreferences(FILE_NAME, 1);
		editor = sp.edit();
		delayTimeSeekBar = (SeekBar) findViewById(R.id.seekbar_speedvalue);
		tvSpeedValue = (TextView) findViewById(R.id.tv_speedvalue);
		tvTip = (TextView) findViewById(R.id.tv_tip);
	}
	
    //保存设置
	@Override
	protected void onDestroy()
	{		
		super.onDestroy();
		editor.putInt("delay_open", (int)delayOpenTime);
		editor.commit();
	}
	
}
