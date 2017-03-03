package com.cdc.util;

import android.os.AsyncTask;

public class AsyncHttpGetHelper extends AsyncTask<String,Void,String> {
//	private String url;
	private AsyncHttpAssistant assistant;
	
	public AsyncHttpGetHelper(AsyncHttpAssistant assistant){
//		this.url = url;
		this.assistant = assistant;
	}

	@Override
	protected String doInBackground(String... url) {
		String result = HttpUtil.executeGet(url[0]);
		return result;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	@Override
	protected void onPostExecute(String result) {
		
		assistant.assist(result);
		super.onPostExecute(result);
	}



}
