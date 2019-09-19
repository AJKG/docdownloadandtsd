package com.yodlee.docdownloadandtsd.Controller;


import com.fasterxml.jackson.core.JsonParseException;
import com.yodlee.docdownloadandtsd.DAO.DBAccessRepositoryImpl;
import com.yodlee.docdownloadandtsd.DAO.RpaldaRepository;
import com.yodlee.docdownloadandtsd.DAO.SitepRepository;
import com.yodlee.docdownloadandtsd.DAO.SplunkRepository;
import com.yodlee.docdownloadandtsd.Services.*;
import com.yodlee.docdownloadandtsd.VO.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.yodlee.docdownloadandtsd.authenticator.Authorization;
import sun.misc.Cache;

import java.io.IOException;
import java.util.*;

@Controller
@RestController
@Api(value = "BifrostControllerAPI", produces = MediaType.APPLICATION_JSON_VALUE)
public class DDTsdController {

    @Autowired
    RpaldaService rpaldaService;

    @Autowired
    CacheRefreshRecertificationService cacheRefreshRecertificationService;

    @Autowired
    RpaldaRepository rpaldaRepository;

    @Autowired
    DocDownloadRecertificationService docDownloadRecService;

    @Autowired
    TSDRecertificationService tsdRecertificationService;

    @Autowired
    DBAccessRepositoryImpl dbAccessRepository;

    @Autowired
    ObjectResponseToJSON objectResponseToJSON;

    @Autowired
    SplunkRepository splunkRepository;

    @Autowired
    SitepRepository sitepRepository;

    @Autowired
    Authorization authorization;

    @Autowired
    HammerAuthenticationService hammerAuthenticationService;

    

    @RequestMapping(value="/MetaDataMonitoring",method={RequestMethod.GET})
    @ResponseBody
    public HashMap<Object, HashMap<String, Object>> getDBPushDifference(String sumInfo, String TSDorDoc) throws Exception{
        List<Object> resultList = null;
        if(sumInfo.toLowerCase().equals("all")) {
            resultList = rpaldaService.getDiff();
        }else {
            resultList = rpaldaService.getInput(sumInfo, TSDorDoc);
        }
        HashMap<Object, HashMap<String, Object>> finalResponse = new HashMap<>();

        HashMap<DocResponseVO, ArrayList<FirememExtractedResponseForDocumentDownload>> obj = null;
        HashMap<TSDResponseVO, ArrayList<FirememExtractedResponseForTSD>> objT = null;

        for(Object res : resultList) {
            System.out.println("Looping for CSID");

            List<Object> usersList = new ArrayList<>();

            String sumInfoId = "" ;

            if(res instanceof DocDownloadVO) {

                System.out.println("Getting in to loop for DOC Download");
                sumInfoId = ((DocDownloadVO) res).getSumInfoId();
                if(((DocDownloadVO) res).getDocDownloadSeed().equals("1")) {
                        obj = docDownloadRecService.docDownloadEnabled((DocDownloadVO) res, sumInfoId, "cii");
                }else if(((DocDownloadVO) res).getDocDownloadSeed().equals("0")) {
                        obj = docDownloadRecService.docDownloadEnabled((DocDownloadVO) res, sumInfoId, "msa");
                }

                for (Map.Entry<DocResponseVO, ArrayList<FirememExtractedResponseForDocumentDownload>> hmap : obj.entrySet()) {
                    DocResponseVO ddr  = hmap.getKey();
                    ArrayList<FirememExtractedResponseForDocumentDownload> ddrU = hmap.getValue();

                    System.out.println("Inserting for Doc in to DB: "+sumInfoId);
                    dbAccessRepository.AddDocResponseToDB(ddr);

                    if(ddrU!=null) {
                        for (FirememExtractedResponseForDocumentDownload ItemList : ddrU) {
                            dbAccessRepository.AddUserResponse(ItemList);
                        }
                    }


                }
            }

            else if(res instanceof TransactionSelectionDurationVO) {

                String tsdProd = ""+((TransactionSelectionDurationVO) res).getTransactionDurationProd();
                String tsdSeed = ""+((TransactionSelectionDurationVO) res).getTransactionDurationSeed();
                sumInfoId = ((TransactionSelectionDurationVO) res).getSumInfoId();

                if(rpaldaRepository.isNullValue(tsdProd)) {
                    tsdProd = "90";
                }
                System.out.println("Getting in to loop for TSD");
                if(Integer.parseInt(tsdProd) > Integer.parseInt(tsdSeed)) {
                     objT = tsdRecertificationService.transactionDurationdEnabled((TransactionSelectionDurationVO)res,sumInfoId, tsdProd);
                }else{
                     objT = tsdRecertificationService.transactionDurationdEnabled((TransactionSelectionDurationVO)res,sumInfoId, tsdSeed);
                }

                for (Map.Entry<TSDResponseVO, ArrayList<FirememExtractedResponseForTSD>> hmapT : objT.entrySet()) {
                    TSDResponseVO ddT  = hmapT.getKey();
                    ArrayList<FirememExtractedResponseForTSD> ddrTU = hmapT.getValue();

                    System.out.println("Inserting for TSD in to DB: "+sumInfoId);
                    dbAccessRepository.AddTSDResponseToDB(ddT);

                    if(ddrTU!=null) {
                        for (FirememExtractedResponseForTSD ItemList : ddrTU) {
                            dbAccessRepository.AddUserResponse(ItemList);
                        }
                    }

                }
            }

        }

        System.out.println("Execution Finished");
        return finalResponse;
    }

