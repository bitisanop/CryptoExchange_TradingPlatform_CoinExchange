package com.bitisan.market.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-market",contextId = "exchange-rate")
public interface ExchangeRateFeign {

    @RequestMapping("/exchangeRateFeign/cny4Feign/{coin}")
    BigDecimal getCnyExchangeRate4Feign(@PathVariable(value="coin") String coin);

    @RequestMapping("/exchangeRateFeign/cny4Feign/usd-{unit}")
    BigDecimal getUsdCnyRate4Feign(@PathVariable(value="unit") String unit);

    @RequestMapping("/exchangeRateFeign/all/{coin}")
    HashMap<String,BigDecimal> getAllExchangeRate(@PathVariable(value="coin") String coin);
}
