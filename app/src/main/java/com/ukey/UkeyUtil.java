package com.ukey;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




import com.cdc.common.Base64;
import com.cdc.common.Constants;
import com.gsta.ukeyesurfing.aidl.GetCertOprResult;
import com.gsta.ukeyesurfing.aidl.IUkeyService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

/**
 * 
 * 类名: UkeyUtil</br> 包名：com.ukey </br> 描述: 翼支付U盾验证</br> 发布版本号：</br> 开发人员：
 * huangzy</br> 创建时间： 2015-1-22
 */
public class UkeyUtil {

  /** 上下文 */
  private Context context;
  /** aidl服务 */
  private ServiceConnection ukeySc = null;
  private IUkeyService mBind = null;
  private String spUserId_ICCID = "";
  private int absPkgHash;
  /** 提示框 */
  private ProgressDialog progressDlg;
  private Handler serviceHandler = null;
  /** 证书 */
  private String certHexStr;
  /** 证书 */
  private String certBase64Str;
  /** 签名 */
  private String signString;
  /** 后台随机标识 */
  private String getRanDom = null;
  /** 帐号密码输入框的View */
  private View view;
  /** 用户帐号 */
  private String userName;
  /** 用户密码 */
  private String passWord;
  
  private String href;
  
  private String target;

  private int runType = 0;
  
  
  public UkeyUtil(Context context, View view,String href,String target) {
    this.context = context;
    this.view = view;
    this.href = href;
    this.target = target;
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    spUserId_ICCID = tm.getSimSerialNumber(); // 取出ICCID.
    System.out.println("spUserId_ICCID>>" + spUserId_ICCID);
    try {
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
      Intent intent = new Intent("com.gsta.ukeyesurfing.service.UkeyService");
      context.bindService(intent, ukeySc, Context.BIND_AUTO_CREATE);
      serviceHandler = new Handler(mHander);
    } catch (Throwable e) {
      e.printStackTrace();
      ukeySc = null;
      Message message = new Message();
      message.what = -1;
      message.obj = -102;
      serviceHandler.sendMessage(message);
    }
  }

  public void onPause() {
    if (null != ukeySc) {
      context.unbindService(ukeySc);
    }
  }

  /**
   * 描述： 设置 userName</br>
   * 
   * @param userName
   */

  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * 描述： 设置 passWord</br>
   * 
   * @param passWord
   */

  public void setPassWord(String passWord) {
    this.passWord = passWord;
  }

