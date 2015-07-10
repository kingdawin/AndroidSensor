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
 * ����ѹ��ͼƬ������ͼƬ�ķ���
 * 
 * @author Dawin
 * 
 */
public class BitmapUtil
{
	//ʾ��,��Activityʹ��
	public void sample(){
		//mImageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.id.myimage, 100, 100));		
	}
	
	/**
	 * ��ͼƬѹ����ָ����С
	 * 
	 * @param res The resources object containing the image data
 
	 * @param resId
	 *            ͼƬ��ԴID
	 * @param reqWidth
	 *            Ŀ����
	 * @param reqHeight
	 *            Ŀ��߶�
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight)
	{
		// BitmapFactory.Options����˵����
		// �����������inJustDecodeBounds��������Ϊtrue�Ϳ����ý���������ֹΪbitmap�����ڴ棬
		// ����ֵҲ������һ��Bitmap���󣬶���null��
		// ��ȻBitmap��null�ˣ�����BitmapFactory.Options��outWidth��outHeight��outMimeType���Զ��ᱻ��ֵ��
		
		// ��һ�ν�����inJustDecodeBounds����Ϊtrue������ȡͼƬ��С
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// �������涨��ķ�������inSampleSizeֵ
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// ʹ�û�ȡ����inSampleSizeֵ�ٴν���ͼƬ
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}
	
	/**
	 * Ӧ�ó������õ�����ڴ�
	 */
	public void maxMemory()
	{
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		Log.d("TAG", "Max memory is " + maxMemory + "KB");
	}

	/**
	 * ����ѹ����
	 * 
	 * @param options
	 *            ��¼��Դͼ�Ŀ��
	 * @param reqWidth
	 *            Ŀ���
	 * @param reqHeight
	 *            Ŀ���
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		// ԴͼƬ�ĸ߶ȺͿ��
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth)
			{
				// �����ʵ�ʿ�ߺ�Ŀ���ߵı���
				final int heightRatio = Math.round((float) height / (float) reqHeight);
				final int widthRatio = Math.round((float) width / (float) reqWidth);
				// ѡ���͸�����С�ı�����ΪinSampleSize��ֵ���������Ա�֤����ͼƬ�Ŀ�͸�
				// һ��������ڵ���Ŀ��Ŀ�͸ߡ�
				inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			}
		return inSampleSize;
	}
	/*---------------------------------------------����ͼƬ��ʵ�ֿ��ټ���-------------------------------------------*/
	//ʹ�� LruCache ������ͼƬ�����ӣ�
	private LruCache<String, Bitmap> mMemoryCache;  
	/*  
	@Override  
	protected void onCreate(Bundle savedInstanceState) {  
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
	//���� ImageView �м���һ��ͼƬʱ,���Ȼ��� LruCache �Ļ����н��м�顣����ҵ�����Ӧ�ļ�ֵ��������̸���ImageView ��������һ����̨�߳�����������ͼƬ��

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

		// �ں�̨����ͼƬ��
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
