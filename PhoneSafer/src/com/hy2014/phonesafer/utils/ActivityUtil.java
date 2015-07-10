package com.hy2014.phonesafer.utils;

import java.util.LinkedList;

import android.app.Activity;
/**
 * Activity������
 * 
 * @author Dawin 
 * 
 * SysApplication.getInstance().exit()
 */
public class ActivityUtil 
{
	//��ʼ�������
	private LinkedList<Activity> mList = new LinkedList<Activity>();
	private static ActivityUtil instance;

	private ActivityUtil() {
	}

	// ����
	public synchronized static ActivityUtil getInstance()
	{
		if (null == instance)
			{
				instance = new ActivityUtil();
			}
		return instance;
	}

	/**����µĻ*/
	public void addActivity(Activity activity)
	{
		mList.add(activity);
	}

	/**
	 * �Ƴ����еĻ
	 * �˳����򣬱�������Activity���������
	 */
	public void exit()
	{
		try
			{
				for (Activity activity : mList)
					{
						if (activity != null)
							activity.finish();
					}
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				System.exit(0);
			}
	}

}
