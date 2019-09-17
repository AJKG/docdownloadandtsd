package com.yodlee.docdownloadandtsd.Services;


import com.yodlee.docdownloadandtsd.VO.SumInfoStatsVO;
import com.yodlee.docdownloadandtsd.VO.SumInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SumInfoStats {

    @Autowired
    @Qualifier("sitepJdbcTemplate")
    JdbcTemplate jdbc;


    public List<SumInfoStatsVO> getVolume(List<SumInfoVO> Sum_info){

        ArrayList<String> CSIDList = new ArrayList<>();
        for(SumInfoVO csid : Sum_info){
            CSIDList.add(csid.getSum_info_id());
        }

        int ListSize = CSIDList.size();

        int looplength = ListSize/999;

        if(ListSize%999 > 0){
            looplength = looplength+1;
        }



        List<SumInfoStatsVO> VolumeCSID = new ArrayList<>();

        try{
            int cc = 0;
            for(int i = 1; i<=looplength; i++){

                String QueryPart = "";
                int loopinitiator = (i-1)*999;
                int loopend = 999 + (i-1)*999;
                for(int j = loopinitiator; j<loopend; j++){
                    cc++;
                    if(cc == (ListSize)) {
                        QueryPart = QueryPart + CSIDList.get(j);
                        break;
                    }else{
                        QueryPart = QueryPart + CSIDList.get(j) + ",";
                    }

                }

                String sql = "select sum_info_id, COALESCE(sum(num_errors), 0) as Total_request,"
                                +"COALESCE(ROUND(((sum(decode(TYPE_OF_ERROR, 0,num_errors,433,num_errors,473,num_errors,474,num_errors,801,num_errors,0))/sum(num_errors))*100),2), 0) as success_percentage "
                                +"from site_stats_suminfo "
                                +"where sum_info_id in ("+QueryPart+") "
                                +"and timestamp > (sysdate-5) "
                                +"group by sum_info_id";


                List<Map<String, Object>> rows = jdbc.queryForList(sql);

                for(Map row : rows){
                    SumInfoStatsVO ssv = new SumInfoStatsVO();
                    ssv.setSum_info_id(row.get("sum_info_id").toString());
                    ssv.setTotal_request(row.get("Total_request").toString());
                    ssv.setSuccess_percentage(row.get("success_percentage").toString());

                    VolumeCSID.add(ssv);
                }
            }
        }catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }


        return VolumeCSID;
    }
}
