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
 * 声音设置:提示音或警报音
 * 
 * @author Dawin
 * 
 *         Create:2015-01-05
 */
public class SettingRingActivity extends BaseActivity
{
	// 系统铃声列表适配器
	private RingSettingAdapter mAdapterSys;
	// APP铃声列表适配器
	private RingAppSettingAdapter mAdapterApp;

	private ListView lvRing;
	private ListView lvRingApp;
	// 保存
	private Button btnSave;
	// 返回
	private Button btnBack;
	/** raw文件Id（提示音或警报音） */
	public int[] resId;
	// 铃声类型
	private int ringType;
	// 铃声字段
	public String ringName;
	private MediaPlayer player;
	/**铃声ID*/ 
	private int ringPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ring_setting_activity);
		findViewById();
		// 确定要设置的铃声类型：提示音或报警音
		setRingName();
		sp = getSharedPreferences(FILE_NAME, 1/* Mode */);
		editor = sp.edit();
		//铃声ID
		ringPosition = sp.getInt(ringName, 0);
		//系统铃声
		mAdapterSys = new RingSettingAdapter(context, ringPosition, ringType);
		lvRing.setAdapter(mAdapterSys);
		lvRing.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvRing.setOnItemClickListener(mOnItemClickListenerSys);
		// 程序铃声
		mAdapterApp = new RingAppSettingAdapter(context, ringPosition, ringType);
		lvRingApp.setAdapter(mAdapterApp);
		lvRingApp.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvRingApp.setOnItemClickListener(mOnItemClickListenerApp);

		btnBack.setOnClickListener(mOnClickListener);
		btnSave.setOnClickListener(mOnClickListener);
	}

	public void findViewById()
	{
		// 系统铃声
		lvRing = (ListView) findViewById(R.id.ring_lv);
		// App内置铃声
		lvRingApp = (ListView) findViewById(R.id.app_ring_lv);
		/* 返回按钮和保存按钮 */
		btnBack = (Button) findViewById(R.id.back_btn);
		btnSave = (Button) findViewById(R.id.save_btn);
	}

	/**
	 * 确定要设置的铃声类型：提示音或报警音
	 */
	public void setRingName()
	{
		ringType = getIntent().getIntExtra("ringType", RingtoneManager.TYPE_NOTIFICATION);
		switch (ringType)
		{
		// 提示音
		case RingtoneManager.TYPE_NOTIFICATION:
			ringName = "ring";
			resId = BaseActivity.rawRingResId;
			break;
		// 警报音
		case RingtoneManager.TYPE_RINGTONE:
			ringName = "ringAlarm";
			resId = BaseActivity.rawAlarmResId;
			break;
		}
	}

	/* 系统铃声点击事件 */
	private OnItemClickListener mOnItemClickListenerSys = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			ViewHolder mHolder = new ViewHolder(parent);
			/* 设置Imageview不可被点击 */
			mHolder.checkBoxImg.setClickable(false);
			mAdapterSys.map.clear();
			mAdapterApp.map.clear();

			/* 将所点击的位置记录在map中 */
			mAdapterSys.map.put(position, true);
			mAdapterSys.notifyDataSetChanged();

			mAdapterApp.notifyDataSetChanged();

			/* 判断位置不为0则播放的条目为position-1 */
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
				/* position为0是跟随系统，先得到系统所使用的铃声，然后播放 */
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
	/* App铃声列表的点击事件 */
	private OnItemClickListener mOnItemClickListenerApp = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			ViewHolder mHolder = new ViewHolder(parent);
			/* 设置Imageview不可被点击 */
			mHolder.checkBoxImg.setClickable(false);
			mAdapterSys.map.clear();
			mAdapterApp.map.clear();

			/* 将所点击的位置记录在map中 */
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
	 * 播放指定歌曲
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
				//不循环
				player.setLooping(false);

				player.prepare();
				player.start();
			} catch (Exception e)
			{
				LogUtil.e("[SettingRingActivity] ring() error" + e.getMessage());
			}
	}

	/* 按钮点击事件 */
	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			/* 返回 */
			case R.id.back_btn:
				releasePlay();
				finish();
				break;
			/* 保存 */
			case R.id.save_btn:
				// 系统铃声
				if (mAdapterSys.map.size() > 0)
					{
						if (lvRing.getCheckedItemPosition() == 0)// 跟随系统
							{
								LogUtil.e("系统铃声   ringName=" + ringName + "  Position=0");
								editor.putInt(ringName, 0).commit();// 系统第一首
							} else
							{
								LogUtil.e("系统铃声   ringName=" + ringName + "  Position=" + lvRing.getCheckedItemPosition());
								editor.putInt(ringName, lvRing.getCheckedItemPosition()).commit();
							}
					}
				// App铃声
				else if (mAdapterApp.map.size() > 0)
					{
						LogUtil.e("App铃声   ringName=" + ringName + "  Position" + -(lvRingApp.getCheckedItemPosition() + 1));
						editor.putInt(ringName, -(lvRingApp.getCheckedItemPosition() + 1)).commit();
					}
				Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
//44098219890919273X  identify
