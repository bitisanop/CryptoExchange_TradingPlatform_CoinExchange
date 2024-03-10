package com.bitisan.exchange.feign;

import com.bitisan.exchange.entity.OrderDetailAggregation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = "bitisan-exchange",contextId = "orderDetailAggregationFeign")
public interface OrderDetailAggregationFeign {

    @RequestMapping("/orderDetailAggregationFeign/save")
    void save(@RequestBody OrderDetailAggregation aggregation);
}
