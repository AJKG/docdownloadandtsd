/**
 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc.
 * Use is subject to license terms.
 *
 */


package com.yodlee.docdownloadandtsd.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection ="BatchIDCollections")

public class BatchCreatedView {
	
	
	int batchDetailsId;
	
	@Id
	String agentName;
	
	String batchReqDetailsId;
	
	@JsonProperty
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	
	
	@JsonProperty
	public int getBatchDetailsId() {
		return batchDetailsId;
	}
	public void setBatchDetailsId(int batchDetailsId) {
		this.batchDetailsId = batchDetailsId;
	}
	
	@JsonProperty
	public String getBatchReqDetailsId() {
		return batchReqDetailsId;
	}
	public void setBatchReqDetailsId(String batchReqDetailsId) {
		this.batchReqDetailsId = batchReqDetailsId;
	}
	

}
