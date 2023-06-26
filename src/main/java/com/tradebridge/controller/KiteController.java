package com.tradebridge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tradebridge.service.KiteLoginService;
import com.tradebridge.service.SyncOrderService;
import com.tradebridge.service.UserAccountInfo;

@RestController
public class KiteController {
    private final UserAccountInfo userAccountInfo;
    private final SyncOrderService syncOrderService;

    private final KiteLoginService sessionService;

    public KiteController(UserAccountInfo userAccountInfo, SyncOrderService syncOrderService, KiteLoginService sessionService) {
        this.userAccountInfo = userAccountInfo;
        this.syncOrderService = syncOrderService;
        this.sessionService = sessionService;
    }

    @GetMapping("/account-info")
    public void getAccountInfo() {
        userAccountInfo.printAccountInfo();
    }

    @GetMapping("/loginurl/{user}")
    public String getLoginUrl(@PathVariable String user) {
        return userAccountInfo.getLoginURL(user);
    }

    @PostMapping("/sync/{user1}/{user2}")
    public void sync(@PathVariable String user1,
                     @PathVariable  String user2,
                     @RequestParam("instrument") String instrument,
                     @RequestParam("duration") int duration
    ) {
         syncOrderService.syncOrders(user1,user2,instrument,duration);
    }

    @PostMapping("/login/{user1}")
    public void login(@PathVariable String user1, @RequestBody String token) {
        sessionService.login(user1, token);
    }
}
