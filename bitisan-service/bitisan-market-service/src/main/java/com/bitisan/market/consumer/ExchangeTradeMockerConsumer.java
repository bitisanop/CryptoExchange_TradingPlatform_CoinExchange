package com.bitisan.market.consumer;

import com.alibaba.fastjson.JSON;
import com.bitisan.market.job.ExchangePushJob;
import com.bitisan.market.processor.CoinProcessor;
import com.bitisan.market.processor.CoinProcessorFactory;
import com.bitisan.pojo.ExchangeTrade;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RocketMQMessageListener(topic = "exchange-trade-mocker", consumerGroup = "market-exchange-trade-mocker")
public class ExchangeTradeMockerConsumer implements RocketMQListener<String> {
	private Logger logger = LoggerFactory.getLogger(ExchangeTradeMockerConsumer.class);
	@Autowired
	private ExchangePushJob pushJob;
	@Autowired
	private CoinProcessorFactory coinProcessorFactory;


	@Override
	public void onMessage(String s) {

		logger.info("mock数据topic={},value={},size={}", "exchange-trade-mocker", s, 1);
		List<ExchangeTrade> trades = JSON.parseArray(s, ExchangeTrade.class);
		String symbol = trades.get(0).getSymbol();
		// 处理行情
		CoinProcessor coinProcessor = coinProcessorFactory.getProcessor(symbol);
		if (coinProcessor != null) {
			coinProcessor.process(trades);
		}
		pushJob.addTrades(symbol, trades);
	}


}
