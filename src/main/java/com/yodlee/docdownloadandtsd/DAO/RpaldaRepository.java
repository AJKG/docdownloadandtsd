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
import sun.misc.Cache;

import javax.print.Doc;
import java.util.*;


@Service
public class RpaldaRepository {

    @Autowired
    @Qualifier("rpaldaJdbcTemplate")
    JdbcTemplate jdbc;

    @Autowired
    @Qualifier("repaldaJdbcTemplate")
    JdbcTemplate jdbcRepalda;


    public List<Object> getDiff() throws Exception {

        List<Object> list = new ArrayList<>();


        List<DataDiffDetailsVO> dataDiffList = executeRpaldaQuery();

        Map<String, String> tsdParamValues = executeRepaldaTSDQuery(dataDiffList);



        List<TransactionSelectionDurationVO> tsdList = new ArrayList<>();
        List<CacheRunVO> cacheRunList = new ArrayList<>();
        List<DocDownloadVO> docDownloadList = new ArrayList<>();

        for(DataDiffDetailsVO diff : dataDiffList) {

            String dataDiff = diff.getDataDiff();

            if(dataDiff.contains("DOC_TYPE_SUMINFO_CONFIG")) {
                DocDownloadVO docDownload = new DocDownloadVO();
                docDownload.setSumInfoId(diff.getSumInfoId());
                docDownload.setAgentName(diff.getAgentName());
                docDownload.setMigId(diff.getMigId());
                docDownload.setMigratedBy(diff.getMigratedBy());
                docDownload.setRequestedDate(diff.getRequestedDate());
                setSeedAndProdDocDownload(docDownload, dataDiff);
                if(!isNullValue(docDownload.getDocDownloadSeed()) || !isNullValue(docDownload.getDocDownloadProd())) {
                    docDownloadList.add(docDownload);
                }
            }

            if(dataDiff.contains("SUM_INFO_PARAM_VALUE_ID") && !isNullValue(tsdParamValues.get(diff.getSumInfoId())) && dataDiff.contains(tsdParamValues.get(diff.getSumInfoId()))) {
                TransactionSelectionDurationVO tsd = new TransactionSelectionDurationVO();
                tsd.setMigId(diff.getMigId());
                tsd.setMigratedBy(diff.getMigratedBy());
                tsd.setAgentName(diff.getAgentName());
                tsd.setRequestedDate(diff.getRequestedDate());
                tsd.setSumInfoId(diff.getSumInfoId());
                setSeedAndProdTSD(tsd, dataDiff, tsdParamValues);
                if(!isNullValue(tsd.getTransactionDurationSeed()) || !isNullValue(tsd.getTransactionDurationProd())) {
                    tsdList.add(tsd);
                }
            }

            if(dataDiff.contains("IS_CACHERUN_DISABLED")) {
                CacheRunVO cacheRun = new CacheRunVO();
                cacheRun.setMigId(diff.getMigId());
                cacheRun.setAgentName(diff.getAgentName());
                cacheRun.setMigratedBy(diff.getMigratedBy());
                cacheRun.setRequestedDate(diff.getRequestedDate());
                cacheRun.setSumInfoId(diff.getSumInfoId());
                setSeedAndProdCacheRun(cacheRun, dataDiff);
                if(!isNullValue(cacheRun.getCacheRunSeed()) || !isNullValue(cacheRun.getCacheRunProd())) {
                    cacheRunList.add(cacheRun);
                }
            }

        }



        list.addAll(tsdList);
        list.addAll(docDownloadList);
        list.addAll(cacheRunList);

        return list;
    }

