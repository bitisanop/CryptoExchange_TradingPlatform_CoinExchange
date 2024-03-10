package com.bitisan.exchange.config;

import com.alibaba.fastjson.JSON;
import com.bitisan.exchange.Trader.CoinTrader;
import com.bitisan.exchange.Trader.CoinTraderFactory;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.entity.ExchangeOrderDetail;
import com.bitisan.exchange.service.ExchangeOrderDetailService;
import com.bitisan.exchange.service.ExchangeOrderService;
import com.bitisan.exchange.util.OrderUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CoinTraderEvent implements ApplicationRunner {
    private Logger log = LoggerFactory.getLogger(CoinTraderEvent.class);
    @Autowired
    CoinTraderFactory coinTraderFactory;
    @Autowired
    private ExchangeOrderService exchangeOrderService;
    @Autowired
    private ExchangeOrderDetailService exchangeOrderDetailService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("======initialize coinTrader======");
        // coinTraderFactory.getTraderMap();
        Map<String, CoinTrader> traders = coinTraderFactory.getTraderMap();
        traders.forEach((symbol,trader) ->{
            log.info("======CoinTrader Process: " + symbol + "======");
            List<ExchangeOrder> orders = exchangeOrderService.findAllTradingOrderBySymbol(symbol);
            log.info("Initialize: find all trading orders, total count( " + orders.size() + ")");
            List<ExchangeOrder> tradingOrders = new ArrayList<>();
            List<ExchangeOrder> completedOrders = new ArrayList<>();
            orders.forEach(order -> {
                BigDecimal tradedAmount = BigDecimal.ZERO;
                BigDecimal turnover = BigDecimal.ZERO;
                List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(order.getOrderId());

                for(ExchangeOrderDetail trade:details){
                    tradedAmount = tradedAmount.add(trade.getAmount());
                    turnover = turnover.add(trade.getAmount().multiply(trade.getPrice()));
                }
                order.setTradedAmount(tradedAmount);
                order.setTurnover(turnover);
                if(!OrderUtils.isCompleted(order)){
                    tradingOrders.add(order);
                }
                else{
                    completedOrders.add(order);
                }
            });
            log.info("Initialize: tradingOrders total count( " + tradingOrders.size() + ")");
            try {
                trader.trade(tradingOrders);
            } catch (ParseException e) {
                e.printStackTrace();
                log.info("异常：trader.trade(tradingOrders);");
            }
            //判断已完成的订单发送消息通知
            if(completedOrders.size() > 0){
                log.info("Initialize: completedOrders total count( " + tradingOrders.size() + ")");
                rocketMQTemplate.convertAndSend("exchange-order-completed", JSON.toJSONString(completedOrders));
            }
            trader.setReady(true);
        });
    }
}
