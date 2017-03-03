package com.cdc.plugin;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.cdc.common.MD5;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.widget.Toast;

public class DownloadFile extends CordovaPlugin {

	public static final String ACTION = "download";
	private CallbackContext callbackContext;
	private DownloadManager downloadManager;

	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException {

		String result = "";
		this.callbackContext = callbackContext;
		if (ACTION.equals(action)) {
			/*String url = args.getString(0);
			String fileName = args.getString(1);
			String mimetype = args.getString(2);
						
			result = this.download(url, fileName, mimetype);
			if (result.length() > 0) {
				return true;
			}*/
			String url = args.getString(0);
			String fileName = args.getString(1);
			FileDownloadActivity.startActivity(this.cordova.getActivity(), url, fileName);
			return true;
		}
		return false;
	}
	
	public void openOldFile(String fileName, String mimeType) {
		File docFile = new File(fileName);
		try {
			Uri path = Uri.fromFile(docFile);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(path, mimeType);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.cordova.getActivity().startActivity(intent);

		} catch (android.content.ActivityNotFoundException e) {
//			Log.d("PhoneGapLog", "docPlugin:Error loading url " + fileName + ":"+ e.toString());
			Toast.makeText(this.cordova.getActivity(), "未找到打开此类文件的应用", Toast.LENGTH_SHORT).show();
		}

	}
	
	public void openFile(long id, String mimeType) {
		/*File docFile = new File(fileName);
		try {
			Uri path = Uri.fromFile(docFile);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(path, mimeType);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.cordova.getActivity().startActivity(intent);

		} catch (android.content.ActivityNotFoundException e) {
//			Log.d("PhoneGapLog", "docPlugin:Error loading url " + fileName + ":"+ e.toString());
			Toast.makeText(this.cordova.getActivity(), "未找到打开此类文件的应用", Toast.LENGTH_SHORT).show();
		}*/
		Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri downloadFileUri = downloadManager.getUriForDownloadedFile(id);
        intent.setDataAndType(downloadFileUri,mimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.cordova.getActivity().startActivity(intent);

	}

	public String download(final String url, final String fileName, final String mimetype) {
		Log.d("cordovaLog", "download url " + url);
		
		String savePath = null;
		String saveName = null;
		
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 获得存储卡的路径
			String sdpath = Environment.getExternalStorageDirectory() + "/";
			savePath = sdpath + "moa";
    	};
    	
    	/*int idx = fileName.lastIndexOf(".");
    	if(idx>-1){
    		String fileSufix = fileName.substring(idx);
    		String m5Str;
			try {
				m5Str = MD5.getMD5(url);
			} catch (NoSuchAlgorithmException e) {
				m5Str=new Date().getTime()+"";
			}
    		saveName = m5Str+fileSufix;
    	}*/
    	
    	saveName=fileName;
    	
    	File f = new File(savePath);
    	if(!f.exists()){
    		f.mkdirs();
    	}
    	
    	final File saveFile = new File(savePath, saveName.replace("\"", ""));
    	if(saveFile.exists()){
    		openOldFile(saveFile.getPath(),mimetype);
    		return "success";
    	}
    	
    	Toast.makeText(this.cordova.getActivity(), "附件下载中", Toast.LENGTH_LONG).show();
        String serviceString = Context.DOWNLOAD_SERVICE;
        
        downloadManager = (DownloadManager)this.cordova.getActivity().getSystemService(serviceString);

        Uri uri = Uri.parse(url);
        Request request = new Request(uri);
//        request.addRequestHeader("Cookie", CookieStr);
        request.setDestinationUri(Uri.fromFile(saveFile));
        
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        final long myDownloadReference = downloadManager.enqueue(request);
        
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        
        BroadcastReceiver receiver = new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)&&myDownloadReference == reference) {
              Log.d("xxxxxxxx",myDownloadReference+"");
              openFile(reference,mimetype);
            }
          }
        };
        this.cordova.getActivity().registerReceiver(receiver, filter);
        return "success";
	}

	private void callback(CallbackContext callbackContext, int resultCode) {

		if (resultCode==1) {
			callbackContext.success();
        }else if (resultCode==0) {
            Toast.makeText(this.cordova.getActivity(), "打开失败！", Toast.LENGTH_SHORT).show();
        }

	}
	
	@Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        Log.d("xxxxxxxxxxxxxxxx", "call back....................");
//	        callback(this.callbackContext, resultCode);
	        Message msg = new Message();
	        msg.arg1 = resultCode;
	        handler.handleMessage(msg);
	        super.onActivityResult(requestCode, resultCode, data);
	       
	}
	
	private Handler handler = new Handler() {  
        public void handleMessage(Message msg) { 
        	int result = msg.arg1;
        	callback(DownloadFile.this.callbackContext, result);
        }
	};

}