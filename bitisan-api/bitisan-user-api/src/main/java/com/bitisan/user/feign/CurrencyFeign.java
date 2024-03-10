package com.bitisan.user.feign;

import com.bitisan.user.entity.Currency;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "currencyFeign")
public interface CurrencyFeign {


    @GetMapping("/currencyFeign/findAllCurrency")
    List<Currency> findAllCurrency();


}
