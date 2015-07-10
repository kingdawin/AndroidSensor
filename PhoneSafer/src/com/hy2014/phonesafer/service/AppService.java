/*
 *@author Dawin,2015-1-2
 *
 *
 *
 */
package com.hy2014.phonesafer.service;

import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.activity.BaseActivity;
import com.hy2014.phonesafer.activity.MediaButtonReceiver;
import com.hy2014.phonesafer.gestureLock.GestureLockView;
import com.hy2014.phonesafer.utils.LogUtil;
import com.hy2014.phonesafer.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.app.KeyguardManager;
import android.app.Service;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

/**
 * ����ʹ�õķ���
 * 
 * @author Dawin
 *����������������
 */
public class AppService extends Service
{
	private AppBinder binder = new AppBinder();
	private GestureLockView gv;
	/**��ʾ���룬��������*/
	private TextView tvTop;
	/**��ʾʣ��ʱ�䣬��ʾ����*/
	private TextView tvBottom;
	// �ؼ���Ч��
	private Animation animation;

	private SharedPreferences sp;
	private final String FILE_NAME = "ring";
	private final int READ_SECOND = 1;
	private View lockScreenView;

	private WindowManager.LayoutParams mLayoutParams;
	private WindowManager wm;
	private PowerManager.WakeLock wl;
	@Override
	public void onCreate()
	{
		super.onCreate();
		LogUtil.i("SensorService  onCreate");		
		mKeyGuardManager=(KeyguardManager )getSystemService(Context.KEYGUARD_SERVICE);
		sp = getSharedPreferences(FILE_NAME, 1);
		setAnimation();
		// ��������cpu
		lockWakeCPU();
		//��Դ����home���㲥
		registerPowerReceiver();
		dialogOnScreen();
	}

