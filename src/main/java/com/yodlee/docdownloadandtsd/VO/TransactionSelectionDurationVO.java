package com.yodlee.docdownloadandtsd.VO;

public class TransactionSelectionDurationVO {

    private String migId;
    private String migratedBy;
    private String transactionDurationSeed;
    private String transactionDurationProd;
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

    public String getTransactionDurationSeed() {
        return transactionDurationSeed;
    }

    public void setTransactionDurationSeed(String transactionDurationSeed) {
        this.transactionDurationSeed = transactionDurationSeed;
    }

    public String getTransactionDurationProd() {
        return transactionDurationProd;
    }

    public void setTransactionDurationProd(String transactionDurationProd) {
        this.transactionDurationProd = transactionDurationProd;
    }

    public String getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }
}
