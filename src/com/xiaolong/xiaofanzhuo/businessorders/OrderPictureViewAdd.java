package com.xiaolong.xiaofanzhuo.businessorders;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

/*
 * OrderPictureViewAdd
 * @author hongxiaolong
 */

public class OrderPictureViewAdd implements IViewAddAndEventSet {

	private BasePictAdapter adapter;
	private String database = null;

	public OrderPictureViewAdd(BasePictAdapter adapter) {
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
			convertView = layoutInflater.inflate(R.layout.hong_menu_order_item,
					null);

			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.order_image_view);
			holder.textTitle = (TextView) convertView
					.findViewById(R.id.menu_name);
			holder.textPrice = (TextView) convertView
					.findViewById(R.id.menu_price);
			holder.buttonAdd = (Button) convertView
					.findViewById(R.id.button_add);
			holder.buttonDel = (Button) convertView
					.findViewById(R.id.button_delete);
			holder.textAmount = (TextView) convertView
					.findViewById(R.id.menu_amount);
			
			DataOperations.setTypefaceForTextView(context, holder.textTitle);
			DataOperations.setTypefaceForTextView(context, holder.textPrice);
			DataOperations.setTypefaceForTextView(context, holder.textAmount);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Bundle data = list.get(position);
		final String id = data.getString("id");
		final String quantity = data.getString("quantity");
		final String url = data.getString("url");
		database = data.getString("database");

		AsyncHttpClient Down = new AsyncHttpClient();
		
		FileUtil.setImageSrc(holder.imageView,
				DataOperations.getActualString(url));

		if (data.containsKey("food"))
			holder.textTitle.setText(data.getString("food"));
		else {
			final TextView foodName = holder.textTitle;
			Down.get(DataOperations.webServer + id + "_Food",
					new AsyncHttpResponseHandler() {
						@SuppressWarnings("deprecation")
						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							String food = DataOperations
									.getActualString(content);
							data.putString("food", content);
							foodName.setText(food);
						}
					});
		}
		if (data.containsKey("foodprice"))
			holder.textPrice.setText(data.getString("foodprice"));
		else {
			final TextView timeView = holder.textPrice;
			Down.get(DataOperations.webServer + id + "_FoodPrice",
					new AsyncHttpResponseHandler() {
						@SuppressWarnings("deprecation")
						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							String foodprice = DataOperations
									.getActualString(content);
							data.putString("foodprice", content);
							timeView.setText("单价: " + foodprice + "元");
						}
					});
		}

		final TextView quantityView = holder.textAmount;
		quantityView.setText(quantity);
		holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String price = "";
				if (data.containsKey("foodprice"))
					price = data.getString("foodprice");
				else 
					Toast.makeText(context.getApplicationContext(),
							"亲，请等待加载完成!!", Toast.LENGTH_SHORT)
							.show();
				System.out.println("点击 + ");
				DatabaseAdapter dbHelper = new DatabaseAdapter(context, database);
				dbHelper.open();
				dbHelper.updateFoodTable(id, price, true);
				String count = dbHelper.fetchFoodQuantity(id);
				quantityView.setText(count);
				dbHelper.close();

				Intent intent = new Intent();
				intent.setAction("action.updateOrderUI");
				context.sendBroadcast(intent);
			}
		});
		holder.buttonDel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String price = "";
				if (data.containsKey("foodprice"))
					price = data.getString("foodprice");
				else 
					Toast.makeText(context.getApplicationContext(),
							"亲，请等待加载完成!!", Toast.LENGTH_SHORT)
							.show();
				System.out.println("点击 - ");
				DatabaseAdapter dbHelper = new DatabaseAdapter(context, database);
				dbHelper.open();
				int count = Integer.valueOf(dbHelper.fetchFoodQuantity(id));
				dbHelper.updateFoodTable(id, price, false);
				if (0 == --count)
					adapter.removePicture(data);
				quantityView.setText(String.valueOf(count));
				dbHelper.close();

				Intent intent = new Intent();
				intent.setAction("action.updateOrderUI");
				context.sendBroadcast(intent);
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView textTitle;
		TextView textPrice;
		Button buttonAdd;
		Button buttonDel;
		TextView textAmount;
	}
}
