package com.yodlee.docdownloadandtsd.Configuration;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MultipleDBConfig {

    @Bean(name = "sitep")
    @ConfigurationProperties(prefix = "spring.sitepdatasource")
    public DataSource sitepDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "sitepJdbcTemplate")
    public JdbcTemplate sitepjdbcTemplate(@Qualifier("sitep") DataSource dssitep) {
        return new JdbcTemplate(dssitep);
    }

    @Bean(name = "repalda")
    @ConfigurationProperties(prefix = "spring.repaldadatasource")
    public DataSource repaldaDataSource() {
        return  DataSourceBuilder.create().build();
    }

    @Bean(name = "repaldaJdbcTemplate")
    public JdbcTemplate repaldaJdbcTemplate(@Qualifier("repalda") DataSource dsrepalda) {
        return new JdbcTemplate(dsrepalda);
    }

}
