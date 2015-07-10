/*
*@author Dawin,2015-1-20
*
*
*
*/
package com.hy2014.phonesafer.activity;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivityAbstrat extends Activity
{
	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState); 
		init();
	} 
	
	public void init(){
		setContentView();
		findViews();
		getData();
		showContent();
	}

	public abstract void setContentView();
	public abstract void findViews();
	public abstract void getData();
	public abstract void showContent();
}
