package com.bitisan.market.consumer;

import com.alibaba.fastjson.JSON;
import com.bitisan.constant.NettyCommand;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.feign.ExchangeOrderFeign;
import com.bitisan.market.handler.NettyHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RocketMQMessageListener(topic = "exchange-order-completed", consumerGroup = "market-exchange-order-completed")
public class ExchangeTradeCompletedConsumer implements RocketMQListener<String> {
	private Logger logger = LoggerFactory.getLogger(ExchangeTradeCompletedConsumer.class);
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private ExchangeOrderFeign exchangeOrderFeign;
	@Autowired
	private NettyHandler nettyHandler;


	@Override
	public void onMessage(String s) {
		List<ExchangeOrder> orders = JSON.parseArray(s, ExchangeOrder.class);
		for (ExchangeOrder order : orders) {
			String symbol = order.getSymbol();
			// 委托成交完成处理
			exchangeOrderFeign.tradeCompleted(order.getOrderId(), order.getTradedAmount(),
					order.getTurnover());
			// 推送订单成交
			messagingTemplate.convertAndSend(
					"/topic/market/order-completed/" + symbol + "/" + order.getMemberId(), order);
			nettyHandler.handleOrder(NettyCommand.PUSH_EXCHANGE_ORDER_COMPLETED, order);
		}
	}


}
