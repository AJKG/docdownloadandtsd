package com.yodlee.docdownloadandtsd.Controller;


import com.yodlee.docdownloadandtsd.DAO.DBAccessRepositoryImpl;
import com.yodlee.docdownloadandtsd.DAO.RpaldaRepository;
import com.yodlee.docdownloadandtsd.Services.DocDownloadRecertificationService;
import com.yodlee.docdownloadandtsd.Services.ObjectResponseToJSON;
import com.yodlee.docdownloadandtsd.Services.RpaldaService;
import com.yodlee.docdownloadandtsd.Services.TSDRecertificationService;
import com.yodlee.docdownloadandtsd.VO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Controller
@RestController
public class DDTsdController {

    @Autowired
    RpaldaService rpaldaService;

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
    

    @RequestMapping(value="/MetaDataMonitoring",method={RequestMethod.GET})
    @ResponseBody
    public HashMap<Object, HashMap<String, Object>> getDBPushDifference() throws Exception{

       // List<Object> resultList = rpaldaService.getDiff();

        List<Object> resultList = rpaldaService.getInput();

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

    @RequestMapping(value="/ViewDoc",method={RequestMethod.GET})
    @ResponseBody
    public List<DocResponseVO> getDocResponseFromDB() throws Exception{

        List<DocResponseVO> DocMap = dbAccessRepository.getDocResponseFromDB();

      return DocMap;
    }

    @RequestMapping(value="/ViewTSD",method={RequestMethod.GET})
    @ResponseBody
    public List<TSDResponseVO> getTSDResponseFromDB() throws Exception{

        List<TSDResponseVO> TSDMap = dbAccessRepository.getTSDResponseFromDB();

        return TSDMap;
    }



}
