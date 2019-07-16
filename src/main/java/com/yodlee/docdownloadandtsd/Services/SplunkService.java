package com.yodlee.docdownloadandtsd.Services;/** * Copyright (c) 2019 Yodlee Inc. All Rights Reserved. * * This software is the confidential and proprietary information of Yodlee, Inc. * Use is subject to license terms. * */import com.fasterxml.jackson.core.JsonParseException;import com.fasterxml.jackson.databind.JsonMappingException;import com.fasterxml.jackson.databind.ObjectMapper;import com.yodlee.docdownloadandtsd.DAO.SplunkRepository;import com.yodlee.docdownloadandtsd.VO.ItemDetailsVO;import org.json.JSONArray;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;import java.io.IOException;@Servicepublic class SplunkService {	private final Logger logger = LoggerFactory.getLogger(this.getClass());	@Autowired	YuvaGatewayImpl yuvagateway;	@Autowired	SplunkRepository splunkRepository;	public String getAgentName(String suminfo) throws Exception {		String agentName = "";		try {			agentName = splunkRepository.getAgentName(suminfo);		}catch(Exception e)		{			logger.error("Error while executing yuva:", e);			return "";		}				return agentName;	}			public ItemDetailsVO[] getyuvasegmentusers(String agentname,String suminfo) throws Exception {		String yuvaresponse = "";		try {			//yuvaresponse = yuvagateway.getYuvaSegmentUsers(agentname,suminfo);		}catch(Exception e)		{			logger.error("Error while executing yuva:", e);		}		JSONArray userArray = null;        if(yuvaresponse.isEmpty())        {             userArray = splunkRepository.getUsersFromSplunk(suminfo);        }else {              userArray = splunkRepository.getUsers(yuvaresponse,agentname,suminfo);             if(userArray.length()<2) {                  userArray = splunkRepository.getUsersFromSplunk(suminfo);             }         }        System.out.println("Here");        ItemDetailsVO[] yuvaItems=new ObjectMapper().readValue(userArray.toString(), ItemDetailsVO[].class);		return yuvaItems;	}	public String getSplunkQueryData(String suminfo) throws JsonParseException, JsonMappingException, IOException {		String response = null;		try {			response = splunkRepository.executeSplunkServices(suminfo);		} catch (Exception e) {			logger.error("Error while executing splunk query:", e);		}		System.out.println("response here in getsplunkquerydata \n"+response);		return response;	}}