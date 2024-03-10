package com.bitisan.user.feign;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.user.entity.Automainconfig;
import com.bitisan.user.service.AutomainconfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author markchao
 * @since 2022-03-20
 */
@RestController
@RequestMapping("/autoMainConfigFeign")
public class AutoMainConfigFeignController extends BaseController {


    @Autowired
    private AutomainconfigService automainconfigService;


    @GetMapping(value = "/findAll")
    public Page<Automainconfig> findAll(@RequestParam("pageNo")Integer pageNo, @RequestParam("pageSize")Integer pageSize){
        return automainconfigService.findAll(pageNo,pageSize);
    }

    @GetMapping(value = "/findAutoMainConfigByCoinNameAndProtocol")
    public Automainconfig findAutoMainConfigByCoinNameAndProtocol(@RequestParam("coinName")String coinName, @RequestParam("protocol")Integer protocol){
        return automainconfigService.findAutoMainConfigByCoinNameAndProtocol(coinName,protocol);
    }

    @PostMapping(value = "/save")
    public Automainconfig save(@RequestBody Automainconfig automainconfig){
        automainconfigService.saveOrUpdate(automainconfig);
        return automainconfig;
    }

}