  /**
   * 
   * 方法名: onActivityResult</br> 详述: 调用U盾插件后的回调</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-22</br>
   * 
   * @param requestCode
   * @param resultCode
   * @param data
   */
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    System.out.println("onActivityResult>>>>>>onActivityResult");
    if (requestCode == absPkgHash + 1) {
      if(data != null){
        int oprResult = data.getIntExtra("OPR_RESULT", -1);
        System.out.println("oprResult>>>" + oprResult);
        if (checkOprResult(oprResult)) {
          byte[] signedData = data.getByteArrayExtra("SIGNED_DATA");
          signString = Base64.encode(signedData);
          System.out.println("签名：" + signString);
          checkSign();
        }
      }else{
        serviceHandler.sendEmptyMessage(9);
      }
    } else if (requestCode == absPkgHash + 3) {
      if(data != null){
        // 绑定业务
        int oprResult = data.getIntExtra("OPR_RESULT", -1);
        System.out.println("oprResult>>>" + oprResult);
        if (checkOprResult(oprResult)) {
          startUkeyAction(2);
        }
      }else{
        serviceHandler.sendEmptyMessage(6);
      }
    } else if (requestCode == absPkgHash + 7) {
        if(data != null){
          // 绑定业务
          int oprResult = data.getIntExtra("OPR_RESULT", -1);
          if(oprResult == 4){
        	  showDialog("成功提交解锁请求");
          }else{
        	 if(oprResult == 1){
        		 showDialog("提交解锁请求失败,U 盾环境不具备(手机无 UIM 卡、 UIM 卡不支持 U 盾、UIM 卡上未安装 U 盾应用、未开通或未设置 pin 码 等)");
        	 }else if(oprResult == 2){
        		 showDialog("提交解锁请求失败,U 盾插件版本需更新");
        	 }else if(oprResult == 3){
        		 showDialog("提交解锁请求失败,U 盾未锁定");
        	 }else if(oprResult == 6){
        		 showDialog("提交解锁请求失败,无操作权限");
        	 }else{
        		 showDialog("提交解锁请求失败,其他原因提交失败(服务端异 常、网络频繁闪断等等)");
        	 }        	  	  
          }
        }
    }
  }

  private boolean checkOprResult(int oprResult) {
    if (oprResult == 0) {
      return true;
    }
    dismiss();
    switch (oprResult) {
      case 1:
        System.out.println("1－U盾环境不具备（手机无UIM卡、UIM卡不支持U盾、UIM卡上未安装U盾应用、未开通或未设置pin码等）");
//        Toast.makeText(context, "1－U盾环境不具备（手机无UIM卡、UIM卡不支持U盾、UIM卡上未安装U盾应用、未开通或未设置pin码等）", Toast.LENGTH_LONG).show();
        showDialog("U盾环境不具备（手机无UIM卡、UIM卡不支持U盾、UIM卡上未安装U盾应用、未开通或未设置pin码等）");
        break;
      case 2:
        System.out.println("2－U盾插件版本需更新");
//        Toast.makeText(context, "2－U盾插件版本需更新", Toast.LENGTH_LONG).show();
        showDialog("U盾插件版本需更新");
        break;
      case 3:
        System.out.println("3－U盾已锁定 ");
//        Toast.makeText(context, "3－U盾已锁定 ", Toast.LENGTH_LONG).show();
//        showDialog("U盾已锁定");
        Toast.makeText(context, "U盾已锁定 ", Toast.LENGTH_LONG).show();
        pinunlock();
        break;
      case 4:
        System.out.println("4－本应用未关联证书 ");
//        Toast.makeText(context, "4－本应用未关联证书 ", Toast.LENGTH_LONG).show();
        showDialog("本应用未关联证书 ");
        break;
      case 5:
        System.out.println("5－本应用关联证书已过期");
//        Toast.makeText(context, "5－本应用关联证书已过期", Toast.LENGTH_LONG).show();
        showDialog("本应用关联证书已过期");
        break;
      case 6:
        System.out.println("6－无操作权限");
//        Toast.makeText(context, "6－无操作权限", Toast.LENGTH_LONG).show();
        showDialog("无操作权限");
        break;
      case 99:
        System.out.println("99 其它原因（服务端异常、网络频繁闪断等等");
//        Toast.makeText(context, "99其它原因（服务端异常、网络频繁闪断等等", Toast.LENGTH_LONG).show();
        showDialog("其它原因（服务端异常、网络频繁闪断等等");
        break;
      default:
        System.out.println("签名失败（OPR_RESULT不为0），为null");
//        Toast.makeText(context, "签名失败（OPR_RESULT不为0），为null", Toast.LENGTH_LONG).show();
        showDialog("签名失败（OPR_RESULT不为0），为null");
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
    new AlertDialog.Builder(context).setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        System.exit(0);
      }
    }).setMessage(msg).setCancelable(false).create().show();
  }

  /**
   * 
   * 方法名: </br>
   * 详述: </br>
   * 开发人员：huangzy</br>
   * 创建时间：2015-1-28</br>
   * @param msg
   */
  private void showReStartDialog(String msg,String btn1,String btn2){
    new AlertDialog.Builder(context).setTitle("提示")
    .setPositiveButton(btn1, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        startUkeyAction(0);
      }
    }).setNegativeButton(btn2, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        System.exit(0);
      }
    }).setMessage(msg).setCancelable(false).create().show();
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

  Handler.Callback mHander = new Handler.Callback() {

    @Override
    public boolean handleMessage(Message msg) {
      switch (msg.what) {
        case 0:// "验证帐号密码有效性！"
          progressDlg.setMessage("验证帐号密码有效性！");
          view.setVisibility(View.INVISIBLE);
          checkUserNamePassWord();
          break;
        case 1:// 验证帐号密码成功//没绑定
          if (runType == 0) {
//            progressDlg.setMessage("正在获取客户端登录标识！");
//            getRandomNumber();
            serviceHandler.sendEmptyMessage(10);
          } else {
            progressDlg.setMessage("正在绑定业务中，请稍候！");
            relatedBusiness();
          }
          break;
        case 101:// 验证帐号密码成功//有绑定
          if (runType == 0) {
            progressDlg.setMessage("正在获取客户端登录标识！");
            getRandomNumber();
          } else {
            dismiss();
            Toast.makeText(context, "此帐号已经绑定了其他的U盾!", Toast.LENGTH_LONG).show();
            view.setVisibility(View.VISIBLE);
          }
          break;
        case 2:// 验证帐号密码失败
          dismiss();
          Toast.makeText(context, "帐号密码验证失败，请查看是否输入有误！", Toast.LENGTH_LONG).show();
          view.setVisibility(View.VISIBLE);
          break;
        case 3:// 获取随机数成功
          progressDlg.setMessage("正在验证签名是否合法！");
          getSign();// 获取签名
          break;
        case 4:// 获取随机数失败
          dismiss();
          showReStartDialog("获取客户端登录标识，是否重新获取？","重新获取","退出应用");
          break;
        case 5:// 绑定业务成功
          if(runType == 1){
            startUkeyAction(0);
          }else{
            progressDlg.setMessage("正在获取客户端登录标识！");
            getRandomNumber();
          }
          break;
        case 6:// 绑定业务失败
//          showReStartDialog("绑定业务失败，是否重新绑定？","重新绑定","退出应用");
          new AlertDialog.Builder(context).setTitle("提示")
          .setPositiveButton("重新绑定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
              uShieldBind();
            }
          }).setNegativeButton("退出应用", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
              System.exit(0);
            }
          }).setMessage("绑定业务失败，是否重新绑定？").setCancelable(false).create().show();
          break;
        case 7:// 绑定业务成功
          dismiss();
          new AlertDialog.Builder(context).setTitle("提示")
              .setPositiveButton("绑定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                  view.setVisibility(View.VISIBLE);
                }
              }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                  System.exit(0);
                }
              }).setMessage("检测到翼支付U盾，是否绑定业务？").setCancelable(false).create().show();
          break;
        case 8:// 签名认证失败
          dismiss();
          /*ControlMethod method = new ControlMethod(context);
          StringBuffer buffer = new StringBuffer();
          buffer.append(href);
          buffer.append("?");
          buffer.append("username="+userName);
          buffer.append("&password="+passWord);
          method.setHandler(loginHandler);
          method.actionListener("mapScript://submit()", buffer.toString(), target);*/
          //TODO
          break;
        case 9:// 签名认证失败
          dismiss();
          showReStartDialog("签名认证失败，是否重新认证？","重新认证","退出应用");
          break;
        case 10://
          progressDlg.setMessage("正在绑定业务中，请稍候！");
          uShieldBind();
          break;
        case 11://
          dismiss();
          Toast.makeText(context, "U盾绑定帐号不正确", Toast.LENGTH_LONG).show();
          view.setVisibility(View.VISIBLE);
          break;
        default:
          dismiss();
          if ((Integer) msg.obj == -100) {
            showDialog("没有检测到U盾插件，请先安装后再启动本应用！");
          } else if ((Integer) msg.obj == -101) {
            showDialog("没有检测到U盾，请检测是否已经插入！");
          } else if ((Integer) msg.obj == -102) {
            showDialog("U盾调用初始化失败，请重新启动应用！");
          } else {
            Toast.makeText(context, MiscUtils.getUkeyZhengShuError((Integer) msg.obj), Toast.LENGTH_LONG).show();
            if((Integer) msg.obj == 3){
                pinunlock();
            }
          }
          break;
      }
      return true;
    }
  };
  
  
  public Handler loginHandler = new Handler() {
    @Override
    public void handleMessage(final Message msg) {
      switch (msg.what) {
        case Constants.PROGRESS_MESSAGE_NORMAL: // 普通信息消息对话框
          showReStartDialog("获取登录数据失败，是否重新登录？","重新登录","退出应用");
          break;
        case Constants.HANDLERDOWNSUCCESS:
         /* ResponseDataModel model = (ResponseDataModel) msg.obj;
          String responseXml = model.mapxml;
          String target = model.target;
          String url = model.command;
          String message = model.getMessage();
          System.out
              .println("===target====" + target + "====responseXml===" + responseXml + "====message===" + message);
          if (message == null || message.equals("")) {
            ((GdcnDisplayActicity) context).openWindos(target, responseXml, url, false);// 打开界面
          } else {
            showReStartDialog("获取登录数据失败，是否重新登录？","重新登录","退出应用");
          }*/
        	//TODO
        	System.out.println("TODO ...........");
          break;
      }
    }
  };

  /**
   * 
   * 方法名: startUkeyAction</br> 详述: 启动Ukey</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-27</br>
   * 
   * @param type
   *          0初始化，1关联业务
   */
  public void startUkeyAction(final int runType) {
    if ("".equals(spUserId_ICCID)) {
      Message message = new Message();
      message.what = -1;
      message.obj = -101;
      serviceHandler.sendMessage(message);
      return;
    }
    if(ukeySc == null){
      return;
    }
    
    
    this.runType = runType;
    if (progressDlg == null) {
      progressDlg = new ProgressDialog(context);
      progressDlg.setMessage(runType == 0 ? "检测U盾情况！" : "验证帐号密码有效性！");
      progressDlg.setCancelable(false);
      progressDlg.show();
    }
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        if (runType == 0) {
          // 获取我的证书
          int resultCode = getCertificate();
          if (resultCode == 0) {// 已经关联了业务，执行登录操作
            // 验证帐号密码有效性
            serviceHandler.sendEmptyMessage(0);
          } else if (resultCode == 4) {// 没有关联业务
            serviceHandler.sendEmptyMessage(7);
          } else {// 有问题
            Message message = new Message();
            message.what = -1;
            message.obj = resultCode;
            serviceHandler.sendMessage(message);
          }
        } else if(runType == 1){
          // 验证帐号密码有效性
          int resultCode = getCertificate();
          if(resultCode == 0){
            UkeyUtil.this.runType = 0;
          }
          serviceHandler.sendEmptyMessage(0);
        }else if(runType == 2){
          int resultCode = getCertificate();
          if(resultCode == 0){
            serviceHandler.sendEmptyMessage(10);
          } else if (resultCode == 4) {// 没有关联业务
            serviceHandler.sendEmptyMessage(7);
          } else {// 有问题
            Message message = new Message();
            message.what = -1;
            message.obj = resultCode;
            serviceHandler.sendMessage(message);
          }
        }
      }
    }).start();
  }

  /**
   * 
   * 方法名: getAbsPkgHash</br> 详述: 获取请求Code</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-22</br>
   * 
   * @param intent
   */
  private void startActivityForResult(Intent intent, int type) {
    PackageManager pm = context.getPackageManager();
    List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
    boolean isIntentSafe = activities.size() > 0;
    if (isIntentSafe) {
      Signature[] signs = null;
      try {
        String pkgName = context.getApplicationContext().getPackageName();
        signs = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES).signatures;
      } catch (NameNotFoundException e) {
        e.printStackTrace();
      }
      int pkgHash = -1;
      if (signs != null) {
        pkgHash = Math.abs(signs[0].hashCode());
        absPkgHash = pkgHash;
      }
      ((Activity) context).startActivityForResult(intent, pkgHash + type);
    } else {

    }
  }

  /**
   * 
   * 方法名: relatedBusiness</br> 详述: U盾关联业务</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-22</br>
   */
  private void relatedBusiness() {
    Intent intent = new Intent("com.gsta.ukeyesurfing.needpinverify");
    Bundle extras = new Bundle();
    extras.putInt("OPR_TYPE", 3);// 1:SIGN; 2:UPDATE CERTIFICATE;
    extras.putString("SP_USERID", spUserId_ICCID);
    intent.putExtras(extras);
    startActivityForResult(intent, 3);
  }
  
  /**
   * 
   * 方法名: relatedBusiness</br> 详述: U盾关联业务</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-22</br>
   */
  private void pinunlock() {
    Intent intent = new Intent("com.gsta.ukeyesurfing.pinunlock");
    startActivityForResult(intent, 7);
  }

  /**
   * 
   * 方法名: getSign</br> 详述: 获取签名</br> 开发人员：huangzy</br> 创建时间：2015-1-22</br>
   */
  private void getSign() {
    Intent intent = new Intent("com.gsta.ukeyesurfing.needpinverify");
    Bundle extras = new Bundle();
    extras.putInt("OPR_TYPE", 1);// 1:SIGN; 2:UPDATE CERTIFICATE;
    extras.putByteArray("TO_BE_SIGNED_DATA", getRanDom.getBytes());
    extras.putString("HASH_ALGORITHM", "SHA-256");
    extras.putString("SP_USERID", spUserId_ICCID);
    extras.putString("TITLE", "签名");
    intent.putExtras(extras);
    startActivityForResult(intent, 1);
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
        if(resultCode == 0){
          certHexStr = certOprResult.getCertHexStr();
          byte[] certBytes = MiscUtils.hexStrToBytes(certHexStr); 
          certBase64Str = Base64.encode(certBytes);
        }
        return resultCode;
      } catch (Throwable e) {
        e.printStackTrace();
        return -101;
      }
    } else {
      // 没有找到翼支付Ukey插件.....
      return -100;
    }
  }

  /**
   * 
   * 方法名: checkUserNamePassWord</br> 详述: 验证帐号密码</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-22</br>
   */
  public void checkUserNamePassWord() {
    HashMap<String, String> datas = new HashMap<String, String>();
    datas.put("username", userName);
    datas.put("password", passWord);
    MiscUtils.loadDatas(context,new Handler(new Handler.Callback() {

      @Override
      public boolean handleMessage(Message msg) {
        switch (msg.what) {
          case Constants.PROGRESS_MESSAGE_NORMAL: // 普通信息消息对话框
            showMdpError(msg);
            break;
          case -99:
            String xml = (String) msg.obj;
            String code = MiscUtils.getTagValue(xml, "code", "0");
            System.out.println("code>>>" + code);
            String message = MiscUtils.getTagValue(xml, "message", "");
            System.out.println("message>>>" + message);
            if (code.equals("1")) {
              // 1 代表验证成功且有绑定
              serviceHandler.sendEmptyMessage(101);
            } else if(code.equals("2")){
              //2 代表验证成功未有绑定
              serviceHandler.sendEmptyMessage(1);
            } else {
              // 验证帐号密码失败
              serviceHandler.sendEmptyMessage(2);
            }
            break;
        }
        return false;
      }
    }), "zqgxq_verifyAccount", datas);
  }

  /**
   * 
   * 方法名: uShieldBind</br> 详述: 绑定业务</br> 开发人员：huangzy</br> 创建时间：2015-1-27</br>
   */
  public void uShieldBind() {
    HashMap<String, String> datas = new HashMap<String, String>();
    datas.put("username", userName);
    datas.put("iccid", spUserId_ICCID);
    datas.put("digitCert", certHexStr);
    MiscUtils.loadDatas(context,new Handler(new Handler.Callback() {

      @Override
      public boolean handleMessage(Message msg) {

        switch (msg.what) {
          case Constants.PROGRESS_MESSAGE_NORMAL: // 普通信息消息对话框
            showMdpError(msg);
            break;
          case -99:
            String xml = (String) msg.obj;
            String code = MiscUtils.getTagValue(xml, "code", "0");
            System.out.println("code>>>" + code);
            String message = MiscUtils.getTagValue(xml, "message", "");
            System.out.println("message>>>" + message);
            if (code.equals("1")) {
              // 绑定业务成功
              serviceHandler.sendEmptyMessage(5);
            } else {
              // 绑定业务失败
              if(message.indexOf("已经绑定到其他账号") != -1){
                dismiss();
                new AlertDialog.Builder(context).setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    view.setVisibility(View.VISIBLE);
                  }
                }).setMessage(message +" 请联系管理员！").setCancelable(false).create().show();
              }else{
                serviceHandler.sendEmptyMessage(6);
              } 
            }
            break;
        }
        return false;
      }
    }), "zqgxq_uShieldBind", datas);
  }

  /**
   * 
   * 方法名: getRandomNumber</br> 详述: 获取随机数</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-22</br>
   */
  public void getRandomNumber() {
    HashMap<String, String> datas = new HashMap<String, String>();
    datas.put("username", userName);
    datas.put("iccid", spUserId_ICCID);
    datas.put("digitCert", certHexStr);
    MiscUtils.loadDatas(context,new Handler(new Handler.Callback() {

      @Override
      public boolean handleMessage(Message msg) {
        switch (msg.what) {
          case Constants.PROGRESS_MESSAGE_NORMAL: // 普通信息消息对话框
            showMdpError(msg);
            break;
          case -99:
            String xml = (String) msg.obj;
            String code = MiscUtils.getTagValue(xml, "code", "0");
            System.out.println("code>>>" + code);
            String message = MiscUtils.getTagValue(xml, "message", "");
            System.out.println("message>>>" + message);
            if (code.equals("1")) {
              // 获取随机数成功
              getRanDom = message;
              serviceHandler.sendEmptyMessage(3);
            } else {
              if(message.indexOf("U盾账号绑定不正确") != -1){
                serviceHandler.sendEmptyMessage(11);
              }else{
                // 获取随机数失败
                serviceHandler.sendEmptyMessage(4);
              }
            }
            break;
        }
        return false;
      }
    }), "zqgxq_genRandom", datas);
  }

  /**
   * 
   * 方法名: checkSign</br> 详述: 验证签名</br> 开发人员：huangzy</br> 创建时间：2015-1-27</br>
   */
  public void checkSign() {
    HashMap<String, String> datas = new HashMap<String, String>();
    datas.put("random", getRanDom);
    datas.put("b64sign", signString);
    datas.put("b64cert", certBase64Str);
    MiscUtils.loadDatas(context,new Handler(new Handler.Callback() {

      @Override
      public boolean handleMessage(Message msg) {
        switch (msg.what) {
          case Constants.PROGRESS_MESSAGE_NORMAL: // 普通信息消息对话框
            showMdpError(msg);
            break;
          case -99:
            String xml = (String) msg.obj;
            String code = MiscUtils.getTagValue(xml, "code", "0");
            System.out.println("code>>>" + code);
            String message = MiscUtils.getTagValue(xml, "message", "");
            System.out.println("message>>>" + message);
            if (code.equals("1")) {
              // 验证证书成功
              System.out.println("验证证书成功");
              serviceHandler.sendEmptyMessage(8);
            } else {
              // 验证证书失败
              System.out.println("验证证书失败");
              serviceHandler.sendEmptyMessage(9);
            }
            break;
        }
        return false;
      }
    }), "zqgxq_verifySign", datas);
  }

  /**
   * 
   * 方法名: goLogin</br> 详述: 执行登录操作</br> 开发人员：huangzy</br> 创建时间：2015-1-22</br>
   */
  public void goLogin() {
    progressDlg.setMessage("正在登录中...");
  }

  /**
   * 
   * 方法名: showMdpError</br> 详述: Mdp请求失败</br> 开发人员：huangzy</br>
   * 创建时间：2015-1-27</br>
   * 
   * @param msg
   */
  private void showMdpError(Message msg) {
    dismiss();
    /*if (msg.obj instanceof String) {
      showReStartDialog(msg.obj.toString(),"重新登录","退出应用");
    } else if (msg.obj instanceof ResponseSystemTagModel) {
      final ResponseSystemTagModel model = (ResponseSystemTagModel) msg.obj;
      showReStartDialog(model.getMessage(),"重新登录","退出应用");
    }*/
  }

}
