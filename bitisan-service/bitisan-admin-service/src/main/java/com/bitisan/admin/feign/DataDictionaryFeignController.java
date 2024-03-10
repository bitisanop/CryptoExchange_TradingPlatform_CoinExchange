package com.bitisan.admin.feign;

import com.bitisan.admin.entity.DataDictionary;
import com.bitisan.admin.service.DataDictionaryService;
import com.bitisan.controller.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 委托订单处理类
 */
@Api(tags = "委托订单处理类")
@Slf4j
@RestController
@RequestMapping("/dataDictionaryFeign")
public class DataDictionaryFeignController extends BaseController {

    @Autowired
    private DataDictionaryService dataDictionaryService;


    @PostMapping("findByBond")
    public DataDictionary findByBond(@RequestParam("bond")String bond){
       return dataDictionaryService.findByBond(bond);
    }

}
