package com.yodlee.docdownloadandtsd.Services;


import com.yodlee.docdownloadandtsd.DAO.RpaldaRepository;
import com.yodlee.docdownloadandtsd.DAO.SplunkRepository;
import com.yodlee.docdownloadandtsd.VO.DocDownloadVO;
import com.yodlee.docdownloadandtsd.VO.FirememExtractedResponseForTSD;
import com.yodlee.docdownloadandtsd.VO.ItemDetailsVO;
import com.yodlee.docdownloadandtsd.VO.TransactionSelectionDurationVO;
import com.yodlee.docdownloadandtsd.exceptionhandling.LoginExceptionHandler;
import com.yodlee.docdownloadandtsd.exceptionhandling.NullPointerExceptionHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class TSDRecertificationService {

    @Autowired
    SplunkService splunkService;

    @Autowired
    SplunkRepository splunkRepository;

    @Autowired
    HammerServicesImpl hammerServices;

    @Autowired
    RpaldaRepository rpaldaRepository;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public HashMap<HashMap<String, Object>, HashMap<String, String>> transactionDurationdEnabled(String sumInfoId, String tsd) throws Exception{

        String agentName = splunkService.getAgentName(sumInfoId);

        ItemDetailsVO[] yuvaPojo = null;
        agentName = agentName.trim();

        try {
            yuvaPojo = splunkService.getyuvasegmentusers(agentName, sumInfoId);
        } catch (HttpClientErrorException httpClientErrorException) {
            logger.info("Retrive item from Yuva/splunk error:"
                    + Arrays.toString(httpClientErrorException.getStackTrace()));
            throw new LoginExceptionHandler("Splunk Login Failure :" + httpClientErrorException.getMessage());
        }

        String accessTokenId = hammerServices.hammerLogin();

        // please remove comment
        ArrayList<String> agentBaseList = new ArrayList<String>();
        agentBaseList.add(agentName);
        Map<String, String> baseAgentMap = new HashMap<String, String>();
        try {
            baseAgentMap = splunkRepository.getAgentBase(agentBaseList);
        } catch (Exception exception) {
            logger.info("Issue with Splunk : " + Arrays.toString(exception.getStackTrace()));
            throw new NullPointerExceptionHandler("Issue with Splunk :" + exception.getMessage());
        }

        if (baseAgentMap.isEmpty()) {
            logger.info("Please provide correct agent name (case-sensetive) : " + agentName);
            throw new NullPointerExceptionHandler("Please provide correct agent name (case-sensetive)");
        }
        String agentBaseName = baseAgentMap.get(agentName);
        if (agentBaseName.equals("AgentBase") || agentBaseName.equals("Scripts")) {
            agentBaseName = agentName;
        }
        HashMap<String, HashMap<String, Object>> jDapItemListFromBatch = new HashMap<String, HashMap<String, Object>>();
        ArrayList<String> ignoreItemList = new ArrayList<String>();

        Integer batchDetailsId = hammerServices.createBatchForTSD(yuvaPojo, accessTokenId, agentBaseName, "msa");
        Integer batchReqDetailsId = hammerServices.triggerBatchForTSD(batchDetailsId, accessTokenId, "DL", "", tsd);
        JSONObject batchResultList = hammerServices.pollingTriggerBatch(batchReqDetailsId, accessTokenId);

        JSONArray jDapBatchResultArray = batchResultList.getJSONArray("batchResultList");

        System.out.println(jDapBatchResultArray);

        for (int i = 0; i < jDapBatchResultArray.length(); i++) {
            JSONObject itemObj = jDapBatchResultArray.getJSONObject(i);
            String dumpUrl = itemObj.optString("dumpUrl");
            Integer itemId = itemObj.optInt("itemId");
            Integer fmLatency = itemObj.optInt("fmLatency");
            Integer fmCode = itemObj.optInt("fmCode");
            String dbName = itemObj.optString("dbName");
            String respType = itemObj.optString("responseType");
            String sumInfoIds = itemObj.optString("sumInfoId");

            if (dumpUrl == null || "".equals(dumpUrl)) {
                ignoreItemList.add(itemId + "|" + dbName);
                continue;
            }

            if(!rpaldaRepository.isNullValue(respType) && !respType.equalsIgnoreCase("containerresponse")
            && !rpaldaRepository.isNullValue(sumInfoIds)&& !sumInfoId.trim().equalsIgnoreCase(sumInfoIds)) {
                ignoreItemList.add(itemId + "|" + dbName);
                continue;
            }



            HashMap<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("dumpUrl", dumpUrl);
            dataMap.put("fmLatency", fmLatency);
            dataMap.put("fmCode", fmCode);

            jDapItemListFromBatch.put(itemId + "|" + dbName, dataMap);
        }

        HashMap<String, Object> allFirememDataMap = hammerServices.retriveDataFromFirememForTSD(jDapItemListFromBatch, tsd);

        HashMap<String, String> dataValues = new HashMap<>();
        HashMap<HashMap<String, Object>, HashMap<String, String>> finalMap = new HashMap<>();

        int countPresent = 0;
        int countAbsent = 0;
        String messageFound = null;
        String messageNotFound = null;
        String message = null;
        int max_value = -1;

        for(Map.Entry<String, Object> fmData : allFirememDataMap.entrySet()) {

            Object ob = fmData.getValue();

            if(ob instanceof FirememExtractedResponseForTSD) {

                if(rpaldaRepository.isNullValue(((FirememExtractedResponseForTSD) ob).getJdapXMLResponse())){
                        continue;
                }

                if (((FirememExtractedResponseForTSD) ob).isTsdGenuine()) {
                    System.out.println("here111");
                    if(messageFound == null) {
                        messageFound = "Yes";
                    }
                    countPresent=countPresent+1;
                }
                else{
                    System.out.println("here222");
                    if(messageNotFound == null) {
                        messageNotFound = "No";
                    }
                    countAbsent=countAbsent+1;
                }

                String max_date = ((FirememExtractedResponseForTSD) ob).getIsTSDPresent();
                    if(max_date.matches("[0-9]+")) {
                        int valMaxDate = Integer.parseInt(max_date);
                        if(max_value < valMaxDate) {
                            max_value = valMaxDate;
                        }
                    }
            }

        }

        if(messageFound==null && messageNotFound==null) {
            message = "No user is successful need to verify the variation";
        }else if(messageFound != null){
            message = messageFound;
        }else{
            message = messageNotFound;
        }

        System.out.println("count Present : "+countPresent);
        System.out.println("count Absent : "+countAbsent);

        String countPercent = null;
        if(countPresent==0 && countAbsent==0) {
            countPercent = "0%";
        }else {
            countPercent = Integer.toString((countPresent / (countAbsent + countPresent)) * 100);
            countPercent = countPercent + "%";
        }

        String maxTSDFM = null;

        if(max_value==-1) {
            maxTSDFM = "Variation..need to verify the TSD...";
        }else{
            maxTSDFM = Integer.toString(max_value);
        }

        dataValues.put("isTSDPresent", message);
        dataValues.put("tsdPercentage", countPercent);
        dataValues.put("maxTSD", maxTSDFM);

        finalMap.put(allFirememDataMap, dataValues);

        return finalMap;

    }

}
