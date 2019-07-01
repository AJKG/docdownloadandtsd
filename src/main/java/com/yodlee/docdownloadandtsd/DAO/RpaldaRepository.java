package com.yodlee.docdownloadandtsd.DAO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class RpaldaRepository {

    @Autowired
    @Qualifier("rpaldaJdbcTemplate")
    JdbcTemplate jdbc;

    //Testing from Chetan's Account
    //Test1
    //Test2

    public ArrayList<String> getDiff() throws Exception {


        try{
            String sql = "select mig_id as migration_id,data_diff as data_diff from migration_request where REQUEST_DATE > sysdate-1 and data_diff is not null";

            List<Map<String, Object>> rows = jdbc.queryForList(sql);

            for (Map row : rows) {
                System.out.println("Setting: "+row.get("migration_id")+" "+row.get("data_diff"));
            }



        }catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
        return null;
    }
}
