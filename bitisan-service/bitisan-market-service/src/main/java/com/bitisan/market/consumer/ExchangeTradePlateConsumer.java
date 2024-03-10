package com.bitisan.market.consumer;

import com.alibaba.fastjson.JSON;
import com.bitisan.market.job.ExchangePushJob;
import com.bitisan.market.processor.CoinProcessorFactory;
import com.bitisan.pojo.TradePlate;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQMessageListener(topic = "exchange-trade-plate", consumerGroup = "market-exchange-trade-plate")
public class ExchangeTradePlateConsumer implements RocketMQListener<String> {
	private Logger logger = LoggerFactory.getLogger(ExchangeTradePlateConsumer.class);
	@Autowired
	private ExchangePushJob pushJob;
	@Autowired
	private CoinProcessorFactory coinProcessorFactory;


	@Override
	public void onMessage(String s) {
		TradePlate plate = JSON.parseObject(s, TradePlate.class);
		String symbol = plate.getSymbol();
		pushJob.addPlates(symbol, plate);
	}


}
