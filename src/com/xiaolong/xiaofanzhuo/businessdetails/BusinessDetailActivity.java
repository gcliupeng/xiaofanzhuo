package com.xiaolong.xiaofanzhuo.businessdetails;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.origamilabs.library.views.StaggeredGridView;
import com.xiaolong.xiaofanzhuo.businessorders.BusinessOrderActivity;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.dataoperations.DatabaseAdapter;
import com.xiaolong.xiaofanzhuo.enteractivity.ZoneShowActivity;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.DownloadPicts;
import com.xiaolong.xiaofanzhuo.fileio.ThumbHandler;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/**
 * BussinessDetailActivity
 * 
 * @author hongxiaolong
 * 
 */

@SuppressWarnings("deprecation")
public class BusinessDetailActivity extends BaseActivity {

	private DownloadPicts thread = null;
	private Handler handler = null;
	private BasePictAdapter mAdapter;
	private SlidingDrawer slidingDrawer;
	private ImageView imageView;
	private TextView titleView;

	private ImageView basketImg;
	private TextView tvNum;
	private TextView textView01;
	private TextView textView02;
	private TextView textView03;
	private TextView textView04;

	private long totalNum = 0;
	private String phoneNum = "";

	private String dbName = null;

	private ImageButton buttonBack;
	private ImageButton buttonHome;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(BusinessDetailActivity.this);

		setContentView(R.layout.hong_business_detail);

		StaggeredGridView gridView = (StaggeredGridView) this
				.findViewById(R.id.mycontent);

		// 从Intent 中获取数据
		Bundle bundle = this.getIntent().getExtras();
		final String shopId = bundle.getString("id");
		dbName = shopId;

		slidingDrawer = (SlidingDrawer) findViewById(R.id.sliding_drawer);
		imageView = (ImageView) findViewById(R.id.my_image);

		buttonBack = (ImageButton) findViewById(R.id.button_back);
		buttonHome = (ImageButton) findViewById(R.id.button_home);
		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (thread != null) {
					thread.setStop();
				}
				BusinessDetailActivity.this.finish();
			}
		});

		buttonHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BusinessDetailActivity.this,
						ZoneShowActivity.class);
				startActivity(intent);
				BusinessDetailActivity.this.finish();
			}

		});

		basketImg = (ImageView) findViewById(R.id.shopping_img_cart);
		basketImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (thread != null) {
					thread.setStop();
				}
				Bundle data = new Bundle();
				data.putString("id", shopId);// 把唯一标识符传过去
				Intent intent = new Intent(BusinessDetailActivity.this,
						BusinessOrderActivity.class);
				intent.putExtras(data);
				startActivity(intent);
				BusinessDetailActivity.this.finish();
			}
		});

		tvNum = (TextView) findViewById(R.id.tv_rolla);
		titleView = (TextView) findViewById(R.id.detail_title);
		DataOperations.setTypefaceForTextView(BusinessDetailActivity.this, titleView);
		titleView.setText("商家名称");

		textView01 = (TextView) findViewById(R.id.textview01);
		textView02 = (TextView) findViewById(R.id.textview02);
		textView03 = (TextView) findViewById(R.id.textview03);
		textView04 = (TextView) findViewById(R.id.textview04);
		DataOperations.setTypefaceForTextView(BusinessDetailActivity.this, textView01);
		DataOperations.setTypefaceForTextView(BusinessDetailActivity.this, textView02);
		DataOperations.setTypefaceForTextView(BusinessDetailActivity.this, textView03);
		DataOperations.setTypefaceForTextView(BusinessDetailActivity.this, textView04);

		AsyncHttpClient Down = new AsyncHttpClient();
		final TextView title = titleView;
		Down.get(DataOperations.webServer + shopId + "_ShopName",
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						title.setText(content);
					}
				});
		final TextView avg = textView01;
		Down.get(DataOperations.webServer + shopId + "_ShopAverPrice",
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						avg.setText("人均: " + content);
					}
				});
		final TextView tag = textView02;
		Down.get(DataOperations.webServer + shopId + "_ShopTag",
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						tag.setText("标签: " + content);
					}
				});
		final TextView site = textView03;
		Down.get(DataOperations.webServer + shopId + "_ShopSite",
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						site.setText("地址: " + content);
					}
				});
		final TextView phone = textView04;
		Down.get(DataOperations.webServer + shopId + "_PhoneNum",
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						phoneNum = DataOperations.getActualString(content);
						phone.setText("电话: " + phoneNum);
					}
				});

		textView04.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (thread != null) {
					thread.setStop();
				}
				creatPhoneDialog();
			}
		});

		slidingDrawer
				.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
					public void onDrawerOpened() {
						// textview.setVisibility(View.GONE);
						imageView
								.setImageResource(R.drawable.hong_menu_pull_down);
					}
				});
		slidingDrawer
				.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
					public void onDrawerClosed() {
						// textview.setVisibility(View.VISIBLE);
						imageView
								.setImageResource(R.drawable.hong_menu_pull_up);
					}
				});

		@SuppressWarnings("unused")
		int margin = getResources().getDimensionPixelSize(R.dimen.margin);

		gridView.setFastScrollEnabled(true);

		mAdapter = new BasePictAdapter(BusinessDetailActivity.this);
		mAdapter.setIViewAddAndEventSet(new MenuPictureViewAdd(mAdapter));
		gridView.setAdapter(mAdapter);

		handler = new ThumbHandler(mAdapter);
		start(BusinessDetailActivity.this, "GetShopMenuInfo__" + shopId, shopId);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("action.updateDetailUI");
		registerReceiver(mRefreshBroadcastReceiver, intentFilter);

		refreshData();
		if (0 != totalNum) {
			tvNum.setText(String.valueOf(totalNum));
		}

	}

	private void start(Context context, String requestCode, String database) {
		thread = new DownloadPicts(context, handler, requestCode, database);
		thread.start();
	}

	@Override
	protected void onPause() {
		if (thread != null) {
			thread.setStop();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (thread != null) {
			thread.setStop();
		}
		super.onDestroy();
		MyApplication.getInstance().remove(BusinessDetailActivity.this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		this.unregisterReceiver(mRefreshBroadcastReceiver);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * 弹出提示拨叫对话框
	 */
	private void creatPhoneDialog() {
		new AlertDialog.Builder(this)
				.setMessage("亲，呼叫店家点餐: " + phoneNum)
				.setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent phoneIntent = new Intent(
								"android.intent.action.CALL", Uri.parse("tel:"
										+ phoneNum));
						startActivity(phoneIntent);// 这个activity要把通话界面隐藏以及相应的把菜品界面展示；

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("action.updateDetailUI")) {
				refreshData();
				if (0 == totalNum) {
					tvNum.setText("0");
					return;
				}
				tvNum.setText(String.valueOf(totalNum));
			}
		}
	};

	private void refreshData() {
		totalNum = 0;
		DatabaseAdapter dbHelper = new DatabaseAdapter(this, dbName);
		dbHelper.open();
		Cursor cursor = dbHelper.fetchAllFoods();
		while (cursor.moveToNext()) {
			String quantity = cursor.getString(cursor
					.getColumnIndex("quantity"));
			totalNum += Integer.valueOf(DataOperations
					.getActualString(quantity));
		}
		dbHelper.close();
	}
}