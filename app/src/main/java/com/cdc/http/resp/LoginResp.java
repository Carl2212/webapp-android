package com.cdc.http.resp;


public class LoginResp {
	private HeaderResp header;
	
	private LoginResultResp result;

	public HeaderResp getHeader() {
		return header;
	}

	public void setHeader(HeaderResp header) {
		this.header = header;
	}

	public LoginResultResp getResult() {
		return result;
	}

	public void setResult(LoginResultResp result) {
		this.result = result;
	}

}
