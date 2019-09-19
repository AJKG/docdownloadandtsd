package com.yodlee.docdownloadandtsd.Services;


import com.gargoylesoftware.htmlunit.WebClient;
import com.yodlee.docdownloadandtsd.DAO.RpaldaRepository;
import com.yodlee.docdownloadandtsd.DAO.SplunkRepository;
import com.yodlee.docdownloadandtsd.VO.*;
import com.yodlee.docdownloadandtsd.exceptionhandling.LoginExceptionHandler;
import com.yodlee.docdownloadandtsd.exceptionhandling.NullPointerExceptionHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

    @Autowired
    YUVASegmentService yuvaSegmentService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public HashMap<TSDResponseVO, ArrayList<FirememExtractedResponseForTSD>> transactionDurationdEnabled(TransactionSelectionDurationVO tsdvo, String sumInfoId, String tsd) throws Exception{
        System.out.println("Getting in to loop for TSD Duration Enabled");

        int noOfItems = 0;

        HashMap<String, HashMap<String, Object>> jDapItemListFromBatch = TriggerBatchForTSD(sumInfoId, tsd);

        ArrayList<FirememExtractedResponseForTSD> allFirememDataMap = null;
        if(jDapItemListFromBatch!=null) {
             allFirememDataMap = hammerServices.retriveDataFromFirememForTSD(jDapItemListFromBatch, tsd, tsdvo);
        }


        int countPresent = 0;
        int countAbsent = 0;
        String messageFound = null;
        String messageNotFound = null;
        String message = null;
        int max_value = -1;

        HashMap<TSDResponseVO, ArrayList<FirememExtractedResponseForTSD>> finalMap = new HashMap<>();

        if(allFirememDataMap!=null) {
            noOfItems = allFirememDataMap.size();
            for (FirememExtractedResponseForTSD fmData : allFirememDataMap) {


                if (rpaldaRepository.isNullValue(fmData.getJdapXMLResponse())) {
                    continue;
                }

                if (fmData.isTsdGenuine()) {
                    if (messageFound == null) {
                        messageFound = "Yes";
                    }
                    countPresent = countPresent + 1;
                } else {
                    if (messageNotFound == null) {
                        messageNotFound = "No";
                    }
                    countAbsent = countAbsent + 1;
                }

                String max_date = (fmData).getIsTSDPresent();
                if (max_date.matches("[0-9]+")) {
                    int valMaxDate = Integer.parseInt(max_date);
                    if (max_value < valMaxDate) {
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

        System.out.println("count Present TSD : "+countPresent);
        System.out.println("count Absent TSD: "+countAbsent);

        String countPercent = null;
        if(Integer.toString(countPresent).equals("0") && Integer.toString(countAbsent).equals("0")) {
            countPercent = "0%";
        }else {
            int countFinal = countPresent + countAbsent;
            float countUpdated = (float)countPresent/countFinal;
            countUpdated = countUpdated * 100;
            System.out.println(countUpdated);
            countPercent = countUpdated + "%";
        }

        String maxTSDFM = null;

        if(max_value==-1) {
            maxTSDFM = "-1";
        }else{
            maxTSDFM = Integer.toString(max_value);
        }



        TSDResponseVO tResponse = new TSDResponseVO();
        tResponse.setSumInfoId(tsdvo.getSumInfoId());
        tResponse.setIsTSDPresent(message);
        tResponse.setTSDPercentage(countPercent);
        tResponse.setMigId(tsdvo.getMigId());
        tResponse.setNoOfItems(""+noOfItems);
        tResponse.setAgentName(tsdvo.getAgentName());
        tResponse.setMigratedBy(tsdvo.getMigratedBy());
        tResponse.setTransactionSelectionDurationSeed(tsdvo.getTransactionDurationSeed());
        //Overriding the production value based on firemem.
        tResponse.setTransactionSelectionDurationProd(maxTSDFM);
        tResponse.setRequestedDate(tsdvo.getRequestedDate());
        tResponse.setMetaDataType("TransactionSelectionDuration");





        finalMap.put(tResponse, allFirememDataMap);

        return finalMap;

    }

    private HashMap<String, String> getRequestIDs(ItemDetailsVO[] yuvaPojo, String tsd, String token) throws Exception {
        HashMap<String, String> itemReqIDMap=new HashMap<String,String>();

        for(ItemDetailsVO yuvaItem : yuvaPojo) {

            String reqID = hammerServices.triggerFiremem(yuvaItem.getCacheItemId(), yuvaItem.getDataBase(), tsd, token);
            if(!rpaldaRepository.isNullValue(reqID)) {
                itemReqIDMap.put(yuvaItem.getCacheItemId(), reqID);
            }

        }
        return itemReqIDMap;
    }

    private String modifiedFirememDumpLink(String dumpLink) {
        if(dumpLink.contains("dumpdispatcher")) {
            String modifiedDumpLink=dumpLink.replaceAll("\\?id=", "/").replaceAll("dumpdispatcher","downloads");
            String shortenedDump=modifiedDumpLink.substring(0, modifiedDumpLink.indexOf(".html")+5);
            return shortenedDump;
        }
        return dumpLink;
    }

    public HashMap<String, HashMap<String, Object>> TriggerBatchForTSD(String sumInfoId, String tsd) throws Exception{
        System.out.println("Getting in to TriggerBatchForTSD");

        HashMap<String, HashMap<String, Object>> jDapItemListFromBatch = new HashMap<String, HashMap<String, Object>>();

        String agentName = splunkService.getAgentName(sumInfoId);
        agentName = agentName.trim();


        ItemDetailsVO[] yuvaUsers = yuvaSegmentService.getYUVASegments(agentName, sumInfoId);

        if(yuvaUsers.length==0){
            return jDapItemListFromBatch;
        }


        String accessTokenId = hammerServices.hammerLogin();




        ArrayList<String> ignoreItemList = new ArrayList<String>();


        HashMap<String, String> itemReqIDMap1 = getRequestIDs(yuvaUsers, tsd, accessTokenId);

        int itemNo = 0;
        for(Map.Entry<String, String> itemMap : itemReqIDMap1.entrySet()) {
            itemNo++;
            DumpDetailsVO ddvo = hammerServices.getDumpLink(itemMap.getValue(), itemMap.getKey(), sumInfoId, accessTokenId, itemNo);

            String dumpUrl = ddvo.getDumpUrl();
            if(rpaldaRepository.isNullValue(dumpUrl)) {

                continue;
            }
            String dbName = dumpUrl.substring(dumpUrl.indexOf("dbName=")+"dbName=".length(), dumpUrl.indexOf("&cobrandID"));
            Integer errCode = ddvo.getStatusCode();
            String itemId = ddvo.getCacheItem();


            HashMap<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("itemType", "cii");
            dataMap.put("dumpUrl", dumpUrl);
            dataMap.put("fmCode", errCode);

            jDapItemListFromBatch.put(itemId + "|" + dbName, dataMap);
        }
        return jDapItemListFromBatch;
    }

}
