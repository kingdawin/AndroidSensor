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
 * fragmentʹ��˵��
 * 1onCreateView �󶨲���
 * 2onAttach() ����Activity��ϵ
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
	//�����ӿ���Activityͨ��
	public interface onClickListener
	{
		void add(int a, int b);
	}
	//Activity�������
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	//����Fragement��Activity�Ĺ�ϵ
	@Override
	public void onAttach(Activity activity)
	{
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
}
