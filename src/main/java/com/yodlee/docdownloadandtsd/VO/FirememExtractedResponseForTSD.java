package com.yodlee.docdownloadandtsd.VO;

public class FirememExtractedResponseForTSD {

    private String itemId;

    private String errorCode;

    private String jdapDumpUrl;

    private boolean tsdGenuine;

    private String isTSDPresent;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getJdapDumpUrl() {
        return jdapDumpUrl;
    }

    public void setJdapDumpUrl(String jdapDumpUrl) {
        this.jdapDumpUrl = jdapDumpUrl;
    }

    public boolean isTsdGenuine() {
        return tsdGenuine;
    }

    public void setTsdGenuine(boolean tsdGenuine) {
        this.tsdGenuine = tsdGenuine;
    }

    public String getIsTSDPresent() {
        return isTSDPresent;
    }

    public void setIsTSDPresent(String isTSDPresent) {
        this.isTSDPresent = isTSDPresent;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public String toString() {
        return "FirememExtractedResponseForTSD{" +
                "itemId='" + itemId + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", jdapDumpUrl='" + jdapDumpUrl + '\'' +
                ", tsdGenuine=" + tsdGenuine +
                ", isTSDPresent='" + isTSDPresent + '\'' +
                '}';
    }
}

