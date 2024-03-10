package com.bitisan.user.feign;

import com.bitisan.user.entity.Country;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-user",contextId = "countryFeign")
public interface CountryFeign {


    @GetMapping("/countryFeign/findByZhName")
    Country findByZhName(@RequestParam("zhName")String zhName);


}
