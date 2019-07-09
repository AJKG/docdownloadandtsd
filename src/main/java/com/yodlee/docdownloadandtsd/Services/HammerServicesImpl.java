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
import com.yodlee.docdownloadandtsd.VO.FirememExtractedAjaxResponse;
import com.yodlee.docdownloadandtsd.VO.FirememExtractedResponseForDocumentDownload;
import com.yodlee.docdownloadandtsd.VO.ItemDetailsVO;
import com.yodlee.docdownloadandtsd.authenticator.Authorization;
import com.yodlee.docdownloadandtsd.exceptionhandling.GeneralErrorHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.*;

@Service
public class HammerServicesImpl {


	@Autowired

	Authorization authorization;

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
		System.out.println("pssss"+password);

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

	public Integer createBatch(ItemDetailsVO[] yuvaPojo, String accessTokenId, String agentName)
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
			itemDetailMap.put("itemId", yuvaItemDetails.getMemSiteAccId());
			itemDetailMap.put("dbName", yuvaItemDetails.getDataBase());
			itemDetailMap.put("description","Ajax Batch Testing");
			itemDetailMap.put("itemType", "3");
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

	public Integer triggerBatch(Integer batchDetailsId,String accessTokenId, String customrefreshRoute, String customRoute)
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
				Thread.sleep(7000);
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
						//System.out.println("isBatchRefreshComplete is "+isBatchRefreshComplete);
						isBatchRefreshComplete=false;
						//itemListmap.put(itemId, true);
					}
					
				}

			}

			poolingCount++;
			System.out.println("poolingCount "+poolingCount);
			poolingCount++;

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
			logger.info("JDAP FIRMEM => jDapFirememXML : "+jDapFirememXML);
			String jDapAccountSummaryXML=getAccountSummaryXML(jDapFirememResponse);
			logger.info("JDAP FIRMEM => jDapAccountSummaryXML : "+jDapAccountSummaryXML);

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

	public HashMap<String,Object> retriveDataFromFirememForDocDownload(HashMap<String, HashMap<String,Object>> jDapItemListFromBatch)
			throws JSONException, IOException {
		logger.info("######################################### retriveDataFromFiremem #########################################");
		ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);

		HashMap<String,Object> docDownloadResponseMap= new HashMap<String,Object>();

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

			// JDAP Firemem Access and retrive
			String jDapFirememResponse= restTemplate.getForObject(jDApDumpUrl, String.class);

			String jDapFirememXML = fetchFinalSiteXML(jDapFirememResponse);
			logger.info("JDAP FIRMEM => jDapFirememXML : "+jDapFirememXML);
			String jDapAccountSummaryXML=getAccountSummaryXML(jDapFirememResponse);
			logger.info("JDAP FIRMEM => jDapAccountSummaryXML : "+jDapAccountSummaryXML);


			FirememExtractedResponseForDocumentDownload docResponse = new FirememExtractedResponseForDocumentDownload();
			docResponse.setJdapXMLResponse(jDapFirememXML);
			docResponse.setErrorCode(jDapFmCode);
			docResponse.setJdapDumpUrl(jDApDumpUrlToSend);
			if(jDapFirememXML.contains("DOC_DOWNLOADED") && jDapFirememXML.contains("<documentId>")) {
				docResponse.setDocPresent(true);
			}else{
				docResponse.setDocPresent(false);
			}

			docDownloadResponseMap.put(jDapItemId,docResponse);
		}
		return docDownloadResponseMap;
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
		if(firememResponse.contains("Site XML Validation")) {
			firememXML = firememResponse.substring(firememResponse.indexOf("Site XML Validation")+"Site XML Validation".length(),firememResponse.lastIndexOf("</PRE>"));
			firememXML = firememXML.substring(firememXML.indexOf("<PRE>")+ "<PRE>".length());
			firememXML= firememXML.replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&quot;","\"");
			// firememXML.replaceAll("&gt;",">");
			//firememXML.replaceAll("&quot;","\"");
			firememXML.replaceAll("&#10;"," ");

			firememXML= firememXML.replaceAll("&#10;"," ");
		}else if(firememResponse.contains(secondIdentifier)){
			firememXML=firememResponse.substring(firememResponse.indexOf(secondIdentifier)+secondIdentifier.length(),firememResponse.lastIndexOf("</PRE>"));
			firememXML = firememXML.substring(firememXML.indexOf("<PRE>")+ "<PRE>".length());
			firememXML= firememXML.replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&quot;","\"");
			firememXML.replaceAll("&#10;"," ");
			firememXML= firememXML.replaceAll("&#10;"," ");
		}else if(firememResponse.contains("<site name=")){
			firememXML=firememResponse.substring(firememResponse.indexOf("<site name=")+secondIdentifier.length(),firememResponse.indexOf("</site>"));
			firememXML= firememXML.replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&quot;","\"");
			firememXML.replaceAll("&#10;"," ");
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
}

