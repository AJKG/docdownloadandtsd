package com.yodlee.docdownloadandtsd.VO;

public class TransactionSelectionDurationVO {

    private String migId;
    private String migratedBy;
    private String transactionDurationSeed;
    private String transactionDurationProd;
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

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public String toString() {
        return "TransactionSelectionDurationVO{" +
                "migId='" + migId + '\'' +
                ", migratedBy='" + migratedBy + '\'' +
                ", agentName='" + agentName + '\'' +
                ", transactionDurationSeed='" + transactionDurationSeed + '\'' +
                ", transactionDurationProd='" + transactionDurationProd + '\'' +
                ", requestedDate='" + requestedDate + '\'' +
                ", sumInfoId='" + sumInfoId + '\'' +
                '}';
    }
}
