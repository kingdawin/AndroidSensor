/*
 *@author Dawin,2015-2-7
 *
 *
 *
 */
package com.hy2014.phonesafer.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

/**
 * 风中压缩图片，缓存图片的方法
 * 
 * @author Dawin
 * 
 */
public class BitmapUtil
{
	//示例,在Activity使用
	public void sample(){
		//mImageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.id.myimage, 100, 100));		
	}
	
	/**
	 * 将图片压缩到指定大小
	 * 
	 * @param res The resources object containing the image data
 
	 * @param resId
	 *            图片资源ID
	 * @param reqWidth
	 *            目标宽度
	 * @param reqHeight
	 *            目标高度
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight)
	{
		// BitmapFactory.Options参数说明：
		// 将这个参数的inJustDecodeBounds属性设置为true就可以让解析方法禁止为bitmap分配内存，
		// 返回值也不再是一个Bitmap对象，而是null。
		// 虽然Bitmap是null了，但是BitmapFactory.Options的outWidth、outHeight和outMimeType属性都会被赋值。
		
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}
	
	/**
	 * 应用程序能用的最大内存
	 */
	public void maxMemory()
	{
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		Log.d("TAG", "Max memory is " + maxMemory + "KB");
	}

	/**
	 * 计算压缩比
	 * 
	 * @param options
	 *            记录了源图的宽高
	 * @param reqWidth
	 *            目标宽
	 * @param reqHeight
	 *            目标高
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth)
			{
				// 计算出实际宽高和目标宽高的比率
				final int heightRatio = Math.round((float) height / (float) reqHeight);
				final int widthRatio = Math.round((float) width / (float) reqWidth);
				// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
				// 一定都会大于等于目标的宽和高。
				inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			}
		return inSampleSize;
	}
	/*---------------------------------------------缓存图片，实现快速加载-------------------------------------------*/
	//使用 LruCache 来缓存图片的例子：
	private LruCache<String, Bitmap> mMemoryCache;  
	/*  
	@Override  
	protected void onCreate(Bundle savedInstanceState) {  
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
	} */

	public void addBitmapToMemoryCache(String key, Bitmap bitmap)
	{
		if (getBitmapFromMemCache(key) == null)
			{
				mMemoryCache.put(key, bitmap);
			}
	}

	public Bitmap getBitmapFromMemCache(String key)
	{
		return mMemoryCache.get(key);
	}  
	//当向 ImageView 中加载一张图片时,首先会在 LruCache 的缓存中进行检查。如果找到了相应的键值，则会立刻更新ImageView ，否则开启一个后台线程来加载这张图片。

	public void loadBitmap(int resId, ImageView imageView) {  
	    final String imageKey = String.valueOf(resId);  
	    final Bitmap bitmap = getBitmapFromMemCache(imageKey);  
	    if (bitmap != null) {  
	        imageView.setImageBitmap(bitmap);  
	    } else {  
	        imageView.setImageResource(0/*R.drawable.image_placeholder*/); 
	        
	        BitmapWorkerTask task = new BitmapWorkerTask(imageView);  
	        task.execute(resId);  
	    }  
	}

	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap>
	{
		private ImageView mImageView;

		public BitmapWorkerTask(ImageView imageView) {
			mImageView = imageView;
		}

		// 在后台加载图片。
		@Override
		protected Bitmap doInBackground(Integer... params)
		{
			final Bitmap bitmap = decodeSampledBitmapFromResource(
			/* getResources() */mImageView.getResources(), params[0], 100, 100);
			addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
			return bitmap;
		}
	}
	
}
