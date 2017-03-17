package com.cdc.plugin;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.cdc.common.MD5;
import com.cdc.zqoa.R;


public class WebViewActivity extends Activity {

	String CookieStr=null;
	private final static String UA = "#CTC-MobileOA";
	float x1 = 0;
	float x2 = 0;
	float y1 = 0;
	float y2 = 0;
	long startTime = 0l;
	long endTime=0l;
	/**是否跳转到另外的app*/
	private boolean isOnSkipOut = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		String url = bundle.getString("url");
		String closeBtnString = bundle.getString("closeBtnString");
		setContentView(R.layout.activity_web);
		final WebView web = (WebView) findViewById(R.id.webView1);

		TextView tv = (TextView) findViewById(R.id.exitMailBtn);
		if(null!=closeBtnString){
			tv.setText(closeBtnString);
		}else{
			tv.setText("关闭");
		}

		tv.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				((TextView)v).setTextColor(getResources().getColor(R.color.webview_tool_bar_font_color));
				WebViewActivity.this.finish();
			}

		});

		web.setDownloadListener(new MyWebViewDownLoadListener());
		WebSettings webSettings = web.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDatabaseEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setRenderPriority(RenderPriority.HIGH);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不用缓存

		String ua = webSettings.getUserAgentString();
		if (ua != null){
			ua = ua + UA;
		}else{
			ua = UA;
		}


		webSettings.setUserAgentString(ua);


		web.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				Date endDate = new Date();

//				saveLog(startDate, endDate, logUrl);

//				Log.d("xxx", "11111111111111111111");
				/*Toast.makeText(WebViewActivity.this, "打开fdksf！", Toast.LENGTH_SHORT).show();*/

				CookieManager cookieManager = CookieManager.getInstance();
				CookieStr = cookieManager.getCookie(url);
//	            Log.d("xxx", "Cookies = " + CookieStr);
				super.onPageFinished(view, url);
//				WebViewActivity.this.finish();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,String description, String failingUrl){
				Toast.makeText(WebViewActivity.this, "打开失败,请关闭重试！", Toast.LENGTH_SHORT).show();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
//				view.loadUrl(url);
				return false;
			}
		});

		/*web.setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {   //当手指按下的时候
					x1 = event.getX();
					y1 = event.getY();
					startTime = new Date().getTime();
				}
				if(event.getAction() == MotionEvent.ACTION_UP) {   //当手指离开的时候
					x2 = event.getX();
					y2 = event.getY();
					endTime = new Date().getTime();
					Log.d("xxxxxxxxx1", (y2 - y1)+"");
					Log.d("xxxxxxxxx2", (x2 - x1)+"");
//					Log.d("xxxxxxxxx2", (endTime-startTime)+"");
//					Log.d("xxxxxxxxx3", (x2 - x1)/(endTime-startTime)+"");
					if((Math.abs(y2-y1)<50||(x2-x1)/Math.abs(y2-y1)>2) && x2 - x1 > 50&&endTime-startTime<300&&(x2 - x1)/(endTime-startTime)>0.6) {
//						Toast.makeText(WebViewActivity.this, "向右滑", Toast.LENGTH_SHORT).show();
						WebViewActivity.this.finish();
					}

				}
				return false;
			}

		});*/
		web.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {  //表示按返回键时的操作
						if(isOnSkipOut) {
							isOnSkipOut = false;
							Toast.makeText(WebViewActivity.this, "已回到应用，再点击一次进行后退", Toast.LENGTH_SHORT).show();
						}else{
							web.goBack();   //后退
						}


						//webview.goForward();//前进
						return true;    //已处理
					}
				}
				return false;
			}
		});
		Toast.makeText(WebViewActivity.this, "加载中", Toast.LENGTH_LONG).show();
		web.loadUrl(url);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		/*if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

	public void openFile(String fileName, String mimeType) {
		File docFile = new File(fileName);
		try {
			//跳转到别的app
			isOnSkipOut = true;
			Uri path = Uri.fromFile(docFile);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(path, mimeType);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		} catch (android.content.ActivityNotFoundException e) {
//			Log.d("PhoneGapLog", "docPlugin:Error loading url " + fileName + ":"+ e.toString());
			Toast.makeText(this, "未找到打开此类文件的应用", Toast.LENGTH_SHORT).show();
		}

	}

	private class MyWebViewDownLoadListener implements DownloadListener{

		private String savePath = null;
		private String saveName = null;
		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition, final String mimetype,
									long contentLength) {
			Log.i("tag", "url="+url);
			Log.i("tag", "userAgent="+userAgent);
			Log.i("tag", "contentDisposition="+contentDisposition);
			Log.i("tag", "mimetype="+mimetype);
			Log.i("tag", "contentLength="+contentLength);



			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// 获得存储卡的路径
				String sdpath = Environment.getExternalStorageDirectory() + "/";
				savePath = sdpath + "download/moa";
			};

			int idx = contentDisposition.lastIndexOf(".");
			if(idx>-1){
				String fileSufix = contentDisposition.substring(idx);
				String m5Str;
				try {
					m5Str = MD5.getMD5(url);
				} catch (NoSuchAlgorithmException e) {
					m5Str=new Date().getTime()+"";
				}
				saveName = m5Str+fileSufix;
			}

			File f = new File(savePath);
			if(!f.exists()){
				f.mkdirs();
			}

			final File saveFile = new File(savePath, saveName.replace("\"", ""));
			if(saveFile.exists()){
				openFile(saveFile.getPath(),mimetype);
				return;
			}

			Toast.makeText(WebViewActivity.this, "附件下载中", Toast.LENGTH_LONG).show();
			String serviceString = Context.DOWNLOAD_SERVICE;
			DownloadManager downloadManager;
			downloadManager = (DownloadManager)getSystemService(serviceString);

			Uri uri = Uri.parse(url);
			DownloadManager.Request request = new Request(uri);
			request.addRequestHeader("Cookie", CookieStr);
			request.setDestinationUri(Uri.fromFile(saveFile));

			request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			final long myDownloadReference = downloadManager.enqueue(request);

			IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

			BroadcastReceiver receiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
					if (myDownloadReference == reference) {
						Log.d("xxxxxxxx",myDownloadReference+"");
						openFile(saveFile.getPath(),mimetype);
					}
				}
			};
			registerReceiver(receiver, filter);
            
            /*ContentValues values = new ContentValues();
            values.put(Downloads.URI, url);//指定下载地址
           values.put(ContentValues.COOKIE_DATA, cookie);*/
        	
            /*Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent); */
		}
	}

}
