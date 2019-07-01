package com.yodlee.docdownloadandtsd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DocdownloadandtsdApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocdownloadandtsdApplication.class, args);
    }

}
