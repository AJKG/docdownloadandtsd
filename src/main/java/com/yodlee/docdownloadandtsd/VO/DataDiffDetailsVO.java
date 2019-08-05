package com.yodlee.docdownloadandtsd.VO;

public class DataDiffDetailsVO {

    private String sumInfoId;
    private String dataDiff;
    private String migratedBy;
    private String requestedDate;
    private String migId;
    private String agentName;

    public String getSumInfoId() {
        return sumInfoId;
    }

    public void setSumInfoId(String sumInfoId) {
        this.sumInfoId = sumInfoId;
    }

    public String getDataDiff() {
        return dataDiff;
    }

    public void setDataDiff(String dataDiff) {
        this.dataDiff = dataDiff;
    }

    public String getMigratedBy() {
        return migratedBy;
    }

    public void setMigratedBy(String migratedBy) {
        this.migratedBy = migratedBy;
    }

    public String getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }

    public String getMigId() {
        return migId;
    }

    public void setMigId(String migId) {
        this.migId = migId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public String toString() {
        return "DataDiffDetailsVO{" +
                "sumInfoId='" + sumInfoId + '\'' +
                ", dataDiff='" + dataDiff + '\'' +
                ", agentName='" + agentName + '\'' +
                ", migratedBy='" + migratedBy + '\'' +
                ", requestedDate='" + requestedDate + '\'' +
                ", migId='" + migId + '\'' +
                '}';
    }
}
