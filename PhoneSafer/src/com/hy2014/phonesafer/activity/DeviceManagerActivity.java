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
 * δ��
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

		// ��ȡ�豸�������
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		// AdminReceiver �̳��� DeviceAdminReceiver
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
	 * �����豸����Ȩ�� �ɹ�ִ�м���ʱ��DeviceAdminReceiver�е� onEnabled ����Ӧ
	 */
	private void activeManage()
	{
		// �����豸����(��ʽIntent) - ��AndroidManifest.xml���趨��Ӧ������
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

		// Ȩ���б�
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);

		// ����(additional explanation)
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "------ �������� ------");

		startActivityForResult(intent, 0);
	}

	/**
	 * �����豸����Ȩ�� �ɹ�ִ�н���ʱ��DeviceAdminReceiver�е� onDisabled ����Ӧ
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
	 * ����ϵͳ��
	 */
	private void systemLock()
	{
		LogUtil.d("------ Lock Screen ------");
		boolean active = policyManager.isAdminActive(componentName);
		//�豸�������Ѿ������������
		if (active)
			{
				policyManager.lockNow();
			}
	}
}