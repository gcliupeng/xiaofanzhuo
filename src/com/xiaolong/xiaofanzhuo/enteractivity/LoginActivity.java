package com.xiaolong.xiaofanzhuo.enteractivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/**
 * LoginActivity
 * 
 * @author hongxiaolong
 * 
 */

public class LoginActivity extends BaseActivity {

	private static final String TAG = "LoginActivity";
	static final int CANSHU = 1;
	private EditText edTextUser, edTextSecretCode;
	private Button buttonLogin;
	private Button buttonRegister;
	private TextView titleView;

	private String userName = "";
	private String password = "";
	private CheckBox keepPassword;
	private CheckBox autoLogin;
	private SharedPreferences sp;
	private boolean autoLoginFlag = false;
	private boolean keepPwdFlag = false;
	private ImageButton buttonBack;
	private ImageButton buttonHome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hong_login);

		sp = this.getSharedPreferences("xiaofanzhuologininfo", MODE_PRIVATE);

		edTextUser = (EditText) findViewById(R.id.edittext_user);
		edTextSecretCode = (EditText) findViewById(R.id.edittext_secretcode);
		keepPassword = (CheckBox) findViewById(R.id.checkbox_secretcode);
		autoLogin = (CheckBox) findViewById(R.id.checkbox_autologin);
		buttonLogin = (Button) findViewById(R.id.button_login);
		buttonRegister = (Button) findViewById(R.id.button_regeist);

		buttonBack = (ImageButton) findViewById(R.id.button_back);
		buttonHome = (ImageButton) findViewById(R.id.button_home);
		titleView = (TextView) findViewById(R.id.detail_title);
		DataOperations.setTypefaceForTextView(LoginActivity.this, titleView);
		titleView.setText("用户登录");
		
		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, ZoneShowActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
			}

		});
		
		buttonHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, ZoneShowActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
			}

		});

		checkAutoLogin();
		checkKeepPwd();

		buttonLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				userName = edTextUser.getText().toString();
				password = edTextSecretCode.getText().toString();

				if (userName.equals("")) {
					Toast.makeText(LoginActivity.this, "请输入手机号",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (password.equals("")) {
					Toast.makeText(LoginActivity.this, "请输入密码",
							Toast.LENGTH_SHORT).show();
					return;
				}

				AccountAuthentication authentication = new AccountAuthentication();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						LoginActivity.this);
				if (!authentication.checkup(userName, password, builder))
					return;

				String requestCode = "WhiteLogin____" + userName + "_"
						+ password;
				AsyncHttpClient Down = new AsyncHttpClient();
				Down.get(DataOperations.webServer + requestCode,
						new AsyncHttpResponseHandler() {

							@SuppressWarnings("deprecation")
							@Override
							public void onFailure(Throwable error,
									String content) {
								// TODO Auto-generated method stub
								super.onFailure(error, content);
								Toast.makeText(LoginActivity.this,
										"亲，网络不给力，请稍后!", Toast.LENGTH_SHORT)
										.show();
							}

							@SuppressWarnings("deprecation")
							@Override
							public void onSuccess(int statusCode, String content) {
								// TODO Auto-generated method stub
								super.onSuccess(statusCode, content);
								if (content
										.contains("WhiteLogin_Result____UsernameTRUE_PasswdTRUE")) {

									Toast.makeText(LoginActivity.this, "登录成功!",
											Toast.LENGTH_SHORT).show();

									if (true == autoLoginFlag
											|| true == keepPwdFlag) {
										sp.edit()
												.putString("USERNAME", userName)
												.commit();
										sp.edit()
												.putString("PASSWORD", password)
												.commit();
									}

									Intent intent = new Intent();
									intent.setClass(LoginActivity.this,
											PersonInfoActivity.class);
									startActivity(intent);
									LoginActivity.this.finish();
									return;
								}
								if (content
										.contains("WhiteLogin_Result____UsernameFALSE_PasswdFALSE")) {
									Toast.makeText(LoginActivity.this,
											"登录失败，用户名或密码错误!",
											Toast.LENGTH_SHORT).show();
									return;
								}
								Toast.makeText(LoginActivity.this,
										"亲，网络不给力，请稍后!", Toast.LENGTH_SHORT)
										.show();
								Log.i(TAG,
										"登录服务器返回信息未定义!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							}
						});
			}
		});

		keepPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (keepPassword.isChecked()) {
					keepPwdFlag = true;
					sp.edit().putBoolean("KEEPPWD", true).commit();
				} else {
					sp.edit().putBoolean("KEEPPWD", false).commit();
				}

			}
		});

		autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (autoLogin.isChecked()) {
					autoLoginFlag = true;
					keepPassword.setChecked(true);
					sp.edit().putBoolean("AUTOLOGIN", true).commit();
				} else {
					sp.edit().putBoolean("AUTOLOGIN", false).commit();
				}

			}
		});

		buttonRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				LoginActivity.this.startActivity(intent);
				// LoginActivity.this.finish();
			}

		});
		
		edTextSecretCode.setOnEditorActionListener(new EditText.OnEditorActionListener() {

		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_DONE) {
		            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					
		            userName = edTextUser.getText().toString();
					password = edTextSecretCode.getText().toString();

					if (userName.equals("")) {
						Toast.makeText(LoginActivity.this, "请输入手机号",
								Toast.LENGTH_SHORT).show();
						return false;
					}
					if (password.equals("")) {
						Toast.makeText(LoginActivity.this, "请输入密码",
								Toast.LENGTH_SHORT).show();
						return false;
					}

					AccountAuthentication authentication = new AccountAuthentication();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							LoginActivity.this);
					if (!authentication.checkup(userName, password, builder))
						return false;

					String requestCode = "WhiteLogin____" + userName + "_"
							+ password;
					AsyncHttpClient Down = new AsyncHttpClient();
					Down.get(DataOperations.webServer + requestCode,
							new AsyncHttpResponseHandler() {

								@SuppressWarnings("deprecation")
								@Override
								public void onFailure(Throwable error,
										String content) {
									// TODO Auto-generated method stub
									super.onFailure(error, content);
									Toast.makeText(LoginActivity.this,
											"亲，网络不给力，请稍后!", Toast.LENGTH_SHORT)
											.show();
								}

								@SuppressWarnings("deprecation")
								@Override
								public void onSuccess(int statusCode, String content) {
									// TODO Auto-generated method stub
									super.onSuccess(statusCode, content);
									if (content
											.contains("WhiteLogin_Result____UsernameTRUE_PasswdTRUE")) {

										Toast.makeText(LoginActivity.this, "登录成功!",
												Toast.LENGTH_SHORT).show();

										if (true == autoLoginFlag
												|| true == keepPwdFlag) {
											sp.edit()
													.putString("USERNAME", userName)
													.commit();
											sp.edit()
													.putString("PASSWORD", password)
													.commit();
										}

										Intent intent = new Intent();
										intent.setClass(LoginActivity.this,
												PersonInfoActivity.class);
										startActivity(intent);
										LoginActivity.this.finish();
										return;
									}
									if (content
											.contains("WhiteLogin_Result____UsernameFALSE_PasswdFALSE")) {
										Toast.makeText(LoginActivity.this,
												"登录失败，用户名或密码错误!",
												Toast.LENGTH_SHORT).show();
										return;
									}
									Toast.makeText(LoginActivity.this,
											"亲，网络不给力，请稍后!", Toast.LENGTH_SHORT)
											.show();
									Log.i(TAG,
											"登录服务器返回信息未定义!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
								}
							});
		           
		            
					
		            return true;  
		        }
		        return false;
		    }
			
		});
		
	}

	private void checkAutoLogin() {

		if (sp.contains("KEEPPWD") && sp.contains("AUTOLOGIN")
				&& sp.contains("USERNAME") && sp.contains("PASSWORD")) {

			boolean autoFlag = sp.getBoolean("AUTOLOGIN", false);
			if (true == autoFlag) {
				autoLogin.setChecked(true);
				keepPwdFlag = true;
				autoLoginFlag = true;

				String user = sp.getString("USERNAME", "");
				String pwd = sp.getString("PASSWORD", "");
				String requestCode = "WhiteLogin____" + user + "_" + pwd;

				AsyncHttpClient Down = new AsyncHttpClient();
				Down.get(DataOperations.webServer + requestCode,
						new AsyncHttpResponseHandler() {

							@SuppressWarnings("deprecation")
							@Override
							public void onFailure(Throwable error,
									String content) {
								// TODO Auto-generated method stub
								super.onFailure(error, content);
								Toast.makeText(LoginActivity.this,
										"亲，网络不给力，请稍后!", Toast.LENGTH_SHORT)
										.show();
							}

							@SuppressWarnings("deprecation")
							@Override
							public void onSuccess(int statusCode, String content) {
								// TODO Auto-generated method stub
								super.onSuccess(statusCode, content);
								if (content
										.contains("WhiteLogin_Result____UsernameTRUE_PasswdTRUE")) {
									Toast.makeText(LoginActivity.this, "自动登录!",
											Toast.LENGTH_SHORT).show();

									Intent intent = new Intent();
									intent.setClass(LoginActivity.this,
											PersonInfoActivity.class);
									startActivity(intent);
									LoginActivity.this.finish();
									return;
								}
								if (content
										.contains("WhiteLogin_Result____UsernameFALSE_PasswdFALSE")) {
									Toast.makeText(LoginActivity.this,
											"登录失败，用户名或密码错误!",
											Toast.LENGTH_SHORT).show();
									return;
								}
								Log.i(TAG,
										"登录服务器返回信息未定义!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							}
						});
			}
		}
		return;
	}

	private void checkKeepPwd() {

		if (sp.contains("KEEPPWD") && sp.contains("USERNAME")
				&& sp.contains("PASSWORD")) {
			boolean keepFlag = sp.getBoolean("KEEPPWD", false);
			if (true == keepFlag) {
				keepPassword.setChecked(true);
				keepPwdFlag = true;
				String user = sp.getString("USERNAME", "");
				String pwd = sp.getString("PASSWORD", "");
				edTextUser.setText(user);
				edTextSecretCode.setText(pwd);
			}
		}
	}

}
