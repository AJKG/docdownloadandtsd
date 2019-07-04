package com.yodlee.docdownloadandtsd.Utility;
/**
 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.
 * This software is the confidential and proprietary information of Yodlee, Inc.
 * Use is subject to license terms.
 */


import java.util.HashMap;

public class NodeUtil implements RowdelFields{
	
	
	public static class CompareLogic{
		public static final String UNIQUE_ID = "uniqueId";
		public static final String ROWDEL_FIELDS = "rowdelFields";
		public static final String ATTRIBUTES = "attributes";
		public static final String TAG = "tag";
		public static final String DEFAUT = "default";
	}
	
	//The array represents the sequence in which the comparison logics must be used
	public static final String[] comparisonLogics = {CompareLogic.ROWDEL_FIELDS, CompareLogic.ATTRIBUTES, CompareLogic.TAG};
	public static HashMap<String,XMLNode> map = new HashMap<String,XMLNode>();
	public static final String[] attributesToSkipForDiscripancy = {"pageHash","pageHashSE","rowId","localFormat"};

	/*
	 * These fields are used for showing the type of difference of any node after it is compared with another node of another tree.
	 * Used by the comparator.
	 */
	public static final String DIFFERENCE_TYPE_NONE = "none";//resembles that the 2 nodes are the same
	public static final String DIFFERENCE_TYPE_MISSING_NODE = "missingNode";//Resembles if this node is not present in the other comparing tree
	public static final String DIFFERENCE_TYPE_NEW_NODE = "newNode";
	public static final String DIFFERENCE_TYPE_DISCREPANCT_NODE = "discripantNode"; //Resembles is there is a difference in the data
	
	public static HashMap<String,String> nodeColorMap = new HashMap<String,String>();
	public static HashMap<String,String> tagContainerMap = new HashMap<String,String>();
	
	static{
		nodeColorMap.put(DIFFERENCE_TYPE_NONE, "black");
		nodeColorMap.put(DIFFERENCE_TYPE_MISSING_NODE, "red");
		nodeColorMap.put(DIFFERENCE_TYPE_NEW_NODE, "red");
		nodeColorMap.put(DIFFERENCE_TYPE_DISCREPANCT_NODE, "blue");
		
		tagContainerMap.put("bankAccount", "bank");
		tagContainerMap.put("cardAccount", "card");
		tagContainerMap.put("investmentAccunt", "stocks");
	}
	
	public static class State{
		public static final String ACCOUNT_SUMMARY = "ACCOUNT_SUMMARY";
		public static final String ACCOUNT_DETAILS = "ACCOUNT_DETAILS";
		public static final String TRANSACTION = "TRANSACTION";
		public static final String HOLDING = "HOLDING";
		public static final String STATEMENTS = "STATEMENTS";
		public static final String OTHER = "OTHER";
	}
	
}
