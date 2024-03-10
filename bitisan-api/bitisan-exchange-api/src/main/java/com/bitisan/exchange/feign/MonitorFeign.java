package com.bitisan.exchange.feign;

import com.alibaba.fastjson.JSONObject;
import com.bitisan.constant.ExchangeOrderDirection;
import com.bitisan.constant.ExchangeOrderType;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.pojo.TradePlateItem;
import com.bitisan.util.MessageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-exchange",contextId = "monitorFeign")
public interface MonitorFeign {

    @GetMapping(value = "/monitorFeign/engines")
    Map<String, Integer> engines();

    @RequestMapping("/monitorFeign/plate")
    Map<String, List<TradePlateItem>> traderPlate(@RequestParam("symbol") String symbol);

    @RequestMapping("/monitorFeign/plate-mini")
    Map<String, JSONObject> traderPlateMini(@RequestParam("symbol")String symbol);

    @RequestMapping("/monitorFeign/plate-full")
    Map<String, JSONObject> traderPlateFull(@RequestParam("symbol")String symbol);

    @GetMapping(value = "/monitorFeign/reset-trader")
    MessageResult resetTrader(@RequestParam("symbol")String symbol);

    @RequestMapping("/monitorFeign/stop-trader")
    MessageResult stopTrader(@RequestParam("symbol")String symbol);

    @RequestMapping("/monitorFeign/overview")
    JSONObject traderOverview(@RequestParam("symbol") String symbol);

    @RequestMapping("/monitorFeign/start-trader")
    MessageResult startTrader(@RequestParam("symbol")String symbol);

    @RequestMapping("/monitorFeign/order4Feign")
    ExchangeOrder findOrder(@RequestBody ExchangeOrder order);

}
