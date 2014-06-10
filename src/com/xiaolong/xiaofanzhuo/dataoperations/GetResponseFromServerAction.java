package com.xiaolong.xiaofanzhuo.dataoperations;

import java.util.ArrayList;
import java.util.List;

/*
 * GetResponseFromServerAction
 * by hongxiaolong
 */

public class GetResponseFromServerAction {

	public static String webServer = "http://182.92.80.201/2.php?content=";

	@SuppressWarnings("unused")
	private String mResult = null;

	/**
	 * @param id
	 *            请求码
	 * @return String
	 */
	public String getStringFromServerById(String requestCode)
			throws InterruptedException, Throwable {
		return requestFromServer(requestCode);
	}

	/**
	 * @param id
	 *            请求码
	 * @return List-Imageurl
	 */
	public List<String> getStringListFromServerById(String requestCode)
			throws InterruptedException, Throwable {
		List<String> list = new ArrayList<String>();
		String reponse = requestFromServer(requestCode);
		String[] ret = splitFromStringBySymbol(reponse, "\n");
		for (int i = 0; i < ret.length; ++i)
			list.add(ret[i]);
		return list;
	}

	private String requestFromServer(String requestCode) {
		AsyncHttpUsage.makeRequest(webServer, requestCode);
		return null;
	}

	public static String[] splitFromStringBySymbol(String orginString,
			String symbol) {
		return orginString.split(symbol);
	}

}
