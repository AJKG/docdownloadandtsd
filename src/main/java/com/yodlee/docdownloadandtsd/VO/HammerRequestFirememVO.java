package com.yodlee.docdownloadandtsd.VO;

import java.util.List;
import java.util.Map;

public class HammerRequestFirememVO {

    private String serverType;
    private String itemType;
    private List<String> requestTypes;
    private int mfaPreference;
    private String dbName;
    private String itemId;
    private String refreshRoute;
    private String customrefreshRoute;
    private String customRoute;
    private boolean prodCertified;
    private String agentFileType;
    private boolean modifiedtxnDuration;

    private Map<String, Object> transactionSelectionDuration;

    public HammerRequestFirememVO() {}

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public List<String> getRequestTypes() {
        return requestTypes;
    }

    public void setRequestTypes(List<String> requestTypes) {
        this.requestTypes = requestTypes;
    }

    public int getMfaPreference() {
        return mfaPreference;
    }

    public void setMfaPreference(int mfaPreference) {
        this.mfaPreference = mfaPreference;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getRefreshRoute() {
        return refreshRoute;
    }

    public void setRefreshRoute(String refreshRoute) {
        this.refreshRoute = refreshRoute;
    }

    public String getCustomrefreshRoute() {
        return customrefreshRoute;
    }

    public void setCustomrefreshRoute(String customrefreshRoute) {
        this.customrefreshRoute = customrefreshRoute;
    }

    public String getCustomRoute() {
        return customRoute;
    }

    public void setCustomRoute(String customRoute) {
        this.customRoute = customRoute;
    }

    public boolean isProdCertified() {
        return prodCertified;
    }

    public void setProdCertified(boolean prodCertified) {
        this.prodCertified = prodCertified;
    }

    public String getAgentFileType() {
        return agentFileType;
    }

    public void setAgentFileType(String agentFileType) {
        this.agentFileType = agentFileType;
    }

    public boolean isModifiedtxnDuration() {
        return modifiedtxnDuration;
    }

    public void setModifiedtxnDuration(boolean modifiedtxnDuration) {
        this.modifiedtxnDuration = modifiedtxnDuration;
    }

    public Map<String, Object> getTransactionSelectionDuration() {
        return transactionSelectionDuration;
    }

    public void setTransactionSelectionDuration(
            Map<String, Object> transactionSelectionDuration) {
        this.transactionSelectionDuration = transactionSelectionDuration;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HammerRequestFiremem{");
        sb.append("serverType='").append(serverType).append('\'');
        sb.append(", itemType='").append(itemType).append('\'');
        sb.append(", requestTypes='").append(requestTypes).append('\'');
        sb.append(", mfaPreference='").append(mfaPreference).append('\'');
        sb.append(", dbName='").append(dbName).append('\'');
        sb.append(", itemId='").append(itemId).append('\'');
        sb.append(", refreshRoute='").append(refreshRoute).append('\'');
        sb.append(", customrefreshRoute='").append(customrefreshRoute).append('\'');
        sb.append(", customRoute='").append(customRoute).append('\'');
        sb.append(", prodCertified='").append(prodCertified).append('\'');
        sb.append(", agentFileType='").append(agentFileType).append('\'');
        sb.append(", modifiedtxnDuration='").append(modifiedtxnDuration).append('\'');
        sb.append(", transactionSelectionDuration=").append(transactionSelectionDuration);
        sb.append('}');
        return sb.toString();
    }

}
