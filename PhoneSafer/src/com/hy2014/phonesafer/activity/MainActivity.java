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
 * һϵ�п��غͲ໬����
 * @author Dawin 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressWarnings("deprecation")
@SuppressLint({ "HandlerLeak", "Wakelock" })
public class MainActivity extends BaseActivity
{	
	//
	/** ������������ */
	private SensorManager sensorManager;
	/** �������ٶȴ�����*/
	private Sensor accelrometerSensor;
	/** ���봫����*/
	private Sensor proximitySensor;
	/** ���Ӧ������*/
	private Sensor lightSensor;

	/** ���������Ķ��� */
	private AudioManager mAudioManager;
	/**true:ʹ���������̹߳ر�*/
	private boolean mIsDestroy = false;
	
	/**����λ��*/
	private int ringPosition;
	/**����λ��*/
	private int alarmPosition;
	
	/** ��ϵͳ������ */
	private MediaPlayer mMediaPlayerSysAlarm;
	/**���ڻ�ȡϵͳ��������uri*/
	private RingtoneManager mRingtoneManagerSysAlarmUri;
	/** ����ϵͳ��ʾ��*/
	private RingtoneManager mRingtoneManagerSysRing;
	/** ��App(raw�ļ�)������ */
	private MediaPlayer mMediaPlayerAlarmApp;	
	/**��App(raw�ļ�)��ʾ��*/
	private MediaPlayer mMediaPlayerRingApp;	
	
	/** ����״̬��1:start 2:pause,complete ��*/
	private  int mediaPlayState;
	/** ������� */
	private int maxVolume;
	private int maxRingVolume;

	/**����·��*/ 
	private Uri alarmUri;
	
	/*-----------------����--------------*/
	/** �������� */
	private CheckSwitchButton alarmSwitch;
	/**�񶯿���*/
	private CheckSwitchButton vibrateSwitch;
	/**�ƹ⿪��*/
	private CheckSwitchButton lightSwitch;
	/**���ƿ���*/ 
	private CheckSwitchButton gestureSwitch;
	/**�ӳٱ���*/
	private CheckSwitchButton delayAlarmSwitch;	
	/**��������*/
	private CheckSwitchButton volumeSwitch;	
	
	private TextView tvtVibrateSwitch;
	private TextView tvtAlarmSwitch;
	private TextView tvtLightSwitch;	
	/**��ʾ����״̬*/
	private TextView tvInfo;
	
	/**����״̬��ť(׼�����������������ر�)*/
	private CircularProgressButton cBtnAlarmState;
	
	/**�㲥 */
	private PowerChangeBroadcast powerChangeBroadcast = new PowerChangeBroadcast();
	
	/**����(�������ע��)*/ 
	//private static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
	/**����(�������ע��)*/ 
	//private static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
	/**�����β�(�������ע��)*/ 
	private static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
	
	//�豸�������㲥�����
	private ComponentName componentName;
	
	private AppBinder binder;
	/**���������豸��*/
	private Vibrator vibrator;
	
	/*-------------------------�����--------------------------*/
	private Camera mCamera;
	private Camera.Parameters mParameters;
	
	/**
	 * ��ǰ����
	 */
	private int currentVolume;

	private Thread volumeChangeThread;
	private int currentRingVolume;
	private static final int CHANGEVALUE = 1;
	/**�豸����*/
	private DevicePolicyManager manager;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
	    sp = getSharedPreferences(FILE_NAME, 1);
	    editor = sp.edit();
		
		//�豸������
		devicePolicyManager();		
		//�󶨿ؼ�
		findViewById();
		// ���ƾ�������
		mRingtoneManagerSysAlarmUri = new RingtoneManager(context);
		mRingtoneManagerSysAlarmUri.getCursor();
		mRingtoneManagerSysRing = new RingtoneManager(context);
		// ��ʾ��
		mRingtoneManagerSysRing.setType(RingtoneManager.TYPE_NOTIFICATION);
		mRingtoneManagerSysRing.getCursor();
		
