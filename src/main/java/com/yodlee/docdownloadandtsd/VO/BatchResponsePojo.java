package com.yodlee.docdownloadandtsd.VO;

/**

 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.

 *

 * This software is the confidential and proprietary information of Yodlee, Inc.

 * Use is subject to license terms.

 *

 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author MMahto
 *
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class BatchResponsePojo {

	private String container;

	private String dbName;

	private String batchDetailsId;

	private String dumpUrl;

	private String responseType;

	private String baseScriptName;

	private String cobrandId;

	private String noOfAccounts;

	private String fmScriptVersion;

	private String refreshable;

	private String fmLatency;

	private String sumInfoId;

	private String itemId;

	private String siteId;

	private String scriptName;

	private String fmCode;

	/**
	 * @return the container
	 */
	public String getContainer() {
		return container;
	}

	/**
	 * @param container the container to set
	 */
	public void setContainer(String container) {
		this.container = container;
	}

	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName the dbName to set
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the batchDetailsId
	 */
	public String getBatchDetailsId() {
		return batchDetailsId;
	}

	/**
	 * @param batchDetailsId the batchDetailsId to set
	 */
	public void setBatchDetailsId(String batchDetailsId) {
		this.batchDetailsId = batchDetailsId;
	}

	/**
	 * @return the dumpUrl
	 */
	public String getDumpUrl() {
		return dumpUrl;
	}

	/**
	 * @param dumpUrl the dumpUrl to set
	 */
	public void setDumpUrl(String dumpUrl) {
		this.dumpUrl = dumpUrl;
	}

	/**
	 * @return the responseType
	 */
	public String getResponseType() {
		return responseType;
	}

	/**
	 * @param responseType the responseType to set
	 */
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	/**
	 * @return the baseScriptName
	 */
	public String getBaseScriptName() {
		return baseScriptName;
	}

	/**
	 * @param baseScriptName the baseScriptName to set
	 */
	public void setBaseScriptName(String baseScriptName) {
		this.baseScriptName = baseScriptName;
	}

	/**
	 * @return the cobrandId
	 */
	public String getCobrandId() {
		return cobrandId;
	}

	/**
	 * @param cobrandId the cobrandId to set
	 */
	public void setCobrandId(String cobrandId) {
		this.cobrandId = cobrandId;
	}

	/**
	 * @return the noOfAccounts
	 */
	public String getNoOfAccounts() {
		return noOfAccounts;
	}

	/**
	 * @param noOfAccounts the noOfAccounts to set
	 */
	public void setNoOfAccounts(String noOfAccounts) {
		this.noOfAccounts = noOfAccounts;
	}

	/**
	 * @return the fmScriptVersion
	 */
	public String getFmScriptVersion() {
		return fmScriptVersion;
	}

	/**
	 * @param fmScriptVersion the fmScriptVersion to set
	 */
	public void setFmScriptVersion(String fmScriptVersion) {
		this.fmScriptVersion = fmScriptVersion;
	}

	/**
	 * @return the refreshable
	 */
	public String getRefreshable() {
		return refreshable;
	}

	/**
	 * @param refreshable the refreshable to set
	 */
	public void setRefreshable(String refreshable) {
		this.refreshable = refreshable;
	}

	/**
	 * @return the fmLatency
	 */
	public String getFmLatency() {
		return fmLatency;
	}

	/**
	 * @param fmLatency the fmLatency to set
	 */
	public void setFmLatency(String fmLatency) {
		this.fmLatency = fmLatency;
	}

	/**
	 * @return the sumInfoId
	 */
	public String getSumInfoId() {
		return sumInfoId;
	}

	/**
	 * @param sumInfoId the sumInfoId to set
	 */
	public void setSumInfoId(String sumInfoId) {
		this.sumInfoId = sumInfoId;
	}

	/**
	 * @return the itemId
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return siteId;
	}

	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	/**
	 * @return the scriptName
	 */
	public String getScriptName() {
		return scriptName;
	}

	/**
	 * @param scriptName the scriptName to set
	 */
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	/**
	 * @return the fmCode
	 */
	public String getFmCode() {
		return fmCode;
	}

	/**
	 * @param fmCode the fmCode to set
	 */
	public void setFmCode(String fmCode) {
		this.fmCode = fmCode;
	}



}
