package com.registro.CruzMarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CruzMarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(CruzMarketApplication.class, args);
    }
}

