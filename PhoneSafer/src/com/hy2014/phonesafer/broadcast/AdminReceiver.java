/*
 *@author Dawin,2015-1-8
 *
 *
 *
 */
package com.hy2014.phonesafer.broadcast;

import com.hy2014.phonesafer.activity.BaseActivity;
import com.hy2014.phonesafer.utils.LogUtil;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * 设备管理广播
 * @author Dawin
 * 
 * 接收激活/关闭设备管理器的广播
 * 
 */
public class AdminReceiver extends DeviceAdminReceiver
{
	
	@Override
	public DevicePolicyManager getManager(Context context)
	{
		LogUtil.d("------" + "getManager" + "------");
		return super.getManager(context);
	}

	@Override
	public ComponentName getWho(Context context)
	{
		LogUtil.d("------" + "getWho" + "------");
		return super.getWho(context);
	}	

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent)
	{
		LogUtil.d("------" + "onDisableRequested" + "------");
		return super.onDisableRequested(context, intent);
	}
    //设备管理器激活
	@Override
	public void onEnabled(Context context, Intent intent)
	{
		LogUtil.d("------" + "onEnabled" + "------");		
		BaseActivity.adminActive=true;
		super.onEnabled(context, intent);
	}
	/**
	 * 禁用
	 */
	@Override
	public void onDisabled(Context context, Intent intent)
	{
		LogUtil.d("------" + "onDisabled" + "------");
		super.onDisabled(context, intent);
	}
	
	@Override
	public void onPasswordChanged(Context context, Intent intent)
	{
		LogUtil.d("------" + "onPasswordChanged" + "------");
		super.onPasswordChanged(context, intent);
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent)
	{
		LogUtil.d("------" + "onPasswordFailed" + "------");
		super.onPasswordFailed(context, intent);
	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent)
	{
		LogUtil.d("------" + "onPasswordSucceeded" + "------");
		super.onPasswordSucceeded(context, intent);
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		LogUtil.d("------" + "onReceive" + "------");

		super.onReceive(context, intent);
	}

	@Override
	public IBinder peekService(Context myContext, Intent service)
	{
		LogUtil.d("------" + "peekService" + "------");
		return super.peekService(myContext, service);
	}
}
