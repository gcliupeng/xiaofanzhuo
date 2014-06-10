package com.xiaolong.xiaofanzhuo.fileio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.dataoperations.DatabaseAdapter;

public class DownloadPicts extends Thread {
	private Handler mHandler = null;
	private String mRequesetCode = null;
	private volatile boolean isRun = true;
	private List<String> mPictList = null;
	private List<String> mQuantityList = null;
	private List<String> mPriceList = null;
	private boolean isDatabase = false;
	private String mDatabase = null;
	private Context mContext;

	// Read from webServer
	public DownloadPicts(Context context, Handler handler,
			String mRequesetCode, String database) {
		super();
		this.mHandler = handler;
		this.mRequesetCode = mRequesetCode;
		setName(this.getClass().getSimpleName());
		mDatabase = database;
		mContext = context;
	}

	// Read from SQLite
	public DownloadPicts(Context context, Handler handler, String database,
			List<String> ids, List<String> quantities, List<String> prices) {
		super();
		this.mHandler = handler;
		mPictList = new ArrayList<String>();
		mQuantityList = new ArrayList<String>();
		mPriceList = new ArrayList<String>();
		for (int i = 0; i < ids.size(); ++i) {
			mPictList.add(ids.get(i));
			mQuantityList.add(quantities.get(i));
			mPriceList.add(prices.get(i));
		}
		isDatabase = true;
		mDatabase = database;
		mContext = context;
		setName(this.getClass().getSimpleName());
	}

	@SuppressWarnings("deprecation")
	@Override
	/*
	 * 该函数实现从Url（或者缓存）下载图片包含缩略图到pict中
	 */
	public void run() {

		final AsyncHttpClient Down = new AsyncHttpClient();
		final String DownloadPictsList = DataOperations.webServer
				+ this.mRequesetCode;
		Down.get(DownloadPictsList, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(Throwable error, String content) {
				// TODO Auto-generated method stub
				super.onFailure(error, content);
				System.out.println("Request Code: " + DownloadPictsList);
				System.out
						.println("DownloadPictsList! Error reponse from server: "
								+ content);
				System.out.println(error.getStackTrace()[0].getMethodName());
				try {
					System.out
							.println("DownloadPictsList! Error reponse from server: "
									+ DataOperations.exception(error));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, content);
				System.out.println("Request Code: " + DownloadPictsList);
				System.out.println("StatusCode from server: " + statusCode);
				System.out.println("Reponse from server: " + content);

				if (DataOperations.isInvalidDataFromServer(content))
					return;

				Message msgClear = mHandler.obtainMessage();
				msgClear.what = 2;
				mHandler.sendMessage(msgClear);

				List<String> ids = isDatabase ? mPictList
						: new ArrayList<String>();

				if (false == isDatabase) {
					String[] ret = content.split("\n");
					for (int i = 0; i < ret.length; ++i)
						ids.add(ret[i]);
				}

				for (int i = 0; i < ids.size(); ++i) {

					final String id = ids.get(i);

					if (DataOperations.isInvalidDataFromServer(id))
						return;

					final int position = i;
					final String DownloadPicts = DataOperations.webServer
							+ ids.get(i) + "_ImgUrl";
					Down.get(DownloadPicts, new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(Throwable error, String content) {
							// TODO Auto-generated method stub
							super.onFailure(error, content);
							System.out
									.println("Request Code: " + DownloadPicts);
							System.out
									.println("DownloadPicts! Error reponse from server: "
											+ content);
							System.out.println(error.getStackTrace()[0]
									.getMethodName());
							try {
								System.out
										.println("DownloadPicts! Error reponse from server: "
												+ DataOperations
														.exception(error));
							} catch (IOException e) {
								// TODO Auto-generated catch
								// block
								e.printStackTrace();
							}
						}

						@Override
						public void onSuccess(int statusCode, String content) {

							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							System.out
									.println("Request Code: " + DownloadPicts);
							System.out
									.println("StatusCode of pictsDown from server: "
											+ statusCode);
							String imageUrl = DataOperations
									.getActualString(content);
							System.out
									.println("Reponse of pictsDown from server: "
											+ imageUrl);

							if (null == imageUrl) {
								System.out.println("Deleted Url from server: "
										+ imageUrl);
								if (null != mDatabase) {
									DatabaseAdapter dbHelper = new DatabaseAdapter(
											mContext, mDatabase);
									dbHelper.open();
									dbHelper.deleteFood(id);
									dbHelper.close();

									Intent intent = new Intent();
									intent.setAction("action.updateOrderUI");
									mContext.sendBroadcast(intent);
									return;
								}
								System.out.println("Error Url from server: "
										+ imageUrl);
								return;
							}

							@SuppressWarnings("unused")
							Bitmap map = FileUtil
									.getBitMapIfNecessary(imageUrl);// 由图片url获取bitmap
							Message msg = mHandler.obtainMessage();
							Bundle data = new Bundle();
							data.putString("id", id);// 把唯一标识符传过去
							data.putString("url", imageUrl);
							data.putString("database", mDatabase);
							if (true == isDatabase) {
								data.putString("quantity",
										mQuantityList.get(position));
								data.putString("price",
										mPriceList.get(position));
							}
							msg.setData(data);
							msg.what = 1;
							mHandler.sendMessage(msg);
						}

					});
				}
			}
		});
	}

	public void setStop() {
		this.isRun = false;
	}

	public boolean isRun() {
		return isRun;
	}

}
