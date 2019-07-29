package com.yodlee.docdownloadandtsd.Services;


import com.yodlee.docdownloadandtsd.DAO.SplunkRepository;
import com.yodlee.docdownloadandtsd.VO.DocDownloadVO;
import com.yodlee.docdownloadandtsd.VO.DocResponseVO;
import com.yodlee.docdownloadandtsd.VO.FirememExtractedResponseForDocumentDownload;
import com.yodlee.docdownloadandtsd.VO.ItemDetailsVO;
import com.yodlee.docdownloadandtsd.exceptionhandling.LoginExceptionHandler;
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

@Service
public class DocDownloadRecertificationService {

    @Autowired
    SplunkService splunkService;

    @Autowired
    SplunkRepository splunkRepository;

    @Autowired
    HammerServicesImpl hammerServices;

    @Autowired
    AgentBaseNameService agentBasenameService;

    @Autowired
    YUVASegmentService yuvaSegmentService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public HashMap<DocResponseVO, ArrayList<FirememExtractedResponseForDocumentDownload>> docDownloadEnabled(DocDownloadVO ddvo, String sumInfoId, String msaOrCii) throws Exception{
        System.out.println("Getting in to DocDownload Recertification Method");

        HashMap<String, HashMap<String, Object>> jDapItemListFromBatch = TriggerBatchforDocDownload(sumInfoId, msaOrCii);

         ArrayList<FirememExtractedResponseForDocumentDownload> allFirememDataMap = hammerServices.retriveDataFromFirememForDocDownload(jDapItemListFromBatch, ddvo);


        int countPresent = 0;
        int countAbsent = 0;
        String messageFound = null;
        String messageNotFound = null;
        String message = null;


        HashMap<DocResponseVO, ArrayList<FirememExtractedResponseForDocumentDownload>> finalMap = new HashMap<>();

        for(FirememExtractedResponseForDocumentDownload fmData : allFirememDataMap) {

                if (fmData.isDocPresent()) {
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


        DocResponseVO dResponse = new DocResponseVO();
        dResponse.setSumInfoId(ddvo.getSumInfoId());
        dResponse.setIsDocPresent(message);
        dResponse.setDocPercentage(countPercent);
        dResponse.setMigId(ddvo.getMigId());
        dResponse.setMigratedBy(ddvo.getMigratedBy());
        dResponse.setDocDownloadSeed(ddvo.getDocDownloadSeed());
        dResponse.setDocDownloadProd(ddvo.getDocDownloadProd());
        dResponse.setRequestedDate(ddvo.getRequestedDate());
        dResponse.setMetaDataType("DocDownload");




        finalMap.put(dResponse, allFirememDataMap);

        return finalMap;

    }


    public HashMap<String, HashMap<String, Object>> TriggerBatchforDocDownload(String sumInfoId, String msaOrCii) throws Exception{
        System.out.println("Getting in to Trigger Batch for DocDownload Method");

        HashMap<String, HashMap<String, Object>> jDapItemListFromBatch = new HashMap<>();

        String agentName = splunkService.getAgentName(sumInfoId);
        agentName = agentName.trim();

        String agentBaseName = agentBasenameService.getAgentBaseName(agentName);




        ItemDetailsVO[] yuvaUsers = yuvaSegmentService.getYUVASegments(agentName, sumInfoId);


        String accessTokenId = hammerServices.hammerLogin();
        ArrayList<String> ignoreItemList = new ArrayList<String>();
        Integer batchDetailsId = null;

        try {
            batchDetailsId  = hammerServices.createBatchForDocumentDownload(yuvaUsers, accessTokenId, agentBaseName, msaOrCii);
        }catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        Integer batchReqDetailsId = hammerServices.triggerBatchForDocumentDownload(batchDetailsId, accessTokenId, "DL", "");
        JSONObject batchResultList = hammerServices.pollingTriggerBatch(batchReqDetailsId, accessTokenId);

        JSONArray jDapBatchResultArray = batchResultList.getJSONArray("batchResultList");

        System.out.println("DocDownload Batch Results Polling: "+jDapBatchResultArray);

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


        return jDapItemListFromBatch;
    }

}
