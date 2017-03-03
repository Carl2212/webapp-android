package com.cdc.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;

import com.cdc.oa.UkeyHandleActivity;

public class UpdatePIN extends CordovaPlugin {

	public static final String ACTION = "update";

	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException {

		String result = "";

		if (ACTION.equals(action)) {
			String message = "";
			this.callback(message, callbackContext);
			result = this.update();
			if (result.length() > 0) {
				return true;
			}
		}
		return false;
	}

	public String update() {
		Intent intent = new Intent(this.cordova.getActivity(),UkeyHandleActivity.class);
		intent.putExtra("type", "ukey_revise");
		this.cordova.getActivity().startActivity(intent);
		
		return "";
	}

	private void callback(String message, CallbackContext callbackContext) {

		if (message != null && message.length() > 0) {
			callbackContext.success(message);// 这里的succsee函数，就是调用js的success函数
		} else {
			callbackContext.error("Expectedone non-empty string argument.");
		}

	}

}