		/*-----------------��ȡxml�ļ�����-----------------*/
		getDataFromFile();
		// ���ƽ���
		if (isGestureLock)
			{
				gestureLock();
			}
		// �����񶯵Ķ���
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		/*------------------�󶨷���--------------*/
		Intent service = new Intent(context, AppService.class);
		bindService(service, connServiceConnection, BIND_AUTO_CREATE);
		/*---------------��ʼ�������---------------*/
		initFlashLight();
		// δ��������ʱ��������ʾ����
		tvInfo.setVisibility(View.GONE);
		gestureSwitch.setChecked(isGestureLock);
		lightSwitch.setChecked(isLight);
		delayAlarmSwitch.setChecked(isDelayAlarm);
		alarmSwitch.setChecked(false);
		vibrateSwitch.setChecked(isVibrate);
		volumeSwitch.setChecked(isVolume);
		setTextVibrate();
		setTextAlarmSwitch();
		// ������
		sensorManager();
		// ��ʼ����ý��
		initMediaPlayer();
		// ��������
		volueManager();
		// �̼߳�������:����������
		onVolumeChangeListener();
		setSwitchListener();
		moreSetting();
		// ����״̬��ť
		cBtnAlarmState.setIndeterminateProgressMode(true);
	}

	/**
	 * �豸������
	 */
	public void devicePolicyManager()
	{
		// ����豸�ܰ�ȫ�����
		manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(this, AdminReceiver.class);
		// ����Ȩ��
		adminActive = manager.isAdminActive(componentName);
		// �жϸ�����Ƿ���ϵͳ����Ա��Ȩ��
		if (!adminActive)
			{ // ������ͼ
				Intent intent = new Intent();
				// ָ�����ϵͳ����Ķ�������
				intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				// ָ�����ĸ������Ȩ
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
				startActivity(intent);
			}
	}

	/** ��ʼ�������*/
	public void initFlashLight()
	{
		try
			{
				mCamera = Camera.open();
				mParameters = mCamera.getParameters();
			} catch (Exception e)
			{
				Toast.makeText(context, "������޷�ʹ�� ����" + e.getMessage(), 1).show();
			}
	}
	
	/**
	 * 
	 * ��ȡxml�ļ����������
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
		// ���ģʽ
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
		/*---����---*/
		// ��������
		alarmSwitch = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton);
		// ����ƿ���
		lightSwitch = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton_light);
		// ���ƿ���
		gestureSwitch = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton_gesture_lock);
		// �ӳٱ�������
		delayAlarmSwitch = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton_delay_alarm);
		/* �񶯿��� */
		vibrateSwitch = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton_vibrate);
		/* �������� */
		volumeSwitch = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton_volume);
		// ����״̬��ť
		cBtnAlarmState = (CircularProgressButton) findViewById(R.id.cbtn_alarm_state);
	}

	/**
	 * ע�Ὺ���¼�
	 */
	public void setSwitchListener()
	{
		// �ӳٱ������ؼ���
		delayAlarmSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
		// ���ƽ������ؼ���
		gestureSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
		lightSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
		//�������ؼ���
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
			// ��������
			case R.id.mCheckSwithcButton:
				if (isChecked)
					{
						LogUtil.d("[MainActivity]mCsbAlarm isChecked");
						// openSingleMode();
						openMultiMode();
					} else
					{
						LogUtil.d("[MainActivity] !isChecked");
						tvInfo.setText("�ѹرվ���");
						attemptClose = true;
						closeAlarm();
						setTextAlarmSwitch();
					}
				break;
			// ����ƿ���
			case R.id.mCheckSwithcButton_light:
				isLight = isChecked;
				editor.putBoolean("isLight", isLight);
				editor.commit();
				// ���ڱ����������
				if (isAlarming)
					{
						setFlashlightEnabled(isChecked);
						handler.sendEmptyMessage(FLASH_LIGHT);
					}
				break;
			// ���ƿ���
			case R.id.mCheckSwithcButton_gesture_lock:
				// ������������û�����ý������ƣ���ʾ�û���������
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
			// �ӳٱ�������
			case R.id.mCheckSwithcButton_delay_alarm:
				// �򿪽������أ���û�����ý������ƣ���ʾ�û���������
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
			/* �񶯿��� */
			case R.id.mCheckSwithcButton_vibrate:
				isVibrate = isChecked;
				editor.putBoolean("isVibrate", isVibrate);
				editor.commit();
				setTextVibrate();
				break;
			/* �������� */
			case R.id.mCheckSwithcButton_volume:
				isVolume = isChecked;
				editor.putBoolean("isVolume", isVolume);
				editor.commit();
				break;
			}
		}
	};
	/**
	 * 3���ģʽ create:2015-01-30
	 */
	public void openMultiMode()
	{
		attemptClose = false;
		tempDelayTime = delayOpenTime;
		// �ڴ�ģʽ������ģʽ����ʽ��ͬ
		if (isPocket)
			{
				isInPocket = false;
				if (proximitySensor == null)
					return;
				sensorManager.registerListener(mProximityListenerMulti, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
		// ���ٶȣ�usb
		cBtnAlarmState.setProgress(CircularProgressButton.INDETERMINATE_STATE_PROGRESS);
		handler.sendEmptyMessage(READ_SECOND_OTHER_MULTI_MODE);
	}
	/*��˾�ľ���λ��*/
	
	/**
	 * �������õĴ�����
	 */
	public void saveSensorID()
	{
		editor.putInt("sensorTypeId", sensorTypeId);
		editor.commit();
	}
	public void setTextVibrate()
	{
		tvtVibrateSwitch.setText(isVibrate ? "������" : "�񶯹ر�");
	}

	public void setTextAlarmSwitch()
	{
		tvtAlarmSwitch.setText(isOpen ? "��������" : "�����ر�");
	}

	public void setTextLightSwitch()
	{
		tvtLightSwitch.setText(lightSwitch.isChecked() ? "���⿪��" : "����ر�");
	}


	/**
	 * ����������������
	 * 
	 * ��ǰ�����ı�ʱ���Զ��������-1
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
								int count = 0;// =2: ������+
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
									//	LogUtil.e("��������+");
										currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
										mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume - CHANGEVALUE,
												AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
									}
								if (currentVolume > mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
									{
									//	LogUtil.e("��������-");
										count++;
										currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
										mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume - CHANGEVALUE,
												AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
										if (count == 1)
											{
												// LogUtil.w("����һ������-");
											//	isDerease = true;
											}
									}

								if (currentRingVolume < mAudioManager.getStreamVolume(AudioManager.STREAM_RING))
									{
										count++;
										//LogUtil.e("��������+");
										currentRingVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
										mAudioManager.setStreamVolume(AudioManager.STREAM_RING, maxRingVolume - CHANGEVALUE,
												AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
									}
								if (currentRingVolume > mAudioManager.getStreamVolume(AudioManager.STREAM_RING))
									{
										count++;
									//	LogUtil.e("��������-");
										currentRingVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
										mAudioManager.setStreamVolume(AudioManager.STREAM_RING, maxRingVolume - CHANGEVALUE,
												AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
										if (count == 1)
											{
												// LogUtil.w("����һ������-");
											//	isDerease = true;
											}
										
										
									}
								/*if (count == 2)
									{
										LogUtil.w("����һ������+");
										handler.sendEmptyMessage(VOLUME_LISTENER);
									} else if (isDerease)
									{
										LogUtil.w("����һ������-");
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
	 * ��������
	 * 
	 */
	public void volueManager()
	{
		// ���AudioManager����
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
	/** ��˸�������λms */
	private static long FLASH_SPEED = 500;
    /**�ɹ���������*/
	private static final int OPEN_SUCCESS = 1;
	private static final int READ_SECOND_PRESS_BUTTON=2;
	private static final int READ_SECOND_OTHER_MULTI_MODE=8;
    /**��������*/
	private static final int VOLUME_LISTENER = 4;
	/**����� */
	private static final int FLASH_LIGHT = 5;
	private static final int VIBRATOR=7;
	
	/** ����ƿ��أ����������˸*/
	private boolean isFlashOpen;
	// �������߳���Ϣ������Ϣ�ŵ���Ϣ����
	private final Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{			
			switch (msg.what)
			{
			// ��������
			case OPEN_SUCCESS:
				LogUtil.i("[MainActivity] handler �ѿ�������");
				alarmSwitch.setChecked(true);
				ring();
				setTextAlarmSwitch();
				displayInfo();
				break;			
			case READ_SECOND_OTHER_MULTI_MODE:				
				//USB/������Ӧ/���
				if (tempDelayTime > 0)
					{
						tvInfo.setVisibility(View.VISIBLE);
						// ����
						tvInfo.setTextColor(Color.GREEN);
						tvInfo.setText(tempDelayTime + " �������");
						handler.sendEmptyMessageDelayed(READ_SECOND_OTHER_MULTI_MODE, 1000);
						//cBtnAlarmState.setProgress(CircularProgressButton.INDETERMINATE_STATE_PROGRESS);
						
						tempDelayTime--;
					} else
					{
						isOpen = true;
						//�ڴ�ģʽ����ʧ�ܡ�
						if(!isPocket){
							ring();
						}
						if(isPocket&&(isUsb||isAcceleration))
							{
								ring();
							}
						
						// ����ģʽ������������
						if (isUsb)
							{								
								currentSensorID = USB;
							}
						if (isAcceleration)
							{
								// currentSensorID = ACCELEROMERER;
								registerAccelrometer();
							}
						
						// �ڴ�ģʽ����ָ��ʱ��δ����ڴ���ע��������
						if (isPocket && !isInPocket)
							{
								LogUtil.i("[MainActivity] isPocket unregister mProximityListenerMulti");
								sensorManager.unregisterListener(mProximityListenerMulti);// TODO:����ע�����ٶȡ�usb?
								if (isAcceleration || isUsb)
									{
										// 5����δ����ڴ�,ȡ������
										isAlarming = false;
										isInPocket = false;
									}
								// ֻ�пڴ�ģʽ��Ҫ����
								else
									{
										// 5����δ����ڴ�,ȡ������
										isOpen = false;
										// ��ʾ������ʾ�ѽ������
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
				// ������ÿ�ε����ʱ����������������Ч��Χ��
				// ������ʱ���п����������Ŀ�ʼ������ͽ����ǵ���
				long secondTime = System.currentTimeMillis();
				// �ж�ÿ�ε�����¼�����Ƿ������������Ч��Χ
				// ������ʱ���п����������Ŀ�ʼ������ͽ����ǵ���
				if (secondTime - firstTimeVolume <= 500)
					{
						++count;
					} else
					{
						count = 1;
					}
				// �ӳ٣������ж��û��ĵ�������Ƿ����
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
								LogUtil.d("[MainActivity] �����¼�");
							} else if (count > 1)
							{
								LogUtil.d("[MainActivity] ��������¼���������� " + count + " ��");
								if (count == unLockCount)
									{
										LogUtil.w("[MainActivity] �������");
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
	 * ע��
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
	 * ����¼�
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
			// ���ƽ���
			intent = new Intent(context, GestureLockSettingActivity.class);
			startActivity(intent);
			break;
		// ����������Ӧ
		case R.id.btn_speed_value_setting:
			intent = new Intent(context, SettingXYAngleActivity .class);
			startActivity(intent);
			break;
		// �ӳ�ʱ������
		case R.id.btn_delay_time_setting:
			intent = new Intent(context, SettingDelayTimeActivity.class);
			startActivity(intent);
			break;
			//����ģʽ
		case R.id.btn_model_setting:
			intent = new Intent(context, SettingModelActivity.class);
			startActivity(intent);
			break;
		}
	}

	/** ��ǰ�����Ĵ�����ID */
	private int currentSensorID;

	/** ������� */
	public void closeAlarm()
	{
		LogUtil.i("[MainActivity] closeAlarm()");
		if (isOpen)
			{		
		        LogUtil.w("[MainActivity] closeAlarm() sensorTypeId=" + sensorTypeId);
				isOpen = false;
				//�رվ�����
				if(alarmPosition<0){
					releaseMediaPlayerAlarmApp();
				}else {
					if (mediaPlayState == 1)
						{
							pause();
						}
				}			
				//��ʾ�ѽ������
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
	 * �رն�ģʽ
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
	 * ��ʾ������ <br>
	 * 
	 * ������������ʱ,������ʾ������
	 * 
	 * */
	public void ring()
	{
		LogUtil.i("[MainActivity] ring()");
		try
			{				
				play();
				// ��
				vibrate(1000);
			} catch (Exception e)
			{
				Toast.makeText(context, "[MainActivity] ring() error:" + e.getMessage(), 1).show();
			}
	}

	/**
	 * ����ϵͳ����
	 * @param ringPosition ����Id
	 */
	public void play(int ringPosition)
	{
		LogUtil.i("[MainActivity]    play(int ringPosition)");
		try
			{			
				//����ϵͳ
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
	 * ������ʾ��
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
				//App����
				mMediaPlayerRingApp = MediaPlayer.create(context, rawRingResId[Math.abs(ringPosition) - 1]);
				mMediaPlayerRingApp.start();
			}
	}
	/**
	 * �򿪾���
	 * ˵����
	 * �������ƽ��������ű�����
	 */
	public void openAlarm()
	{
		//���handler��Ϣ
		handler.removeMessages(READ_SECOND_OTHER_MULTI_MODE);	
		isAlarming = true;
		mediaPlayState = 1;				
		/*���ƽ���*/
		binder.dialog();	
	
	}

	/**
	 * �ֻ���
	 * @param time �ֻ���ʱ�䣬��λms
	 */
	private void vibrate(final int time)
	{
		if (isVibrate)
			{
				Thread thread=new Thread(){
					public void run() {
						//Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
						// ��һ�����������ǽ������飬
						// �ڶ����������ظ�������-1Ϊ���ظ�����-1���pattern��ָ���±꿪ʼ�ظ�
						// mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);
						vibrator.vibrate(time);
					};
				};
				thread.start();			
			}
	}
	
	/**
	 * �ֻ���ֱ���������
	 * @param time �ֻ���ʱ�䣬��λms
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
						// ��һ�����������ǽ������飬
						// �ڶ����������ظ�������-1Ϊ���ظ�����-1���pattern��ָ���±꿪ʼ�ظ�
						// mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);		
						vibrator.vibrate(new long[] {200, 200,100}, -1);
						handler.sendEmptyMessageDelayed(VIBRATOR,550/*500+200+500+200*/);						
					}
				};
			  	thread.start();
			}
			
	}
	
	/**
	 * ��ʼ��MediaPlayer
	 */
	public void initMediaPlayer()
	{
		mMediaPlayerSysAlarm = new MediaPlayer();
		mMediaPlayerSysAlarm.setAudioStreamType(AudioManager.STREAM_RING);
		mMediaPlayerSysAlarm.setOnCompletionListener(completionListener);			
	}

	/**
	 * ������Ϻ����²���
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
	 * ���������ķ���
	 */
	public void sensorManager()
	{
		// ��ȡ������������
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// ����ֻ�֧�ֵĴ�����
		// �Ӵ������������л��ȫ���Ĵ������б�
		List<Sensor> allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		// ��ʾÿ���������ľ�����Ϣ
		for (Sensor s : allSensors)
			{
				String tempString = "\n" + " �豸���ƣ�" + s.getName() + "\n" + " �豸�汾��" + s.getVersion() + "\n" + " ��Ӧ�̣�"
						+ s.getVendor() + "\n";
				switch (s.getType())
				{
				case Sensor.TYPE_ACCELEROMETER:
					accelrometerSensor = s;
					LogUtil.i(s.getType() + " ���ٶȴ�����accelerometer" + tempString);
					break;
				case Sensor.TYPE_PROXIMITY:
					LogUtil.i(s.getType() + " ���봫����proximity" + tempString);
					proximitySensor = s;
					break;
				}
			}
	}


	/*----------------------------------------�ٶȷ�ֵ------------------------------*/
	/**�ٶȵ���ֵ����ҡ���ٶȴﵽ��ֵ���������*/ 
	private static final int SPEED_SHRESHOLD = 2000;//3000

	// ���μ���ʱ����
	private static final int UPTATE_INTERVAL_TIME = 50;
	// �ֻ���һ��λ�õ�������Ӧ����
	private float lastX;
	private float lastY;
	private float lastZ;
	// �ϴμ��ʱ��
	private long lastUpdateTime;

	/**
	 * �ֻ��������ƶ�ʱ�򣬱���
	 * @param event
	 * @return
	 */
	public boolean speedThreshold(SensorEvent event)
	{
		// ���ڼ��ʱ��
		long currentUpdateTime = System.currentTimeMillis();
		// ���μ���ʱ����
		long timeInterval = currentUpdateTime - lastUpdateTime;
		// �ж��Ƿ�ﵽ�˼��ʱ����
		if (timeInterval < UPTATE_INTERVAL_TIME)
			return false;
		// ���ڵ�ʱ����lastʱ��
		lastUpdateTime = currentUpdateTime;

		// ���x,y,z����
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		// ���x,y,z�ı仯ֵ
		float deltaX = x - lastX;
		float deltaY = y - lastY;
		float deltaZ = z - lastZ;

		// �����ڵ�������last����
		lastX = x;
		lastY = y;
		lastZ = z;

		double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;
		LogUtil.i("speed=" + speed);
		// �ﵽ�ٶȷ�ֵ��������ʾ
		if (speed >SPEED_SHRESHOLD)
			{
				return true;
			}
		return false;
	}
	/**
	 * ���ٶȸ�Ӧ����
	 * 
	 * ˵���� X��ʾ�����ƶ��ļ��ٶ� Y��ʾǰ���ƶ��ļ��ٶ� Z��ʾ��ֱ����ļ��ٶ�
	 * 
	 * 
	 * Sensor.TYPE_ACCELEROMETER��ȡ�ļ��ٶ�ʵ�������ֻ��˶��ļ��ٶ����������ٶȵĺϼ��ٶȡ� ��ֹ״̬:<br>
	 * value[0]���ֻ�����ϵx��������ļнǣ�<br>
	 * ʵ�ʼ��ٶ���x���ϵķ�����ȥ�������ٶ���z���ϵķ���
	 * value[1]���ֻ�����ϵy��������ļнǣ�<br>
	 * value[2]���ֻ�����ϵz��������ļнǡ�<br>
	 * */
	private SensorEventListener accelerometerListener = new SensorEventListener()
	{
		@Override
		public void onSensorChanged(SensorEvent event)
		{	
			//�ֻ�x,y�н��Ƿ񳬹���ȫ��
			checkDeviceSafeScope(event);		
		}	 		
		// ��Ӧ�ٶȱ仯
		// Accuracy:��ȷֵ
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
			LogUtil.i("acceleromererListener  accuracy =" + accuracy);
		}
	};


	
	// �����ֻ���ˮƽ�ĽǶ�
	// ��ΰѵ�ǰ���ٶȵ�ֵת��Ϊ��ǰ�Ƕ�ֵ�أ�����Ҫһ����Ӳ�����������������е�ԭ��������ͬѧ���Կ�һЩ���ٶȴ�����������飬���ڼ��ٶ�
	// ���������кܶ�Ӧ�ã������ٶȵĲ�����λ�ƵĲ����������Ҫ���Ӹ��ӵ��㷨�ˣ�����Ͳ��ٽ���
	private int toDegree(float zz)
	{
		// �����жϼ��ٶȵ�ֵ�Ƿ����10��С��-10��������Ϊ���˶������м����ǲ��ȶ��ģ�������Ҫ������ھ�ֹ״̬�µ��ȶ�ֵ��
		if (zz > 10)
			{
				zz = 10;
			} else if (zz < -10)
			{
				zz = -10;
			}

		// acos��zz / 10�����������б�ǶȵĻ���ֵ�� Math.acos(1)=0
		// Math.acos(-1)=3.1415->degree=179
		double r = Math.acos(zz / 10);
		// Ȼ�󽫻���ֵת��Ϊ�Ƕ�ֵ
		int degree = (int) (r * 180 / Math.PI);
		// ��󷵻�һ��String
		return degree;
	}

	/**
	 * x�Ƕ� [0,90] 
	 * �Ҳ��+ 
	 * ����-
	 * ��Ļ���������� 
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

	/**y�Ƕ�
	 * [0,90]
	 * ������+
	 * �ײ���-
	 * ��Ļ���������� 
	 * @param yy
	 * @return
	 */
	private int toDegreeY(float yy)
	{
		// �����жϼ��ٶȵ�ֵ�Ƿ����10��С��-10��������Ϊ���˶������м����ǲ��ȶ��ģ�������Ҫ������ھ�ֹ״̬�µ��ȶ�ֵ��
		if (yy > 10)
			{
				yy = 10;
			} else if (yy < -10)
			{
				yy = -10;
			}
		
		// acos��zz / 10�����������б�ǶȵĻ���ֵ��    Math.acos(1)=0  Math.acos(-1)=3.1415->degree=179
		double r = Math.asin(yy / 10);
		// Ȼ�󽫻���ֵת��Ϊ�Ƕ�ֵ
		int degree = (int) (r * 180 / Math.PI);
		// ��󷵻�һ��String
		return degree;
	}	

	private float[] gravity = new float[3];
	// �ֻ�ʵ�ʵļ��ٶ�
	private int[] linear_acceleration = new int[3];
	// alpha is calculated as t / (t + dT)
	// with t, the low-pass filter's time-constant
	// and dT, the event delivery rate
	private final float ALPHA = 0.8F;

	/**
	 * ��������
	 * 
	 * @param event
	 *            ���������������ֻ�ʵ�ʼ��ٶ�
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

	// ��Ҫˢ�µ����ݷŵ�onResume����
	@Override
	protected void onResume()
	{
		super.onResume();
		ringPosition = sp.getInt("ring", -1);
		LogUtil.v("[MainActivity]ringPosition  " + ringPosition);
	    //ͼ������
		password = sp.getString("password", "null");
		// ���ý�������
		if ("null".equals(password))
			{
				Intent intent = new Intent(context, GestureLockSettingActivity.class);
				startActivity(intent);
			}
		
		//��������Id
		alarmPosition = sp.getInt("ringAlarm", 1);
		LogUtil.v("[MainActivity]ringAlarm  " + alarmPosition);			
		alarmUri = alarmPosition==0?mRingtoneManagerSysAlarmUri.getRingtoneUri(0):mRingtoneManagerSysAlarmUri.getRingtoneUri(alarmPosition - 1);
		registerBroadcast();		
		//���ü��ٶȾ����İ�ȫ����
		settedX=sp.getInt("degreeX", 0);
		settedY=sp.getInt("degreeY", 0);
		safeScodeHighX = settedX + shakeAngle > 180 ? (settedX + shakeAngle - 180) : (settedX + shakeAngle);
		safeScodeLowX = settedX - shakeAngle < 0 ? 180 + settedX - shakeAngle : settedX - shakeAngle;
		safeScodeHighY = settedY + shakeAngle > 180 ? (settedY + shakeAngle - 180) : (settedY + shakeAngle);
		safeScodeLowY = settedY - shakeAngle < 0 ? 180 + settedY - shakeAngle : settedY - shakeAngle;
		//App���ƽ���
		if (isAlarming)
			{
				LogUtil.w("[onResume] ���ڱ���");
			}
		if (isHome && isGestureLock)
			{
				gestureLock();
			}
	}	
	
	/**��ʾ���ƽ�������*/ 
	public void gestureLock()
	{		
		Intent intent = new Intent(context, GesturUnlockActivity.class);
		startActivity(intent);
	}
	
	/**
	 * ע��һЩ�й㲥
	 */
	public void registerBroadcast()
	{
		IntentFilter filter = new IntentFilter();
		// ���Ͽ�
		filter.addAction(Intent.ACTION_POWER_DISCONNECTED);		
		// �����β�
		filter.addAction(ACTION_HEADSET_PLUG);
		// �رվ���
		filter.addAction(ACTION_CLOSE_ALARM);
		// �򿪾���
		filter.addAction(ACTION_OPEN_ALARM);
		registerReceiver(powerChangeBroadcast, filter);
	}

	public void pause()
	{
		mediaPlayState = 2;
		mMediaPlayerSysAlarm.pause();
	}
	/**
	 * �Ƿ���������
	 * 
	 * ˵���� �β��ǳɶԳ��ֵģ� ���û�в��룬����ʾ�Ͽ�
	 */
	private boolean isIn;

	/**
	 * 
	 * �㲥
	 * 
	 * ���գ� �رա��򿪾����㲥 �����β�㲥 ��Դ�Ͽ��㲥
	 */
	public class PowerChangeBroadcast extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			// if(isOpen){
			// �����β�
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
									// �Ͽ����ӣ�����
										{
											Toast.makeText(context, "�Ͽ����ӣ�����", Toast.LENGTH_SHORT).show();
											// �򿪾���
											openAlarm();
										}									
								} else if (intent.getIntExtra("state", 0) == 1)
								{
									isIn = true;
									ToastUtil.makeText(context, "headset connected");
								}
						}
				}else
			// ���Ͽ�
			if (Intent.ACTION_POWER_DISCONNECTED.equals(action))
				{
					LogUtil.i("ACTION_POWER_DISCONNECTED");
					if (isOpen && currentSensorID == USB)
					// �Ͽ����ӣ�����
						{
							Toast.makeText(context, "�Ͽ����ӣ�����", Toast.LENGTH_SHORT).show();
							// �򿪾���
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
					// ��������
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
									LogUtil.e("[MainActivity]�㲥  ������������error�� " + e.getMessage());
								}
						}

					// ����ƿ���
					if (isLight)
						{
							isFlashOpen = true;
							handler.sendEmptyMessageDelayed(FLASH_LIGHT, FLASH_SPEED);
						}
					vibrate();
				}			
		}
	}

	// �ӳ�ʱ����������ʱ������Ч��Χ
