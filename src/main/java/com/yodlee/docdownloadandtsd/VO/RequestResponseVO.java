package com.yodlee.docdownloadandtsd.VO;

import java.util.HashMap;
import java.util.List;


public class RequestResponseVO {
	private String cacheItemId;
	private String acctRequest;
	private String acctResponse;
	private String transRequest;
	private String sumInfoID;
	private String scriptLatency;
	private String transResponse;
	private String isAjaxPresent;
	private String isAccAjax;
	private String is2YearsPresent;
	private String isTransAjax;
	private String accResponseType;
	private String transResponseType;
	private String className;
	private String statusCode;
	private String accReqMethod;
	private String transReqMethod;
	private String dumpUrl;
	private String DBID;
	private String accFinalCode;
	private String transFinalCode;
	private String responseXML;
	private String oldestTxnResp;
	private String oldURL;
	private HashMap<String, String[]> acc_urlParams;
	private HashMap<String, String[]> acc_headerParams;
	private HashMap<String, String[]> trans_urlParams;
	private HashMap<String, String[]> trans_headerParams;	
	private List<String> fullRawData;
	public String getCacheItemId() {
		return cacheItemId;
	}
	public void setCacheItemId(String cacheItemId) {
		this.cacheItemId = cacheItemId;
	}
	public String getAcctRequest() {
		return acctRequest;
	}
	public void setAcctRequest(String acctRequest) {
		this.acctRequest = acctRequest;
	}
	public String getAcctResponse() {
		return acctResponse;
	}
	public void setAcctResponse(String acctResponse) {
		this.acctResponse = acctResponse;
	}
	public String getTransRequest() {
		return transRequest;
	}
	public void setTransRequest(String transRequest) {
		this.transRequest = transRequest;
	}
	public String getSumInfoID() {
		return sumInfoID;
	}
	public void setSumInfoID(String sumInfoID) {
		this.sumInfoID = sumInfoID;
	}
	public String getScriptLatency() {
		return scriptLatency;
	}
	public void setScriptLatency(String scriptLatency) {
		this.scriptLatency = scriptLatency;
	}
	public String getTransResponse() {
		return transResponse;
	}
	public void setTransResponse(String transResponse) {
		this.transResponse = transResponse;
	}
	public String getIsAjaxPresent() {
		return isAjaxPresent;
	}
	public void setIsAjaxPresent(String isAjaxPresent) {
		this.isAjaxPresent = isAjaxPresent;
	}
	public String getIsAccAjax() {
		return isAccAjax;
	}
	public void setIsAccAjax(String isAccAjax) {
		this.isAccAjax = isAccAjax;
	}
	public String getIs2YearsPresent() {
		return is2YearsPresent;
	}
	public void setIs2YearsPresent(String is2YearsPresent) {
		this.is2YearsPresent = is2YearsPresent;
	}
	public String getIsTransAjax() {
		return isTransAjax;
	}
	public void setIsTransAjax(String isTransAjax) {
		this.isTransAjax = isTransAjax;
	}
	public String getAccResponseType() {
		return accResponseType;
	}
	public void setAccResponseType(String accResponseType) {
		this.accResponseType = accResponseType;
	}
	public String getTransResponseType() {
		return transResponseType;
	}
	public void setTransResponseType(String transResponseType) {
		this.transResponseType = transResponseType;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getAccReqMethod() {
		return accReqMethod;
	}
	public void setAccReqMethod(String accReqMethod) {
		this.accReqMethod = accReqMethod;
	}
	public String getTransReqMethod() {
		return transReqMethod;
	}
	public void setTransReqMethod(String transReqMethod) {
		this.transReqMethod = transReqMethod;
	}
	public String getDumpUrl() {
		return dumpUrl;
	}
	public void setDumpUrl(String dumpUrl) {
		this.dumpUrl = dumpUrl;
	}
	public String getDBID() {
		return DBID;
	}
	public void setDBID(String dBID) {
		DBID = dBID;
	}
	public String getAccFinalCode() {
		return accFinalCode;
	}
	public void setAccFinalCode(String accFinalCode) {
		this.accFinalCode = accFinalCode;
	}
	public String getTransFinalCode() {
		return transFinalCode;
	}
	public void setTransFinalCode(String transFinalCode) {
		this.transFinalCode = transFinalCode;
	}
	public String getResponseXML() {
		return responseXML;
	}
	public void setResponseXML(String responseXML) {
		this.responseXML = responseXML;
	}
	public String getOldestTxnResp() {
		return oldestTxnResp;
	}
	public void setOldestTxnResp(String oldestTxnResp) {
		this.oldestTxnResp = oldestTxnResp;
	}
	public String getOldURL() {
		return oldURL;
	}
	public void setOldURL(String oldURL) {
		this.oldURL = oldURL;
	}
	public HashMap<String, String[]> getAcc_urlParams() {
		return acc_urlParams;
	}
	public void setAcc_urlParams(HashMap<String, String[]> acc_urlParams) {
		this.acc_urlParams = acc_urlParams;
	}
	public HashMap<String, String[]> getAcc_headerParams() {
		return acc_headerParams;
	}
	public void setAcc_headerParams(HashMap<String, String[]> acc_headerParams) {
		this.acc_headerParams = acc_headerParams;
	}
	public HashMap<String, String[]> getTrans_urlParams() {
		return trans_urlParams;
	}
	public void setTrans_urlParams(HashMap<String, String[]> trans_urlParams) {
		this.trans_urlParams = trans_urlParams;
	}
	public HashMap<String, String[]> getTrans_headerParams() {
		return trans_headerParams;
	}
	public void setTrans_headerParams(HashMap<String, String[]> trans_headerParams) {
		this.trans_headerParams = trans_headerParams;
	}
	public List<String> getFullRawData() {
		return fullRawData;
	}
	public void setFullRawData(List<String> fullRawData) {
		this.fullRawData = fullRawData;
	}
	@Override
	public String toString() {
		return "RequestResponseVO [cacheItemId=" + cacheItemId + ", acctRequest=" + acctRequest + ", acctResponse="
				+ acctResponse + ", transRequest=" + transRequest + ", sumInfoID=" + sumInfoID + ", scriptLatency="
				+ scriptLatency + ", transResponse=" + transResponse + ", isAjaxPresent=" + isAjaxPresent
				+ ", isAccAjax=" + isAccAjax + ", is2YearsPresent=" + is2YearsPresent + ", isTransAjax=" + isTransAjax
				+ ", accResponseType=" + accResponseType + ", transResponseType=" + transResponseType + ", className="
				+ className + ", statusCode=" + statusCode + ", accReqMethod=" + accReqMethod + ", transReqMethod="
				+ transReqMethod + ", dumpUrl=" + dumpUrl + ", DBID=" + DBID + ", accFinalCode=" + accFinalCode
				+ ", transFinalCode=" + transFinalCode + ", responseXML=" + responseXML + ", oldestTxnResp="
				+ oldestTxnResp + ", oldURL=" + oldURL + ", acc_urlParams=" + acc_urlParams + ", acc_headerParams="
				+ acc_headerParams + ", trans_urlParams=" + trans_urlParams + ", trans_headerParams="
				+ trans_headerParams + ", fullRawData=" + fullRawData + "]";
	}

}
