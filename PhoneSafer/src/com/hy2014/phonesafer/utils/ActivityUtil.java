package com.hy2014.phonesafer.utils;

import java.util.LinkedList;

import android.app.Activity;
/**
 * Activity管理类
 * 
 * @author Dawin 
 * 
 * SysApplication.getInstance().exit()
 */
public class ActivityUtil 
{
	//初始化活动集合
	private LinkedList<Activity> mList = new LinkedList<Activity>();
	private static ActivityUtil instance;

	private ActivityUtil() {
	}

	// 单列
	public synchronized static ActivityUtil getInstance()
	{
		if (null == instance)
			{
				instance = new ActivityUtil();
			}
		return instance;
	}

	/**添加新的活动*/
	public void addActivity(Activity activity)
	{
		mList.add(activity);
	}

	/**
	 * 移除所有的活动
	 * 退出程序，遍历所有Activity，逐个销毁
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
