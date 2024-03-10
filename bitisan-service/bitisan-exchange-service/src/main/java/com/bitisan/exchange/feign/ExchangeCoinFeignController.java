package com.bitisan.exchange.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.exchange.service.ExchangeCoinService;
import com.bitisan.screen.ExchangeCoinScreen;
import com.bitisan.util.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1816:54
            */
@RestController
@RequestMapping("exchangeCoinFeign")
public class ExchangeCoinFeignController extends BaseController {
    @Autowired
    private ExchangeCoinService exchangeCoinService;

    @GetMapping("/findAllEnabled")
    public List<ExchangeCoin> findAllEnabled(){
        List<ExchangeCoin> list = exchangeCoinService.findAllEnabled();
        return list;
    }

    @GetMapping("/findAllVisible")
    public List<ExchangeCoin> findAllVisible(){
        List<ExchangeCoin> list = exchangeCoinService.findAllVisible();
        return list;
    }

    @GetMapping("/findAllByFlag")
    public List<ExchangeCoin> findAllByFlag(@RequestParam("flag")int flag){
        List<ExchangeCoin> list = exchangeCoinService.findAllByFlag(flag);
        return list;
    }

    @GetMapping("/findBySymbol")
    public ExchangeCoin findBySymbol(@RequestParam("symbol")String symbol){
        ExchangeCoin coin = exchangeCoinService.findBySymbol(symbol);
        return coin;
    }

    @PostMapping(value = "/save")
    public ExchangeCoin save(@RequestBody ExchangeCoin exchangeCoin){
        exchangeCoinService.saveOrUpdate(exchangeCoin);
        return exchangeCoin;
    }

    @PostMapping(value = "findAll")
    public Page<ExchangeCoin> findAll(@RequestBody ExchangeCoinScreen screen){
        return exchangeCoinService.findAll(screen);
    }

    @PostMapping(value = "getBaseSymbol")
    public List<String> getBaseSymbol(){
        return exchangeCoinService.getBaseSymbol();
    }

    @PostMapping(value = "getCoinSymbol")
    public List<String> getCoinSymbol(@RequestParam("baseSymbol")String baseSymbol){
        return exchangeCoinService.getCoinSymbol(baseSymbol);
    }

    @PostMapping(value = "deletes")
    public MessageResult deletes(String[] ids){
        exchangeCoinService.deletes(ids);
        return MessageResult.success();
    }
    @PostMapping(value = "findAllByRobotType")
    public List<ExchangeCoin> findAllByRobotType(@RequestParam("type")Integer type){
        return exchangeCoinService.findAllByRobotType(type);
    }

    @PostMapping(value = "getAllCoin")
    public List<String> getAllCoin(){
        return exchangeCoinService.getAllCoin();
    }

}
