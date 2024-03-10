package com.bitisan.agent.controller;

import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 后台货币web
 * @date 2019/12/29 15:01
 */
@RestController
@RequestMapping("coin")
@Slf4j
public class CoinController extends BaseController {
    @Autowired
    private CoinFeign coinFeign;

    @Autowired
    private LocaleMessageSourceService messageSource;

    @PostMapping("all-name")
    @AccessLog(module = AdminModule.SYSTEM, operation = "查找所有coin的name")
    public MessageResult getAllCoinName() {
        List<String> list = coinFeign.getAllCoinName();
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), list);
    }

    @PostMapping("all-name-and-unit")
    @AccessLog(module = AdminModule.SYSTEM, operation = "查找所有coin的name和unit")
    public MessageResult getAllCoinNameAndUnit() {
        List<Coin> list = coinFeign.getAllCoinNameAndUnit();
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), list);
    }
}
