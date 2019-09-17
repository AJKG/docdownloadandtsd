package com.yodlee.docdownloadandtsd.Services;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.yodlee.docdownloadandtsd.VO.ItemDetailsVO;
import com.yodlee.docdownloadandtsd.VO.RequestResponseVO;
import com.yodlee.docdownloadandtsd.VO.SplunkItemDetailsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class DumpReadService {

    HashMap<String, RequestResponseVO> dumpDetails=new HashMap<String, RequestResponseVO>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    HtmlPage page=null;

    public HashMap<String, String> extractDumpLink(SplunkItemDetailsVO[] itemDetails, String sum_info, boolean fromCache) throws Exception {
        System.out.println("logger to verify");
        HashMap<String, String> itemReqIDMap=new HashMap<String, String>();
        for(SplunkItemDetailsVO row : itemDetails){
            String cacheItem=row.getCACHE_ITEM_ID();
            String dumpfile=row.getDUMP_FILE();
                itemReqIDMap.put(cacheItem, dumpfile);
        }

        return itemReqIDMap;
    }

    private String modifiedDumpLink(String dumpLink) {
        if(!dumpLink.contains("yoshiee.yodlee.com")) {
            String modifiedDumpLink="https://yoshiee.yodlee.com:2443/Extractor/HTMLExtractor?dumpLink="+dumpLink+".gz";
            return modifiedDumpLink;
        }
        return dumpLink;
    }


    public String findCodeCheck(){
       String arr[]  = {"172.17.6.173.2019.05.16.03.29.56.35_MFA_credits.html","172.17.9.99.2019.06.04.23.41.46.8_MFA_loans.html","172.17.8.197.2019.05.20.17.55.28.85_MFA_bank.html","172.17.7.177.2019.06.04.21.21.55.12_MFA_loans.html","172.17.8.102.2019.06.04.12.14.10.139_MFA_credits.html","172.17.6.201.2019.04.24.08.57.03.10_MFA_credits.html","172.17.8.16.2019.06.03.23.49.09.306_MFA_loans.html","172.17.7.149.2019.04.29.08.25.22.134_MFA_credits.html","172.17.9.107.2019.06.04.03.29.47.349_MFA_credits.html","172.17.8.50.2019.06.03.22.39.39.353_MFA_loans.html","172.17.9.197.2019.06.04.02.26.52.301_MFA_loans.html","172.17.7.206.2019.05.31.05.23.04.234_MFA_loans.html","172.17.7.163.2019.06.04.21.29.34.364_MFA_credits.html","172.17.8.99.2019.05.23.07.43.38.62_MFA_loans.html","172.17.8.90.2019.06.02.11.38.45.288_MFA_loans.html","172.17.6.238.2019.05.24.14.30.41.30_MFA_bank.html","172.17.9.199.2019.05.31.06.18.38.265_MFA_loans.html","172.17.6.205.2019.05.26.23.09.44.155_MFA_mortgage.html","172.17.8.204.2019.05.16.02.39.08.19_MFA_bank.html","172.17.6.205.2019.06.02.16.14.09.312_MFA_credits.html","172.17.8.50.2019.05.22.11.23.12.32_MFA_loans.html","172.17.6.183.2019.06.04.00.43.46.302_MFA_loans.html","172.17.7.127.2019.05.28.05.40.30.173_MFA_bank.html","172.17.7.127.2019.05.28.05.44.38.173_MFA_loans.html","172.17.9.146.2019.06.04.02.52.50.142_MFA_bank.html","172.17.9.94.2019.05.21.12.08.01.3_MFA_loans.html","172.17.9.63.2019.05.24.14.19.12.85_MFA_loans.html","172.17.8.219.2019.05.31.05.23.29.267_MFA_loans.html","172.17.9.107.2019.05.31.05.23.02.262_MFA_loans.html","172.17.6.149.2019.06.03.15.38.40.43_MFA_credits.html","172.17.7.131.2019.05.22.05.22.21.48_MFA_loans.html","172.17.7.212.2019.05.31.05.19.55.261_MFA_loans.html","172.17.7.55.2019.05.16.02.39.12.30_MFA_bank.html","172.17.8.249.2019.06.03.23.51.29.161_MFA_bank.html","172.17.8.90.2019.05.16.00.46.52.17_MFA_bank.html","172.17.8.37.2019.05.31.05.21.54.257_MFA_loans.html","172.17.9.109.2019.05.16.01.06.57.24_MFA_bank.html","172.17.8.204.2019.05.16.06.22.55.27_MFA_loans.html","172.17.9.99.2019.06.04.23.12.51.11_MFA_loans.html","172.17.9.94.2019.05.16.00.38.10.16_MFA_bank.html","172.17.7.56.2019.05.16.01.17.44.18_MFA_bank.html","172.17.7.184.2019.05.31.05.19.01.74_MFA_loans.html","172.17.9.197.2019.05.16.01.00.09.28_MFA_bank.html"};
       //String arr[] = {"172.17.6.173.2019.05.16.03.29.56.35_MFA_credits.html"};
        System.out.println("arr.length: "+arr.length);
        for(int i=0; i<arr.length; i++){
            String searchURL = modifiedDumpLink(arr[i]);

            String MFAType="";
            boolean isNewFiremem=true;
            if(!searchURL.contains("fmdumps.")){
                isNewFiremem=false;
            }

            WebClient client = new WebClient();
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);
            client.getOptions().setThrowExceptionOnFailingStatusCode(false);
            client.getOptions().setUseInsecureSSL(true);

            try
            {
                try {
                    page = client.getPage(searchURL);
                }catch(StackOverflowError e){
                    System.out.println("Stack Overflow Error error for this dump");
                    page = null;
                }
                WebResponse response = page.getWebResponse();
                String content = response.getContentAsString();
                //System.out.println(content);
                if(content.contains("Dump specified is not available")) {
                    System.out.println(searchURL+" | Dump Not Available");
                }else if(content.contains("c93G-SOMV_sJrGEgvBinM")){
                  System.out.println(searchURL+" | True");
              }else{
                  System.out.println(searchURL+" | False");
              }
            }catch(Exception e) {
                client.close();
                System.out.println("Exception: "+e);
                logger.info("Exception "+e);
            }
            client.close();

        }


        return "0";
    }

    public String findXml(String searchUrl, String cacheItemId, boolean fromCache, String sumInfo) throws Exception {
        searchUrl=modifiedDumpLink(searchUrl);
        System.out.println("search url "+searchUrl);
        String MFAType="";
        boolean isNewFiremem=true;
        if(!searchUrl.contains("fmdumps.")){
            isNewFiremem=false;
        }
        String accountNum=null,accountName=null,accountHolder=null,balance=null,transAmount=null,transDescription=null;
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setUseInsecureSSL(true);
        RequestResponseVO reqResObj=new RequestResponseVO();
        reqResObj.setCacheItemId(cacheItemId);
        reqResObj.setDumpUrl(searchUrl);
        try
        {
            try {
                page = client.getPage(searchUrl);
            }catch(StackOverflowError e){
              System.out.println("Stack Overflow Error error for this dump");
              page = null;
            }
            List<HtmlElement> Xmltobesent =page.getByXPath("//sessionpromptrequest");
            System.out.println("Xml count: "+Xmltobesent.size());

            if(Xmltobesent.size()==0){
                return "Non MFA";
            }

            int i = 0;
            for(HtmlElement XmlL : Xmltobesent){
                String requestXml=XmlL.asXml();
                MFAType = requestXml.substring(requestXml.indexOf("type=")+6,requestXml.indexOf(">")-1);
                System.out.println("Printing Xml: "+MFAType);

                if(i==0){
                    return MFAType;
                }
                i++;
                if(requestXml.contains("&lt;class&gt;")){
                    String classEle=requestXml.substring(requestXml.indexOf("&lt;class&gt;")+"&lt;class&gt;".length(), requestXml.indexOf("&lt;/class&gt;"));
                    String agentName=classEle.substring(classEle.indexOf("&lt;name&gt;")+"&lt;name&gt;".length(), classEle.indexOf("&lt;/name&gt;"));
                    reqResObj.setClassName(agentName);
                }
            }

            return MFAType;
        }catch(Exception e) {
            client.close();
            System.out.println("Exception: "+e);
            logger.info("Exception "+e);
        }
        client.close();

        dumpDetails.put(searchUrl, reqResObj);
        return MFAType;

    }

    }
