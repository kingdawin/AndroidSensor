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
 * 程序使用的服务
 * 
 * @author Dawin
 *服务启动解锁界面
 */
public class AppService extends Service
{
	private AppBinder binder = new AppBinder();
	private GestureLockView gv;
	/**提示输入，输入正误*/
	private TextView tvTop;
	/**显示剩余时间，提示解锁*/
	private TextView tvBottom;
	// 控件振动效果
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
		// 锁屏唤醒cpu
		lockWakeCPU();
		//电源键，home，广播
		registerPowerReceiver();
		dialogOnScreen();
	}

	/**
	 * 在最顶层显示的界面
	 */
	public void dialogOnScreen()
	{
		// instance of WindowManager
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		// the LayoutParams for lockScreenView (设置lockScreenView视图的属性)
		// TYPE_SYSTEM_ERROR (关键是设置属性TYPE_SYSTEM_ERROR:出现任何东西的前面 appear on top
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
		// 提示输入，输入正误
		tvTop = (TextView) lockScreenView.findViewById(R.id.tv_info);
		// 显示剩余时间，提示解锁
		tvBottom = (TextView) lockScreenView.findViewById(R.id.tv_read_second);
		tvBottom.setTextColor(Color.GREEN);
		tvBottom.setVisibility(View.VISIBLE);
		//监听手势
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
						tvTop.setText("手势错误");
					}
			}
		});
	}
	/**
	 * 设置动画效果
	 */
	public void setAnimation()
	{		
		animation = new TranslateAnimation(-10, 10, 0, 0);
		animation.setDuration(20);
		animation.setRepeatCount(5);
		animation.setRepeatMode(Animation.REVERSE);
	}

	private ComponentName mbCN;
	/** 控制音量的对象 */
	public AudioManager mAudioManager;

	/**
	 * 音量值改变触发此广播--用来监听音量按钮
	 * 
	 * <receiver
	 * android:name="com.hy2014.phonesafer.activity.MediaButtonReceiver" >
	 * <intent-filter android:priority="1000" >
	 * <action android:name="android.intent.action.MEDIA_BUTTON" /> 
	 * 监听音量变化 
	 * <action android:name="android.media.VOLUME_CHANGED_ACTION" /> </intent-filter>
	 * </receiver>
	 */
	public void registerMediaButtonEventReceiver()
	{
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// mAudioManager = (AudioManager)
		// getSystemService(Context.AUDIO_SERVICE);
		// 构造一个ComponentName，指向MediaoButtonReceiver类
		mbCN = new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());
		// 注册一个MedioButtonReceiver广播监听
		mAudioManager.registerMediaButtonEventReceiver(mbCN);
	}

	public void registerPowerReceiver()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(powerHomeReceiver, intentFilter);
	}

	/**
	 * 申请设备电源锁 （非常耗电）
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

	/** 释放设备电源锁 */
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
	 * 解锁电源键，Home键的广播
	 * 说明：
	 * Android中屏蔽"电源键长按"、"Home键"、"Home键长按" 这几个“按钮”的触发， 
	 * 都会产生一个Action==Intent.ACTION_CLOSE_SYSTEM_DIALOGS的通知
	 * 区分他们三个的不同事件的参数，就是随着Intent带过来的reason字符串，分别对应
	 * "电源键长按"（globalscreen）、"Home键"（homekey）、"Home键长按"（recentapps） 
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
									// 模拟按键，提高屏蔽长按电源的成功率
									// sendKeyCode(KeyEvent.KEYCODE_VOLUME_UP);
									sendKeyCode(KeyEvent.KEYCODE_MENU);
									// sendKeyCode(KeyEvent.KEYCODE_BACK);
									// 屏蔽电源长按键的方法：
									Intent myIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
									myIntent.putExtra("myReason", true);
									context.sendBroadcast(myIntent);
									LogUtil.w("电源  键被长按");

								} else if (reason.equalsIgnoreCase("homekey"))
								{
									// 屏蔽Home键的方法
									// 在这里做一些你自己想要的操作,比如重新打开自己的锁屏程序界面，这样子就不会消失了
									LogUtil.w("Home 键被触发");
									if (BaseActivity.isGestureLock)
										{
											BaseActivity.isHome = true;
										}

								} else if (reason.equalsIgnoreCase("recentapps"))
								{
									LogUtil.w("Home 键被长按");
								}
						}
				}
		}
	}

	/**
	 * 模拟按键
	 * @param keyCode 键值
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
						// 读秒
						tvBottom.setText(seconds + "秒后启动报警");
						handler.sendEmptyMessageDelayed(READ_SECOND, 1000);
						seconds--;
					} else
					{
						LogUtil.e("不读秒");
						tvBottom.setText("请解锁");
						if (mSuccess)
							{
								// Toast.makeText(context, "警报解除", 1).show();
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
	 * 锁屏时显示自定义手势解锁
	 */
	public void showInLockScreen()
	{
		LogUtil.w("[AppService] showInLockScreen()");
		mSuccess = false;
		gv.setKey(BaseActivity.password);
		tvTop.setText("请绘制手势关闭警报");
		tvTop.setTextColor(Color.WHITE);
		tvBottom.setText("请解锁");
		try
			{
				// add the view to window
				wm.addView(lockScreenView, mLayoutParams);
			} catch (Exception e)
			{
				//ToastUtil.makeText(getApplicationContext(), "error:" + e.getMessage());
			}

		// 延迟报警
		if (BaseActivity.isDelayAlarm)
			{
				LogUtil.w("[SensorService] 延迟报警");
				seconds = BaseActivity.delayOpenTime;
				handler.sendEmptyMessage(READ_SECOND);
			}
		// 立刻报警
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

	/**用来锁屏和解锁的对象*/
	private KeyguardManager mKeyGuardManager ;
	private KeyguardLock mLock;
	/**
	 * 解除锁屏
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void unLock()
	{		
		if (mKeyGuardManager.isKeyguardLocked())
			{
				LogUtil.e("unLock() 解锁");
				// LogUtil.e("是否已锁mKeyGuardManager.isKeyguardLocked()="+mKeyGuardManager.isKeyguardLocked());
				mLock = mKeyGuardManager.newKeyguardLock("MainActivity");// Activity名字
				mLock.disableKeyguard();	
			}
	}
	
	/**
	 * 服务与MainActivity通信
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

//4维空间包括时间