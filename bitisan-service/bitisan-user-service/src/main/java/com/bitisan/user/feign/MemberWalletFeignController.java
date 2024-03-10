package com.bitisan.user.feign;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.MemberWalletScreen;
import com.bitisan.user.dto.MemberWalletDTO;
import com.bitisan.user.entity.MemberDeposit;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.service.MemberDepositService;
import com.bitisan.user.service.MemberWalletService;
import com.bitisan.util.MessageResult;
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
@RequestMapping("/memberWalletFeign")
public class MemberWalletFeignController extends BaseController {

    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private MemberDepositService memberDepositService;

//    @ApiOperation(value = "根据币种和会员Id查询会员钱包")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "coinUnit", value = "币种"),
//            @ApiImplicitParam(name = "memberId", value = "会员id"),
//    })
    @GetMapping("findByCoinUnitAndMemberId")
    public MemberWallet findByCoinUnitAndMemberId(@RequestParam("coinUnit") String coinUnit,@RequestParam("memberId") Long memberId) {
        MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(coinUnit,memberId);
        return memberWallet;
    }

//    @ApiOperation(value = "冻结余额")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "id"),
//            @ApiImplicitParam(name = "amount", value = "账户"),
//    })
    @PostMapping("freezeBalance")
    public MessageResult freezeBalance(@RequestParam("id") Long id, @RequestParam("amount") BigDecimal amount) {
        int ret = memberWalletService.freezeBalance(id, amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

//    @ApiOperation(value = "扣除余额")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "walletId", value = "钱包id"),
//            @ApiImplicitParam(name = "amount", value = "账户"),
//    })
    @PostMapping("deductBalance")
    public MessageResult deductBalance(@RequestParam("walletId") Long walletId, @RequestParam("amount") BigDecimal amount) {
        int ret = memberWalletService.decreaseBalance(walletId, amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

//    @ApiOperation(value = "增加余额")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "walletId", value = "钱包id"),
//            @ApiImplicitParam(name = "amount", value = "账户"),
//    })
    @PostMapping("increaseBalance")
    public MessageResult increaseBalance(@RequestParam("walletId") Long walletId, @RequestParam("amount") BigDecimal amount){
        int ret = memberWalletService.increaseBalance(walletId, amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

    @PostMapping("increaseToRelease")
    MessageResult increaseToRelease(@RequestParam("walletId") Long walletId, @RequestParam("amount") BigDecimal amount){
        int ret = memberWalletService.increaseToRelease(walletId, amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

//    @ApiOperation(value = "减少冻结")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "walletId", value = "钱包id"),
//            @ApiImplicitParam(name = "amount", value = "账户"),
//    })
    @PostMapping("decreaseFrozen")
    public MessageResult decreaseFrozen(@RequestParam("walletId") Long walletId, @RequestParam("amount") BigDecimal amount){
        int ret = memberWalletService.decreaseFrozen(walletId, amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

//    @ApiOperation(value = "解冻余额")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "coinId", value = "币种id"),
//            @ApiImplicitParam(name = "memberId", value = "会员id"),
//            @ApiImplicitParam(name = "amount", value = "账户"),
//    })
    @PostMapping("thawBalance")
    public MessageResult thawBalance(@RequestParam("coinId")String coinId, @RequestParam("memberId") Long memberId, @RequestParam("amount") BigDecimal amount){
        MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(coinId,memberId);
        if(memberWallet==null){
            return MessageResult.error("Information Expired");
        }
        int ret = memberWalletService.thawBalance(memberWallet.getId(), amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

//    @ApiOperation(value = "根据会员id获取列表")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "memberId", value = "会员id"),
//    })
    @GetMapping("findAllByMemberId")
    public List<MemberWallet>  findAllByMemberId(@RequestParam("memberId") Long memberId) {
        List<MemberWallet> list = memberWalletService.findAllByMemberId(memberId);
        return list;
    }

//    @ApiOperation(value = "保存")
    @PostMapping(value = "/save")
    public Boolean save(@RequestBody MemberWallet wallet){
        return memberWalletService.saveOrUpdate(wallet);
    }

//    @ApiOperation(value = "获取可用余额")
    @PostMapping(value = "/getBalance")
    public Page<MemberWalletDTO> getBalance(@RequestBody MemberWalletScreen screen){
        return memberWalletService.getBalance(screen);
    }

    @PostMapping("/lockWallet")
    public boolean lockWallet(@RequestParam("uid")Long uid, @RequestParam("unit")String unit){
        return memberWalletService.lockWallet(uid,unit);
    }

    @PostMapping("/unlockWallet")
    boolean unlockWallet(@RequestParam("uid")Long uid, @RequestParam("unit")String unit){
        return memberWalletService.unlockWallet(uid,unit);
    }

    @PostMapping("/findDeposit")
    MemberDeposit findDeposit(@RequestParam("address")String address,@RequestParam("txid") String txid){
        return memberDepositService.findDeposit(address,txid);
    }

    @PostMapping("/recharge")
    MessageResult recharge(@RequestParam("unit")String unit,
                           @RequestParam("address")String address,
                           @RequestParam("amount")BigDecimal amount,
                           @RequestParam("txid")String txid,
                           @RequestParam("fromAddress")String fromAddress,
                           @RequestParam("blockHeight")Long blockHeight){

       return memberWalletService.recharge(unit,address,amount,txid,fromAddress,blockHeight);
    }
}

