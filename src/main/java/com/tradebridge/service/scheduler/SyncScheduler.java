package com.tradebridge.service.scheduler;

import com.tradebridge.service.SyncOrderService;

public class SyncScheduler {
    private final SyncOrderService syncOrderService;

    public SyncScheduler(SyncOrderService syncOrderService) {
        this.syncOrderService = syncOrderService;
    }

   // @Scheduled(fixedRate = 120000)
    public void syncOrdersPeriodically() {
        String user1 = "chandan";
        String user2 = "shashi";
        String instrument = "NIFTY";
        syncOrderService.syncOrders(user1, user2, instrument,2);
    }
}
