package com.yodlee.docdownloadandtsd.VO;/** *  * Copyright (c) 2019 Yodlee Inc. All Rights Reserved. * * This software is the confidential and proprietary information of Yodlee, Inc. * Use is subject to license terms. *  */import com.fasterxml.jackson.annotation.JsonIgnoreProperties;import com.fasterxml.jackson.annotation.JsonProperty;@JsonIgnoreProperties(ignoreUnknown = true)public class BackTrackDataView {	private String summary;	private String transaction;	private String holding;	private String login;	private String statement;		@JsonProperty	public String getStatement() {		return statement;	}	public void setStatement(String statement) {		this.statement = statement;	}	@JsonProperty	public String getLogin() {		return login;	}	public void setLogin(String login) {		this.login = login;	}	@JsonProperty	public String getSummary() {		return summary;	}	public void setSummary(String summary) {		this.summary = summary;	}	@JsonProperty	public String getTransaction() {		return transaction;	}	public void setTransaction(String transaction) {		this.transaction = transaction;	}	@JsonProperty	public String getHolding() {		return holding;	}	public void setHolding(String holding) {		this.holding = holding;	}	@Override	public String toString() {		return "BackTrackDataView [summary=" + summary + ", transaction=" + transaction + ", holding=" + holding				+ ", login=" + login + ", statement=" + statement + "]";	}}