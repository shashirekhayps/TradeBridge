package com.tradebridge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.tradebridge.service.SyncOrderService;
import com.tradebridge.service.UserAccountInfo;

@RestController
public class KiteController {
    private final UserAccountInfo userAccountInfo;
    private final SyncOrderService syncOrderService;

    public KiteController(UserAccountInfo userAccountInfo, SyncOrderService syncOrderService) {
        this.userAccountInfo = userAccountInfo;
        this.syncOrderService = syncOrderService;
    }

    @GetMapping("/account-info")
    public void getAccountInfo() {
        userAccountInfo.printAccountInfo();
    }

    @GetMapping("/loginurl/{user}")
    public String getLoginUrl(@PathVariable String user) {
        return userAccountInfo.getLoginURL(user);
    }

    @GetMapping("/sync/{user1}/{user2}")
    public void sync(@PathVariable String user1,String user2) {
         syncOrderService.syncOrders(user1,user2,"BANK");
    }
}