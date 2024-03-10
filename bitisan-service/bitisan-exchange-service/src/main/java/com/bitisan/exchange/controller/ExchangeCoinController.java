package com.bitisan.exchange.controller;

import com.bitisan.controller.BaseController;
import com.bitisan.exchange.service.ExchangeCoinService;
import com.bitisan.util.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1816:54
            */
@RestController
@RequestMapping("exchange-coin")
public class ExchangeCoinController extends BaseController {
    @Autowired
    private ExchangeCoinService exchangeCoinService;

    //获取基币
    @RequestMapping("base-symbol")
    public MessageResult baseSymbol() {
        List<String> baseSymbol = exchangeCoinService.getBaseSymbol();
        if (baseSymbol != null && baseSymbol.size() > 0) {
            return success(baseSymbol);
        }
        return error("baseSymbol null");
    }
}
