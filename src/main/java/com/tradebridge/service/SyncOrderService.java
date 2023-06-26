package com.tradebridge.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;


@Service
public class SyncOrderService {

    private final Map<String,KiteConnect> kiteConnects;
    private final RedisTemplate<String, String> redisTemplate;

    public SyncOrderService(Map<String, KiteConnect> kiteConnects, RedisTemplate<String, String> redisTemplate) {
        this.kiteConnects = kiteConnects;
        this.redisTemplate = redisTemplate;
    }

    public void syncOrders(String user1,String user2,String instrument,int duration) {
        KiteConnect kiteConnectUser1 = kiteConnects.get(user1);
        KiteConnect kiteConnectUser2 = kiteConnects.get(user2);
        try {
            List<Order> user1Orders = filterOrders(kiteConnectUser1,instrument,duration);
            List<Order> user2Orders = filterOrders(kiteConnectUser2,instrument,1440);
            List<Order> uniqueToUser1 = findUniqueToUser(user1Orders,user2Orders);
            placeOrder(uniqueToUser1,kiteConnectUser2);
        } catch (KiteException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Order> findUniqueToUser(List<Order> user1Orders, List<Order> user2Orders) {
        return user1Orders.stream()
                .filter(user1Order -> user2Orders.stream()
                        .noneMatch(user2Order -> user1Order.tradingSymbol.equals(user2Order.tradingSymbol)
                                && user1Order.transactionType.equals(user2Order.transactionType)
                                && user1Order.validity.equals(user2Order.validity)
                                && user1Order.quantity.equals(user2Order.quantity)
                                && redisTemplate.opsForValue().get(getKey(user1Order)) != null)
                        )
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
            orderParams.exchange = Constants.EXCHANGE_NFO;
            orderParams.transactionType = orderToPlace.transactionType;
            orderParams.price = getAdjustedValue(orderToPlace);

            Order order = null;
            System.out.println("placing order " +
                    orderParams.tradingsymbol +
                    " of quantity : " +
                    orderParams.quantity + " at order price " + orderParams.price);
            try {
                String key = getKey(orderToPlace);
                if(redisTemplate.opsForValue().get(key) == null) {
                    order = kiteConnectUser.placeOrder(orderParams, Constants.VARIETY_REGULAR);
                    redisTemplate.opsForValue().set(key, order.orderId);
                } else {
                    System.out.println(" Duplicate Order found for order " + orderToPlace.tradingSymbol );
                }
            } catch (KiteException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
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

    private List<Order> filterOrders(KiteConnect kiteConnect,String instrument,int duration) throws IOException, KiteException {
        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        LocalDateTime currentDateTime = LocalDateTime.now(istZone);
        LocalDateTime tenMinutesAgo = currentDateTime.minusMinutes(duration);
        Date tenMinutesAgoDate = Date.from(tenMinutesAgo.atZone(istZone).toInstant());

        return kiteConnect.getOrders().stream().
                filter(order -> order.status.equals("COMPLETE")
                        && order.tradingSymbol.startsWith(instrument)
                        && order.orderTimestamp.after(tenMinutesAgoDate)
                ).toList();
    }

    private String getKey(Order orderToPlace) {
        return orderToPlace.tradingSymbol + orderToPlace.orderTimestamp.toString() + orderToPlace.quantity + orderToPlace.transactionType;
    }
}
