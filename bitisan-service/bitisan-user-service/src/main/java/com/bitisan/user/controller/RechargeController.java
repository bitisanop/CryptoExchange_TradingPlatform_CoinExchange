package com.bitisan.user.controller;


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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@Api(tags = "充值记录")
@RestController
@RequestMapping("/recharge")
public class RechargeController extends BaseController {

    @Autowired
    private RechargeService rechargeService;


    /**
     * 充值记录
     */
    @ApiOperation(value = "充值记录列表")
    @PermissionOperation
    @PostMapping("list")
    public MessageResult list(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, @RequestParam("page")Integer page, @RequestParam("pageSize")Integer pageSize) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        MessageResult mr = new MessageResult(0, "success");

        Page<Recharge> records = rechargeService.findAllByMemberId(user.getId(), page, pageSize);
        mr.setData(IPage2Page(records));
        return mr;
    }

}

