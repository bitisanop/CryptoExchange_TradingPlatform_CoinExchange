package com.bitisan.user.controller;//package com.bitisan.user.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.TransactionType;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.*;
import com.bitisan.user.service.*;
import com.bitisan.user.system.CoinExchangeFactory;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.user.vo.MemberTransaction4Front;
import com.bitisan.user.vo.MemberTransactionVO;
import com.bitisan.user.vo.MemberWalletVo;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.bitisan.constant.SysConstant.SESSION_MEMBER;

@Api(tags = "资产")
@RestController
@RequestMapping("/asset")
@Slf4j
public class AssetController extends BaseController {
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private WalletTransRecordService walletTransRecordService;
    @Autowired
    private MemberTransactionService transactionService;
    @Autowired
    private CoinExchangeFactory coinExchangeFactory;
    @Value("${gcx.match.max-limit:1000}")
    private double gcxMatchMaxLimit;
    @Value("${gcx.match.each-limit:5}")
    private double gcxMatchEachLimit;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @Autowired
    private QuickExchangeService quickExchangeService;
    @Autowired
    private LocaleMessageSourceService sourceService;

    @Autowired
    private MemberService memberService;



    /**
     * 用户钱包信息
     *
     * @return
     */
    @ApiOperation(value = "用户钱包信息")
    @PermissionOperation
    @RequestMapping("wallet")
    public MessageResult findWallet(@RequestHeader(SESSION_MEMBER) String authMember) {
        AuthMember member = AuthMember.toAuthMember(authMember);
        List<MemberWallet> wallets = walletService.findAllByMemberId(member.getId());
        List<MemberWalletVo> walletVos = new ArrayList<>();
        wallets.forEach(wallet -> {
            MemberWalletVo vo = new MemberWalletVo();
            BeanUtils.copyProperties(wallet,vo);
            Coin coin = coinService.findByUnit(wallet.getCoinId());
            CoinExchangeFactory.ExchangeRate rate = coinExchangeFactory.get(coin.getUnit());
            if (rate != null) {
                coin.setUsdRate(rate.getUsdRate());
                coin.setCnyRate(rate.getCnyRate());
            } else {
                log.info("unit = {} , rate = null ", coin.getUnit());
            }
            vo.setCoin(coin);
            walletVos.add(vo);
        });
        MessageResult mr = MessageResult.success("success");
        mr.setData(walletVos);
        return mr;
    }



    /**
     * 查询特定类型的记录
     *
     * @param pageNo
     * @param pageSize
     * @param type
     * @return
     */
    @ApiOperation(value = "查询特定类型的记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "页数"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量"),
            @ApiImplicitParam(name = "type", value = "交易类型"),
    })
    @PermissionOperation
    @RequestMapping("transaction")
    public MessageResult findTransaction(@RequestHeader(SESSION_MEMBER) String authMember, int pageNo, int pageSize, TransactionType type) {
        AuthMember member = AuthMember.toAuthMember(authMember);
        MessageResult mr = new MessageResult();
        mr.setData(IPage2Page(transactionService.queryByMember(member.getId(), pageNo, pageSize, type)));
        mr.setCode(0);
        mr.setMessage("success");
        return mr;
    }

    /**
     * 查询所有记录
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "查询所有记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "页数"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
            @ApiImplicitParam(name = "symbol", value = "币种"),
            @ApiImplicitParam(name = "type", value = "交易类型"),
    })
    @PermissionOperation
    @RequestMapping("transaction/all")
    public MessageResult findTransaction(@RequestHeader(SESSION_MEMBER) String authMember, HttpServletRequest request, int pageNo, int pageSize,
                                         @RequestParam(value = "startTime",required = false)  String startTime,
                                         @RequestParam(value = "endTime",required = false)  String endTime,
                                         @RequestParam(value = "symbol",required = false)  String symbol,
                                         @RequestParam(value = "type",required = false)  String type) throws ParseException {
        AuthMember member = AuthMember.toAuthMember(authMember);
        MessageResult mr = new MessageResult();
        TransactionType transactionType = null;
        if (StringUtils.isNotEmpty(type)) {
            transactionType = TransactionType.valueOfOrdinal(Integer.parseInt(type));
        }
        mr.setCode(0);
        mr.setMessage("success");
        Page<MemberTransaction4Front> page = transactionService.queryByMember(member.getId(), pageNo, pageSize, transactionType, startTime, endTime, symbol);
        mr.setData(IPage2Page(page));
        return mr;
    }

    @ApiOperation(value = "根据币种查找钱包")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "币种"),
    })
    @PermissionOperation
    @RequestMapping("wallet/{symbol}")
    public MessageResult findWalletBySymbol(@RequestHeader(SESSION_MEMBER) String authMember, @PathVariable String symbol) {
        AuthMember member = AuthMember.toAuthMember(authMember);
        MessageResult mr = MessageResult.success("success");
        mr.setData(walletService.findByCoinUnitAndMemberId(symbol, member.getId()));
        return mr;
    }


    /**
     * 获取兑换列表
     * @param member
     * @return
     */
    @ApiOperation(value = "获取兑换列表")
    @PermissionOperation
    @RequestMapping("wallet/quick-exchange-list")
    public MessageResult queryQuickExchange(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
        List<QuickExchange> retList =  quickExchangeService.findAllByMemberId(member.getId());
        MessageResult ret = new MessageResult();
        ret.setCode(0);
        ret.setData(retList);
        ret.setMessage("获取成功");
        return ret;
    }
}
