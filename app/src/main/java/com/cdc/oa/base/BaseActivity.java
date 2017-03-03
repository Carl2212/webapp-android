package com.cdc.oa.base;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cdc.zqoa.R;


public class BaseActivity extends Activity implements OnClickListener{

	public final String TAG_BASE = this.getClass().getSimpleName();
	private ProgressDialog progressDialong;
	Button btn_left ;
	RelativeLayout top_relative;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_base); 
	}
	
	@Override
	public void onClick(View v) {
		/*// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_left:
			onBackPressed();
			break;
		default:
			break;
		}*/
	}
	
	public void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	public void showProgressDialog(String text) {
		progressDialong = ProgressDialog.show(this, "", text);
		progressDialong.setCancelable(true);
	}

	public void showNotCancelDialog(String text) {
		progressDialong = ProgressDialog.show(this, "", text);
		progressDialong.setCancelable(false);
	}

	public void hideProgressDialog() {
		if (!this.isFinishing() && null != progressDialong
				&& progressDialong.isShowing()) {
			progressDialong.cancel();
		}
	}
	
	/**
	   * 
	   * 方法名: showDialog</br> 详述: 打开对话框</br>  创建时间：2015-1-26</br>
	   * 
	   * @param msg
	   */
	  protected void showFinishDialog(String msg) {
	    new AlertDialog.Builder(this).setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener() {

	      @Override
	      public void onClick(DialogInterface dialog, int which) {
	        finish();
	      }
	    }).setMessage(msg).setCancelable(false).create().show();
	  }

	public interface SuccessResultCallBack {
		public void callBack(Object backResult);
	}
	
	
	
	/*
	public Drawable setTopRelative(String style){
		Drawable drawable = null;
		if(Constants.Color.DARKBLUE.equals(style)){
			drawable = getResources().getDrawable(R.drawable.btn_role_login_darkblue);
		}else if(Constants.Color.LIGHTBLUE.equals(style)){
			drawable = getResources().getDrawable(R.drawable.btn_role_login_lightblue);
		}else if(Constants.Color.RED.equals(style)){
			drawable = getResources().getDrawable(R.drawable.btn_role_login_red);
		}else if(Constants.Color.GREEN.equals(style)){
			drawable = getResources().getDrawable(R.drawable.btn_role_login_green);
		}else if(Constants.Color.YELLOW.equals(style)){
			drawable = getResources().getDrawable(R.drawable.btn_role_login_yellow);
		}else if(Constants.Color.PINK.equals(style)){
			drawable = getResources().getDrawable(R.drawable.btn_role_login_pink);
		}else{
			drawable = getResources().getDrawable(R.drawable.btn_role_login_darkblue);
		}
		return drawable;
	}*/
}
