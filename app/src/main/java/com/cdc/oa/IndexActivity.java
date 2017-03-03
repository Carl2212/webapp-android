package com.cdc.oa;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.cdc.common.Constants;
import com.cdc.common.HttpThread;
import com.cdc.common.MyLog;
import com.cdc.http.resp.SimpleResp;
import com.cdc.oa.base.BaseActivity;
import com.cdc.zqoa.R;
import com.ukey.MiscUtils;

public class IndexActivity extends BaseActivity{
	private String ICCID;
	private String certHexStr;
	private String logonRandomStr;
	private String signedStr;
	private String userName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index);
		clearTmpFiles();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				getCert(Constants.GETCERTREQUESTCODE);
				
			}
		}, 1000);
	}
	
	private void clearTmpFiles(){
		String sdpath = Environment.getExternalStorageDirectory() + "/";
		String savePath = sdpath + "moa/";
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
	
	/**
	 * 获取证书
	 * @param requestCode 用于区分是验证是否登录时获取还是刚绑定后获取
	 */
	void getCert(int requestCode){
		Intent intent = new Intent(IndexActivity.this,UkeyHandleActivity.class);
		intent.putExtra("type", "ukey_certificate");
		startActivityForResult(intent,requestCode);
	}
	
	/**
	 * 验证oa用户名和密码,并获取用户名
	 */
	void getUserName(){
		Intent intent = new Intent(IndexActivity.this,LoginActivity.class);
		startActivityForResult(intent,Constants.GETUSERNAMEREQUESTCODE);
	}
	
	/**
	 * 将oa账号和u盾绑定
	 * @param userName
	 */
	void bindUkey(String userName){
		this.userName=userName;
		Intent intent = new Intent(IndexActivity.this,UkeyHandleActivity.class);
		intent.putExtra("type", "ukey_bind");
		intent.putExtra("userName", userName);
		startActivityForResult(intent,Constants.UKBINDCODE);
	}
	
	/**
	 * 解除用户账号与U盾的绑定
	 * 
	 */
	private void dounbind(){
		Intent intent = new Intent(IndexActivity.this,UkeyHandleActivity.class);
		intent.putExtra("type", "ukey_unbound");
		startActivityForResult(intent,Constants.UKUNBINDCODE);
	}
	
	private void getSign(){
		Intent intent = new Intent(IndexActivity.this,UkeyHandleActivity.class);
		intent.putExtra("type", "ukey_sign");
		intent.putExtra("toSignStr", logonRandomStr);
		MyLog.i("to sign------------->"+logonRandomStr);
		startActivityForResult(intent,Constants.UKSIGNREQUEST);
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
        if(Constants.GETCERTREQUESTCODE==requestCode){
        	Bundle extras = data.getExtras();
        	int result = extras.getInt("result");
        	switch(result){
        		case 0:
        			ICCID=extras.getString("ICCID");
        			certHexStr=extras.getString("certHexStr");
        			
        			getLogonRandom();
        			
        			break;
	        	case 4:
	        		getUserName();
	        		break;
	        		
	        	default:
	        		String msg = extras.getString("msg");
	            	showFinishDialog("获取证书失败,"+msg);
        	}
        	
        	
        }else if(Constants.GETUSERNAMEREQUESTCODE==requestCode){
        	String userName=data.getExtras().getString("userName");
        	if(null!=userName){
        		bindUkey(userName);
        	}
        	
        }else if(Constants.UKBINDCODE==requestCode){
        	int result=data.getExtras().getInt("result");
        	if(checkOprResult(result)){
        		showLongToast("关联成功");
        		getCert(Constants.GETCERTRAFTERBIND);
        	}
        	
        }else if(Constants.GETCERTRAFTERBIND==requestCode){
        	int result = data.getExtras().getInt("result");
        	switch(result){
        		case 0:
        			bindOnMoa();
        			break;
	        	case 4:
	        		getUserName();
	        		break;
	        		
	        	default:
	            	showFinishDialog("获取证书失败,code:"+result);
        	}
        	
        }else if(Constants.UKUNBINDCODE==requestCode){
        	int result=data.getExtras().getInt("result");
        	if(checkOprResult(result)){
        		showLongToast("解绑成功");
        		getUserName(); //重新绑定操作
        	}
        	
        }else if(Constants.UKSIGNREQUEST==requestCode){
        	Bundle extras = data.getExtras();
        	int result = extras.getInt("result");
        	if(checkOprResult(result)){
        		signedStr = extras.getString("signed");
        		MyLog.i("signed------------->"+signedStr);
        		loadMoa(); //重新绑定操作
        	}
        	
        }
    }  
	
	private void getLogonRandom(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("iccid", ICCID);
		
		
		String request_url = IndexActivity.this.getString(R.string.server_url) + "wap/getrandom";
//		String request_url = "http://192.168.150.157/ms/wap/getrandom";
		MyLog.i("请求URL：" + request_url);
		new HttpThread(request_url, map, getLoginRandomHandler).start();
		showProgressDialog("验证绑定状态，请稍候");
	}
	
	
	private boolean checkOprResult(int oprResult) {
	    if (oprResult == 0) {
	      return true;
	    }
	    switch (oprResult) {
	      case 1:
	        System.out.println("1－U盾环境不具备（手机无UIM卡、UIM卡不支持U盾、UIM卡上未安装U盾应用、未开通或未设置pin码等）");
	        showFinishDialog("U盾环境不具备（手机无UIM卡、UIM卡不支持U盾、UIM卡上未安装U盾应用、未开通或未设置pin码等）");
	        break;
	      case 2:
	        System.out.println("2－U盾插件版本需更新");
	        showFinishDialog("U盾插件版本需更新");
	        break;
	      case 3:
	        System.out.println("3－U盾已锁定 ");
	        showFinishDialog("U盾已锁定");
	        break;
	      case 4:
	        System.out.println("4－本应用未关联证书 ");
	        showFinishDialog("本应用未关联证书 ");
	        break;
	      case 5:
	        System.out.println("5－本应用关联证书已过期");
	        showFinishDialog("本应用关联证书已过期");
	        break;
	      case 6:
	        System.out.println("6－无操作权限");
	        showFinishDialog("无操作权限");
	        break;
	      case 99:
	        System.out.println("99 –其它原因（服务端异常、网络频繁闪断等等");
	        showFinishDialog("其它原因（服务端异常、网络频繁闪断等等");
	        break;
	      
	      default:
	    	 showFinishDialog("操作失败");
	        break;
	    }
	    return false;
	  }
	
	private Handler getLoginRandomHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case Constants.RESULT_SUCCESS:
				Object obj = msg.obj;
				if(obj != null){
					String json = (String)obj;
					try {
						SimpleResp resp = JSON.parseObject(json, SimpleResp.class);
						if(resp != null && resp.getHeader() != null){
							String code = resp.getHeader().getCode();
							if("1".equals(code)){
								logonRandomStr=resp.getResult();
								getSign();
								hideProgressDialog();
//								Intent intent=new Intent();  
//						        intent.putExtra("userName", userName_et.getText().toString());  
//						        setResult(Constants.GETUSERNAMEREQUESTCODE, intent); 
//						        finish();
							}else if("0".equals(code)){ //ICCID未与用户账号绑定
								dounbind();
							}else{ //其它错误
								String describe = resp.getHeader().getDescribe();
								hideProgressDialog();
								showFinishDialog(describe);
								
							}
						}else{
							hideProgressDialog();
							showFinishDialog("获取登录序号失败");
						}
						
					} catch (Exception e) {
						hideProgressDialog();
						MyLog.i("解析json错误:" + e.getMessage());
						e.printStackTrace();
						showFinishDialog("获取登录序号失败");
					}
				}
				
				break;

			default:
				hideProgressDialog();
				showFinishDialog("获取登录序号失败");
				break;
			}
		}
		
	};
	
	private void bindOnMoa(){
		Handler bindOnMoaHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.arg1) {
				case Constants.RESULT_SUCCESS:
					Object obj = msg.obj;
					if(obj != null){
						String json = (String)obj;
						try {
							SimpleResp resp = JSON.parseObject(json, SimpleResp.class);
							if(resp != null && resp.getHeader() != null){
								String code = resp.getHeader().getCode();
								if("1".equals(code)){
									getLogonRandom();
								}else if("0".equals(code)){ //ICCID未与用户账号绑定
									String describe = resp.getHeader().getDescribe();
									hideProgressDialog();
									showFinishDialog(describe);
								}else{ //其它错误
									String describe = resp.getHeader().getDescribe();
									hideProgressDialog();
									showFinishDialog(describe);
								}
							}else{
								hideProgressDialog();
								showFinishDialog("绑定失败");
							}
							
						} catch (Exception e) {
							hideProgressDialog();
							MyLog.i("解析json错误:" + e.getMessage());
							e.printStackTrace();
							showFinishDialog("绑定失败");
						}
					}
					
					break;

				default:
					hideProgressDialog();
					showFinishDialog("绑定失败");
					break;
				}
			}
			
		};
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("iccid", ICCID);
		
		map.put("username", userName);
		map.put("cert", certHexStr);
		
		
		String request_url = IndexActivity.this.getString(R.string.server_url) + "wap/cabind";
