package com.yodlee.docdownloadandtsd.DAO;


import com.yodlee.docdownloadandtsd.VO.CacheRunVO;
import com.yodlee.docdownloadandtsd.VO.DataDiffDetailsVO;
import com.yodlee.docdownloadandtsd.VO.DocDownloadVO;
import com.yodlee.docdownloadandtsd.VO.TransactionSelectionDurationVO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class RpaldaRepository {

    @Autowired
    @Qualifier("rpaldaJdbcTemplate")
    JdbcTemplate jdbc;

    @Autowired
    @Qualifier("repaldaJdbcTemplate")
    JdbcTemplate jdbcRepalda;

    //Testing from Chetan's Account
    public ArrayList<String> getDiff() throws Exception {

        List<DataDiffDetailsVO> dataList = new ArrayList<>();
        List<TransactionSelectionDurationVO> tsdList = new ArrayList<>();
        List<CacheRunVO> cacheRunList = new ArrayList<>();
        List<DocDownloadVO> docDownloadList = new ArrayList<>();
        Map<String, String> tsdParamValues = new HashMap<>();

        try{
            String sql = "select mig_id,sum_info_id,migrated_by,data_diff,request_date from migration_request where REQUEST_DATE > sysdate-1 and data_diff is not null and migrated_by is not null";
            List<Map<String, Object>> rows = jdbc.queryForList(sql);

            for (Map row : rows) {
                DataDiffDetailsVO diff = new DataDiffDetailsVO();
                diff.setMigId(""+row.get("mig_id"));
                diff.setMigratedBy(""+row.get("migrated_by"));
                diff.setSumInfoId(""+row.get("sum_info_id"));
                diff.setDataDiff(""+row.get("data_diff"));
                dataList.add(diff);
            }

        }catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }

        String allSumInfo = "";

        for(DataDiffDetailsVO dddvo : dataList) {
            allSumInfo=allSumInfo+dddvo.getSumInfoId()+",";
        }

        allSumInfo = allSumInfo.substring(0, allSumInfo.length()-1);
        allSumInfo = allSumInfo.trim();

        System.out.println("allSumInfo : "+allSumInfo);

        String sqlSuminfo = "select sum_info_id,sum_info_param_value_id from sum_info_param_value where sum_info_id in ("+allSumInfo+") and sum_info_param_key_id = 34";
        List<Map<String, Object>> rowRepalda = jdbcRepalda.queryForList(sqlSuminfo);

        for (Map row : rowRepalda) {
            tsdParamValues.put(row.get("sum_info_id").toString(), row.get("sum_info_param_value_id").toString());
        }

        for(DataDiffDetailsVO diff : dataList) {

            String dataDiff = diff.getDataDiff();
            if(dataDiff.contains("SUM_INFO_PARAM_VALUE_ID") && !isNullValue(tsdParamValues.get(diff.getSumInfoId())) && dataDiff.contains(tsdParamValues.get(diff.getSumInfoId()))) {
                TransactionSelectionDurationVO tsd = new TransactionSelectionDurationVO();
                tsd.setMigId(diff.getMigId());
                tsd.setMigratedBy(diff.getMigratedBy());
                tsd.setRequestedDate(diff.getRequestedDate());
                tsd.setSumInfoId(diff.getSumInfoId());
                setSeedAndProdTSD(tsd, dataDiff, tsdParamValues);
                if(!isNullValue(tsd.getTransactionDurationSeed()) || !isNullValue(tsd.getTransactionDurationProd())) {
                    tsdList.add(tsd);
                }
            }
            if(dataDiff.contains("DOC_TYPE_SUMINFO_CONFIG")) {
                DocDownloadVO docDownload = new DocDownloadVO();
                docDownload.setMigId(diff.getMigId());
                docDownload.setMigratedBy(diff.getMigratedBy());
                docDownload.setRequestedDate(diff.getRequestedDate());
                docDownload.setSumInfoId(diff.getSumInfoId());
                setSeedAndProdDocDownload(docDownload, dataDiff);
                if(!isNullValue(docDownload.getDocDownloadSeed()) || !isNullValue(docDownload.getDocDownloadProd())) {
                    docDownloadList.add(docDownload);
                }
            }
            if(dataDiff.contains("IS_CACHERUN_DISABLED")) {
                CacheRunVO cacheRun = new CacheRunVO();
                cacheRun.setMigId(diff.getMigId());
                cacheRun.setMigratedBy(diff.getMigratedBy());
                cacheRun.setRequestedDate(diff.getRequestedDate());
                cacheRun.setSumInfoId(diff.getSumInfoId());
                setSeedAndProdCacheRun(cacheRun, dataDiff);
                if(!isNullValue(cacheRun.getCacheRunSeed()) || !isNullValue(cacheRun.getCacheRunProd())) {
                    cacheRunList.add(cacheRun);
                }
            }

        }


        System.out.println(tsdList.size());
        System.out.println(docDownloadList.size());
        System.out.println(cacheRunList.size());

        return null;
    }

    private static void setSeedAndProdTSD (TransactionSelectionDurationVO tsd, String dataDiff, Map<String, String> tsdParamValues) {

        JSONObject obj = new JSONObject(dataDiff);
        if(obj.has("SUN JVM-All Prod | scexag07s")) {

            JSONObject parentObj = obj.getJSONObject("SUN JVM-All Prod | scexag07s");
            if(parentObj.has(tsd.getSumInfoId())) {
                JSONObject sumInfoObj = parentObj.getJSONObject(tsd.getSumInfoId());
                if(sumInfoObj.has("SUM_INFO_PARAM_VALUE")) {
                    JSONArray tsdArray = sumInfoObj.getJSONArray("SUM_INFO_PARAM_VALUE");
                    for(int i = 0 ;i <tsdArray.length(); i++) {
                        JSONObject dbId = tsdArray.getJSONObject(i);
                        if(dbId.has("SEED")) {
                            JSONObject seedVal = dbId.getJSONObject("SEED");
                            if(seedVal.has("SUM_INFO_PARAM_VALUE_ID") && seedVal.get("SUM_INFO_PARAM_VALUE_ID").toString().contains(tsdParamValues.get(tsd.getSumInfoId()))) {
                                tsd.setTransactionDurationSeed(""+seedVal.get("PARAM_VALUE"));
                            }
                        }
                        if(dbId.has("PROD")) {
                            JSONObject prodVal = dbId.getJSONObject("PROD");
                            if(prodVal.has("SUM_INFO_PARAM_VALUE_ID") && prodVal.get("SUM_INFO_PARAM_VALUE_ID").toString().contains(tsdParamValues.get(tsd.getSumInfoId()))) {
                                tsd.setTransactionDurationProd(""+prodVal.get("PARAM_VALUE"));
                            }
                        }
                    }
                }
            }

        }

    }

    public static void setSeedAndProdDocDownload(DocDownloadVO docDownload, String dataDiff){

        JSONObject obj = new JSONObject(dataDiff);
        if(obj.has("SUN JVM-All Prod | scexag07s")) {

            JSONObject parentObj = obj.getJSONObject("SUN JVM-All Prod | scexag07s");
            if(parentObj.has(docDownload.getSumInfoId())) {
                JSONObject sumInfoObj = parentObj.getJSONObject(docDownload.getSumInfoId());
                if(sumInfoObj.has("DOC_TYPE_SUMINFO_CONFIG")) {
                    JSONArray docArray = sumInfoObj.getJSONArray("DOC_TYPE_SUMINFO_CONFIG");
                    for(int i = 0 ;i <docArray.length(); i++) {
                        JSONObject dbId = docArray.getJSONObject(i);
                        if(dbId.has("SEED")) {
                            JSONObject seedVal = dbId.getJSONObject("SEED");
                            if(seedVal.has("IS_DOC_DWNLD_EN")) {
                                docDownload.setDocDownloadSeed(""+seedVal.get("IS_DOC_DWNLD_EN"));
                            }
                        }
                        if(dbId.has("PROD")) {
                            JSONObject prodVal = dbId.getJSONObject("PROD");
                            if(prodVal.has("IS_DOC_DWNLD_EN")) {
                                docDownload.setDocDownloadProd(""+prodVal.get("IS_DOC_DWNLD_EN"));
                            }
                        }
                    }
                }
            }

        }

    }

    public static void setSeedAndProdCacheRun(CacheRunVO cacheRun, String dataDiff){

        JSONObject obj = new JSONObject(dataDiff);
        if(obj.has("SUN JVM-All Prod | scexag07s")) {

            JSONObject parentObj = obj.getJSONObject("SUN JVM-All Prod | scexag07s");
            if(parentObj.has(cacheRun.getSumInfoId())) {
                JSONObject sumInfoObj = parentObj.getJSONObject(cacheRun.getSumInfoId());
                if(sumInfoObj.has("SUM_INFO")) {
                    JSONArray docArray = sumInfoObj.getJSONArray("SUM_INFO");
                    for(int i = 0 ;i <docArray.length(); i++) {
                        JSONObject dbId = docArray.getJSONObject(i);
                        if(dbId.has("SEED")) {
                            JSONObject seedVal = dbId.getJSONObject("SEED");
                            if(seedVal.has("IS_CACHERUN_DISABLED")) {
                                cacheRun.setCacheRunSeed(""+seedVal.get("IS_CACHERUN_DISABLED"));
                            }
                        }
                        if(dbId.has("PROD")) {
                            JSONObject prodVal = dbId.getJSONObject("PROD");
                            if(prodVal.has("IS_CACHERUN_DISABLED")) {
                                cacheRun.setCacheRunProd(""+prodVal.get("IS_CACHERUN_DISABLED"));
                            }
                        }
                    }
                }
            }

        }

    }

    public static boolean isNullValue(String value) {
        return (value == null || value.trim().length() == 0 || value.trim().equalsIgnoreCase("null")) ? true:false;
    }

}
