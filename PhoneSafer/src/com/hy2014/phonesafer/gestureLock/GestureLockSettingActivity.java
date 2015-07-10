package com.hy2014.phonesafer.gestureLock;

import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.activity.BaseActivity;
import com.hy2014.phonesafer.utils.LogUtil;
import com.hy2014.phonesafer.utils.ToastUtil;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

/**
 * 设置解锁手势
 * 
 * @author Dawin
 * 
 */
public class GestureLockSettingActivity extends BaseActivity
{

	private GestureLockView gv;
	private TextView textView;
	// 密码设置计数
	private int count;
	// 振动效果
	private Animation animation;
	private Context context;
	private static final String FILE_NAME = "ring";

	private String password;
	/** 是否已经设置过手势 */
	private boolean isSettedPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_unlock_setting);
		context = this;
		gv = (GestureLockView) findViewById(R.id.gv);
		textView = (TextView) findViewById(R.id.tv_info);

		sp = getSharedPreferences(FILE_NAME, 1);
		editor = sp.edit();

		if (sp.getString("password", "null").equals("null"))
			{
				textView.setTextColor(Color.WHITE);
				textView.setText("请绘制解锁手势");
				// 设置手势
				isSettedPwd = false;
			}
		/* 已经设置过手势 */
		else
			{
				textView.setTextColor(Color.WHITE);
				textView.setText("请绘制旧手势");
				isSettedPwd = true;
			}

		animation = new TranslateAnimation(-10, 10, 0, 0);
		animation.setDuration(50);
		animation.setRepeatCount(10);
		animation.setRepeatMode(Animation.REVERSE);
		gv.setKey(sp.getString("password", "null"));
		gv.setOnGestureFinishListener(new GestureLockView.OnGestureFinishListener()
		{

			@Override
			public void OnGestureFinish(boolean success, String key)
			{

				if (isSettedPwd)
					{
						// 输入旧手势
						if (key.equals(sp.getString("password", "null")))
							{
								textView.setTextColor(Color.GREEN);
								textView.setText("绘制新手势");
								isSettedPwd = false;
								// 重新设置手势
							} else
							{
								textView.setTextColor(Color.RED);
								textView.setText("错误!请绘制旧手势");
							}
					} else
					{// 第一次设置手势
						count++;
						if (count == 1)
							{
								password = key;
								textView.setTextColor(Color.WHITE);
								textView.setText("再次绘制手势");
							} else
							{
								count = 0;
								if (password.equals(key))
									{
										LogUtil.e("[GestureLockSettingActivity] 你设置的密码：" + key);
										BaseActivity.password = password;
										editor.putString("password", key);
										editor.commit();
										isSettedPwd = true;
										ToastUtil.makeText(context, "设置成功");
										finish();
									} else
									{
										textView.setTextColor(Color.RED);
										textView.setText("手势不一致,请重新绘制");
										textView.startAnimation(animation);
									}
							}
					}

			}
		});

	}
}
