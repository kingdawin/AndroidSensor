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
 * ���ý�������
 * 
 * @author Dawin
 * 
 */
public class GestureLockSettingActivity extends BaseActivity
{

	private GestureLockView gv;
	private TextView textView;
	// �������ü���
	private int count;
	// ��Ч��
	private Animation animation;
	private Context context;
	private static final String FILE_NAME = "ring";

	private String password;
	/** �Ƿ��Ѿ����ù����� */
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
				textView.setText("����ƽ�������");
				// ��������
				isSettedPwd = false;
			}
		/* �Ѿ����ù����� */
		else
			{
				textView.setTextColor(Color.WHITE);
				textView.setText("����ƾ�����");
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
						// ���������
						if (key.equals(sp.getString("password", "null")))
							{
								textView.setTextColor(Color.GREEN);
								textView.setText("����������");
								isSettedPwd = false;
								// ������������
							} else
							{
								textView.setTextColor(Color.RED);
								textView.setText("����!����ƾ�����");
							}
					} else
					{// ��һ����������
						count++;
						if (count == 1)
							{
								password = key;
								textView.setTextColor(Color.WHITE);
								textView.setText("�ٴλ�������");
							} else
							{
								count = 0;
								if (password.equals(key))
									{
										LogUtil.e("[GestureLockSettingActivity] �����õ����룺" + key);
										BaseActivity.password = password;
										editor.putString("password", key);
										editor.commit();
										isSettedPwd = true;
										ToastUtil.makeText(context, "���óɹ�");
										finish();
									} else
									{
										textView.setTextColor(Color.RED);
										textView.setText("���Ʋ�һ��,�����»���");
										textView.startAnimation(animation);
									}
							}
					}

			}
		});

	}
}
