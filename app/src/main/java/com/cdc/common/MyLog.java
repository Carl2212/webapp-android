package com.cdc.common;

import android.util.Log;



public class MyLog {
	
	public static void d(String msg) {
		if(Constants.SHOW_LOG) {
			Log.d(Constants.LOG_TAG, msg);
		}
	}
	
	public static void w(String msg) {
		if(Constants.SHOW_LOG) {
			Log.w(Constants.LOG_TAG, msg);
		}
	}
	
	public static void i(String msg) {
		if(Constants.SHOW_LOG) {
			Log.i(Constants.LOG_TAG, msg);
		}
	}

	
	public static void e(String msg) {
		if(Constants.SHOW_LOG) {
			Log.e(Constants.LOG_TAG, msg);
		}
	}
}