    @RequestMapping(value="/getCache",method={RequestMethod.GET})
    @ApiOperation("Get the response for Cache")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = SumInfoVO.class)})
    @ResponseBody
    public List<SumInfoVO> getCacheRunDisabledCSID(String sumInfo) throws Exception{

        System.out.println("Printing Sum_Info: "+sumInfo);

        List<SumInfoVO> CacheMap = dbAccessRepository.getCacheResponseFromDB(sumInfo);

        if(CacheMap.size()==0) {
            SumInfoVO sumInfoVO = new SumInfoVO();
            sumInfoVO.setSum_info_id(sumInfo);
            sumInfoVO.setIs_cacherun_disabled("Sum_Info_Id is being processed. Please wait for sometime.");
            System.out.println("Inserting the Sum_info for Cache Response");
            dbAccessRepository.AddCacheResponseToDB(sumInfoVO);

            List<SumInfoVO> Sum_info = cacheRefreshRecertificationService.getCSIDForEnablement(sumInfo);
            CacheMap = dbAccessRepository.getCacheResponseFromDB(sumInfo);
        }

       return CacheMap;
    }

    @RequestMapping(value="/getDoc",method={RequestMethod.GET})
    @ResponseBody
    public List<DocResponseVO> getDocResponseFromDB(String sumInfo, boolean reCert, boolean getLatest) throws Exception{

        List<DocResponseVO> DocMap = dbAccessRepository.getDocResponseFromDB(sumInfo, reCert);

        if(DocMap.size()==0 || (getLatest && reCert)){
            DocResponseVO docResponseVO = new DocResponseVO();
            docResponseVO.setSumInfoId(sumInfo);
            docResponseVO.setIsDocPresent("Sum_Info_Id is being processed. Please wait for sometime.");
            System.out.println("Inserting the Sum_info for Doc Response");
            dbAccessRepository.AddDocResponseToDB(docResponseVO);
         HashMap<Object, HashMap<String, Object>> finalResponse = getDBPushDifference(sumInfo, "Doc");
            DocMap = dbAccessRepository.getDocResponseFromDB(sumInfo, reCert);
        }

      return DocMap;
    }

    @RequestMapping(value="/getTSD",method={RequestMethod.GET})
    @ResponseBody
    public List<TSDResponseVO> getTSDResponseFromDB(String sumInfo) throws Exception{

        List<TSDResponseVO> TSDMap = new ArrayList<TSDResponseVO>();


            if(!sumInfo.contains(",")) {
                TSDMap = dbAccessRepository.getTSDResponseFromDB(sumInfo);
            }

        if(TSDMap.size()==0 || TSDMap.isEmpty()){

            String arrTSD[] = sumInfo.split(",");

            for(Object sumInfoI : arrTSD) {
                if(!sumInfoI.toString().matches("\\d+")){
                   System.out.println("Sum_Info_ID is not in proper format"+sumInfoI.toString());
                   continue;
                }
                TSDResponseVO tsdResponseVO = new TSDResponseVO();
                tsdResponseVO.setSumInfoId(sumInfoI.toString());
                tsdResponseVO.setIsTSDPresent("Sum_Info_Id is being processed. Please wait for sometime.");
                System.out.println("Inserting the Sum_info for TSD Response");
                dbAccessRepository.AddTSDResponseToDB(tsdResponseVO);
            }
            for(Object sumInfoI : arrTSD){
                HashMap<Object, HashMap<String, Object>> finalResponse = getDBPushDifference(sumInfoI.toString(), "TSD");
            }
            TSDMap = dbAccessRepository.getTSDResponseFromDB(sumInfo);
        }

        return TSDMap;
    }

    @RequestMapping(value="/TestingCSID",method={RequestMethod.GET})
    @ResponseBody
    public List<Map<String, Object>> getTTRResponse() throws Exception{

        List<Map<String, Object>> ttr = sitepRepository.getCSID("12443");

        return ttr;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = "/auth", method = { RequestMethod.POST })
    @ResponseBody
    public ResponseEntity<String> authenticate(@RequestHeader(value = "userName") String userName,
                                               @RequestHeader(value = "password") String password)

    {

        String token = null;

        // Setting Response headers to prevent clickJacking //
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.set("X-Frame-Options", "DENY");
        responseHeaders.set("Content-Security-Policy", "frame-ancestors 'none'");
        responseHeaders.set("Set-Cookie", "server.session.cookie.secure=" + "true");
        responseHeaders.set("Set-Cookie", "server.session.cookie.http-only=" + "true");
        responseHeaders.set("X-XSS-Protection", "1; mode=block");
        responseHeaders.set("Content-Security-Policy", "default-src 'self' script-src 'self'");

        try {

            password = authorization.encrypt(password);

            token = hammerAuthenticationService.generateToken(userName, password);

        } catch (JsonParseException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }

        return new ResponseEntity<String>(token.toString(), responseHeaders, HttpStatus.OK);

    }

}
