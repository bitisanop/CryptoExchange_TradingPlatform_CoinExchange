package com.bitisan.user.feign;


import com.alibaba.fastjson.JSON;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.user.entity.Currency;
import com.bitisan.user.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * <p>
 * 会员用户 前端控制器
 * </p>
 *
 * @author markchao
 * @since 2021-06-14
 */
@RestController
@RequestMapping("/currencyFeign")
public class CurrencyFeignController extends BaseController {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @GetMapping("findAllCurrency")
    public List<Currency> findAllCurrency() {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        String listJson = opsForValue.get(SysConstant.CURRENCY);
        if(!StringUtils.isEmpty(listJson)){
            return JSON.parseArray(listJson,Currency.class);
        }else {
            return currencyService.list();
        }
    }


}

