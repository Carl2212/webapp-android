package com.cdc.plugin;

import java.io.File;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.Environment;
import android.util.Log;

import com.cdc.util.ExitApplication;

public class ExitApp extends CordovaPlugin {

	public static final String ACTION = "exit";

	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException {
		Log.d("xxxxxxxxxxxxxxxxxxx--------", args.toString());
		if (ACTION.equals(action)) {
			clearTmpFiles();
			ExitApplication.getInstance().exit();
			callback(callbackContext);
		}
		return false;
	}
	
	private void clearTmpFiles(){
		String sdpath = Environment.getExternalStorageDirectory() + "/";
		String savePath = sdpath + "moa";
		File folder = new File(savePath);
		if(folder.exists()){
			File[] files = folder.listFiles();
			for(File f: files){
				try{
					f.delete();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}


	private void callback(CallbackContext callbackContext) {

		callbackContext.success();

	}
	
	

}