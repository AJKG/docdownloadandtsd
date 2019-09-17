package com.yodlee.docdownloadandtsd.Services;



import com.yodlee.docdownloadandtsd.DAO.DBAccessRepositoryImpl;
import com.yodlee.docdownloadandtsd.DAO.SitepRepository;
import com.yodlee.docdownloadandtsd.DAO.SplunkRepository;
import com.yodlee.docdownloadandtsd.DAO.YADRepository;
import com.yodlee.docdownloadandtsd.VO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CacheRefreshRecertificationService {

    @Autowired
    SitepRepository sitepRepository;

    @Autowired
    DBAccessRepositoryImpl dbAccessRepository;

    @Autowired
    TTRDashboardService ttrDashboardService;

    @Autowired
    YADRepository yadRepository;

    @Autowired
    SumInfoStats sumInfoStats;

    @Autowired
    SplunkRepository splunkRepository;

    public List<SumInfoVO> getCSIDForEnablement(String sumInfo) throws Exception{

        List<SumInfoVO> Sum_info = new ArrayList<>();



        List<Map<String, Object>> rows = sitepRepository.getCSID(sumInfo);
        System.out.println("Size of List from DB: "+ rows.size());

        if(rows.size()==0){
            return Sum_info;
        }

        List<ABSListVO> Abs = dbAccessRepository.getABSListFromDB();
        System.out.println("Size of ABS List: "+Abs.size());
        TTRDashboardVO[] TTR=ttrDashboardService.getTTRDashboard();
        System.out.println("Size of TTRDashboard List: "+TTR.length);
        List<TTRBandingVO> TTRBand = dbAccessRepository.getTTRBandFromDB();
        System.out.println("Size of TTRBand List: "+TTRBand.size());


        //For Loop to set ABS, TTR Status, TTR Banding.
        for (Map row : rows) {

            SumInfoVO suminfovo = new SumInfoVO();
            suminfovo.setSum_info_id((row.get("sum_info_id").toString()));
            suminfovo.setIs_cacherun_disabled((row.get("is_cacherun_disabled").toString()));
            suminfovo.setTag_id((row.get("tag_id").toString()));
            suminfovo.setTag((row.get("tag").toString()));
            suminfovo.setSite_id((row.get("site_id").toString()));
            suminfovo.setDisplay_name((row.get("display_name").toString()));
            suminfovo.setLocale_id((row.get("locale_id").toString()));
            suminfovo.setLocale_key((row.get("locale_key").toString()));
            suminfovo.setCountry_name((row.get("country_name").toString()));
            suminfovo.setMfa_type_id((row.get("mfa_type_id").toString()));
            suminfovo.setMfa_type_name((row.get("mfa_type_name").toString()));
            suminfovo.setBase_url((row.get("base_url").toString()));
            suminfovo.setIs_beta((row.get("is_beta").toString()));
            suminfovo.setLogin_url((row.get("login_url").toString()));
            suminfovo.setLogin_url((row.get("login_url").toString()));

            for (ABSListVO abs : Abs) {
                if (abs.getSiteName().contains(row.get("base_url").toString()) || abs.getSiteName().contains(row.get("login_url").toString()) ||
                        row.get("base_url").toString().contains(abs.getSiteName()) || row.get("login_url").toString().contains(abs.getSiteName())) {
                    suminfovo.setABS(true);
                }else{
                    suminfovo.setABS(false);
                }
            }

            for (TTRDashboardVO ttr : TTR) {
                if (ttr.getCf_suminfo().contains(row.get("sum_info_id").toString())) {
                    suminfovo.setIfTTRRaised(true);
                }else{
                    suminfovo.setIfTTRRaised(false);
                }
            }

            if(row.get("mfa_type_id").equals("1") || row.get("mfa_type_id").equals("2")){
                suminfovo.setToken_UserDependentMFA(true);
            }else{
                suminfovo.setToken_UserDependentMFA(false);
            }

            for (TTRBandingVO tb : TTRBand) {
                if(tb.getSite_id().equals(row.get("site_id").toString())) {
                    if (tb.getCategory().equals("TTR")) {
                        suminfovo.setTTR_Site(true);
                    } else {
                        suminfovo.setTTR_Site(false);
                    }
                }
            }

            Sum_info.add(suminfovo);
        }


        System.out.println("Getting in to YAD Repository to get the YAD Notes");
        yadRepository.getYADDetails(Sum_info);


        System.out.println("Getting the volume for all CSID");
        List<SumInfoStatsVO> VolumeCSID = sumInfoStats.getVolume(Sum_info);

        ArrayList<String> VolumeProcessed = new ArrayList<>();

        //For Loop to process Volume of CSID.
        System.out.println("Updating YAD Notes as per Need");
        for (SumInfoVO addV : Sum_info) {
            for (SumInfoStatsVO row: VolumeCSID) {
                if (addV.getSum_info_id().equals(row.getSum_info_id())) {
                    addV.setTotal_request(row.getTotal_request());
                    addV.setSuccess_percentage(row.getSuccess_percentage());
                    int tr = Integer.parseInt(row.getTotal_request());
                    //Adding Non MFA and Low Volume CSID's to Final List & other MFA where Cache run disablement is not required
                    if((tr<25) || addV.getMfa_type_id().equals("0") || addV.getMfa_type_id().isEmpty()){
                        String YADNotes = addV.getYADNotes();
                        System.out.println("Printing YADNotes: for "+addV.getSum_info_id()+" | "+YADNotes);
                        if(YADNotes.equals("Can be Enabled") || YADNotes.equals("Notes not found")){
                            addV.setYADNotes("Enable");
                        }
                    }else{
                        VolumeProcessed.add(addV.getSum_info_id());
                    }
                }
            }

        }



        System.out.println("Process Dump for all CSIDs");
        int count = 0;
        int mfascanning = 0;
        for(SumInfoVO addMFA : Sum_info) {
            count++;
            if(!addMFA.isABS() && !addMFA.isIfTTRRaised() && !addMFA.isToken_UserDependentMFA() && !addMFA.getYADNotes().equals("Enable")){
            //For loop to add to DB and status String.
            for (String sum_info : VolumeProcessed) {
                if (sum_info.equals(addMFA.getSum_info_id())) {
                    mfascanning++;
                    System.out.println("No of CSID already Processed for MFA Dump: " + mfascanning);
                    try {
                        String MFAfromDump = splunkRepository.getMFATypefromDump(sum_info);
                        System.out.println("MFA From Dump: " + MFAfromDump);

                        if (MFAfromDump.toLowerCase().equals("tokenid") || MFAfromDump.toLowerCase().equals("image")) {
                            addMFA.setMFADump("Keep Disabled");
                        } else {
                            if (MFAfromDump.toLowerCase().equals("no user found") || MFAfromDump.toLowerCase().equals("questions")) {
                                addMFA.setMFADump("Enable");
                            } else {
                                addMFA.setMFADump("Can be Enabled");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        }else{
                addMFA.setMFADump("User Processing Not Required");
            }
            dbAccessRepository.AddCacheResponseToDB(addMFA);
        }
        return Sum_info;
    }
}
