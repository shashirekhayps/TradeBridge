package com.tradebridge.service;


import java.io.IOException;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;

public class KiteConnectFactory {

    public KiteConnect createKiteConnect(String apiKey, String requestToken, String apiSecret) {
        KiteConnect kiteConnect = new KiteConnect(apiKey);
        generateAndSetSession(kiteConnect,requestToken, apiSecret);
        return kiteConnect;
    }

    private void generateAndSetSession(KiteConnect kiteSdk, String requestToken, String apiSecret) {
        try {
            User user = kiteSdk.generateSession(requestToken, apiSecret);
            kiteSdk.setAccessToken(user.accessToken);
            kiteSdk.setPublicToken(user.publicToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (KiteException e) {
            throw new RuntimeException(e);
        }
    }
}
