package com.bitisan.user.feign;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.ContractFinanceScreen;
import com.bitisan.user.entity.MemberSecondWallet;
import com.bitisan.user.service.MemberSecondWalletService;
import com.bitisan.user.service.MemberService;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
@RequestMapping("/memberSecondWalletFeign")
public class MemberSecondWalletFeignController extends BaseController {

    @Autowired
    private MemberSecondWalletService memberSecondWalletService;
    @Autowired
    private MemberService memberService;

//    @ApiOperation(value = "根据会员id查询全部")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "memberId", value = "会员id"),
//    })
    @PostMapping("findAllByMemberId")
    public List<MemberSecondWallet> findAllByMemberId(@RequestParam("memberId") Long memberId) {
        List<MemberSecondWallet> memberSecondWallet = memberSecondWalletService.findAllByMemberId(memberId);
        return memberSecondWallet;
    }

//    @ApiOperation(value = "保存")
    @PostMapping("save")
    public MessageResult save(@RequestBody MemberSecondWallet memberSecondWallet){
        boolean ret = memberSecondWalletService.saveOrUpdate(memberSecondWallet);
        if (ret) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

//    @ApiOperation(value = "根据币种和会员Id查询会员钱包")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "coinUnit", value = "币种"),
//            @ApiImplicitParam(name = "memberId", value = "会员id"),
//    })
    @PostMapping("findByCoinUnitAndMemberId")
    public MemberSecondWallet findByCoinUnitAndMemberId(@RequestParam("unit") String unit,@RequestParam("memberId") Long memberId) {
        MemberSecondWallet memberSecondWallet = memberSecondWalletService.findByCoinUnitAndMemberId(unit, memberId);
        return memberSecondWallet;
    }

//    @ApiOperation(value = "增加余额")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "walletId", value = "钱包id"),
//            @ApiImplicitParam(name = "amount", value = "数量"),
//    })
    @PostMapping("increaseBalance")
    public MessageResult increaseBalance(@RequestParam("walletId") Long walletId, @RequestParam("amount") BigDecimal amount) {
        int ret  = memberSecondWalletService.increaseBalance(walletId, amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

//    @ApiOperation(value = "扣除余额")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "walletId", value = "钱包id"),
//            @ApiImplicitParam(name = "amount", value = "数量"),
//    })
    @PostMapping("decreaseBalance")
    public MessageResult decreaseBalance(@RequestParam("walletId") Long walletId, @RequestParam("amount") BigDecimal amount) {
        int ret  = memberSecondWalletService.decreaseBalance(walletId, amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

//    @ApiOperation(value = "减少冻结")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "walletId", value = "钱包id"),
//            @ApiImplicitParam(name = "amount", value = "数量"),
//    })
    @PostMapping("decreaseFrozen")
    public MessageResult decreaseFrozen(@RequestParam("walletId") Long walletId, @RequestParam("amount") BigDecimal amount){
        int ret = memberSecondWalletService.decreaseFrozen(walletId, amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

//    @ApiOperation(value = "解冻余额")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "unit", value = "币种"),
//            @ApiImplicitParam(name = "memberId", value = "会员id"),
//            @ApiImplicitParam(name = "amount", value = "数量"),
//    })
    @PostMapping("thawBalance")
    public MessageResult thawBalance(@RequestParam("unit") String unit, @RequestParam("memberId") Long memberId, @RequestParam("amount") BigDecimal amount){
        MemberSecondWallet memberSecondWallet = memberSecondWalletService.findByCoinUnitAndMemberId(unit, memberId);
        if(memberSecondWallet==null){
            return MessageResult.error("Information Expired");
        }
        int ret = memberSecondWalletService.thawBalance(memberSecondWallet.getId(), amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

//    @ApiOperation(value = "冻结余额")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "walletId", value = "钱包id"),
//            @ApiImplicitParam(name = "amount", value = "数量"),
//    })
    @PostMapping("freezeBalance")
    public MessageResult freezeBalance(@RequestParam("walletId") Long walletId, @RequestParam("amount") BigDecimal amount){
        int ret = memberSecondWalletService.freezeBalance(walletId, amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

//    @ApiOperation(value = "获取全部")
    @PostMapping("findAll")
    public Page<MemberSecondWallet> findAll(@RequestBody ContractFinanceScreen screen){
        return memberSecondWalletService.findAll(screen);
    }
}

