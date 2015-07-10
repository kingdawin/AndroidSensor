/*
 *@author Dawin,2015-1-20
 *
 *
 *
 */
package com.hy2014.phonesafer.fragment;

import com.hy2014.phonesafer.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * fragment使用说明
 * 1onCreateView 绑定布局
 * 2onAttach() 建立Activity关系
 * 
 * @author Dawin
 *
 */
public class Fragment1 extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment1, container, false);
	}
	//创建接口与Activity通信
	public interface onClickListener
	{
		void add(int a, int b);
	}
	//Activity创建完后
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	//建立Fragement和Activity的关系
	@Override
	public void onAttach(Activity activity)
	{
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
}
