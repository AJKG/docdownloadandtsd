package com.yodlee.docdownloadandtsd.VO;

public class DocDownloadVO {

    private String migId;
    private String migratedBy;
    private String docDownloadSeed;
    private String docDownloadProd;
    private String requestedDate;
    private String sumInfoId;

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

    public String getDocDownloadSeed() {
        return docDownloadSeed;
    }

    public void setDocDownloadSeed(String docDownloadSeed) {
        this.docDownloadSeed = docDownloadSeed;
    }

    public String getDocDownloadProd() {
        return docDownloadProd;
    }

    public void setDocDownloadProd(String docDownloadProd) {
        this.docDownloadProd = docDownloadProd;
    }

    public String getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }

    @Override
    public String toString() {
        return "DocDownloadVO{" +
                "migId='" + migId + '\'' +
                ", migratedBy='" + migratedBy + '\'' +
                ", docDownloadSeed='" + docDownloadSeed + '\'' +
                ", docDownloadProd='" + docDownloadProd + '\'' +
                ", requestedDate='" + requestedDate + '\'' +
                ", sumInfoId='" + sumInfoId + '\'' +
                '}';
    }
}
