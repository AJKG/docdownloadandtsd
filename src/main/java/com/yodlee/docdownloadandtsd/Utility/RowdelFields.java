package com.yodlee.docdownloadandtsd.Utility;

/**
 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.
 * This software is the confidential and proprietary information of Yodlee, Inc.
 * Use is subject to license terms.
 */

public interface RowdelFields{
	public static final String ACCOUNT_NUMBER = "accountNumber";
	public static final String ACCOUNT_NAME = "accountName";
	public static final String TRANS_DATE = "transDate";
	public static final String POST_DATE = "postDate";
	public static final String AMOUNT = "amount";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String CLOSING_BALANCE = "closingBalance";
	public static final String STATEMENT_DATE = "statementDate";
	public static final String UNIQUE_ID = "uniqueId";
	public static final String TRANSACTION_ID = "transactionId";
	public static final String DESCRIPTION = "description";
	public static final String BASE_TYPE = "baseType";
	public static final String BILL_DATE = "billDate";
	public static final String DUE_DATE = "dueDate";
	public static final String CUSIP_NUMBER = "cusipNumber";
	public static final String SYMBOL = "symbol";
	public static final String HOLDING_TYPE = "holdingType";
	public static final String QUANTITY = "quantity";
	public static final String PRICE = "price";
	public static final String MEMBER_NAME = "memberName";
	public static final String TOTAL_AMOUNT = "totalAmount";
	public static final String TOTAL_DESCRIPTION = "totalDescription";
	public static final String DATE = "date";
	
	/*
	 * Note : the sequence resembles the priority of the match
	 * Kindly insert any new object at an approproate index as per the precedence of the tag to be considered for rowdel
	 */
	public static final String[] fieldsForRowdel = {ACCOUNT_NUMBER, ACCOUNT_NAME, TRANS_DATE, POST_DATE, AMOUNT, START_DATE, END_DATE, 
												  CLOSING_BALANCE, STATEMENT_DATE, UNIQUE_ID, TRANSACTION_ID, DESCRIPTION, BASE_TYPE, 
												  BILL_DATE, DUE_DATE, CUSIP_NUMBER, SYMBOL, HOLDING_TYPE, QUANTITY, PRICE, MEMBER_NAME, 
												  TOTAL_AMOUNT, TOTAL_DESCRIPTION, DATE};
	
}
