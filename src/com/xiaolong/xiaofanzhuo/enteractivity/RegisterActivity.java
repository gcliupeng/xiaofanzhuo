package com.xiaolong.xiaofanzhuo.enteractivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

public class RegisterActivity extends BaseActivity {

	private static final String TAG = "RegisterActivity";
	private EditText editTextPhoneR;
	private EditText editTextPasswordR;
	private EditText editTextRePasswordR;
	private Button buttonR;
	
	private TextView titleView;
	private String phoneNumber = "";
	private String password = "";
	private String rePassword = "";

	private ImageButton buttonBack;
	private ImageButton buttonHome;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.hong_register);

		editTextPhoneR = (EditText) findViewById(R.id.register_phonenumber);
		editTextPasswordR = (EditText) findViewById(R.id.register_password);
		editTextRePasswordR = (EditText) findViewById(R.id.register_repassword);
		
		buttonR = (Button) findViewById(R.id.regsiter_registerbutton);
		buttonBack = (ImageButton) findViewById(R.id.button_back);
		buttonHome = (ImageButton) findViewById(R.id.button_home);
		titleView = (TextView) findViewById(R.id.detail_title);
		DataOperations.setTypefaceForTextView(RegisterActivity.this, titleView);
		titleView.setText("用户登录");
			
		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RegisterActivity.this.finish();
			}

		});
		
		buttonHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, ZoneShowActivity.class);
				startActivity(intent);
				RegisterActivity.this.finish();
			}

		});

		buttonR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				phoneNumber = editTextPhoneR.getText().toString();
				Log.i(TAG, phoneNumber + "1");
				password = editTextPasswordR.getText().toString();
				rePassword = editTextRePasswordR.getText().toString();
				if (!password.equals(rePassword)) {
					Toast.makeText(RegisterActivity.this, "输入密码不一致!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				AccountAuthentication authentication = new AccountAuthentication();
				AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
				if (!authentication.checkup(phoneNumber, password, builder))
					return;
				
				String requestCode = "WhiteZhuCe_Username_Passwd____"
						+ phoneNumber + "_" + password;
				
				AsyncHttpClient Down = new AsyncHttpClient();
				Down.get(DataOperations.webServer + requestCode,
						new AsyncHttpResponseHandler() {
					
							@SuppressWarnings("deprecation")
							@Override
							public void onFailure(Throwable error,
									String content) {
								// TODO Auto-generated method stub
								super.onFailure(error, content);
								Toast.makeText(RegisterActivity.this, "亲，网络不给力，请稍后!",
										Toast.LENGTH_SHORT).show();
							}

							@SuppressWarnings("deprecation")
							@Override
							public void onSuccess(int statusCode, String content) {
								// TODO Auto-generated method stub
								super.onSuccess(statusCode, content);
								if (content.contains("WhiteZhuCe_Result____UsernameTRUE_PasswdTRUE")) {
									Toast.makeText(RegisterActivity.this, "注册成功!",
											Toast.LENGTH_SHORT).show();
									Intent intent = new Intent();
									intent.setClass(RegisterActivity.this,
											PersonInfoActivity.class);
									startActivity(intent);
									RegisterActivity.this.finish();
									return;
								}
								if (content.contains("WhiteZhuCe_Result____UsernameFALSE_PasswdFALSE")) {
									Toast.makeText(RegisterActivity.this,
											"注册失败，请检查用户名、密码或网络!", Toast.LENGTH_SHORT)
											.show();
									return;
								}
								Toast.makeText(RegisterActivity.this, "亲，网络不给力，请稍后!",
										Toast.LENGTH_SHORT).show();
								Log.i(TAG, "注册服务器返回信息未定义!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							}
						});

			}
		});

		editTextRePasswordR.setOnEditorActionListener(new EditText.OnEditorActionListener() {

		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_DONE) {
		            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					
		            
		            
		            phoneNumber = editTextPhoneR.getText().toString();
					Log.i(TAG, phoneNumber + "1");
					password = editTextPasswordR.getText().toString();
					rePassword = editTextRePasswordR.getText().toString();
					if (!password.equals(rePassword)) {
						Toast.makeText(RegisterActivity.this, "输入密码不一致!",
								Toast.LENGTH_SHORT).show();
						return false;
					}
					
					AccountAuthentication authentication = new AccountAuthentication();
					AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
					if (!authentication.checkup(phoneNumber, password, builder))
						return false;
					
					String requestCode = "WhiteZhuCe_Username_Passwd____"
							+ phoneNumber + "_" + password;
					
					AsyncHttpClient Down = new AsyncHttpClient();
					Down.get(DataOperations.webServer + requestCode,
							new AsyncHttpResponseHandler() {
						
								@SuppressWarnings("deprecation")
								@Override
								public void onFailure(Throwable error,
										String content) {
									// TODO Auto-generated method stub
									super.onFailure(error, content);
									Toast.makeText(RegisterActivity.this, "亲，网络不给力，请稍后!",
											Toast.LENGTH_SHORT).show();
								}

								@SuppressWarnings("deprecation")
								@Override
								public void onSuccess(int statusCode, String content) {
									// TODO Auto-generated method stub
									super.onSuccess(statusCode, content);
									if (content.contains("WhiteZhuCe_Result____UsernameTRUE_PasswdTRUE")) {
										Toast.makeText(RegisterActivity.this, "注册成功!",
												Toast.LENGTH_SHORT).show();
										Intent intent = new Intent();
										intent.setClass(RegisterActivity.this,
												PersonInfoActivity.class);
										startActivity(intent);
										RegisterActivity.this.finish();
										return;
									}
									if (content.contains("WhiteZhuCe_Result____UsernameFALSE_PasswdFALSE")) {
										Toast.makeText(RegisterActivity.this,
												"注册失败，请检查用户名、密码或网络!", Toast.LENGTH_SHORT)
												.show();
										return;
									}
									Toast.makeText(RegisterActivity.this, "亲，网络不给力，请稍后!",
											Toast.LENGTH_SHORT).show();
									Log.i(TAG, "注册服务器返回信息未定义!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
								}
							});
		            
					
		            return true;  
		        }
		        return false;
		    }
			
		});
		
	}

}
