package com.bitisan.exchange.feign;

import com.bitisan.pojo.ExchangeTrade;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-exchange",contextId = "exchangeTradeFeign")
public interface ExchangeTradeFeign {

    @GetMapping(value = "/exchangeTradeFeign/findLatest")
    List<ExchangeTrade> findLatest(@RequestParam("symbol")String symbol, @RequestParam("size")int size);
}
