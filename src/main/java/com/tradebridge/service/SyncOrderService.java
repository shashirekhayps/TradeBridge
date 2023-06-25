package com.tradebridge.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;


@Service
public class SyncOrderService {

    private final Map<String, KiteConnect> kiteConnects;

    public SyncOrderService(Map<String, KiteConnect> kiteConnects) {
        this.kiteConnects = kiteConnects;
    }

    public void syncOrders(String user1,String user2,String instrument) {
        KiteConnect kiteConnectUser1 = kiteConnects.get(user1);
        KiteConnect kiteConnectUser2 = kiteConnects.get(user2);
        try {
            List<Order> user1Orders = filterOrders(kiteConnectUser1,instrument);
            List<Order> user2Orders = filterOrders(kiteConnectUser2,instrument);
            List<Order> uniqueToUser1 = findUniqueToUser1(user1Orders,user2Orders);
            List<Order> uniqueToUser2 = findUniqueToUser2(user1Orders,user2Orders);
            placeOrder(uniqueToUser1,kiteConnectUser2);
            placeOrder(uniqueToUser2,kiteConnectUser1);
        } catch (KiteException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Order> findUniqueToUser1(List<Order> user1Orders,List<Order> user2Orders) {
        return user1Orders.stream()
                .filter(user1Order -> user2Orders.stream()
                        .noneMatch(user2Order -> user1Order.tradingSymbol.equals(user2Order.tradingSymbol)
                                && user1Order.transactionType.equals(user2Order.transactionType)
                                && user1Order.validity.equals(user2Order.validity)))
                .toList();
    }

    public List<Order> findUniqueToUser2(List<Order> user1Orders,List<Order> user2Orders) {
        return user2Orders.stream()
                .filter(user2Order -> user1Orders.stream()
                        .noneMatch(user1Order -> user2Order.tradingSymbol.equals(user1Order.tradingSymbol)
                                && user2Order.transactionType.equals(user1Order.transactionType)
                                && user2Order.validity.equals(user1Order.validity)))
                .toList();
    }

    public void placeOrder(List<Order> uniqOrderToPlace,KiteConnect kiteConnectUser) {

        for (Order orderToPlace: uniqOrderToPlace
             ) {

            OrderParams orderParams = new OrderParams();
            orderParams.quantity = Integer.valueOf(orderToPlace.quantity);
            orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
            orderParams.tradingsymbol = orderToPlace.tradingSymbol;
            orderParams.product = Constants.PRODUCT_NRML;
            orderParams.exchange = Constants.EXCHANGE_NSE;
            orderParams.transactionType = orderToPlace.transactionType;
            orderParams.validity = orderToPlace.validity;
            orderParams.price = getAdjustedValue(orderToPlace);

            Order order = null;
            try {
                order = kiteConnectUser.placeOrder(orderParams, Constants.VARIETY_REGULAR);
            } catch (KiteException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(order.orderId);
        }
    }

    private Double getAdjustedValue(Order order) {
        if(order.transactionType.equals(Constants.TRANSACTION_TYPE_BUY)) {
           return Double.parseDouble(order.price ) + 1.0 ;
        }
        return Double.parseDouble(order.price ) - 1.0 ;
    }

    private List<Order> filterOrders(KiteConnect kiteConnect,String instrument) throws IOException, KiteException {
        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        LocalDateTime currentDateTime = LocalDateTime.now(istZone);
        LocalDateTime tenMinutesAgo = currentDateTime.minusMinutes(10);
        Date tenMinutesAgoDate = Date.from(tenMinutesAgo.atZone(istZone).toInstant());

        return kiteConnect.getOrders().stream().
                filter(order -> order.status.equals("COMPLETE")
                        && order.tradingSymbol.startsWith(instrument)
                        && order.orderTimestamp.after(tenMinutesAgoDate)
                ).toList();
    }
}
