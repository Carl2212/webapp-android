package com.cdc.plugin.download;

import java.io.File;
import java.util.ArrayList;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Updater
 */

public class Updater {

	private String apkName;
	private String apkPath;
	private String apkDirName;
	private String title;
	private String downloadUrl;
	private Context context;
	private DownloadManager downloadManager;
	private long mTaskId;
	private boolean hideNotification = false;
	// private ProgressListener mProgressListener;
	private boolean allowedOverRoaming = false;
	private DownloadReceiver downloadReceiver;
	private DownloadObserver downloadObserver;
	private boolean claerCache = false;

	private Updater(Context context) {
		this.context = context;
	}

	private void download() {
		if (context == null) {
			throw new NullPointerException("context must not be null");
		}
		if (TextUtils.isEmpty(downloadUrl)) {
			throw new NullPointerException("downloadUrl must not be null");
		}
		downloadManager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);

		// 获取一个下载请求
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(downloadUrl));

		// 设置wifi，流量都可以下载
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
				| DownloadManager.Request.NETWORK_WIFI);

		// 漫游网络是否可以下载
		request.setAllowedOverRoaming(allowedOverRoaming);

		// 在通知栏中显示标题，默认就是显示的
		request.setTitle(TextUtils.isEmpty(title) ? apkName : title);

		// 设置隐藏通知栏下载
		request.setNotificationVisibility(hideNotification ? DownloadManager.Request.VISIBILITY_HIDDEN
				: DownloadManager.Request.VISIBILITY_VISIBLE);

//		// 设置下载路径
//		if (TextUtils.isEmpty(apkPath) && TextUtils.isEmpty(apkDirName)) {
//			deleteFileIfExist(new File(Environment.DIRECTORY_DOWNLOADS, apkName));
//			request.setDestinationInExternalPublicDir(
//					Environment.DIRECTORY_DOWNLOADS, apkName);
//		} else if (!TextUtils.isEmpty(apkDirName)) {
//			deleteFileIfExist(new File(
//					Environment.getExternalStorageDirectory() + File.separator
//							+ apkDirName + File.separator + apkName));
//			request.setDestinationInExternalPublicDir(apkDirName, apkName);
//		} else {
//			String apkAbsPath = apkPath + File.separator + apkName;
//			deleteFileIfExist(new File(apkAbsPath));
//			request.setDestinationUri(Uri.fromFile(new File(apkAbsPath)));
//		}
		
		String apkAbsPath =apkPath;
		request.setDestinationUri(Uri.fromFile(new File(apkAbsPath)));

		// 将下载请求加入下载队列
		// 加入下载队列后会给该任务返回一个long型的id，
		// 通过该id可以取消任务，重启任务等等
		mTaskId = downloadManager.enqueue(request);

	}

	public void cancelDownload(){
		removeDownload(mTaskId);
	}
	
	private void removeDownload(long taskId){
		if( null == downloadManager ) return;
		if( 0 == taskId ) return;
		downloadManager.remove(taskId);
		Toast.makeText(context, "已取消下载", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 注册下载完成的监听
	 */
	public void registerDownloadReceiver() {
		if (downloadReceiver == null) {
			downloadReceiver = new DownloadReceiver();
		}
		context.registerReceiver(downloadReceiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	/**
	 * 解绑下载完成的监听
	 */
	public void unRegisterDownloadReceiver() {
		if (downloadReceiver != null) {
			context.unregisterReceiver(downloadReceiver);
		}
	}

	private ArrayList<ProgressListener> listeners;

	/**
	 * 添加下载进度回调
	 */
	public void addProgressListener(ProgressListener progressListener) {
		if (listeners == null) {
			listeners = new ArrayList<ProgressListener>();
		}
		if (!listeners.contains(progressListener)) {
			listeners.add(progressListener);
		}
		if (downloadObserver == null) {
			downloadObserver = new DownloadObserver(handler, downloadManager,
					mTaskId);
			context.getContentResolver().registerContentObserver(
					Uri.parse("content://downloads/"), true, downloadObserver);
		}
	}

	/**
	 * 移除下载进度回调
	 */
	public void removeProgressListener(ProgressListener progressListener) {
		if (!listeners.contains(progressListener)) {
			throw new NullPointerException(
					"this progressListener not attch Updater");
		}
		if (listeners != null && !listeners.isEmpty()) {
			listeners.remove(progressListener);
			if (listeners.isEmpty() && downloadObserver != null)
				context.getContentResolver().unregisterContentObserver(
						downloadObserver);
		}
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			Bundle data = msg.getData();
			long cutBytes = data.getLong(DownloadObserver.CURBYTES);
			long totalBytes = data.getLong(DownloadObserver.TOTALBYTES);
			int progress = data.getInt(DownloadObserver.PROGRESS);
			if (listeners != null && !listeners.isEmpty()) {
				for (ProgressListener listener : listeners) {
					listener.onProgressChange(totalBytes, cutBytes, progress);
				}
			}
			return false;
		}
	});

	public static class Builder {

		private Updater mUpdater;

		public Builder(Context context) {
			mUpdater = new Updater(context);
		}

		/**
		 * 设置下载下来的apk文件名
		 * 
		 * @param apkName
		 *            apk文件的名字
		 * @return
		 */
		public Builder setFileName(String apkName) {
			mUpdater.apkName = apkName;
			return this;
		}

		/**
		 * 设置apk下载的路径
		 * 
		 * @param apkPath
		 *            自定义的全路径
		 * @return
		 */
		public Builder setFilePath(String apkPath) {
			mUpdater.apkPath = apkPath;
			return this;
		}

		/**
		 * 设置下载apk的文件目录
		 * 
		 * @param dirName
		 *            sd卡的文件夹名字
		 * @return
		 */
		public Builder setFileDir(String dirName) {
			mUpdater.apkDirName = dirName;
			return this;
		}

		/**
		 * 设置下载的链接地址
		 * 
		 * @param downloadUrl
		 *            apk的下载链接
		 * @return
		 */
		public Builder setDownloadUrl(String downloadUrl) {
			mUpdater.downloadUrl = downloadUrl;
			return this;
		}

		/**
		 * 通知栏显示的标题
		 * 
		 * @param title
		 *            标题
		 * @return
		 */
		public Builder setNotificationTitle(String title) {
			mUpdater.title = title;
			return this;
		}

		/**
		 * 隐藏通知栏
		 * 
		 * @return
		 */
		public Builder hideNotification() {
			mUpdater.hideNotification = true;
			return this;
		}

		/**
		 * 是否为debug模式，会输出很多log信息（手动斜眼）
		 * 
		 * @return
		 */
		public Builder debug() {
			LogUtils.isDebug = true;
			return this;
		}

		/**
		 * 允许漫游网络可下载
		 * 
		 * @return
		 */
		public Builder allowedOverRoaming() {
			mUpdater.allowedOverRoaming = true;
			return this;
		}

		public Builder clearCache() {
			mUpdater.claerCache = true;
			return this;
		}

		/**
		 * 开始下载
		 * 
		 * @return
		 */
		public Updater start() {
			mUpdater.download();
			return mUpdater;
		}

	}

}
