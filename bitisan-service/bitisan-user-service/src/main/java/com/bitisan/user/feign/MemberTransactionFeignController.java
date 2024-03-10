package com.bitisan.user.feign;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.MemberTransactionScreen;
import com.bitisan.user.entity.MemberTransaction;
import com.bitisan.user.service.MemberTransactionService;
import com.bitisan.user.vo.MemberTransactionVO;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>
 * 会员用户 前端控制器
 * </p>
 *
 * @author markchao
 * @since 2021-06-14
 */
@RestController
@RequestMapping("/memberTransactionFeign")
public class MemberTransactionFeignController extends BaseController {

    @Autowired
    private MemberTransactionService memberTransactionService;

//    @ApiOperation(value = "保存")
    @PostMapping("save")
    public MessageResult save(@RequestBody MemberTransaction memberTransaction){
        if(memberTransaction.getCreateTime()==null){
            memberTransaction.setCreateTime(new Date());
        }
        boolean ret = memberTransactionService.saveOrUpdate(memberTransaction);
        if (ret) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

//    @ApiOperation(value = "根据条件查询")
    @PostMapping("joinFind")
    public Page<MemberTransactionVO> joinFind(@RequestBody MemberTransactionScreen screen){
       return memberTransactionService.joinFind(screen);
    }

//    @ApiOperation(value = "根据id查询")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "id"),
//    })
    @PostMapping("findOne")
    public MemberTransaction findOne(@RequestBody Long id){
       return memberTransactionService.findOne(id);
    }

    @PostMapping("deleteHistory")
    public int deleteHistory(@RequestParam("startTime") Date startTime){
        return memberTransactionService.deleteHistory(startTime);
    }

    @PostMapping("updateRewardRobot")
    void updateRewardRobot(){
        memberTransactionService.updateRewardRobot();
    }

    @PostMapping("sendExchangeReward")
    void sendExchangeReward(){
        memberTransactionService.sendExchangeReward();
    }

    @PostMapping("sendSecondReward")
    void sendSecondReward() {
        memberTransactionService.sendSecondReward();
    }

    @PostMapping("sendOptionReward")
    void sendOptionReward() {
        memberTransactionService.sendOptionReward();
    }

}

