package com.yodlee.docdownloadandtsd.VO;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ABSList")
public class ABSListVO {


    private String SiteName;
    private String Locale;


    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String siteName) {
        SiteName = siteName;
    }

    public String getLocale() {
        return Locale;
    }

    public void setLocale(String locale) {
        Locale = locale;
    }
}
