package com.yodlee.docdownloadandtsd.Services;


import com.yodlee.docdownloadandtsd.DAO.SplunkRepository;
import com.yodlee.docdownloadandtsd.VO.DocDownloadVO;
import com.yodlee.docdownloadandtsd.VO.FirememExtractedResponseForDocumentDownload;
import com.yodlee.docdownloadandtsd.VO.ItemDetailsVO;
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
public class DocDownloadRecertificationService {

    @Autowired
    SplunkService splunkService;

    @Autowired
    SplunkRepository splunkRepository;

    @Autowired
    HammerServicesImpl hammerServices;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public HashMap<HashMap<String, Object>, HashMap<String, String>> docDownloadEnabled(DocDownloadVO ddvo, String sumInfoId, String msaOrCii) throws Exception{

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
        Integer batchDetailsId = null;

        try {
            batchDetailsId  = hammerServices.createBatchForDocumentDownload(yuvaPojo, accessTokenId, agentBaseName, msaOrCii);
        }catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        Integer batchReqDetailsId = hammerServices.triggerBatchForDocumentDownload(batchDetailsId, accessTokenId, "DL", "");
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

            if (dumpUrl == null || "".equals(dumpUrl)) {
                ignoreItemList.add(itemId + "|" + dbName);
                continue;
            }

            HashMap<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("dumpUrl", dumpUrl);
            dataMap.put("fmLatency", fmLatency);
            dataMap.put("fmCode", fmCode);

            jDapItemListFromBatch.put(itemId + "|" + dbName, dataMap);
        }

        HashMap<String, Object> allFirememDataMap = hammerServices.retriveDataFromFirememForDocDownload(jDapItemListFromBatch);

        HashMap<String, String> dataValues = new HashMap<>();
        HashMap<HashMap<String, Object>, HashMap<String, String>> finalMap = new HashMap<>();

        int countPresent = 0;
        int countAbsent = 0;
        String messageFound = null;
        String messageNotFound = null;
        String message = null;

        for(Map.Entry<String, Object> fmData : allFirememDataMap.entrySet()) {

            Object ob = fmData.getValue();

            if(ob instanceof FirememExtractedResponseForDocumentDownload) {

                if (((FirememExtractedResponseForDocumentDownload) ob).isDocPresent()) {
                    if(messageFound==null) {
                        messageFound = "Yes";
                    }
                    countPresent++;
                }
                else{
                    if(messageNotFound == null) {
                        messageNotFound = "No";
                    }
                    countAbsent++;
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

        String countPercent = null;
        if(Integer.toString(countPresent).equals("0") && Integer.toString(countAbsent).equals("0")) {
            System.out.println("here1111====");
            countPercent = "0%";
        }else {
            int countFinal = countPresent + countAbsent;
            float countUpdated = (float)countPresent/countFinal;
            countUpdated = countUpdated * 100;
            System.out.println(countUpdated);
            countPercent = countUpdated + "%";
        }

        dataValues.put("isDocPresent", message);
        dataValues.put("docPercentage", countPercent);
        dataValues.put("migID", ddvo.getMigId());
        dataValues.put("migratedBy", ddvo.getMigratedBy());
        dataValues.put("docDownloadSeed", ddvo.getDocDownloadSeed());
        dataValues.put("docDownloadProd", ddvo.getDocDownloadProd());
        dataValues.put("requestedDate", ddvo.getRequestedDate());
        dataValues.put("type", "DocDownloadVO");
        dataValues.put("sumInfoID", ddvo.getSumInfoId());

        finalMap.put(allFirememDataMap, dataValues);

        return finalMap;

    }

}
