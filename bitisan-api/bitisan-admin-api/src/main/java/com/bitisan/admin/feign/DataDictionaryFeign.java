package com.bitisan.admin.feign;

import com.bitisan.admin.entity.DataDictionary;
import com.bitisan.screen.ExchangeCoinScreen;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-admin",contextId = "dataDictionaryFeign")
public interface DataDictionaryFeign {

    @PostMapping("/dataDictionaryFeign/findByBond")
    DataDictionary findByBond(@RequestParam("bond")String bond);
}
