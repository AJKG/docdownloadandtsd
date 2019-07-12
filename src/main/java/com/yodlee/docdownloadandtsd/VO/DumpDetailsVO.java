package com.yodlee.docdownloadandtsd.VO;

public class DumpDetailsVO {
	private String dumpUrl;
	private Integer statusCode;
	private String cacheItem;
	
	public String getCacheItem() {
		return cacheItem;
	}
	public void setCacheItem(String cacheItem) {
		this.cacheItem = cacheItem;
	}
	public String getDumpUrl() {
		return dumpUrl;
	}
	public void setDumpUrl(String dumpUrl) {
		this.dumpUrl = dumpUrl;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	public String toString() {
		return "DumpDetailsVO{" +
				"dumpUrl='" + dumpUrl + '\'' +
				", statusCode=" + statusCode +
				", cacheItem='" + cacheItem + '\'' +
				'}';
	}
}
