package com.bitisan.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.user.entity.Automainconfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "autoMainConfigFeign")
public interface AutoMainConfigFeign {

    @GetMapping(value = "/autoMainConfigFeign/findAll")
    Page<Automainconfig> findAll(@RequestParam("pageNo")Integer pageNo, @RequestParam("pageSize")Integer pageSize);

    @GetMapping(value = "/autoMainConfigFeign/findAutoMainConfigByCoinNameAndProtocol")
    Automainconfig findAutoMainConfigByCoinNameAndProtocol(@RequestParam("coinName")String coinName, @RequestParam("protocol")Integer protocol);

    @PostMapping(value = "/autoMainConfigFeign/save")
    Automainconfig save(@RequestBody Automainconfig automainconfig);
}
