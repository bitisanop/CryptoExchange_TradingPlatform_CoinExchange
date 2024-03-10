package com.bitisan.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.screen.ConvertCoinScreen;
import com.bitisan.screen.ConvertOrderScreen;
import com.bitisan.user.entity.ConvertCoin;
import com.bitisan.user.entity.ConvertOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "convertFeign")
public interface ConvertFeign {

    @GetMapping("/convertFeign/findByCoinUnit")
    ConvertCoin findByCoinUnit(@RequestParam("coinUnit")String coinUnit);

    @PostMapping(value = "/convertFeign/save")
    ConvertCoin save(@RequestBody ConvertCoin convertCoin);

    @PostMapping(value = "/convertFeign/findAll")
    Page<ConvertCoin> findAll(@RequestBody ConvertCoinScreen convertScreen);

    @PostMapping(value = "/convertFeign/findOrderAll")
    Page<ConvertOrder> findOrderAll(@RequestBody ConvertOrderScreen orderScreen);
}
