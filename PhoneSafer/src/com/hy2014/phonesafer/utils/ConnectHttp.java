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
 * �������ӹ�����
 * @author Dawin
 *
 */
public class ConnectHttp
{
	// �����������ӵķ���
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
				LogUtil.w("�Ӱٶȷ��ص����ݣ�"+contentAsString);
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
	//�����յ�������ת��String
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
		
		/*��ͬ�����𣬵��쵼��������ɣ������²ۣ�����Ӧ�ø�����һ����������ô����˵��������Ϊʲô��˵���������漦�͵����¶�������
		 *ʲô��Ӧ�����еġ��ֵܣ���������Ǿ�һ��ǧ�ɡ����������ˡ�
		 *���ϼ��Σ����ڴ����Ŀ�оͲ�����ֵ�������Ķ��ѣ�����ô�����������ȱ��˲������㣬��������ı�Ȼ���쵼�����΢����
		 *��ȷ�Ĺ���ϰ���Ǽ�ʱ�����������Ĺ������ȷ���������Ļ����֪�����״̬��������Ҳ����໥���һ��ǰ����    
		 
		 *����������꣬���ºα����ᣬС��Ů�����������������ˣ����¶�������ȥ��
		 *��Ȼ������
		 *�������⣬��������������С��鱳�ڹ����汳���ߺߣ��Ժ�������ô��ʰ��
		 
		 *������
		 *���ս��кܶࡰϷ�ԡ������ܾ��鷢չ�����߾�����Ҫ����Ҫͻ���Լ����Ƚ��˱��˵�Ϸ�����ƻ���������Ӱ����Ӿ硣
		 */
	}
	public void goodHobby()
	{
		//�����кõ�ϰ�ߣ�
		/*
		 * ��ȷ�Ĺ���ϰ���Ǽ�ʱ�����������Ĺ������ȷ���������Ļ����֪�����״̬��������Ҳ����໥���һ��ǰ����
		 * 
		 * ��Ҫ���ø����handler����Ӧһ�����handlerҲ��Ӧ�����������õ�������ᵼ��message.what��ͻ
		 */
		
	}
	public void createBitmapDiff()
	{
		/*
		������ͬ��bitmap
		��Ӧ��Ϊ4���ձ�ֱ���:�ͣ��У��ߣ����߾��ȣ����ṩ�������bitmap��Դ�����ܰ�������������Ļ�ֱ����ж��������õĻ��ʺ�Ч����
		Ҫ������Щͼ����Ӧ�ô�ԭʼ��ʸ��ͼ����Դ���֣�Ȼ��������гߴ���������ɸ����ܶ��µ�ͼ��
		xhdpi: 2.0
		hdpi: 1.5
		mdpi: 1.0 (��׼)*/

		//����xhdpi 96x96 ��mdpi 48x48
		
	}
}

