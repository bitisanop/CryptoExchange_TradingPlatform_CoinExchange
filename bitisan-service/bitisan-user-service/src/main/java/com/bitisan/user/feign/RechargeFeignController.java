package com.bitisan.user.feign;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.RechargeScreen;
import com.bitisan.user.entity.Recharge;
import com.bitisan.user.service.RechargeService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 会员用户 前端控制器
 * </p>
 *
 * @author markchao
 * @since 2021-06-14
 */
@RestController
@RequestMapping("/rechargeFeign")
public class RechargeFeignController extends BaseController {

    @Autowired
    private RechargeService rechargeService;

//    @ApiOperation(value = "充值记录分页")
    @PostMapping("findAllOut")
    public List<Recharge> findAllOut(@RequestBody RechargeScreen screen){
        return rechargeService.findAllOut(screen);
    }

//    @ApiOperation(value = "充值记录全部")
    @PostMapping("findAll")
    public Page<Recharge> findAll(@RequestBody RechargeScreen rechargeScreen){
        return rechargeService.findAll(rechargeScreen);
    }



}

