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

import java.math.BigDecimal;

@Component
@Slf4j
@RocketMQMessageListener(topic = "exchange-order-cancel-success", consumerGroup = "market-exchange-order-cancel-success")
public class ExchangeTradeCancelSuccessConsumer implements RocketMQListener<String> {
	private Logger logger = LoggerFactory.getLogger(ExchangeTradeCancelSuccessConsumer.class);

	@Autowired
	private ExchangeOrderFeign exchangeOrderFeign;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private NettyHandler nettyHandler;


	@Override
	public void onMessage(String s) {
		//logger.info("取消订单消息topic={},value={},size={}", record.topic(), record.value(), records.size());
		ExchangeOrder order = JSON.parseObject(s, ExchangeOrder.class);
		String symbol = order.getSymbol();
		// 调用服务处理
		exchangeOrderFeign.cancelOrder(order.getOrderId(), order.getTradedAmount(), order.getTurnover()==null? BigDecimal.ZERO:order.getTurnover());
		// 推送实时成交
		messagingTemplate.convertAndSend("/topic/market/order-canceled/" + symbol + "/" + order.getMemberId(),
				order);
		nettyHandler.handleOrder(NettyCommand.PUSH_EXCHANGE_ORDER_CANCELED, order);
	}


}
