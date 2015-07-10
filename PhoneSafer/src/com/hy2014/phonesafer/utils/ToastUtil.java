/*
 *@author Dawin,2015-1-29
 *
 *
 *
 */
package com.hy2014.phonesafer.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil
{
	public static void makeText(Context context,String msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
}
