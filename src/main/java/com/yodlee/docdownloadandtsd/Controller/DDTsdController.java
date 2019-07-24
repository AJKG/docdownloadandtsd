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

    public static HashMap<String, Object> docDownloadUsers = new HashMap<>();

    @RequestMapping(value="/TestClob",method={RequestMethod.GET})
    @ResponseBody
    public HashMap<Object, HashMap<String, Object>> getDBPushDifference() throws Exception{
        List<Object> resultList = rpaldaService.getDiff();
        HashMap<Object, HashMap<String, Object>> fin = new HashMap<>();


        //Testing for single sum_info_id
        int countDoc = 0;
        int countTSD = 0;

        for(Object res : resultList) {
            List<Object> usersList = new ArrayList<>();

            if(res instanceof DocDownloadVO && countDoc < 1) {
                countDoc++;
                String sumInfoId = "25627";
                        //((DocDownloadVO) res).getSumInfoId();

                if(((DocDownloadVO) res).getDocDownloadSeed().equals("1")) {
                    HashMap<HashMap<String, Object>, HashMap<String,Object>> obj = docDownloadRecService.docDownloadEnabled((DocDownloadVO)res, sumInfoId, "msa");
                    for (Map.Entry<HashMap<String, Object>, HashMap<String, Object>> hmap : obj.entrySet()) {
                        HashMap<String, Object> hV = hmap.getValue();
                        HashMap<String, Object> hK = hmap.getKey();

                        for(String values : hK.keySet()) {
                            Object data = hK.get(values);
                            usersList.add(data);
                        }

                        if(hV.get("isDocPresent").toString().equalsIgnoreCase("no")) {
                            //do YCC changes
                        }
                        hV.put("users", usersList);
                        fin.put(sumInfoId, hV);
                    }



                }else if(((DocDownloadVO) res).getDocDownloadSeed().equals("0")) {
                    HashMap<HashMap<String, Object>, HashMap<String,Object>> obj = docDownloadRecService.docDownloadEnabled((DocDownloadVO)res,sumInfoId, "msa");
                    for (Map.Entry<HashMap<String, Object>, HashMap<String, Object>> hmap : obj.entrySet()) {
                        HashMap<String, Object> hV = hmap.getValue();
                        HashMap<String, Object> hK = hmap.getKey();

                        for(String values : hK.keySet()) {
                            Object data = hK.get(values);
                            usersList.add(data);
                        }

                        if(hV.get("isDocPresent").toString().equalsIgnoreCase("yes")) {
                            //do YCC changes
                        }
                        hV.put("users", usersList);
                        fin.put(sumInfoId, hV);
                    }
                }
            }

            if(res instanceof TransactionSelectionDurationVO && countTSD < 1) {
                countTSD++;

                String tsdProd = "90";
                        //""+((TransactionSelectionDurationVO) res).getTransactionDurationProd();
                String tsdSeed = "0";
                        //""+((TransactionSelectionDurationVO) res).getTransactionDurationSeed();
                String sumInfoId = "663";
                       // ((TransactionSelectionDurationVO) res).getSumInfoId();
                if(rpaldaRepository.isNullValue(tsdProd)) {
                    tsdProd = "90";
                }

                if(Integer.parseInt(tsdProd) > Integer.parseInt(tsdSeed)) {
                    HashMap<HashMap<String, Object>, HashMap<String,Object>> obj = tsdRecertificationService.transactionDurationdEnabled((TransactionSelectionDurationVO)res,sumInfoId, tsdProd);
                    for (Map.Entry<HashMap<String, Object>, HashMap<String, Object>> hmap : obj.entrySet()) {
                        HashMap<String, Object> hV = hmap.getValue();
                        HashMap<String, Object> hK = hmap.getKey();

                        for(String values : hK.keySet()) {
                            Object data = hK.get(values);
                            usersList.add(data);
                        }

                        if(hV.get("isTSDPresent").toString().equalsIgnoreCase("yes")) {
                            //do YCC changes
                        }
                        hV.put("users", usersList);
                        fin.put(sumInfoId, hV);
                    }
                }else{
                    HashMap<HashMap<String, Object>, HashMap<String,Object>> obj = tsdRecertificationService.transactionDurationdEnabled((TransactionSelectionDurationVO)res,sumInfoId, tsdSeed);
                    for (Map.Entry<HashMap<String, Object>, HashMap<String, Object>> hmap : obj.entrySet()) {
                        HashMap<String, Object> hV = hmap.getValue();
                        HashMap<String, Object> hK = hmap.getKey();

                        for(String values : hK.keySet()) {
                            Object data = hK.get(values);
                            usersList.add(data);
                        }
                        if(hV.get("isTSDPresent").toString().equalsIgnoreCase("no")) {
                            //do YCC changes
                        }
                        hV.put("users", usersList);
                        fin.put(sumInfoId, hV);

                    }
                }

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

    @RequestMapping(value="/docDownloadRecertify",method={RequestMethod.GET})
    @ResponseBody
    public HashMap<Object, HashMap<String, Object>> docDownloadRecertification() throws Exception {

        HashMap<Object, HashMap<String, Object>> fin = new HashMap<>();
        String[] sumInfo = {"3664", "1984", "12151", "10719", "10659", "12024", "9688", "26079", "25976", "7087"};

        for (int i = 0; i < sumInfo.length; i++) {
            Object res = sumInfo[i];
            String sumInfoId = sumInfo[i];

            HashMap<HashMap<String, Object>, HashMap<String, Object>> obj = docDownloadRecService.docDownloadEnabled((DocDownloadVO)res, sumInfoId, "msa");
            if(obj!=null) {
                for (Map.Entry<HashMap<String, Object>, HashMap<String, Object>> hmap : obj.entrySet()) {
                    HashMap<String, Object> hV = hmap.getValue();
                    if (hV.get("isDocPresent").toString().equalsIgnoreCase("no")) {
                        //do YCC changes
                    }
                    fin.put(res, hV);
                }
            }
        }

        return fin;

    }



}
