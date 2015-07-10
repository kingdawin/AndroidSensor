package com.hy2014.phonesafer.fragment;
import com.hy2014.phonesafer.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;

/**
 *旋转按钮，调节时间
 * 
 * @author 丸子
 * @version 0.0.1
 */
public class FragmentMainRotatActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        /*----------------fragment测试----------------*/
        setContentView(R.layout.fragment_main);
        //dynamicAddFragment();      
      
        /*----------------旋转按钮，有bug----------------*/
        /*
        setContentView(R.layout.activity_rotate);
        RotatView rotatView=(RotatView)findViewById(R.id.myRotatView);
        rotatView.setRotatDrawableResource(R.drawable.a);
        */
	}

    /*动态添加Fragment主要分为4步：

    1.获取到FragmentManager，在Activity中可以直接通过getFragmentManager得到。

    2.开启一个事务，通过调用beginTransaction方法开启。

    3.向容器内加入Fragment，一般使用replace方法实现，需要传入容器的id和Fragment的实例。

    4.提交事务，调用commit方法提交。
    */
	/**动态获取fragment*/ 
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