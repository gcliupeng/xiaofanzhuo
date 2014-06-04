package com.xiaolong.xiaofanzhuo.businessorders;

import java.util.ArrayList;
import java.util.List;

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
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xiaolong.xiaofanzhuo.businessdetails.BusinessDetailActivity;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.dataoperations.DatabaseAdapter;
import com.xiaolong.xiaofanzhuo.enteractivity.ZoneShowActivity;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.DownloadPicts;
import com.xiaolong.xiaofanzhuo.fileio.ThumbHandler;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/*
 * BusinessOrderActivity
 * @author hongxiaolong
 */

public class BusinessOrderActivity extends BaseActivity {

	private static final String TAG = "BusinessOrderActivity";

	private BasePictAdapter mAdapter;
	private ListView mListView;

	private TextView tvNum;
	private ImageView basketImg;
	private ImageView phoneImg;
	private TextView totalView;
	private TextView titleView;;

	private DownloadPicts thread = null;
	private Handler handler = null;

	private long totalPrice = 0;
	private long totalNum = 0;
	private String phoneNum = "";

	private Bundle extraData;
	private String dbName;

	private ImageButton buttonBack;
	private ImageButton buttonHome;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(BusinessOrderActivity.this);
		setContentView(R.layout.hong_menu_order_main);

		// 从Intent 中获取数据
		extraData = this.getIntent().getExtras();
		String shopId = extraData.getString("id");
		dbName = shopId;

