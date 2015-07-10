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
 * ��ý�尴ť�㲥
 * 
 * @author Dawin
 * ������״̬��ȡ�����¼���
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
				//htc����Ҳ������
			}// recevier 2
		else
			{
				recevierTimes++;
				
				if (recevierTimes == 2)
					{
						LogUtil.e("[MediaButtonReceiver]����  ");
						recevierTimes = 0;
						listenVolumeCount();
					}
			}
*/
	}

	public void listenVolumeCount()
	{
		
		
		// ������ÿ�ε����ʱ����������������Ч��Χ��
		// ������ʱ���п����������Ŀ�ʼ������ͽ����ǵ���
		long secondTime = System.currentTimeMillis();
		// �ж�ÿ�ε�����¼�����Ƿ������������Ч��Χ
		// ������ʱ���п����������Ŀ�ʼ������ͽ����ǵ���
		if (secondTime - firstTimeVolume <= 350)
			{
				++count;
			} else
			{

				count = 1;
			}
		// �ӳ٣������ж��û��ĵ�������Ƿ����
		delay(350);
		firstTimeVolume = secondTime;

	}

	

	// �ӳ�ʱ����������ʱ������Ч��Χ
	private void delay(long interval)
	{
		if (task != null)
			task.cancel();

		task = new TimerTask()
		{
			@Override
			public void run()
			{
				LogUtil.w("ʱ���̣߳�������Դ����");
				handler.sendEmptyMessage(1);
			}
		};
		delayTimer = new Timer();
		// ��һ������Ϊִ�е�mTimerTask
		// �ڶ�������Ϊ�ӳٵ��¼�������д1000����˼�� mTimerTask���ӳ�1��ִ��
		// ����������Ϊ���ִ��һ�Σ�����д1000 ��ʾû1��ִ��һ��mTimerTask��Run����
		delayTimer.schedule(task, interval);
	}

	// �������߳���Ϣ������Ϣ�ŵ���Ϣ����
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
						LogUtil.d("�����¼�");
					} else if (count > 1)
					{
						LogUtil.d("��������¼���������� " + count + " ��");
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
