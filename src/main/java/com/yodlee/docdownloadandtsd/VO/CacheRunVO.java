package com.yodlee.docdownloadandtsd.VO;

public class CacheRunVO {

    private String migId;
    private String migratedBy;
    private String cacheRunSeed;
    private String cacheRunProd;
    private String requestedDate;
    private String sumInfoId;
    private String agentName;

    public String getSumInfoId() {
        return sumInfoId;
    }

    public void setSumInfoId(String sumInfoId) {
        this.sumInfoId = sumInfoId;
    }

    public String getMigId() {
        return migId;
    }

    public void setMigId(String migId) {
        this.migId = migId;
    }

    public String getMigratedBy() {
        return migratedBy;
    }

    public void setMigratedBy(String migratedBy) {
        this.migratedBy = migratedBy;
    }

    public String getCacheRunSeed() {
        return cacheRunSeed;
    }

    public void setCacheRunSeed(String cacheRunSeed) {
        this.cacheRunSeed = cacheRunSeed;
    }

    public String getCacheRunProd() {
        return cacheRunProd;
    }

    public void setCacheRunProd(String cacheRunProd) {
        this.cacheRunProd = cacheRunProd;
    }

    public String getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public String toString() {
        return "CacheRunVO{" +
                "migId='" + migId + '\'' +
                ", migratedBy='" + migratedBy + '\'' +
                ", agentName='" + agentName + '\'' +
                ", cacheRunSeed='" + cacheRunSeed + '\'' +
                ", cacheRunProd='" + cacheRunProd + '\'' +
                ", requestedDate='" + requestedDate + '\'' +
                ", sumInfoId='" + sumInfoId + '\'' +
                '}';
    }
}
