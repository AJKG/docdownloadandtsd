package com.yodlee.docdownloadandtsd.Configuration;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebMvc
public class MultipleDBConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        converters.add(jsonConverter);
    }

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

    @Bean(name = "rpalda")
    @ConfigurationProperties(prefix = "spring.rpaldadatasource")
    public DataSource rpaldaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "rpaldaJdbcTemplate")
    public JdbcTemplate rpaldaJdbcTemplate(@Qualifier("rpalda") DataSource dsrpalda) {
        return new JdbcTemplate(dsrpalda);
    }

}
