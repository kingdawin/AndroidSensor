/*
 *@author Dawin,2015-1-8
 *
 *
 *
 */
package com.hy2014.phonesafer.activity;

import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.broadcast.AdminReceiver;
import com.hy2014.phonesafer.utils.LogUtil;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 未用
 * @author Dawin
 *
 */
public class DeviceManagerActivity extends Activity implements OnClickListener
{
	
    //flex2 no advertisment
	private DevicePolicyManager policyManager;
	private ComponentName componentName;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 获取设备管理服务
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		// AdminReceiver 继承自 DeviceAdminReceiver
		componentName = new ComponentName(this, AdminReceiver.class);
		
		init();
	}

	private void init()
	{
		Button active =(Button)new Object();
		Button unactive = (Button)new Object();
		Button syslock = (Button)new Object();

		active.setOnClickListener(this);
		unactive.setOnClickListener(this);
		syslock.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.action_settings:
			activeManage();
			break;
		case R.id.tv_delay_alarm:
			unActiveManage();
			break;
		case R.id.tv_info:
			systemLock();
			break;
		default:
			break;
		}
	}

	/**
	 * 激活设备管理权限 成功执行激活时，DeviceAdminReceiver中的 onEnabled 会响应
	 */
	private void activeManage()
	{
		// 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

		// 权限列表
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);

		// 描述(additional explanation)
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "------ 其他描述 ------");

		startActivityForResult(intent, 0);
	}

	/**
	 * 禁用设备管理权限 成功执行禁用时，DeviceAdminReceiver中的 onDisabled 会响应
	 */
	private void unActiveManage()
	{
		LogUtil.d("------ unActiveManage ------");
		boolean active = policyManager.isAdminActive(componentName);
		if (active)
			{
				policyManager.removeActiveAdmin(componentName);
			}
	}

	/**
	 * 调出系统锁
	 */
	private void systemLock()
	{
		LogUtil.d("------ Lock Screen ------");
		boolean active = policyManager.isAdminActive(componentName);
		//设备管理器已经激活。可以锁屏
		if (active)
			{
				policyManager.lockNow();
			}
	}
}