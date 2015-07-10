/**
 * 
 */
package com.hy2014.phonesafer.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import com.hy2014.phonesafer.R;

import android.R.layout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ReceiverCallNotAllowedException;
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
public class RingSettingAdapter extends BaseAdapter
{

	private List<String> ringList;
	private Context mContext;
	public Cursor cursor;
	/**
	 * 用来获取通知声，铃声
	 */
	private RingtoneManager rm;
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
	public RingSettingAdapter(Context context, int index, int ringType) {
		this.mContext = context;
		this.index = index;
		if (firstItemState)
			{
				firstItemState = false;
				map.put(index, true);
			}
		getRing(ringType);
	}
	public void toJinYuan(){
		//1 waiting untill she graduated
		//2   清明3日，从4.4-4.6 转角遇见你
		//3  插座的问题 
		//4
	}
	
	public void getRing(int ringType)
	{
		//新建一个arraylist来接收从系统中获取的短信铃声数据 
		ringList = new ArrayList<String>();
		//添加“跟随系统”选项 
		ringList.add("默认铃声");
		// 获取RingtoneManager 
		rm = new RingtoneManager(mContext);
		// 指定获取类型为短信铃声 
		rm.setType(ringType);
		// 创建游标
		cursor = rm.getCursor();
		while (cursor.moveToNext())
			{
				ringList.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
			}
		cursor.close();
	}

	@Override
	public int getCount()
	{
		return ringList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return ringList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{	
		if (convertView == null)
			{			
				convertView = LayoutInflater.from(mContext).inflate(R.layout.select_iamgebutton_adapter, null);
				mHodler=new ViewHolder(convertView);
				convertView.setTag(mHodler);
			} else
			{
				mHodler = (ViewHolder) convertView.getTag();
			}
		
		//设定按钮背景图 
		mHodler.checkBoxImg.setBackgroundResource(map.get(position) == null ? R.drawable.pressed : R.drawable.checked);
		mHodler.ringNameTv.setText(ringList.get(position));
		return convertView;
	}
	
	/* 封装类 */
	public static class ViewHolder
	{
		//铃声名字
		public TextView ringNameTv;
		//单选
		public ImageView checkBoxImg;

		public ViewHolder(View v) {
			this.ringNameTv = (TextView) v.findViewById(R.id.select_imagebtn_ring_tv);
			this.checkBoxImg = (ImageView) v.findViewById(R.id.select_imagebtn_btn);
		}
	}

}
