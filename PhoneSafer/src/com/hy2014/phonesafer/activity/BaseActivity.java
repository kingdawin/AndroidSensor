package com.hy2014.phonesafer.activity;

import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.utils.ActivityUtil;



import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


/**
 * Activity的基类
 * 
 * @author Dawin
 * 
 * 所有Activity都继承此类
 * 
 */
public class BaseActivity extends Activity
{

	/**
	 * 设备管理器是否被激活
	 */
	public static  boolean adminActive;

	/**设置的x与水平面夹角*/	
	public static int settedX;
	/**设置的y与水平面夹角*/
	public static int settedY;
	
	//x,y夹角安全区间
	public static int safeScodeHighY ;
	public static int safeScodeLowY;
	public static int safeScodeHighX ;
	public static int safeScodeLowX;
	
	/** 上下文对象:指整个程序的环境 */
	public Context context;
	/** 手势图形密码 */
	public static String password;

	/*---------------------必须同时添加资源ID和资源名字!---------------------*/
	/** raw提示音文件id */
	public static final int[] rawRingResId = new int[] { R.raw.blipblip};
	/** raw提示音文件名 */
	public static final String[] ringAppDatas = new String[] { "blipblip"};
	
	/** raw警报音文件id */
	public static final int[] rawAlarmResId = new int[] { R.raw.alarmsound};
	/** raw警报音文件名 */	
	public static final String[] alarmAppDatas=new String[]{"alarmsound"};
	/*---------------------------------------------------------------*/

	/** 是否允许全屏 */
	public static boolean mAllowFullScreen = false;
	/** 正在报警 */
	public static boolean isAlarming;
	// public static boolean isLockScreen;
	/** ---------------------全局变量------------------------ */
	/** 用来保存设置参数 */
	public SharedPreferences sp;
	public SharedPreferences.Editor editor;

	/** true:启动警报。false:解除警报 */
	public static boolean isOpen;
	//public static boolean isOpenPocket;
	/** 闪光灯 */
	public static boolean isLight;
	/** 振动开关 */
	public static boolean isVibrate;
	/** 手势解锁 */
	public static boolean isGestureLock;
	/** 延迟报警 */
	public static boolean isDelayAlarm;
	/**声音开关*/
	public static boolean isVolume;
	/**抖动范围 阀值 */
	public static int speedValue = 20;
	/** 延迟报警 */
	public static int delayOpenTime = 5;// 默认值5s
	/** 按Home键 */
	public static boolean isHome;
	/** 5秒时间内，是否企图关闭保护 */
	public static boolean attemptClose;

	public static final String FILE_NAME = "ring";

	/*-----------------------------------*/
	public static final String[] sensorType = { "口袋模式"/* "距离感应" */,/* "USB感应" */"USB模式", "重力感应"/*
																							 * ,
																							 * "光感模式"
																							 */};
	
	public static final String[] powerCount = { "连按2次解锁", "连击3次解锁", "连击4次解锁" };
	/** 连击n次来解锁 */
	public static int unLockCount = 2;

	/*---------------------------------自定义的一系列广播---------------------------------*/
	public static final String ACTION_FINISH_GESTURELOCK_SCREEN = "com.hy2014.phonesafer.finish_gesturelock_screen";
	public static final String ACTION_OPEN_ALARM = "com.hy2014.phonesafer.open_alarm";
	public static final String ACTION_CLOSE_ALARM = "com.hy2014.phonesafer.close_alarm";

	/** 传感器ID */
	public static final int PROXIMITY = 0;
	public static final int USB = 1;
	public static final int ACCELEROMERER = 2;
	//public static final int LIGHT = 3;
	/** 0:"距离感应",1:"USB感应",2:"重力感应" */
	public static int sensorTypeId = PROXIMITY;// 默认距离感应

	/** 系统解锁密码 */
	public static String lockScreenPassword = "123";// 默认123系统解锁密码
	
	/**
	 * 抖动角度
	 */
	public static int shakeAngle =35;

	/**-------------------组合模式--------------*/	
	public static boolean isAcceleration;
	public static boolean isPocket;
	public static boolean isUsb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
		context = this;
		// 将Activity加入List容器
		ActivityUtil.getInstance().addActivity(this);
		// 去标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 竖屏锁定
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// 是否全屏
		if (mAllowFullScreen)
			{
				requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
			}
		// 隐藏软键盘
		hideSoftInputView();	  
	}

	/**
	 * 隐藏软键盘
	 * 
	 * 打开有EditText的界面时，阻止键盘自动弹出
	 */
	public void hideSoftInputView()
	{
		InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
			{
				if (getCurrentFocus() != null)
					manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
	}

}