/*	private void delay(long interval)
	{
		if (task != null)
			task.cancel();

		task = new TimerTask()
		{
			@Override
			public void run()
			{
				LogUtil.w("ʱ���̣߳�������Դ����");
				handler.sendEmptyMessage(READ_SECOND_PRESS_BUTTON);
			}
		};
		delayTimer = new Timer();
		// ��һ������Ϊִ�е�mTimerTask
		// �ڶ�������Ϊ�ӳٵ��¼�������д1000����˼�� mTimerTask���ӳ�1��ִ��
		// ����������Ϊ���ִ��һ�Σ�����д1000 ��ʾû1��ִ��һ��mTimerTask��Run����
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
		// true��������Ч
	}
	
	
	/** �Ƿ�ŵ��ڴ� */
	private boolean isInPocket;
	
	/** ������� */
	private SensorEventListener mProximityListenerMulti = new SensorEventListener()
	{
		public void onSensorChanged(SensorEvent event)
		{
			// �ֻ�������ľ���
			float distance = event.values[0];
			LogUtil.e("distance=" + distance);
			// ����ڴ�
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
							// �ڴ�ģʽ����������ģʽ�ض�Ҳ�ɹ�������
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
			// ����ڴ���
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
	
	/*--------------------�й⴫����-----------------*/
	// ��һ�ι��
	private float lastLightValue;
	/**
	 * �������
	 * 
	 * ��⵽�ֻ�������ľ���
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
							// ����������ע�����������ظ�����
							sensorManager.unregisterListener(lightListener);
						}
				}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
			LogUtil.i("lightListener  accuracy =" + accuracy);
		}
	};

	/*------------------��ʾ��Ϣ---------------------*/
	private String state = "δ����";

	/** ��ʾ��ǰ״̬ */
	public void displayInfo()
	{			
		state = isOpen ? "������" : "δ����";
		int color = isOpen ? Color.RED : Color.BLACK;
		
	    int stateIndex = isOpen ? CircularProgressButton.SUCCESS_STATE_PROGRESS : CircularProgressButton.IDLE_STATE_PROGRESS;
		cBtnAlarmState.setProgress(stateIndex);
		
		tvInfo.setVisibility(isOpen ? View.VISIBLE : View.GONE);
		tvInfo.setTextColor(color);
		StringBuffer modeName = new StringBuffer();
		if (isInPocket)
			{
				modeName.append("�ڴ�ģʽ  ");
			}
		if (isUsb&&currentSensorID==USB)
			{
				modeName.append("USBģʽ  ");
			}
		if (isAcceleration)
			{
				modeName.append("������Ӧģʽ  ");
			}
	
		tvInfo.setText(state+" "+modeName.toString());
	}
	
	public void displayInfo(String sensorName)
	{
		state = isOpen ? "������" : "δ����";
		int stateIndex = isOpen ? CircularProgressButton.SUCCESS_STATE_PROGRESS : CircularProgressButton.IDLE_STATE_PROGRESS;
		cBtnAlarmState.setProgress(stateIndex);
		int color = isOpen ? Color.RED : Color.BLACK;
		tvInfo.setTextColor(color);
		tvInfo.setText(state + " " + sensorName);
		
	}

	

	/**
	 * ��������ƵĿ����͹ر�
	 * 
	 * @param isEnable
	 *            �Ƿ�������
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
	 * �ͷ��������Դ
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

	/*--------------------------------��������-----------------------------------------*/

	private ArrayAdapter<String> adapterPower;
	private ArrayAdapter<String> adapterSensor;
	private SlideMenu moreSettingSlideMenu;

	/**
	 * ��������
	 */
	public void moreSetting()
	{
		moreSettingSlideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		// ����ѡ������ArrayAdapter��������
		adapterSensor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sensorType);		
		adapterPower = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, powerCount);
		// ���������б�ķ��
		adapterSensor.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		adapterPower.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ����Ĭ��ֵ,���ݵĵ�һ��Ԫ��
		setDefaultSensor();
		
	}

	/**
	 * ����������Ĭ�ϵĴ����� ���ļ���currentSensorID�ֶ�
	 */
	public void setDefaultSensor()
	{
		sensorTypeId = getSensorID();
	}

	/**
	 * ��ȡ���õĴ�����
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
		// ʹ�����������߳�ֹͣ
		mIsDestroy = true;
		LogUtil.i("ע������������");		
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
				LogUtil.e("��������� error:"+e.getMessage());
			}	
		// �ͷ��������Դ
		destroyedCamera();
		saveSensorID();
		// �ر�����Activity
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
	 * �ֻ�x,y�н��Ƿ񳬹���ȫ����
	 * ������ȫ��ʱ����������
	 * @param event
	 */
	public void checkDeviceSafeScope(SensorEvent event)
	{
		//�����������ٶ�
		avoidGavity(event);
		// ��֤���ٶ�=0���ֻ����ڻ����ƶ������ܵõ���ȷ�ĽǶ�ֵ
		if (linear_acceleration[2] != 0 || linear_acceleration[1] != 0 || linear_acceleration[0] != 0)
			{
				return;
			}

		/*--------------------------�����ֻ���ǰx,y��н�--------------------------*/
		int z = (int) event.values[2];
		int currentDegreeX = toDegreeX(event.values[0]);
		int currentDegreeY = toDegreeY(event.values[1]);
		// �Ƕ�ת����0-180
		currentDegreeX = SettingXYAngleActivity.xAngleTo180(currentDegreeX, z);
		currentDegreeY = SettingXYAngleActivity.yAngleTo180(currentDegreeY, z);
		// x��ȫ����͵�ǰ�нǶԱ�
		if (!isSafe(currentDegreeX, true))
			{
				openAlarm();
				sensorManager.unregisterListener(accelerometerListener);
				return;
			}

		// y��ȫ����͵�ǰ�нǶԱ�
		if (!isSafe(currentDegreeY, false))
			{
				openAlarm();
				sensorManager.unregisterListener(accelerometerListener);
				return;
			}
	}

	/**
	 * �н��Ƿ��ڰ�ȫ����
	 * 
	 * @param currentDegree
	 *            ��ǰ�н�
	 * @param isCheckX
	 *            true:���X�ᣬfalse:���Y��
	 * @return true:��ȫ
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
				//��������
				manager.lockNow();
			} else
			{
				LogUtil.i("[MainActivity] δ�����豸������");
				Intent intent = new Intent(); // ������ͼ
				intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN); // ָ�����ϵͳ����Ķ�������
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName); // ָ�����ĸ������Ȩ
				startActivity(intent);
			}
	}
	/**
	 * ģ��Home��
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