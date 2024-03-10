package com.bitisan.exchange.util;

import com.bitisan.constant.ExchangeOrderDirection;
import com.bitisan.constant.ExchangeOrderStatus;
import com.bitisan.constant.ExchangeOrderType;
import com.bitisan.exchange.entity.ExchangeOrder;

public class OrderUtils {

    public static boolean isCompleted(ExchangeOrder order){
        if(order.getStatus() != ExchangeOrderStatus.TRADING) {
            return true;
        } else{
            if(order.getType() == ExchangeOrderType.MARKET_PRICE && order.getDirection() == ExchangeOrderDirection.BUY){
                return order.getAmount().compareTo(order.getTurnover()) <= 0;
            }
            else{
                return order.getAmount().compareTo(order.getTradedAmount()) <= 0;
            }
        }
    }
}
