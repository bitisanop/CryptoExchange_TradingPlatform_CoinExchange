package com.bitisan.market.feign;

import com.bitisan.market.component.CoinExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/exchangeRateFeign")
public class ExchangeRateFeignController {
    @Autowired
    private CoinExchangeRate coinExchangeRate;

    @RequestMapping("cny4Feign/{coin}")
    public BigDecimal getCnyExchangeRate4Feign(@PathVariable(value="coin") String coin){
        BigDecimal latestPrice = coinExchangeRate.getCnyRate(coin);
        return latestPrice;
    }

    @RequestMapping("cny4Feign/usd-{unit}")
    public BigDecimal getUsdCnyRate4Feign(@PathVariable(value="unit") String unit){
        BigDecimal rate = null;
        if("CNY".equalsIgnoreCase(unit)) {
            rate = coinExchangeRate.getUsdtCnyRate();
        }
        else if("JPY".equalsIgnoreCase(unit)) {
            rate = coinExchangeRate.getUsdJpyRate();
        }
        else if("HKD".equalsIgnoreCase(unit)) {
            rate = coinExchangeRate.getUsdHkdRate();
        }
        else {
            rate = BigDecimal.ZERO;
        }
       return rate;
    }

    @RequestMapping("all/{coin}")
    public HashMap<String,BigDecimal> getAllExchangeRate(@PathVariable String coin){
        HashMap<String,BigDecimal> ratesMap = coinExchangeRate.getAllRate(coin);
        return ratesMap;
    }
}
