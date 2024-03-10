package com.bitisan.user.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.BooleanEnum;
import com.bitisan.constant.CommonStatus;
import com.bitisan.constant.SysConstant;
import com.bitisan.constant.WithdrawStatus;
import com.bitisan.controller.BaseController;
import com.bitisan.exception.InformationExpiredException;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.entity.WithdrawCodeRecord;
import com.bitisan.user.service.*;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.user.vo.WithdrawWalletInfo;
import com.bitisan.util.MD5;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bitisan.util.BigDecimalUtils.compare;
import static org.springframework.util.Assert.*;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @date 2020年01月26日
 */
@Api(tags = "提币码")
@Slf4j
@RestController
@RequestMapping(value = "/withdrawcode", method = RequestMethod.POST)
public class WithdrawCodeController extends BaseController {
//    @Autowired
//    private MemberAddressService memberAddressService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private WithdrawCodeRecordService withdrawApplyService;
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private LocaleMessageSourceService sourceService;
    @Autowired
    private MemberTransactionService memberTransactionService ;

    /**
     * 支持提现的地址
     * @return
     */
    @RequestMapping("support/coin")
    public MessageResult queryWithdraw() {
        List<Coin> list = coinService.findAllCanWithDraw();
        List<String> list1 = new ArrayList<>();
        list.stream().forEach(x -> list1.add(x.getUnit()));
        MessageResult result = MessageResult.success();
        result.setData(list1);
        return result;
    }

    /**
     * 提现币种详细信息
     * @return
     */
    @ApiOperation(value = "提现币种详细信息")
    @PermissionOperation
    @RequestMapping("support/coin/info")
    public MessageResult queryWithdrawCoin(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        List<Coin> list = coinService.findAllCanWithDraw();

        List<MemberWallet> list1 = memberWalletService.findAllByMemberId(user.getId());
        long id = user.getId();
        List<WithdrawWalletInfo> list2 = list1.stream().filter(x -> {
            for (Coin coin : list) {
                if (coin.getUnit().equals(x.getCoinId())) {
                    return true;
                }
            }
            return false;

        }).map(x -> {
            Coin coin = coinService.findByUnit(x.getCoinId());
            WithdrawWalletInfo walletInfo = WithdrawWalletInfo.builder()
                            .balance(x.getBalance())
                            .withdrawScale(coin.getWithdrawScale())
                            .maxTxFee(coin.getMaxTxFee())
                            .minTxFee(coin.getMinTxFee())
                            .minAmount(coin.getMinWithdrawAmount())
                            .maxAmount(coin.getMaxWithdrawAmount())
                            .name(coin.getName())
                            .nameCn(coin.getNameCn())
                            .threshold(coin.getWithdrawThreshold())
                            .unit(coin.getUnit())
//                            .accountType(coin.getAccountType())
                            .canAutoWithdraw(BooleanEnum.creator(coin.getCanAutoWithdraw()))
//                            .addresses(memberAddressService.queryAddress(id, x.getCoin().getName()))
                    .build();
            return walletInfo;
                }

        ).collect(Collectors.toList());
        MessageResult result = MessageResult.success();
        result.setData(list2);
        return result;
    }

