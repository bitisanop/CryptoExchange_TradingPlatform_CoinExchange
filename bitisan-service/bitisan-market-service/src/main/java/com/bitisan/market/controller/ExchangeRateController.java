package com.bitisan.market.controller;

import com.bitisan.market.component.CoinExchangeRate;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Api(tags = "币种汇率")
@RestController
@RequestMapping("/exchange-rate")
public class ExchangeRateController {
    @Autowired
    private CoinExchangeRate coinExchangeRate;

    @ApiOperation(value = "获取usd币种汇率")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coin", value = "币种")
    })
    @RequestMapping("usd/{coin}")
    public MessageResult getUsdExchangeRate(@PathVariable String coin){
        MessageResult mr = new MessageResult(0,"success");
        BigDecimal latestPrice = coinExchangeRate.getUsdRate(coin);
        mr.setData(latestPrice.toString());
        return mr;
    }

    @ApiOperation(value = "获取usdtcny币种汇率")
    @RequestMapping("usdtcny")
    public MessageResult getUsdtExchangeRate(){
        MessageResult mr = new MessageResult(0,"success");
        BigDecimal latestPrice = coinExchangeRate.getUsdtCnyRate();
        mr.setData(latestPrice.toString());
        return mr;
    }

    @ApiOperation(value = "获取cny币种汇率")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coin", value = "币种")
    })
    @RequestMapping("cny/{coin}")
    public MessageResult getCnyExchangeRate(@PathVariable String coin){
        MessageResult mr = new MessageResult(0,"success");
        BigDecimal latestPrice = coinExchangeRate.getCnyRate(coin);
        mr.setData(latestPrice.toString());
        return mr;
    }

    @ApiOperation(value = "获取jpy币种汇率")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coin", value = "币种")
    })
    @RequestMapping("jpy/{coin}")
    public MessageResult getJpyExchangeRate(@PathVariable String coin){
        MessageResult mr = new MessageResult(0,"success");
        BigDecimal latestPrice = coinExchangeRate.getJpyRate(coin);
        mr.setData(latestPrice.toString());
        return mr;
    }

    @ApiOperation(value = "获取hkd币种汇率")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coin", value = "币种")
    })
    @RequestMapping("hkd/{coin}")
    public MessageResult getHkdExchangeRate(@PathVariable String coin){
        MessageResult mr = new MessageResult(0,"success");
        BigDecimal latestPrice = coinExchangeRate.getHkdRate(coin);
        mr.setData(latestPrice.toString());
        return mr;
    }

    @ApiOperation(value = "获取usd币种汇率")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "unit", value = "币种")
    })
    @RequestMapping("usd-{unit}")
    public MessageResult getUsdCnyRate(@PathVariable String unit){
        MessageResult mr = new MessageResult(0,"success");
        if("CNY".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdtCnyRate());
        }
        else if("JPY".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdJpyRate());
        }
        else if("HKD".equalsIgnoreCase(unit)) {
            mr.setData(coinExchangeRate.getUsdHkdRate());
        }
        else {
            mr.setData(BigDecimal.ZERO);
        }
        return mr;
    }
}
