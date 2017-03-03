package com.cdc.oa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.apache.cordova.CordovaActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.cdc.common.MyLog;
import com.cdc.model.DeviceInfo;
import com.cdc.util.ExitApplication;
import com.cdc.util.HttpUtil;
import com.cdc.util.NetworkDetector;
import com.cdc.util.ZipUtils;
import com.cdc.zqoa.R;
import com.google.gson.Gson;



/**
 * 系统入口
 * 
 * @author 
 * 
 */
public class MainActivity extends CordovaActivity {

	private static final int VERSION_RESULT = 0;
	private static final int PROGRESS_VALUE = 1;
	private static final int DOWN_SUCCESS = 2;
	private static final int VERSION_FAIL = 3;
	
	private String serverUrl;

	private String qybm;
	private String xmbm;
	private String clientType;
	
	private String ICCID;
	private String signedStr;
		
	public MainActivity(){
		
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(this.getClass().getName(), "----------> start main");
		ICCID=getIntent().getStringExtra("ICCID");
		signedStr=getIntent().getStringExtra("signedStr");
		
		
		this.serverUrl = this.getResources().getString(R.string.server_url);
        this.qybm = this.getResources().getString(R.string.qybm);
        this.xmbm = this.getResources().getString(R.string.xmbm);
        this.clientType = this.getResources().getString(R.string.client_type);
		
		super.onCreate(savedInstanceState);
//		super.setIntegerProperty("splashscreen", R.drawable.splash);
		setContentView(R.layout.activity_main);
		
		ExitApplication.getInstance().addActivity(this);
		
		boolean networkState = NetworkDetector.detect(this);  
		if (!networkState) {
			//((TextView)MainActivity.this.findViewById(R.id.updateStatus)).setText("网络未连接");
			new AlertDialog.Builder(this)
		    .setIcon(android.R.drawable.ic_dialog_alert)
		    .setTitle("提示")
		    .setMessage("网络不可用，请设置网络")
		    /*.setPositiveButton("下载", new DialogInterface.OnClickListener(){
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0); 
			    	while(true){
						Log.d("xxxxx", "xxxxxxxddddddddddd");
						if(NetworkDetector.detect(MainActivity.this)){
							new Thread(new VersionCheckThread()).start();
							break;
						}
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
			    }
			 })*/
		    .setNegativeButton("确定",  new DialogInterface.OnClickListener(){
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	 System.exit(0);
			    }
			 })
			 .setCancelable(false)
		    .show();
			
			
		    
		}else{
		
			//启动服务
			/*Intent newIntent = new Intent(this, TodoCountService.class); 
		    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //注意，必须添加这个标记，否则启动会失败 
		    this.startService(newIntent); */		
			new Thread(new VersionCheckThread()).start();
			//super.loadUrl("http://192.168.150.157/common_moa/moa2/build/production/Sme/index.html");
		}
		
	}
	
	private void clearPreCache(){
		String folder = this.getCacheDir().getParent();
//		File cacheFile = new File(folder+"/databases");
		File cacheFile1 = new File(folder+"/cache");
		File cacheFile2 = new File(folder+"/app_database");
		File cacheFile3 = new File(folder+"/app_webview");
		File cacheFile4 = new File(folder+"/databases");
//		clearCacheFolder(cacheFile, System.currentTimeMillis());
		clearCacheFolder(cacheFile1, System.currentTimeMillis());
		clearCacheFolder(cacheFile2, System.currentTimeMillis());
		clearCacheFolder(cacheFile3, System.currentTimeMillis());
		clearCacheFolder(cacheFile4, System.currentTimeMillis());
	}
	
	private int clearCacheFolder(File dir, long numDays) {         
	    int deletedFiles = 0;        
	    if (dir!= null && dir.isDirectory()) {            
	        try {               
	            for (File child:dir.listFiles()) {   
	                if (child.isDirectory()) {             
	                    deletedFiles += clearCacheFolder(child, numDays);         
	                }   
	                if (child.lastModified() < numDays) {    
	                    if (child.delete()) {                  
	                        deletedFiles++;          
	                    }   
	                }   
	            }            
	        } catch(Exception e) {      
	            e.printStackTrace();   
	        }    
	    }      
	    return deletedFiles;    
	}

	
	private void loadHtml(String version){
		
		String assetsPath = this.getDir("assets", 0).toString();
		File iosFile = new File(assetsPath + "/doc/lib/cordova-ios.js");
		if(iosFile.exists()){
			iosFile.delete();
		}
		
		if(null!=ICCID){
			writeCertFile();//将证书,ICCID,加密串写入js,以传个html页面
		}
		
		
		String path = this.getDir("assets", 0).toString();
		writeJSFile(version);
		loadUrl("file://"+path+"/doc/index.html");
	}
	
	private void writeCertFile(){
		String fileStrHead = "global_certMsg=";
		Gson gson = new Gson();  
		Map<String, String> certMap = new HashMap<String, String>();
		certMap.put("iccid", ICCID);
		certMap.put("signedstr", signedStr);
		String fileString = fileStrHead+ gson.toJson(certMap);
		MyLog.i("cert.js: " + fileString);
		String path = this.getDir("assets", 0).toString();
		
		writeFile(path+"/doc/cert.js", fileString);
		
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_exit: // 退出
				System.exit(0);
				// finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	@Override
	protected void onStart() {
		
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	public boolean deleteDirectory(String sPath) {  
	    //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
	    if (!sPath.endsWith(File.separator)) {  
	        sPath = sPath + File.separator;  
	    }  
	    File dirFile = new File(sPath);  
	    //如果dir对应的文件不存在，或者不是一个目录，则退出  
	    if (!dirFile.exists() || !dirFile.isDirectory()) {  
	        return false;  
	    }  
	    boolean flag = true;  
	    //删除文件夹下的所有文件(包括子目录)  
	    File[] files = dirFile.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        //删除子文件  
	        if (files[i].isFile()) {  
//	            flag = deleteFile(files[i].getAbsolutePath()); 
	        	files[i].delete();
	            if (!flag) break;  
	        } //删除子目录  
	        else {  
	            flag = deleteDirectory(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        }  
	    }  
	    if (!flag) return false;  
	    //删除当前目录  
	    if (dirFile.delete()) {  
	        return true;  
	    } else {  
	        return false;  
	    }  
	}
	
	private void writeFile(String fullFileName, String content){
		File f = new File(fullFileName);
		if(f.exists()){
			f.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			PrintWriter pw = new PrintWriter(fos);
			pw.println(content);
			pw.flush();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private String readVersion(String versionFile){
		FileReader fr = null;
		try {
			fr = new FileReader(versionFile);
			BufferedReader bf = new BufferedReader(fr);
			return bf.readLine();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        
         try{
        	 return super.onKeyDown(keyCode, event);
         }catch(Exception e){
        	 e.printStackTrace();
         }
         return false;
     }
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		 
        
        try{
       	 return super.onKeyUp(keyCode, event);
        }catch(Exception e){
       	 e.printStackTrace();
        }
        return false;
    }
	
	public void writeJSFile(String version)
	{
//		 	Build bd = new Build();  
//		    String model = bd.MODEL; 
			TelephonyManager tm = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
			String deviceName = android.os.Build.MODEL; //型号
			String deviceId = "";
			try{
				deviceId = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
			}catch(Exception e){
				
			}

			if(deviceId==null){
				deviceId = android.os.Build.SERIAL; //API LEVEL 9 or up
				
			}
			if(deviceId==null){
				deviceId = tm.getDeviceSoftwareVersion();
			}
			
			Log.e("DEVICE_ID ", deviceId + " ");
			String fileStringHead = "global_options = ";
			DeviceInfo info = new DeviceInfo();
				
			info.setUuid(deviceId);
			info.setUrl(serverUrl);
			info.setDevicename(deviceName);
			info.setQybm(qybm);
			info.setXmbm(xmbm);
			info.setPlatform("android_"+ 
					android.os.Build.VERSION.RELEASE); //android 版本
			info.setShellversion(getVersionName());
			info.setClientVersion(version);
			Gson gson = new Gson();  
			String fileString = fileStringHead+ gson.toJson(info);  
			String path = this.getDir("assets", 0).toString();
			version1Upgrade(path+"/doc/options.js");
			writeFile(path+"/doc/options.js", fileString);
			
	}
	
	/**
	 * version 1 无法彻底清除缓存，故version 1升级到其它版本时调用新的缓存清空方法
	 */
	private void version1Upgrade(String fullFilePath){
		File optionFile = new File(fullFilePath);
		if(optionFile.exists()){
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(optionFile));
				String options = br.readLine();
				if(options.indexOf("shellversion\":\"1\"")>-1){
					this.clearPreCache();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private int getVersionCode() 
	{
	        // 获取packagemanager的实例
	         PackageManager packageManager = getPackageManager();
	         // getPackageName()是你当前类的包名，0代表是获取版本信息
	         PackageInfo packInfo = null;
			try {
				packInfo = packageManager.getPackageInfo(getPackageName(),0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			int version =0;
			if(packInfo!=null)
				version = packInfo.versionCode;
	         return version;
	}
	
	private String getVersionName() 
	{
	        // 获取packagemanager的实例
	         PackageManager packageManager = getPackageManager();
	         // getPackageName()是你当前类的包名，0代表是获取版本信息
	         PackageInfo packInfo = null;
			try {
				packInfo = packageManager.getPackageInfo(getPackageName(),0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			String name ="";
			if(packInfo!=null)
				name = packInfo.versionName;
	         return name;
	}
	
	
	Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
//            Log.i(this.getClass().getName(), "-------------->message:" + msg.getData().getString("result"));   
            switch(msg.what){
	            case VERSION_RESULT:
	            	String result = msg.getData().getString("result");
	                versionProcess(result);
	                break;
	            case PROGRESS_VALUE:
	            	String progress = msg.getData().getString("progress");
	            	updateDownloadProgress(progress);
	                break;
	            case DOWN_SUCCESS:
	            	String docPath = msg.getData().getString("docPath");
	            	String clientVersion = msg.getData().getString("clientVersion");
	            	handleDownloadFile(docPath, clientVersion);
	                break;
	            case VERSION_FAIL:    
	                new AlertDialog.Builder(MainActivity.this)
				    .setIcon(android.R.drawable.ic_dialog_alert)
				    .setTitle("提示")
				    .setMessage("请求超时,请检查网络及vpn设置,或与管理员联系")
				    .setNegativeButton("确定",  new DialogInterface.OnClickListener(){
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    	 System.exit(0);
					    }
					 })
					 .setCancelable(false)
				    .show();
	                break;
            }
        	
             super.handleMessage(msg);   
        }   
   };
   
   private void versionProcess(String result){
	   
		try {
			//ClientVersionEntity client = new ClientVersionEntity().jsonString2Entity(result);
			JSONObject client = new JSONObject(result);
			JSONObject hearderObj = client.getJSONObject("header");
			JSONObject resObj = client.getJSONObject("result");
			String code = hearderObj.getString("code");
			
			Log.d(this.getClass().getName(), result);
			if("1".equals(code)){
				String shellVersion = null;
				if(resObj.has(clientType+"_version")){
					shellVersion=resObj.getString(clientType+"_version");
				}
				
				if(shellVersion!=null&&!shellVersion.equals(getVersionName())){ //安装包版本更新
					
					confirmShellUpgrade(resObj);
				}else{
					htmlVersionProcess(resObj);
				}
				
			}else{
				((TextView)MainActivity.this.findViewById(R.id.updateStatus)).setText(hearderObj.getString("describe"));
			}
			
			//  /data/data/com.cdc.pro.zgdxoa/app_assets
			
			
		} catch (Exception e) {
			e.printStackTrace();
			if(null==result){
				((TextView)MainActivity.this.findViewById(R.id.updateStatus)).setText("网络未连接或应用服务异常");
			}else{
				((TextView)MainActivity.this.findViewById(R.id.updateStatus)).setText("获取版本信息失败");
			}
			
		}finally{
			
		}
   }
   
   private void htmlVersionProcess(JSONObject resObj) throws JSONException{
	   String assetsPath = MainActivity.this.getDir("assets", 0).toString();
	   File docDir = new File(assetsPath+"/doc");
	   
	   String version = resObj.getString("clientVersion");
	   String downUrl = serverUrl + "fileupdown/downhtml?clientName="+ resObj.getString("clientName");
	   String updateStrLabel = "client_update_msg";
	   
	   if(clientType.toLowerCase().indexOf("pad")>-1){
		   version = resObj.getString("padVersion");
		   downUrl = serverUrl + "fileupdown/downhtml?clientName="+ resObj.getString("padClientName");
		   updateStrLabel= "pad_update_msg";
	   }
	   
		if(!docDir.exists()){
			clearPreCache();
			new Thread(new HtmlDownloadThread(downUrl, assetsPath, version)).start();
			
		}else{
			String oldVersion = readVersion(assetsPath + "/version");
			if(oldVersion==null || !oldVersion.equals(version)){
				String updateMsg = "";
				if(resObj.has(updateStrLabel)){
					updateMsg = resObj.getString(updateStrLabel);
				}
				confirmUpdate(downUrl, assetsPath, version, updateMsg);
//				clearPreCache();
//				new Thread(new HtmlDownloadThread(downUrl, assetsPath, version)).start();
			}else{
//					startActivity(new Intent(MainActivity.this, Phonegap.class));
				loadHtml(version); //旧版本
			}
		}
   }
   
   private void confirmShellUpgrade(final JSONObject resObj) throws JSONException{
	   final String shellDownUrl = clientType.toLowerCase().indexOf("pad")>-1?resObj.getString("androidpad_down_url"):resObj.getString("android_down_url");
	   String shellUpdateMsg = clientType.toLowerCase().indexOf("pad")>-1?resObj.getString("androidpad_update_msg"):resObj.getString("android_update_msg");
	   
	   new AlertDialog.Builder(this)
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .setTitle("应用更新提示")
	    .setMessage(shellUpdateMsg+"\n")
	    .setPositiveButton("下载安装更新", new DialogInterface.OnClickListener(){
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	//调用浏览器下载
		    	Intent intent = new Intent();        
		        intent.setAction("android.intent.action.VIEW");    
		        Uri content_url = Uri.parse(shellDownUrl);   
		        intent.setData(content_url);  
		        startActivity(intent); 
		        System.exit(0);
		    }
		 })
	    .setNegativeButton("取消",  new DialogInterface.OnClickListener(){
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	System.exit(0);
		    	/*try {
					htmlVersionProcess(resObj);
				} catch (JSONException e) {
					e.printStackTrace();
					((TextView)MainActivity.this.findViewById(R.id.updateStatus)).setText("获取版本信息失败");
				}  */  
		    }
		 })
		 .setCancelable(false)
	    .show();

	}
   
   private void confirmUpdate(final String downUrl,final String assetsPath,final String version,String updateMsg){
		new AlertDialog.Builder(this)
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .setTitle("资源包更新提示")
	    .setMessage(updateMsg+"\n")
	    .setPositiveButton("更新", new DialogInterface.OnClickListener(){
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	clearPreCache();
				new Thread(new HtmlDownloadThread(downUrl, assetsPath, version)).start();	      
		    }
		 })
	    .setNegativeButton("暂不更新",  new DialogInterface.OnClickListener(){
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	System.exit(0);
		    	/*String oldVersion = readVersion(assetsPath + "/version");
		    	loadHtml(oldVersion); //旧版本     
*/		    }
		 })
		 .setCancelable(false)
	    .show();

	}
   
   class VersionCheckThread implements Runnable {   
       public void run() {  
    	   List<NameValuePair> params = new ArrayList<NameValuePair>();
	   		params.add(new BasicNameValuePair("qybm", qybm));
	   		params.add(new BasicNameValuePair("xmbm", xmbm));
   		
	   		String result=null;
			try {
				result = HttpUtil.executePost(serverUrl + "wap/aconnect" , params);
				 Message message = new Message();
		            message.what=VERSION_RESULT;
		            Bundle b = new Bundle();
		            b.putString("result", result);
		            message.setData(b);  
		            myHandler.sendMessage(message);   
			} catch (Exception e) {
				
				e.printStackTrace();
				 Message message = new Message();
				 message.what=VERSION_FAIL;
	            
	            myHandler.sendMessage(message);
	            
				
			}
               
           
//            Thread.currentThread().interrupt();  
       }   
  }  
	 

   class HtmlDownloadThread implements Runnable {
	    private String url;
	    private String docPath;
		private String clientVersion;
		boolean downSuccess = false;
		public HtmlDownloadThread(String url,String docPath,String clientVersion){
			this.url = url;
			this.docPath = docPath;
			this.clientVersion = clientVersion;
		}
       public void run() {  
    	   try {  
               //连接地址  
               URL u = new URL(this.url);  
               HttpURLConnection c = (HttpURLConnection) u.openConnection();  
               c.setRequestMethod("POST");  
               c.setDoOutput(true);  
               c.connect();  
                 
               //计算文件长度  
               int lenghtOfFile = c.getContentLength();  
                 
                 
               FileOutputStream f = new FileOutputStream(new File(this.docPath+"/doc.zip"));  
           
               InputStream in = c.getInputStream();  
 
              //下载的代码  
               byte[] buffer = new byte[1024];  
               int len1 = 0;  
               long total = 0;  
                 
               while ((len1 = in.read(buffer)) > 0) {  
                   total += len1; //total = total + len1 
                    Message message = new Message();
      	             message.what=PROGRESS_VALUE;
      	             Bundle b = new Bundle();
                    b.putString("progress","" + (int)((total*100)/lenghtOfFile));
      	             message.setData(b);  
      	             myHandler.sendMessage(message);  
                    f.write(buffer, 0, len1);  
               }  
               f.close();  
               Message message = new Message();
 	            message.what=DOWN_SUCCESS;
 	            Bundle b = new Bundle();
               b.putString("docPath",this.docPath);
               b.putString("clientVersion",this.clientVersion);
 	            message.setData(b);  
 	            myHandler.sendMessage(message); 
           } catch (Exception e) {  
           	((TextView)MainActivity.this.findViewById(R.id.updateStatus)).setText("下载失败");
               e.printStackTrace();
           }   
       }   
   }  
   
   private void updateDownloadProgress(String str){
	   ((TextView)MainActivity.this.findViewById(R.id.updateStatus)).setText("下载资源  "+str+"%");
   }
   
   private void handleDownloadFile(String docPath, String clientVersion){
	   InputStream is;
		try {
			deleteDirectory(docPath+"/doc");//如果文件夹存在，先删除
			
			is = new FileInputStream(docPath+"/doc.zip");
			ZipInputStream zis = new ZipInputStream(is);
			
			
			ZipUtils.fileUnZip(zis, new File(docPath+"/doc"));
			if(zis!=null){
				zis.close();
			}
			writeFile(docPath + "/version", clientVersion);
//			startActivity(new Intent(MainActivity.this, Phonegap.class));
			loadHtml(clientVersion);
		} catch (Exception e) {
			((TextView)MainActivity.this.findViewById(R.id.updateStatus)).setText("资源解压失败");
			e.printStackTrace();
		}
   }
   
   @Override
   public void onDestroy(){
	   clearTmpFiles();
	   super.onDestroy();
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
   
}
