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
 * �����첽����(��Activity�д���)
 * @author Dawin
 * @deprecated
 */
public class AsyncTaskTest extends AsyncTask<Integer, Integer, String>
{
    /**  
     * �÷�������������UI�̵߳��У���Ҫ�����첽�����������ڸ÷����в��ܶ�UI���еĿռ�������ú��޸�  
     * 
     * ���ǿ��Ե���publishProgress��������onProgressUpdate��UI���в���  
     */	
	@Override
	protected String /* ��ӦAsyncTask�ĵ���������*/doInBackground(Integer/*��ӦAsyncTask�еĵ�һ������*/... params)
	{
		//publishProgress(values)
		return null;		
	}
	
	/*ArrayList ��Vector����ʹ�����鷽ʽ�洢���ݣ�������Ԫ��������ʵ�ʴ洢�������Ա����ӺͲ���Ԫ�أ�
	 *���Ƕ�����ֱ�Ӱ��������Ԫ�أ����ǲ���Ԫ��Ҫ�漰����Ԫ���ƶ����ڴ�����������������ݿ��������������Vector����ʹ����synchronized�������̰߳�ȫ����
	 *ͨ�������Ͻ�ArrayList���LinkedListʹ��˫������ʵ�ִ洢�����������������Ҫ����ǰ��������������ǲ�������ʱֻ��Ҫ��¼�����ǰ����ɣ����Բ����ٶȽϿ졣
	 */
	
	/**
	 * �����Integer������ӦAsyncTask�еĵڶ�������
	 * ��doInBackground�������У���ÿ�ε���publishProgress�������ᴥ��onProgressUpdateִ��
	 * onProgressUpdate����UI�߳���ִ�У����п��Զ�UI�ؼ����в���
	 */
	@Override
	protected void onProgressUpdate(Integer... progress)
	{
		// setProgressPercent(progress[0]);
	}
	
	/**   
     * �����String������ӦAsyncTask�еĵ�����������Ҳ���ǽ���doInBackground�ķ���ֵ��  
     * 
     * ��doInBackground����ִ�н���֮�������У�����������UI�̵߳��� ���Զ�UI�ռ��������  
     * 
     */  
	@Override
	protected void onPostExecute(String result)
	{
		super.onPostExecute(result);
	}
                                                            
	//�÷���������UI�̵߳���,����������UI�̵߳��� ���Զ�UI�ռ��������
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
	}

}
