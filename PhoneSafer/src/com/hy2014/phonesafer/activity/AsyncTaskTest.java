/*
*@author Dawin,2015-1-16
*
*
*
*/
package com.hy2014.phonesafer.activity;

import java.util.ArrayList;
import java.util.Vector;

import android.os.AsyncTask;

/**
 * 处理异步加载(在Activity中创建)
 * @author Dawin
 * @deprecated
 */
public class AsyncTaskTest extends AsyncTask<Integer, Integer, String>
{
    /**  
     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改  
     * 
     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作  
     */	
	@Override
	protected String /* 对应AsyncTask的第三个参数*/doInBackground(Integer/*对应AsyncTask中的第一个参数*/... params)
	{
		//publishProgress(values)
		return null;		
	}
	
	/*ArrayList 和Vector都是使用数组方式存储数据，此数组元素数大于实际存储的数据以便增加和插入元素，
	 *它们都允许直接按序号索引元素，但是插入元素要涉及数组元素移动等内存操作，所以索引数据快而插入数据慢，Vector由于使用了synchronized方法（线程安全），
	 *通常性能上较ArrayList差，而LinkedList使用双向链表实现存储，按序号索引数据需要进行前向或后向遍历，但是插入数据时只需要记录本项的前后项即可，所以插入速度较快。
	 */
	
	/**
	 * 这里的Integer参数对应AsyncTask中的第二个参数
	 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
	 * onProgressUpdate是在UI线程中执行，所有可以对UI控件进行操作
	 */
	@Override
	protected void onProgressUpdate(Integer... progress)
	{
		// setProgressPercent(progress[0]);
	}
	
	/**   
     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）  
     * 
     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置  
     * 
     */  
	@Override
	protected void onPostExecute(String result)
	{
		super.onPostExecute(result);
	}
                                                            
	//该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
	}

}
