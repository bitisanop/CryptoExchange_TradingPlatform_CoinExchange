package com.bitisan.market.consumer;

import com.alibaba.fastjson.JSON;
import com.bitisan.constant.NettyCommand;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.feign.ExchangeOrderFeign;
import com.bitisan.market.handler.NettyHandler;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RocketMQMessageListener(topic = "exchange-trade", consumerGroup = "market-exchange-trade")
public class ExchangeTradeConsumer implements RocketMQListener<String> {
	private Logger logger = LoggerFactory.getLogger(ExchangeTradeConsumer.class);
	@Autowired
	private CoinProcessorFactory coinProcessorFactory;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private ExchangeOrderFeign exchangeOrderFeign;
	@Autowired
	private NettyHandler nettyHandler;
	@Value("${second.referrer.award}")
	private boolean secondReferrerAward;
	private ExecutorService executor = new ThreadPoolExecutor(30, 100, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.AbortPolicy());
	@Autowired
	private ExchangePushJob pushJob;

	@Override
	public void onMessage(String s) {
		List<ExchangeTrade> trades = JSON.parseArray(s,ExchangeTrade.class);
		executor.submit(new HandleTradeThread(trades));
	}


	public class HandleTradeThread implements Runnable {
		private List<ExchangeTrade> trades;

		private HandleTradeThread(List<ExchangeTrade> trades) {
			this.trades = trades;
		}

		@Override
		public void run() {
			//logger.info("topic={},value={}", record.topic(), record.value());
			try {
				String symbol = trades.get(0).getSymbol();
				CoinProcessor coinProcessor = coinProcessorFactory.getProcessor(symbol);
				for (ExchangeTrade trade : trades) {
					// 成交明细处理
					exchangeOrderFeign.processExchangeTrade(trade, secondReferrerAward);
					// 推送订单成交订阅
					ExchangeOrder buyOrder  = exchangeOrderFeign.findOne(trade.getBuyOrderId());
					ExchangeOrder sellOrder = exchangeOrderFeign.findOne(trade.getSellOrderId());
					messagingTemplate.convertAndSend(
							"/topic/market/order-trade/" + symbol + "/" + buyOrder.getMemberId(), buyOrder);
					messagingTemplate.convertAndSend(
							"/topic/market/order-trade/" + symbol + "/" + sellOrder.getMemberId(), sellOrder);
					nettyHandler.handleOrder(NettyCommand.PUSH_EXCHANGE_ORDER_TRADE, buyOrder);
					nettyHandler.handleOrder(NettyCommand.PUSH_EXCHANGE_ORDER_TRADE, sellOrder);
				}
				// 处理K线行情
				if (coinProcessor != null) {
					coinProcessor.process(trades);
				}
				pushJob.addTrades(symbol, trades);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