    public List<Object> getInput() throws Exception {

        List<Object> list = new ArrayList<>();

        //3664,1984,12151,10719,10659,12024,9688,26079,25976,7087
        int[] DocCSID = {};
        //5,803,3472,545,2422,1644,5702,11765,662,3247
        int[] TSDCSID = {};

        int[] CacheCSID = {};

        List<TransactionSelectionDurationVO> tsdList = new ArrayList<>();
        List<CacheRunVO> cacheRunList = new ArrayList<>();
        List<DocDownloadVO> docDownloadList = new ArrayList<>();

        System.out.println("Length of DocCSID: "+DocCSID.length+" | Length of TSDCSID: "+TSDCSID.length);

        if (DocCSID.length != 0){
            System.out.println("Getting in to Doc Input");
            for (int i = 0; i < DocCSID.length; i++) {
                DocDownloadVO docDownload = new DocDownloadVO();
                docDownload.setSumInfoId(""+DocCSID[i]);
                docDownload.setAgentName("AgentName");
                docDownload.setMigId(""+i+DocCSID[i]);
                docDownload.setMigratedBy("Test");
                docDownload.setRequestedDate("2019-07-30 00:00:00.0");
                docDownload.setDocDownloadSeed("1");
                docDownload.setDocDownloadProd("");
                if(!isNullValue(docDownload.getDocDownloadSeed()) || !isNullValue(docDownload.getDocDownloadProd())) {
                    docDownloadList.add(docDownload);
                }
            }
    }

        if (TSDCSID.length != 0){
            System.out.println("Getting into TSD Input");
            for (int i = 0; i < TSDCSID.length; i++) {
                TransactionSelectionDurationVO tsd = new TransactionSelectionDurationVO();
                tsd.setMigId(""+i+TSDCSID[i]);
                tsd.setMigratedBy("Test");
                tsd.setAgentName("AgentName");
                tsd.setRequestedDate("2019-07-30 00:00:00.0");
                tsd.setSumInfoId(""+TSDCSID[i]);
                tsd.setTransactionDurationSeed("365");
                tsd.setTransactionDurationProd("");
                if(!isNullValue(tsd.getTransactionDurationSeed()) || !isNullValue(tsd.getTransactionDurationProd())) {
                    tsdList.add(tsd);
                }
            }
        }

        if (CacheCSID.length != 0){
            for (int i = 0; i < CacheCSID.length; i++) {
                CacheRunVO cacheRun = new CacheRunVO();
                cacheRun.setMigId(""+i+CacheCSID[i]);
                cacheRun.setAgentName("AgentName");
                cacheRun.setMigratedBy("Test");
                cacheRun.setRequestedDate("2019-07-30 00:00:00.0");
                cacheRun.setSumInfoId(""+CacheCSID[i]);
                cacheRun.setCacheRunSeed("1");
                if(!isNullValue(cacheRun.getCacheRunSeed()) || !isNullValue(cacheRun.getCacheRunProd())) {
                    cacheRunList.add(cacheRun);
                }
            }
        }

        list.addAll(tsdList);
        list.addAll(docDownloadList);
        list.addAll(cacheRunList);

        return list;
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

    private List<DataDiffDetailsVO> executeRpaldaQuery(){

        List<DataDiffDetailsVO> dataDiffQueryOutput = new ArrayList<>();

        try{
            String sql = "select mig_id, migrated_by, sum_info_id, agent_name, data_diff, request_date from migration_request where REQUEST_DATE > sysdate-5 and data_diff is not null and migrated_by is not null";
            List<Map<String, Object>> rows = jdbc.queryForList(sql);

            for (Map row : rows) {
                DataDiffDetailsVO diff = new DataDiffDetailsVO();
                diff.setMigId(""+row.get("mig_id"));
                diff.setMigratedBy(""+row.get("migrated_by"));
                diff.setSumInfoId(""+row.get("sum_info_id"));
                diff.setDataDiff(""+row.get("data_diff"));
                diff.setAgentName(""+row.get("agent_name"));
                diff.setRequestedDate(""+row.get("request_date"));
                dataDiffQueryOutput.add(diff);
            }

        }catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
        return dataDiffQueryOutput;
    }

    private Map<String, String> executeRepaldaTSDQuery(List<DataDiffDetailsVO> dataDiffList){

        String allSumInfo = "";
        Map<String, String> tsdParamValues = new HashMap<>();

        for(DataDiffDetailsVO dddvo : dataDiffList) {
            allSumInfo = allSumInfo + dddvo.getSumInfoId() + ",";

        }

        allSumInfo = allSumInfo.substring(0, allSumInfo.length()-1);
        allSumInfo = allSumInfo.trim();

        String sqlSuminfo = "select sum_info_id,sum_info_param_value_id from sum_info_param_value where sum_info_id in ("+allSumInfo+") and sum_info_param_key_id = 34";
        List<Map<String, Object>> rowRepalda = new ArrayList<>();
        try {
            rowRepalda = jdbcRepalda.queryForList(sqlSuminfo);
        }catch(Exception e){
            System.out.println("Reattempting the Rpalda Connection");
            try {
                rowRepalda = jdbcRepalda.queryForList(sqlSuminfo);
            }catch(Exception e1){
                e1.printStackTrace();
            }
        }

        for (Map row : rowRepalda) {
            tsdParamValues.put(row.get("sum_info_id").toString(), row.get("sum_info_param_value_id").toString());
        }

        return tsdParamValues;
    }


}
