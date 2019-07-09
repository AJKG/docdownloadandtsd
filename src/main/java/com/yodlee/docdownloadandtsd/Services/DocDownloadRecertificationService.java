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

    public String docDownloadEnabled(DocDownloadVO ddvo) throws Exception{

        System.out.println("here===");

        String sumInfoId = "663";
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

        Integer batchDetailsId = hammerServices.createBatch(yuvaPojo, accessTokenId, agentBaseName);
        Integer batchReqDetailsId = hammerServices.triggerBatch(batchDetailsId, accessTokenId, "DL", "");
        JSONObject batchResultList = hammerServices.pollingTriggerBatch(batchReqDetailsId, accessTokenId);

        JSONArray jDapBatchResultArray = batchResultList.getJSONArray("batchResultList");

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

        System.out.println(""+allFirememDataMap);

        for(Map.Entry<String, Object> fmData : allFirememDataMap.entrySet()) {

            Object ob = fmData.getValue();

            if(ob instanceof FirememExtractedResponseForDocumentDownload) {

                System.out.println(""+((FirememExtractedResponseForDocumentDownload) ob).isDocPresent());

                if (((FirememExtractedResponseForDocumentDownload) ob).isDocPresent()) {
                    return "Yes";
                }else if(((FirememExtractedResponseForDocumentDownload) ob).getErrorCode().equals("518")) {
                    return "Unable to verify for MFA users";
                }

            }

        }

        return "No";

    }

    public void docDownloadDisabled(DocDownloadVO ddvo) throws Exception {

    }


}
