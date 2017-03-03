package com.cdc.http.resp;

import java.util.List;

public class LoginResultResp extends ResultResp{
	private List<String> userlist;
	private String userInfo;
	
	public String getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	public List<String> getUserlist() {
		return userlist;
	}
	public void setUserlist(List<String> userlist) {
		this.userlist = userlist;
	}
	
}
