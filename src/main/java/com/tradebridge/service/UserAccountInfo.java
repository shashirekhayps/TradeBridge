package com.tradebridge.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;


import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Margin;

@Service
public class UserAccountInfo {
    private final Map<String, KiteConnect> kiteConnects;

    public UserAccountInfo(Map<String, KiteConnect> kiteConnects) {
        this.kiteConnects = kiteConnects;
    }

    public void printAccountInfo() {
        kiteConnects.forEach((userId, kiteConnect) -> {
            try {
                Margin margins = kiteConnect.getMargins("equity");
                System.out.println("User ID: " + userId);
                System.out.println("Available Cash: " + margins.available.cash);
                System.out.println("Utilized Debits: " + margins.utilised.debits);
                System.out.println("-----------------------------");
            } catch (KiteException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public String getLoginURL(String user) {
        for (Map.Entry<String, KiteConnect> entry : kiteConnects.entrySet()) {
            if (entry.getKey().equals(user)) {
                return entry.getValue().getLoginURL();
            }
        }
        return null;
    }
}
