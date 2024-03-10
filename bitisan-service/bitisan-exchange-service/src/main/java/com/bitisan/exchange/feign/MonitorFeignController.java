package com.bitisan.exchange.feign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bitisan.constant.ExchangeCoinPublishType;
import com.bitisan.constant.ExchangeOrderDirection;
import com.bitisan.exchange.Trader.CoinTrader;
import com.bitisan.exchange.Trader.CoinTraderFactory;
import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.entity.ExchangeOrderDetail;
import com.bitisan.exchange.service.ExchangeCoinService;
import com.bitisan.exchange.service.ExchangeOrderDetailService;
import com.bitisan.exchange.service.ExchangeOrderService;
import com.bitisan.exchange.util.OrderUtils;
import com.bitisan.pojo.TradePlateItem;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.MessageResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/monitorFeign")
public class MonitorFeignController {
	private Logger log = LoggerFactory.getLogger(MonitorFeignController.class);
	@Autowired
	private CoinTraderFactory factory;
	@Autowired
    private ExchangeOrderService exchangeOrderService;
    @Autowired
    private ExchangeOrderDetailService exchangeOrderDetailService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private ExchangeCoinService exchangeCoinService;
	@Autowired
	private LocaleMessageSourceService msService;

	@RequestMapping("engines")
	public Map<String, Integer> engines() {
		Map<String, CoinTrader> traders = factory.getTraderMap();
		Map<String, Integer> symbols = new HashMap<String, Integer>();
		traders.forEach((key, trader) -> {
			if(trader.isTradingHalt()) {
				symbols.put(key, 2);
			}else {
				symbols.put(key, 1);
			}
		});
		return symbols;
	}

	@RequestMapping("plate")
	public Map<String, List<TradePlateItem>> traderPlate(@RequestParam("symbol") String symbol) {
		Map<String, List<TradePlateItem>> result = new HashMap<>();
		CoinTrader trader = factory.getTrader(symbol);
		if (trader == null) {
			return null;
		}
		result.put("bid", trader.getTradePlate(ExchangeOrderDirection.BUY).getItems());
		result.put("ask", trader.getTradePlate(ExchangeOrderDirection.SELL).getItems());
		return result;
	}

	@RequestMapping("plate-mini")
	public Map<String, JSONObject> traderPlateMini(@RequestParam("symbol") String symbol) {
		Map<String, JSONObject> result = new HashMap<>();
		CoinTrader trader = factory.getTrader(symbol);
		if (trader == null) {
			return null;
		}
		result.put("bid", trader.getTradePlate(ExchangeOrderDirection.BUY).toJSON(24));
		result.put("ask", trader.getTradePlate(ExchangeOrderDirection.SELL).toJSON(24));
		return result;
	}

	@RequestMapping("plate-full")
	public Map<String, JSONObject> traderPlateFull(String symbol) {
		Map<String, JSONObject> result = new HashMap<>();
		CoinTrader trader = factory.getTrader(symbol);
		if (trader == null) {
			return null;
		}
		result.put("bid", trader.getTradePlate(ExchangeOrderDirection.BUY).toJSON(100));
		result.put("ask", trader.getTradePlate(ExchangeOrderDirection.SELL).toJSON(100));
		return result;
	}

	/**
	 * 重置交易引擎，重新开始（应用场景主要是防止plate盘面出现脏数据（倒挂等）
	 * @param symbol
	 * @return
	 */

