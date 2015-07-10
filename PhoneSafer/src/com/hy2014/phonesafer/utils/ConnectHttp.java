/*
*@author Dawin,2015-1-20
*
*
*
*/
package com.hy2014.phonesafer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络连接工具类
 * @author Dawin
 *
 */
public class ConnectHttp
{
	// 建立网络连接的方法
	public static void httpConnection() throws IOException
	{
		
		int len = 500;
		InputStream is = null;
		try
			{
				URL url = new URL("http://www.baidu.com/");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				conn.setReadTimeout(10000);
				conn.setConnectTimeout(10000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);

				conn.connect();
				int resCode = conn.getResponseCode();// 200 success
				is = conn.getInputStream();
				// Convert the InputStream into a string
				String contentAsString = readIt(is, len);
				LogUtil.w("从百度返回的数据："+contentAsString);
			} catch (Exception e)
			{
				LogUtil.w("httpConnection   error"+e.getMessage());
			} finally
			{
				if (is != null)
					{
						is.close();
					}
			}

	}
	//将接收的数据流转成String
	// Reads an InputStream and converts it to a String.
	public static String readIt(InputStream is, int len) throws IOException
	{
		Reader reader = null;
		reader = new InputStreamReader(is);
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}
	public void badHobby()
	{
		
		/*当同事问起，当领导问起，想想吧，各种吐槽：“你应该告诉我一声”、“怎么不早说！”、“为什么不说！”、“真鸡巴耽误事儿！”。
		 *什么反应都会有的。兄弟，你的形象，那就一落千丈、江河日下了。
		 *搞上几次，你在大家心目中就不再是值得信赖的队友，你这么做是主动逼迫别人不信任你，接踵而至的必然是领导对你的微管理。
		 *正确的工作习惯是及时反馈，紧急的工作优先反馈，让你的伙伴们知道你的状态，这样大家才能相互配合一起前进。    
		 
		 *人生几多风雨，往事何必再提，小龙女早跟了杨过这兔崽子了，有事儿你找他去。
		 *黯然销魂掌
		 *遇到问题，推脱责任拉其他小伙伴背黑锅，垫背，哼哼，以后看我们怎么收拾你
		 
		 *抢功：
		 *演艺界有很多“戏霸”，不管剧情发展，不吝剧情需要，总要突出自己，既搅了别人的戏，又破坏了整个电影或电视剧。
		 */
	}
	public void goodHobby()
	{
		//开发中好的习惯：
		/*
		 * 正确的工作习惯是及时反馈，紧急的工作优先反馈，让你的伙伴们知道你的状态，这样大家才能相互配合一起前进。
		 * 
		 * 不要重用父类的handler，对应一个类的handler也不应该让其子类用到，否则会导致message.what冲突
		 */
		
	}
	public void createBitmapDiff()
	{
		/*
		创建不同的bitmap
		你应该为4种普遍分辨率:低，中，高，超高精度，都提供相适配的bitmap资源。这能帮助你在所有屏幕分辨率中都能有良好的画质和效果。
		要生成这些图像，你应该从原始的矢量图像资源着手，然后根据下列尺寸比例，生成各种密度下的图像。
		xhdpi: 2.0
		hdpi: 1.5
		mdpi: 1.0 (基准)*/

		//例如xhdpi 96x96 那mdpi 48x48
		
	}
}

