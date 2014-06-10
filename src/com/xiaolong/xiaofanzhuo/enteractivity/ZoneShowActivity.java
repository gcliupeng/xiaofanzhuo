package com.xiaolong.xiaofanzhuo.enteractivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.xiaolong.xiaofanzhuo.businessdetails.BusinessDetailActivity;
import com.xiaolong.xiaofanzhuo.businesslistings.BusinessListActivity;
import com.xiaolong.xiaofanzhuo.fileio.FileUtil;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.ButtonClickEffect;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/**
 * ZoneShowActivity
 * 
 * @author hongxiaolong
 * 
 */

public class ZoneShowActivity extends BaseActivity {

	ImageButton imageButton1, imageButton2, imageButton3, imageButton4;
	@SuppressWarnings("unused")
	private MyApplication myApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hong_zone_show);

		FileUtil.init();//设置文件缓存路径
		
		myApplication = (MyApplication) getApplication();// 获得我们的应用程序MyApplication

		imageButton1 = (ImageButton) findViewById(R.id.img_dashanzi);
		imageButton2 = (ImageButton) findViewById(R.id.img_head);
		imageButton3 = (ImageButton) findViewById(R.id.img_798);
		imageButton4 = (ImageButton) findViewById(R.id.img_wangjing);

		ButtonClickEffect.setButtonFocusChanged(imageButton1);
		ButtonClickEffect.setButtonFocusChanged(imageButton2);
		ButtonClickEffect.setButtonFocusChanged(imageButton3);
		ButtonClickEffect.setButtonFocusChanged(imageButton4);

		imageButton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
//				intent.putExtra("area", "1");
				intent.setClass(ZoneShowActivity.this, BusinessDetailActivity.class);
     			intent.putExtra("area", "1");
				intent.setClass(ZoneShowActivity.this, BusinessListActivity.class);
				startActivity(intent);
				ZoneShowActivity.this.finish();
			}
		});

		// personal information, login
		imageButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ZoneShowActivity.this, LoginActivity.class);
				startActivity(intent);
				ZoneShowActivity.this.finish();
			}
		});

		imageButton3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("area", "2");
				intent.setClass(ZoneShowActivity.this, BusinessListActivity.class);
				startActivity(intent);
				ZoneShowActivity.this.finish();
			}
		});

		imageButton4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("area", "3");
				intent.setClass(ZoneShowActivity.this, BusinessListActivity.class);
				startActivity(intent);
				ZoneShowActivity.this.finish();
			}
		});
	}

}
