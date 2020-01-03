package com.plusone.pwms.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WebServicesRequest implements Serializable {

	// 参数
//	private Map<String, Object> params;
//
//	public Map<String, Object> getParams() {
//		return params;
//	}
//
//	public void setParams(Map<String, Object> params) {
//		this.params = params;
//	}
	private String pageId;
	private Long whId;
	private Long userId;
	private Map<String, Object> parameters = new HashMap();

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public Long getWhId() {
		return whId;
	}

	public void setWhId(Long whId) {
		this.whId = whId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
}
