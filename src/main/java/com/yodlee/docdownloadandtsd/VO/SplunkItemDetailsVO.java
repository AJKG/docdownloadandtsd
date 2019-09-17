package com.yodlee.docdownloadandtsd.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CacheUserList")
public class SplunkItemDetailsVO {

    private String SUM_INFO_ID;
    private String MEM_SITE_ACCOUNT_ID;
    private String CACHE_ITEM_ID;
    private String DB_ID;
    private String CLASS;
    private String DUMP_FILE;

    @JsonProperty("CLASS")
    public String getCLASS() {
        return CLASS;
    }
    public void setCLASS(String cLASS) {
        CLASS = cLASS;
    }
    @JsonProperty("SUM_INFO_ID")
    public String getSUM_INFO_ID() {
        return SUM_INFO_ID;
    }
    public void setSUM_INFO_ID(String sUM_INFO_ID) {
        SUM_INFO_ID = sUM_INFO_ID;
    }

    @JsonProperty("MEM_SITE_ACCOUNT_ID")
    public String getMEM_SITE_ACCOUNT_ID() {
        return MEM_SITE_ACCOUNT_ID;
    }

    public void setMEM_SITE_ACCOUNT_ID(String mEM_SITE_ACCOUNT_ID) {
        MEM_SITE_ACCOUNT_ID = mEM_SITE_ACCOUNT_ID;
    }

    @JsonProperty("CACHE_ITEM_ID")
    public String getCACHE_ITEM_ID() {
        return CACHE_ITEM_ID;
    }
    public void setCACHE_ITEM_ID(String cACHE_ITEM_ID) {
        CACHE_ITEM_ID = cACHE_ITEM_ID;
    }

    @JsonProperty("DB_ID")
    public String getDB_ID() {
        return DB_ID;
    }
    public void setDB_ID(String dB_ID) {
        DB_ID = dB_ID;
    }

    @JsonProperty("DUMP_FILE")
    public String getDUMP_FILE() {
        return DUMP_FILE;
    }
    public void setDUMP_FILE(String dUMP_FILE) {
        DUMP_FILE = dUMP_FILE;
    }

    @Override
    public String toString() {
        return "ItemDetailsVO [SUM_INFO_ID=" + SUM_INFO_ID + ", MEM_SITE_ACCOUNT_ID=" + MEM_SITE_ACCOUNT_ID + ", CACHE_ITEM_ID="
                + CACHE_ITEM_ID + ", DBID=" + DB_ID + ", CLASS=" + CLASS + ", DUMPLINK="+ DUMP_FILE + "]";
    }



}
