package com.cdc.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author Administrator
 *
 */
public class HttpUtil {

	/**
	 * http get请求远程服务器
	 * @param url
	 * @return
	 * @throws Exception 
	 */
	public static String getResult(final String url) throws Exception{
		FutureTask<String> task = new FutureTask<String>(new Callable<String>() {

			@Override
			public String call() throws Exception {
				HttpParams params = new BasicHttpParams();
				/* 超时设置 */
	            /* 从连接池中取连接的超时时间 */
	            ConnManagerParams.setTimeout(params, 1000);
	            /* 连接超时 */
	            HttpConnectionParams.setConnectionTimeout(params, Constants.CONN_TIMEOUT);
	            /* 请求超时 */
	            HttpConnectionParams.setSoTimeout(params, Constants.TIMEOUT);
	            
	            HttpClient httpClient = new DefaultHttpClient(params);
				HttpGet get = new HttpGet(url);
				HttpResponse response = httpClient.execute(get);
				if(response.getStatusLine().getStatusCode() == 200){
					String result = EntityUtils.toString(response.getEntity());
					return result;
				}
				return null;
			}
		});
		new Thread(task).start();
		return task.get();
	}
	
	/**
	 * http post请求远程服务器
	 * @param url
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public static String postResult(final String url,final Map<String, String> map) throws Exception{
		FutureTask<String> task = new FutureTask<String>(new Callable<String>() {

			@Override
			public String call() throws Exception {
				HttpParams params = new BasicHttpParams();
				/* 超时设置 */
	            /* 从连接池中取连接的超时时间 */
	            ConnManagerParams.setTimeout(params, 1000);
	            /* 连接超时 */
	            HttpConnectionParams.setConnectionTimeout(params, Constants.CONN_TIMEOUT);
	            /* 请求超时 */
	            HttpConnectionParams.setSoTimeout(params, Constants.TIMEOUT);
	            
	            HttpClient httpClient = new DefaultHttpClient(params);
	            String paramer = "";
				HttpPost post = new HttpPost(url);
				if(map != null){
					List<NameValuePair> paramList = new ArrayList<NameValuePair>();
					for(String key : map.keySet()){
						paramList.add(new BasicNameValuePair(key, map.get(key)));
						paramer += key + ":" + map.get(key) + ";";
					}
					MyLog.i("请求参数：" + paramer);
					post.setEntity(new UrlEncodedFormEntity(paramList,"UTF-8"));
				}
				
				HttpResponse response = httpClient.execute(post);
				if(response.getStatusLine().getStatusCode() == 200){
					String result = EntityUtils.toString(response.getEntity());
					return result;
				}
				return null;
			}
		});
		new Thread(task).start();
		return task.get();
	}
}
