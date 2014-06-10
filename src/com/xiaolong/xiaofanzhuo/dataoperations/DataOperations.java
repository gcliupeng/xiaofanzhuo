package com.xiaolong.xiaofanzhuo.dataoperations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class DataOperations {

	public static String webServer = "http://182.92.80.201/2.php?content=";

	public static String getActualString(String str) {

		if (!str.isEmpty() && str.indexOf("\'") == 0)
			str = str.substring(1, str.length()); // 去掉第一个'
		if (!str.isEmpty() && str.lastIndexOf("\n") == str.length() - 1)
			str = str.substring(0, str.length() - 1); // 去掉最后一个\n
		if (!str.isEmpty() && str.lastIndexOf("\'") == str.length() - 1)
			str = str.substring(0, str.length() - 1); // 去掉最后一个'

		return str;

	}

	/**
	 * 将异常信息转化成字符串
	 * 
	 * @param t
	 * @return
	 * @throws IOException
	 */
	public static String exception(Throwable t) throws IOException {
		if (t == null)
			return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			t.printStackTrace(new PrintStream(baos));
		} finally {
			baos.close();
		}
		return baos.toString();
	}

	/**
	 * 检测异常数据
	 * 
	 * @param input
	 * @return true: invalid data false: valid data
	 */
	public static boolean isInvalidDataFromServer(String input) {
		if (null == input || input.contains("异常")) {
			System.out.print("Invalid data from server: " + input);
			return true;
		}
		return false;
	}

	/**
	 * 设置字体格式
	 * @param context
	 * @param textView
	 */
	public static void setTypefaceForTextView(Context context, TextView textView) {
		Typeface typeFace = Typeface.createFromAsset(context.getAssets(),
				"fonts/huakangwawa.ttf");
		textView.setTypeface(typeFace);
	}
}
