/**
 * Copyright (c) 2019 Yodlee Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc.
 * Use is subject to license terms.
 *
 */
package com.yodlee.docdownloadandtsd.VO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SiteAnalysisView {

		private String ClassName;
		
		private String batchStatus;
		
		private String AjaxType;
		private Double AccountSummaryPercentage;
		private Double TransactionMatchedPercentage;
		private Double HoldingsMatchedPercentage;
		private Double StatementMatchedPercentage;
		
		private int SegmentCount;
		
		private String batchMessage;
		
		
		@JsonProperty
		public String getBatchMessage() {
			return batchMessage;
		}

		
		public void setBatchMessage(String batchMessage) {
			this.batchMessage = batchMessage;
		}

		@JsonProperty
		public String getBatchStatus() {
			return batchStatus;
		}

		public void setBatchStatus(String batchStatus) {
			this.batchStatus = batchStatus;
		}

		
		@JsonProperty
		public String getAjaxType() {
			return AjaxType;
		}

		public void setAjaxType(String ajaxType) {
			AjaxType = ajaxType;
		}

		

		@JsonProperty
		public String getClassName() {
			return ClassName;
		}

		public void setClassName(String className) {
			ClassName = className;
		}

		@JsonProperty
		public Double getAccountSummaryPercentage() {
			return AccountSummaryPercentage;
		}


		public void setAccountSummaryPercentage(Double accountSummaryPercentage) {
			AccountSummaryPercentage = accountSummaryPercentage;
		}

		@JsonProperty
		public Double getTransactionMatchedPercentage() {
			return TransactionMatchedPercentage;
		}


		public void setTransactionMatchedPercentage(Double transactionMatchedPercentage) {
			TransactionMatchedPercentage = transactionMatchedPercentage;
		}
		@JsonProperty

		public Double getHoldingsMatchedPercentage() {
			return HoldingsMatchedPercentage;
		}


		public void setHoldingsMatchedPercentage(Double holdingsMatchedPercentage) {
			HoldingsMatchedPercentage = holdingsMatchedPercentage;
		}


		@JsonProperty
		public Double getStatementMatchedPercentage() {
			return StatementMatchedPercentage;
		}


		public void setStatementMatchedPercentage(Double statementMatchedPercentage) {
			StatementMatchedPercentage = statementMatchedPercentage;
		}

		@JsonProperty
		public int getSegmentCount() {
			return SegmentCount;
		}


		public void setSegmentCount(int segmentCount) {
			SegmentCount = segmentCount;
		}
		
		
	}

