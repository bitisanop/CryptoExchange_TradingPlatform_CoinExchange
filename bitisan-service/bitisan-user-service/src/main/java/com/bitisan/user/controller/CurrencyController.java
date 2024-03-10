package com.bitisan.user.controller;


import com.bitisan.controller.BaseController;
import com.bitisan.user.service.CurrencyService;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "法币汇率")
@RestController
@RequestMapping("/currency")
public class CurrencyController extends BaseController {

    @Autowired
    private CurrencyService currencyService;

    @ApiOperation(value = "获取全部")
    @GetMapping(value = "/findAll")
    public MessageResult findAll() {
        return success(currencyService.findAll());
    }

}
