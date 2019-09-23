package com.yodlee.docdownloadandtsd.Services;

/**

 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.

 *

 * This software is the confidential and proprietary information of Yodlee, Inc.

 * Use is subject to license terms.

 *

 */

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.yodlee.docdownloadandtsd.DAO.RpaldaRepository;
import com.yodlee.docdownloadandtsd.VO.*;
import com.yodlee.docdownloadandtsd.authenticator.Authorization;
import com.yodlee.docdownloadandtsd.exceptionhandling.GeneralErrorHandler;
import com.yodlee.docdownloadandtsd.exceptionhandling.ToolsResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HammerServicesImpl {


	@Autowired
	Authorization authorization;

	@Autowired
	RpaldaRepository rpaldaRepository;

	@Value("${hammer.UserId}")
	private String hammerUserName;

	@Value("${hammer.Password}")
	private String hammerPassword;

	@Value("${hammer.url}")
	private String HAMMER_FIREMEM_AUTH_URL;

	@Value("${hammer.batchCreation.url}")
	private String BATCH_CREATION_URL;

	@Value("${hammer.batchTrigger.url}")
	private String BATCH_TRIGGER_URL;

	@Value("${hammer.batchPolling.url}")
	private String BATCH_POLLING_URL;

	@Value("${hammer.batchCreate.items.count}")
	private int itemCount;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public String hammerLogin() throws  IOException {
		// TODO Auto-generated method stub
		logger.info("######################################### hammerLogin #########################################");
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = getHeader(null);
		
		String password=authorization.decrypt(hammerPassword);

		HashMap<String, String> hammerRequestBody = new HashMap<String, String>();
		hammerRequestBody.put("username", hammerUserName);
		hammerRequestBody.put("password", password);

		HttpEntity<Map<String, String>> hammerRequest = new HttpEntity<Map<String, String>>(hammerRequestBody, headers);

		String hammerResponse ="";
		try {
			hammerResponse= restTemplate.postForObject(HAMMER_FIREMEM_AUTH_URL, hammerRequest, String.class);
		}catch (HttpClientErrorException httpClientErrorException) {
			logger.error("Found issue while login into Firmem Hammer API: "+Arrays.toString(httpClientErrorException.getStackTrace()));
			throw new GeneralErrorHandler("Found issue while login into Firmem Hammer API");
		} catch (Exception exception) {
			logger.error("Something is not right.Please retry :"+Arrays.toString(exception.getStackTrace()));
			throw new GeneralErrorHandler("Something is not right.Please retry.");
		}

		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = mapper.getFactory();
		JsonParser parser = factory.createParser(hammerResponse);
		JsonNode actualObj = mapper.readTree(parser);
		String authTokenId = actualObj.get("token").toString();
		authTokenId = "Bearer "+authTokenId.replaceAll("\"", "");
		logger.error("Token ID is "+authTokenId);
		return authTokenId;
	}

	public String triggerFiremem(String cacheItem,String dbName, String tsd, String token) throws Exception{
		//dbName=getCorrectDBName(dbName);
		//HammerService hammerService = new HammerService();
		//token = hammerService.generateToken(hammerUserName,hammerPassword,hammerLoginUrl);
		System.out.println("token rcvd : "+token);
		HttpHeaders header=getHeader(token);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new ToolsResponseHandler());

		HammerRequestFirememVO action = new HammerRequestFirememVO();
		action.setServerType("I");
		action.setItemType("2");
		List<String> requestType= new ArrayList<String>();
		requestType.add("4");
		action.setRequestTypes(requestType);
		action.setMfaPreference(0);
		action.setDbName(dbName);
		action.setItemId(cacheItem);
		action.setRefreshRoute("D");
		action.setCustomrefreshRoute("C");
		action.setCustomRoute("");
		action.setProdCertified(true);
		action.setAgentFileType("JAVA");
		action.setModifiedtxnDuration(true);

		Map<String, Object> map = new HashMap<>();
		map.put("open", "true");
		map.put("firstTimeTxnDays", "");
		map.put("inactiveUsersTxnDays", "");
		map.put("maxTxnDays", "");

		Map<String, String> map1 = new HashMap<>();
		map1.put("com.yodlee.contentservice.transactions.max_txn_available_duration", tsd);

		Map<String, String> map2 = new HashMap<>();
		map2.put("Unbilled Transaction Selection Period", tsd);

		map.put("txnKeyValue", map1);
		map.put("otherKeyValue", map2);
		action.setTransactionSelectionDuration(map);

		ObjectMapper mapper = new ObjectMapper();

		String request = mapper.writeValueAsString(action);
		System.out.println("Generating Request from Hammer Services"+request);

		HttpEntity<String> requestEntity = new HttpEntity<String>(request,header);
		String response = restTemplate.postForObject("https://firemem.tools.yodlee.com/hammer/R/F/IR", requestEntity, String.class);
		System.out.println("response here "+response);

		JSONObject fmResponse=new JSONObject(response);

		if(!fmResponse.has("appRequestId")) {
			return null;
		}

		System.out.println("Final response "+fmResponse.getString("appRequestId"));
		return fmResponse.getString("appRequestId");
	}


	public Integer createBatchForDocumentDownload(ItemDetailsVO[] yuvaPojo, String accessTokenId, String agentName, String msaOrCii)
			throws IOException, JSONException {
		logger.info("######################################### createBatch #########################################");
		RestTemplate restTemplate = new RestTemplate();

		//JSONObject batchResponse = new JSONObject();

		/*Creating the request body for creation the batch*/

		HashMap<String, Object> batchCreationRequestBody = new HashMap<String, Object>();
		HashMap<String, Object> batchCreationItemMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, String>> itemDetailList= new ArrayList<HashMap<String, String>>();

		int count =0;
		for( ItemDetailsVO yuvaItemDetails: yuvaPojo) {
			if(++count>itemCount) {
				break;
			}
			HashMap<String, String> itemDetailMap = new HashMap<String, String>();
			if(msaOrCii.equalsIgnoreCase("cii")) {
				itemDetailMap.put("itemId", yuvaItemDetails.getCacheItemId());
				itemDetailMap.put("itemType", "2");
			}else {
				itemDetailMap.put("itemId", yuvaItemDetails.getMemSiteAccId());
				itemDetailMap.put("itemType", "3");
			}
			itemDetailMap.put("dbName", yuvaItemDetails.getDataBase());
			itemDetailMap.put("description","Ajax Batch Testing");
			itemDetailList.add(itemDetailMap);
		}

		batchCreationItemMap.put("Add", itemDetailList);
		batchCreationRequestBody.put("items", batchCreationItemMap);
		batchCreationRequestBody.put("agentName",agentName);
		batchCreationRequestBody.put("description",(agentName.length()>19) ? agentName.substring(0, 19): agentName);
		batchCreationRequestBody.put("nickName",(agentName.length()>19) ? agentName.substring(0, 19): agentName);

		logger.info("Request body of batch Creation : "+batchCreationRequestBody.toString());

		HttpHeaders headers= getHeader(accessTokenId);

		HttpEntity<Map<String, Object>> requestEntityForBatchCreation = new HttpEntity<Map<String, Object>>(batchCreationRequestBody,headers);

		String batchCreationResponse = restTemplate.postForObject(BATCH_CREATION_URL, requestEntityForBatchCreation, String.class);

		logger.info("Batch Creation Response :"+batchCreationResponse);

		JSONObject batchCreatonResponseObject = new JSONObject(batchCreationResponse);

		Integer batchDetailsId = batchCreatonResponseObject.optInt("batchDetailsId");

		if(batchDetailsId == 0) {
			logger.info("Create Batch error :"+batchCreatonResponseObject.optString("statusMsg"));
			throw new GeneralErrorHandler("Create Batch error :"+batchCreatonResponseObject.optString("statusMsg"));
		}
		System.out.println("Create Batch => batchDetailsId:"+batchDetailsId);
		return batchDetailsId;
	}

	public Integer createBatchForTSD(ItemDetailsVO[] yuvaPojo, String accessTokenId, String agentName, String msaOrCii)
			throws IOException, JSONException {
		logger.info("######################################### createBatch #########################################");
		RestTemplate restTemplate = new RestTemplate();

		//JSONObject batchResponse = new JSONObject();

		/*Creating the request body for creation the batch*/

		HashMap<String, Object> batchCreationRequestBody = new HashMap<String, Object>();
		HashMap<String, Object> batchCreationItemMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, String>> itemDetailList= new ArrayList<HashMap<String, String>>();

		int count =0;
		for( ItemDetailsVO yuvaItemDetails: yuvaPojo) {
			if(++count>itemCount) {
				break;
			}
			HashMap<String, String> itemDetailMap = new HashMap<String, String>();
			if(msaOrCii.equalsIgnoreCase("cii")) {
				itemDetailMap.put("itemId", yuvaItemDetails.getCacheItemId());
				itemDetailMap.put("itemType", "2");
			}else {
				itemDetailMap.put("itemId", yuvaItemDetails.getMemSiteAccId());
				itemDetailMap.put("itemType", "3");
			}
			itemDetailMap.put("dbName", yuvaItemDetails.getDataBase());
			itemDetailMap.put("description","Ajax Batch Testing");
			itemDetailList.add(itemDetailMap);
		}

		batchCreationItemMap.put("Add", itemDetailList);
		batchCreationRequestBody.put("items", batchCreationItemMap);
		batchCreationRequestBody.put("agentName",agentName);
		batchCreationRequestBody.put("description",(agentName.length()>19) ? agentName.substring(0, 19): agentName);
		batchCreationRequestBody.put("nickName",(agentName.length()>19) ? agentName.substring(0, 19): agentName);

		logger.info("Request body of batch Creation : "+batchCreationRequestBody.toString());

		HttpHeaders headers= getHeader(accessTokenId);

		HttpEntity<Map<String, Object>> requestEntityForBatchCreation = new HttpEntity<Map<String, Object>>(batchCreationRequestBody,headers);

		String batchCreationResponse = restTemplate.postForObject(BATCH_CREATION_URL, requestEntityForBatchCreation, String.class);
		logger.info("Batch Creation Response :"+batchCreationResponse);

		JSONObject batchCreatonResponseObject = new JSONObject(batchCreationResponse);

		Integer batchDetailsId = batchCreatonResponseObject.optInt("batchDetailsId");

		if(batchDetailsId == 0) {
			logger.info("Create Batch error :"+batchCreatonResponseObject.optString("statusMsg"));
			throw new GeneralErrorHandler("Create Batch error :"+batchCreatonResponseObject.optString("statusMsg"));
		}
		logger.info("Create Batch => batchDetailsId:"+batchDetailsId);
		return batchDetailsId;
	}

	public Integer triggerBatchForDocumentDownload(Integer batchDetailsId,String accessTokenId, String customrefreshRoute, String customRoute)
			throws JSONException,IOException {
		logger.info("######################################### triggerBatch #########################################");
		RestTemplate restTemplate = new RestTemplate();

		/*Request Body to trigger the batch*/
		HashMap<String, Object> batchTriggerRequestBody = new HashMap<String, Object>();
		HashMap<String, Object> batchTriggerRequestParams = new HashMap<String, Object>();

		ArrayList<String> al = new ArrayList<>();
		al.add("3");

		batchTriggerRequestParams.put("serverType", "I");
		batchTriggerRequestParams.put("requestTypes", al);
		batchTriggerRequestParams.put("prodCertified", true);
		batchTriggerRequestParams.put("agentFileType", "JAVA");
		batchTriggerRequestParams.put("customrefreshRoute", customrefreshRoute);

		HashMap<String, Object> iavRequestMap = new HashMap<String, Object>();
		iavRequestMap.put("accountNumberMatchPrefix","");
		iavRequestMap.put("accountNumberMatchSuffix","");
		iavRequestMap.put("paramKeyValues",new HashMap<>());

		HashMap<String, Object> iavPlusRequestMap = new HashMap<String, Object>();
		iavPlusRequestMap.put("iavPlus",false);
		iavPlusRequestMap.put("paramKeyValues",new HashMap<>());

		iavRequestMap.put("iavPlusRequest",iavPlusRequestMap);
		batchTriggerRequestParams.put("iavRequest",iavRequestMap);

		HashMap<String, Object> docDownloadRequestMap = new HashMap<String, Object>();
		docDownloadRequestMap.put("pfm", false);
		docDownloadRequestMap.put("latest", false);
		docDownloadRequestMap.put("allAccounts", true);
		docDownloadRequestMap.put("docDownloadRequest", false);
		docDownloadRequestMap.put("durationType", "");
		docDownloadRequestMap.put("taxDurationType", "");

		HashMap<String, Object> statementRequest = new HashMap<String, Object>();

		statementRequest.put("stmtDurationType", "");
		statementRequest.put("taxDurationType", "");

		docDownloadRequestMap.put("statementRequest", statementRequest);
		batchTriggerRequestParams.put("docDownloadRequest",docDownloadRequestMap);



		if(customRoute.equals("D")) {
			batchTriggerRequestParams.put("refreshRoute",customRoute);
		}else {
			batchTriggerRequestParams.put("customRoute",customRoute);
		} 


		batchTriggerRequestBody.put("batchRefreshParams", batchTriggerRequestParams);
		batchTriggerRequestBody.put("batchDetailsId", batchDetailsId);
		batchTriggerRequestBody.put("userName", hammerUserName);
		
		logger.info("Batch Trigger Request Body :"+batchTriggerRequestBody);
		HttpHeaders headers = getHeader(accessTokenId);

		HttpEntity<Map<String, Object>> batchTriggerRequestEntity = new HttpEntity<Map<String, Object>>(batchTriggerRequestBody, headers);

		String batchTriggerResponse = restTemplate.postForObject(BATCH_TRIGGER_URL, batchTriggerRequestEntity, String.class);

		System.out.println("batchResp : " +batchTriggerResponse);

		JSONObject triggerBatchResponse = new JSONObject(batchTriggerResponse);

		String appRequestId =triggerBatchResponse.optString("appRequestId");

		if(appRequestId.equals("REJECTED")) {
			String statusMsg= triggerBatchResponse.getString("statusMsg");
			logger.info("Batch Trigger response : "+triggerBatchResponse.toString());
			if (statusMsg.contains("ajaxtool")){
				String batchid = statusMsg.substring(statusMsg.indexOf("Batch Id")+9);
				batchid = batchid.substring(0,batchid.indexOf(" "));
				Integer batchOldReq = Integer.parseInt(batchid);
				System.out.println("old batch request id: "+batchOldReq);
				return batchOldReq;

			}else {
				throw new GeneralErrorHandler("Batch Trigger Status : " + appRequestId + " | Status Msg : " + statusMsg);
			}
		}

		JSONArray batchReqDetailList=triggerBatchResponse.getJSONArray("batchReqDetailList");

		/*Integer batchStatusId= batchReqDetailList.getJSONObject(0).getInt("batchStatusId");
		if(batchStatusId==4) {

		}*/
		Integer batchReqDetailsId= triggerBatchResponse.optInt("batchReqDetailsId");
		logger.info("Batch Trigger => batchReqDetailsId:"+batchReqDetailsId);

		return batchReqDetailsId;
	}

	public Integer triggerBatchForDocumentDownloadThroughSDG(Integer batchDetailsId,String accessTokenId, String customrefreshRoute, String customRoute)
			throws JSONException,IOException {
		logger.info("######################################### triggerBatch DOC SDG #########################################");
		RestTemplate restTemplate = new RestTemplate();

		/*Request Body to trigger the batch*/
		HashMap<String, Object> batchTriggerRequestBody = new HashMap<String, Object>();
		HashMap<String, Object> batchTriggerRequestParams = new HashMap<String, Object>();

		ArrayList<String> al = new ArrayList<>();

		batchTriggerRequestParams.put("serverType", "SA");
		batchTriggerRequestParams.put("requestTypes", al);
		batchTriggerRequestParams.put("prodCertified", true);
		batchTriggerRequestParams.put("agentFileType", "JAVA");
		batchTriggerRequestParams.put("customrefreshRoute", customrefreshRoute);

		HashMap<String, Object> iavRequestMap = new HashMap<String, Object>();
		iavRequestMap.put("accountNumberMatchPrefix","");
		iavRequestMap.put("accountNumberMatchSuffix","");
		iavRequestMap.put("paramKeyValues",new HashMap<>());

		HashMap<String, Object> iavPlusRequestMap = new HashMap<String, Object>();
		iavPlusRequestMap.put("iavPlus",false);
		iavPlusRequestMap.put("paramKeyValues",new HashMap<>());

		iavRequestMap.put("iavPlusRequest",iavPlusRequestMap);
		batchTriggerRequestParams.put("iavRequest",iavRequestMap);

		HashMap<String, Object> docDownloadRequestMap = new HashMap<String, Object>();
		docDownloadRequestMap.put("pfm", false);
		docDownloadRequestMap.put("latest", false);
		docDownloadRequestMap.put("allAccounts", true);
		docDownloadRequestMap.put("docDownloadRequest", false);
		docDownloadRequestMap.put("durationType", "");
		docDownloadRequestMap.put("taxDurationType", "");

		HashMap<String, Object> statementRequest = new HashMap<String, Object>();
		statementRequest.put("stmtDurationType", "");
		statementRequest.put("taxDurationType", "");

		docDownloadRequestMap.put("statementRequest", statementRequest);
		batchTriggerRequestParams.put("docDownloadRequest",docDownloadRequestMap);


		HashMap<String, Object> dataSetRequest = new HashMap<String, Object>();
		HashMap<String, Object> basicAggregationData = new HashMap<String, Object>();
		HashMap<String, Object> dataSetsMap = new HashMap<String, Object>();

		dataSetsMap.put("BASIC_AGG_DATA.BASIC_ACCOUNT_INFO",true);
		dataSetsMap.put("BASIC_AGG_DATA.ACCOUNT_DETAILS",true);
		dataSetsMap.put("BASIC_AGG_DATA.STATEMENTS",true);

		basicAggregationData.put("dataSetsMap", dataSetsMap);
		dataSetRequest.put("basicAggregationData", basicAggregationData);


		HashMap<String, Object> documentEnabled = new HashMap<String, Object>();
		HashMap<String, Object> dataSetsMapDoc = new HashMap<String, Object>();

		dataSetsMapDoc.put("DOCUMENT.TAX",true);
		dataSetsMapDoc.put("DOCUMENT.EBILLS",true);
		dataSetsMapDoc.put("DOCUMENT.STATEMENTS",true);

		documentEnabled.put("dataSetsMap", dataSetsMapDoc);
		dataSetRequest.put("document", documentEnabled);

		batchTriggerRequestParams.put("dataSetRequest", dataSetRequest);

		if(customRoute.equals("D")) {
			batchTriggerRequestParams.put("refreshRoute",customRoute);
		}else {
			batchTriggerRequestParams.put("customRoute",customRoute);
		}

		batchTriggerRequestBody.put("batchRefreshParams", batchTriggerRequestParams);
		batchTriggerRequestBody.put("batchDetailsId", batchDetailsId);
		batchTriggerRequestBody.put("userName", hammerUserName);

		logger.info("Batch Trigger Request Body :"+batchTriggerRequestBody);
		HttpHeaders headers = getHeader(accessTokenId);

		HttpEntity<Map<String, Object>> batchTriggerRequestEntity = new HttpEntity<Map<String, Object>>(batchTriggerRequestBody, headers);

		String batchTriggerResponse = restTemplate.postForObject(BATCH_TRIGGER_URL, batchTriggerRequestEntity, String.class);

		System.out.println("batchResp : " +batchTriggerResponse);

		JSONObject triggerBatchResponse = new JSONObject(batchTriggerResponse);

		String appRequestId =triggerBatchResponse.optString("appRequestId");

		if(appRequestId.equals("REJECTED")) {
			String statusMsg= triggerBatchResponse.getString("statusMsg");
			logger.info("Batch Trigger response : "+triggerBatchResponse.toString());
			throw new GeneralErrorHandler("Batch Trigger Status : "+appRequestId +" | Status Msg : "+statusMsg);
		}

		JSONArray batchReqDetailList=triggerBatchResponse.getJSONArray("batchReqDetailList");

/*Integer batchStatusId= batchReqDetailList.getJSONObject(0).getInt("batchStatusId");
if(batchStatusId==4) {

}*/
		Integer batchReqDetailsId= triggerBatchResponse.optInt("batchReqDetailsId");
		logger.info("Batch Trigger => batchReqDetailsId:"+batchReqDetailsId);

		return batchReqDetailsId;
	}


	public Integer triggerBatchForTSD(Integer batchDetailsId,String accessTokenId, String customrefreshRoute, String customRoute, String tsd)
			throws JSONException,IOException {
		logger.info("######################################### triggerBatch #########################################");
		RestTemplate restTemplate = new RestTemplate();

		/*Request Body to trigger the batch*/
		HashMap<String, Object> batchTriggerRequestBody = new HashMap<String, Object>();
		HashMap<String, Object> batchTriggerRequestParams = new HashMap<String, Object>();

		batchTriggerRequestParams.put("serverType", "SR");
		batchTriggerRequestParams.put("requestTypes", new ArrayList<>());
		batchTriggerRequestParams.put("prodCertified", true);
		batchTriggerRequestParams.put("agentFileType", "JAVA");
		batchTriggerRequestParams.put("customrefreshRoute", customrefreshRoute);

		HashMap<String, Object> iavRequestMap = new HashMap<String, Object>();
		iavRequestMap.put("accountNumberMatchPrefix","");
		iavRequestMap.put("accountNumberMatchSuffix","");
		iavRequestMap.put("paramKeyValues",new HashMap<>());

		HashMap<String, Object> iavPlusRequestMap = new HashMap<String, Object>();
		iavPlusRequestMap.put("iavPlus",false);
		iavPlusRequestMap.put("paramKeyValues",new HashMap<>());

		iavRequestMap.put("iavPlusRequest",iavPlusRequestMap);
		batchTriggerRequestParams.put("iavRequest",iavRequestMap);

		HashMap<String, Object> docDownloadRequestMap = new HashMap<String, Object>();
		docDownloadRequestMap.put("pfm", false);
		docDownloadRequestMap.put("latest", false);
		docDownloadRequestMap.put("allAccounts", true);
		docDownloadRequestMap.put("docDownloadRequest", false);
		docDownloadRequestMap.put("durationType", "");
		docDownloadRequestMap.put("taxDurationType", "");

		HashMap<String, Object> statementRequest = new HashMap<String, Object>();

		statementRequest.put("stmtDurationType", "");
		statementRequest.put("taxDurationType", "");

		docDownloadRequestMap.put("statementRequest", statementRequest);
		batchTriggerRequestParams.put("docDownloadRequest",docDownloadRequestMap);

		HashMap<String, Object> basicAggregationData = new HashMap<String, Object>();
		HashMap<String, Object> prRequest = new HashMap<>();

		prRequest.put("prRequest", false);

		basicAggregationData.put("prRequest", prRequest);

		HashMap<String, Object> dataSetsMap = new HashMap<String, Object>();
		dataSetsMap.put("BASIC_AGG_DATA.BASIC_ACCOUNT_INFO", true);
		dataSetsMap.put("BASIC_AGG_DATA.ACCOUNT_DETAILS", true);
		dataSetsMap.put("BASIC_AGG_DATA.TRANSACTIONS", true);
		dataSetsMap.put("BASIC_AGG_DATA.TRANSACTIONS.maxTxnSelectionDuration", tsd);

		basicAggregationData.put("basicAggregationData",dataSetsMap);
		batchTriggerRequestParams.put("dataSetRequest",basicAggregationData);


		if(customRoute.equals("D")) {
			batchTriggerRequestParams.put("refreshRoute",customRoute);
		}else {
			batchTriggerRequestParams.put("customRoute",customRoute);
		}


		batchTriggerRequestBody.put("batchRefreshParams", batchTriggerRequestParams);
		batchTriggerRequestBody.put("batchDetailsId", batchDetailsId);
		batchTriggerRequestBody.put("userName", hammerUserName);

		logger.info("Batch Trigger Request Body :"+batchTriggerRequestBody);
		HttpHeaders headers = getHeader(accessTokenId);

		HttpEntity<Map<String, Object>> batchTriggerRequestEntity = new HttpEntity<Map<String, Object>>(batchTriggerRequestBody, headers);

		String batchTriggerResponse = restTemplate.postForObject(BATCH_TRIGGER_URL, batchTriggerRequestEntity, String.class);

		System.out.println("batchResp : " +batchTriggerResponse);

		JSONObject triggerBatchResponse = new JSONObject(batchTriggerResponse);

		String appRequestId =triggerBatchResponse.optString("appRequestId");

		if(appRequestId.equals("REJECTED")) {
			String statusMsg= triggerBatchResponse.getString("statusMsg");
			logger.info("Batch Trigger response : "+triggerBatchResponse.toString());
			if (statusMsg.contains("ajaxtool")){
						String batchid = statusMsg.substring(statusMsg.indexOf("Batch Id")+9);
						batchid = batchid.substring(0,batchid.indexOf(" "));
						Integer batchOldReq = Integer.parseInt(batchid);
						System.out.println("old batch request id: "+batchOldReq);
						return batchOldReq;

			}else {
				throw new GeneralErrorHandler("Batch Trigger Status : " + appRequestId + " | Status Msg : " + statusMsg);
			}
		}

		JSONArray batchReqDetailList=triggerBatchResponse.getJSONArray("batchReqDetailList");

		/*Integer batchStatusId= batchReqDetailList.getJSONObject(0).getInt("batchStatusId");
		if(batchStatusId==4) {

		}*/
		Integer batchReqDetailsId= triggerBatchResponse.optInt("batchReqDetailsId");
		logger.info("Batch Trigger => batchReqDetailsId:"+batchReqDetailsId);

		return batchReqDetailsId;
	}


	private SimpleClientHttpRequestFactory getClientHttpRequestFactory()
	{
		SimpleClientHttpRequestFactory clientHttpRequestFactory
		= new SimpleClientHttpRequestFactory();
		//Connect timeout
		clientHttpRequestFactory.setConnectTimeout(6000000);

		//Read timeout
		clientHttpRequestFactory.setReadTimeout(6000000);
		return clientHttpRequestFactory;
	}

	public JSONObject pollingTriggerBatch(Integer batchReqDetailsId, String accessTokenId)
			throws JSONException, IOException, InterruptedException {
		logger.info("######################################### pollingTriggerBatch #########################################");
		ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);

		HttpHeaders headers = getHeader(accessTokenId);

		//JSONObject batchResponse = new JSONObject();

		/*Creating the request body for creation the batch*/

		//HashMap<String, Object> batchPollingRequestBody = new HashMap<String, Object>();
		//System.out.println(batchReqDetailsId);
		HttpEntity<Integer> batchPollingRequestBodyEntity = new HttpEntity<Integer>(batchReqDetailsId,headers);

		//System.out.println(batchPollingRequestBodyEntity.toString());
		//String batchPollingResponse = restTemplate.postForObject(BATCH_POLLING_URL, batchPollingRequestBodyEntity, String.class);

		String batchPollingResponse = null;
		Integer batchStatusId= -1;
		HashMap<String, String> itemListmap = new HashMap<String, String>();
		boolean isBatchRefreshComplete=true;
		int poolingCount=0;
		
		do {
			batchPollingResponse = restTemplate.postForObject(BATCH_POLLING_URL, batchPollingRequestBodyEntity, String.class);

			if(poolingCount>0) {
				Thread.sleep(5000);
			}
			
			//logger.info(batchPollingResponse);
			JSONObject batchPollingResponseJsonObject= new JSONObject(batchPollingResponse);
			Integer batchStatus= batchPollingResponseJsonObject.optInt("batchStatus");
			//logger.info("Batch Polling => batchStatus:"+batchStatus);
			isBatchRefreshComplete =true;

			if(batchStatus !=4) {
				JSONArray batchReqDetailList=batchPollingResponseJsonObject.getJSONArray("batchReqDetailList");
				
				batchStatusId= batchReqDetailList.getJSONObject(0).getInt("batchStatusId");
				//System.out.println(batchStatusId);

				JSONArray batchResultListArray=batchPollingResponseJsonObject.getJSONArray("batchResultList");

				for(int i=0;i<batchResultListArray.length();i++) {
					JSONObject itemJson = batchResultListArray.getJSONObject(i);
					String dumpUrl = itemJson.optString("dumpUrl");
					boolean refreshable = itemJson.optBoolean("refreshable");
					//String itemId = itemJson.optInt("itemId");
					//logger.info("refreshable"+refreshable);
					if(dumpUrl == null && refreshable) {
						System.out.println("isBatchRefreshComplete is "+isBatchRefreshComplete);
						isBatchRefreshComplete=false;
						//itemListmap.put(itemId, true);
					}
					
				}

			}

			poolingCount++;
			System.out.println("poolingCount "+poolingCount+"| Batch Status ID: "+batchStatusId);
			if(batchStatusId==7){
				System.out.println("Batch Partially Completed");
			}

		} while (! (batchStatusId.equals(5) ||  batchStatusId.equals(4) ||  batchStatusId.equals(7)) || ! isBatchRefreshComplete);
