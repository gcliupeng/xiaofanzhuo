package com.xiaolong.xiaofanzhuo.dataoperations;
import java.net.URLEncoder;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class AsyncHttpUsage {
	
	 @SuppressWarnings("deprecation")
	public static void makeRequest(String webServer, String relativeCode) {
	        AsyncHttpClient client = new AsyncHttpClient();

	        client.get(webServer + URLEncoder.encode(relativeCode), new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable error, String content) {
					// TODO Auto-generated method stub
					super.onFailure(error, content);
					System.out.println("Error reponse from server: " + content);
				}

				@Override
				public void onSuccess(int statusCode, String content) {
					// TODO Auto-generated method stub
					super.onSuccess(statusCode, content);
					System.out.println("StatusCode from server: " + statusCode);
					System.out.println("Reponse from server: " + content);
				}            

	        });
	    }

}


