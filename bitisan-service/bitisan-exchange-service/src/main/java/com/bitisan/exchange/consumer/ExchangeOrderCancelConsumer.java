package com.bitisan.exchange.consumer;

import com.alibaba.fastjson.JSON;
import com.bitisan.exchange.Trader.CoinTrader;
import com.bitisan.exchange.Trader.CoinTraderFactory;
import com.bitisan.exchange.entity.ExchangeOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RocketMQMessageListener(topic = "exchange-order-cancel", consumerGroup = "exchange-order-cancel-handle")
public class ExchangeOrderCancelConsumer implements RocketMQListener<String> {

    @Autowired
    private CoinTraderFactory traderFactory;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @Override
    public void onMessage(String s) {
        log.info("取消订单topic={},s={}","exchange-order-cancel",s);
        ExchangeOrder order = JSON.parseObject(s, ExchangeOrder.class);
        if(order == null){
            return ;
        }
        CoinTrader trader = traderFactory.getTrader(order.getSymbol());
        if(trader.getReady()) {
            try {
                ExchangeOrder result = trader.cancelOrder(order);
                if (result != null) {
                    rocketMQTemplate.convertAndSend("exchange-order-cancel-success", JSON.toJSONString(result));
                }
            }catch (Exception e){
                log.info("====取消订单出错===",e);
                e.printStackTrace();
            }
        }
    }
}
