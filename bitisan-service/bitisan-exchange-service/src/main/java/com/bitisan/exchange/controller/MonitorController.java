package com.bitisan.exchange.controller;

import com.alibaba.fastjson.JSONObject;
import com.bitisan.exchange.Trader.CoinTrader;
import com.bitisan.exchange.Trader.CoinTraderFactory;
import com.bitisan.exchange.entity.ExchangeOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/monitor")
public class MonitorController {
	private Logger log = LoggerFactory.getLogger(MonitorController.class);
	@Autowired
	private CoinTraderFactory factory;

    @RequestMapping("trader-detail")
    public JSONObject  traderDetail(@RequestParam("symbol") String symbol){
        CoinTrader trader = factory.getTrader(symbol);
        if(trader == null ) {
            return null;
        }
        JSONObject result = new JSONObject();
        //卖盘信息
        JSONObject ask = new JSONObject();
        //买盘信息
        JSONObject bid = new JSONObject();
        ask.put("limit_price_queue",trader.getSellLimitPriceQueue());
        ask.put("market_price_queue",trader.getSellMarketQueue());
        bid.put("limit_price_queue",trader.getBuyLimitPriceQueue());
        bid.put("market_price_queue",trader.getBuyMarketQueue());
        result.put("ask",ask);
        result.put("bid",bid);
        return result;
    }

	@RequestMapping("symbols")
	public List<String> symbols() {
		Map<String, CoinTrader> traders = factory.getTraderMap();
		List<String> symbols = new ArrayList<>();
		traders.forEach((key, trader) -> {
			symbols.add(key);
		});
		return symbols;
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
	@RequestMapping("order")
	public ExchangeOrder findOrder(
			@RequestParam("symbol") String symbol,
			@RequestParam("orderId") String orderId,
			@RequestParam("direction")Integer direction,
			@RequestParam("type") Integer type) {
		CoinTrader trader = factory.getTrader(symbol);
		return trader.findOrder(orderId, type, direction);
	}

}
