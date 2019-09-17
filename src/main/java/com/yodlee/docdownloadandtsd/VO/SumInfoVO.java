package com.yodlee.docdownloadandtsd.VO;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CacheResponse")
public class SumInfoVO {
    private String sum_info_id;
    private String is_cacherun_disabled;
    private String tag_id;
    private String tag;
    private String site_id;
    private String display_name;
    private String locale_id;
    private String locale_key;
    private String country_name;
    private String mfa_type_id;
    private String mfa_type_name;
    private String base_url;
    private String is_beta;
    private String login_url;
    private boolean isABS;
    private boolean ifTTRRaised;
    private boolean TTR_Site;
    private String Total_request;
    private String success_percentage;
    private String YADNotes;
    private String MFADump;
    private boolean Token_UserDependentMFA;



    public String getSum_info_id() { return sum_info_id; }
    public void setSum_info_id(String sum_info_id) {
        this.sum_info_id = sum_info_id;
    }

    public String getIs_cacherun_disabled() {
        return is_cacherun_disabled;
    }
    public void setIs_cacherun_disabled(String is_cacherun_disabled) {
        this.is_cacherun_disabled = is_cacherun_disabled;
    }

    public String getTag_id() {
        return tag_id;
    }
    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSite_id() {
        return site_id;
    }
    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getDisplay_name() {
        return display_name;
    }
    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getLocale_id() {
        return locale_id;
    }
    public void setLocale_id(String locale_id) {
        this.locale_id = locale_id;
    }

    public String getLocale_key() {
        return locale_key;
    }
    public void setLocale_key(String locale_key) {
        this.locale_key = locale_key;
    }

    public String getCountry_name() {
        return country_name;
    }
    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getMfa_type_id() {
        return mfa_type_id;
    }
    public void setMfa_type_id(String mfa_type_id) {
        this.mfa_type_id = mfa_type_id;
    }

    public String getMfa_type_name() {
        return mfa_type_name;
    }
    public void setMfa_type_name(String mfa_type_name) {
        this.mfa_type_name = mfa_type_name;
    }

    public String getBase_url() {
        return base_url;
    }
    public void setBase_url(String base_url) {
        this.base_url = base_url;
    }

    public String getIs_beta() {
        return is_beta;
    }
    public void setIs_beta(String is_beta) {
        this.is_beta = is_beta;
    }

    public String getLogin_url() {
        return login_url;
    }
    public void setLogin_url(String login_url) {
        this.login_url = login_url;
    }


    public String getTotal_request() {
        return Total_request;
    }
    public void setTotal_request(String Total_Request) {
        this.Total_request = Total_Request;
    }

    public String getSuccess_percentage() { return success_percentage; }
    public void setSuccess_percentage(String success_percentage) {
        this.success_percentage = success_percentage;
    }

    public String getYADNotes() { return YADNotes; }
    public void setYADNotes(String YADNotes) {
        this.YADNotes = YADNotes;
    }

    public String getMFADump() { return MFADump; }
    public void setMFADump(String MFADump) {
        this.MFADump = MFADump;
    }


    public boolean isABS() {
        return isABS;
    }

    public void setABS(boolean ABS) {
        isABS = ABS;
    }

    public boolean isIfTTRRaised() {
        return ifTTRRaised;
    }

    public void setIfTTRRaised(boolean ifTTRRaised) {
        this.ifTTRRaised = ifTTRRaised;
    }

    public boolean isToken_UserDependentMFA() {
        return Token_UserDependentMFA;
    }

    public void setToken_UserDependentMFA(boolean token_UserDependentMFA) {
        Token_UserDependentMFA = token_UserDependentMFA;
    }


    public boolean isTTR_Site() {
        return TTR_Site;
    }

    public void setTTR_Site(boolean TTR_Site) {
        this.TTR_Site = TTR_Site;
    }
}
