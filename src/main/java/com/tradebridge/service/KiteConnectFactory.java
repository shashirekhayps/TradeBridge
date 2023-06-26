package com.tradebridge.service;


import com.zerodhatech.kiteconnect.KiteConnect;

public class KiteConnectFactory {

    public KiteConnect createKiteConnect(String apiKey) {
        return new KiteConnect(apiKey);
    }
}
