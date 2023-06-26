package com.tradebridge.service;


import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tradebridge.config.KiteProperties;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;

@Service
public class KiteLoginService {

    private final KiteProperties kiteProperties;

    private final Map<String,KiteConnect> kiteConnects;



    public KiteLoginService(KiteProperties kiteProperties, Map<String, KiteConnect> kiteConnects) {
        this.kiteProperties = kiteProperties;
        this.kiteConnects = kiteConnects;
    }

    public void login(String user,String token) {
        KiteProperties.UserProperty userProperty = kiteProperties.getUsers().get(user);
        KiteConnect kiteConnect = kiteConnects.get(user);
        generateAndSetSession(kiteConnect,token, userProperty.getApiSecret());
        System.out.println("user : " + user + " successfully logged-in");
    }
    private void generateAndSetSession(KiteConnect kiteSdk, String requestToken, String apiSecret) {
        try {
            User user = kiteSdk.generateSession(requestToken, apiSecret);
            kiteSdk.setAccessToken(user.accessToken);
            kiteSdk.setPublicToken(user.publicToken);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (KiteException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
