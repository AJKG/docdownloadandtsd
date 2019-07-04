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

/**
 * @author MMahto
 *
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class HammerServicesPojo {

	private String itemId;
	
	private String dbName;
	
	private String fmCode;
	
	private String fmScriptVersion;
	
	private String dumpUrl;
	
	private String fmLatency;

	/**
	 * @return the itemId
	 */
	@JsonProperty
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
	 * @return the dbName
	 */
	@JsonProperty
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
	 * @return the fmCode
	 */
	@JsonProperty
	public String getFmCode() {
		return fmCode;
	}

	/**
	 * @param fmCode the fmCode to set
	 */
	public void setFmCode(String fmCode) {
		this.fmCode = fmCode;
	}

	/**
	 * @return the fmScriptVersion
	 */
	@JsonProperty
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
	 * @return the dumpUrl
	 */
	@JsonProperty
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
	 * @return the fmLatency
	 */
	@JsonProperty
	public String getFmLatency() {
		return fmLatency;
	}

	/**
	 * @param fmLatency the fmLatency to set
	 */
	public void setFmLatency(String fmLatency) {
		this.fmLatency = fmLatency;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HammerServicesPojo [itemId=" + itemId + ", dbName=" + dbName + ", fmCode=" + fmCode
				+ ", fmScriptVersion=" + fmScriptVersion + ", dumpUrl=" + dumpUrl + ", fmLatency=" + fmLatency + "]";
	}
	
	
	
	
}

