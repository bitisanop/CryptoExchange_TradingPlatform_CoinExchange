package com.bitisan.user.controller;

import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.constant.TransactionType;
import com.bitisan.controller.BaseController;
import com.bitisan.market.feign.MarketFeign;
import com.bitisan.pojo.CoinThumb;
import com.bitisan.user.entity.ConvertCoin;
import com.bitisan.user.entity.ConvertOrder;
import com.bitisan.user.entity.MemberTransaction;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.service.ConvertCoinService;
import com.bitisan.user.service.ConvertOrderService;
import com.bitisan.user.service.MemberTransactionService;
import com.bitisan.user.service.MemberWalletService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.user.util.DBUtils;
import com.bitisan.util.DateUtil;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Date;
import java.util.List;



/**
 * 动态兑换币种（前端CExchange）
 */
@Api(tags = "动态兑换币种")
@RestController
@RequestMapping("convert")
public class ConvertController extends BaseController {

    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private MemberTransactionService transactionService;
    @Autowired
    private ConvertCoinService coinService;
    @Autowired
    private ConvertOrderService convertOrderService;
    @Autowired
    private DBUtils dbUtils;
    @Autowired
    private MarketFeign marketFeign;

    /**
     * 查询所有记录
     *
     * @param authMember
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "页码"),
            @ApiImplicitParam(name = "pageSize", value = "每页总数"),
    })
    @PermissionOperation
    @RequestMapping("orderList")
    public MessageResult findTransaction(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, HttpServletRequest request, int pageNo, int pageSize) throws ParseException {
        //校验 签到活动 币种 会员 会员钱包
        AuthMember member = AuthMember.toAuthMember(authMember);
        MessageResult mr = new MessageResult();
        mr.setCode(0);
        mr.setMessage("success");
        mr.setData(IPage2Page(convertOrderService.queryByMember(member.getId(), pageNo, pageSize)));
        return mr;
    }

    @ApiOperation(value = "获取全部币种")
    @RequestMapping("getCoins")
    public MessageResult getCoins() {
        List<ConvertCoin> all = coinService.findByStatus(1);
        return success(all);
    }

    @ApiOperation(value = "根据单位获取币种")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "unit", value = "单位"),
    })
    @RequestMapping("getCoinsByUnit")
    public MessageResult getCoinsByUnit(String unit) {
        List<ConvertCoin> all = coinService.findByStatus(1);
        for (int i = all.size()-1; i >=0; i--) {
            ConvertCoin convertCoin = all.get(i);
            if(convertCoin.getCoinUnit().equalsIgnoreCase(unit)){
                all.remove(convertCoin);
            }
        }
        return success(all);
    }

    @ApiOperation(value = "获取价格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromUnit", value = "源币种"),
            @ApiImplicitParam(name = "toUnit", value = "目标币种"),
    })
    @RequestMapping("getPrice")
    public MessageResult getPrice(String fromUnit,String toUnit) {
        BigDecimal lastPrice = getLastPrice(fromUnit, toUnit);
        if(lastPrice!=null){
            return success(lastPrice);
        }
        return error("交易对不支持");

    }

    private BigDecimal getLastPrice(String fromUnit,String toUnit){
        List<CoinThumb> thumbList = marketFeign.findSymbolThumb4Feign();
        if(fromUnit.indexOf("#")>0){
            dbUtils.excuteUpdateSql(fromUnit.split("#")[1]);
            fromUnit = fromUnit.split("#")[0];
        }
        String symbol=fromUnit.toUpperCase()+"/"+toUnit.toUpperCase();
        String symbol2 = toUnit.toUpperCase()+"/"+fromUnit.toUpperCase();
        for (CoinThumb coinThumb : thumbList) {
            if(coinThumb.getSymbol().equalsIgnoreCase(symbol)){
                return coinThumb.getClose();
            }
            if(coinThumb.getSymbol().equalsIgnoreCase(symbol2)){
                return BigDecimal.ONE.divide(coinThumb.getClose(),8, RoundingMode.HALF_UP);
            }
        }
        return null;
    }

    @ApiOperation(value = "转换")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromUnit", value = "源币种"),
            @ApiImplicitParam(name = "toUnit", value = "目标币种"),
            @ApiImplicitParam(name = "needAmount", value = "需要交易的数量"),
    })
    @PermissionOperation
    @RequestMapping("do-exchange")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult doExchagne(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember, String fromUnit,String toUnit, BigDecimal needAmount) {
        AuthMember member = AuthMember.toAuthMember(authMember);
        ConvertCoin convertCoin = coinService.findByCoinUnit(toUnit);
        //获取现价
        BigDecimal lastPrice = getLastPrice(fromUnit, toUnit);
        if(lastPrice==null){
            return error("交易对不支持");
        }
        // 检查余额是否足够
        MemberWallet needMW = walletService.findByCoinUnitAndMemberId(fromUnit, member.getId());
        if(needMW.getBalance().compareTo(needAmount) < 0) {
            return error("Balance is not enough");
        }
        // 执行兑换
        MemberWallet targetMW = walletService.findByCoinUnitAndMemberId(toUnit, member.getId());
        BigDecimal amount = needAmount.multiply(lastPrice);

        BigDecimal fee = amount.multiply(convertCoin.getFee());
        amount = amount.subtract(fee);
        //创建交易
        ConvertOrder order = new ConvertOrder();
        order.setFee(fee);
        order.setCreateTime(new Date());
        order.setMemberId(member.getId());
        order.setFromUnit(fromUnit);
        order.setToUnit(toUnit);
        order.setFromAmount(needAmount);
        order.setToAmount(amount);
        order.setPrice(lastPrice);
        order.setStatus(1);
        convertOrderService.save(order);

        // 增加目标余额
        walletService.increaseBalance(targetMW.getId(), amount);

        // 减少基币余额
        walletService.decreaseBalance(needMW.getId(), needAmount);

        // 增加资金变更记录（增加）
        MemberTransaction memberTransaction = new MemberTransaction();
        memberTransaction.setFee(BigDecimal.ZERO);
        memberTransaction.setAmount(amount);
        memberTransaction.setMemberId(member.getId());
        memberTransaction.setSymbol(toUnit);
        memberTransaction.setType(TransactionType.ACTIVITY_BUY.getCode());
        memberTransaction.setCreateTime(DateUtil.getCurrentDate());
        memberTransaction.setRealFee("0");
        memberTransaction.setDiscountFee("0");
        transactionService.save(memberTransaction);

        // 增加资金变更记录（减少记录）
        MemberTransaction memberTransactionOut = new MemberTransaction();
        memberTransactionOut.setFee(BigDecimal.ZERO);
        memberTransactionOut.setAmount(needAmount);
        memberTransactionOut.setMemberId(member.getId());
        memberTransactionOut.setSymbol(fromUnit);
        memberTransactionOut.setType(TransactionType.ACTIVITY_BUY.getCode());
        memberTransactionOut.setCreateTime(DateUtil.getCurrentDate());
        memberTransactionOut.setRealFee("0");
        memberTransactionOut.setDiscountFee("0");
        transactionService.save(memberTransactionOut);

        return success("Exchange successful");
    }
}
