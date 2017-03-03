package com.cdc.http.resp;

import java.io.Serializable;

public class ResultResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String count;
	private String success;
	private String message;
	private String submit;
	private String isNeedRount;
	private String sign;
	private String sessionid;
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSubmit() {
		return submit;
	}
	public void setSubmit(String submit) {
		this.submit = submit;
	}
	public String getIsNeedRount() {
		return isNeedRount;
	}
	public void setIsNeedRount(String isNeedRount) {
		this.isNeedRount = isNeedRount;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSessionid() {
		return sessionid;
	}
	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}
	
}
