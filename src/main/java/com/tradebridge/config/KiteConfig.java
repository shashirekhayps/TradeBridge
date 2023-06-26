package com.tradebridge.config;

import java.util.HashMap;
import java.util.Map;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.tradebridge.service.KiteConnectFactory;
import com.zerodhatech.kiteconnect.KiteConnect;

@Configuration
public class KiteConfig {
    private final KiteProperties kiteProperties;


    public KiteConfig(KiteProperties kiteProperties) {
        this.kiteProperties = kiteProperties;
    }

    @Bean
    public KiteConnectFactory kiteConnectFactory() {
        return new KiteConnectFactory();
    }

    @Bean
    public Map<String, KiteConnect> kiteConnects(KiteConnectFactory kiteConnectFactory) {
        Map<String, KiteConnect> kiteConnects = new HashMap<>();
        for (Map.Entry<String, KiteProperties.UserProperty> entry : kiteProperties.getUsers().entrySet()) {
            String username = entry.getKey();
            KiteProperties.UserProperty userProperty = entry.getValue();
            KiteConnect kiteConnect = kiteConnectFactory.createKiteConnect(userProperty.getApiKey());
            kiteConnects.put(username, kiteConnect);
        }
        return kiteConnects;
    }
}
