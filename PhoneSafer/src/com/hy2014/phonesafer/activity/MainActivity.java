package com.hy2014.phonesafer.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import com.dd.loadlingButton.CircularProgressButton;
import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.broadcast.AdminReceiver;
import com.hy2014.phonesafer.gestureLock.GestureLockSettingActivity;
import com.hy2014.phonesafer.gestureLock.GesturUnlockActivity;
import com.hy2014.phonesafer.service.AppService;
import com.hy2014.phonesafer.service.AppService.AppBinder;
import com.hy2014.phonesafer.utils.ActivityUtil;
import com.hy2014.phonesafer.utils.LogUtil;
import com.hy2014.phonesafer.utils.ToastUtil;
import com.hy2014.phonesafer.view.CheckSwitchButton;
import com.hy2014.phonesafer.view.SlideMenu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager.KeyguardLock;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;

import android.view.KeyEvent;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import android.widget.TextView;
import android.widget.Toast;

/**
 * 一系列开关和侧滑设置
 * @author Dawin 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressWarnings("deprecation")
@SuppressLint({ "HandlerLeak", "Wakelock" })
public class MainActivity extends BaseActivity
{	
	//
	/** 传感器管理器 */
	private SensorManager sensorManager;
	/** 重力加速度传感器*/
	private Sensor accelrometerSensor;
	/** 距离传感器*/
	private Sensor proximitySensor;
	/** 光感应传感器*/
	private Sensor lightSensor;

	/** 控制音量的对象 */
	private AudioManager mAudioManager;
	/**true:使音量监听线程关闭*/
	private boolean mIsDestroy = false;
	
	/**铃声位置*/
	private int ringPosition;
	/**警报位置*/
	private int alarmPosition;
	
	/** 放系统警报音 */
	private MediaPlayer mMediaPlayerSysAlarm;
	/**用于获取系统警报铃声uri*/
	private RingtoneManager mRingtoneManagerSysAlarmUri;
	/** 播放系统提示音*/
	private RingtoneManager mRingtoneManagerSysRing;
	/** 放App(raw文件)警报音 */
	private MediaPlayer mMediaPlayerAlarmApp;	
	/**放App(raw文件)提示音*/
	private MediaPlayer mMediaPlayerRingApp;	
	
	/** 播放状态（1:start 2:pause,complete ）*/
	private  int mediaPlayState;
	/** 最大音量 */
	private int maxVolume;
	private int maxRingVolume;

	/**铃声路径*/ 
	private Uri alarmUri;
	
	/*-----------------开关--------------*/
	/** 警报开关 */
	private CheckSwitchButton alarmSwitch;
	/**振动开关*/
	private CheckSwitchButton vibrateSwitch;
	/**灯光开关*/
	private CheckSwitchButton lightSwitch;
	/**手势开关*/ 
	private CheckSwitchButton gestureSwitch;
	/**延迟报警*/
	private CheckSwitchButton delayAlarmSwitch;	
	/**声音开关*/
	private CheckSwitchButton volumeSwitch;	
	
	private TextView tvtVibrateSwitch;
	private TextView tvtAlarmSwitch;
	private TextView tvtLightSwitch;	
	/**显示警报状态*/
	private TextView tvInfo;
	
	/**警报状态按钮(准备启动、已启动、关闭)*/
	private CircularProgressButton cBtnAlarmState;
	
	/**广播 */
	private PowerChangeBroadcast powerChangeBroadcast = new PowerChangeBroadcast();
	
	/**唤醒(必须代码注册)*/ 
	//private static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
	/**待机(必须代码注册)*/ 
	//private static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
	/**耳机拔插(必须代码注册)*/ 
	private static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
	
	//设备管理器广播组件名
	private ComponentName componentName;
	
	private AppBinder binder;
	/**用来控制设备振动*/
	private Vibrator vibrator;
	
	/*-------------------------闪光灯--------------------------*/
	private Camera mCamera;
	private Camera.Parameters mParameters;
	
	/**
	 * 当前音量
	 */
	private int currentVolume;

	private Thread volumeChangeThread;
	private int currentRingVolume;
	private static final int CHANGEVALUE = 1;
	/**设备管理*/
	private DevicePolicyManager manager;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
	    sp = getSharedPreferences(FILE_NAME, 1);
	    editor = sp.edit();
		
		//设备管理器
		devicePolicyManager();		
		//绑定控件
		findViewById();
		// 控制警报铃声
		mRingtoneManagerSysAlarmUri = new RingtoneManager(context);
		mRingtoneManagerSysAlarmUri.getCursor();
		mRingtoneManagerSysRing = new RingtoneManager(context);
		// 提示音
		mRingtoneManagerSysRing.setType(RingtoneManager.TYPE_NOTIFICATION);
		mRingtoneManagerSysRing.getCursor();
		
		/*-----------------获取xml文件数据-----------------*/
		getDataFromFile();
		// 手势解锁
		if (isGestureLock)
			{
				gestureLock();
			}
		// 控制振动的对象
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		/*------------------绑定服务--------------*/
		Intent service = new Intent(context, AppService.class);
		bindService(service, connServiceConnection, BIND_AUTO_CREATE);
		/*---------------初始化闪光灯---------------*/
		initFlashLight();
		// 未启动警报时，隐藏提示文字
		tvInfo.setVisibility(View.GONE);
		gestureSwitch.setChecked(isGestureLock);
		lightSwitch.setChecked(isLight);
		delayAlarmSwitch.setChecked(isDelayAlarm);
		alarmSwitch.setChecked(false);
		vibrateSwitch.setChecked(isVibrate);
		volumeSwitch.setChecked(isVolume);
		setTextVibrate();
		setTextAlarmSwitch();
		// 传感器
		sensorManager();
		// 初始化多媒体
		initMediaPlayer();
		// 音量管理
		volueManager();
		// 线程监听音量:进制音量减
		onVolumeChangeListener();
		setSwitchListener();
		moreSetting();
		// 警报状态按钮
		cBtnAlarmState.setIndeterminateProgressMode(true);
	}

	/**
	 * 设备管理器
	 */
	public void devicePolicyManager()
	{
		// 获得设备管安全理服务
		manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(this, AdminReceiver.class);
		// 申请权限
		adminActive = manager.isAdminActive(componentName);
		// 判断该组件是否有系统管理员的权限
		if (!adminActive)
			{ // 构造意图
				Intent intent = new Intent();
				// 指定添加系统外设的动作名称
				intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				// 指定给哪个组件授权
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
				startActivity(intent);
			}
	}

	/** 初始化闪光灯*/
	public void initFlashLight()
	{
		try
			{
				mCamera = Camera.open();
				mParameters = mCamera.getParameters();
			} catch (Exception e)
			{
				Toast.makeText(context, "闪光灯无法使用 错误：" + e.getMessage(), 1).show();
			}
	}
	
	/**
	 * 
	 * 获取xml文件保存的数据
	 * 
	 */
	public void getDataFromFile()
	{
		//sp = getSharedPreferences(FILE_NAME, 1);
		//editor = sp.edit();		
		
		isVibrate = sp.getBoolean("isVibrate", true);
		isLight = sp.getBoolean("isLight", false);
		isGestureLock = sp.getBoolean("isGestureLock", false);
		isDelayAlarm = sp.getBoolean("isDelayAlarm", false);
		isVolume = sp.getBoolean("isVolume", true);
		lockScreenPassword = sp.getString("lockScreenPassword", lockScreenPassword);
		shakeAngle = sp.getInt("degree", 20);
		// 混合模式
		isPocket = sp.getBoolean("isPocket", true);
		isUsb = sp.getBoolean("isUsb", false);
		isAcceleration = sp.getBoolean("isAcceleration", false);
		delayOpenTime = sp.getInt("delay_open", 5);
	}	

	public void findViewById()
	{
		tvtVibrateSwitch = (TextView) findViewById(R.id.tv_vibrate);
		tvtAlarmSwitch = (TextView) findViewById(R.id.btn_alarm_switch);
		tvInfo = (TextView) findViewById(R.id.tv_info);
		/*---开关---*/
		// 警报开关
		alarmSwitch = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton);
		// 闪光灯开关
		lightSwitch = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton_light);
		// 手势开关
		gestureSwitch = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton_gesture_lock);
		// 延迟报警开关
		delayAlarmSwitch = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton_delay_alarm);
		/* 振动开关 */
		vibrateSwitch = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton_vibrate);
		/* 声音开关 */
		volumeSwitch = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton_volume);
		// 警报状态按钮
		cBtnAlarmState = (CircularProgressButton) findViewById(R.id.cbtn_alarm_state);
	}

	/**
	 * 注册开关事件
	 */
	public void setSwitchListener()
	{
		// 延迟报警开关监听
		delayAlarmSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
		// 手势解锁开关监听
		gestureSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
		lightSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
		//警报开关监听
		alarmSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
		vibrateSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
		volumeSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
	}

	private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener()
	{

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			switch (buttonView.getId())
			{
			// 警报开关
			case R.id.mCheckSwithcButton:
				if (isChecked)
					{
						LogUtil.d("[MainActivity]mCsbAlarm isChecked");
						// openSingleMode();
						openMultiMode();
					} else
					{
						LogUtil.d("[MainActivity] !isChecked");
						tvInfo.setText("已关闭警报");
						attemptClose = true;
						closeAlarm();
						setTextAlarmSwitch();
					}
				break;
			// 闪光灯开关
			case R.id.mCheckSwithcButton_light:
				isLight = isChecked;
				editor.putBoolean("isLight", isLight);
				editor.commit();
				// 正在报警，闪光灯
				if (isAlarming)
					{
						setFlashlightEnabled(isChecked);
						handler.sendEmptyMessage(FLASH_LIGHT);
					}
				break;
			// 手势开关
			case R.id.mCheckSwithcButton_gesture_lock:
				// 开启解锁，但没有设置解锁手势，提示用户设置手势
				if ("null".equals(password) && isChecked)
					{
						Intent intent = new Intent(context, GestureLockSettingActivity.class);
						startActivity(intent);
						buttonView.setChecked(false);
					} else
					{
						isGestureLock = isChecked;
						editor.putBoolean("isGestureLock", isGestureLock);
						editor.commit();
					}
				break;
			// 延迟报警开关
			case R.id.mCheckSwithcButton_delay_alarm:
				// 打开解锁开关，但没有设置解锁手势，提示用户设置手势
				if ("null".equals(password) && isChecked)
					{
						Intent intent = new Intent(context, GestureLockSettingActivity.class);
						startActivity(intent);
						buttonView.setChecked(false);
					} else
					{
						isDelayAlarm = isChecked;
						editor.putBoolean("isDelayAlarm", isChecked);
						editor.commit();
					}
				
				break;
			/* 振动开关 */
			case R.id.mCheckSwithcButton_vibrate:
				isVibrate = isChecked;
				editor.putBoolean("isVibrate", isVibrate);
				editor.commit();
				setTextVibrate();
				break;
			/* 声音开关 */
			case R.id.mCheckSwithcButton_volume:
				isVolume = isChecked;
				editor.putBoolean("isVolume", isVolume);
				editor.commit();
				break;
			}
		}
	};
	/**
	 * 3混合模式 create:2015-01-30
	 */
	public void openMultiMode()
	{
		attemptClose = false;
		tempDelayTime = delayOpenTime;
		// 口袋模式与其他模式处理方式不同
		if (isPocket)
			{
				isInPocket = false;
				if (proximitySensor == null)
					return;
				sensorManager.registerListener(mProximityListenerMulti, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
		// 加速度，usb
		cBtnAlarmState.setProgress(CircularProgressButton.INDETERMINATE_STATE_PROGRESS);
		handler.sendEmptyMessage(READ_SECOND_OTHER_MULTI_MODE);
	}
	/*公司的具体位置*/
	
	/**
	 * 保存设置的传感器
	 */
	public void saveSensorID()
	{
		editor.putInt("sensorTypeId", sensorTypeId);
		editor.commit();
	}
	public void setTextVibrate()
	{
		tvtVibrateSwitch.setText(isVibrate ? "振动启动" : "振动关闭");
	}

	public void setTextAlarmSwitch()
	{
		tvtAlarmSwitch.setText(isOpen ? "警报开启" : "警报关闭");
	}

	public void setTextLightSwitch()
	{
		tvtLightSwitch.setText(lightSwitch.isChecked() ? "闪光开启" : "闪光关闭");
	}


	/**
	 * 持续监听音量变量
	 * 
	 * 当前音量改变时，自动调到最大-1
	 * 
	 */
	public void onVolumeChangeListener()
	{		
		currentRingVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
		currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		volumeChangeThread = new Thread()
		{
			public void run()
			{
				while (!mIsDestroy)
					{
						if (isOpen)
							{
								// LogUtil.e("onVolumeChangeListener" );
								int count = 0;// =2: 按音量+
								//boolean isDerease = false;
								try
									{
										Thread.sleep(20);
									} catch (InterruptedException e)
									{
										LogUtil.e("[MainActivity]onVolumeChangeListener Thread.sleep(20) " + e.getMessage());
									}
								if (currentVolume < mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
									{
										count++;
									//	LogUtil.e("音乐音量+");
										currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
										mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume - CHANGEVALUE,
												AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
									}
								if (currentVolume > mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
									{
									//	LogUtil.e("音乐音量-");
										count++;
										currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
										mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume - CHANGEVALUE,
												AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
										if (count == 1)
											{
												// LogUtil.w("按了一次音量-");
											//	isDerease = true;
											}
									}

								if (currentRingVolume < mAudioManager.getStreamVolume(AudioManager.STREAM_RING))
									{
										count++;
										//LogUtil.e("铃声音量+");
										currentRingVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
										mAudioManager.setStreamVolume(AudioManager.STREAM_RING, maxRingVolume - CHANGEVALUE,
												AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
									}
								if (currentRingVolume > mAudioManager.getStreamVolume(AudioManager.STREAM_RING))
									{
										count++;
									//	LogUtil.e("铃声音量-");
										currentRingVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
										mAudioManager.setStreamVolume(AudioManager.STREAM_RING, maxRingVolume - CHANGEVALUE,
												AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
										if (count == 1)
											{
												// LogUtil.w("按了一次音量-");
											//	isDerease = true;
											}
										
										
									}
								/*if (count == 2)
									{
										LogUtil.w("按了一次音量+");
										handler.sendEmptyMessage(VOLUME_LISTENER);
									} else if (isDerease)
									{
										LogUtil.w("按了一次音量-");
										handler.sendEmptyMessage(VOLUME_LISTENER);
									}*/
							}
					}
			};
		};
		volumeChangeThread.start();
	}

	ServiceConnection connServiceConnection = new ServiceConnection()
	{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			// get Binder
			binder = (AppBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{

		}
	};

	/**
	 * 音量管理
	 * 
	 */
	public void volueManager()
	{
		// 获得AudioManager对象
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = /* 1 */mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		maxRingVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
		mAudioManager.setStreamVolume(AudioManager.STREAM_RING, maxRingVolume - CHANGEVALUE,
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume - CHANGEVALUE,
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}
	
	private long tempDelayTime;	
	/*---------------------------------Handler------------------------------------*/
	/** 闪烁间隔，单位ms */
	private static long FLASH_SPEED = 500;
    /**成功启动警报*/
	private static final int OPEN_SUCCESS = 1;
	private static final int READ_SECOND_PRESS_BUTTON=2;
	private static final int READ_SECOND_OTHER_MULTI_MODE=8;
    /**音量监听*/
	private static final int VOLUME_LISTENER = 4;
	/**闪光灯 */
	private static final int FLASH_LIGHT = 5;
	private static final int VIBRATOR=7;
	
	/** 闪光灯开关：让闪光灯闪烁*/
	private boolean isFlashOpen;
	// 接收子线程消息，将消息放到消息队列
	private final Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{			
			switch (msg.what)
			{
			// 启动警报
			case OPEN_SUCCESS:
				LogUtil.i("[MainActivity] handler 已开启保护");
				alarmSwitch.setChecked(true);
				ring();
				setTextAlarmSwitch();
				displayInfo();
				break;			
			case READ_SECOND_OTHER_MULTI_MODE:				
				//USB/重力感应/光感
				if (tempDelayTime > 0)
					{
						tvInfo.setVisibility(View.VISIBLE);
						// 读秒
						tvInfo.setTextColor(Color.GREEN);
						tvInfo.setText(tempDelayTime + " 秒后启动");
						handler.sendEmptyMessageDelayed(READ_SECOND_OTHER_MULTI_MODE, 1000);
						//cBtnAlarmState.setProgress(CircularProgressButton.INDETERMINATE_STATE_PROGRESS);
						
						tempDelayTime--;
					} else
					{
						isOpen = true;
						//口袋模式启动失败。
						if(!isPocket){
							ring();
						}
						if(isPocket&&(isUsb||isAcceleration))
							{
								ring();
							}
						
						// 其他模式，启动传感器
						if (isUsb)
							{								
								currentSensorID = USB;
							}
						if (isAcceleration)
							{
								// currentSensorID = ACCELEROMERER;
								registerAccelrometer();
							}
						
						// 口袋模式超过指定时间未放入口袋，注销传感器
						if (isPocket && !isInPocket)
							{
								LogUtil.i("[MainActivity] isPocket unregister mProximityListenerMulti");
								sensorManager.unregisterListener(mProximityListenerMulti);// TODO:并且注销加速度、usb?
								if (isAcceleration || isUsb)
									{
										// 5秒内未放入口袋,取消监听
										isAlarming = false;
										isInPocket = false;
									}
								// 只有口袋模式需要启动
								else
									{
										// 5秒内未放入口袋,取消监听
										isOpen = false;
										// 提示音，提示已解除警报
										isAlarming = false;
										isInPocket = false;
										currentSensorID = -1;
										alarmSwitch.setChecked(false);
									}
							}
						displayInfo();
						if(isOpen)
							{
								lockScreen();
							}
					}				
				break;
			/*case VOLUME_LISTENER:
				// 连击是每次点击的时间间隔都在连击的有效范围内
				// 不符合时，有可能是连击的开始，否则就仅仅是单击
				long secondTime = System.currentTimeMillis();
				// 判断每次点击的事件间隔是否符合连击的有效范围
				// 不符合时，有可能是连击的开始，否则就仅仅是单击
				if (secondTime - firstTimeVolume <= 500)
					{
						++count;
					} else
					{
						count = 1;
					}
				// 延迟，用于判断用户的点击操作是否结束
				delay(500);
				firstTimeVolume = secondTime;
				break;*/
			case FLASH_LIGHT:
				if (isOpen && isLight)
					{
						setFlashlightEnabled(isFlashOpen);
						isFlashOpen = !isFlashOpen;						
						handler.sendEmptyMessageDelayed(FLASH_LIGHT, FLASH_SPEED);
					}				
				break;
			case VIBRATOR:
				if(isAlarming)
					{
						vibrate();
					}				
				break;				
			case READ_SECOND_PRESS_BUTTON:
						if (count == 1)
							{
								LogUtil.d("[MainActivity] 单击事件");
							} else if (count > 1)
							{
								LogUtil.d("[MainActivity] 连续点击事件，共点击了 " + count + " 次");
								if (count == unLockCount)
									{
										LogUtil.w("[MainActivity] 解除警报");
										closeAlarm();
										Intent intent=new Intent(ACTION_FINISH_GESTURELOCK_SCREEN);
										sendBroadcast(intent);
									}
							}
						delayTimer.cancel();
						count = 0;					
				break;
			}			
		}
	};

	/**
	 * 注册
	 */
	public void registerAccelrometer()
	{
		if (accelrometerSensor == null)
			return;
		sensorManager.registerListener(accelerometerListener, accelrometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void registerLight()
	{
		if (lightSensor == null)
			return;
		sensorManager.registerListener(lightListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void registerProximit()
	{
		if (proximitySensor == null)
			return;
		sensorManager.registerListener(mProximityListenerMulti, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	/**
	 * 点击事件
	 * 
	 * @param view
	 */
	public void clickView(View view)
	{
		Intent intent;
		switch (view.getId())
		{
		case R.id.linLayout_more_setting:

			if (moreSettingSlideMenu.isMainScreenShowing())
				{
					moreSettingSlideMenu.openMenu();
				} else
				{
					moreSettingSlideMenu.closeMenu();
				}

			break;

		case R.id.btn_ring_setting:
			intent = new Intent(context, SelectRingTypeActivity.class);
			startActivity(intent);
			break;

		case R.id.btn_password_setting:
			// 手势解锁
			intent = new Intent(context, GestureLockSettingActivity.class);
			startActivity(intent);
			break;
		// 设置重力感应
		case R.id.btn_speed_value_setting:
			intent = new Intent(context, SettingXYAngleActivity .class);
			startActivity(intent);
			break;
		// 延迟时间设置
		case R.id.btn_delay_time_setting:
			intent = new Intent(context, SettingDelayTimeActivity.class);
			startActivity(intent);
			break;
			//设置模式
		case R.id.btn_model_setting:
			intent = new Intent(context, SettingModelActivity.class);
			startActivity(intent);
			break;
		}
	}

	/** 当前启动的传感器ID */
	private int currentSensorID;

	/** 解除警报 */
	public void closeAlarm()
	{
		LogUtil.i("[MainActivity] closeAlarm()");
		if (isOpen)
			{		
		        LogUtil.w("[MainActivity] closeAlarm() sensorTypeId=" + sensorTypeId);
				isOpen = false;
				//关闭警报声
				if(alarmPosition<0){
					releaseMediaPlayerAlarmApp();
				}else {
					if (mediaPlayState == 1)
						{
							pause();
						}
				}			
				//提示已解除警报
				ring();
				isAlarming = false;
				closeMultiMode();				
		
				if (isAcceleration)
					{
						sensorManager.unregisterListener(accelerometerListener);
					}
				if (isPocket)
					{
						sensorManager.unregisterListener(mProximityListenerMulti);
					}			
				
				currentSensorID = -1;
				setFlashlightEnabled(false);
				alarmSwitch.setChecked(false);	
				
			} else
			{
				
				handler.removeMessages(READ_SECOND_OTHER_MULTI_MODE);	
			}

		displayInfo();
	}

	/**
	 * 关闭多模式
	 */
	public void closeMultiMode()
	{
	    if(isUsb){currentSensorID = -1;}
		if (isAcceleration)
			{
				sensorManager.unregisterListener(accelerometerListener);
			}
		if (isPocket)
			{
				sensorManager.unregisterListener(mProximityListenerMulti);
			}
		handler.removeMessages(READ_SECOND_OTHER_MULTI_MODE);
	}

	/**
	 * 提示音、振动 <br>
	 * 
	 * 启动或解除警报时,播放提示音、振动
	 * 
	 * */
	public void ring()
	{
		LogUtil.i("[MainActivity] ring()");
		try
			{				
				play();
				// 振动
				vibrate(1000);
			} catch (Exception e)
			{
				Toast.makeText(context, "[MainActivity] ring() error:" + e.getMessage(), 1).show();
			}
	}

	/**
	 * 播放系统铃声
	 * @param ringPosition 铃声Id
	 */
	public void play(int ringPosition)
	{
		LogUtil.i("[MainActivity]    play(int ringPosition)");
		try
			{			
				//跟随系统
				if (ringPosition == 0)
					{
						mRingtoneManagerSysRing.getRingtone(0).play();
						return;
					}
				mRingtoneManagerSysRing.getRingtone(ringPosition - 1).play();
			} catch (Exception e)
			{
				LogUtil.e("[MainActivity] play(int ringPosition) error:"+e.getMessage());
			}	
	}

	/**
	 * 播放提示音
	 */
	public void play()
	{
		LogUtil.e("ringPosition="+ringPosition);
		if (ringPosition >= 0)
			{
				play(ringPosition);
			} else
			{
				releaseMediaPlayerRing();
				//App铃声
				mMediaPlayerRingApp = MediaPlayer.create(context, rawRingResId[Math.abs(ringPosition) - 1]);
				mMediaPlayerRingApp.start();
			}
	}
	/**
	 * 打开警报
	 * 说明：
	 * 启动手势解锁，播放报警声
	 */
	public void openAlarm()
	{
		//清除handler消息
		handler.removeMessages(READ_SECOND_OTHER_MULTI_MODE);	
		isAlarming = true;
		mediaPlayState = 1;				
		/*手势界面*/
		binder.dialog();	
	
	}

	/**
	 * 手机震动
	 * @param time 手机震动时间，单位ms
	 */
	private void vibrate(final int time)
	{
		if (isVibrate)
			{
				Thread thread=new Thread(){
					public void run() {
						//Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
						// 第一个｛｝里面是节奏数组，
						// 第二个参数是重复次数，-1为不重复，非-1则从pattern的指定下标开始重复
						// mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);
						vibrator.vibrate(time);
					};
				};
				thread.start();			
			}
	}
	
	/**
	 * 手机震动直到报警解除
	 * @param time 手机震动时间，单位ms
	 */
	private void vibrate()
	{		
		if (isVibrate)
			{
				Thread thread=new Thread(){
					@Override
					public void run()
					{
						  //Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
						// 第一个｛｝里面是节奏数组，
						// 第二个参数是重复次数，-1为不重复，非-1则从pattern的指定下标开始重复
						// mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);		
						vibrator.vibrate(new long[] {200, 200,100}, -1);
						handler.sendEmptyMessageDelayed(VIBRATOR,550/*500+200+500+200*/);						
					}
				};
			  	thread.start();
			}
			
	}
	
	/**
	 * 初始化MediaPlayer
	 */
	public void initMediaPlayer()
	{
		mMediaPlayerSysAlarm = new MediaPlayer();
		mMediaPlayerSysAlarm.setAudioStreamType(AudioManager.STREAM_RING);
		mMediaPlayerSysAlarm.setOnCompletionListener(completionListener);			
	}

	/**
	 * 播放完毕后，重新播放
	 */
	private OnCompletionListener completionListener = new OnCompletionListener()
	{
		@Override
		public void onCompletion(MediaPlayer mp)
		{
			if (mediaPlayState == 1)
				{
					mMediaPlayerSysAlarm.start();
				}
		}
	};
	
	/**
	 * 管理传感器的方法
	 */
	public void sensorManager()
	{
		// 获取传感器管理器
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// 检查手机支持的传感器
		// 从传感器管理器中获得全部的传感器列表
		List<Sensor> allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		// 显示每个传感器的具体信息
		for (Sensor s : allSensors)
			{
				String tempString = "\n" + " 设备名称：" + s.getName() + "\n" + " 设备版本：" + s.getVersion() + "\n" + " 供应商："
						+ s.getVendor() + "\n";
				switch (s.getType())
				{
				case Sensor.TYPE_ACCELEROMETER:
					accelrometerSensor = s;
					LogUtil.i(s.getType() + " 加速度传感器accelerometer" + tempString);
					break;
				case Sensor.TYPE_PROXIMITY:
					LogUtil.i(s.getType() + " 距离传感器proximity" + tempString);
					proximitySensor = s;
					break;
				}
			}
	}


	/*----------------------------------------速度阀值------------------------------*/
	/**速度的阈值，当摇晃速度达到这值后产生作用*/ 
	private static final int SPEED_SHRESHOLD = 2000;//3000

	// 两次检测的时间间隔
	private static final int UPTATE_INTERVAL_TIME = 50;
	// 手机上一个位置的重力感应坐标
	private float lastX;
	private float lastY;
	private float lastZ;
	// 上次检测时间
	private long lastUpdateTime;

	/**
	 * 手机被快速移动时候，报警
	 * @param event
	 * @return
	 */
	public boolean speedThreshold(SensorEvent event)
	{
		// 现在检测时间
		long currentUpdateTime = System.currentTimeMillis();
		// 两次检测的时间间隔
		long timeInterval = currentUpdateTime - lastUpdateTime;
		// 判断是否达到了检测时间间隔
		if (timeInterval < UPTATE_INTERVAL_TIME)
			return false;
		// 现在的时间变成last时间
		lastUpdateTime = currentUpdateTime;

		// 获得x,y,z坐标
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		// 获得x,y,z的变化值
		float deltaX = x - lastX;
		float deltaY = y - lastY;
		float deltaZ = z - lastZ;

		// 将现在的坐标变成last坐标
		lastX = x;
		lastY = y;
		lastZ = z;

		double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;
		LogUtil.i("speed=" + speed);
		// 达到速度阀值，发出提示
		if (speed >SPEED_SHRESHOLD)
			{
				return true;
			}
		return false;
	}
	/**
	 * 加速度感应监听
	 * 
	 * 说明： X表示左右移动的加速度 Y表示前后移动的加速度 Z表示垂直方向的加速度
	 * 
	 * 
	 * Sensor.TYPE_ACCELEROMETER获取的加速度实际上是手机运动的加速度与重力加速度的合加速度。 静止状态:<br>
	 * value[0]是手机坐标系x轴与桌面的夹角，<br>
	 * 实际加速度在x轴上的分量减去重力加速度在z轴上的分量
	 * value[1]是手机坐标系y轴与桌面的夹角，<br>
	 * value[2]是手机坐标系z轴与桌面的夹角。<br>
	 * */
	private SensorEventListener accelerometerListener = new SensorEventListener()
	{
		@Override
		public void onSensorChanged(SensorEvent event)
		{	
			//手机x,y夹角是否超过安全区
			checkDeviceSafeScope(event);		
		}	 		
		// 反应速度变化
		// Accuracy:精确值
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
			LogUtil.i("acceleromererListener  accuracy =" + accuracy);
		}
	};


	
	// 计算手机与水平的角度
	// 如何把当前加速度的值转化为当前角度值呢？这需要一定的硬件基础才能明白其中的原理，不懂得同学可以看一些加速度传感器方面的书，关于加速度
	// 传感器还有很多应用，比如速度的测量，位移的测量，这就需要更加复杂的算法了，这里就不再介绍
	private int toDegree(float zz)
	{
		// 首先判断加速度的值是否大于10，小于-10，这是因为在运动过程中加速是不稳定的，而我们要测的是在静止状态下的稳定值。
		if (zz > 10)
			{
				zz = 10;
			} else if (zz < -10)
			{
				zz = -10;
			}

		// acos（zz / 10）就能求出倾斜角度的弧度值。 Math.acos(1)=0
		// Math.acos(-1)=3.1415->degree=179
		double r = Math.acos(zz / 10);
		// 然后将弧度值转化为角度值
		int degree = (int) (r * 180 / Math.PI);
		// 最后返回一个String
		return degree;
	}

	/**
	 * x角度 [0,90] 
	 * 右侧高+ 
	 * 左侧高-
	 * 屏幕朝下无数据 
	 * @param xx
	 * @return
	 */
	private int toDegreeX(float xx)
	{
		if (xx > 10)
			{
				xx = 10;
			} else if (xx < -10)
			{
				xx = -10;
			}
		double r = Math.asin(xx / 10);
		int degree = (int) (r * 180 / Math.PI);
		return degree;
	}

	/**y角度
	 * [0,90]
	 * 顶部高+
	 * 底部高-
	 * 屏幕朝下无数据 
	 * @param yy
	 * @return
	 */
	private int toDegreeY(float yy)
	{
		// 首先判断加速度的值是否大于10，小于-10，这是因为在运动过程中加速是不稳定的，而我们要测的是在静止状态下的稳定值。
		if (yy > 10)
			{
				yy = 10;
			} else if (yy < -10)
			{
				yy = -10;
			}
		
		// acos（zz / 10）就能求出倾斜角度的弧度值。    Math.acos(1)=0  Math.acos(-1)=3.1415->degree=179
		double r = Math.asin(yy / 10);
		// 然后将弧度值转化为角度值
		int degree = (int) (r * 180 / Math.PI);
		// 最后返回一个String
		return degree;
	}	

	private float[] gravity = new float[3];
	// 手机实际的加速度
	private int[] linear_acceleration = new int[3];
	// alpha is calculated as t / (t + dT)
	// with t, the low-pass filter's time-constant
	// and dT, the event delivery rate
	private final float ALPHA = 0.8F;

	/**
	 * 消除重力
	 * 
	 * @param event
	 *            消除重力，计算手机实际加速度
	 */
	public void avoidGavity(SensorEvent event)
	{
		gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
		gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
		gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

		linear_acceleration[0] = (int) (event.values[0] - gravity[0]);
		linear_acceleration[1] = (int) (event.values[1] - gravity[1]);
		linear_acceleration[2] = (int) (event.values[2] - gravity[2]);
	}

	// 需要刷新的数据放到onResume方法
	@Override
	protected void onResume()
	{
		super.onResume();
		ringPosition = sp.getInt("ring", -1);
		LogUtil.v("[MainActivity]ringPosition  " + ringPosition);
	    //图形密码
		password = sp.getString("password", "null");
		// 设置解锁手势
		if ("null".equals(password))
			{
				Intent intent = new Intent(context, GestureLockSettingActivity.class);
				startActivity(intent);
			}
		
		//报警铃声Id
		alarmPosition = sp.getInt("ringAlarm", 1);
		LogUtil.v("[MainActivity]ringAlarm  " + alarmPosition);			
		alarmUri = alarmPosition==0?mRingtoneManagerSysAlarmUri.getRingtoneUri(0):mRingtoneManagerSysAlarmUri.getRingtoneUri(alarmPosition - 1);
		registerBroadcast();		
		//设置加速度警报的安全区域
		settedX=sp.getInt("degreeX", 0);
		settedY=sp.getInt("degreeY", 0);
		safeScodeHighX = settedX + shakeAngle > 180 ? (settedX + shakeAngle - 180) : (settedX + shakeAngle);
		safeScodeLowX = settedX - shakeAngle < 0 ? 180 + settedX - shakeAngle : settedX - shakeAngle;
		safeScodeHighY = settedY + shakeAngle > 180 ? (settedY + shakeAngle - 180) : (settedY + shakeAngle);
		safeScodeLowY = settedY - shakeAngle < 0 ? 180 + settedY - shakeAngle : settedY - shakeAngle;
		//App手势解锁
		if (isAlarming)
			{
				LogUtil.w("[onResume] 正在报警");
			}
		if (isHome && isGestureLock)
			{
				gestureLock();
			}
	}	
	
	/**显示手势解锁界面*/ 
	public void gestureLock()
	{		
		Intent intent = new Intent(context, GesturUnlockActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 注册一些列广播
	 */
	public void registerBroadcast()
	{
		IntentFilter filter = new IntentFilter();
		// 充电断开
		filter.addAction(Intent.ACTION_POWER_DISCONNECTED);		
		// 耳机拔插
		filter.addAction(ACTION_HEADSET_PLUG);
		// 关闭警报
		filter.addAction(ACTION_CLOSE_ALARM);
		// 打开警报
		filter.addAction(ACTION_OPEN_ALARM);
		registerReceiver(powerChangeBroadcast, filter);
	}

	public void pause()
	{
		mediaPlayState = 2;
		mMediaPlayerSysAlarm.pause();
	}
	/**
	 * 是否插入耳机线
	 * 
	 * 说明： 拔插是成对出现的， 如果没有插入，不提示断开
	 */
	private boolean isIn;

	/**
	 * 
	 * 广播
	 * 
	 * 接收： 关闭、打开警报广播 耳机拔插广播 电源断开广播
	 */
	public class PowerChangeBroadcast extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			// if(isOpen){
			// 耳机拔插
			if (ACTION_HEADSET_PLUG.equals(action))
				{
					LogUtil.i("HEADSET_PLUG");
					if (intent.hasExtra("state"))
						{
							if (intent.getIntExtra("state", 0) == 0 && isIn)
								{
									isIn = false;
									Toast.makeText(context, "headset not connected", Toast.LENGTH_LONG).show();
									LogUtil.i("HEADSET_PLUG");
									if (isOpen && currentSensorID == USB)
									// 断开连接，报警
										{
											Toast.makeText(context, "断开连接，报警", Toast.LENGTH_SHORT).show();
											// 打开警报
											openAlarm();
										}									
								} else if (intent.getIntExtra("state", 0) == 1)
								{
									isIn = true;
									ToastUtil.makeText(context, "headset connected");
								}
						}
				}else
			// 充电断开
			if (Intent.ACTION_POWER_DISCONNECTED.equals(action))
				{
					LogUtil.i("ACTION_POWER_DISCONNECTED");
					if (isOpen && currentSensorID == USB)
					// 断开连接，报警
						{
							Toast.makeText(context, "断开连接，报警", Toast.LENGTH_SHORT).show();
							// 打开警报
							openAlarm();
						}
				}
			if (ACTION_CLOSE_ALARM.equals(action))
				{
					closeAlarm();
				}
			if (ACTION_OPEN_ALARM.equals(action))
				{
					isOpen = true;
					// 声音开关
					if (isVolume)
						{
							mediaPlayState = 1;
							isAlarming = true;
							try
								{
									if (alarmPosition < 0)
										{
											releaseMediaPlayerAlarmApp();
											mMediaPlayerAlarmApp = MediaPlayer.create(context,
													rawAlarmResId[Math.abs(alarmPosition + 1)]);
											mMediaPlayerAlarmApp.setLooping(true);
											mMediaPlayerAlarmApp.start();
										} else
										{
											mMediaPlayerSysAlarm.reset();
											mMediaPlayerSysAlarm.setDataSource(context, alarmUri);
											mMediaPlayerSysAlarm.prepare();
											mMediaPlayerSysAlarm.start();
										}

								} catch (Exception e)
								{
									LogUtil.e("[MainActivity]广播  播放铃声出错error： " + e.getMessage());
								}
						}

					// 闪光灯开关
					if (isLight)
						{
							isFlashOpen = true;
							handler.sendEmptyMessageDelayed(FLASH_LIGHT, FLASH_SPEED);
						}
					vibrate();
				}			
		}
	}

	// 延迟时间是连击的时间间隔有效范围
/*	private void delay(long interval)
	{
		if (task != null)
			task.cancel();

		task = new TimerTask()
		{
			@Override
			public void run()
			{
				LogUtil.w("时钟线程，监听电源按键");
				handler.sendEmptyMessage(READ_SECOND_PRESS_BUTTON);
			}
		};
		delayTimer = new Timer();
		// 第一个参数为执行的mTimerTask
		// 第二个参数为延迟得事件，这里写1000得意思是 mTimerTask将延迟1秒执行
		// 第三个参数为多久执行一次，这里写1000 表示没1秒执行一次mTimerTask的Run方法
		delayTimer.schedule(task, interval);
	}
*/
	private int count = 0;
	private long firstTimeVolume = 0;
	private Timer delayTimer;
	private TimerTask task;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		LogUtil.e("[MainActivity] onKeyDown  keyCode=" + keyCode);
		return super.onKeyDown(keyCode, event);
		// true：按键无效
	}
	
	
	/** 是否放到口袋 */
	private boolean isInPocket;
	
	/** 距离监听 */
	private SensorEventListener mProximityListenerMulti = new SensorEventListener()
	{
		public void onSensorChanged(SensorEvent event)
		{
			// 手机和物体的距离
			float distance = event.values[0];
			LogUtil.e("distance=" + distance);
			// 放入口袋
			if (distance == 0 && isInPocket == false)
				{
					isInPocket = true;
					if (isAcceleration || isUsb)
						{
							//TODO:is that right?
							isOpen=true;
						} else
						{
							isOpen=true;
							// 口袋模式启动，其他模式必定也成功启动了
							alarmSwitch.setChecked(true);
							handler.removeMessages(READ_SECOND_OTHER_MULTI_MODE);
							setTextAlarmSwitch();
							displayInfo();
							ring();
							lockScreen();
						}
					return;
				}
			if(!isInPocket)return;
			// 放入口袋后
			if (isOpen && distance >= 1)
				{
					openAlarm();
					sensorManager.unregisterListener(mProximityListenerMulti);
					isInPocket = false;
				}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
			
		}
	};
	
	/*--------------------感光传感器-----------------*/
	// 上一次光度
	private float lastLightValue;
	/**
	 * 距离监听
	 * 
	 * 检测到手机和人体的距离
	 */
	private SensorEventListener lightListener = new SensorEventListener()
	{
		public void onSensorChanged(SensorEvent event)
		{
			LogUtil.i("lightValue [0]=" + event.values[0]);
			if (isOpen)
				{
					if (Math.abs(lastLightValue - event.values[0]) > 30)
						{
							lastLightValue = event.values[0];
							openAlarm();
							// 启动警报后，注销监听。防重复启动
							sensorManager.unregisterListener(lightListener);
						}
				}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
			LogUtil.i("lightListener  accuracy =" + accuracy);
		}
	};

	/*------------------提示信息---------------------*/
	private String state = "未启动";

	/** 显示当前状态 */
	public void displayInfo()
	{			
		state = isOpen ? "已启动" : "未启动";
		int color = isOpen ? Color.RED : Color.BLACK;
		
	    int stateIndex = isOpen ? CircularProgressButton.SUCCESS_STATE_PROGRESS : CircularProgressButton.IDLE_STATE_PROGRESS;
		cBtnAlarmState.setProgress(stateIndex);
		
		tvInfo.setVisibility(isOpen ? View.VISIBLE : View.GONE);
		tvInfo.setTextColor(color);
		StringBuffer modeName = new StringBuffer();
		if (isInPocket)
			{
				modeName.append("口袋模式  ");
			}
		if (isUsb&&currentSensorID==USB)
			{
				modeName.append("USB模式  ");
			}
		if (isAcceleration)
			{
				modeName.append("重力感应模式  ");
			}
	
		tvInfo.setText(state+" "+modeName.toString());
	}
	
	public void displayInfo(String sensorName)
	{
		state = isOpen ? "已启动" : "未启动";
		int stateIndex = isOpen ? CircularProgressButton.SUCCESS_STATE_PROGRESS : CircularProgressButton.IDLE_STATE_PROGRESS;
		cBtnAlarmState.setProgress(stateIndex);
		int color = isOpen ? Color.RED : Color.BLACK;
		tvInfo.setTextColor(color);
		tvInfo.setText(state + " " + sensorName);
		
	}

	

	/**
	 * 设置闪光灯的开启和关闭
	 * 
	 * @param isEnable
	 *            是否打开闪光灯
	 * 
	 * @author Dawin
	 * 
	 * @date 2015-01-02
	 */
	private void setFlashlightEnabled(boolean isEnable)
	{
		if (isEnable)
			{
				try
					{
						LogUtil.d("flash open");
						mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
						mCamera.setParameters(mParameters);
						mCamera.startPreview();
					} catch (Exception ex)
					{
						LogUtil.e("[MainActivity] setFlashlightEnabled error:" + ex.getMessage());
					}
			} else
			{
				try
					{
						LogUtil.d("flash close");
						mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
						mCamera.setParameters(mParameters);
						mCamera.stopPreview();
					} catch (Exception ex)
					{
						LogUtil.e("[MainActivity] setFlashlightNotEnabled error:" + ex.getMessage());
					}
			}
	}

	/**
	 * 释放闪光灯资源
	 */
	public void destroyedCamera()
	{
		if (mCamera != null)
			{
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
	}

	/*--------------------------------更多设置-----------------------------------------*/

	private ArrayAdapter<String> adapterPower;
	private ArrayAdapter<String> adapterSensor;
	private SlideMenu moreSettingSlideMenu;

	/**
	 * 更多设置
	 */
	public void moreSetting()
	{
		moreSettingSlideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		// 将可选内容与ArrayAdapter连接起来
		adapterSensor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sensorType);		
		adapterPower = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, powerCount);
		// 设置下拉列表的风格
		adapterSensor.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		adapterPower.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 设置默认值,数据的第一个元素
		setDefaultSensor();
		
	}

	/**
	 * 设置下拉框默认的传感器 从文件拿currentSensorID字段
	 */
	public void setDefaultSensor()
	{
		sensorTypeId = getSensorID();
	}

	/**
	 * 获取设置的传感器
	 */
	public int getSensorID()
	{
		int defValue = PROXIMITY;
		return sp.getInt("sensorTypeId", defValue);
	}	
	/*---------------------------------onDestroy----------------------------------*/
	protected void onDestroy()
	{
		super.onDestroy();		
		// 使监听音量键线程停止
		mIsDestroy = true;
		LogUtil.i("注销传感器监听");		
		if (mRingtoneManagerSysAlarmUri != null)
			{
				mRingtoneManagerSysAlarmUri.getCursor().close();
			}	
		
		if (mRingtoneManagerSysRing!= null)
			{
				mRingtoneManagerSysRing.getCursor().close();
			}	
		
		releaseMediaPlayerAlarm();
		releaseMediaPlayerAlarmApp();	
		releaseMediaPlayerRing();		
		sensorManager.unregisterListener(accelerometerListener);
		sensorManager.unregisterListener(lightListener);		
		unregisterReceiver(powerChangeBroadcast);
		try
			{
				unbindService(connServiceConnection);		
			} catch (Exception e)
			{
				LogUtil.e("解绑服务错误 error:"+e.getMessage());
			}	
		// 释放闪光灯资源
		destroyedCamera();
		saveSensorID();
		// 关闭所有Activity
		ActivityUtil.getInstance().exit();	
	};
	
	public void releaseMediaPlayerAlarm()
	{
		if (mMediaPlayerSysAlarm != null)
			{
				mMediaPlayerSysAlarm.release();
				mMediaPlayerSysAlarm = null;
			}
	}

	public void releaseMediaPlayerAlarmApp()
	{
		if (mMediaPlayerAlarmApp != null)
			{
				mMediaPlayerAlarmApp.release();
				mMediaPlayerAlarmApp = null;
			}
	}
	

	public void releaseMediaPlayerRing()
	{
		if (mMediaPlayerRingApp != null)
			{
				mMediaPlayerRingApp.release();
				mMediaPlayerRingApp = null;
			}
	}

	/**
	 * 手机x,y夹角是否超过安全区。
	 * 超过安全区时启动警报。
	 * @param event
	 */
	public void checkDeviceSafeScope(SensorEvent event)
	{
		//消除重力加速度
		avoidGavity(event);
		// 保证加速度=0，手机处于缓慢移动。才能得到正确的角度值
		if (linear_acceleration[2] != 0 || linear_acceleration[1] != 0 || linear_acceleration[0] != 0)
			{
				return;
			}

		/*--------------------------计算手机当前x,y轴夹角--------------------------*/
		int z = (int) event.values[2];
		int currentDegreeX = toDegreeX(event.values[0]);
		int currentDegreeY = toDegreeY(event.values[1]);
		// 角度转换成0-180
		currentDegreeX = SettingXYAngleActivity.xAngleTo180(currentDegreeX, z);
		currentDegreeY = SettingXYAngleActivity.yAngleTo180(currentDegreeY, z);
		// x安全区域和当前夹角对比
		if (!isSafe(currentDegreeX, true))
			{
				openAlarm();
				sensorManager.unregisterListener(accelerometerListener);
				return;
			}

		// y安全区域和当前夹角对比
		if (!isSafe(currentDegreeY, false))
			{
				openAlarm();
				sensorManager.unregisterListener(accelerometerListener);
				return;
			}
	}

	/**
	 * 夹角是否在安全区间
	 * 
	 * @param currentDegree
	 *            当前夹角
	 * @param isCheckX
	 *            true:检查X轴，false:检查Y轴
	 * @return true:安全
	 */
	public boolean isSafe(int currentDegree, boolean isCheckX)
	{
		LogUtil.i("isCheckX?  "+isCheckX);
		int safeScodeHigh = isCheckX ? safeScodeHighX : safeScodeHighY;
		int safeScodeLow = isCheckX ? safeScodeLowX : safeScodeLowY;
		if (safeScodeHigh > safeScodeLow)
			{
				if (currentDegree > safeScodeLow && currentDegree < safeScodeHigh)
					{
						LogUtil.i("safe");
						return true;
					} else
					{
						return false;
					}
			}
		if (currentDegree > safeScodeHigh && currentDegree < safeScodeLow)
			{
				return false;
			} else
			{
				LogUtil.i("safe");
				return true;
			}
	}


	
	public void lockScreen()
	{
		if (adminActive)
			{
				//立刻锁屏
				manager.lockNow();
			} else
			{
				LogUtil.i("[MainActivity] 未激活设备管理器");
				Intent intent = new Intent(); // 构造意图
				intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN); // 指定添加系统外设的动作名称
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName); // 指定给哪个组件授权
				startActivity(intent);
			}
	}
	/**
	 * 模拟Home键
	 *//*
	public void homeKey()
	{
		isHome=true;
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}*/
}