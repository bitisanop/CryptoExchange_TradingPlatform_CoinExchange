package com.bitisan.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.BooleanEnum;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.*;
import com.bitisan.user.service.*;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.user.vo.WithdrawWalletInfo;
import com.bitisan.util.GoogleAuthenticatorUtil;
import com.bitisan.util.MD5;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.Assert.notNull;
import static com.bitisan.util.BigDecimalUtils.compare;
import static org.springframework.util.Assert.isTrue;

/**
 * @author Bitisan E-Mali:bitisanop@gmail.com
 * @date 2021年01月26日
 */
@Api(tags = "提币")
@RestController
@Slf4j
@RequestMapping(value = "/withdraw", method = RequestMethod.POST)
public class WithdrawController extends BaseController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private LocaleMessageSourceService sourceService;
    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private CoinextService coinextService;

    /**
     * 支持提现的地址
     *
     * @return
     */
    @ApiOperation(value = "支持提现的地址")
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
     *
     * @param authMember
     * @return
     */
    @ApiOperation(value = "提现币种详细信息")
    @PermissionOperation
    @RequestMapping("support/coin/info")
    public MessageResult queryWithdrawCoin(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        List<Coin> list = coinService.findAllCanWithDraw();
        Map<String,Coin> coinMap = list.stream().collect(Collectors.toMap(Coin::getName, Function.identity()));
        for (Coin coin : list) {
            coinMap.put(coin.getName(),coin);
        }

        List<MemberWallet> list1 = memberWalletService.findAllByMemberId(user.getId());
        long id = user.getId();
        List<WithdrawWalletInfo> list2 = list1.stream().filter(x -> coinMap.containsKey(x.getCoinId())).map(x ->{
            Coin coin = coinMap.get(x.getCoinId());
            WithdrawWalletInfo info = WithdrawWalletInfo.builder()
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
                    .canAutoWithdraw(BooleanEnum.creator(coin.getCanAutoWithdraw()))
                    .addresses(null).build();
            return info;
                }

        ).collect(Collectors.toList());
        MessageResult result = MessageResult.success();
        result.setData(list2);
        return result;
    }


    /**
     * 申请提币(请到PC端提币或升级APP)
     * 没有验证码校验
     *
     * @param
     * @param unit
     * @param address
     * @param amount
     * @param fee
     * @param remark
     * @param jyPassword
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "申请提币")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "unit", value = "币种"),
            @ApiImplicitParam(name = "address", value = "提现地址"),
            @ApiImplicitParam(name = "amount", value = "金额"),
            @ApiImplicitParam(name = "fee", value = "提现手续费"),
            @ApiImplicitParam(name = "remark", value = "用户申请提现备注"),
            @ApiImplicitParam(name = "jyPassword", value = "交易密码"),
    })
    @PermissionOperation
    @RequestMapping("apply")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult withdraw(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, String unit, String address,
                                  BigDecimal amount, BigDecimal fee, String remark, String jyPassword) throws Exception {
        return MessageResult.success(sourceService.getMessage("WITHDRAW_TO_PC"));
    }






    /**
     * 提币记录
     */
    @ApiOperation(value = "保存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coin", value = "币种"),
    })
    @PermissionOperation
    @RequestMapping("list")
    public MessageResult list(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, @RequestParam("page")Integer page, @RequestParam("pageSize")Integer pageSize) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        MessageResult mr = new MessageResult(0, "success");
        Page<Withdraw> records = withdrawService.findAllByMemberId(user.getId(), page, pageSize);
        mr.setData(IPage2Page(records));
        return mr;
    }


    /**
     * 提币
     *
     * @return
     */
    @ApiOperation(value = "保存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coin", value = "币种"),
    })
    @PermissionOperation
    @PostMapping("create")
    public MessageResult create(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember,
                                @RequestParam(value = "coinName") String coinName,
                                @RequestParam(value = "coinprotocol") Integer coinprotocol,
                                @RequestParam(value = "address") String address,
                                @RequestParam(value = "money") double money,
                                @RequestParam(value = "code") String code,
                                @RequestParam(value = "codeType") Integer codeType,
                                @RequestParam(value = "payPwd") String payPwd) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        Long memberid = user.getId();
        // 查询资金密码
        Member member = memberService.getById(user.getId());
        isTrue(MD5.md5(payPwd + member.getSalt()).toLowerCase().equals(member.getJyPassword()), sourceService.getMessage("ERROR_JYPASSWORD"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //验证验证码
        if(codeType==1){
            //邮箱
            String email = member.getEmail();
            Object redisCode =valueOperations.get(SysConstant.EMAIL_WITHDRAW_MONEY_CODE_PREFIX + email);
            notNull(redisCode, sourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
            if (!redisCode.toString().equals(code)) {
                return error(sourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
            } else {
                valueOperations.getOperations().delete(SysConstant.EMAIL_WITHDRAW_MONEY_CODE_PREFIX + email);
            }
        }else if(codeType==2){
            //手机
            String key = SysConstant.PHONE_WITHDRAW_MONEY_CODE_PREFIX + member.getMobilePhone();
            Object redisCode =valueOperations.get(key);
            notNull(redisCode, sourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
            if (!redisCode.toString().equals(code)) {
                return error(sourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
            } else {
                valueOperations.getOperations().delete(key);
            }
        }else{
            //Google
            long t = System.currentTimeMillis();
            GoogleAuthenticatorUtil ga = new GoogleAuthenticatorUtil();
            //  ga.setWindowSize(0); // should give 5 * 30 seconds of grace...
            boolean r = ga.check_code(member.getGoogleKey(), Long.valueOf(code), t);
            if(!r){
                return error(sourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
            }
        }

        // 查询币种配置
        Coinext firstByCoinnameAndProtocol = coinextService.findFirstByCoinNameAndProtocol(coinName, coinprotocol);

        if (firstByCoinnameAndProtocol == null) {
            return MessageResult.error(sourceService.getMessage("COIN_ILLEGAL"));
        }

        Integer iswithdraw = firstByCoinnameAndProtocol.getIsWithdraw().getCode();
        if (iswithdraw != 1) {
            return MessageResult.error(sourceService.getMessage("COIN_NOT_SUPPORT"));
        }
        Integer decimals = firstByCoinnameAndProtocol.getDecimals();
        Integer isautowithdraw = firstByCoinnameAndProtocol.getIsAutoWithdraw().getCode();

        // 保留两位小数
        BigDecimal bigDecimalMoney = new BigDecimal(money).setScale(decimals, BigDecimal.ROUND_DOWN);

        BigDecimal bigDecimalMinwithdraw = firstByCoinnameAndProtocol.getMinWithdraw();
        BigDecimal bigDecimalMaxwithdraw = firstByCoinnameAndProtocol.getMaxWithdraw();
        BigDecimal bigDecimalWithdrawfee = firstByCoinnameAndProtocol.getWithdrawFee();
        BigDecimal bigDecimalMinwithdrawfee = firstByCoinnameAndProtocol.getMinWithdrawFee();

        // 如果提现金额为0或者负数
        if (bigDecimalMoney.compareTo(new BigDecimal(0)) <= 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MIN"));
        }

        // 如果提现金额小于最低提现数量
        if (bigDecimalMoney.compareTo(bigDecimalMinwithdraw) < 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MIN"));
        }

        // 如果提现金额大于最大金额
        if (bigDecimalMoney.compareTo(bigDecimalMaxwithdraw) > 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MAX"));
        }

        BigDecimal fee = bigDecimalMoney.multiply(bigDecimalWithdrawfee).setScale(decimals, BigDecimal.ROUND_DOWN);
        // 如果手续费小于最低手续费则使用最低手续费
        if (fee.compareTo(bigDecimalMinwithdrawfee) < 0) {
            fee = bigDecimalMinwithdrawfee;
        }

        BigDecimal realMoney = bigDecimalMoney.subtract(fee).setScale(decimals, BigDecimal.ROUND_DOWN);

        // 如果实际到账为0或者负数
        if (realMoney.compareTo(new BigDecimal(0)) <= 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MIN"));
        }

        MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(coinName, memberid);
        isTrue(compare(memberWallet.getBalance(), bigDecimalMoney), sourceService.getMessage("INSUFFICIENT_BALANCE"));

        isTrue(memberWallet.getIsLock() == BooleanEnum.IS_FALSE, "钱包已锁定");


        Withdraw withdraw = new Withdraw();
        withdraw.setMemberId(memberid.intValue());
        withdraw.setAddTime(new Date().getTime());
        withdraw.setCoinId(0);
        withdraw.setCoinName(coinName);
        withdraw.setAddress(address);
        withdraw.setMoney(bigDecimalMoney);
        withdraw.setFee(fee);
        withdraw.setRealMoney(realMoney);
        withdraw.setProcessMold(0);
        withdraw.setHash("");
        withdraw.setStatus(isautowithdraw == 1 ? 1 : 0);
        withdraw.setProcessTime(0L);
        withdraw.setWithdrawInfo("");
        withdraw.setRemark("");
        withdraw.setProtocol(coinprotocol);
        withdraw.setProtocolName(firstByCoinnameAndProtocol.getProtocolName());

        withdrawService.save(withdraw);

        return MessageResult.success();
    }







}