		mListView = (ListView) findViewById(R.id.menu_order_listview);
		basketImg = (ImageView) findViewById(R.id.shopping_img_cart);
		tvNum = (TextView) findViewById(R.id.tv_rolla);
		DataOperations.setTypefaceForTextView(BusinessOrderActivity.this, tvNum);
		phoneImg = (ImageView) findViewById(R.id.order_phone_img);
		totalView = (TextView) findViewById(R.id.order_total_price);
		DataOperations.setTypefaceForTextView(BusinessOrderActivity.this, totalView);
		titleView = (TextView) findViewById(R.id.detail_title);
		DataOperations.setTypefaceForTextView(BusinessOrderActivity.this, titleView);
		titleView.setText("商家名称");
		AsyncHttpClient Down = new AsyncHttpClient();
		final TextView title = titleView;
		Down.get(DataOperations.webServer + shopId + "_ShopName",
				new AsyncHttpResponseHandler() {
					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						title.setText(content);
					}
				});
		Down.get(DataOperations.webServer + shopId + "_PhoneNum",
				new AsyncHttpResponseHandler() {
					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);
						phoneNum = content;
					}
				});

		buttonBack = (ImageButton) findViewById(R.id.button_back);
		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (thread != null) {
					thread.setStop();
				}

				BusinessOrderActivity.this.finish();
				Intent intent = new Intent(BusinessOrderActivity.this,
						BusinessDetailActivity.class);
				intent.putExtras(extraData);
				startActivity(intent);

			}
		});

		buttonHome = (ImageButton) findViewById(R.id.button_home);
		buttonHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BusinessOrderActivity.this,
						ZoneShowActivity.class);
				startActivity(intent);
				BusinessOrderActivity.this.finish();
			}

		});

		basketImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				creatBasketDialog();
			}
		});

		phoneImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				creatPhoneDialog();
			}
		});

		mAdapter = new BasePictAdapter(BusinessOrderActivity.this);
		mAdapter.setIViewAddAndEventSet(new OrderPictureViewAdd(mAdapter));
		mListView.setAdapter(mAdapter);

		final DatabaseAdapter dbHelper = new DatabaseAdapter(this, dbName);
		dbHelper.open();
		Cursor cursor = dbHelper.fetchAllFoods();
		List<String> ids = new ArrayList<String>();
		List<String> quantities = new ArrayList<String>();
		List<String> prices = new ArrayList<String>();
		while (cursor.moveToNext()) {
			System.out.println(TAG + " "
					+ cursor.getInt(cursor.getColumnIndex("id")) + " | "
					+ cursor.getString(cursor.getColumnIndex("food")) + " | "
					+ cursor.getString(cursor.getColumnIndex("quantity"))
					+ " | " + cursor.getString(cursor.getColumnIndex("price")));
			ids.add(cursor.getString(cursor.getColumnIndex("food")));
			String tQuantity = DataOperations.getActualString(cursor
					.getString(cursor.getColumnIndex("quantity")));
			String tPrice = DataOperations.getActualString(cursor
					.getString(cursor.getColumnIndex("price")));
			quantities.add(tQuantity);
			prices.add(tPrice);
			totalPrice = totalPrice + Integer.valueOf(tQuantity)
					* Integer.valueOf(tPrice);
			totalNum = totalNum + Integer.valueOf(tQuantity);
		}
		dbHelper.close();

		handler = new ThumbHandler(mAdapter);
		start(BusinessOrderActivity.this, dbName, ids, quantities, prices);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("action.updateOrderUI");
		registerReceiver(mRefreshBroadcastReceiver, intentFilter);

		if (0 != totalPrice) {
			totalView.setText("总价: " + String.valueOf(totalPrice) + "元");
			tvNum.setText(String.valueOf(totalNum));
		}
	}

	private void start(Context context, String database, List<String> ids,
			List<String> quantities, List<String> prices) {
		thread = new DownloadPicts(context, handler, database, ids, quantities,
				prices);
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
		MyApplication.getInstance().remove(BusinessOrderActivity.this);
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
	 * 弹出提示清空对话框
	 */
	private void creatBasketDialog() {
		new AlertDialog.Builder(this).setMessage("亲，您确定要清空购物篮么?")
				.setPositiveButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (0 == totalPrice)
							Toast.makeText(getApplicationContext(),
									"亲，购物篮是空的哦，请先点餐!!", Toast.LENGTH_SHORT)
									.show();
						else {
							final DatabaseAdapter dbHelper = new DatabaseAdapter(
									BusinessOrderActivity.this, dbName);
							dbHelper.open();
							dbHelper.clean();
							dbHelper.close();

							Intent intent = new Intent(
									BusinessOrderActivity.this,
									BusinessDetailActivity.class);
							intent.putExtras(extraData);
							startActivity(intent);
							BusinessOrderActivity.this.finish();
							
							Toast toast = Toast.makeText(getApplicationContext(),
									"亲，购物篮已清空，您可以尽情点餐!!", Toast.LENGTH_SHORT);
								    //可以控制toast显示的位置
								    toast.setGravity(Gravity.CENTER, 0, 0);
								    toast.show();
								    
						}
					}
				}).show();
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
						if (0 == totalPrice)
							Toast.makeText(getApplicationContext(),
									"亲，购物篮是空的哦，请先点餐!!", Toast.LENGTH_SHORT)
									.show();
						else {
							Intent phoneIntent = new Intent(
									"android.intent.action.CALL", Uri
											.parse("tel:" + phoneNum));
							startActivity(phoneIntent);// 这个activity要把通话界面隐藏以及相应的把菜品界面展示；
						}
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
			if (action.equals("action.updateOrderUI")) {
				refreshData();
				if (0 == totalPrice) {
					totalView.setText("购物车空空如也!");
					tvNum.setText("0");

					BusinessOrderActivity.this.finish();
					Intent intentBack = new Intent(BusinessOrderActivity.this,
							BusinessDetailActivity.class);
					intentBack.putExtras(extraData);
					startActivity(intentBack);

					Toast toast = Toast.makeText(getApplicationContext(),
							"亲，购物篮空空如也，请先点餐!", Toast.LENGTH_SHORT);
						    //可以控制toast显示的位置
						    toast.setGravity(Gravity.CENTER, 0, 0);
						    toast.show();

					return;
				}
				totalView.setText("总价: " + String.valueOf(totalPrice) + "元");
				tvNum.setText(String.valueOf(totalNum));
			}
		}
	};

	private void refreshData() {
		totalPrice = 0;
		totalNum = 0;
		DatabaseAdapter dbHelper = new DatabaseAdapter(this, dbName);
		dbHelper.open();
		Cursor cursor = dbHelper.fetchAllFoods();
		while (cursor.moveToNext()) {
			String tQuantity = DataOperations.getActualString(cursor
					.getString(cursor.getColumnIndex("quantity")));
			String tPrice = DataOperations.getActualString(cursor
					.getString(cursor.getColumnIndex("price")));
			totalPrice += Integer.valueOf(tQuantity) * Integer.valueOf(tPrice);
			totalNum += Integer.valueOf(tQuantity);
		}
		dbHelper.close();
	}

}