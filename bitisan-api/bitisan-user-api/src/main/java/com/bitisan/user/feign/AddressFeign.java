package com.bitisan.user.feign;

import com.bitisan.user.entity.Addressext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author Bitisan E-Mali:bitisanop@gmail.com
 * @Description: coin
 * @date 2021/4/214:20
 */
@FeignClient(value = "bitisan-user",contextId = "addressFeign")
public interface AddressFeign{

    @PostMapping(value = "/addressFeign/findByAddress")
    Addressext findByAddress(@RequestParam("address")String address);

    @PostMapping(value = "/addressFeign/save")
    Addressext save(@RequestBody Addressext addressext);
}
