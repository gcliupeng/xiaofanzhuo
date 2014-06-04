package com.xiaolong.xiaofanzhuo.businesslistings;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.FileUtil;
import com.xiaolong.xiaofanzhuo.fileio.IViewAddAndEventSet;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

public class ResPictureViewAdd implements IViewAddAndEventSet {

	@SuppressWarnings("unused")
	private BasePictAdapter adapter;

	public ResPictureViewAdd(BasePictAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public View addViewAndAddEvenet(Context context, View convertView,
			int position, List<Bundle> list, boolean isMultiSelectMode) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.liu_vlist, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.img);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.info = (TextView) convertView.findViewById(R.id.info);
			holder.praisnum = (TextView) convertView
					.findViewById(R.id.praisnum);
			holder.eatnum = (TextView) convertView.findViewById(R.id.eatnum);

			DataOperations.setTypefaceForTextView(context, holder.name);
			DataOperations.setTypefaceForTextView(context, holder.info);
			DataOperations.setTypefaceForTextView(context, holder.praisnum);
			DataOperations.setTypefaceForTextView(context, holder.eatnum);

			holder.isfree = (ImageView) convertView.findViewById(R.id.isfree);
			holder.iswai = (ImageView) convertView.findViewById(R.id.iswai);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Bundle data = list.get(position);
		final String id = data.getString("id");

		AsyncHttpClient Down = new AsyncHttpClient();

		if (data.containsKey("shopname"))
			holder.name.setText(data.getString("shopname"));
		else {
			final TextView nameView = holder.name;
			Down.get(DataOperations.webServer + id + "_ShopName",
					new AsyncHttpResponseHandler() {
						@SuppressWarnings("deprecation")
						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							String name = DataOperations
									.getActualString(content);
							data.putString("shopname", name);
							nameView.setText(name);
						}
					});
		}
		if (data.containsKey("shoptag"))
			holder.info.setText(data.getString("name"));
		else {
			final TextView infoView = holder.info;
			Down.get(DataOperations.webServer + id + "_ShopTag",
					new AsyncHttpResponseHandler() {
						@SuppressWarnings("deprecation")
						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							String tag = DataOperations
									.getActualString(content);
							data.putString("shoptag", tag);
							infoView.setText(tag);
						}
					});
		}
		if (data.containsKey("praisenum"))
			holder.praisnum.setText(data.getString("praisenum"));
		else {
			final TextView praisnumView = holder.praisnum;
			Down.get(DataOperations.webServer + id + "_PraiseNum",
					new AsyncHttpResponseHandler() {
						@SuppressWarnings("deprecation")
						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							String praise = DataOperations
									.getActualString(content);
							data.putString("praisenum", praise);
							praisnumView.setText(praise);
						}
					});
		}
		if (data.containsKey("eatnum"))
			holder.eatnum.setText(data.getString("eatnum"));
		else {
			final TextView eatNumView = holder.eatnum;
			Down.get(DataOperations.webServer + id + "_NumofPeopleWant2Eat",
					new AsyncHttpResponseHandler() {
						@SuppressWarnings("deprecation")
						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							String eatnum = DataOperations
									.getActualString(content);
							data.putString("eatnum", eatnum);
							eatNumView.setText(eatnum);
						}
					});
		}
		if (data.containsKey("sendfoodout")) {
			if (data.getString("sendfoodout").contains("不"))
				holder.iswai.setImageResource(R.drawable.waimai_s);
			else
				holder.iswai.setImageBitmap(null);
		} else {
			final ImageView iswaiView = holder.iswai;
			Down.get(DataOperations.webServer + id + "_SendFoodOut",
					new AsyncHttpResponseHandler() {
						@SuppressWarnings("deprecation")
						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							String iswai = DataOperations
									.getActualString(content);
							data.putString("sendfoodout", iswai);
							if (iswai.contains("不"))
								iswaiView.setImageResource(R.drawable.waimai_s);
							else
								iswaiView.setImageBitmap(null);
						}
					});
		}
		if (data.containsKey("busystate")) {
			if (data.getString("busystate").contains("空"))
				holder.isfree.setImageResource(R.drawable.idle1_s);
			else
				holder.isfree.setImageResource(R.drawable.busy);
		} else {
			final ImageView isfreeView = holder.isfree;
			Down.get(DataOperations.webServer + id + "_BusyState",
					new AsyncHttpResponseHandler() {
						@SuppressWarnings("deprecation")
						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							String isfree = DataOperations
									.getActualString(content);
							data.putString("busystate", isfree);
							if (isfree.contains("空"))
								isfreeView.setImageResource(R.drawable.idle1_s);
							else
								isfreeView.setImageResource(R.drawable.busy);
						}
					});
		}
		FileUtil.setImageSrc(holder.imageView,
				DataOperations.getActualString(data.getString("url")));

		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView name;
		TextView info;
		ImageView isfree;
		ImageView iswai;
		TextView praisnum;
		TextView eatnum;
	}

}
