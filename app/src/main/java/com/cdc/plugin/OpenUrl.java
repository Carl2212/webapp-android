package com.cdc.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.widget.Toast;

public class OpenUrl extends CordovaPlugin {

	public static final String ACTION = "open";
	private CallbackContext callbackContext;

	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException {

		String result = "";
		this.callbackContext = callbackContext;
		if (ACTION.equals(action)) {
			String url = args.getString(0);
			String closeBtnString = null;
			if(args.length()>1){
				closeBtnString=args.getString(1);
			}
			
			result = this.openUrl(url,closeBtnString);
			if (result.length() > 0) {
				return true;
			}
		}
		return false;
	}

	public String openUrl(final String url, final String closeBtnString) {
		Log.d("PhoneGapLog", "docPlugin:Error loading url " + url);
		try {
//			 Bundle bundle = this.cordova.getActivity().getIntent().getExtras();
//			 bundle.putString("LoadUrlTimeoutValue", "10000");
			 /*cordova.getActivity().runOnUiThread(new Runnable() {
	                public void run() {
	                	Intent intent = new Intent();
	        			intent.setClass(OpenUrl.this.cordova.getActivity(), WebViewActivity.class);
	        			intent.putExtra("url", url);
	        			OpenUrl.this.cordova.startActivityForResult(OpenUrl.this, intent, 0);
	                }
	         });*/
			Intent intent = new Intent();
			intent.setClass(OpenUrl.this.cordova.getActivity(), WebViewActivity.class);
			intent.putExtra("url", url);
			intent.putExtra("closeBtnString", closeBtnString);
			OpenUrl.this.cordova.getActivity().startActivity(intent);

			return "";

		} catch (android.content.ActivityNotFoundException e) {
			Log.d("PhoneGapLog", "docPlugin:Error loading url " + url + ":"+ e.toString());
			return e.toString();
		}

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
        	callback(OpenUrl.this.callbackContext, result);
        }
	};

}