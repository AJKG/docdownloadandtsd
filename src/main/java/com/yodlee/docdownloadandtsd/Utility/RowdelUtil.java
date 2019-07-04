package com.yodlee.docdownloadandtsd.Utility;

/**

 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.

 * This software is the confidential and proprietary information of Yodlee, Inc.

 * Use is subject to license terms.

 */

import java.util.*;

public class RowdelUtil implements RowdelFields {

	private static HashMap<String, HashMap<String,List<List<String>>>> containerRuleMap = new HashMap<String, HashMap<String,List<List<String>>>>();
	private static HashMap<String, HashSet<String>> tagRowdelFieldMap = new HashMap<String, HashSet<String>>();
	
	static{
		String[] accountRules = {ACCOUNT_NUMBER+"+"+ACCOUNT_NAME, ACCOUNT_NUMBER, ACCOUNT_NAME};
		String[] bankStatementRules = {START_DATE +"+"+ END_DATE +"+"+ CLOSING_BALANCE, STATEMENT_DATE +"+"+ CLOSING_BALANCE, UNIQUE_ID};
		
		String[] investmentHoldingaRule = {
											DESCRIPTION +"+"+ CUSIP_NUMBER +"+"+ SYMBOL +"+"+ HOLDING_TYPE,
											DESCRIPTION +"+"+ CUSIP_NUMBER +"+"+ HOLDING_TYPE,
											DESCRIPTION +"+"+ SYMBOL +"+"+ HOLDING_TYPE,
											DESCRIPTION +"+"+ HOLDING_TYPE,
											UNIQUE_ID
										  };
		
		String[] bankTransactionRules =  {
											TRANSACTION_ID, 
											TRANS_DATE +"+"+ AMOUNT +"+"+ DESCRIPTION +"+"+ BASE_TYPE,
											POST_DATE +"+"+ AMOUNT +"+"+ DESCRIPTION +"+"+ BASE_TYPE,
											TRANS_DATE +"+"+ AMOUNT,
											POST_DATE +"+"+ AMOUNT,
											UNIQUE_ID
										  };
		
		String[] cardStatementRules = 	{	
											BILL_DATE +"+"+DUE_DATE, 
											BILL_DATE, 
											DUE_DATE	
										};
		
		String[] cardTransactionRules = {
											TRANSACTION_ID, 
											TRANS_DATE +"+"+ AMOUNT +"+"+ DESCRIPTION +"+"+ BASE_TYPE,
											POST_DATE +"+"+ AMOUNT +"+"+ DESCRIPTION +"+"+ BASE_TYPE,
											TRANS_DATE +"+"+ AMOUNT,
											POST_DATE +"+"+ AMOUNT,
											UNIQUE_ID
			  							}; 
		
		String[] investmentTransactionRules = {
												TRANSACTION_ID,
												DATE + "+" + DESCRIPTION + "+" + AMOUNT,
												DATE + "+" + DESCRIPTION + "+" + CUSIP_NUMBER,
												CUSIP_NUMBER + QUANTITY + "+" + PRICE,
												CUSIP_NUMBER + "+" + AMOUNT
											  };
		
		String[] loanStatementRules = 	{
											BILL_DATE + "+" + DUE_DATE,
											BILL_DATE,
											DUE_DATE
										};
		
		String[] loanTransactionRules = {
											TRANS_DATE + "+" + DESCRIPTION,
											POST_DATE + "+" + DESCRIPTION,
											TRANS_DATE + "+" + AMOUNT,
											POST_DATE + "+" + AMOUNT,
											UNIQUE_ID
										};
		
		setRules("bank", "bankAccount", accountRules);
		setRules("stocks", "investmentAccount", accountRules);
		setRules("card", "cardAccount", accountRules);
		setRules("stocks", "holding",investmentHoldingaRule);
		setRules("bank", "transaction",bankTransactionRules);
		setRules("stocks", "transaction",investmentTransactionRules);
		setRules("loan","transaction", loanTransactionRules);
		setRules("card","transaction", cardTransactionRules);
	
	}
	
	
	private static void setRules(String container, String tagName, String[] rules){
			
		if(container==null || tagName==null || rules==null ){
			return;
		}
		
		//If the container is not present
		if(containerRuleMap.get(container)==null){
			containerRuleMap.put(container,new HashMap<String,List<List<String>>>());
		}
		
		//If the tagName is not present under the respctive container
		if(containerRuleMap.get(container).get(tagName)==null){
			containerRuleMap.get(container).put(tagName,new ArrayList<List<String>>());
		}
		
		ArrayList<List<String>> ruleList = new ArrayList<List<String>>();
		HashSet<String> presentRowdelFieldsSet = new HashSet<String>();
		
		for(String rule : rules){
			StringTokenizer rowdelFields = new StringTokenizer(rule,"+");
			ArrayList<String> listOfRowdelFields = new ArrayList<String>();
			while(rowdelFields.hasMoreTokens()){
				String rowdelField = rowdelFields.nextToken().trim();
				listOfRowdelFields.add(rowdelField);
				presentRowdelFieldsSet.add(rowdelField);
			}
			
			ruleList.add(listOfRowdelFields);
		}
		
		if(tagRowdelFieldMap.get(tagName)==null){
			tagRowdelFieldMap.put(tagName, presentRowdelFieldsSet);
		}else{
			HashSet<String> preRowdelFieldsSet = tagRowdelFieldMap.get(tagName);
			presentRowdelFieldsSet.addAll(preRowdelFieldsSet);
			tagRowdelFieldMap.put(tagName, presentRowdelFieldsSet);
		}
		
		containerRuleMap.get(container).get(tagName).addAll(ruleList);
	}
	
	public static List<List<String>> getRuleList(String container, String tag){
		
		ArrayList<List<String>> ruleList = new ArrayList<List<String>>();
		
		if(containerRuleMap.get(container)!=null && containerRuleMap.get(container).get(tag)!=null  ){
			ruleList.addAll(containerRuleMap.get(container).get(tag));
		}
		
		return ruleList;
	}
	
	
	public static boolean isFieldConsiderableAsRowDelField(String fieldToMatch){
		boolean isConsiderable = false;	
			if(fieldToMatch==null || fieldToMatch.equals("")){
				return false;
			}

		for(String field : fieldsForRowdel){
			if(field.equals(fieldToMatch)){
				isConsiderable = true;
				break;
			}
		}
		
		return isConsiderable;
	}
}
