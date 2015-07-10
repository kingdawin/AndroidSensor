package com.hy2014.phonesafer.activity;

import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.adapter.RingAppSettingAdapter;
import com.hy2014.phonesafer.adapter.RingSettingAdapter;
import com.hy2014.phonesafer.adapter.RingSettingAdapter.ViewHolder;
import com.hy2014.phonesafer.utils.LogUtil;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * ��������:��ʾ���򾯱���
 * 
 * @author Dawin
 * 
 *         Create:2015-01-05
 */
public class SettingRingActivity extends BaseActivity
{
	// ϵͳ�����б�������
	private RingSettingAdapter mAdapterSys;
	// APP�����б�������
	private RingAppSettingAdapter mAdapterApp;

	private ListView lvRing;
	private ListView lvRingApp;
	// ����
	private Button btnSave;
	// ����
	private Button btnBack;
	/** raw�ļ�Id����ʾ���򾯱����� */
	public int[] resId;
	// ��������
	private int ringType;
	// �����ֶ�
	public String ringName;
	private MediaPlayer player;
	/**����ID*/ 
	private int ringPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ring_setting_activity);
		findViewById();
		// ȷ��Ҫ���õ��������ͣ���ʾ���򱨾���
		setRingName();
		sp = getSharedPreferences(FILE_NAME, 1/* Mode */);
		editor = sp.edit();
		//����ID
		ringPosition = sp.getInt(ringName, 0);
		//ϵͳ����
		mAdapterSys = new RingSettingAdapter(context, ringPosition, ringType);
		lvRing.setAdapter(mAdapterSys);
		lvRing.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvRing.setOnItemClickListener(mOnItemClickListenerSys);
		// ��������
		mAdapterApp = new RingAppSettingAdapter(context, ringPosition, ringType);
		lvRingApp.setAdapter(mAdapterApp);
		lvRingApp.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvRingApp.setOnItemClickListener(mOnItemClickListenerApp);

		btnBack.setOnClickListener(mOnClickListener);
		btnSave.setOnClickListener(mOnClickListener);
	}

	public void findViewById()
	{
		// ϵͳ����
		lvRing = (ListView) findViewById(R.id.ring_lv);
		// App��������
		lvRingApp = (ListView) findViewById(R.id.app_ring_lv);
		/* ���ذ�ť�ͱ��水ť */
		btnBack = (Button) findViewById(R.id.back_btn);
		btnSave = (Button) findViewById(R.id.save_btn);
	}

	/**
	 * ȷ��Ҫ���õ��������ͣ���ʾ���򱨾���
	 */
	public void setRingName()
	{
		ringType = getIntent().getIntExtra("ringType", RingtoneManager.TYPE_NOTIFICATION);
		switch (ringType)
		{
		// ��ʾ��
		case RingtoneManager.TYPE_NOTIFICATION:
			ringName = "ring";
			resId = BaseActivity.rawRingResId;
			break;
		// ������
		case RingtoneManager.TYPE_RINGTONE:
			ringName = "ringAlarm";
			resId = BaseActivity.rawAlarmResId;
			break;
		}
	}

	/* ϵͳ��������¼� */
	private OnItemClickListener mOnItemClickListenerSys = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			ViewHolder mHolder = new ViewHolder(parent);
			/* ����Imageview���ɱ���� */
			mHolder.checkBoxImg.setClickable(false);
			mAdapterSys.map.clear();
			mAdapterApp.map.clear();

			/* ���������λ�ü�¼��map�� */
			mAdapterSys.map.put(position, true);
			mAdapterSys.notifyDataSetChanged();

			mAdapterApp.notifyDataSetChanged();

			/* �ж�λ�ò�Ϊ0�򲥷ŵ���ĿΪposition-1 */
			if (position != 0)
				{
					try
						{
							RingtoneManager rm = new RingtoneManager(context);
							rm.setType(ringType);
							// do not Cursor.close() the cursor. The cursor can
							// be Cursor.deactivate() safely.
							rm.getCursor();
							Uri uri = rm.getRingtoneUri(position - 1);
							ring(uri);
						} catch (Exception e)
						{
							LogUtil.e("[SettingRingActivity]  error:" + e.getMessage());
						}
				} else
				/* positionΪ0�Ǹ���ϵͳ���ȵõ�ϵͳ��ʹ�õ�������Ȼ�󲥷� */
				{
					try
						{
							RingtoneManager rm = new RingtoneManager(context);
							rm.setType(ringType);
							rm.getCursor();
							Uri uri = rm.getRingtoneUri(0);
							ring(uri);

							// Uri uri =
							// RingtoneManager.getActualDefaultRingtoneUri(context,
							// ringType);
							// ring(uri);
						} catch (Exception e)
						{
							LogUtil.e("[SettingRingActivity]  error:" + e.getMessage());
						}
				}
		}
	};
	/* App�����б�ĵ���¼� */
	private OnItemClickListener mOnItemClickListenerApp = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			ViewHolder mHolder = new ViewHolder(parent);
			/* ����Imageview���ɱ���� */
			mHolder.checkBoxImg.setClickable(false);
			mAdapterSys.map.clear();
			mAdapterApp.map.clear();

			/* ���������λ�ü�¼��map�� */
			mAdapterApp.map.put(position, true);
			mAdapterApp.notifyDataSetChanged();
			mAdapterSys.notifyDataSetChanged();

			releasePlay();
			player = MediaPlayer.create(context, resId[position]);
			player.start();
		}
	};

	private void releasePlay()
	{
		if (player != null)
			{
				player.release();
				player = null;
			}
	}

	/**
	 * ����ָ������
	 * 
	 * @param uri
	 */
	private void ring(Uri uri)
	{
		try
			{
				releasePlay();
				player = new MediaPlayer();

				player.reset();
				player.setDataSource(context, uri);
				//��ѭ��
				player.setLooping(false);

				player.prepare();
				player.start();
			} catch (Exception e)
			{
				LogUtil.e("[SettingRingActivity] ring() error" + e.getMessage());
			}
	}

	/* ��ť����¼� */
	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			/* ���� */
			case R.id.back_btn:
				releasePlay();
				finish();
				break;
			/* ���� */
			case R.id.save_btn:
				// ϵͳ����
				if (mAdapterSys.map.size() > 0)
					{
						if (lvRing.getCheckedItemPosition() == 0)// ����ϵͳ
							{
								LogUtil.e("ϵͳ����   ringName=" + ringName + "  Position=0");
								editor.putInt(ringName, 0).commit();// ϵͳ��һ��
							} else
							{
								LogUtil.e("ϵͳ����   ringName=" + ringName + "  Position=" + lvRing.getCheckedItemPosition());
								editor.putInt(ringName, lvRing.getCheckedItemPosition()).commit();
							}
					}
				// App����
				else if (mAdapterApp.map.size() > 0)
					{
						LogUtil.e("App����   ringName=" + ringName + "  Position" + -(lvRingApp.getCheckedItemPosition() + 1));
						editor.putInt(ringName, -(lvRingApp.getCheckedItemPosition() + 1)).commit();
					}
				Toast.makeText(context, "����ɹ�", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
//44098219890919273X  identify
