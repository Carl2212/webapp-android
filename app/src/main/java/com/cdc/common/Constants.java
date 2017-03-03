package com.cdc.common;

public class Constants {
	public static int todoCount = 0;
	public static int toreadCount = 0;
	public static int mailCount = 0;
	public final static int PROGRESS_MESSAGE_NORMAL = 1;
	
	  /**
	   * // 下载成功
	   */
	public final static int HANDLERDOWNSUCCESS = 12;
	public final static int GETCERTREQUESTCODE = 1;
	public final static int GETUSERNAMEREQUESTCODE = 2;
	public final static int UKBINDCODE = 3;
	public final static int GETCERTRAFTERBIND = 4;
	public final static int UKUNBINDCODE = 5;
	public final static int UKSIGNREQUEST = 6;
	
	public static final class Color{
		public static final String DARKBLUE = "darkblue";
		public static final String GREEN = "green";
		public static final String LIGHTBLUE = "lightblue";
		public static final String PINK = "pink";
		public static final String RED = "red";
		public static final String YELLOW = "yellow";
	}
	
	public static final String PLATFORM = "android";
	
	public static final String HTTP = "http://";
	//public static final String REQUEST_URL = "http://192.168.200.67:61000/ms/wap/execute";
	
	public static final String GET_EXECUTE_URL = "/wap/execute";
	
	public static final String GET_PLATFORM_URL = "/ms/wap/iconnect";
	public static final String GET_MENU_URL = "/ms/wap/chkservice";
	public static final String GET_RESOUCE = "/ms/wap/resources/";
	
	//获取菜单url
	//public static final String MENU_URL = "http://192.168.200.67:61000/ms/wap/chkservice?devicename=&platform=&uuid=&model=&version=&width=1600&height=900&qybm=CDC&xmbm=TYOA";
	
	public static final int CONN_TIMEOUT = 5000;
	public static final int TIMEOUT = 30000;
	public static final int RESULT_SUCCESS = 1;// 获取数据成功
	public static final int RESULT_FAIL = -1;// 获取数据失败
	public static final int RESULT_NO_NETWORK = -2;// 没有网络连接
	public static final int RESULT_NO_DATA = -3;// 获取数据成功，但是没有数据
	public static final String LOG_TAG = "cdcoa";// log的tag
	public static final String PAGESIZE = "10";//分页显示数量 
	
	public static final boolean SHOW_LOG = true;// 是否显示log
	
	public static final String key_full_name = "key_full_name";
	public static final String key_group_name = "key_group_name";
	public static final String key_office_phone = "key_office_phone";
	public static final String key_email = "key_email";
	
	public static final String key_user_id="key_user_id";
	
	public static final String key_user_name="key_user_name";
	
	public static final String key_user_psw="key_user_psw";
	
	public static final String key_token="key_token";
	
	public static final String key_nickname="key_nickname";
	
	public static final String key_account="key_account";
	
	public static final String key_re_pwd = "key_re_pwd";
	
	public static final String key_auto_login = "key_auto_login";
	
	public static final String key_session_id = "key_session_id";
	
	public static final String key_login_pwd="key_login_pwd";
	public static final String key_mobile="key_mobile";
	public static final String LOG_12345 = "log_12345";
	public static final String key_server_address_info = "key_server_address_info";
	public static final String key_style_setting_info = "key_style_setting_info";
	public static final String key_qybm = "key_qybm";
	public static final String key_xmbm = "key_xmbm";
	
	
	public static final String addressBook = "addressBook";
	
	public static final String memo = "memo";
	
	public static final String leaderSchedule = "leaderSchedule";
	public static final String schedule = "schedule";
	
	public static final String notice = "notice";
	
	public static final String key_menu = "key_menu";
	
	public static final String key_my_schedule_item = "key_my_schedule_item";
	
	public static final String key_address_user_info = "key_address_user_info";
	
	public static final String key_mail_info = "key_mail_info";
	public static final String key_mail_detail_info = "key_mail_detail_info";
	public static final String key_notice_info = "key_notice_info";
	public static final String key_menu_info = "key_menu_info";
	public static final String key_node_info = "key_node_info";
	public static final String key_group_user_info = "key_group_user_info";
	public static final String key_chawen_info = "key_chawen_info";
	public static final String key_login_menu_info = "key_login_menu_info";
 
}
