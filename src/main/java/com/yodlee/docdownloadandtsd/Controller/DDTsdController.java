package com.yodlee.docdownloadandtsd.Controller;


import com.yodlee.docdownloadandtsd.Services.RpaldaService;
import com.yodlee.docdownloadandtsd.VO.CacheRunVO;
import com.yodlee.docdownloadandtsd.VO.DocDownloadVO;
import com.yodlee.docdownloadandtsd.VO.TransactionSelectionDurationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DDTsdController {

    @Autowired
    RpaldaService rpaldaService;

    @RequestMapping(value="/TestClob",method={RequestMethod.GET})
    @ResponseBody
    public List<Object> getDBPushDifference() throws Exception{
        System.out.println("Controller");
        return rpaldaService.getDiff();
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
