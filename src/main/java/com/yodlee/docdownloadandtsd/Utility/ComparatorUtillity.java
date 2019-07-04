package com.yodlee.docdownloadandtsd.Utility;


import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

public class ComparatorUtillity {


	public static String getFiremem(String firmem) throws MalformedURLException, IOException {

		if (firmem.contains("dumpdispatcher")) {
            String modifiedDumpLink = firmem.replaceAll("\\?id=", "/").replaceAll("dumpdispatcher", "downloads");
            String shortenedDump = modifiedDumpLink.substring(0, modifiedDumpLink.indexOf(".html") + 5);
            firmem=shortenedDump;
        }
		

		HttpsURLConnection firememConnection = (HttpsURLConnection) new URL(firmem).openConnection();
		firememConnection.setRequestMethod("GET");


		int responseCode = firememConnection.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);

		if (responseCode == HttpsURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(firememConnection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			//System.out.println(response.toString());
			return response.toString();
		} else {
			//System.out.println("GET request not worked");
			return null;
		}
	}


	public static String fetchFinalSiteXML(String firememResponse) {
		String firememXML = firememResponse.substring(firememResponse.indexOf("Site XML Validation")+"Site XML Validation".length(),firememResponse.lastIndexOf("</PRE>"));
		firememXML = firememXML.substring(firememXML.indexOf("<PRE>")+ "<PRE>".length());
		firememXML= firememXML.replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&quot;","\"");
		// firememXML.replaceAll("&gt;",">");
		//firememXML.replaceAll("&quot;","\"");
		firememXML.replaceAll("&#10;"," ");

		firememXML= firememXML.replaceAll("&#10;"," ");

		//System.out.println(firememXML);
		return firememXML;
	}

	public static String getAccountSummaryXML(String firememResponse) {
		String accountSummaryXMl=firememResponse.substring(firememResponse.indexOf("ACCOUNT_SUMMARY_XML::")+"ACCOUNT_SUMMARY_XML::".length());
		accountSummaryXMl=accountSummaryXMl.substring(0,accountSummaryXMl.indexOf("</td>"));
		if(accountSummaryXMl.contains("<PRE>")){
			accountSummaryXMl = accountSummaryXMl.substring(accountSummaryXMl.indexOf("<PRE>")+"<PRE>".length(),accountSummaryXMl.lastIndexOf("</PRE>"));
		}
        //System.out.println(accountSummaryXMl);
		return accountSummaryXMl;
	}

	public static HashMap<String, HashMap<String, ArrayList<String>>> fetchAjaxResponse(String firememResponse) {
		
		String firememResponseTemp=firememResponse;
		
		 HashMap<String, HashMap<String, ArrayList<String>>> firememResponseColleection= new HashMap<String, HashMap<String, ArrayList<String>>>();
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
	            //System.out.println(state);
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


	        }while (firememResponseTemp.contains("AJAX_RESPONSE_FOR_TOOL:"));
		
		
		return firememResponseColleection;
	}
}

