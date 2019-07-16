package com.yodlee.docdownloadandtsd.Controller;

import com.yodlee.docdownloadandtsd.DAO.RpaldaRepository;
import com.yodlee.docdownloadandtsd.Services.DocDownloadRecertificationService;
import com.yodlee.docdownloadandtsd.Services.RpaldaService;
import com.yodlee.docdownloadandtsd.Services.TSDRecertificationService;
import com.yodlee.docdownloadandtsd.VO.CacheRunVO;
import com.yodlee.docdownloadandtsd.VO.DocDownloadVO;
import com.yodlee.docdownloadandtsd.VO.TransactionSelectionDurationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class DDTsdController {

    @Autowired
    RpaldaService rpaldaService;

    @Autowired
    RpaldaRepository rpaldaRepository;

    @Autowired
    DocDownloadRecertificationService docDownloadRecService;

    @Autowired
    TSDRecertificationService tsdRecertificationService;

    public static HashMap<String, Object> docDownloadUsers = new HashMap<>();

    @RequestMapping(value="/TestClob",method={RequestMethod.GET})
    @ResponseBody
    public HashMap<Object, HashMap<String, String>> getDBPushDifference() throws Exception{
        System.out.println("Controller");
        List<Object> resultList = rpaldaService.getDiff();
        HashMap<Object, HashMap<String, String>> fin = new HashMap<>();


        for(Object res : resultList) {
            /*if(res instanceof DocDownloadVO) {
                String sumInfoId = ((DocDownloadVO) res).getSumInfoId();

                if(((DocDownloadVO) res).getDocDownloadSeed().equals("1")) {
                    HashMap<HashMap<String, Object>, HashMap<String,String>> obj = docDownloadRecService.docDownloadEnabled(sumInfoId, "cii");
                    for (Map.Entry<HashMap<String, Object>, HashMap<String, String>> hmap : obj.entrySet()) {
                        HashMap<String, String> hV = hmap.getValue();
                        if(hV.get("isDocPresent").equalsIgnoreCase("no")) {
                            //do YCC changes
                        }
                        fin.put(res, hV);
                    }


                }else if(((DocDownloadVO) res).getDocDownloadSeed().equals("0")) {
                    HashMap<HashMap<String, Object>, HashMap<String,String>> obj = docDownloadRecService.docDownloadEnabled(sumInfoId, "msa");
                    for (Map.Entry<HashMap<String, Object>, HashMap<String, String>> hmap : obj.entrySet()) {
                        HashMap<String, String> hV = hmap.getValue();
                        if(hV.get("isDocPresent").equalsIgnoreCase("yes")) {
                            //do YCC changes
                        }
                        fin.put(res, hV);
                    }
                }
            }*/

            if(res instanceof TransactionSelectionDurationVO) {

                String tsdProd = ""+((TransactionSelectionDurationVO) res).getTransactionDurationProd();
                String tsdSeed = ""+((TransactionSelectionDurationVO) res).getTransactionDurationSeed();
                String sumInfoId = ((TransactionSelectionDurationVO) res).getSumInfoId();
                if(rpaldaRepository.isNullValue(tsdProd)) {
                    tsdProd = "90";
                }

                if(Integer.parseInt(tsdProd) > Integer.parseInt(tsdSeed)) {
                    HashMap<HashMap<String, Object>, HashMap<String,String>> obj = tsdRecertificationService.transactionDurationdEnabled(sumInfoId, tsdProd);
                    for (Map.Entry<HashMap<String, Object>, HashMap<String, String>> hmap : obj.entrySet()) {
                        HashMap<String, String> hV = hmap.getValue();
                        if(hV.get("isTSDPresent").equalsIgnoreCase("yes")) {
                            //do YCC changes
                        }
                        fin.put(res, hV);
                    }
                }else{
                    HashMap<HashMap<String, Object>, HashMap<String,String>> obj = tsdRecertificationService.transactionDurationdEnabled(sumInfoId, tsdSeed);
                    for (Map.Entry<HashMap<String, Object>, HashMap<String, String>> hmap : obj.entrySet()) {
                        HashMap<String, String> hV = hmap.getValue();
                        if(hV.get("isTSDPresent").equalsIgnoreCase("no")) {
                            //do YCC changes
                        }
                        fin.put(res, hV);
                    }
                }

                break;

            }

        }

        return fin;

    }

    @RequestMapping(value="/DocDownloadData",method={RequestMethod.GET})
    @ResponseBody
    public List<DocDownloadVO> docDownloadFunc() throws Exception {
        List<Object> allList = rpaldaService.getDiff();
        List<DocDownloadVO> ddvo = new ArrayList<>();
        for(Object dList : allList) {
            if(dList instanceof DocDownloadVO){
                ddvo.add((DocDownloadVO)dList);
            }
        }
        return ddvo;
    }

    @RequestMapping(value="/TSDData",method={RequestMethod.GET})
    @ResponseBody
    public List<TransactionSelectionDurationVO> transactionSelectionDurationFunc() throws Exception {
        List<Object> allList = rpaldaService.getDiff();
        List<TransactionSelectionDurationVO> tsdVoList = new ArrayList<>();
        for(Object dList : allList) {
            if(dList instanceof TransactionSelectionDurationVO){
                tsdVoList.add((TransactionSelectionDurationVO)dList);
            }
        }
        return tsdVoList;
    }

    @RequestMapping(value="/CacheRunData",method={RequestMethod.GET})
    @ResponseBody
    public List<CacheRunVO> cacheRunFunc() throws Exception {
        List<Object> allList = rpaldaService.getDiff();
        List<CacheRunVO> cacheRunVOList = new ArrayList<>();
        for(Object dList : allList) {
            if(dList instanceof CacheRunVO){
                cacheRunVOList.add((CacheRunVO)dList);
            }
        }
        return cacheRunVOList;
    }


}
