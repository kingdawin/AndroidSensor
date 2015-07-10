package com.hy2014.phonesafer.utils;

import android.os.StrictMode;
import android.util.Log;

/**
 * 日志工具类
 * 
 * @author Dawin DEBUG:控制日志是否打印
 */
public class LogUtil
{
	
	
	// true:打印日志，false:关闭日志
	private static final boolean DEBUG = true;
	// 日志标签
	private static final String TAG = "PhoneSafer";

	// Send a VERBOSE log message
	public static void v(String msg)
	{
		if (DEBUG)
			Log.v(TAG, msg);
	}

	public static void v(Object msg)
	{
		v(msg.toString());
	}

	public static void v(String tag, String msg)
	{
		v(msg);
	}

	// Send a WARN log message
	public static void w(String msg)
	{
		if (DEBUG)
			Log.w(TAG, msg);
	}

	public static void w(Object msg)
	{
		w(msg.toString());
	}

	// Send an ERROR log message
	public static void e(String msg)
	{
		if (DEBUG)
			Log.e(TAG, msg);
	}

	public static void e(Object msg)
	{
		e(msg.toString());
	}

	public static void i(String msg)
	{
		if (DEBUG)
			Log.i(TAG, msg);
	}

	public static void i(Object msg)
	{
		i(msg.toString());
	}

	// Send a DEBUG log message.
	public static void d(String msg)
	{
		if (DEBUG)
			Log.d(TAG, msg);
	}

	public static void d(Object msg)
	{
		d(msg.toString());
	}

	/*-------------------------------------------------------分割线 -------------------------------------------------------*/
	public static void println(String printInfo)
	{
		if (DEBUG && null != printInfo)
			{
				System.out.println(printInfo);
			}
	}

	public static void print(String printInfo)
	{
		if (DEBUG && null != printInfo)
			{
				System.out.print(printInfo);
			}
	}

	/**
	 * 打印类的基本信息
	 */
	public static void printBaseInfo()
	{
		if (DEBUG)
			{
				StringBuffer strBuffer = new StringBuffer();
				StackTraceElement[] stackTrace = new Throwable().getStackTrace();

				strBuffer.append("; class:").append(stackTrace[1].getClassName()).append("; method:")
						.append(stackTrace[1].getMethodName()).append("; number:").append(stackTrace[1].getLineNumber())
						.append("; fileName:").append(stackTrace[1].getFileName());

				println(strBuffer.toString());
			}
	}

	public static void printFileNameAndLinerNumber()
	{
		if (DEBUG)
			{
				StringBuffer strBuffer = new StringBuffer();
				StackTraceElement[] stackTrace = new Throwable().getStackTrace();

				strBuffer.append("; fileName:").append(stackTrace[1].getFileName()).append("; number:")
						.append(stackTrace[1].getLineNumber());

				println(strBuffer.toString());
			}
	}

	public static int printLineNumber()
	{
		if (DEBUG)
			{
				StringBuffer strBuffer = new StringBuffer();
				StackTraceElement[] stackTrace = new Throwable().getStackTrace();

				strBuffer.append("; number:").append(stackTrace[1].getLineNumber());

				println(strBuffer.toString());
				return stackTrace[1].getLineNumber();
			} else
			{
				return 0;
			}
	}

	public static void printMethod()
	{
		if (DEBUG)
			{
				StringBuffer strBuffer = new StringBuffer();
				StackTraceElement[] stackTrace = new Throwable().getStackTrace();

				strBuffer.append("; number:").append(stackTrace[1].getMethodName());

				println(strBuffer.toString());
			}
	}

	public static void printFileNameAndLinerNumber(String printInfo)
	{
		if (null == printInfo || !DEBUG)
			{
				return;
			}
		StringBuffer strBuffer = new StringBuffer();
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();

		strBuffer.append("; fileName:").append(stackTrace[1].getFileName()).append("; number:")
				.append(stackTrace[1].getLineNumber()).append("\n").append((null != printInfo) ? printInfo : "");

		println(strBuffer.toString());
	}

	public static void showStrictMode()
	{
		if (DEBUG)
			{
				StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
						.detectNetwork().penaltyLog().build());
				StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
						.detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
			}
	}

	
}
