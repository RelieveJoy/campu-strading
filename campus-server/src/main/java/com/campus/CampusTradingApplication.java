package com.campus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@Slf4j
@EnableCaching
public class CampusTradingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusTradingApplication.class, args);
        log.info("server started");
    }
}
