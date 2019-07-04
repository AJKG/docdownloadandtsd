package com.yodlee.docdownloadandtsd.VO;
/**
 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc.
 * Use is subject to license terms.
 *
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SplunkYccDataIntegration {

	Double latency;
	Double mfaPercentage;
	String addAccountPercentage;
	boolean showPopup;
	Double successPercentage;
	String mfaType;
	
	@JsonProperty
	public Double getLatency() {
		return latency;
	}
	public void setLatency(Double latency) {
		this.latency = latency;
	}
	@JsonProperty
	public Double getMfaPercentage() {
		return mfaPercentage;
	}
	public void setMfaPercentage(Double mfaPercentage) {
		this.mfaPercentage = mfaPercentage;
	}
	@JsonProperty
	public String getAddAccountPercentage() {
		return addAccountPercentage;
	}
	public void setAddAccountPercentage(String addAccountPercentage) {
		this.addAccountPercentage = addAccountPercentage;
	}
	
	@JsonProperty
	public boolean isShowPopup() {
		return showPopup;
	}
	public void setShowPopup(boolean showPopup) {
		this.showPopup = showPopup;
	}
	@JsonProperty
	public Double getSuccessPercentage() {
		return successPercentage;
	}
	public void setSuccessPercentage(Double successPercentage) {
		this.successPercentage = successPercentage;
	}
	@JsonProperty
	public String getMfaType() {
		return mfaType;
	}
	public void setMfaType(String mfaType) {
		this.mfaType = mfaType;
	}
	
		
	
	
	
}
