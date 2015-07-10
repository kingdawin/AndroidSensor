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
	//使用 LruCache 来缓存图片的例子：
	private LruCache<String, Bitmap> mMemoryCache;  
	 ImageView imageView;
	@Override  
	protected void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。  
	    // LruCache通过构造函数传入缓存值，以KB为单位。  
	    int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);  
	    // 使用最大可用内存值的1/8作为缓存的大小。  
	    int cacheSize = maxMemory / 8;  
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {  
	        @Override  
	        protected int sizeOf(String key, Bitmap bitmap) {  
	            // 重写此方法来衡量每张图片的大小，默认返回图片数量。  
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
	//当向 ImageView 中加载一张图片时,首先会在 LruCache 的缓存中进行检查。如果找到了相应的键值，则会立刻更新ImageView ，否则开启一个后台线程来加载这张图片。

	/**
	 * @param resId
	 *            目标图片ID(R.drawable.xx)
	 * @param imageView
	 *            xml的ImageView控件
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
				imageView.setImageResource(0/* R.drawable.image_placeholder */);//正在加载的图片

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

		// 在后台加载图片。
		@Override
		protected Bitmap doInBackground(Integer... params)
		{
			final Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromResource(getResources(), params[0], 100, 100);
			addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
			return bitmap;
		}

		// 加载完成，显示图片
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}
	}
}
