package com.cdc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import android.util.Log;
/**
 * 
 * @author lft
 *
 */
public class HttpUtil {
	/**
	 * 通过http get方式读取服务端数据
	 * @param url
	 * @return
	 */
	public static String executeGet(String url) {
		Log.d(HttpUtil.class.getName()+",请求地址：", url);
		String result = null;
		BufferedReader reader = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer strBuffer = new StringBuffer("");
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuffer.append(line);
			}
			result = strBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	/**
	 * 通过http get方式读取服务端数据
	 * @param url
	 * @return
	 * @throws ConnectTimeoutException 
	 */
	public static String executePost(String url, List<NameValuePair> params) throws ConnectTimeoutException {
		Log.d(HttpUtil.class.getName()+",请求地址：", url);
		String result = null;
		BufferedReader reader = null;
		try {
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            // 读取超时
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 0000);

			HttpPost request = new HttpPost(url);
			
			
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(request);
			
			if(response.getStatusLine().getStatusCode() == 200){
				reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer strBuffer = new StringBuffer("");
				String line = null;
				while ((line = reader.readLine()) != null) {
					strBuffer.append(line);
				}
				result = strBuffer.toString();
				
			}
			
		}catch(ConnectTimeoutException ce){
			throw ce;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Log.i(HttpUtil.class.getName(), result);
		return result;
	}

}
