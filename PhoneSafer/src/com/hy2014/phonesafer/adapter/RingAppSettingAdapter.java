/**
 * 
 */
package com.hy2014.phonesafer.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.activity.BaseActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author 1900<br>
 *         2014-4-8 ����2:43:17
 */

@SuppressLint({ "NewApi", "UseSparseArrays" })
public class RingAppSettingAdapter extends BaseAdapter
{
    //private List<String> ringList;
	private String [] ringAppDatas;
	private Context mContext;
	public Cursor cursor;
	/**
	 * ������ȡ֪ͨ��������
	 */
	//private RingtoneManager ringMangager;
	public Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
	private ViewHolder mHodler;
	public ListView ringView;
	public int index;
	private boolean firstItemState = true;

	/**
	 * @param context
	 *            ������
	 * @param index
	 *            ��Ϊ��¼��ѡ������position����SharedPreferences��¼����ȡ
	 * @param ringType
	 *            ��������
	 */
	public RingAppSettingAdapter(Context context, int index, int ringType) {
		this.mContext = context;
		this.index = index;
		// ������
		ringAppDatas = (ringType == RingtoneManager.TYPE_RINGTONE ? BaseActivity.alarmAppDatas : BaseActivity.ringAppDatas);
		if (index < 0 && firstItemState)
			{
				firstItemState = false;
				map.put(Math.abs(index + 1), true);
			}
	}

	@Override
	public int getCount()
	{
		return ringAppDatas.length;
	}

	@Override
	public Object getItem(int position)
	{
		return ringAppDatas[position];
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}
    //�Ż���
	//1����convertView�Ƿ�Ϊnull,���Ϊnull�򴴽�������һ��converView.
	//2��Ϊ����ʹ��converView.
	//3���������̫�࣬��Ҫ��ҳ��ʾ
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		
		//��convertView��װ��ViewHodler�У�����ϵͳ�ڴ�ռ�� 
		//convertViewΪ�����ʼ�� 
		if (convertView == null)
			{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.select_iamgebutton_adapter, null);
				mHodler = new ViewHolder(convertView);
				convertView.setTag(mHodler);
			} else
			{
				/* ��Ϊ����ֱ��ʹ�����еķ�װ�� */
				mHodler = (ViewHolder) convertView.getTag();
			}

		//�趨��ť����ͼ
		mHodler.iv.setBackgroundResource(map.get(position) == null ? R.drawable.pressed : R.drawable.checked);
		mHodler.tv.setText(ringAppDatas[position]);
		return convertView;
	}

	/* ��װ�� */
	public static class ViewHolder
	{
		 TextView tv;
		 ImageView iv;

		public ViewHolder(View v) {
			/* �����ʼ�� */
			this.tv = (TextView) v.findViewById(R.id.select_imagebtn_ring_tv);
			this.iv = (ImageView) v.findViewById(R.id.select_imagebtn_btn);
		}
	}

}
