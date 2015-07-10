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
 * ���þ����ӳ�ʱ��
 * @author Dawin
 * 
 *����̳и�����޸��˸�����Ϊ����onCreate()������super.����ٴ�ִ���˸��಻��Ҫ�ķ���
 *finalize��Object��ķ������������ռ���ִ��ʱ�������յĶ�����ô˷�����
 */
public class SettingDelayTimeActivity extends BaseActivity
{
	public TextView tvSpeedValue;
	public TextView tvTip;
	/**��ʾ��ʲô����*/
	public String tip;
	// ��ֵ������
	public SeekBar delayTimeSeekBar;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_speedvalue);
		init();
        tip="���þ����ӳ�ʱ��";
        tvTip.setText(tip);
		//��ȡ���õ��ӳ�ʱ��
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
	
    //��������
	@Override
	protected void onDestroy()
	{		
		super.onDestroy();
		editor.putInt("delay_open", (int)delayOpenTime);
		editor.commit();
	}
	
}
