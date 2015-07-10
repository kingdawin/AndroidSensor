package com.hy2014.phonesafer.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.hy2014.phonesafer.utils.LogUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

/**
 * 多媒体按钮广播
 * 
 * @author Dawin
 * 非锁屏状态获取音量事件。
 *  
 */
public class MediaButtonReceiver extends BroadcastReceiver
{
	Context context;
	private static int recevierTimes;
	
	private static int count = 0;
	private static long firstTimeVolume = 0;
	private static Timer delayTimer;
	private static TimerTask task;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		LogUtil.e("[MediaButtonReceiver]");
	//	LogUtil.e("[MediaButtonReceiver]BaseActivity.isLockScreen="+BaseActivity.isLockScreen);
		this.context = context;
		
		/*if (BaseActivity.isLockScreen)
			{
				listenVolumeCount();
				//htc解锁也是两次
			}// recevier 2
		else
			{
				recevierTimes++;
				
				if (recevierTimes == 2)
					{
						LogUtil.e("[MediaButtonReceiver]两次  ");
						recevierTimes = 0;
						listenVolumeCount();
					}
			}
*/
	}

	public void listenVolumeCount()
	{
		
		
		// 连击是每次点击的时间间隔都在连击的有效范围内
		// 不符合时，有可能是连击的开始，否则就仅仅是单击
		long secondTime = System.currentTimeMillis();
		// 判断每次点击的事件间隔是否符合连击的有效范围
		// 不符合时，有可能是连击的开始，否则就仅仅是单击
		if (secondTime - firstTimeVolume <= 350)
			{
				++count;
			} else
			{

				count = 1;
			}
		// 延迟，用于判断用户的点击操作是否结束
		delay(350);
		firstTimeVolume = secondTime;

	}

	

	// 延迟时间是连击的时间间隔有效范围
	private void delay(long interval)
	{
		if (task != null)
			task.cancel();

		task = new TimerTask()
		{
			@Override
			public void run()
			{
				LogUtil.w("时钟线程，监听电源按键");
				handler.sendEmptyMessage(1);
			}
		};
		delayTimer = new Timer();
		// 第一个参数为执行的mTimerTask
		// 第二个参数为延迟得事件，这里写1000得意思是 mTimerTask将延迟1秒执行
		// 第三个参数为多久执行一次，这里写1000 表示没1秒执行一次mTimerTask的Run方法
		delayTimer.schedule(task, interval);
	}

	// 接收子线程消息，将消息放到消息队列
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 1:

				if (count == 1)
					{
						LogUtil.d("单击事件");
					} else if (count > 1)
					{
						LogUtil.d("连续点击事件，共点击了 " + count + " 次");
						if (count == BaseActivity.unLockCount)
							{
								// send close alarm broadcast
								Intent intent2 = new Intent(BaseActivity.ACTION_CLOSE_ALARM);
								context.sendBroadcast(intent2);
							}
					}
				delayTimer.cancel();
				count = 0;
				break;
			}

		}
	};
}
