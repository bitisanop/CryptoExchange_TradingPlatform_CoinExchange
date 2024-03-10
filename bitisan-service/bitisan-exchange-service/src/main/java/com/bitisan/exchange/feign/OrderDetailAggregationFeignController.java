package com.bitisan.exchange.feign;

import com.bitisan.controller.BaseController;
import com.bitisan.exchange.entity.OrderDetailAggregation;
import com.bitisan.exchange.service.OrderDetailAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1816:54
            */
@RestController
@RequestMapping("orderDetailAggregationFeign")
public class OrderDetailAggregationFeignController extends BaseController {
    @Autowired
    private OrderDetailAggregationService orderDetailAggregationService;

    @RequestMapping("/save")
    public void save(@RequestBody OrderDetailAggregation aggregation){
        orderDetailAggregationService.save(aggregation);
    }


}
