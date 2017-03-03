package com.cdc.http.resp;

import java.io.Serializable;

public class BizModulesResp implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String moduleId;

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
}
