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
 * 网络直接传输对象
 * 
 * @author Dawin
 * 
 */
public class ObjectTransfer
{
	//1构建bean实现Serializable接口
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
	        book.setBookName("Android高级编程");  
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
	   Serializable与Parcelable:
		1. Serializable源于JDK，是为了方便文件操作、数据库、网络流对象序列化；
		2. Parcelable源于Android SDK，是为了在组件中传递数据，序列化效率较高、内存开销小，但不适合数据持久化存储；
		3. Serializable一般不需要自己写实现，不保存静态变量，可以使用Transient关键字对部分字段不进行序列化，也可以覆盖writeObject、readObject方法以实现序列化过程自定义；
		4. Parcelable需要实现writeToParcel、describeContents函数以及静态的CREATOR变量，实际上就是将如何打包和解包的工作自己来定义，而序列化的这些操作完全由底层实现。
		
		*
		*/
}
