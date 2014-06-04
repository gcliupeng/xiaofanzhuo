package com.xiaolong.xiaofanzhuo.businessdetails;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.dataoperations.DatabaseAdapter;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.FileUtil;
import com.xiaolong.xiaofanzhuo.fileio.IViewAddAndEventSet;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/**
 * MenuPictureViewAdd
 * 
 * @author hongxiaolong
 * 
 */

public class MenuPictureViewAdd implements IViewAddAndEventSet {

	@SuppressWarnings("unused")
	private BasePictAdapter adapter;
	private String database = null;

	public MenuPictureViewAdd(BasePictAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public View addViewAndAddEvenet(final Context context, View convertView,
			int position, List<Bundle> list, boolean isMultiSelectMode) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.hong_menu_infos_list,
					null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.news_pic);
			holder.contentView = (TextView) convertView
					.findViewById(R.id.news_title);
			holder.timeView = (TextView) convertView
					.findViewById(R.id.news_time);
			DataOperations.setTypefaceForTextView(context, holder.contentView);
			DataOperations.setTypefaceForTextView(context, holder.timeView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Bundle data = list.get(position);
		final String id = data.getString("id");
		database = data.getString("database");

		AsyncHttpClient Down = new AsyncHttpClient();

		if (data.containsKey("height"))
			holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, (int) data
							.getInt("height")));
		else {
			final ImageView imageView = holder.imageView;
			Down.get(DataOperations.webServer + id + "_Height",
					new AsyncHttpResponseHandler() {
						@SuppressWarnings("deprecation")
						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							if (DataOperations.isInvalidDataFromServer(content))
								return;
							int height = (int) Integer.valueOf(DataOperations
									.getActualString(content));
							data.putInt("height", height);
							imageView
									.setLayoutParams(new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.MATCH_PARENT,
											(int) height));
						}
					});
		}
		FileUtil.setImageSrc(holder.imageView,
				DataOperations.getActualString(data.getString("url")));

		if (data.containsKey("food"))
			holder.contentView.setText(data.getString("food"));
		else {
			final TextView foodView = holder.contentView;
			Down.get(DataOperations.webServer + id + "_Food",
					new AsyncHttpResponseHandler() {
						@SuppressWarnings("deprecation")
						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							String food = DataOperations
									.getActualString(content);
							data.putString("food", food);
							foodView.setText(food);
						}
					});
		}

		if (data.containsKey("foodprice"))
			holder.timeView.setText(data.getString("foodprice"));
		else {
			final TextView timeView = holder.timeView;
			Down.get(DataOperations.webServer + id + "_FoodPrice",
					new AsyncHttpResponseHandler() {
						@SuppressWarnings("deprecation")
						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							String foodprice = DataOperations
									.getActualString(content);
							data.putString("foodprice", foodprice);
							timeView.setText("单价: " + foodprice + "元");
						}
					});
		}

		holder.imageView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String price = "";
				if (data.containsKey("foodprice"))
					price = data.getString("foodprice");
				else
					Toast.makeText(context.getApplicationContext(),
							"亲，请等待加载完成!!", Toast.LENGTH_SHORT).show();
				System.out.println("已记录图片点击!!!");
				DatabaseAdapter dbHelper = new DatabaseAdapter(context,
						database);
				dbHelper.open();
				dbHelper.updateFoodTable(id,price, true);
				dbHelper.close();

				Intent intent = new Intent();
				intent.setAction("action.updateDetailUI");
				context.sendBroadcast(intent);
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView contentView;
		TextView timeView;
	}
}
