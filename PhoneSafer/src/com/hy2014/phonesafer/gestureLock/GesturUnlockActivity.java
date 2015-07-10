package com.hy2014.phonesafer.gestureLock;


import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.activity.BaseActivity;
import com.hy2014.phonesafer.utils.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
/**
 * 手势解锁
 * 
 * @author RD47
 * 
 */
public class GesturUnlockActivity extends BaseActivity
{

	private GestureLockView gv;
	private TextView textView;
	private TextView tvReadSecond;
	// 控件振动效果
	private Animation animation;
	private SharedPreferences sp;
	private  final String FILE_NAME = "ring";
	private boolean mSuccess = false;
	private String password;
	// 计算秒
	private int seconds = 5;
	private final int READ_SECOND = 1;
	private final Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case READ_SECOND:
				if (seconds >= 0 && !mSuccess)
					{
						// 读秒
						tvReadSecond.setTextColor(Color.GREEN);
						tvReadSecond.setText(seconds + "秒后启动报警");
						handler.sendEmptyMessageDelayed(READ_SECOND, 1000);
						seconds--;
					} else
					{
						if (mSuccess)
							{
								//Toast.makeText(context, "警报解除", 1).show();
							} else
							{
								Intent intent = new Intent(ACTION_OPEN_ALARM);
								sendBroadcast(intent);
								tvReadSecond.setVisibility(View.INVISIBLE);
								// Toast.makeText(context, "警报启动", 1).show();
							}
					}
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_unlock_setting);
		LogUtil.e("[GesturUnlockActivity] onCreate");

		gv = (GestureLockView) findViewById(R.id.gv);
		textView = (TextView) findViewById(R.id.tv_info);
		tvReadSecond = (TextView) findViewById(R.id.tv_read_second);
		if (isDelayAlarm && isOpen)
			{
				seconds =delayOpenTime;
				tvReadSecond.setVisibility(View.VISIBLE);
				handler.sendEmptyMessage(READ_SECOND);
			}
		sp = getSharedPreferences(FILE_NAME, 1);

		textView.setTextColor(Color.WHITE);
		textView.setText("请绘制解锁手势");
		password = sp.getString("password", "null");
		animation = new TranslateAnimation(-10, 10, 0, 0);
		animation.setDuration(50);
		animation.setRepeatCount(10);
		animation.setRepeatMode(Animation.REVERSE);
		gv.setKey(password);
		// gv.setKey(password); // Z 字型
		gv.setOnGestureFinishListener(new GestureLockView.OnGestureFinishListener()
		{

			@Override
			public void OnGestureFinish(boolean success, String key)
			{
				mSuccess = success;
				if (success)
					{
						// textView.setText("正确");
						Intent intent = new Intent(ACTION_CLOSE_ALARM);
						sendBroadcast(intent);
						isHome = false;
						finish();
					} else
					{
						textView.setTextColor(Color.RED);
						textView.setText("手势错误");
						textView.startAnimation(animation);
					}
			}
		});

	}

	// 屏蔽返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		if (keyCode == KeyEvent.KEYCODE_BACK)
			{
				return true;
			}
		return super.onKeyDown(keyCode, event);
	}

	protected void onResume()
	{
		super.onResume();
		IntentFilter filter = new IntentFilter(ACTION_FINISH_GESTURELOCK_SCREEN);
		registerReceiver(finishBroadcast, filter);
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(finishBroadcast);
	}

	FinishBroadcast finishBroadcast = new FinishBroadcast();

	private final class FinishBroadcast extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (ACTION_FINISH_GESTURELOCK_SCREEN.equals(action))
				{
					finish();
				}
		}

	}
	
    
}
