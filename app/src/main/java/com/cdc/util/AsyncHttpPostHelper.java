package com.cdc.util;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncHttpPostHelper extends AsyncTask<String,Void,String> {
//	private String url;
	private AsyncHttpAssistant assistant;
	private List<NameValuePair> httpParam;
	
	public AsyncHttpPostHelper(List<NameValuePair> httpParam, AsyncHttpAssistant assistant){
//		this.url = url;
		this.assistant = assistant;
		this.httpParam = httpParam;
	}

	@Override
	protected String doInBackground(String... url) {
		Log.i(this.getClass().getName(), "------->in asyncHttp2");
		String result=null;
		try {
			result = HttpUtil.executePost(url[0], this.httpParam);
		} catch (ConnectTimeoutException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		Log.i(this.getClass().getName(), "------->in asyncHttp3");
		return result;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	@Override
	protected void onPostExecute(String result) {
		Log.i(this.getClass().getName(), "------->in asyncHttp1");
		assistant.assist(result);
		super.onPostExecute(result);
	}



}
