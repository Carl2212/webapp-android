package com.cdc.http.resp;

import java.io.Serializable;
import java.util.List;

public class MenusInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String action;
	private String defaultShow;
	private String iconName;
	private String pageNum;
	private String seq;
	private String title;
	private List<BizModulesResp> bizModules;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDefaultShow() {
		return defaultShow;
	}
	public void setDefaultShow(String defaultShow) {
		this.defaultShow = defaultShow;
	}
	public String getIconName() {
		return iconName;
	}
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
	public String getPageNum() {
		return pageNum;
	}
	public void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<BizModulesResp> getBizModules() {
		return bizModules;
	}
	public void setBizModules(List<BizModulesResp> bizModules) {
		this.bizModules = bizModules;
	}
}