    /**
     * 申请提币（添加验证码校验）
     * @param unit 币种单位
     * @param amount 提币数量
     * @param jyPassword  交易密码
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "申请提币（添加验证码校验）")
    @PermissionOperation
    @RequestMapping("apply/code")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult withdrawCode(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, String unit,
                                  BigDecimal amount, String jyPassword) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
    	hasText(jyPassword, sourceService.getMessage("MISSING_JYPASSWORD"));
        hasText(unit, sourceService.getMessage("MISSING_COIN_TYPE"));
        Coin coin = coinService.findByUnit(unit);
        amount.setScale(coin.getWithdrawScale(),BigDecimal.ROUND_DOWN);
        notNull(coin, sourceService.getMessage("COIN_ILLEGAL"));

        isTrue(coin.getStatus().equals(CommonStatus.NORMAL) && coin.getCanWithdraw().equals(BooleanEnum.IS_TRUE), sourceService.getMessage("COIN_NOT_SUPPORT"));
        isTrue(compare(coin.getMaxWithdrawAmount(), amount), sourceService.getMessage("WITHDRAW_MAX") + coin.getMaxWithdrawAmount());
        isTrue(compare(amount, coin.getMinWithdrawAmount()), sourceService.getMessage("WITHDRAW_MIN") + coin.getMinWithdrawAmount());
        MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(unit, user.getId());
        isTrue(compare(memberWallet.getBalance(), amount), sourceService.getMessage("INSUFFICIENT_BALANCE"));
//        isTrue(memberAddressService.findByMemberIdAndAddress(user.getId(), address).size() > 0, sourceService.getMessage("WRONG_ADDRESS"));
        isTrue(memberWallet.getIsLock()==BooleanEnum.IS_FALSE,"钱包已锁定");
        Member member = memberService.getById(user.getId());
        String mbPassword = member.getJyPassword();
        hasText(mbPassword, sourceService.getMessage("NO_SET_JYPASSWORD"));
        Assert.isTrue(MD5.md5Digest(jyPassword + member.getSalt()).toLowerCase().equals(mbPassword), sourceService.getMessage("ERROR_JYPASSWORD"));

        // 冻结用户资产
        int result = memberWalletService.freezeBalance(memberWallet.getId(), amount);
        if (result == 0) {
            throw new InformationExpiredException("Information Expired");
        }
        // 生成提现码记录
        WithdrawCodeRecord withdrawApply = new WithdrawCodeRecord();
        withdrawApply.setCoinId(coin.getUnit());
        withdrawApply.setMemberId(user.getId());
        withdrawApply.setWithdrawAmount(amount);
        withdrawApply.setStatus(WithdrawStatus.PROCESSING.getCode());
        // 生成提现码(MD5)
        String withdrawCode = MD5.md5Digest(System.currentTimeMillis() + Math.random() + "");
        withdrawApply.setWithdrawCode(withdrawCode);

        if (withdrawApplyService.saveOrUpdate(withdrawApply)) {
        	MessageResult mr = new MessageResult(0, "success");
        	mr.setData(withdrawApply);
            return mr;
        } else {
            throw new InformationExpiredException("Information Expired");
        }
    }

    /**
     * 提币码充值
     * @param withdrawCode 提币码
     * @param withdrawCode
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "提币码充值")
    @PermissionOperation
    @RequestMapping("apply/recharge")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult withdrawCodeRecharge(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, String withdrawCode) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
    	hasText(withdrawCode, sourceService.getMessage("MISSING_JYPASSWORD"));
        WithdrawCodeRecord record = withdrawApplyService.findByWithdrawCode(withdrawCode);

        if(record != null) {
        	if(record.getStatus() != WithdrawStatus.PROCESSING.getCode()) {
        		return MessageResult.error("该充值码已被使用或删除！");
        	}
        	withdrawApplyService.withdrawSuccess(record.getId(), user.getId());
        	return MessageResult.success("充值码充值成功！");
        }else {
        	return MessageResult.error("充值码不存在！");
        }
    }

    /**
     * 获取提币码信息
     * @param withdrawCode 提币码
     * @param withdrawCode
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "获取提币码信息")
    @PermissionOperation
    @RequestMapping("apply/info")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult getWithdrawCodeInfo(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, String withdrawCode) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        hasText(withdrawCode, sourceService.getMessage("MISSING_JYPASSWORD"));
        WithdrawCodeRecord record = withdrawApplyService.findByWithdrawCode(withdrawCode);
        if(record != null) {
            MessageResult ret = new MessageResult(0, "获取成功！");
            ret.setData(record);
            return ret;
        }else {
            return MessageResult.error("充值码不存在！");
        }
    }

    /**
     * 提币码记录
     * @param page 页码
     * @param pageSize 数量
     * @return
     */
    @ApiOperation(value = "提币码记录")
    @PermissionOperation
    @RequestMapping("record")
    public MessageResult pageWithdraw(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember,
                                      @RequestParam("page")Integer page,
                                      @RequestParam("pageSize")Integer pageSize) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        MessageResult mr = new MessageResult(0, "success");
        IPage<WithdrawCodeRecord> records = withdrawApplyService.findAllByMemberId(user.getId(), page, pageSize);
        mr.setData(IPage2Page(records));
        return mr;
    }

}
