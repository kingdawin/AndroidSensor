package com.hy2014.phonesafer.fragment;
import com.hy2014.phonesafer.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;

/**
 *��ת��ť������ʱ��
 * 
 * @author ����
 * @version 0.0.1
 */
public class FragmentMainRotatActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        /*----------------fragment����----------------*/
        setContentView(R.layout.fragment_main);
        //dynamicAddFragment();      
      
        /*----------------��ת��ť����bug----------------*/
        /*
        setContentView(R.layout.activity_rotate);
        RotatView rotatView=(RotatView)findViewById(R.id.myRotatView);
        rotatView.setRotatDrawableResource(R.drawable.a);
        */
	}

    /*��̬���Fragment��Ҫ��Ϊ4����

    1.��ȡ��FragmentManager����Activity�п���ֱ��ͨ��getFragmentManager�õ���

    2.����һ������ͨ������beginTransaction����������

    3.�������ڼ���Fragment��һ��ʹ��replace����ʵ�֣���Ҫ����������id��Fragment��ʵ����

    4.�ύ���񣬵���commit�����ύ��
    */
	/**��̬��ȡfragment*/ 
	public void dynamicAddFragment()
	{
		Display display = getWindowManager().getDefaultDisplay();
		if (display.getWidth() > display.getHeight())
			{
				Fragment1 fragment1 = new Fragment1();
				getFragmentManager().beginTransaction().replace(R.id.main_layout, fragment1).commit();
			} else
			{
				Fragment2 fragment2 = new Fragment2();
				getFragmentManager().beginTransaction().replace(R.id.main_layout, fragment2).commit();
			}
	}
}