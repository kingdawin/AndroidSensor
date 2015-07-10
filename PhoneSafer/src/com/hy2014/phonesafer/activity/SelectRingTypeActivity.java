/*
*@author Dawin,2015-1-5
*
*
*
*/
package com.hy2014.phonesafer.activity;


import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.utils.LogUtil;

import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 
 * 铃声类型选择
 * 
 * @author Dawin
 *来电铃声   “ringtones”（来电铃声） RingtoneManager.TYPE_RINGTONE
 *"alarms”（闹钟铃声）
 *“notifications”（短信通知铃声）
 */
public class SelectRingTypeActivity extends BaseActivity
{	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_ring_setting_select);
	}
	//https://wssb.dgsi.gov.cn/grwscx/
	public void clickView(View view)
	{
		Intent intent = new Intent(context, SettingRingActivity.class);
	
		switch (view.getId())
		{
		// 警报音设置
		case R.id.btn_ring:
			LogUtil.i("btn_ring  " + RingtoneManager.TYPE_RINGTONE);
			intent.putExtra("ringType", RingtoneManager.TYPE_RINGTONE);
			break;
		// 提示音设置
		case R.id.btn_alarm:
			intent.putExtra("ringType", RingtoneManager.TYPE_NOTIFICATION);
			break;
		}
		startActivity(intent);
	}
}
