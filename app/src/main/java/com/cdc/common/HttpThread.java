package com.cdc.common;

import java.util.Map;

import android.os.Handler;
import android.os.Message;

public class HttpThread extends Thread{
	
	private Handler mHandler;
	private String url;
	private Map<String, String> map ;
	
	public HttpThread(String url,Map<String, String> map,Handler mHandler){
		this.url = url;
		this.map = map;
		this.mHandler = mHandler;
	}

	@Override
	public void run() {
		MyLog.i("异步线程启动中");
		Message msg = new Message();
		
		try {
			MyLog.i("请求服务器接口：" + url);
			String result = HttpUtil.postResult(url,map);
			if(result != null){
				msg.arg1 = Constants.RESULT_SUCCESS;
				String s = result.replace("\\n", "").replace("\\t", "");
				msg.obj = s;
				MyLog.i("服务器接口请求成功，返回数据：" + s);
			}else{
				msg.arg1 = Constants.RESULT_NO_NETWORK;
				MyLog.i("服务器接口请求失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mHandler.sendMessage(msg);
	}

}
