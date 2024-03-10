package com.bitisan.user.feign;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.PageParam;
import com.bitisan.user.entity.RedEnvelope;
import com.bitisan.user.service.RedEnvelopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 会员用户 前端控制器
 * </p>
 *
 * @author markchao
 * @since 2021-06-14
 */
@RestController
@RequestMapping("/redEnvelopeFeign")
public class RedEnvelopeFeignController extends BaseController {

    @Autowired
    private RedEnvelopeService rechargeService;

    @PostMapping("/findAll")
    public Page<RedEnvelope> findAll(@RequestBody PageParam pageParam){
        LambdaQueryWrapper<RedEnvelope> query = new LambdaQueryWrapper<>();
        Page<RedEnvelope> page = new Page<>(pageParam.getPageNo(),pageParam.getPageSize());
        return rechargeService.page(page);
    }

    @PostMapping("/findOne")
    RedEnvelope findOne(@RequestParam("id") Long id){
        return rechargeService.getById(id);
    }

    @PostMapping("/save")
    RedEnvelope save(@RequestBody RedEnvelope redEnvelope){
        rechargeService.saveOrUpdate(redEnvelope);
        return redEnvelope;
    }


}

