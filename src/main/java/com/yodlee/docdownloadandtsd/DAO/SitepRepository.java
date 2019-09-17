package com.yodlee.docdownloadandtsd.DAO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;


@Service
public class SitepRepository {


    @Autowired
    @Qualifier("sitepJdbcTemplate")
    JdbcTemplate jdbc;



    public List<Map<String, Object>> getCSID(String sumInfo) throws Exception{
        if(sumInfo.toLowerCase().equals("all")){
            sumInfo = "";
        }
        try {
            String sql = "select si.sum_info_id as sum_info_id,"
                    + "si.is_cacherun_disabled as is_cacherun_disabled,"
                    + "si.tag_id as tag_id,"
                    + "COALESCE(t.tag,'Bills') as tag,"
                    + "si.SITE_ID as site_id,"
                    + "dmc.value as display_name,"
                    + "s.PRIMARY_LOCALE_ID as locale_id,"
                    + "l.locale_key as locale_key,"
                    + "COALESCE(c.country_name,'GLOBAL') as country_name,"
                    + "COALESCE(si.MFA_TYPE_ID,0) as mfa_type_id,"
                    + "COALESCE(mt.MFA_TYPE_NAME,'NON_MFA') as mfa_type_name,"
                    + "si.BASE_URL as base_url,"
                    + "si.IS_BETA as is_beta,"
                    + "lf.login_url as login_url "
                    + "from sum_info si,tag t,"
                    + "mfa_type @ REPALDA mt,"
                    + "db_message_catalog @ REPALDA dmc,"
                    + "site @ REPALDA s,locale @ REPALDA l,"
                    + "country @ REPALDA c,"
                    + "login_form @ REPALDA lf "
                    + "where s.site_id=si.site_id "
                    + "and s.mc_key=dmc.mc_key (+)"
                    + "and si.sum_info_id in ("+sumInfo+")"
                    + "and si.is_beta!=1"
                    + "and si.tag_id in (5,65,4,21,12,22) "
                    + "and l.COUNTRY_ID=c.COUNTRY_ID (+)"
                    + "and si.login_form_id=lf.login_form_id "
                    + "and s.PRIMARY_LOCALE_ID=l.LOCALE_ID "
                    + "and s.PRIMARY_LOCALE_ID=dmc.LOCALE_ID "
                    + "and si.MFA_TYPE_ID=mt.MFA_TYPE_ID(+)"
                    + "and si.tag_id=t.tag_id (+)"
                    + "and si.is_ready=1 "
                    + "and si.is_cacherun_disabled=1";


            List<Map<String, Object>> rows = jdbc.queryForList(sql);


            return rows;
        }catch (EmptyResultDataAccessException e) {
        System.out.println(e);
        return null; }

    }

}


