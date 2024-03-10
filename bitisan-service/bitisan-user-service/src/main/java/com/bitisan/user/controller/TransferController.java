package com.bitisan.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.BooleanEnum;
import com.bitisan.constant.CommonStatus;
import com.bitisan.constant.SysConstant;
import com.bitisan.constant.TransactionType;
import com.bitisan.controller.BaseController;
import com.bitisan.exception.InformationExpiredException;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.*;
import com.bitisan.user.service.*;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.BigDecimalUtils;
import com.bitisan.util.DESUtil;
import com.bitisan.util.MD5;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.bitisan.util.BigDecimalUtils.compare;
import static org.springframework.util.Assert.*;


/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @date 2020年02月27日
 */
@Api(tags = "转账")
@RestController
@Slf4j
@RequestMapping(value = "/transfer", method = RequestMethod.POST)
public class TransferController extends BaseController {
    @Autowired
    private LocaleMessageSourceService sourceService;
    @Autowired
    private TransferAddressService transferAddressService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private TransferRecordService transferRecordService;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${transfer.url:}")
    private String url;
    @Value("${transfer.key:}")
    private String key;
    @Value("${transfer.smac:}")
    private String smac;
    @Autowired
    private MemberTransactionService memberTransactionService;
    @Autowired
    private MemberService memberService;

    /**
     * 根据币种查询转账地址等信息
     *
     * @param unit
     * @return
     */
    @ApiOperation(value = "根据币种查询转账地址等信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "unit", value = "币种"),
    })
    @PermissionOperation
    @RequestMapping("address")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult transferAddress(String unit, @RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(unit, user.getId());
        List<TransferAddress> list = transferAddressService.findByCoin(unit);
        MessageResult result = MessageResult.success();
        result.setData(TransferAddressInfo.builder().balance(memberWallet.getBalance())
                .info(list.stream().map(x -> {
                    HashMap<String, Object> map = new HashMap<>(3);
                    map.put("address", x.getAddress());
                    map.put("rate", x.getTransferFee());
                    map.put("minAmount", x.getMinAmount());
                    return map;
                }).collect(Collectors.toList())).build());
        return result;
    }

    /**
     * 转账申请
     *
     * @param unit
     * @param address
     * @param amount
     * @param fee
     * @param jyPassword
     * @param remark
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "转账申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "unit", value = "币种"),
            @ApiImplicitParam(name = "address", value = "提现地址"),
            @ApiImplicitParam(name = "amount", value = "金额"),
            @ApiImplicitParam(name = "fee", value = "提现手续费"),
            @ApiImplicitParam(name = "jyPassword", value = "交易密码"),
            @ApiImplicitParam(name = "remark", value = "用户申请提现备注"),
    })
    @PermissionOperation
    @RequestMapping("apply")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult transfer(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, String unit, String address,
                                  BigDecimal amount, BigDecimal fee, String jyPassword, String remark) throws Exception {
        AuthMember user = AuthMember.toAuthMember(authMember);
        hasText(jyPassword, sourceService.getMessage("MISSING_JYPASSWORD"));
        hasText(unit, sourceService.getMessage("MISSING_COIN_TYPE"));
        Coin coin = coinService.findByUnit(unit);
        notNull(coin, sourceService.getMessage("COIN_ILLEGAL"));
        isTrue(coin.getStatus().equals(CommonStatus.NORMAL) && coin.getCanTransfer().equals(BooleanEnum.IS_TRUE), sourceService.getMessage("COIN_NOT_SUPPORT"));
        TransferAddress transferAddress = transferAddressService.findByCoinIdAndAddress(coin.getUnit(), address);
        isTrue(transferAddress != null, sourceService.getMessage("WRONG_ADDRESS"));
        isTrue(fee.compareTo(BigDecimalUtils.mulRound(amount, BigDecimalUtils.getRate(transferAddress.getTransferFee()))) == 0, sourceService.getMessage("FEE_ERROR"));
        Member member = memberService.getById(user.getId());
        String mbPassword = member.getJyPassword();
        hasText(mbPassword, sourceService.getMessage("NO_SET_JYPASSWORD"));
        isTrue(MD5.md5(jyPassword + member.getSalt()).toLowerCase().equals(mbPassword), sourceService.getMessage("ERROR_JYPASSWORD"));
        MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(coin.getUnit(), user.getId());
        isTrue(compare(memberWallet.getBalance(), BigDecimalUtils.add(amount, fee)), sourceService.getMessage("INSUFFICIENT_BALANCE"));
        int result = memberWalletService.deductBalance(memberWallet.getId(), BigDecimalUtils.add(amount, fee));
        if (result <= 0) {
            throw new InformationExpiredException("Information Expired");
        }
        TransferRecord transferRecord = new TransferRecord();
        transferRecord.setAmount(amount);
        transferRecord.setCoinId(coin.getUnit());
        transferRecord.setMemberId(user.getId());
        transferRecord.setFee(fee);
        transferRecord.setAddress(address);
        transferRecord.setRemark(remark);
        transferRecord.setCreateTime(new Date());
        transferRecordService.save(transferRecord);

        MemberTransaction memberTransaction = new MemberTransaction();
        memberTransaction.setAddress(address);
        memberTransaction.setAmount(BigDecimalUtils.add(fee, amount));
        memberTransaction.setMemberId(user.getId());
        memberTransaction.setSymbol(coin.getUnit());
        memberTransaction.setCreateTime(transferRecord.getCreateTime());
        memberTransaction.setType(TransactionType.TRANSFER_ACCOUNTS.getCode());
        memberTransaction.setFee(transferRecord.getFee());
        memberTransaction.setRealFee(transferRecord.getFee()+"");
        memberTransaction.setDiscountFee("0");
        memberTransactionService.save(memberTransaction);
        if (transferRecord.getId() == null) {
            throw new InformationExpiredException("Information Expired");
        } else {
            JSONObject json = new JSONObject();
            //会员id
            json.put("uid", user.getId());
            //转账数目
            json.put("amount", amount);
            //转账手续费
            json.put("fee", fee);
            //币种单位
            json.put("coin", coin.getUnit());
            //转账地址
            json.put("address", address);
            //转账记录ID
            json.put("recordId", transferRecord.getId());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", json);
            jsonObject.put("sign", MD5.md5(json.toJSONString() + smac));
            String ciphertext = DESUtil.ENCRYPTMethod(jsonObject.toJSONString(), key).toUpperCase();
            String response = restTemplate.postForEntity(url, ciphertext, String.class).getBody();
            JSONObject resJson = JSONObject.parseObject(DESUtil.decrypt(response.trim(), key));
            if (resJson != null) {
                //验证签名
                if (MD5.md5(resJson.getJSONObject("data").toJSONString() + smac).equals(resJson.getString("sign"))) {
                    if (resJson.getJSONObject("data").getIntValue("returnCode") == 1) {
                        transferRecord.setOrderSn(resJson.getJSONObject("data").getString("returnMsg"));
                        transferRecordService.saveOrUpdate(transferRecord);
                        log.info("============》转账成功");
                    }
                }
            }
            return MessageResult.success();
        }
    }

    /**
     * 转账记录
     *
     * @param
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "转账记录")
    @PermissionOperation
    @RequestMapping("record")
    public MessageResult pageWithdraw(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, int pageNo, int pageSize) {
        MessageResult mr = new MessageResult(0, "success");
        AuthMember user = AuthMember.toAuthMember(authMember);
        IPage<TransferRecord> records = transferRecordService.findAllByMemberId(user.getId(), pageNo, pageSize);
        mr.setData(IPage2Page(records));
        return mr;
    }
}
