package com.yodlee.docdownloadandtsd.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "UserList")
public class FirememExtractedResponseForTSD {

    @Id
    private String itemId;

    private String jdapXMLResponse;

    private String errorCode;

    private String jdapDumpUrl;

    private boolean tsdGenuine;

    private String isTSDPresent;

    private String migId;

    private String itemType;

    @JsonIgnore
    public String getJdapXMLResponse() {
        return jdapXMLResponse;
    }

    public void setJdapXMLResponse(String jdapXMLResponse) {
        this.jdapXMLResponse = jdapXMLResponse;
    }

    @JsonProperty
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @JsonProperty
    public String getJdapDumpUrl() {
        return jdapDumpUrl;
    }

    public void setJdapDumpUrl(String jdapDumpUrl) {
        this.jdapDumpUrl = jdapDumpUrl;
    }

    @JsonProperty
    public boolean isTsdGenuine() {
        return tsdGenuine;
    }

    public void setTsdGenuine(boolean tsdGenuine) {
        this.tsdGenuine = tsdGenuine;
    }

    @JsonProperty
    public String getIsTSDPresent() {
        return isTSDPresent;
    }

    public void setIsTSDPresent(String isTSDPresent) {
        this.isTSDPresent = isTSDPresent;
    }

    @JsonProperty
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @JsonProperty
    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    @JsonProperty
    public String getMigId() {
        return migId;
    }

    public void setMigId(String migId) {
        this.migId = migId;
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