//logger.info("batchStatusId outside "+batchStatusId+ " isBatchRefreshzComplete :"+isBatchRefreshComplete);
		logger.info("batch Polling Response :"+batchPollingResponse);
		JSONObject batchPollingResponseJsonObj= new JSONObject(batchPollingResponse);

		/*if(batchStatusId==4) {
			throw new CustomSuccessException("Batch triggered Failed ");
		}*/

		/*JSONArray batchResultArray = batchPollingResponseJsonObj.getJSONArray("batchResultList");

		JSONArray batchReqDetailListArray = batchPollingResponseJsonObj.getJSONArray("batchReqDetailList");
		Integer batchReqDetailsId2INt =(Integer) batchReqDetailListArray.getJSONObject(0).optInt("batchReqDetailsId");

		BatchResponsePojo[] BatchResponsePojoArray =new ObjectMapper().readValue(batchResultArray.toString(), BatchResponsePojo[].class); 
*/
		//HammerServicesPojo[] batchResponse=new ObjectMapper().readValue(batchResult, HammerServicesPojo[].class); 

		return batchPollingResponseJsonObj;
		//return BatchResponsePojoArray;
	}

	public HttpHeaders getHeader(String accessTokenId){
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		if(accessTokenId != null) {
			header.set("Authorization",accessTokenId);
		}
		return header;
	}

	public DumpDetailsVO getDumpLink(String reqId,String cacheItem, String sumInfoId, String token, int itemNo) throws Exception{

		System.out.println("Req Ids here--- "+reqId);
		DumpDetailsVO dumpDetails=new DumpDetailsVO();
		dumpDetails.setCacheItem(cacheItem);




		for(int wait=0;wait<10;wait+=1){

			String dumpResponse=getDumpUrl(reqId, token);
			if(!rpaldaRepository.isNullValue(dumpResponse) && dumpResponse.toLowerCase().contains("token expired")) {
				token = hammerLogin();
				dumpResponse=getDumpUrl(reqId, token);
			}
			if(dumpResponse==null){
				System.out.println("DumpResponse is null while Getting Dump link, Hence Retrying");
				continue;
			}
			JSONObject firememResponse=new JSONObject(dumpResponse);
			String runningStatus=firememResponse.get("refreshStateDescriptions").toString();
			if(itemNo==1){
				System.out.println("Waiting for: "+(wait*30)+" Seconds"+" | "+reqId);
			}
			if(runningStatus.contains("Success")){
				System.out.println(firememResponse);
				System.out.println("Yeyy!! Gottt");
				System.out.println("Dump "+firememResponse.getString("dump")+" Status "+firememResponse.getInt("status"));
				dumpDetails.setDumpUrl(firememResponse.getString("dump"));
				dumpDetails.setStatusCode(firememResponse.getInt("status"));
				return dumpDetails;
			}else if(runningStatus.contains("Failed")) {
				break;
			}
			if(itemNo==1) {
				Thread.sleep(30000);
			}else{
				Thread.sleep(1000);
			}
		}
		return dumpDetails;
	}

	public String getDumpUrl(String requestId, String token) throws Exception{

		HttpHeaders header=getHeader(token);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new ToolsResponseHandler());
		Map<String,String> request=new HashMap<String,String>();
		request.put("appRequestId", requestId);
		HttpEntity<Map<String,String>> requestEntity = new HttpEntity<Map<String,String>>(request,header);
		String response = null;
		try {
			response = restTemplate.postForObject("https://firemem.tools.yodlee.com/hammer/R/F/RS", requestEntity, String.class);
		}catch (Exception e) {
            try {
                response = restTemplate.postForObject("https://firemem.tools.yodlee.com/hammer/R/F/RS", requestEntity, String.class);
            }catch (Exception e1) {
                e1.printStackTrace();
            }
		}
		//System.out.println("response here "+response);
		return response;
	}

	public HashMap<String,Object> retriveDataFromFiremem(HashMap<String, HashMap<String,Object>> jDapItemListFromBatch, HashMap<String, HashMap<String,Object>> ajaxItemListFromBatch)
			throws JSONException, IOException {
		logger.info("######################################### retriveDataFromFiremem #########################################");
		ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);

		HashMap<String,Object> firememExtractedAjaxResponseListMap= new HashMap<String,Object>();
		
		for(String jDapItemId: jDapItemListFromBatch.keySet()) {
			logger.info("Item ID :"+jDapItemId);
			HashMap<String, Object> jDapItemDetailsMap = jDapItemListFromBatch.get(jDapItemId);
			Object jDapDumpUrlObj=jDapItemDetailsMap.get("dumpUrl");
			String jDApDumpUrl="";
			if(jDapDumpUrlObj==null) {
				jDApDumpUrl="";
			}else {
				jDApDumpUrl= (String)jDapDumpUrlObj;
			}
			String jDApDumpUrlToSend=jDApDumpUrl;
			if(jDApDumpUrl!=null)
				jDApDumpUrl =modifiedFirememDumpLink(jDApDumpUrl);

			if(!ajaxItemListFromBatch.containsKey(jDapItemId)) {
				continue;
			}

			Object jDApFmLatencyObj=jDapItemDetailsMap.get("fmLatency");
			String jDapFmLatency="";

			if(jDApFmLatencyObj != null) {
				jDapFmLatency= Integer.toString((Integer)jDApFmLatencyObj);
			}

			Object jDapFmCodeObj=jDapItemDetailsMap.get("fmCode");
			String jDapFmCode="";

			if(jDapFmCodeObj != null) {
				jDapFmCode= Integer.toString((Integer)jDapFmCodeObj);
			}

			HashMap<String, Object> ajaxItemDetailsMap = ajaxItemListFromBatch.get(jDapItemId);
			Object ajaxDumpUrlObj=ajaxItemDetailsMap.get("dumpUrl");
			String ajaxDumpUrl="";
			if(ajaxDumpUrlObj==null) {
				ajaxDumpUrl="";
			}else {
				ajaxDumpUrl= (String)ajaxDumpUrlObj;
			}

			String ajaxDumpUrlToSend=ajaxDumpUrl;

			ajaxDumpUrl =modifiedFirememDumpLink(ajaxDumpUrl);

			Object ajaxFmLatencyObj=ajaxItemDetailsMap.get("fmLatency");
			String ajaxFmLatency="";

			if(ajaxFmLatencyObj != null) {
				ajaxFmLatency= Integer.toString((Integer)ajaxFmLatencyObj);
			}

			Object ajaxFmCodeObj=ajaxItemDetailsMap.get("fmCode");
			String ajaxFmCode="";

			if(ajaxFmCodeObj != null) {
				ajaxFmCode= Integer.toString((Integer)ajaxFmCodeObj);
			}

			// JDAP Firemem Access and retrive
			String jDapFirememResponse= restTemplate.getForObject(jDApDumpUrl, String.class);
			String jDapFirememXML = fetchFinalSiteXML(jDapFirememResponse);
			//logger.info("JDAP FIRMEM => jDapFirememXML : "+jDapFirememXML);
			String jDapAccountSummaryXML=getAccountSummaryXML(jDapFirememResponse);
			//logger.info("JDAP FIRMEM => jDapAccountSummaryXML : "+jDapAccountSummaryXML);

			//AJAX Firemem Access and retrive
			String ajaxFirememResponse= restTemplate.getForObject(ajaxDumpUrl, String.class);
			String ajaxFirememXML = fetchFinalSiteXML(ajaxFirememResponse);
			logger.info("AJAX FIRMEM => ajaxFirememXML : "+ajaxFirememXML);
			String ajaxAccountSummaryXML=getAccountSummaryXML(ajaxFirememResponse);
			logger.info("AJAX FIRMEM => ajaxAccountSummaryXML : "+ajaxAccountSummaryXML);
			
			HashMap<String, HashMap<String, ArrayList<String>>> ajaxFirememResponseColleection = fetchAjaxResponse(ajaxFirememResponse);
			FirememExtractedAjaxResponse firememExtractedAjaxResponse = new FirememExtractedAjaxResponse();
			firememExtractedAjaxResponse.setAjaxAccountSummaryXML(ajaxAccountSummaryXML);
			firememExtractedAjaxResponse.setAjaxFirememResponseColleection(ajaxFirememResponseColleection);
			firememExtractedAjaxResponse.setAjaxFirememXML(ajaxFirememXML);
			firememExtractedAjaxResponse.setAjaxfmCode(ajaxFmCode);
			firememExtractedAjaxResponse.setAjaxfmLatency(ajaxFmLatency);
			firememExtractedAjaxResponse.setAjaxDumpUrl(ajaxDumpUrlToSend);
			firememExtractedAjaxResponse.setjDapAccountSummaryXML(jDapAccountSummaryXML);
			firememExtractedAjaxResponse.setjDapFirememXML(jDapFirememXML);
			firememExtractedAjaxResponse.setjDapfmCode(jDapFmCode);
			firememExtractedAjaxResponse.setjDapfmLatency(jDapFmLatency);
			firememExtractedAjaxResponse.setJdapDumpUrl(jDApDumpUrlToSend);

			firememExtractedAjaxResponseListMap.put(jDapItemId,firememExtractedAjaxResponse);
		}
		return firememExtractedAjaxResponseListMap;
	}

	public ArrayList<FirememExtractedResponseForDocumentDownload> retriveDataFromFirememForDocDownload(HashMap<String, HashMap<String,Object>> jDapItemListFromBatch, DocDownloadVO ddvo)
			throws JSONException, IOException {
		logger.info("######################################### retriveDataFromFirememForDocDownload #####################################");
		ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);

		ArrayList<FirememExtractedResponseForDocumentDownload> docDownloadResponseMap= new ArrayList<>();

		System.out.println("Length of itemlist for DocDownload: "+jDapItemListFromBatch.size());

		for(String jDapItemId: jDapItemListFromBatch.keySet()) {
			logger.info("Item ID :"+jDapItemId);
			HashMap<String, Object> jDapItemDetailsMap = jDapItemListFromBatch.get(jDapItemId);
			Object jDapDumpUrlObj=jDapItemDetailsMap.get("dumpUrl");
			String jDApDumpUrl="";
			if(jDapDumpUrlObj==null) {
				jDApDumpUrl="";
			}else {
				jDApDumpUrl= (String)jDapDumpUrlObj;
			}
			String jDApDumpUrlToSend=jDApDumpUrl;
			if(jDApDumpUrl!=null)
				jDApDumpUrl =modifiedFirememDumpLink(jDApDumpUrl);

			Object jDApFmLatencyObj=jDapItemDetailsMap.get("fmLatency");
			String jDapFmLatency="";

			if(jDApFmLatencyObj != null) {
				jDapFmLatency= Integer.toString((Integer)jDApFmLatencyObj);
			}

			Object jDapFmCodeObj=jDapItemDetailsMap.get("fmCode");
			String jDapFmCode="";

			if(jDapFmCodeObj != null) {
				jDapFmCode= Integer.toString((Integer)jDapFmCodeObj);
			}

			String itemType = jDapItemDetailsMap.get("itemType").toString();

			// JDAP Firemem Access and retrive
			String jDapFirememResponse= restTemplate.getForObject(jDApDumpUrl, String.class);

			String jDapFirememXML = fetchFinalSiteXML(jDapFirememResponse);
			if(jDapFirememXML==null){
				continue;
			}
			logger.info("JDAP FIRMEM => jDapFirememXML : "+jDapFirememXML);
			String jDapAccountSummaryXML=getAccountSummaryXML(jDapFirememResponse);
			logger.info("JDAP FIRMEM => jDapAccountSummaryXML : "+jDapAccountSummaryXML);

			FirememExtractedResponseForDocumentDownload docResponse = new FirememExtractedResponseForDocumentDownload();
			docResponse.setJdapXMLResponse(jDapFirememXML);
			docResponse.setErrorCode(jDapFmCode);
			docResponse.setItemType(itemType);
			docResponse.setJdapDumpUrl(jDApDumpUrlToSend);
			docResponse.setItemId(jDapItemId);
			docResponse.setMigId(ddvo.getMigId());
			if(!rpaldaRepository.isNullValue(jDapFirememXML) && jDapFirememXML.contains("DOC_DOWNLOADED") && jDapFirememXML.contains("<documentId>")) {
				docResponse.setDocPresent(true);
			}else{
				docResponse.setDocPresent(false);
			}

			docDownloadResponseMap.add(docResponse);
		}
		return docDownloadResponseMap;
	}

	public ArrayList<FirememExtractedResponseForTSD> retriveDataFromFirememForTSD(HashMap<String, HashMap<String,Object>> jDapItemListFromBatch, String tsd, TransactionSelectionDurationVO tsdvo)
			throws Exception {
		logger.info("######################################### retriveDataFromFiremem#########################################");
		ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);

		ArrayList<FirememExtractedResponseForTSD> TSDResponseMap= new ArrayList<>();

		for(String jDapItemId: jDapItemListFromBatch.keySet()) {
			long lastDiffDays = 0;
			String oldestTxnDate = null, lastOldestTxnDate=null;
			logger.info("Item ID :"+jDapItemId);
			HashMap<String, Object> jDapItemDetailsMap = jDapItemListFromBatch.get(jDapItemId);
			Object jDapDumpUrlObj=jDapItemDetailsMap.get("dumpUrl");
			String jDApDumpUrl="";
			if(jDapDumpUrlObj==null) {
				jDApDumpUrl="";
			}else {
				jDApDumpUrl= (String)jDapDumpUrlObj;
			}
			String jDApDumpUrlToSend=jDApDumpUrl;
			if(jDApDumpUrl!=null)
				jDApDumpUrl =modifiedFirememDumpLink(jDApDumpUrl);

			Object jDApFmLatencyObj=jDapItemDetailsMap.get("fmLatency");
			String jDapFmLatency="";

			if(jDApFmLatencyObj != null) {
				jDapFmLatency= Integer.toString((Integer)jDApFmLatencyObj);
			}

			Object jDapFmCodeObj=jDapItemDetailsMap.get("fmCode");
			String jDapFmCode="";



			if(jDapFmCodeObj != null) {
				jDapFmCode= Integer.toString((Integer)jDapFmCodeObj);
			}

			String itemType = jDapItemDetailsMap.get("itemType").toString();

			String jDapFirememResponse = null;
			try {
				// JDAP Firemem Access and retrive
				jDapFirememResponse = restTemplate.getForObject(jDApDumpUrl, String.class);
			}catch (Exception e){
				System.out.println("Unable to retrieve firemem for this user"+e);
				continue;
			}

			String jDapFirememXML = fetchFinalSiteXML(jDapFirememResponse);
			String jDapAccountSummaryXML=getAccountSummaryXML(jDapFirememResponse);

			FirememExtractedResponseForTSD TSDResponse = new FirememExtractedResponseForTSD();
			TSDResponse.setJdapXMLResponse(jDapFirememXML);
			TSDResponse.setErrorCode(jDapFmCode);
			TSDResponse.setJdapDumpUrl(jDApDumpUrlToSend);
			TSDResponse.setItemType(itemType);
			TSDResponse.setItemId(jDapItemId);
			TSDResponse.setMigId(tsdvo.getMigId());

			if(!rpaldaRepository.isNullValue(jDapFirememXML)) {
				String finalXml=jDapFirememXML;
				System.out.println("Printing FinalXml: "+finalXml);
				List<String> occurenceCount = new ArrayList();
				if(finalXml.contains("<oldestTxnDate") && finalXml.contains("</oldestTxnDate>")) {
					occurenceCount = countOccurences(finalXml, "oldestTxnDate");
				}else if(finalXml.contains("<postDate") && finalXml.contains("</postDate>")) {
					occurenceCount = countOccurences(finalXml, "postDate");
				}else if(finalXml.contains("<date") && finalXml.contains("</date>")) {
					occurenceCount = countOccurences(finalXml, "date");
				}

				System.out.println("Checking the no. of accounts in TSD: "+occurenceCount.size());
				for(String oldestTxnNew : occurenceCount) {

					System.out.println("oldest=="+oldestTxnNew);

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					Date oldestTxn = sdf.parse(oldestTxnNew);
					Date eDate = new Date();

					long diffOfDays=Math.abs(eDate.getTime()/(1000*24*60*60)-oldestTxn.getTime()/(1000*24*60*60));

					System.out.println("==diff=="+diffOfDays);

					if(lastDiffDays < diffOfDays) {
						lastDiffDays = diffOfDays;
						lastOldestTxnDate = oldestTxnNew;
					}

				}

				if(lastOldestTxnDate != null) {
					if(lastDiffDays > (Integer.parseInt(tsd) - 10)) {
						TSDResponse.setTsdGenuine(true);
						TSDResponse.setIsTSDPresent(Long.toString(lastDiffDays));
					}else {
						TSDResponse.setTsdGenuine(false);
						TSDResponse.setIsTSDPresent(Long.toString(lastDiffDays));
					}

				}else {
					TSDResponse.setTsdGenuine(false);
					TSDResponse.setIsTSDPresent("Need to verify...!! New variation...!");
				}

			}

			TSDResponseMap.add(TSDResponse);
		}
		return TSDResponseMap;
	}


	public String getAllDataFromFiremem(String firememResponse) {
		// TODO Auto-generated method stub
		// Retrive the site XMl and format
		firememResponse = firememResponse.substring(firememResponse.indexOf("Site XML Validation")+"Site XML Validation".length(),firememResponse.lastIndexOf("</PRE>"));
		firememResponse = firememResponse.substring(firememResponse.indexOf("<PRE>")+ "<PRE>".length());
		firememResponse= firememResponse.replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&quot;","\"");
		firememResponse.replaceAll("&#10;"," ");
		firememResponse= firememResponse.replaceAll("&#10;"," ");


		HashMap<String, HashMap<String, ArrayList<String>>> firememResponseColleection= new HashMap<String, HashMap<String, ArrayList<String>>>();
		do {

			String ajaxResponse = firememResponse.substring(firememResponse.indexOf("AJAX_RESPONSE_FOR_TOOL:") + "AJAX_RESPONSE_FOR_TOOL:".length());
			firememResponse=ajaxResponse;
			ajaxResponse=ajaxResponse.substring(0,ajaxResponse.indexOf("</td>"));
			//System.out.println(ajaxResponse);
			String accountNumber =  ajaxResponse.substring(0,ajaxResponse.indexOf("|"));
			String state =  ajaxResponse.substring(ajaxResponse.indexOf("|")+1);
			System.out.println("dfdsg"+state);
			state =  state.substring(0,state.indexOf("|"));
			String toolIden_acctNum_State= "|"+accountNumber+"|"+state+"|";
			System.out.println(state);
			String ajaxRes= ajaxResponse.substring(ajaxResponse.indexOf(toolIden_acctNum_State)+toolIden_acctNum_State.length());

			ArrayList<String> firememResList= null;
			HashMap<String, ArrayList<String>> stateResponseMap=null;

			if(firememResponseColleection.containsKey(accountNumber)){
				stateResponseMap= firememResponseColleection.get(accountNumber);
				if(stateResponseMap.containsKey(state)){
					firememResList = stateResponseMap.get(state);
					firememResList.add(ajaxRes);
					stateResponseMap.put(state,firememResList);
					firememResponseColleection.put(accountNumber,stateResponseMap);
				}else{
					firememResList = new ArrayList<String>();
					firememResList.add(ajaxRes);
					stateResponseMap.put(state,firememResList);
					firememResponseColleection.put(accountNumber,stateResponseMap);
				}
			}else {

				firememResList = new ArrayList<String>();
				firememResList.add(ajaxRes);
				stateResponseMap = new  HashMap<String, ArrayList<String>>();
				stateResponseMap.put(state,firememResList);
				firememResponseColleection.put(accountNumber,stateResponseMap);
			}


		}while (firememResponse.contains("AJAX_RESPONSE_FOR_TOOL:"));


		return null;
	}

	public HashMap<String, HashMap<String, ArrayList<String>>> fetchAjaxResponse(String firememResponse) {

		String firememResponseTemp=firememResponse;

		HashMap<String, HashMap<String, ArrayList<String>>> firememResponseColleection= new HashMap<String, HashMap<String, ArrayList<String>>>();
		List<Map<String, List<Map<String, List<Map<String, String>>>>>> ajaxResposneCollection= new ArrayList<Map<String, List<Map<String, List<Map<String, String>>>>>>();
		if(firememResponseTemp.contains("AJAX_RESPONSE_FOR_TOOL:")) {
			do {
				String ajaxResponse = firememResponseTemp.substring(firememResponseTemp.indexOf("AJAX_RESPONSE_FOR_TOOL:") + "AJAX_RESPONSE_FOR_TOOL:".length());
				firememResponseTemp=ajaxResponse;
				ajaxResponse=ajaxResponse.substring(0,ajaxResponse.indexOf("</td>"));
				//System.out.println(ajaxResponse);
				String accountNumber =  ajaxResponse.substring(0,ajaxResponse.indexOf("|"));
				String state =  ajaxResponse.substring(ajaxResponse.indexOf("|")+1);
				//System.out.println("dfdsg"+state);
				state =  state.substring(0,state.indexOf("|"));
				String toolIden_acctNum_State= "|"+accountNumber+"|"+state+"|";
				//System.out.println(accountNumber+" :sate while retriving the data:"+state);
				String ajaxRes= ajaxResponse.substring(ajaxResponse.indexOf(toolIden_acctNum_State)+toolIden_acctNum_State.length());

				ArrayList<String> firememResList= null;
				HashMap<String, ArrayList<String>> stateResponseMap=null;

				if(firememResponseColleection.containsKey(accountNumber)){
					stateResponseMap= firememResponseColleection.get(accountNumber);
					if(stateResponseMap.containsKey(state)){
						firememResList = stateResponseMap.get(state);
						firememResList.add(ajaxRes);
						stateResponseMap.put(state,firememResList);
						firememResponseColleection.put(accountNumber,stateResponseMap);
					}else{
						firememResList = new ArrayList<String>();
						firememResList.add(ajaxRes);
						stateResponseMap.put(state,firememResList);
						firememResponseColleection.put(accountNumber,stateResponseMap);
					}
				}else {
					//List<Map<String, List<Map<String, List<Map<String, String>>>>>>
					firememResList = new ArrayList<String>();
					firememResList.add(ajaxRes);

					/*HashMap firememResMap = new HashMap<String, ArrayList<String>>();
					firememResMap.put("ajaxResponseList", firememResList);
					firememResMap.put("stateName", state);
					ArrayList<HashMap<String, ArrayList<String>>> statsList= new ArrayList<HashMap<String, ArrayList<String>>>();
					 */
					stateResponseMap = new  HashMap<String, ArrayList<String>>();

					stateResponseMap.put(state,firememResList);
					firememResponseColleection.put(accountNumber,stateResponseMap);
				}


			}while (firememResponseTemp.contains("AJAX_RESPONSE_FOR_TOOL:"));
		}

		return firememResponseColleection;
	}

	public String getAccountSummaryXML(String firememResponse) {
		String accountSummaryXMl =null;
		if(firememResponse.contains("ACCOUNT_SUMMARY_XML::")) {
			accountSummaryXMl=firememResponse.substring(firememResponse.indexOf("ACCOUNT_SUMMARY_XML::")+"ACCOUNT_SUMMARY_XML::".length());
			accountSummaryXMl=accountSummaryXMl.substring(0,accountSummaryXMl.indexOf("</td>"));
			if(accountSummaryXMl.contains("<PRE>")){
				accountSummaryXMl = accountSummaryXMl.substring(accountSummaryXMl.indexOf("<PRE>")+"<PRE>".length(),accountSummaryXMl.lastIndexOf("</PRE>"));
			}
			//System.out.println(accountSummaryXMl);
		}
		return accountSummaryXMl;
	}
	
	public String fetchFinalSiteXML(String firememResponse) {
		String firememXML=null;
		String firstIdentifier="ACCOUNT_SUMMARY_XML::";
		String secondIdentifier="2^^^^^^^^^^^^^^^^^^^^^^^^^^^^site";

		//System.out.println("Printing the Firemem Response for Testing Purpose: "+firememResponse);

		if(firememResponse.contains("Site XML Validation")) {
			firememXML = firememResponse.substring(firememResponse.indexOf("Site XML Validation")+"Site XML Validation".length(),firememResponse.lastIndexOf("</PRE>"));
			firememXML = firememXML.substring(firememXML.indexOf("<PRE>")+ "<PRE>".length());
			firememXML= firememXML.replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&quot;","\"");
			// firememXML.replaceAll("&gt;",">");
			//firememXML.replaceAll("&quot;","\"");
			//firememXML.replaceAll("&#10;"," ");
			firememXML= firememXML.replaceAll("&#10;"," ");
		}else if(firememResponse.contains(secondIdentifier)){
			firememXML=firememResponse.substring(firememResponse.indexOf(secondIdentifier)+secondIdentifier.length(),firememResponse.lastIndexOf("</PRE>"));
			firememXML = firememXML.substring(firememXML.indexOf("<PRE>")+ "<PRE>".length());
			firememXML= firememXML.replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&quot;","\"");
			//firememXML.replaceAll("&#10;"," ");
			firememXML= firememXML.replaceAll("&#10;"," ");
		}else if(firememResponse.contains("<site name=")){
			if(!firememResponse.contains("</site>")){
				System.out.println("Skipping user as firememResponse is empty");
				return firememXML;
			}
			firememXML=firememResponse.substring(firememResponse.indexOf("<site name=")+secondIdentifier.length(),firememResponse.indexOf("</site>"));
			firememXML= firememXML.replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&quot;","\"");
			//firememXML.replaceAll("&#10;"," ");
			firememXML= firememXML.replaceAll("&#10;"," ");
		}
		//System.out.println(firememXML);
		return firememXML;
	}

	public  String modifiedFirememDumpLink(String dumpLink) {
		if (dumpLink.contains("dumpdispatcher")) {
			String modifiedDumpLink = dumpLink.replaceAll("\\?id=", "/").replaceAll("dumpdispatcher", "downloads");
			String shortenedDump = modifiedDumpLink.substring(0, modifiedDumpLink.indexOf(".html") + 5);
			return shortenedDump;
		}
		return dumpLink;
	}

	public static List<String> countOccurences(String xml, String tag) throws Exception
	{
		List<String> count = new ArrayList<>();

		Document doc = Jsoup.parse(xml, "", Parser.xmlParser());

		for (Element e : doc.select(tag)) {
			count.add(e.text());
		}

		return count;
	}

	public static int ordinalIndexOf(String str, String substr, int n) {
		int pos = -1;
		do {
			pos = str.indexOf(substr, pos + 1);
		} while (n-- > 0 && pos != -1);
		return pos;
	}

}

