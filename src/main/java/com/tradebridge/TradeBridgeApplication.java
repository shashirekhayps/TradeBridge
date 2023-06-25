package com.tradebridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.tradebridge.config.KiteProperties;

@SpringBootApplication
@EnableConfigurationProperties(KiteProperties.class)
public class TradeBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeBridgeApplication.class, args);
    }

}
