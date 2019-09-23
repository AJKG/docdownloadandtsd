package com.yodlee.docdownloadandtsd.DAO;


import com.yodlee.docdownloadandtsd.Services.SplunkService;
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

    @Autowired
    SplunkService splunkService;


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

    public List<Object> getInput(String sumInfo, String TSDorDoc) throws Exception {

        List<Object> list = new ArrayList<>();

        //3664,1984,12151,10719,10659,12024,9688,26079,25976,7087
        ArrayList DocCSID = new ArrayList();
        //5,803,3472,545,2422,1644,5702,11765,662,3247
        ArrayList TSDCSID = new ArrayList();

        if(TSDorDoc.toLowerCase().equals("doc")) {
          DocCSID.add(Integer.parseInt(sumInfo));
        }else if(TSDorDoc.toLowerCase().equals("tsd")){
            TSDCSID.add(Integer.parseInt(sumInfo));
        }


        List<TransactionSelectionDurationVO> tsdList = new ArrayList<>();
        List<DocDownloadVO> docDownloadList = new ArrayList<>();

        System.out.println("Length of DocCSID: "+DocCSID.size()+" | Length of TSDCSID: "+TSDCSID.size());

        if (DocCSID.size() != 0){
            System.out.println("Getting in to Doc Input");
            for (Object docCsid : DocCSID) {
                DocDownloadVO docDownload = new DocDownloadVO();
                docDownload.setSumInfoId(""+docCsid.toString());
                docDownload.setAgentName(splunkService.getAgentName(""+docCsid.toString()));
                docDownload.setMigId(""+docCsid.toString());
                docDownload.setRecertification(true);
                Date date = new Date();
                docDownload.setRequestedDate(date.toString());
                docDownload.setDocDownloadSeed("1");
                docDownload.setDocDownloadProd("");
                if(!isNullValue(docDownload.getDocDownloadSeed()) || !isNullValue(docDownload.getDocDownloadProd())) {
                    docDownloadList.add(docDownload);
                }
            }
    }

        if (TSDCSID.size() != 0){
            System.out.println("Getting into TSD Input");
            for (Object tsdCsid : TSDCSID) {
                TransactionSelectionDurationVO tsd = new TransactionSelectionDurationVO();
                tsd.setMigId(""+tsdCsid.toString());
                tsd.setAgentName(splunkService.getAgentName(""+tsdCsid.toString()));
                Date date = new Date();
                tsd.setRequestedDate(date.toString());
                tsd.setSumInfoId(""+tsdCsid.toString());
                //HardCoding value as per need.
                tsd.setTransactionDurationSeed("730");
                tsd.setTransactionDurationProd("");
                if(!isNullValue(tsd.getTransactionDurationSeed()) || !isNullValue(tsd.getTransactionDurationProd())) {
                    tsdList.add(tsd);
                }
            }
        }


        list.addAll(tsdList);
        list.addAll(docDownloadList);

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
