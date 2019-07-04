package com.yodlee.docdownloadandtsd.Utility;

/**

 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.

 *

 * This software is the confidential and proprietary information of Yodlee, Inc.

 * Use is subject to license terms.

 *

 */

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yodlee.docdownloadandtsd.VO.YuvaPojo;
import com.yodlee.docdownloadandtsd.VO.YuvaPojo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


/**
 * @author MMahto
 *
 */

public class HammerServices {


	@Value("${hammer.url}")
	private String HAMMER_FIREMEM_AUTH_URL;

	@Value("${hammer.batchCreation.url}")
	private String BATCH_CREATION_URL;

	@Value("${hammer.batchTrigger.url}")
	private String BATCH_TRIGGER_URL;

	@Value("${hammer.batchPolling.url}")
	private String BATCH_POLLING_URL;

	@Value("${hammer.firememRoute}")
	private String HAMMER_ROUTE;
	
	@Value("${hammer.UserId}")
	private String hammerUserName;

	@Value("${hammer.Password}")
	private String hammerPassword;

	public HttpHeaders getHeader(String tokenId){
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		if(tokenId != null) {
			header.set("Authorization",tokenId);
		}
		return header;
	}

	public String hammerLogin(String username, byte[] password) throws JsonParseException, IOException {
		System.out.println(hammerUserName);
		RestTemplate restTemplate = new RestTemplate();
		password=Base64.getDecoder().decode(password);

		String decode = new String(password, StandardCharsets.UTF_8);

		HttpHeaders headers = getHeader(null);
		HashMap<String, String> hammerRequestBody = new HashMap<String, String>();
		hammerRequestBody.put("username", username);
		hammerRequestBody.put("password", decode);
		//String yuvaAuthenticationURL = "http://172.17.15.21:8888/R/A/L";
		HttpEntity<Map<String, String>> hammerRequest = new HttpEntity<Map<String, String>>(hammerRequestBody, headers);

		//System.out.println("YuvaAuthenticationService : request" + request.getBody());
		//System.out.println("YuvaAuthenticationService : URL" + yuvaAuthenticationURL);
System.out.println("HAMMER_FIREMEM_AUTH_URL is " +HAMMER_FIREMEM_AUTH_URL);
		String hammerResponse = restTemplate.postForObject(HAMMER_FIREMEM_AUTH_URL, hammerRequest, String.class);

		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = mapper.getFactory();
		JsonParser parser = factory.createParser(hammerResponse);
		JsonNode actualObj = mapper.readTree(parser);
		System.out.println("Hammer login : >>>response :" + actualObj);
		String authTokenId = actualObj.get("token").toString();
		authTokenId = authTokenId.replaceAll("\"", "");

		return authTokenId;

	}
	public String getAuthTokenId(String username, byte[] password) throws JsonParseException, IOException {
		RestTemplate restTemplate = new RestTemplate();
		password=Base64.getDecoder().decode(password);

		String decode = new String(password, StandardCharsets.UTF_8);

		HttpHeaders headers = getHeader(null);
		HashMap<String, String> hammerRequestBody = new HashMap<String, String>();
		hammerRequestBody.put("username", username);
		hammerRequestBody.put("password", decode);
		//String yuvaAuthenticationURL = "http://172.17.15.21:8888/R/A/L";
		HttpEntity<Map<String, String>> hammerRequest = new HttpEntity<Map<String, String>>(hammerRequestBody, headers);

		//System.out.println("YuvaAuthenticationService : request" + request.getBody());
		//System.out.println("YuvaAuthenticationService : URL" + yuvaAuthenticationURL);

		String hammerResponse = restTemplate.postForObject(HAMMER_FIREMEM_AUTH_URL, hammerRequest, String.class);

		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = mapper.getFactory();
		JsonParser parser = factory.createParser(hammerResponse);
		JsonNode actualObj = mapper.readTree(parser);
		System.out.println("Hammer login : >>>response :" + actualObj);
		String authTokenId = actualObj.get("token").toString();
		authTokenId = "Bearer "+authTokenId.replaceAll("\"", "");
		return authTokenId;

	}

