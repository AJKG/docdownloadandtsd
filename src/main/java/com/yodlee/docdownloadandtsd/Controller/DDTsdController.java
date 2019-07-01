package com.yodlee.docdownloadandtsd.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

public class DDTsdController {

    @RequestMapping(value="/TestClob",method={RequestMethod.GET})
    @ResponseBody
    public ArrayList<String> getDBPushDifference(String sumInfo, boolean fromCache) throws Exception{
        return sitepservice.getCSID(sumInfo,fromCache);
    }

}
