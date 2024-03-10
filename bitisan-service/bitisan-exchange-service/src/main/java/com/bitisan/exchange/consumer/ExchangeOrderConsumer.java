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

@Slf4j
@Component
@RocketMQMessageListener(topic = "exchange-order", consumerGroup = "exchange-order-handle")
public class ExchangeOrderConsumer implements RocketMQListener<String> {

    @Autowired
    private CoinTraderFactory traderFactory;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @Override
    public void onMessage(String s) {
        ExchangeOrder order = JSON.parseObject(s, ExchangeOrder.class);
        if(order == null || order.getOrderId()==null){
            return ;
        }
        CoinTrader trader = traderFactory.getTrader(order.getSymbol());
        //如果当前币种交易暂停会自动取消订单
        if (trader.isTradingHalt() || !trader.getReady()) {
            //撮合器未准备完成，撤回当前等待的订单
            rocketMQTemplate.convertAndSend("exchange-order-cancel-success", JSON.toJSONString(order));
        } else {
            try {
                long startTick = System.currentTimeMillis();
                trader.trade(order);
                log.info("complete trade,{}ms used!", System.currentTimeMillis() - startTick);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("====交易出错，退回订单===",e);
                rocketMQTemplate.convertAndSend("exchange-order-cancel-success", JSON.toJSONString(order));
            }
        }
    }
}
