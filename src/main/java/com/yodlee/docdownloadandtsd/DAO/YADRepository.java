package com.yodlee.docdownloadandtsd.DAO;


import com.yodlee.docdownloadandtsd.VO.SumInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class YADRepository {


    @Autowired
    @Qualifier("repaldaJdbcTemplate")
    JdbcTemplate jdbc1;

    public List<SumInfoVO> getYADDetails(List<SumInfoVO> CSIDListInitial){
        HashMap<String, String> YADStatus = new HashMap<>();
        HashMap<String, String> CSIDList = new HashMap<>();

       try{
           System.out.println("CSIDListInitial Size: "+CSIDListInitial.size());

                String csCustom = "";
                int cs = 0;
           for (SumInfoVO sumInfoVO : CSIDListInitial){
                cs++;
                if(!sumInfoVO.isABS() && !sumInfoVO.isIfTTRRaised() && !sumInfoVO.isToken_UserDependentMFA()){
               if(cs==CSIDListInitial.size()) {
                   csCustom = csCustom + sumInfoVO.getSum_info_id().toString();
               }else {
                   csCustom = csCustom + sumInfoVO.getSum_info_id().toString() + ",";
               }}else{
                    System.out.println(sumInfoVO.isABS()+" | "+sumInfoVO.isIfTTRRaised()+" | "+sumInfoVO.isToken_UserDependentMFA());
                    sumInfoVO.setYADNotes("Not Eligible for Notes Check");
                }
           }

           System.out.println("Printing csCustom: "+csCustom);

           if(csCustom.length()<=0){
               return CSIDListInitial;
           }



           String csSql = "select si.sum_info_id as sum_info_id,sic.SUM_INFO_CATEGORY_ID as suminfo_categoryid, sic.SUM_INFO_CATEGORY_NAME\n" +
                   "from sum_info_extn si, sum_info_category sic\n" +
                   "where si.SUM_INFO_CATEGORY_ID=sic.SUM_INFO_CATEGORY_ID\n" +
                   "and si.sum_info_id in ("+csCustom+")";

           List<Map<String, Object>> CustomList = jdbc1.queryForList(csSql);

           System.out.println("CSCustomList Size: "+CustomList.size());

           for (SumInfoVO me : CSIDListInitial) {
               for (Map row : CustomList) {
                   if (me.getSum_info_id().equals(row.get("sum_info_id").toString())){
                      // System.out.println("Getting in to check for Custom list matching");
                       if(!row.get("suminfo_categoryid").toString().equals("2") && !row.get("suminfo_categoryid").toString().equals("3") && !row.get("suminfo_categoryid").toString().equals("5")) {
                          // System.out.println("Setting CSID post custom matching");
                           CSIDList.put(me.getSum_info_id(),me.getSite_id());
                       }
                   }else{
                       me.setYADNotes("Custom/DAG Sites");
                   }
               }
           }

         System.out.println("CSIDList Size: "+CSIDList.size());

            String csidpart = "", sitepart = "";
            int c=0;
           for (Map.Entry me : CSIDList.entrySet()){
               c++;

               if(c==CSIDList.size()) {
                   csidpart = csidpart + me.getKey().toString();
                   sitepart = sitepart + me.getValue().toString();
               }else {
                   csidpart = csidpart + me.getKey().toString() + ",";
                   sitepart = sitepart + me.getValue().toString() + ",";
               }
           }



            System.out.println("List of CSID for REPALDA query: "+csidpart);
            System.out.println("List of Site for REPALDA query: "+sitepart);

           String sql = "select primary_key_id,content,classification_id from notes \n" +
                   "where (primary_key_id in ("+csidpart+") and classification_id = 1) or (primary_key_id in("+sitepart+") and classification_id = 3)\n"+
                   "order by primary_key_id,note_id asc";


               List<Map<String, Object>> notes = jdbc1.queryForList(sql);

               for(Map.Entry me : CSIDList.entrySet()){
                   Boolean enable = false,keepdisable=false,needtoanalyze = false,notesnotfound = false;

                   String csid = me.getKey().toString();
                   String site = me.getValue().toString();

                    for(Map row : notes) {

                        if(row.get("classification_id").equals("3")){
                            csid = site;
                        }

                        if(csid.equals(row.get("primary_key_id").toString())){

                            if ((row.get("content").toString().toLowerCase().contains("beta") && row.get("content").toString().toLowerCase().contains("disabl"))
                                    || (row.get("content").toString().toLowerCase().contains("enabl") && row.get("content").toString().toLowerCase().contains("site enabl") && !row.get("content").toString().toLowerCase().contains("service enabl"))
                                    || row.get("content").toString().toLowerCase().contains("full site")) {

                                enable = true;
                                keepdisable = false;
                                needtoanalyze = false;

                            }else if ((row.get("content").toString().toLowerCase().contains("disabl")
                            && !row.get("content").toString().toLowerCase().contains("disabled cache run"))
                                    || row.get("content").toString().toLowerCase().contains("abs")
                                    || row.get("content").toString().toLowerCase().contains("fail")
                                    || row.get("content").toString().toLowerCase().contains("block")
                                    || row.get("content").toString().toLowerCase().contains("custom")
                                    || row.get("content").toString().toLowerCase().contains("enabl") && row.get("content").toString().toLowerCase().contains("disabl")
                                    || row.get("content").toString().toLowerCase().contains("no longer supported")
                                    || row.get("content").toString().toLowerCase().contains("not supported")
                                    || row.get("content").toString().toLowerCase().contains("duplicate")
                                    || row.get("content").toString().toLowerCase().contains("mfa image")
                                    || row.get("content").toString().toLowerCase().contains("recaptcha")) {

                                keepdisable = true;
                                enable = false;
                                needtoanalyze = false;

                            }else if(!enable && !keepdisable){

                                needtoanalyze = true;

                            }
                        }else{
                            notesnotfound = true;
                        }
                    }
                    if(notesnotfound && !enable && !keepdisable && !needtoanalyze){
                        YADStatus.put(csid, "Notes not found");
                        System.out.println("CSID: " + csid + " : Notes not found");
                    }else {
                        if (enable) {
                            YADStatus.put(csid, "Enable");
                            System.out.println("CSID: " + csid + " : Enable");
                        }

                        if (keepdisable) {
                            YADStatus.put(csid, "Keep Disable");
                           System.out.println("CSID: " + csid + " : Keep Disable");
                        }

                        if (needtoanalyze) {
                            YADStatus.put(csid, "Can be Enabled");
                            System.out.println("CSID: " + csid + " : Can be Enabled");
                        }
                    }
                }

           //For Loop to process YAD Notes.
           for (Map.Entry me : YADStatus.entrySet()) {
               for (SumInfoVO addY : CSIDListInitial){
                   if(me.getKey().toString().equals(addY.getSum_info_id())){
                           addY.setYADNotes(me.getValue().toString());
                   }
               }
           }


        return CSIDListInitial;

       }catch (EmptyResultDataAccessException e) {
           System.out.println(e);
           return null;
       }

    }
}
