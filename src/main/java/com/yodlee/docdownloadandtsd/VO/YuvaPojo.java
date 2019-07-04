package com.yodlee.docdownloadandtsd.VO;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/**

 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.

 *

 * This software is the confidential and proprietary information of Yodlee, Inc.

 * Use is subject to license terms.

 *

 */

/**
 * @author MMahto
 *
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class YuvaPojo {


public String cacheItemId;

public String dataBase;


public String segmentId;

/**
 * @return the cacheItemId
 */
@JsonProperty
public String getCacheItemId() {
	return cacheItemId;
}

/**
 * @param cacheItemId the cacheItemId to set
 */
public void setCacheItemId(String cacheItemId) {
	this.cacheItemId = cacheItemId;
}

/**
 * @return the dataBase
 */
@JsonProperty
public String getdataBase() {
	return dataBase;
}

/**
 * @param dataBase the dataBase to set
 */
public void setdataBase(String dataBase) {
	this.dataBase = dataBase;
}

/**
 * @return the segmentId
 */
@JsonProperty
public String getsegmentId() {
	return segmentId;
}

/**
 * @param segmentId the segmentId to set
 */
public void setsegmentId(String segmentId) {
	this.segmentId = segmentId;
}




}