	@RequestMapping("reset-trader")
	public MessageResult resetTrader(@RequestParam("symbol")String symbol) {
		log.info("======[Start]Reset CoinTrader: " + symbol + "======");
		if(factory.containsTrader(symbol)) {
			// 交易对引擎不存在，则创建
			// 检查该币种在数据库定义中是否存在
			ExchangeCoin coin = exchangeCoinService.findBySymbol(symbol);
			if (coin == null || coin.getEnable() != 1) {
				return MessageResult.error(500, "CURRENCY_PAIR_DOES_NOT_EXIST");
			}

			if(coin.getEnable() != 1) {
				return MessageResult.error(500, "PROHIBITION_OF_CURRENCY_PAIRS");
			}

			CoinTrader trader= factory.getTrader(symbol);
			if(!trader.isTradingHalt()) {
				return MessageResult.error(500, "STOP_CURRENT_ENGINE");
			}
			CoinTrader newTrader = new CoinTrader(symbol);
			newTrader.setRocketMQTemplate(rocketMQTemplate);
			newTrader.setBaseCoinScale(coin.getBaseCoinScale());
			newTrader.setCoinScale(coin.getCoinScale());
			newTrader.setPublishType(ExchangeCoinPublishType.creator(coin.getPublishType()));
			newTrader.setClearTime(coin.getClearTime());

			// 创建成功以后需要对未处理订单预处理
			log.info("======CoinTrader Process: " + symbol + "======");
			List<ExchangeOrder> orders = exchangeOrderService.findAllTradingOrderBySymbol(symbol);
			List<ExchangeOrder> tradingOrders = new ArrayList<>();
			List<ExchangeOrder> completedOrders = new ArrayList<>();
			orders.forEach(order -> {
				BigDecimal tradedAmount = BigDecimal.ZERO;
				BigDecimal turnover = BigDecimal.ZERO;
				List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(order.getOrderId());

				for(ExchangeOrderDetail od:details){
					tradedAmount = tradedAmount.add(od.getAmount());
					turnover = turnover.add(od.getAmount().multiply(od.getPrice()));
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
			try {
				newTrader.trade(tradingOrders);
			} catch (ParseException e) {
				e.printStackTrace();
				log.info("异常：trader.trade(tradingOrders);");
				return MessageResult.error(500, symbol + msService.getMessage("ENGINE_CREATION_FAILED"));
			}
			//判断已完成的订单发送消息通知
			if(completedOrders.size() > 0){
				rocketMQTemplate.convertAndSend("exchange-order-completed", JSON.toJSONString(completedOrders));
			}
			newTrader.setReady(true);
			factory.resetTrader(symbol, newTrader);
			log.info("======[END]Reset CoinTrader: " + symbol + " successful======");
			return MessageResult.success(symbol+ msService.getMessage("ENGINE_CREATED_SUCCESSFULLY"));
		}else {
			return MessageResult.error(500, symbol + msService.getMessage("ENGINE_DOES_NOT_EXIST"));
		}
	}

	@RequestMapping("stop-trader")
	public MessageResult stopTrader(String symbol) {
		CoinTrader trader = factory.getTrader(symbol);
		log.info("======Stop CoinTrader: " + symbol + "======");
		if(trader == null) {
			return MessageResult.error(500, symbol + msService.getMessage("CURRENCY_PAIR_ENGINE_DOES_NOT_EXIST"));
		}else {
			if(trader.isTradingHalt()) {
				return MessageResult.error(500, symbol + msService.getMessage("ENGINE_STATE_HAS_STOPPED"));
			}else {
				trader.haltTrading();
				return MessageResult.success("ENGINE_STOPPED_SUCCESSFULLY");
			}
		}
	}

	@RequestMapping("overview")
	public JSONObject traderOverview(@RequestParam("symbol") String symbol){
		CoinTrader trader = factory.getTrader(symbol);
		if(trader == null ) {
			return null;
		}
		JSONObject result = new JSONObject();
		//卖盘信息
		JSONObject ask = new JSONObject();
		//买盘信息
		JSONObject bid = new JSONObject();
		ask.put("limit_price_order_count",trader.getLimitPriceOrderCount(ExchangeOrderDirection.SELL));
		ask.put("market_price_order_count",trader.getSellMarketQueue().size());
		ask.put("depth",trader.getTradePlate(ExchangeOrderDirection.SELL).getDepth());
		bid.put("limit_price_order_count",trader.getLimitPriceOrderCount(ExchangeOrderDirection.BUY));
		bid.put("market_price_order_count",trader.getBuyMarketQueue().size());
		bid.put("depth",trader.getTradePlate(ExchangeOrderDirection.BUY).getDepth());
		result.put("ask",ask);
		result.put("bid",bid);
		return result;
	}

	@RequestMapping("start-trader")
	public MessageResult startTrader(@RequestParam("symbol")String symbol) {
		log.info("======Start CoinTrader: " + symbol + "======");
		if(!factory.containsTrader(symbol)) {
			// 交易对引擎不存在，则创建
			// 检查该币种在数据库定义中是否存在
			ExchangeCoin coin = exchangeCoinService.findBySymbol(symbol);
			if (coin == null || coin.getEnable() != 1) {
				return MessageResult.error(500, "CURRENCY_PAIR_DOES_NOT_EXIST");
			}
			if(coin.getEnable() != 1) {
				return MessageResult.error(500, "PROHIBITION_OF_CURRENCY_PAIRS");
			}
			CoinTrader newTrader = new CoinTrader(symbol);
			newTrader.setRocketMQTemplate(rocketMQTemplate);
			newTrader.setBaseCoinScale(coin.getBaseCoinScale());
			newTrader.setCoinScale(coin.getCoinScale());
			newTrader.setPublishType(ExchangeCoinPublishType.creator(coin.getPublishType()));
			newTrader.setClearTime(coin.getClearTime());

			// 创建成功以后需要对未处理订单预处理
			log.info("======CoinTrader Process: " + symbol + "======");
			List<ExchangeOrder> orders = exchangeOrderService.findAllTradingOrderBySymbol(symbol);
			List<ExchangeOrder> tradingOrders = new ArrayList<>();
			List<ExchangeOrder> completedOrders = new ArrayList<>();
			orders.forEach(order -> {
				BigDecimal tradedAmount = BigDecimal.ZERO;
				BigDecimal turnover = BigDecimal.ZERO;
				List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(order.getOrderId());

				for(ExchangeOrderDetail od:details){
					tradedAmount = tradedAmount.add(od.getAmount());
					turnover = turnover.add(od.getAmount().multiply(od.getPrice()));
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
			try {
				newTrader.trade(tradingOrders);
			} catch (ParseException e) {
				e.printStackTrace();
				log.info("异常：trader.trade(tradingOrders);");
				return MessageResult.error(500, "ENGINE_CREATION_FAILED");
			}
			//判断已完成的订单发送消息通知
			if(completedOrders.size() > 0){
				rocketMQTemplate.convertAndSend("exchange-order-completed", JSON.toJSONString(completedOrders));
			}
			newTrader.setReady(true);
			factory.addTrader(symbol, newTrader);

			return MessageResult.success("CURRENCY_PAIR_CREATED_SUCCESSFULLY");
		}else {
			CoinTrader trader= factory.getTrader(symbol);
			if(trader.isTradingHalt()) {
				trader.resumeTrading();
				return MessageResult.success("ENGINE_STATE_HAS_STOPPED");
			}else {
				return MessageResult.error(500, "ENGINE_STATUS_IS_RUNNING");
			}
		}
	}

	/**
	 * 查找订单
	 *
	 * @return
	 */
	@RequestMapping("order4Feign")
	public ExchangeOrder order4Feign(@RequestBody ExchangeOrder order) {
		return this.findOrder(order.getSymbol(), order.getOrderId(), order.getDirection().getCode(),order.getType().getCode());
	}

	/**
	 * 查找订单
	 *
	 * @param symbol
	 * @param orderId
	 * @param direction
	 * @param type
	 * @return
	 */
	private ExchangeOrder findOrder(
			@RequestParam("symbol") String symbol,
			@RequestParam("orderId") String orderId,
			@RequestParam("direction")Integer direction,
			@RequestParam("type") Integer type) {
		CoinTrader trader = factory.getTrader(symbol);
		return trader.findOrder(orderId, type, direction);
	}
}
