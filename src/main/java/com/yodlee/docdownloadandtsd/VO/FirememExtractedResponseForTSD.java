package com.yodlee.docdownloadandtsd.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FirememExtractedResponseForTSD {

    private String itemId;

    private String jdapXMLResponse;

    private String errorCode;

    private String jdapDumpUrl;

    private boolean tsdGenuine;

    private String isTSDPresent;

    @JsonIgnore
    public String getJdapXMLResponse() {
        return jdapXMLResponse;
    }

    public void setJdapXMLResponse(String jdapXMLResponse) {
        this.jdapXMLResponse = jdapXMLResponse;
    }

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
                ", jdapXMLResponse='" + jdapXMLResponse + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", jdapDumpUrl='" + jdapDumpUrl + '\'' +
                ", tsdGenuine=" + tsdGenuine +
                ", isTSDPresent='" + isTSDPresent + '\'' +
                '}';
    }
}

