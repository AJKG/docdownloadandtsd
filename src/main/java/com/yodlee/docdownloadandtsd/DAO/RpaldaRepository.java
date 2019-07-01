package com.yodlee.docdownloadandtsd.DAO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;


public class RpaldaRepository {

    @Autowired
    @Qualifier("sitepJdbcTemplate")
    JdbcTemplate jdbc;

}
