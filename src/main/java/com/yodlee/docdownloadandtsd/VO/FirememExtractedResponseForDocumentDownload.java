package com.yodlee.docdownloadandtsd.VO;

public class FirememExtractedResponseForDocumentDownload {

    private String itemId;

    private String jdapXMLResponse;

    private String errorCode;

    private String jdapDumpUrl;

    private boolean docPresent;

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

    public boolean isDocPresent() {
        return docPresent;
    }

    public void setDocPresent(boolean docPresent) {
        this.docPresent = docPresent;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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
