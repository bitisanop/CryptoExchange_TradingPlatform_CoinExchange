package com.bitisan.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.screen.CoinextScreen;
import com.bitisan.user.entity.Coinext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "coinextFeign")
public interface CoinextFeign {


    @PostMapping(value = "/coinextFeign/findAll")
    Page<Coinext> findAll(@RequestBody CoinextScreen coinextScreen);

    @GetMapping(value = "/coinextFeign/findFirstByCoinNameAndProtocol")
    Coinext findFirstByCoinNameAndProtocol(@RequestParam("coinName")String coinName, @RequestParam("protocol")Integer protocol);

    @PostMapping(value = "/coinextFeign/save")
    Coinext save(@RequestBody Coinext coinext);
}
