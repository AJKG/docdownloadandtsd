package com.yodlee.docdownloadandtsd.Controller;


import com.yodlee.docdownloadandtsd.Services.RpaldaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
public class DDTsdController {

    @Autowired
    RpaldaService rpaldaService;

    @RequestMapping(value="/TestClob",method={RequestMethod.GET})
    @ResponseBody
    public ArrayList<String> getDBPushDifference() throws Exception{
        System.out.println("Controller");
        return rpaldaService.getDiff();
    }

}