	/**
	 * �������ʾ�Ľ���
	 */
	public void dialogOnScreen()
	{
		// instance of WindowManager
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		// the LayoutParams for lockScreenView (����lockScreenView��ͼ������)
		// TYPE_SYSTEM_ERROR (�ؼ�����������TYPE_SYSTEM_ERROR:�����κζ�����ǰ�� appear on top
		// of everything they can)
		mLayoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
				0, 0, WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, PixelFormat.RGBA_8888);
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate required layout file
		lockScreenView = mInflater.inflate(R.layout.activity_gesture_unlock_setting, null);
		gv = (GestureLockView) lockScreenView.findViewById(R.id.gv);
		gv.setKey(sp.getString("password", "036"));
		// ��ʾ���룬��������
		tvTop = (TextView) lockScreenView.findViewById(R.id.tv_info);
		// ��ʾʣ��ʱ�䣬��ʾ����
		tvBottom = (TextView) lockScreenView.findViewById(R.id.tv_read_second);
		tvBottom.setTextColor(Color.GREEN);
		tvBottom.setVisibility(View.VISIBLE);
		//��������
		gv.setOnGestureFinishListener(new GestureLockView.OnGestureFinishListener()
		{

			@Override
			public void OnGestureFinish(boolean success, String key)
			{
				mSuccess = success;
				if (success)
					{
						Intent intent = new Intent(BaseActivity.ACTION_CLOSE_ALARM);
						sendBroadcast(intent);
						handler.removeMessages(READ_SECOND);
						unLock();
						removeViewFromWindow();						
					} else
					{
						tvTop.setTextColor(Color.RED);
						tvTop.startAnimation(animation);
						tvTop.setText("���ƴ���");
					}
			}
		});
	}
	/**
	 * ���ö���Ч��
	 */
	public void setAnimation()
	{		
		animation = new TranslateAnimation(-10, 10, 0, 0);
		animation.setDuration(20);
		animation.setRepeatCount(5);
		animation.setRepeatMode(Animation.REVERSE);
	}

	private ComponentName mbCN;
	/** ���������Ķ��� */
	public AudioManager mAudioManager;

	/**
	 * ����ֵ�ı䴥���˹㲥--��������������ť
	 * 
	 * <receiver
	 * android:name="com.hy2014.phonesafer.activity.MediaButtonReceiver" >
	 * <intent-filter android:priority="1000" >
	 * <action android:name="android.intent.action.MEDIA_BUTTON" /> 
	 * ���������仯 
	 * <action android:name="android.media.VOLUME_CHANGED_ACTION" /> </intent-filter>
	 * </receiver>
	 */
	public void registerMediaButtonEventReceiver()
	{
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// mAudioManager = (AudioManager)
		// getSystemService(Context.AUDIO_SERVICE);
		// ����һ��ComponentName��ָ��MediaoButtonReceiver��
		mbCN = new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());
		// ע��һ��MedioButtonReceiver�㲥����
		mAudioManager.registerMediaButtonEventReceiver(mbCN);
	}

	public void registerPowerReceiver()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(powerHomeReceiver, intentFilter);
	}

	/**
	 * �����豸��Դ�� ���ǳ��ĵ磩
	 * */
	private void lockWakeCPU()
	{
		if (null == wl)
			{
				PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
				wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "");
				//mgr.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,NAME);
				if (null != wl)
					{
						wl.acquire();
					}
			}
	}

	/** �ͷ��豸��Դ�� */
	private void releaseWakeLock()
	{
		if (null != wl)
			{
				wl.release();
				wl = null;
			}
	}
	
	private PowerHomeReceiver powerHomeReceiver = new PowerHomeReceiver();

	/**
	 * ������Դ����Home���Ĺ㲥
	 * ˵����
	 * Android������"��Դ������"��"Home��"��"Home������" �⼸������ť���Ĵ����� 
	 * �������һ��Action==Intent.ACTION_CLOSE_SYSTEM_DIALOGS��֪ͨ
	 * �������������Ĳ�ͬ�¼��Ĳ�������������Intent��������reason�ַ������ֱ��Ӧ
	 * "��Դ������"��globalscreen����"Home��"��homekey����"Home������"��recentapps�� 
	 */
	private class PowerHomeReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String reason = intent.getStringExtra("reason");
			if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
				{
					LogUtil.w("Intent.ACTION_CLOSE_SYSTEM_DIALOGS : " + reason);
					if (intent.getExtras() != null && intent.getExtras().getBoolean("myReason"))
						{
							LogUtil.w("abortBroadcast");
							abortBroadcast();
						} else if (reason != null)
						{
							LogUtil.w("reason != null");
							if (reason.equalsIgnoreCase("globalactions"))
								{
									// ģ�ⰴ����������γ�����Դ�ĳɹ���
									// sendKeyCode(KeyEvent.KEYCODE_VOLUME_UP);
									sendKeyCode(KeyEvent.KEYCODE_MENU);
									// sendKeyCode(KeyEvent.KEYCODE_BACK);
									// ���ε�Դ�������ķ�����
									Intent myIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
									myIntent.putExtra("myReason", true);
									context.sendBroadcast(myIntent);
									LogUtil.w("��Դ  ��������");

								} else if (reason.equalsIgnoreCase("homekey"))
								{
									// ����Home���ķ���
									// ��������һЩ���Լ���Ҫ�Ĳ���,�������´��Լ�������������棬�����ӾͲ�����ʧ��
									LogUtil.w("Home ��������");
									if (BaseActivity.isGestureLock)
										{
											BaseActivity.isHome = true;
										}

								} else if (reason.equalsIgnoreCase("recentapps"))
								{
									LogUtil.w("Home ��������");
								}
						}
				}
		}
	}

	/**
	 * ģ�ⰴ��
	 * @param keyCode ��ֵ
	 */
	private void sendKeyCode(final int keyCode)
	{
		LogUtil.i("sendKeyCode  " + keyCode);
		new Thread()
		{
			public void run()
			{
				try
					{
						Instrumentation inst = new Instrumentation();
						inst.sendKeyDownUpSync(keyCode);
					} catch (Exception e)
					{
						Log.e("Exception when sendPointerSync", e.toString());
					}
			}
		}.start();
	}
	private int seconds;
	private boolean mSuccess = false;
	private final Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case READ_SECOND:
				if (seconds > 0 && !mSuccess)
					{
						LogUtil.e("seconds >= 0 && !mSuccess");
						// ����
						tvBottom.setText(seconds + "�����������");
						handler.sendEmptyMessageDelayed(READ_SECOND, 1000);
						seconds--;
					} else
					{
						LogUtil.e("������");
						tvBottom.setText("�����");
						if (mSuccess)
							{
								// Toast.makeText(context, "�������", 1).show();
							} else
							{
								Intent intent = new Intent(BaseActivity.ACTION_OPEN_ALARM);
								sendBroadcast(intent);
							}
					}
				break;
			}
		};
	};

	/**
	 * ����ʱ��ʾ�Զ������ƽ���
	 */
	public void showInLockScreen()
	{
		LogUtil.w("[AppService] showInLockScreen()");
		mSuccess = false;
		gv.setKey(BaseActivity.password);
		tvTop.setText("��������ƹرվ���");
		tvTop.setTextColor(Color.WHITE);
		tvBottom.setText("�����");
		try
			{
				// add the view to window
				wm.addView(lockScreenView, mLayoutParams);
			} catch (Exception e)
			{
				//ToastUtil.makeText(getApplicationContext(), "error:" + e.getMessage());
			}

		// �ӳٱ���
		if (BaseActivity.isDelayAlarm)
			{
				LogUtil.w("[SensorService] �ӳٱ���");
				seconds = BaseActivity.delayOpenTime;
				handler.sendEmptyMessage(READ_SECOND);
			}
		// ���̱���
		else
			{
				Intent intent = new Intent(BaseActivity.ACTION_OPEN_ALARM);
				sendBroadcast(intent);
			}
	}

	/** Removes mView from the window */
	public void removeViewFromWindow()
	{
		if (lockScreenView != null)
			{
				wm.removeView(lockScreenView);
			}
	}

	/**���������ͽ����Ķ���*/
	private KeyguardManager mKeyGuardManager ;
	private KeyguardLock mLock;
	/**
	 * �������
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void unLock()
	{		
		if (mKeyGuardManager.isKeyguardLocked())
			{
				LogUtil.e("unLock() ����");
				// LogUtil.e("�Ƿ�����mKeyGuardManager.isKeyguardLocked()="+mKeyGuardManager.isKeyguardLocked());
				mLock = mKeyGuardManager.newKeyguardLock("MainActivity");// Activity����
				mLock.disableKeyguard();	
			}
	}
	
	/**
	 * ������MainActivityͨ��
	 * 
	 * @author Dawin
	 * 
	 */
	public class AppBinder extends Binder
	{
		public void dialog()
		{
			LogUtil.w("[AppBinder] dialog");
			showInLockScreen();
		}
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return binder;
	}

	@Override
	public void onDestroy()
	{
		releaseWakeLock();
		removeViewFromWindow();
		super.onDestroy();
	}
}

//4ά�ռ����ʱ��