//		String request_url = "http://192.168.150.157/ms/wap/cabind";
		MyLog.i("请求URL：" + request_url);
		new HttpThread(request_url, map, bindOnMoaHandler).start();
		showProgressDialog("CA绑定中");
	}
	
	private void testDescry(){
		byte[] certBytes = MiscUtils.hexStrToBytes(certHexStr);
//        String mapcachekey = Base64.encode(certBytes);
        
        X509Certificate cert = MiscUtils.createCertficate(certBytes, "");
        PublicKey publicKey = cert.getPublicKey();
        Cipher cipher;
		try {
			cipher = Cipher.getInstance(publicKey.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, publicKey);  
			  
//	        byte[] b = cipher.doFinal(Base64.decode(signedStr.getBytes()));
//	        String str = new String(b);
	        
	        byte[] b2 = cipher.doFinal(MiscUtils.hexStrToBytes(signedStr));
	        String str2 = MiscUtils.bytesToHexStr(b2);
	        
	        MessageDigest md = MessageDigest.getInstance("SHA-256");
	        md.update(logonRandomStr.getBytes());
	        byte[] b1 = md.digest();
	        String str1 = MiscUtils.bytesToHexStr(b1);
	        
	        
	        
	        
	       // MyLog.w("desc---------->"+str);
	        MyLog.w("desc---------->"+str2);
	        MyLog.w("desc---------->"+str1);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
	}
	
	
	
	private void loadMoa(){
		
	//	testDescry();
		
		Intent intent = new Intent(IndexActivity.this,MainActivity.class);
		intent.putExtra("ICCID", ICCID);
		intent.putExtra("signedStr", signedStr);
		MyLog.i("go to moa..........................");
		MyLog.i("ICCID:"+ICCID);
		MyLog.i("signedStr:"+signedStr);
		startActivity(intent);
		finish();
	}
	
}
