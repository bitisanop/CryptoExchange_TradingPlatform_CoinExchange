package com.bitisan.exchange.feign;

import com.bitisan.controller.BaseController;
import com.bitisan.exchange.service.ExchangeTradeService;
import com.bitisan.pojo.ExchangeTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1816:54
            */
@RestController
@RequestMapping("exchangeTradeFeign")
public class ExchangeTradeController extends BaseController {

    @Autowired
    private ExchangeTradeService exchangeTradeService;

    @GetMapping("/findLatest")
    public List<ExchangeTrade> findLatest(@RequestParam("symbol") String symbol, @RequestParam("size")int size){
        return exchangeTradeService.findLatest(symbol,size);
    }

}
