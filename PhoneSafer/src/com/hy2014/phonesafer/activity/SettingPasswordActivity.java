/*
 *@author Dawin,2015-1-6
 *
 *
 *
 */
package com.hy2014.phonesafer.activity;

import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.utils.ToastUtil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * 设置解锁密码
 * 
 * @author Dawin
 * 
 */
public class SettingPasswordActivity extends BaseActivity
{
	private EditText edtPassword;
	private EditText edtRepassword;

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_setting);

		sp = getSharedPreferences(FILE_NAME, 1);
		editor = sp.edit();

		edtPassword = (EditText) findViewById(R.id.edt_password);
		edtRepassword = (EditText) findViewById(R.id.edt_repassword);
	}

	public void clickView(View view)
	{
		switch (view.getId())
		{
		case R.id.btn_save:
			checkPassword();
			break;
		case R.id.btn_cancle:
			finish();
			break;
		}
	}

	/**
	 * 保存密码
	 * 
	 */
	public void checkPassword()
	{
		// 检查密码是否一致
		String password = edtPassword.getText().toString().trim();
		String repassword = edtRepassword.getText().toString().trim();
		if (repassword.length() > 0 && password.length() > 0)
			{
				if (password.equals(repassword))
					{
						lockScreenPassword = password;
						editor.putString("lockScreenPassword", password);
						editor.commit();
						ToastUtil.makeText(context, "设置成功");
						
						finish();
					} else
					{
						ToastUtil.makeText(context, "密码不一致");
					}
			} else
			{
				ToastUtil.makeText(context, "密码不能为空");
			}
	}
}
