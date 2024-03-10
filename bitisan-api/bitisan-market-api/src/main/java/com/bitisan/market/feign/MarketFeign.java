package com.bitisan.market.feign;

import com.bitisan.pojo.CoinThumb;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-market",contextId = "marketFeign")
public interface MarketFeign {

    @RequestMapping("/marketFeign/engines")
    Map<String, Integer> engines();

    @RequestMapping("/marketFeign/symbolThumb4Feign")
    List<CoinThumb> findSymbolThumb4Feign();

}
