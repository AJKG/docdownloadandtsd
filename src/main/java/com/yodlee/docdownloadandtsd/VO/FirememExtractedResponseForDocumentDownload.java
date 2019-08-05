package com.yodlee.docdownloadandtsd.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "UserList")
public class FirememExtractedResponseForDocumentDownload {

    @Id
    private String itemId;

    private String jdapXMLResponse;

    private String errorCode;

    private String jdapDumpUrl;

    private boolean docPresent;

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
    public boolean isDocPresent() {
        return docPresent;
    }

    public void setDocPresent(boolean docPresent) {
        this.docPresent = docPresent;
    }

    @JsonProperty
    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    @JsonProperty
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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
        return "FirememExtractedResponseForDocumentDownload{" +
                "itemId='" + itemId + '\'' +
                ", jdapXMLResponse='" + jdapXMLResponse + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", jdapDumpUrl='" + jdapDumpUrl + '\'' +
                ", docPresent=" + docPresent +
                '}';
    }
}
