package com.yodlee.docdownloadandtsd.DAO;

/**
 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc.
 * Use is subject to license terms.
 *
 */

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yodlee.docdownloadandtsd.Services.DumpReadService;
import com.yodlee.docdownloadandtsd.VO.ItemDetailsVO;
import com.yodlee.docdownloadandtsd.VO.SplunkItemDetailsVO;
import com.yodlee.docdownloadandtsd.authenticator.Authorization;
import com.yodlee.docdownloadandtsd.splunk.SSLSecurityProtocol;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SplunkRepository {

	@Autowired
	DumpReadService dumpReadService;

	@Autowired
	DBAccessRepositoryImpl dbAccessRepository;


	@Inject
	Authorization authorization;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${splunk.UserId}")
	private String splunkUserId;

	@Value("${splunk.Password}")
	private String splunkPassword;

	@Value("${getagentnamequery}")
	private String agentName_query;

	LocalDateTime lastRefreshed = null;

	private static String REST_URL = "https://splunkapi.yodlee.com/";

	private static String splunkSession = null;

	static SSLSocketFactory delegate;

	static SSLSecurityProtocol sslSecurityProtocol1;

	protected static SSLSecurityProtocol sslSecurityProtocol = SSLSecurityProtocol.TLSv1_2;

	private static SSLSocketFactory sslSocketFactory = createSSLFactory();

	private static final HostnameVerifier HOSTNAME_VERIFIER = new HostnameVerifier() {
		public boolean verify(String s, SSLSession sslSession) {
			return true;
		}
	};

	public static Map<String, String> dbMapping = new HashMap<String, String>();
	static {
		dbMapping.put("dbcana011","dbcana01s");
		dbMapping.put("scexag071","scexag07s");
		dbMapping.put("exayod1","exayods");
		dbMapping.put("auspr04","auspr04s");
		dbMapping.put("auspr011","auspr01s");
		dbMapping.put("inpra011","inprda01s");
		dbMapping.put("auspr031","auspr03s");
		dbMapping.put("scexpr011","scexpr01s");
		dbMapping.put("yoddemo1","yoddemos");
	}

	public void login() {

		System.out.println("Inside login method.");

		String url = REST_URL + "services/auth/login";

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

		System.out.println("username Splunk : " + splunkUserId);

		String splunkPasswordTemp=authorization.decrypt(splunkPassword);


		System.out.println("Password for Splunk: "+splunkPasswordTemp);


		params.add("username", splunkUserId);
		params.add("password", splunkPasswordTemp);

		String response = post(url, params);

		splunkSession = response.substring(response.indexOf("<sessionKey>") + 12, response.lastIndexOf("</sessionKey>"))
				.trim();

		System.out.println("new session created :" + splunkSession);
	}

	public static String post(String url, MultiValueMap<String, String> params) {

		HttpsURLConnection.setDefaultHostnameVerifier(HOSTNAME_VERIFIER);

		HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		requestHeaders.add("Authorization", "Splunk " + splunkSession);

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params,
				requestHeaders);

		ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

		return response.getBody().toString();
	}

	public static SSLSocketFactory createSSLFactory() {

		TrustManager[] trustAll = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {

			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {

			}

		} };

		try {

			SSLContext context;

			switch (sslSecurityProtocol) {

			case TLSv1_2:

			case TLSv1_1:

			case TLSv1:
				context = SSLContext.getInstance("TLS");
				break;

			default:
				context = SSLContext.getInstance("SSL");
			}

			context.init(null, trustAll, new java.security.SecureRandom());

			return new SplunkHttpsSocketFactory(context.getSocketFactory(), sslSecurityProtocol);

		} catch (Exception e) {
			throw new RuntimeException("Error setting up SSL socket factory: " + e, e);
		}
	}

	private static final class SplunkHttpsSocketFactory extends SSLSocketFactory {

		private final SSLSocketFactory delegate;

		private SSLSecurityProtocol sslSecurityProtocol;

		private SplunkHttpsSocketFactory(SSLSocketFactory delegate) {
			this.delegate = delegate;
		}

		private SplunkHttpsSocketFactory(SSLSocketFactory delegate, SSLSecurityProtocol securityProtocol) {

			this.delegate = delegate;
			this.sslSecurityProtocol = securityProtocol;
		}

		private Socket configure(Socket socket) {

			if (socket instanceof SSLSocket) {
				((SSLSocket) socket).setEnabledProtocols(new String[] { sslSecurityProtocol.toString() });
			}

			return socket;
		}

		@Override
		public String[] getDefaultCipherSuites() {
			return delegate.getDefaultCipherSuites();
		}

		@Override
		public String[] getSupportedCipherSuites() {
			return delegate.getSupportedCipherSuites();
		}

		@Override
		public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
			return configure(delegate.createSocket(socket, s, i, b));
		}

		@Override
		public Socket createSocket() throws IOException {
			return configure(delegate.createSocket());
		}

		@Override
		public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
			return configure(delegate.createSocket(s, i));
		}

		@Override
		public Socket createSocket(String s, int i, InetAddress inetAddress, int i1)
				throws IOException, UnknownHostException {
			return configure(delegate.createSocket(s, i, inetAddress, i1));
		}

		@Override
		public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
			return configure(delegate.createSocket(inetAddress, i));
		}

		@Override
		public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1)
				throws IOException {
			return configure(delegate.createSocket(inetAddress, i, inetAddress1, i1));
		}
	}

	public ItemDetailsVO[] getItems(String agentName) throws Exception {

		System.out.println("splunkSession at the top  :" + splunkSession);

		if (splunkSession == null)
			login();

		String queryString = readQuery("src/main/java/com/yodlee/Launcher/query_items.TXT");
		queryString = queryString.replace("#agentName#", agentName);

		String duration = "-1d@h";
		String sid = getSid(false, queryString, duration);
		String url = REST_URL + "/services/search/jobs/" + sid + "/results?output_mode=json&count=5";

		System.out.println("url:" + url);
		System.out.println("splunkSession :" + splunkSession);

		Map<String, String> params = new HashMap<String, String>();
		params.put("Authorization", "Splunk " + splunkSession);

		String response = get(url, params);
		System.out.println("Providing Response from Splunk Repository"+response);
		response = response.substring(response.indexOf("results") + "results".length() + 2);

		ItemDetailsVO[] itemDetails = new ObjectMapper().readValue(response, ItemDetailsVO[].class);

		for (ItemDetailsVO itemDetailsVO : itemDetails) {
			System.out.println(itemDetailsVO.toString());
		}
		clearSession();
		return itemDetails;
	}

	public String getAgentName(String suminfo) throws Exception {

		System.out.println("splunkSession at the top  :" + splunkSession);

		if (splunkSession == null)
			login();

		String queryString = "|inputlookup newsletter_lookup.csv|search SUM_INFO_ID ="+suminfo+"| table CLASS_NAME";
		String duration = "-1d@h";
		String sid = getSid(false, queryString, duration);
		String url = REST_URL + "/services/search/jobs/" + sid + "/results?output_mode=json&count=5";

		System.out.println("url:" + url);
		System.out.println("splunkSession :" + splunkSession);

		Map<String, String> params = new HashMap<String, String>();
		params.put("Authorization", "Splunk " + splunkSession);

		String response = get(url, params);
		System.out.println(response);
		JSONArray arr= new JSONObject(response).getJSONArray("results");
		if(arr.length()>0) {
			String agentName  = arr.getJSONObject(0).getString("CLASS_NAME");
			logger.info("...agentName : "+agentName);
			return agentName;
		}
		return "";
	}

	//for multiple csid -- Used in Excelservice.java
	public String getAgentNameFromCSID(String suminfo) throws Exception {

		System.out.println("splunkSession at the top  :" + splunkSession);

		if (splunkSession == null)
			login();

		String queryString = "|inputlookup newsletter_lookup.csv|search SUM_INFO_ID IN ("+suminfo+")| table SUM_INFO_ID CLASS_NAME";
		String duration = "-1d@h";
		System.out.println(queryString);
		String sid = getSid(false, queryString, duration);
		String url = REST_URL + "/services/search/jobs/" + sid + "/results?output_mode=json";

		System.out.println("url:" + url);
		System.out.println("splunkSession :" + splunkSession);

		Map<String, String> params = new HashMap<String, String>();
		params.put("Authorization", "Splunk " + splunkSession);

		String response = get(url+"&count=1000", params);
		System.out.println(response);
		return response;
	}


	public static String readQuery(String queryFilePath) throws IOException {

		File QueryFile = new File(queryFilePath);

		BufferedReader br = new BufferedReader(new FileReader(QueryFile));

		String line = br.readLine();
		String query = "";

		while (line != null) {
			query = query + "\n" + line;
			line = br.readLine();
		}

		br.close();
		return query;
	}

	public String getSid(boolean reload, String queryString, String earliest_time) {

		String sid = null;
		try {
			MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

			String url = null;
			if (queryString != null) {

				url = REST_URL + "services/search/jobs?output_mode=json";
				params.add("earliest_time", earliest_time);
				params.add("latest_time", "now");
				params.add("search", queryString);
			}

			String response = post(url, params);
			if (queryString != null) {
				sid = response.substring(response.indexOf(":") + 2, response.lastIndexOf("\""));
			} else {
				sid = response.substring(response.indexOf("<sid>") + 5, response.lastIndexOf("</sid>")).trim();
			}

			String jobstatus = getJobstatus(sid, 0);
			System.out.println("jobstatus at the end :" + jobstatus);

		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("inside exception........" + e.getMessage());

			if (e.getMessage().contains("401")) {
				clearSession();
				login();

				if (!reload) {
					sid = getSid(true, queryString, earliest_time);
				}
			}
		}

		System.out.println("sid returning:" + sid);
		return sid;
	}

	public  String getJobstatus(String sid, int count) throws Exception {

		String response = get(REST_URL + "services/search/jobs/" + sid, null);

		String done = response.substring(response.indexOf("<s:key name=\"isDone\">") + 21,
				response.indexOf("<s:key name=\"isDone\">") + 22);

		String status = "";

		if (done.equals("1")) {

			status = response.substring(response.indexOf("<s:key name=\"isFailed\">") + 23,

					response.indexOf("<s:key name=\"isFailed\">") + 24);
			return status;
		} else {

			if (count > 300) {
				return null;
			}

			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}

			status = getJobstatus(sid, ++count);
		}
		return status;
	}

	public String executeSplunkServices(String suminfo) throws Exception {

		System.out.println("splunkSession at the top  :" + splunkSession);

		if (splunkSession == null) {
			login();
		}

		String queryString = readQuery("src/main/resources/static/queryfolder/yccquery.txt");
		//query string for deployment
		//String queryString = readQuery("/home/uraj/wildfly-servlet-13.0.0.Final/standalone/deployments/AjaxAgentTool-1-SNAPSHOT.war/WEB-INF/classes/static/queryfolder/yccquery.txt");

		queryString = queryString.replace("#suminfoid#", suminfo);

		String duration = "-1d@h";
		String sid = getSid(false, queryString, duration);
		String url = REST_URL + "/services/search/jobs/" + sid + "/results?output_mode=json&count=5";

		System.out.println("url:" + url);
		System.out.println("splunkSession :" + splunkSession);

		Map<String, String> params = new HashMap<String, String>();

		params.put("Authorization", "Splunk " + splunkSession);

		String response = get(url, params);
		System.out.println("response in executesplunk method : \n " + response);

		return response;
	}

	public  String get(String url, Map<String, String> params) {

		try {

			HttpsURLConnection.setDefaultHostnameVerifier(HOSTNAME_VERIFIER);

			HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			requestHeaders.add("Authorization", "Splunk " + splunkSession);

			MultiValueMap<String, String> params1 = new LinkedMultiValueMap<String, String>();

			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(params1,

					requestHeaders);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

			return response.getBody().toString();

		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("inside exception........" + e.getMessage());

			if (e.getMessage().contains("401")) {

				clearSession();
				login();
				String data = get(url, params);
				return data;
			}
		}
		return "";
	}

	public Map<String, String> getAgentBase(List<String> agentNames)throws Exception{

		String query2="SELECT e.agent_name, m.agent_name as base_name " + 
				" FROM agent_info e " + 
				" INNER JOIN agent_info m ON m.agent_info_id = e.base_agent_info_id " + 
				" AND e.agent_name"
				+ " IN (";

		for(String agent : agentNames) {
			query2 = query2 + "'"+agent+"',";
		}

		query2  =query2.substring(0,query2.length()-1) + ")";

		System.out.println("splunkSession at the top  :"+splunkSession);

		if (splunkSession == null) {
			login();
		}

		String queryString="|dbxquery connection=repalda query=\""+query2+"\"";
		String duration="-1d@h";
		String sid = getSid(false,queryString,duration);
		String url = REST_URL+"/services/search/jobs/" + sid + "/results?output_mode=json";

		System.out.println("url:"+url);
		System.out.println("splunkSession :"+splunkSession);

		Map<String, String> params = new HashMap<String, String>();
		params.put("Authorization", "Splunk " + splunkSession);

		String response=get(url+"&count=20000",params); 
		System.out.println("....response: "+response);

		JSONObject responseObject = new JSONObject(response);

		JSONArray resultArray = responseObject.getJSONArray("results");

		Map<String, String> resultMap = new HashMap<>();

		for(int i=0;i<resultArray.length();i++) {

			JSONObject agentObject = (JSONObject) resultArray.get(i);
			resultMap.put(agentObject.getString("AGENT_NAME"), agentObject.getString("BASE_NAME"));
		}

		System.out.println(resultMap);
		return resultMap;
	}

	public JSONArray getUsers(String data,String agentName,String suminfo)throws Exception{

		System.out.println("...getUsers...");

		String query2="search index=itemerrors sourcetype=item_errors ";

		JSONArray arr = new JSONArray(data);
		System.out.println("..users size : "+arr.length());

		String splunk = "";
		if(suminfo!=null && !suminfo.isEmpty()) {
			splunk = " SUM_INFO_ID="+suminfo+" CLASS_NAME ="+agentName;
		}else {
			splunk = " CLASS_NAME ="+agentName;
		}

		for (int j = 0; j < arr.length(); j++) {
			JSONObject obj = arr.getJSONObject(j);
			splunk = splunk + " (MEM_SITE_ACC_ID="+obj.getString("memSiteAccID")+" AND COBRAND_ID= "+obj.getString("cobrandId")+") OR ";
		}

		splunk = splunk.substring(0, splunk.length()-3);

		query2 = query2 + splunk +" TYPE_OF_ERROR=0 MEM_SITE_ACC_ID!=-1 CACHE_ITEM_ID!=-1| eval MATCH=if(NEW_TRANSACTIONS>2 AND NUM_ITEM_ACCOUNTS>1 AND NUM_SUCCESSFUL_REFRESH>50,1,0) | eval temp=CACHE_ITEM_ID.DBID |dedup temp |Table SUM_INFO_ID,MEM_SITE_ACC_ID,CACHE_ITEM_ID,DBID,NUM_SUCCESSFUL_REFRESH,NUM_ITEM_ACCOUNTS,NEW_TRANSACTIONS MATCH| sort 0 -MATCH ";
		System.out.println(query2);

		System.out.println("splunkSession :"+splunkSession);
		clearSession();

		if (splunkSession == null) {
			login();
		}

		String queryString= query2;

		System.out.println(queryString);

		String duration="-15d@h";

		String sid = getSid(false,queryString,duration);

		String url = REST_URL+"/services/search/jobs/" + sid + "/results?output_mode=json";

		System.out.println("url:"+url);
		System.out.println("splunkSession :"+splunkSession);

		Map<String, String> params = new HashMap<String, String>();
		params.put("Authorization", "Splunk " + splunkSession);

		String response=get(url,params); 
		System.out.println("....response: "+response);

		JSONObject responseObject = new JSONObject(response);

		JSONArray resultArray = responseObject.getJSONArray("results");

		JSONArray splunk_return_Array = new JSONArray();

		for(int i=0;i<resultArray.length();i++) {

			JSONObject agentObject = (JSONObject) resultArray.get(i);

			JSONObject obj = new JSONObject();

			obj.put("cacheItemId", agentObject.getString("CACHE_ITEM_ID"));
			obj.put("memSiteAccId", agentObject.getString("MEM_SITE_ACC_ID"));

			if(dbMapping.get(agentObject.getString("DBID")) != null) {
				obj.put("dataBase",dbMapping.get(agentObject.getString("DBID")));
				System.out.println("...DB Name Altered : "+dbMapping.get(agentObject.getString("DBID")));
			}else  {
				obj.put("dataBase", agentObject.getString("DBID"));
			}

			splunk_return_Array.put(obj);

		}

		System.out.println("...size : "+splunk_return_Array.length());

		System.out.println(splunk_return_Array);
		clearSession();

		return splunk_return_Array;

	}


	//Method to get agentname from the suminfo provided //

	public String getAgentNamefromSuminfo(String suminfo) throws JsonParseException {
		String agentName=null;
		agentName_query=agentName_query.replace("suminfo", suminfo);
		System.out.println("agentname query is :"+agentName_query);
		System.out.println("splunkSession at the top  :"+splunkSession);

		if (splunkSession == null) {
			login();
		}

		//		String queryString="|dbxquery connection=repalda query=\""+query2+"\"";
		String duration="-1d@h";
		String sid = getSid(false,agentName_query,duration);
		String url = REST_URL+"/services/search/jobs/" + sid + "/results?output_mode=json";

		System.out.println("url:"+url);
		System.out.println("splunkSession :"+splunkSession);

		Map<String, String> params = new HashMap<String, String>();
		params.put("Authorization", "Splunk " + splunkSession);

		String response=get(url,params); ; 
		System.out.println("....response: "+response);

		JSONObject responseObject = new JSONObject(response);

		JSONArray resultArray = responseObject.getJSONArray("results");

		System.out.println("resultarray is "+resultArray);
		JSONObject agentObject = null;
		for(int i=0;i<resultArray.length();i++) {
			agentObject = (JSONObject) resultArray.get(i);
		}
		agentName=agentObject.getString("CLASS_NAME");
		clearSession();
		return agentName;

	}

	public void clearSession() {

		logger.info("clear the session ");
		System.out.println("clear the session : "+splunkSession);
		splunkSession = null;
	}

	public String getMFATypefromDump(String sumInfo)throws Exception{

		// System.out.println("splunkSession at the top  :"+splunkSession);

		if (splunkSession == null) {
			login();
		}

		String queryString="| savedsearch Fetch_Dumps sum_info="+sumInfo;
		String duration="-5d@h";
		String sid = getSid(false,queryString,duration);
		String url = REST_URL+"/services/search/jobs/" + sid + "/results?output_mode=json";
		System.out.println("url:"+url);
		System.out.println("splunkSession :"+splunkSession);
		Map<String, String> params = new HashMap<String, String>();
		params.put("Authorization", "Splunk " + splunkSession);
		String response=get(url,params);
		System.out.println("Splunk Response: "+response);
		response=response.substring(response.indexOf("results")+"results".length()+2);


		SplunkItemDetailsVO[] itemDetails=new ObjectMapper().readValue(response, SplunkItemDetailsVO[].class);


		if(itemDetails.length==0){
			System.out.println("CSID: "+sumInfo+" : no user found");
			return "No User Found";
		}



		for (SplunkItemDetailsVO splunkItemDetailsVO : itemDetails) {
			System.out.println("SplunkRepository: "+splunkItemDetailsVO.toString());
			dbAccessRepository.AddUserResponse(splunkItemDetailsVO);
		}


		HashMap<String, String> dumpLinkmap = dumpReadService.extractDumpLink(itemDetails, sumInfo, false);
		System.out.println("dump Link Map is done.");


		String MFAType = "";
		for(Map.Entry me : dumpLinkmap.entrySet()) {
			String cii = me.getKey().toString();
			String dumplink = me.getValue().toString();

			MFAType = dumpReadService.findXml(dumplink, cii,false, sumInfo);
			System.out.println("Printing MFA Type: "+MFAType);

		}

		return MFAType;

	}


	// getting users from Splunk in case Yuva is not giving the response //
	public JSONArray getUsersFromSplunk(String suminfo) throws Exception{

		System.out.println("...getUsersFromSplunk..."+suminfo);
		String query2="";
		if(suminfo!=null && !suminfo.isEmpty()) {
			query2="search index=itemerrors sourcetype=item_errors SUM_INFO_ID="+suminfo+" TYPE_OF_ERROR=0 DBID!=sdbcaf06  CACHE_ITEM_ID!=-1 MEM_SITE_ACC_ID!=-1 |eval cachMSA=CACHE_ITEM_ID.MEM_SITE_ACC_ID | dedup cachMSA | eval temp=CACHE_ITEM_ID.DBID | dedup temp |eval MATCH=if(NEW_TRANSACTIONS>2 AND NUM_ITEM_ACCOUNTS>1 AND NUM_SUCCESSFUL_REFRESH>50,1,0)| Table SUM_INFO_ID,MEM_SITE_ACC_ID,CACHE_ITEM_ID,DBID,NUM_SUCCESSFUL_REFRESH,NUM_ITEM_ACCOUNTS,NEW_TRANSACTIONS MATCH |sort 0 -MATCH| Head 5";
		}

		System.out.println("splunkSession at the top  :"+splunkSession);
		clearSession();

		if (splunkSession == null) {
			login();
		}

		String queryString= query2;

		System.out.println(queryString);

		String duration="-2d@h";
		String sid = getSid(false,queryString,duration);
		String url = REST_URL+"/services/search/jobs/" + sid + "/results?output_mode=json";

		System.out.println("url:"+url);
		System.out.println("splunkSession :"+splunkSession);

		Map<String, String> params = new HashMap<String, String>();
		params.put("Authorization", "Splunk " + splunkSession);

		String response=get(url,params);
		System.out.println("....response: "+response);

		JSONObject responseObject = new JSONObject(response);
		JSONArray resultArray = responseObject.getJSONArray("results");

		JSONArray splunk_return_Array = new JSONArray();

		int resultArraylength;
		if(resultArray.length()>5){
			resultArraylength = 5;
		}else{
			resultArraylength = resultArray.length();
		}

		for(int i=0;i<resultArraylength;i++) {

			JSONObject agentObject = (JSONObject) resultArray.get(i);

			JSONObject obj = new JSONObject();

			obj.put("cacheItemId", agentObject.getString("CACHE_ITEM_ID"));
			obj.put("memSiteAccId", agentObject.getString("MEM_SITE_ACC_ID"));

			if(dbMapping.get(agentObject.getString("DBID")) != null) {
				obj.put("dataBase",dbMapping.get(agentObject.getString("DBID")));
				System.out.println("...DB Name Altered : "+dbMapping.get(agentObject.getString("DBID")));
			}else {
				obj.put("dataBase", agentObject.getString("DBID"));
			}

			splunk_return_Array.put(obj);

		}

		System.out.println("...size : "+splunk_return_Array.length());
		System.out.println(splunk_return_Array);
		clearSession();
		return splunk_return_Array;
	}
}

