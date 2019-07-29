package com.yodlee.docdownloadandtsd.Services;

import com.yodlee.docdownloadandtsd.VO.ItemDetailsVO;
import com.yodlee.docdownloadandtsd.exceptionhandling.LoginExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;

@Service
public class YUVASegmentService {

    @Autowired
    SplunkService splunkService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ItemDetailsVO[] getYUVASegments(String agentName, String sumInfoId) throws Exception{
        System.out.println("Getting in to YUVA Segments");
        ItemDetailsVO[] yuvaUsers = null;
        try {
            yuvaUsers = splunkService.getyuvasegmentusers(agentName, sumInfoId);
        } catch (HttpClientErrorException httpClientErrorException) {
            logger.info("Retrive item from Yuva/splunk error:"
                    + Arrays.toString(httpClientErrorException.getStackTrace()));
            throw new LoginExceptionHandler("Splunk Login Failure :" + httpClientErrorException.getMessage());
        }
        return yuvaUsers;
    }

}
