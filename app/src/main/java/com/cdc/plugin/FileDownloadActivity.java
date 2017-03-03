package com.cdc.plugin;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cdc.plugin.download.LogUtils;
import com.cdc.plugin.download.OpenFileUtil;
import com.cdc.plugin.download.ProgressListener;
import com.cdc.plugin.download.Updater;
import com.cdc.zqoa.R;

public class FileDownloadActivity extends Activity {

	private String mFileUrl;

	private String mFilePath;
	
	private String mFileName;

	private Updater updater;

	private ProgressBar progressBar;
	private TextView tv_download_status;
	private TextView tv_percent;
	private Button btn_cancel;

	private boolean isDownloadComleted = false;

	public static void startActivity(Context from, String fileUrl, String fileName) {
		Intent intent = new Intent(from, FileDownloadActivity.class);
		intent.putExtra("fileUrl", fileUrl);
		intent.putExtra("fileName", fileName);
		from.startActivity(intent);
	}

	private void getDataFromIntent() {
		Intent intent = getIntent();
		mFileUrl = intent.getStringExtra("fileUrl");
		mFileName  = intent.getStringExtra("fileName");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_download);

		this.setFinishOnTouchOutside(false);
		
		getDataFromIntent();

		initView();

		startDownload();

	}

	private String getFileName(String fileUrl){
		if(TextUtils.isEmpty(fileUrl)) return null;
		if( fileUrl.contains("/") ){
			String[] fileUrlParts = fileUrl.split("/");
			return fileUrlParts[fileUrlParts.length-1];
		}else {
			return ""+System.currentTimeMillis();
		}
		
	}
	
	private void startDownload() {
		
//		String fileName = getFileName(mFileUrl);
		mFilePath = Environment.getExternalStorageDirectory() + File.separator
				+ "moa" + File.separator + mFileName;
		
		File f = new File(Environment.getExternalStorageDirectory() + File.separator + "moa");
    	if(!f.exists()){
    		f.mkdirs();
    	}

		File file = new File(mFilePath);
		Log.d("download_test", "file path ==> " + mFilePath);
		if (file.exists()) {
			Log.d("download_test", "file exist, delete..");
			file.delete();
		}

		updater = new Updater.Builder(getApplicationContext())
				.setDownloadUrl(mFileUrl)
				.setFilePath(mFilePath)
				.setFileName(mFileName)
				.allowedOverRoaming()
				.setNotificationTitle(mFileName)
				.start();

		updater.registerDownloadReceiver();

		updater.addProgressListener(new ProgressListener() {
			private boolean downFinished = false;
			@Override
			public void onProgressChange(long totalBytes, long curBytes,
					int progress) {
				progressBar.setProgress(progress);
				tv_percent.setText(progress + "%");
				
				String status;
				int total;
				int cur;
				if( totalBytes > 1024 * 1024 ){
					total = (int) (totalBytes/1024/1024);
					cur = (int) (curBytes/1024/1024);
					status = cur + "/" + total + "MB";
				}else if( totalBytes > 1024 ){
					total = (int) (totalBytes/1024);
					cur = (int) (curBytes/1024);
					status = cur + "/" + total + "KB";
				}else{
					total = (int) (totalBytes);
					cur = (int) (curBytes);
					status = cur + "/" + total + "B";
				}
				tv_download_status.setText( status );
				
				if (!downFinished && progress >= 100) {
					downFinished=true;
					isDownloadComleted = true;
					btn_cancel.setText("已下载完成，打开");
					
					Intent openFileIntent = OpenFileUtil
							.getOpenFileIntent(mFilePath);
					FileDownloadActivity.this.startActivity(openFileIntent);
					FileDownloadActivity.this.finish();
				} else {
					isDownloadComleted = false;
					btn_cancel.setText("取消下载");
				}
			}
		});
	}

	private void cancelDownload() {
		if (updater == null)
			return;
		updater.cancelDownload();
	}

	private void initView() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		tv_percent = (TextView) findViewById(R.id.tv_percent);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		tv_download_status = (TextView) findViewById(R.id.tv_download_status);

		// 取消下载按钮
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isDownloadComleted) {
					cancelDownload();
				} else {
					Intent openFileIntent = OpenFileUtil
							.getOpenFileIntent(mFilePath);
					FileDownloadActivity.this.startActivity(openFileIntent);
				}
				FileDownloadActivity.this.finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		updater.unRegisterDownloadReceiver();
		LogUtils.debug("onDestroy");
	}

	@Override
	public void onBackPressed() {
	}
}
