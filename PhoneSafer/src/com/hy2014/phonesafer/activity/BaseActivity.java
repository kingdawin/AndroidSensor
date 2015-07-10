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
 * Activity�Ļ���
 * 
 * @author Dawin
 * 
 * ����Activity���̳д���
 * 
 */
public class BaseActivity extends Activity
{

	/**
	 * �豸�������Ƿ񱻼���
	 */
	public static  boolean adminActive;

	/**���õ�x��ˮƽ��н�*/	
	public static int settedX;
	/**���õ�y��ˮƽ��н�*/
	public static int settedY;
	
	//x,y�нǰ�ȫ����
	public static int safeScodeHighY ;
	public static int safeScodeLowY;
	public static int safeScodeHighX ;
	public static int safeScodeLowX;
	
	/** �����Ķ���:ָ��������Ļ��� */
	public Context context;
	/** ����ͼ������ */
	public static String password;

	/*---------------------����ͬʱ�����ԴID����Դ����!---------------------*/
	/** raw��ʾ���ļ�id */
	public static final int[] rawRingResId = new int[] { R.raw.blipblip};
	/** raw��ʾ���ļ��� */
	public static final String[] ringAppDatas = new String[] { "blipblip"};
	
	/** raw�������ļ�id */
	public static final int[] rawAlarmResId = new int[] { R.raw.alarmsound};
	/** raw�������ļ��� */	
	public static final String[] alarmAppDatas=new String[]{"alarmsound"};
	/*---------------------------------------------------------------*/

	/** �Ƿ�����ȫ�� */
	public static boolean mAllowFullScreen = false;
	/** ���ڱ��� */
	public static boolean isAlarming;
	// public static boolean isLockScreen;
	/** ---------------------ȫ�ֱ���------------------------ */
	/** �����������ò��� */
	public SharedPreferences sp;
	public SharedPreferences.Editor editor;

	/** true:����������false:������� */
	public static boolean isOpen;
	//public static boolean isOpenPocket;
	/** ����� */
	public static boolean isLight;
	/** �񶯿��� */
	public static boolean isVibrate;
	/** ���ƽ��� */
	public static boolean isGestureLock;
	/** �ӳٱ��� */
	public static boolean isDelayAlarm;
	/**��������*/
	public static boolean isVolume;
	/**������Χ ��ֵ */
	public static int speedValue = 20;
	/** �ӳٱ��� */
	public static int delayOpenTime = 5;// Ĭ��ֵ5s
	/** ��Home�� */
	public static boolean isHome;
	/** 5��ʱ���ڣ��Ƿ���ͼ�رձ��� */
	public static boolean attemptClose;

	public static final String FILE_NAME = "ring";

	/*-----------------------------------*/
	public static final String[] sensorType = { "�ڴ�ģʽ"/* "�����Ӧ" */,/* "USB��Ӧ" */"USBģʽ", "������Ӧ"/*
																							 * ,
																							 * "���ģʽ"
																							 */};
	
	public static final String[] powerCount = { "����2�ν���", "����3�ν���", "����4�ν���" };
	/** ����n�������� */
	public static int unLockCount = 2;

	/*---------------------------------�Զ����һϵ�й㲥---------------------------------*/
	public static final String ACTION_FINISH_GESTURELOCK_SCREEN = "com.hy2014.phonesafer.finish_gesturelock_screen";
	public static final String ACTION_OPEN_ALARM = "com.hy2014.phonesafer.open_alarm";
	public static final String ACTION_CLOSE_ALARM = "com.hy2014.phonesafer.close_alarm";

	/** ������ID */
	public static final int PROXIMITY = 0;
	public static final int USB = 1;
	public static final int ACCELEROMERER = 2;
	//public static final int LIGHT = 3;
	/** 0:"�����Ӧ",1:"USB��Ӧ",2:"������Ӧ" */
	public static int sensorTypeId = PROXIMITY;// Ĭ�Ͼ����Ӧ

	/** ϵͳ�������� */
	public static String lockScreenPassword = "123";// Ĭ��123ϵͳ��������
	
	/**
	 * �����Ƕ�
	 */
	public static int shakeAngle =35;

	/**-------------------���ģʽ--------------*/	
	public static boolean isAcceleration;
	public static boolean isPocket;
	public static boolean isUsb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
		context = this;
		// ��Activity����List����
		ActivityUtil.getInstance().addActivity(this);
		// ȥ������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ��������
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// �Ƿ�ȫ��
		if (mAllowFullScreen)
			{
				requestWindowFeature(Window.FEATURE_NO_TITLE); // ȡ������
			}
		// ���������
		hideSoftInputView();	  
	}

	/**
	 * ���������
	 * 
	 * ����EditText�Ľ���ʱ����ֹ�����Զ�����
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