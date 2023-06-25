package com.tradebridge.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kite")
public class KiteProperties {
    private Map<String, UserProperty> users = new HashMap<>();

    public Map<String, UserProperty> getUsers() {
        return users;
    }

    public void setUsers(Map<String, UserProperty> users) {
        this.users = users;
    }

    public static class UserProperty {
        private String requestToken;
        private String apiSecret;

        private String apiKey;

        public String getRequestToken() {
            return requestToken;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public void setRequestToken(String requestToken) {
            this.requestToken = requestToken;
        }

        public String getApiSecret() {
            return apiSecret;
        }

        public void setApiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
        }
    }
}
