package com.yodlee.docdownloadandtsd.Services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yodlee.docdownloadandtsd.VO.ItemDetailsVO;
import com.yodlee.docdownloadandtsd.VO.TTRDashboardVO;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

@Service
public class TTRDashboardService {

    public TTRDashboardVO[] getTTRDashboard() throws Exception {

    String response = get("http://192.168.210.151:8090/ttrDashboard");

        TTRDashboardVO[] ttrDashboardVOS = new ObjectMapper().readValue(response, TTRDashboardVO[].class);

            return ttrDashboardVOS;
    }




    public  String get(String url) {

        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders requestHeaders = new HttpHeaders();

            requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params1 = new LinkedMultiValueMap<String, String>();

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(params1,

                    requestHeaders);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response.getBody().toString();

        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("inside exception........" + e.getMessage());

            if (e.getMessage().contains("401")) {


                String data = get(url);
                return data;
            }
        }
        return "";
    }


}
