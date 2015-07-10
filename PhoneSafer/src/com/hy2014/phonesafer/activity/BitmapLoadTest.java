/*
*@author Dawin,2015-2-7
*
*
*
*/
package com.hy2014.phonesafer.activity;

import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.utils.BitmapUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class BitmapLoadTest extends Activity
{
	//ʹ�� LruCache ������ͼƬ�����ӣ�
	private LruCache<String, Bitmap> mMemoryCache;  
	 ImageView imageView;
	@Override  
	protected void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    // ��ȡ�������ڴ�����ֵ��ʹ���ڴ泬�����ֵ������OutOfMemory�쳣��  
	    // LruCacheͨ�����캯�����뻺��ֵ����KBΪ��λ��  
	    int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);  
	    // ʹ���������ڴ�ֵ��1/8��Ϊ����Ĵ�С��  
	    int cacheSize = maxMemory / 8;  
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {  
	        @Override  
	        protected int sizeOf(String key, Bitmap bitmap) {  
	            // ��д�˷���������ÿ��ͼƬ�Ĵ�С��Ĭ�Ϸ���ͼƬ������  
	            return bitmap.getByteCount() / 1024;  
	        }  
	    };  
	    imageView=(ImageView)findViewById(R.id.first_img);
	    loadBitmap(R.drawable.vibrator_remind, imageView);
	}  
	  
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {  
	    if (getBitmapFromMemCache(key) == null) {  
	        mMemoryCache.put(key, bitmap);  
	    }  
	}  
	  
	public Bitmap getBitmapFromMemCache(String key) {  
	    return mMemoryCache.get(key);  
	}  
	//���� ImageView �м���һ��ͼƬʱ,���Ȼ��� LruCache �Ļ����н��м�顣����ҵ�����Ӧ�ļ�ֵ��������̸���ImageView ��������һ����̨�߳�����������ͼƬ��

	/**
	 * @param resId
	 *            Ŀ��ͼƬID(R.drawable.xx)
	 * @param imageView
	 *            xml��ImageView�ؼ�
	 */
	public void loadBitmap(int resId, ImageView imageView)
	{
		final String imageKey = String.valueOf(resId);
		final Bitmap bitmap = getBitmapFromMemCache(imageKey);
		if (bitmap != null)
			{
				imageView.setImageBitmap(bitmap);
			} else
			{
				imageView.setImageResource(0/* R.drawable.image_placeholder */);//���ڼ��ص�ͼƬ

				BitmapWorkerTask task = new BitmapWorkerTask(imageView);
				task.execute(resId);
			}
	}

	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap>
	{
		// private ImageView mImageView;
		public BitmapWorkerTask(ImageView imageView) {
			// mImageView=imageView;
		}

		// �ں�̨����ͼƬ��
		@Override
		protected Bitmap doInBackground(Integer... params)
		{
			final Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromResource(getResources(), params[0], 100, 100);
			addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
			return bitmap;
		}

		// ������ɣ���ʾͼƬ
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}
	}
}
