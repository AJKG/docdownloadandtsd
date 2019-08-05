package com.yodlee.docdownloadandtsd.VO;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "DocDownload")
public class DocResponseVO {

    @Id
    private String sumInfoId;
    private String isDocPresent;
    private String docPercentage;
    private String migId;
    private String migratedBy;
    private String docDownloadSeed;
    private String docDownloadProd;
    private String requestedDate;
    private String metaDataType;
    private String agentName;
    private String noOfItems;

    @JsonProperty
    public String getSumInfoId() {
        return sumInfoId;
    }

    public void setSumInfoId(String sumInfoId) {
        this.sumInfoId = sumInfoId;
    }

    @JsonProperty
    public String getIsDocPresent(String message){return isDocPresent;}

    public void  setIsDocPresent(String isDocPresent){ this.isDocPresent = isDocPresent;}

    @JsonProperty
    public String getDocPercentage(){return docPercentage;}

    public void  setDocPercentage(String docPercentage){ this.docPercentage = docPercentage;}

    @JsonProperty
    public String getMigId() {
        return migId;
    }

    public void setMigId(String migId) {
        this.migId = migId;
    }

    @JsonProperty
    public String getMigratedBy() {
        return migratedBy;
    }

    public void setMigratedBy(String migratedBy) {
        this.migratedBy = migratedBy;
    }

    @JsonProperty
    public String getDocDownloadSeed() { return docDownloadSeed; }

    public void setDocDownloadSeed(String docDownloadSeed) {
        this.docDownloadSeed = docDownloadSeed;
    }

    @JsonProperty
    public String getDocDownloadProd() {
        return docDownloadProd;
    }

    public void setDocDownloadProd(String docDownloadProd) {
        this.docDownloadProd = docDownloadProd;
    }

    @JsonProperty
    public String getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }

    @JsonProperty
    public String getMetaDataType() { return metaDataType;}

    public void setMetaDataType(String metaDataType){this.metaDataType = metaDataType;}

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(String noOfItems) {
        this.noOfItems = noOfItems;
    }

    @Override
    public String toString() {
        return "DocResponse[sumInfoId=" + sumInfoId + ","
                +"isDocPresent="+isDocPresent+","
                +"docPercentage="+docPercentage+","
                +"migID="+migId+","
                +"agentName="+agentName+","
                +"migratedBy="+migratedBy+","
                +"docDownloadSeed="+docDownloadSeed+","
                +"docDownloadProd="+docDownloadProd+","
                +"requestDate="+requestedDate+","
                +"metaDataType="+metaDataType+"]";
    }
    }
