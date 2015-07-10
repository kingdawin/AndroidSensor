/*
 *@author Dawin,2015-1-21
 *
 *
 *
 */
package com.hy2014.phonesafer.utils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * ����ֱ�Ӵ������
 * 
 * @author Dawin
 * 
 */
public class ObjectTransfer
{
	//1����beanʵ��Serializable�ӿ�
	class Book implements Serializable
	{

		private String bookName;

		public String getBookName()
		{
			return bookName;
		}

		public void setBookName(String bookName)
		{
			this.bookName = bookName;
		}
	}
	public void objToStream() throws IOException{
		
		 /* 2
		  
		    Book book = new Book();  
	        book.setBookName("Android�߼����");  
	        book.setAuthor("Reto Meier");  
	        book.setPages(398);  
	        book.setPrice(59.00);  
	        URL url = null;  
	        ObjectOutputStream oos = null;  
	        try {  
	            url = new URL("http://192.168.1.103:8080/ServerTest/servlet/TestServlet");  
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();  
	            connection.setDoInput(true);  
	            connection.setDoOutput(true);  
	            connection.setConnectTimeout(10000);  
	            connection.setReadTimeout(10000);  
	            connection.setRequestMethod("POST");  
	            oos = new ObjectOutputStream(connection.getOutputStream());  
	            oos.writeObject(book);  
	            InputStreamReader read = new InputStreamReader(connection.getInputStream());  
	            BufferedReader br = new BufferedReader(read);  
	            String line = "";  
	            while ((line = br.readLine()) != null) {  
	                Log.d("TAG", "line is " + line);  
	            }  
	            br.close();  
	            connection.disconnect();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	  
	        }  */
	}/*
	   Serializable��Parcelable:
		1. SerializableԴ��JDK����Ϊ�˷����ļ����������ݿ⡢�������������л���
		2. ParcelableԴ��Android SDK����Ϊ��������д������ݣ����л�Ч�ʽϸߡ��ڴ濪��С�������ʺ����ݳ־û��洢��
		3. Serializableһ�㲻��Ҫ�Լ�дʵ�֣������澲̬����������ʹ��Transient�ؼ��ֶԲ����ֶβ��������л���Ҳ���Ը���writeObject��readObject������ʵ�����л������Զ��壻
		4. Parcelable��Ҫʵ��writeToParcel��describeContents�����Լ���̬��CREATOR������ʵ���Ͼ��ǽ���δ���ͽ���Ĺ����Լ������壬�����л�����Щ������ȫ�ɵײ�ʵ�֡�
		
		*
		*/
}
