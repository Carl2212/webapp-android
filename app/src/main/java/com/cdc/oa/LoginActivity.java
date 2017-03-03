package com.cdc.oa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.cdc.common.Constants;
import com.cdc.common.HttpThread;
import com.cdc.common.MyLog;
import com.cdc.common.Utils;
import com.cdc.http.resp.LoginResp;
import com.cdc.http.resp.MenusInfo;
import com.cdc.oa.base.BaseActivity;
import com.cdc.util.ExitApplication;
import com.cdc.zqoa.R;


/**
 * 登陆控制类
 * @author tanjin
 * @since 2014-07-23
 *
 */
public class LoginActivity extends BaseActivity{
	private EditText userName_et;
	private EditText password_et;
//	private BaseSharedPreferences sp1;
	private List<MenusInfo> newList = new ArrayList<MenusInfo>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		setContentView(R.layout.login);
		userName_et = (EditText)findViewById(R.id.et_username);
		password_et = (EditText)findViewById(R.id.et_password);
//		che_mima = (ToggleButton)findViewById(R.id.cb_mima);
//		che_auto = (ToggleButton)findViewById(R.id.cb_auto);
//		server_address = (ImageView)findViewById(R.id.server_address);
//		et_address = (EditText)findViewById(R.id.et_address);
		Button button = (Button)findViewById(R.id.btn_login);
		//查看是否记住密码以及自动登陆
		
		
		//增加点击事件
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String userName = userName_et.getText().toString();
				String password =  password_et.getText().toString();
				
				if(validate()){
					
					Map<String, String> map = new HashMap<String, String>();
					map.put("username", userName);
					map.put("password", password);
					map.put("alsocount","Y");
					map.put("command", "login");
					map.put("cmd", "login");
					map.put("qybm", LoginActivity.this.getString(R.string.qybm));
					map.put("xmbm", LoginActivity.this.getString(R.string.xmbm));
					
					String request_url = LoginActivity.this.getString(R.string.server_url) + Constants.GET_EXECUTE_URL;
					MyLog.i("请求URL：" + request_url);
					new HttpThread(request_url, map, mHandler).start();
					showProgressDialog("验证用户名密码，请稍候");
					
					
				}
			}
		});
		
		
		
	}
	
	/**
	 * 验证用户名密码
	 * @param userName
	 * @param password
	 * @return
	 */
	private boolean validate(){
		String userName = userName_et.getText().toString();
		String password =  password_et.getText().toString();
		
		if(Utils.isEmpty(userName)){
			showToast("用户名不能为空");
			return false;
		}
		if(Utils.isEmpty(password)){
			showToast("密码不能为空");
			return false;
		}
		return true;
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case Constants.RESULT_SUCCESS:
				Map<String, String> map = new HashMap<String, String>();
				Object obj = msg.obj;
				if(obj != null){
					String json = (String)obj;
					try {
						LoginResp resp = JSON.parseObject(json, LoginResp.class);
						if(resp != null 
								&& resp.getHeader() != null
								&& resp.getResult() !=null){
							String code = resp.getHeader().getCode();
							if("1".equals(code)){
								hideProgressDialog();
								Intent intent=new Intent();  
						        intent.putExtra("userName", userName_et.getText().toString());  
						        setResult(Constants.GETUSERNAMEREQUESTCODE, intent); 
						        finish();
							}else{
								String describe = resp.getHeader().getDescribe();
								hideProgressDialog();
								showToast(describe);
								break;
							}
						}else{
							hideProgressDialog();
							showToast("登陆失败");
							break;
						}
						
					} catch (Exception e) {
						hideProgressDialog();
						MyLog.i("解析json错误:" + e.getMessage());
						e.printStackTrace();
						break;
					}
				}
				
				break;

			default:
				hideProgressDialog();
				showToast("登陆失败");
				break;
			}
		}
		
	};
	
}	
