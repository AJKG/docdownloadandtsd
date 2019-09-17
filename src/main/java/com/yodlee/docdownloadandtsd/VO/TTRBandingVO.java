package com.yodlee.docdownloadandtsd.VO;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TTRBanding")
public class TTRBandingVO {

    private String site_id;
    private String band;
    private String category;


    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
