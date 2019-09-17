package com.yodlee.docdownloadandtsd.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TSD")
public class TSDResponseVO {


    @Id
    private String sumInfoId;
    private String isTSDPresent;
    private String TSDPercentage;
    private String migId;
    private String migratedBy;
    private String transactionSelectionDurationSeed;
    private String transactionSelectionDurationProd;
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
    public String getIsTSDPresent(){return isTSDPresent;}

    public void  setIsTSDPresent(String isTSDPresent){ this.isTSDPresent = isTSDPresent;}

    @JsonProperty
    public String getTSDPercentage(){return TSDPercentage;}

    public void  setTSDPercentage(String TSDPercentage){ this.TSDPercentage = TSDPercentage;}

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
    public String getTransactionSelectionDurationSeed() { return transactionSelectionDurationSeed; }

    public void setTransactionSelectionDurationSeed(String transactionSelectionDurationSeed) {
        this.transactionSelectionDurationSeed = transactionSelectionDurationSeed;
    }

    @JsonProperty
    public String getTransactionSelectionDurationProd() {
        return transactionSelectionDurationProd;
    }

    public void setTransactionSelectionDurationProd(String transactionSelectionDurationProd) {
        this.transactionSelectionDurationProd = transactionSelectionDurationProd;
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
        return "TSDResponse [sumInfoId=" + sumInfoId + ","
                +"isTSDPresent="+isTSDPresent+","
                +"TSDPercentage="+TSDPercentage+","
                +"migID="+migId+","
                +"agentName="+agentName+","
                +"migratedBy="+migratedBy+","
                +"transactionSelectionDurationSeed="+transactionSelectionDurationSeed+","
                +"transactionSelectionDurationProd="+transactionSelectionDurationProd+","
                +"requestDate="+requestedDate+","
                +"metaDataType="+metaDataType+"]";
    }

}
