package com.bitisan.exchange.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.screen.ExchangeCoinScreen;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-exchange",contextId = "exchangeCoinFeign")
public interface ExchangeCoinFeign {

    @GetMapping(value = "/exchangeCoinFeign/findAllEnabled")
    List<ExchangeCoin> findAllEnabled();

    @GetMapping(value = "/exchangeCoinFeign/findAllVisible")
    List<ExchangeCoin> findAllVisible();

    @GetMapping(value = "/exchangeCoinFeign/findAllByFlag")
    List<ExchangeCoin> findAllByFlag(@RequestParam("flag") int flag);

    @GetMapping(value = "/exchangeCoinFeign/findBySymbol")
    ExchangeCoin findBySymbol(@RequestParam("symbol")String symbol);

    @PostMapping(value = "/exchangeCoinFeign/save")
    ExchangeCoin save(@RequestBody ExchangeCoin exchangeCoin);

    @PostMapping(value = "/exchangeCoinFeign/findAll")
    Page<ExchangeCoin> findAll(@RequestBody ExchangeCoinScreen screen);

    @PostMapping(value = "/exchangeCoinFeign/getBaseSymbol")
    List<String> getBaseSymbol();

    @PostMapping(value = "/exchangeCoinFeign/getCoinSymbol")
    List<String> getCoinSymbol(@RequestParam("baseSymbol")String baseSymbol);

    @PostMapping(value = "/exchangeCoinFeign/deletes")
    void deletes(@RequestParam(value = "ids")String[] ids);

    @PostMapping(value = "/exchangeCoinFeign/findAllByRobotType")
    List<ExchangeCoin> findAllByRobotType(@RequestParam("type")Integer type);

    @PostMapping(value = "/exchangeCoinFeign/getAllCoin")
    List<String> getAllCoin();
}
