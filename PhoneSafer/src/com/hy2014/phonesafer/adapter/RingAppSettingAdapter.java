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
 *         2014-4-8 下午2:43:17
 */

@SuppressLint({ "NewApi", "UseSparseArrays" })
public class RingAppSettingAdapter extends BaseAdapter
{
    //private List<String> ringList;
	private String [] ringAppDatas;
	private Context mContext;
	public Cursor cursor;
	/**
	 * 用来获取通知声，铃声
	 */
	//private RingtoneManager ringMangager;
	public Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
	private ViewHolder mHodler;
	public ListView ringView;
	public int index;
	private boolean firstItemState = true;

	/**
	 * @param context
	 *            上下文
	 * @param index
	 *            作为记录所选铃声的position传入SharedPreferences记录并调取
	 * @param ringType
	 *            铃声类型
	 */
	public RingAppSettingAdapter(Context context, int index, int ringType) {
		this.mContext = context;
		this.index = index;
		// 警报音
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
    //优化：
	//1考虑convertView是否为null,如果为null则创建并返回一个converView.
	//2不为空则使用converView.
	//3如果数据项太多，就要分页显示
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		
		//将convertView封装在ViewHodler中，减少系统内存占用 
		//convertView为空则初始化 
		if (convertView == null)
			{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.select_iamgebutton_adapter, null);
				mHodler = new ViewHolder(convertView);
				convertView.setTag(mHodler);
			} else
			{
				/* 不为空则直接使用已有的封装类 */
				mHodler = (ViewHolder) convertView.getTag();
			}

		//设定按钮背景图
		mHodler.iv.setBackgroundResource(map.get(position) == null ? R.drawable.pressed : R.drawable.checked);
		mHodler.tv.setText(ringAppDatas[position]);
		return convertView;
	}

	/* 封装类 */
	public static class ViewHolder
	{
		 TextView tv;
		 ImageView iv;

		public ViewHolder(View v) {
			/* 组件初始化 */
			this.tv = (TextView) v.findViewById(R.id.select_imagebtn_ring_tv);
			this.iv = (ImageView) v.findViewById(R.id.select_imagebtn_btn);
		}
	}

}
