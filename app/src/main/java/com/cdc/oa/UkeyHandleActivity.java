package com.cdc.oa;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.cdc.common.Base64;
import com.cdc.common.Constants;
import com.cdc.util.ExitApplication;
import com.gsta.ukeyesurfing.aidl.GetCertOprResult;
import com.gsta.ukeyesurfing.aidl.IUkeyService;
import com.ukey.MiscUtils;

/**
 * 
 * 类名: UkeyHandleActivity</br> 包名：mdp.activity </br> 描述: 在主界面处理一些事情</br>
 * 发布版本号：</br> 开发人员： huangzy</br> 创建时间： 2015-1-29
 */
public class UkeyHandleActivity extends Activity {

  private int absPkgHash;
  private String spUserId_ICCID;
  /** aidl服务 */
  private ServiceConnection ukeySc = null;
  private IUkeyService mBind = null;
  private Handler serviceHandler = null;
  /** 提示框 */
  private ProgressDialog progressDlg;
  
  private String username;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE); 
    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    spUserId_ICCID = tm.getSimSerialNumber(); // 取出ICCID.
    
    try{
    	ukeySc = new ServiceConnection() {

	      @Override
	      public void onServiceDisconnected(ComponentName name) {
	        mBind = null;
	      }

	      @Override
	      public void onServiceConnected(ComponentName name, IBinder service) {
	        mBind = IUkeyService.Stub.asInterface(service);
	        try {
	          System.out.println("mBind.getVersion()>>" + mBind.getVersion());
	        } catch (RemoteException e) {
	          e.printStackTrace();
	        }
	      }
	    };
	    
	    final Intent intent = new Intent();
        intent.setAction("com.gsta.ukeyesurfing.service.UkeyService");
        intent.setPackage("com.gsta.ukeyesurfing");
	    
	    bindService(intent, ukeySc, Context.BIND_AUTO_CREATE);
	    
	    
    }catch(Throwable t){
    	System.out.println("--------------------->error:");
    	t.printStackTrace();
    }
    String type = getIntent().getStringExtra("type");
    if (type.equalsIgnoreCase("ukey_revise")) {
      // 修改PIM
      ukey_revise();
    } else if (type.equalsIgnoreCase("ukey_certificate")) {
      // 查看证书
      ukey_certificate();
    } else if (type.equalsIgnoreCase("ukey_unbound")) {
      // 解除绑定
      ukey_unbound();
    } else if(type.equalsIgnoreCase("ukey_updatecertificate")){
      ukey_update();
    } else if(type.equalsIgnoreCase("ukey_bind")){
    	//绑定
    	ukey_bind(getIntent().getStringExtra("userName"));
    } else if(type.equalsIgnoreCase("ukey_sign")){
    	getSign(getIntent().getStringExtra("toSignStr"));
    }else{
      this.finish();
    }
  }

  @Override
  protected void onDestroy() {
    if (null != ukeySc) {
      unbindService(ukeySc);
    }
    super.onDestroy();
  }

  /**
   * 
   * 方法名: ukey_revise</br> 详述: 修改pim</br> 开发人员：huangzy</br> 创建时间：2015-1-29</br>
   */
  private void ukey_revise() {
    Intent intent = new Intent("com.gsta.ukeyesurfing.modifypin");
    startActivity(intent, 6);
  }

  /**
   * 
   * 方法名: ukey_certificate</br> 详述: 查看证书</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-29</br>
   */
  private void ukey_certificate() {
    serviceHandler = new Handler(mHander);
    if (progressDlg == null) {
      progressDlg = new ProgressDialog(this);
      progressDlg.setMessage("获取U盾证书中，请稍候！");
      progressDlg.setCancelable(false);
      progressDlg.show();
    }
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        // 获取我的证书
        int result = getCertificate();
        /*if(result != 0){
          serviceHandler.sendEmptyMessage(result);
        }*/
      }
    }).start();
  }
  
  /**
   * 
   * 方法名: ukey_update</br>
   * 详述: 更新证书</br>
   * 开发人员：huangzy</br>
   * 创建时间：2015-2-11</br>
   */
  private void ukey_update(){
    Intent intent = new Intent("com.gsta.ukeyesurfing.needpinverify");
    Bundle extras = new Bundle();
    extras.putString("SP_USERID", spUserId_ICCID);
    extras.putInt("OPR_TYPE", 2);//1:SIGN; 2:UPDATE CERTIFICATE;
    intent.putExtras(extras);
    startActivity(intent, 2); 
  }

  /**
   * 
   * 方法名: ukey_unbound</br> 详述: 解除绑定</br> 开发人员：huangzy</br> 创建时间：2015-1-29</br>
   */
  private void ukey_unbound() {
    progressDlg = new ProgressDialog(this);
    progressDlg.setMessage("正在解除业务绑定中，请稍候！");
    progressDlg.setCancelable(false);
    progressDlg.show();
    Intent intent = new Intent("com.gsta.ukeyesurfing.needpinverify");
    Bundle extras = new Bundle();
    extras.putInt("OPR_TYPE", 4);//1:SIGN; 2:UPDATE CERTIFICATE;
    extras.putString("SP_USERID", spUserId_ICCID);
    extras.putString("TITLE", "解除业务绑定");
    intent.putExtras(extras);
    startActivity(intent, 4);
  }
  
  /**
   * 
   * 方法名: ukey_unbound</br> 详述: 绑定</br>  创建时间：2016-11-18</br>
   */
  private void ukey_bind(String userName) {
	  progressDlg = new ProgressDialog(this);
	  progressDlg.setMessage("正在绑定业务中，请稍候！");
	  progressDlg.setCancelable(false);
	  progressDlg.show();
	  Intent intent = new Intent("com.gsta.ukeyesurfing.needpinverify");
	  Bundle extras = new Bundle();
	  extras.putInt("OPR_TYPE", 3);// 1:SIGN; 2:UPDATE CERTIFICATE;
	  extras.putString("SP_USERID", spUserId_ICCID);
	  intent.putExtras(extras);
	  
	  startActivity(intent, 3);
	  
  }
  
  /**
   * 
   * 方法名: getSign</br> 详述: 获取签名</br> 开发人员：huangzy</br> 创建时间：2015-1-22</br>
   */
  private void getSign(String toSignStr) {
    Intent intent = new Intent("com.gsta.ukeyesurfing.needpinverify");
    Bundle extras = new Bundle();
    extras.putInt("OPR_TYPE", 1);// 1:SIGN; 2:UPDATE CERTIFICATE;
		extras.putByteArray("TO_BE_SIGNED_DATA", toSignStr.getBytes());
    extras.putString("HASH_ALGORITHM", "SHA-256");
    extras.putString("SP_USERID", spUserId_ICCID);
    extras.putString("TITLE", "签名");
    intent.putExtras(extras);
    startActivity(intent, 1);
  }
  
  /**
   * @deprecated
   * 方法名: checkUserNamePassWord</br> 详述: 验证帐号密码</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-22</br>
   */
  public void uShieldUnbind() {
    HashMap<String, String> datas = new HashMap<String, String>();
    datas.put("username", username);
    MiscUtils.loadDatas(this,new Handler(new Handler.Callback() {

      @Override
      public boolean handleMessage(Message msg) {
        switch (msg.what) {
          case Constants.PROGRESS_MESSAGE_NORMAL: // 普通信息消息对话框
            dismiss();
            showDialogColse("解除绑定失败！！ U盾解除绑定成功，业务平台解除绑定失败。请联系管理员后重新登录！");
            break;
          case -99:
            String xml = (String) msg.obj;
            String code = MiscUtils.getTagValue(xml, "code", "0");
            System.out.println("code>>>" + code);
            String message = MiscUtils.getTagValue(xml, "message", "");
            System.out.println("message>>>" + message);
            if (code.equals("1")) {
              dismiss();
              showDialogColse("解除绑定成功，请重新启动应用！");
            } else {
              dismiss();
              showDialogColse("解除绑定失败！！ U盾解除绑定成功，业务平台解除绑定失败。请联系管理员后重新登录！");
            }
            break;
        }
        return false;
      }
    }), "zqgxq_uShieldUnbind", datas);
  }
  
  
  
  
  
  
  /**
   * 
   * 方法名: dismiss</br> 详述: 取消loading对话框</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-22</br>
   */
  private void dismiss() {
    if (progressDlg != null && progressDlg.isShowing()) {
      progressDlg.dismiss();
    }
    progressDlg = null;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == absPkgHash + 6) {
      if (data != null) {
        int oprResult = data.getIntExtra("OPR_RESULT", -1);
        System.out.println("oprResult>>>" + oprResult);
        if (checkOprResult(oprResult)) {
          Toast.makeText(this, "修改PIN码成功！", Toast.LENGTH_LONG).show();
          finish();
        }
      } else {
        Toast.makeText(this, "修改PIN码失败！", Toast.LENGTH_LONG).show();
        finish();
      }
    }else if (requestCode == absPkgHash + 4) {
    	int oprResult=-99999;
    	if (data != null) {
    		oprResult = data.getIntExtra("OPR_RESULT", -1);
        
    	}
    	dismiss();
    	Intent intent=new Intent();  
  		intent.putExtra("result", oprResult);
  		setResult(Constants.UKUNBINDCODE, intent); 
  		finish();
    }else if (requestCode == absPkgHash + 1) {
    	int oprResult=-99999;
    	String signedStr = null;
    	if (data != null) {
    		oprResult = data.getIntExtra("OPR_RESULT", -1);
    		if(0==oprResult){
    			//signedStr = new String(data.getByteArrayExtra("SIGNED_DATA"));
    			
    			byte[] hash =data.getByteArrayExtra("SIGNED_DATA");
    			
    			   		  
    		    signedStr = MiscUtils.bytesToHexStr(hash);// Base64.encode(hash);
    		}
    	}
    	dismiss();
    	Intent intent=new Intent();  
  		intent.putExtra("result", oprResult);
  		intent.putExtra("signed", signedStr);
  		setResult(Constants.UKSIGNREQUEST, intent); 
  		finish();
    }else if(requestCode == absPkgHash + 2){
      if (data != null) {
        int oprResult = data.getIntExtra("OPR_RESULT", -1);
        System.out.println("oprResult>>>" + oprResult);
        if (checkOprResult(oprResult)) {
          dismiss();
          Toast.makeText(this, "更新证书成功！", Toast.LENGTH_LONG).show();
          finish();
        }
      } else {
        dismiss();
        Toast.makeText(this, "更新证书失败！", Toast.LENGTH_LONG).show();
        finish();
      }
    }else if(requestCode == absPkgHash + 3){
    	int oprResult=-1;
        if (data != null) {
            oprResult = data.getIntExtra("OPR_RESULT", -1);
            System.out.println("oprResult>>>" + oprResult);
            
            
        }
        dismiss();
        Intent intent=new Intent();  
    	intent.putExtra("result", oprResult);
          
        setResult(Constants.UKBINDCODE, intent); 
    	finish();
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  
  Handler.Callback mHander = new Handler.Callback() {

    @Override
    public boolean handleMessage(Message msg) {
      switch (msg.what) {
        case 0:// "验证帐号密码有效性！"
          Bundle result = msg.getData();
          int resultCode = result.getInt("resultCode", -65535);
          String certHexStr=null;
          String msgStr = null;
          if(checkOprResult(resultCode)){
            certHexStr = result.getString("certHexStr");
            byte[] certBytes = MiscUtils.hexStrToBytes(certHexStr);
            String mapcachekey = Base64.encode(certBytes);
            System.out.println("mapcachekey>>>" + mapcachekey);
            if (certBytes != null) {
              X509Certificate cert = MiscUtils.createCertficate(certBytes, "");
              if (cert != null) {
                String certInfoStr = MiscUtils.certificationToString(cert);
                System.out.println("certInfoStr>>" + certInfoStr);
//                showDialog(certInfoStr);
              } else {
//                Toast.makeText(UkeyHandleActivity.this, "证书无法解析", Toast.LENGTH_LONG).show();
//                finish();
            	  resultCode = -9999;
            	  msgStr="证书无法解析";
              }
            } else {
//              Toast.makeText(UkeyHandleActivity.this, "证书数据异常： " + certHexStr, Toast.LENGTH_LONG).show();
//              finish();
            	resultCode = -9999;
            	msgStr="证书数据异常";
            }
          }
          
          Intent intent=new Intent();  
          intent.putExtra("result", resultCode);  
          intent.putExtra("msg", msgStr);
          
          intent.putExtra("certHexStr", certHexStr);
          intent.putExtra("ICCID", spUserId_ICCID);
//    	  intent.putExtra("result", 4);
          
          setResult(Constants.GETCERTREQUESTCODE, intent); 
          dismiss();
    	  finish();
          break;
        default:
          dismiss();

    	  int resultCode1=msg.what;
    	  String msg1="";
    	  if (msg.what == -100) {
	    	  msg1="没有检测到U盾插件！";
	        } else if (msg.what == -101) {
	    	  msg1="没有检测到U盾！";
	        } else if (msg.what == -102) {
	    	  msg1="U盾调用初始化失败！";
	        }
          
          Intent intent1=new Intent();  
          intent1.putExtra("result", resultCode1);  
          intent1.putExtra("msg", msg1);
          
          
          setResult(Constants.GETCERTREQUESTCODE, intent1); 
          dismiss();
    	  finish();
          
          break;
      }
      return true;
    }
  };
  
  
  /**
   * 
   * 方法名: getAbsPkgHash</br> 详述: 获取请求Code</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-22</br>
   * 
   * @param intent
   */
  private void startActivity(Intent intent, int type) {
    PackageManager pm = getPackageManager();
    List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
    boolean isIntentSafe = activities.size() > 0;
    if (isIntentSafe) {
      Signature[] signs = null;
      try {
        String pkgName = getApplicationContext().getPackageName();
        signs = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES).signatures;
      } catch (NameNotFoundException e) {
        e.printStackTrace();
      }
      int pkgHash = -1;
      if (signs != null) {
        pkgHash = Math.abs(signs[0].hashCode());
        absPkgHash = pkgHash;
      }
      startActivityForResult(intent, pkgHash + type);
    } else {
      Toast.makeText(this, "没有到U盾插件，请查看是否已经安装！", Toast.LENGTH_LONG).show();
      finish();
    }
  }

  /**
   * 
   * 方法名: getCertificate</br> 详述: 获取证书</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-22</br>
   */
  private int getCertificate() {
    if (mBind != null) {
      try {
        GetCertOprResult certOprResult = mBind.getCertificate(spUserId_ICCID);
        int resultCode = certOprResult.getOprResultCode();
        Bundle result = new Bundle();
        result.putInt("resultCode", resultCode);
        if (resultCode == 0) {
          result.putString("certHexStr", certOprResult.getCertHexStr());
        }
        Message resultMsg = Message.obtain();
        resultMsg.what = 0;
        resultMsg.setData(result);
        serviceHandler.sendMessage(resultMsg);
        return resultCode;
      } catch (Throwable e) {
        e.printStackTrace();
        serviceHandler.sendEmptyMessage(-101);
        return -101;
      }
    } else {
      // 没有找到翼支付Ukey插件.....
    	serviceHandler.sendEmptyMessage(-100);
      return -100;
    }
  }

  private boolean checkOprResult(int oprResult) {
    if (oprResult == 0) {
      return true;
    }
    switch (oprResult) {
      case 1:
        System.out.println("1－U盾环境不具备（手机无UIM卡、UIM卡不支持U盾、UIM卡上未安装U盾应用、未开通或未设置pin码等）");
        // Toast.makeText(context,
        // "1－U盾环境不具备（手机无UIM卡、UIM卡不支持U盾、UIM卡上未安装U盾应用、未开通或未设置pin码等）",
        // Toast.LENGTH_LONG).show();
        showDialog("U盾环境不具备（手机无UIM卡、UIM卡不支持U盾、UIM卡上未安装U盾应用、未开通或未设置pin码等）");
        break;
      case 2:
        System.out.println("2－U盾插件版本需更新");
        // Toast.makeText(context, "2－U盾插件版本需更新", Toast.LENGTH_LONG).show();
        showDialog("U盾插件版本需更新");
        break;
      case 3:
        System.out.println("3－U盾已锁定 ");
        // Toast.makeText(context, "3－U盾已锁定 ", Toast.LENGTH_LONG).show();
        showDialog("U盾已锁定");
        break;
      case 4:
        System.out.println("4－本应用未关联证书 ");
        // Toast.makeText(context, "4－本应用未关联证书 ", Toast.LENGTH_LONG).show();
        showDialog("本应用未关联证书 ");
        break;
      case 5:
        System.out.println("5－本应用关联证书已过期");
        // Toast.makeText(context, "5－本应用关联证书已过期", Toast.LENGTH_LONG).show();
        showDialog("本应用关联证书已过期");
        break;
      case 6:
        System.out.println("6－无操作权限");
        // Toast.makeText(context, "6－无操作权限", Toast.LENGTH_LONG).show();
        showDialog("无操作权限");
        break;
      case 99:
        System.out.println("99 –其它原因（服务端异常、网络频繁闪断等等");
        // Toast.makeText(context, "99 –其它原因（服务端异常、网络频繁闪断等等",
        // Toast.LENGTH_LONG).show();
        showDialog("其它原因（服务端异常、网络频繁闪断等等");
        break;
      default:
        System.out.println("签名失败（OPR_RESULT不为0），为null");
//         Toast.makeText(this, "签名失败（OPR_RESULT不为0），为null",
//         Toast.LENGTH_LONG).show();
//        showDialog("签名失败（OPR_RESULT不为0），为null");
        break;
    }
    return false;
  }

  /**
   * 
   * 方法名: showDialog</br> 详述: 打开对话框</br> 开发人员：huangzy</br> 创建时间：2015-1-26</br>
   * 
   * @param msg
   */
  private void showDialog(String msg) {
    new AlertDialog.Builder(this).setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        finish();
      }
    }).setMessage(msg).setCancelable(false).create().show();
  }
  
  /**
   * 
   * 方法名: showDialog</br> 详述: 打开对话框</br> 开发人员：huangzy</br> 创建时间：2015-1-26</br>
   * 
   * @param msg
   */
  private void showDialogColse(String msg) {
    new AlertDialog.Builder(this).setTitle("提示")
    .setPositiveButton("退出应用", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        ExitApplication.getInstance().exit();
      }
    }).setMessage(msg).setCancelable(false).create().show();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (KeyEvent.KEYCODE_BACK == keyCode) {
      return false;
    }
    return super.onKeyDown(keyCode, event);
  }

}