	public String createBatch(YuvaPojo[] yuvaPojo, String authTokenId, String agentName) throws JsonParseException, IOException, JSONException, InterruptedException {
		RestTemplate restTemplate = new RestTemplate();

		//String authTokenId= hammerLogin(username, password);


		JSONObject batchResponse = new JSONObject();
		HashMap<String, Object> batchCreationRequestBody = new HashMap<String, Object>();
		HashMap<String, Object> batchCreationItemMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, String>> itemDetailList= new ArrayList<HashMap<String, String>>();

int count =0;
		for( YuvaPojo yuvaItemDetails: yuvaPojo) {
			count++;
			HashMap<String, String> itemDetailMap = new HashMap<String, String>();
			itemDetailMap.put("itemId", yuvaItemDetails.getCacheItemId());
			itemDetailMap.put("dbName", yuvaItemDetails.getdataBase());
			itemDetailMap.put("description","string" );
			itemDetailMap.put("itemType", "2");
			itemDetailList.add(itemDetailMap);
			if(count>6) {
				break;
			}
		}

		batchCreationItemMap.put("Add", itemDetailList);
		batchCreationRequestBody.put("items", batchCreationItemMap);
		batchCreationRequestBody.put("agentName",agentName);
		batchCreationRequestBody.put("description","description "+agentName);
		batchCreationRequestBody.put("nickName","nick Name "+agentName);

		System.out.println(batchCreationRequestBody);
		HttpHeaders headers= getHeader(authTokenId);

		HttpEntity<Map<String, Object>> requestEntityForBatchCreation = new HttpEntity<Map<String, Object>>(batchCreationRequestBody,headers);
		String batchCreationResponse = restTemplate.postForObject(BATCH_CREATION_URL, requestEntityForBatchCreation, String.class);


		JSONObject batchCreatonResponseObject = new JSONObject(batchCreationResponse);

		int batchDetailsId = batchCreatonResponseObject.getInt("batchDetailsId");


		// trigger batch

		Map<String, Object> batchtriggerBody = new HashMap<>();
		HashMap<String, Object> batchRefreshParams = new HashMap<String, Object>();

		batchRefreshParams.put("serverType", "I");
		batchRefreshParams.put("customrefreshRoute", "C");
		batchRefreshParams.put("agentFileType", "JAVA");
		
		batchRefreshParams.put("customRoute", HAMMER_ROUTE);


		batchtriggerBody.put("batchRefreshParams", batchRefreshParams);
		batchtriggerBody.put("batchDetailsId", batchDetailsId);

		HttpEntity<Map<String, Object>> batchTriggerRequestEntity = new HttpEntity<Map<String, Object>>(batchtriggerBody, headers);

		String batchTriggerResponse = restTemplate.postForObject(BATCH_TRIGGER_URL, batchTriggerRequestEntity, String.class);

		JSONObject triggerBatchResponse = new JSONObject(batchTriggerResponse);

		System.out.println("trigger batch resp" + triggerBatchResponse);
		if (triggerBatchResponse.getInt("batchStatus") == 4) {

			// batchStatus = "4";
			//message = "Batch trigger failed";
			batchResponse.put("batchStatus", "4");
			batchResponse.put("batchMessage", "Batch trigger failed");

			return batchResponse.toString();
		}else {

			// batchStatus = "5";
			Map<String, Object> batchpollingBody = new HashMap<>();
			//batchpollingBody.put(key, value)
			int batchRequestId = triggerBatchResponse.getInt("batchReqDetailsId");
			HttpEntity<Integer> batchPollingRequestEntity = new HttpEntity<Integer>(batchRequestId,headers);

			String pollingResponse = null;
			int j = 0;
			pollingResponse = restTemplate.postForObject(BATCH_POLLING_URL, batchPollingRequestEntity, String.class);

			JSONObject polledResponse = new JSONObject(pollingResponse);

			// batchDetail batchReqDetailList

			while (j != 120 || polledResponse.getJSONArray("batchReqDetailList").getJSONObject(0)
					.getJSONObject("batchDetail").getInt("batchStatusId") != 5) {
				
				Thread.sleep(10000);

				pollingResponse = restTemplate.postForObject(BATCH_POLLING_URL, batchPollingRequestEntity, String.class);
				polledResponse = new JSONObject(pollingResponse);
				j++;
			}

			System.out.println("polling response" + pollingResponse);

			JSONArray batchResultArray = polledResponse.getJSONArray("batchResultList");

			if (batchResultArray.length() > 0) {
				JSONArray SuccessFuldumpsArray = new JSONArray();
				JSONArray CompleteBatchItemInfo = new JSONArray();
				for (int i = 0; i < batchResultArray.length(); i++) {

					JSONObject resultDumpObject = batchResultArray.getJSONObject(i);
					System.out.println("result dump object" + resultDumpObject);
					if (!resultDumpObject.optString("dumpUrl").replace("null", "").isEmpty()) {

						JSONObject tempD = new JSONObject();
						tempD.put("dumpUrl", resultDumpObject.optString("dumpUrl"));
						tempD.put("itemId", resultDumpObject.getInt("itemId"));
						tempD.put("dbName", resultDumpObject.getString("dbName"));
						tempD.put("fmCode", resultDumpObject.getInt("fmCode"));
						tempD.put("scriptName", resultDumpObject.getString("scriptName"));
						tempD.put("fmLatency", resultDumpObject.getInt("fmLatency"));
						if (resultDumpObject.getInt("fmCode") == 0) {
							SuccessFuldumpsArray.put(resultDumpObject.optString("dumpUrl"));
						}
						CompleteBatchItemInfo.put(tempD);

					}

				}
				
				batchResponse.put(agentName, SuccessFuldumpsArray);
				batchResponse.put(agentName + "_CompleteInfo", SuccessFuldumpsArray);
				/*if (SuccessFuldumpsArray.length() == 0) {
					batchStatus = "4";

				}*/
				batchResponse.put("batchStatus", "5");
				batchResponse= new JSONObject();
				batchResponse.put("batchRequestId", batchRequestId);

			}
			return batchResponse.toString();

		}


	}
}

