package com.bitisan.exchange.feign;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.*;
import com.bitisan.controller.BaseController;
import com.bitisan.exchange.Trader.CoinTrader;
import com.bitisan.exchange.Trader.CoinTraderFactory;
import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.entity.ExchangeOrderDetail;
import com.bitisan.exchange.service.ExchangeCoinService;
import com.bitisan.exchange.service.ExchangeOrderDetailService;
import com.bitisan.exchange.service.ExchangeOrderService;
import com.bitisan.pojo.ExchangeTrade;
import com.bitisan.screen.ExchangeOrderScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.service.CoinService;
import com.bitisan.user.service.MemberWalletService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 委托订单处理类
 */
@Slf4j
@RestController
@RequestMapping("/orderFeign")
public class OrderFeignController extends BaseController {
    @Autowired
    private ExchangeOrderService orderService;
    @Autowired
    private ExchangeCoinService exchangeCoinService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private CoinTraderFactory factory;
    @Autowired
    private ExchangeOrderDetailService exchangeOrderDetailService;
    @Autowired
    private LocaleMessageSourceService msService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private String addRedisKey = "E_ADD_%s_%s";
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private MemberFeign memberFeign;

    @Value("${exchange.max-cancel-times:-1}")
    private int maxCancelTimes;

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @GetMapping("/findOne")
    public ExchangeOrder findOne(@RequestParam(value = "id",required = true) String id) throws Exception {
        ExchangeOrder order = orderService.getById(id);
        return order;
    }

    /**
     * 处理交易匹配
     *
     * @param trade
     * @param secondReferrerAward 二级推荐人是否返回佣金 true 返回佣金
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "处理交易匹配")
    @PostMapping("/processExchangeTrade")
    public MessageResult processExchangeTrade(@RequestBody ExchangeTrade trade,
                                              @RequestParam(value = "secondReferrerAward",required = true) boolean secondReferrerAward) throws Exception {
        return orderService.processExchangeTrade(trade,secondReferrerAward);
    }

    @PostMapping("/tradeCompleted")
    public MessageResult tradeCompleted(@RequestParam String orderId,
                                        @RequestParam BigDecimal tradedAmount,
                                        @RequestParam BigDecimal turnover){
        return orderService.tradeCompleted(orderId,tradedAmount,turnover);
    }

    @PostMapping("/cancelOrder")
    public MessageResult cancelOrder(@RequestParam("orderId")String orderId,
                                     @RequestParam("tradedAmount")BigDecimal tradedAmount,
                                     @RequestParam("turnover")BigDecimal turnover){
        return orderService.cancelOrder(orderId, tradedAmount, turnover);
    }

    @PostMapping("findAll")
    public Page<ExchangeOrder> findAll(@RequestBody ExchangeOrderScreen screen){
        return orderService.findAll(screen);
    }


    @PostMapping("findAllDetailByOrderId")
    public List<ExchangeOrderDetail> findAllDetailByOrderId(@RequestParam("orderId")String orderId){
        return exchangeOrderDetailService.findAllByOrderId(orderId);
    }

    @PostMapping(value = "findAllTradingOrderBySymbol")
    public List<ExchangeOrder> findAllTradingOrderBySymbol(@RequestParam("symbol")String symbol){
        return orderService.findAllTradingOrderBySymbol(symbol);
    }

    @PostMapping(value = "forceCancelOrder")
    public MessageResult forceCancelOrder(@RequestBody ExchangeOrder order){
        orderService.forceCancelOrder(order);
        return MessageResult.success();
    }


    /**
     * 行情机器人专用：当前委托
     * @param uid
     * @param sign
     * @return
     */
    @RequestMapping("mockcurrentydhdnskd")
    public Page<ExchangeOrder> currentOrderMock(
            @RequestParam("uid") Long uid,
            @RequestParam("sign") String sign,
            @RequestParam("symbol") String symbol,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("pageSize") int pageSize) {
        if(uid != 1 && uid != 10001) {
            return null;
        }
        if(!sign.equals("77585211314qazwsx")) {
            return null;
        }
        Page<ExchangeOrder> page = orderService.findCurrent(uid, symbol, pageNo, pageSize);

//        page.getContent().forEach(exchangeOrder -> {
//            //获取交易成交详情(机器人无需获取详情）
//
//            BigDecimal tradedAmount = BigDecimal.ZERO;
//            List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(exchangeOrder.getOrderId());
//            exchangeOrder.setDetail(details);
//            for (ExchangeOrderDetail trade : details) {
//                tradedAmount = tradedAmount.add(trade.getAmount());
//            }
//            exchangeOrder.setTradedAmount(tradedAmount);
//
//        });

        return page;
    }


    /**
     * 行情机器人专用：交易取消委托
     * @param uid
     * @param orderId
     * @return
     */
//    @ApiOperation(value = "行情机器人专用：交易取消委托")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "uid", value = "用户id"),
//            @ApiImplicitParam(name = "sign", value = "标记"),
//            @ApiImplicitParam(name = "orderId", value = "订单id"),
//    })
    @RequestMapping("mockcancelydhdnskd")
    public MessageResult cancelOrdermock(@RequestParam("uid") Long uid, @RequestParam("sign")String sign, @RequestParam("orderId")String orderId) {
        ExchangeOrder order = orderService.getById(orderId);
        if(uid != 1 && uid != 10001) {
            return MessageResult.error(500, msService.getMessage("OPERATION_FORBIDDEN"));
        }
        if(!sign.equals("77585211314qazwsx")) {
            return MessageResult.error(500, msService.getMessage("OPERATION_FORBIDDEN"));
        }
        if (order.getStatus() != ExchangeOrderStatus.TRADING) {
            return MessageResult.error(500, msService.getMessage("ORDER_STATUS_ERROR"));
        }
        // 活动清盘期间，无法撤销订单
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(order.getSymbol());
        if(exchangeCoin.getPublishType() != ExchangeCoinPublishType.NONE.getCode()) {
            long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
            try {
                // 处在活动结束时间与清盘结束时间之间
                if(currentTime > dateTimeFormat.parse(exchangeCoin.getEndTime()).getTime() &&
                        currentTime < dateTimeFormat.parse(exchangeCoin.getClearTime()).getTime()) {
                    return MessageResult.error(500, msService.getMessage("CANNOT_CANCEL_ORDER"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return MessageResult.error(500, msService.getMessage("EXAPI_UNKNOWN_ERROR3"));
            }
        }
        if(isExchangeOrderExist(order)){
            // 发送消息至Exchange系统
            rocketMQTemplate.convertAndSend("exchange-order-cancel",JSON.toJSONString(order));
        }
        else{
            //强制取消
            orderService.forceCancelOrder(order);
        }
        return MessageResult.success(msService.getMessage("EXAPI_SUCCESS"));
    }

    /**
     * 查找撮合交易器中订单是否存在
     * @param order
     * @return
     */
    private boolean isExchangeOrderExist(ExchangeOrder order){
        try {
            CoinTrader trader = factory.getTrader(order.getSymbol());
            ExchangeOrder order1 = trader.findOrder(order.getOrderId(), order.getType().getCode(), order.getDirection().getCode());
            return order1 != null;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 行情机器人专用：添加委托订单
     * @param uid
     * @param direction
     * @param symbol
     * @param price
     * @param amount
     * @param type
     *          usedisCount 暂时不用
     * @return
     */
//    @ApiOperation(value = "行情机器人专用：添加委托订单")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "uid", value = "用户id"),
//            @ApiImplicitParam(name = "sign", value = "标记"),
//            @ApiImplicitParam(name = "direction", value = "方向 0：买  1：卖"),
//            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
//            @ApiImplicitParam(name = "amount", value = "金额"),
//            @ApiImplicitParam(name = "price", value = "价格"),
//            @ApiImplicitParam(name = "type", value = "0 市价 1 限价"),
//    })
    @RequestMapping("mockaddydhdnskd")
    public MessageResult addOrderMock(
            @RequestParam("uid") Long uid,
            @RequestParam("sign")String sign,
            @RequestParam("direction") ExchangeOrderDirection direction,
            @RequestParam("symbol")String symbol,
            @RequestParam("price")BigDecimal price,
            @RequestParam("amount")BigDecimal amount,
            @RequestParam("type")ExchangeOrderType type) {
        //int expireTime = SysConstant.USER_ADD_EXCHANGE_ORDER_TIME_LIMIT_EXPIRE_TIME;
        //ValueOperations valueOperations =  redisTemplate.opsForValue();
        if(direction == null || type == null){
            return MessageResult.error(500,msService.getMessage("ILLEGAL_ARGUMENT"));
        }

//        Member member=memberService.findOne(uid);
//        if(member.getMemberLevel()== MemberLevelEnum.GENERAL){
//            return MessageResult.error(500,msService.getMessage("REAL_NAME_AUTHENTICATION"));
//        }

        if(uid != 1 && uid != 10001) {
            return MessageResult.error(500,msService.getMessage("ILLEGAL_ARGUMENT"));
        }
        if(!sign.equals("77585211314qazwsx")) {
            return MessageResult.error(500,msService.getMessage("ILLEGAL_ARGUMENT"));
        }

//        //是否被禁止交易
//        if(member.getTransactionStatus().equals(BooleanEnum.IS_FALSE)){
//            return MessageResult.error(500,msService.getMessage("CANNOT_TRADE"));
//        }

        ExchangeOrder order = new ExchangeOrder();
        //判断限价输入值是否小于零
        if (price.compareTo(BigDecimal.ZERO) <= 0 && type == ExchangeOrderType.LIMIT_PRICE) {
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        //判断数量小于零
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return MessageResult.error(500, msService.getMessage("NUMBER_OF_ILLEGAL"));
        }
        //根据交易对名称（symbol）获取交易对儿信息
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        if (exchangeCoin == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        if(exchangeCoin.getEnable() != 1 || exchangeCoin.getExchangeable() != 1) {
            return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
        }

        //获取基准币
        String baseCoin = exchangeCoin.getBaseSymbol();
        //获取交易币
        String exCoin = exchangeCoin.getCoinSymbol();
        Coin coin=null;
        //根据交易方向查询币种信息
        if (direction == ExchangeOrderDirection.SELL) {
            coin = coinService.findByUnit(exCoin);
        } else {
            coin = coinService.findByUnit(baseCoin);
        }
        if (coin == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        //设置价格精度
        price = price.setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
        //委托数量和精度控制
        if (direction == ExchangeOrderDirection.BUY && type == ExchangeOrderType.MARKET_PRICE) {
            amount = amount.setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
            //最小成交额控制
            if (amount.compareTo(exchangeCoin.getMinTurnover()) < 0) {
                return MessageResult.error(500, msService.getMessage("MINIMUM_TURNOVER") + exchangeCoin.getMinTurnover());
            }
        } else {
            amount = amount.setScale(exchangeCoin.getCoinScale(), BigDecimal.ROUND_DOWN);
            //成交量范围控制
            if(exchangeCoin.getMaxVolume()!=null&&exchangeCoin.getMaxVolume().compareTo(BigDecimal.ZERO)!=0
                    &&exchangeCoin.getMaxVolume().compareTo(amount)<0){
                return MessageResult.error(msService.getMessage("AMOUNT_OVER_SIZE")+" "+exchangeCoin.getMaxVolume());
            }
            if(exchangeCoin.getMinVolume()!=null&&exchangeCoin.getMinVolume().compareTo(BigDecimal.ZERO)!=0
                    &&exchangeCoin.getMinVolume().compareTo(amount)>0){
                return MessageResult.error(msService.getMessage("AMOUNT_TOO_SMALL")+" "+exchangeCoin.getMinVolume());
            }
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0 && type == ExchangeOrderType.LIMIT_PRICE) {
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return MessageResult.error(500, msService.getMessage("NUMBER_OF_ILLEGAL"));
        }

//        MemberWallet baseCoinWallet = walletService.findByCoinUnitAndMemberId(baseCoin, member.getId());
//        MemberWallet exCoinWallet = walletService.findByCoinUnitAndMemberId(exCoin, member.getId());
//        if (baseCoinWallet == null || exCoinWallet == null) {
//            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
//        }
//        if (baseCoinWallet.getIsLock() == BooleanEnum.IS_TRUE || exCoinWallet.getIsLock() == BooleanEnum.IS_TRUE) {
//            return MessageResult.error(500, msService.getMessage("WALLET_LOCKED"));
//        }

        // 如果有最低卖价限制，出价不能低于此价,【(且禁止市场价格卖)去掉】
        if (direction == ExchangeOrderDirection.SELL && exchangeCoin.getMinSellPrice().compareTo(BigDecimal.ZERO) > 0
                && ((price.compareTo(exchangeCoin.getMinSellPrice()) < 0) && type == ExchangeOrderType.LIMIT_PRICE)) {
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        // 如果有最高买价限制，出价不能高于此价，【(且禁止市场价格买)去掉】
        if(direction == ExchangeOrderDirection.BUY && exchangeCoin.getMaxBuyPrice().compareTo(BigDecimal.ZERO) > 0
                && ((price.compareTo(exchangeCoin.getMaxBuyPrice()) > 0) && type == ExchangeOrderType.LIMIT_PRICE)) {
            return MessageResult.error(500, msService.getMessage("NO_PRICE_CEILING"));
        }
        //查看是否启用市价买卖
        if (type == ExchangeOrderType.MARKET_PRICE) {
            if (exchangeCoin.getEnableMarketBuy() == BooleanEnum.IS_FALSE.getCode() && direction == ExchangeOrderDirection.BUY) {
                return MessageResult.error(500, msService.getMessage("NO_MARKET_PRICE_BUY"));
            } else if (exchangeCoin.getEnableMarketSell() == BooleanEnum.IS_FALSE.getCode() && direction == ExchangeOrderDirection.SELL) {
                return MessageResult.error(500, msService.getMessage("NO_MARKET_PRICE_SELL"));
            }
        }

//        //限制委托数量
//        if (exchangeCoin.getMaxTradingOrder() > 0 && orderService.findCurrentTradingCount(uid, symbol, direction) >= exchangeCoin.getMaxTradingOrder()) {
//            return MessageResult.error(500, msService.getMessage("MAXIMUM_QUANTITY") + exchangeCoin.getMaxTradingOrder());
//        }

        // 抢购模式活动订单限制（用户无法在活动前下买单）
        long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
        // 抢购模式下，无法在活动开始前下买单，仅限于管理员下卖单
        if(exchangeCoin.getPublishType() == ExchangeCoinPublishType.QIANGGOU.getCode()) {
            // 抢购模式订单
            try {
                if(currentTime < dateTimeFormat.parse(exchangeCoin.getStartTime()).getTime()) {
                    if(direction == ExchangeOrderDirection.BUY) {
                        // 抢购未开始
                        if(currentTime < dateTimeFormat.parse(exchangeCoin.getStartTime()).getTime()) {
                            return MessageResult.error(500, msService.getMessage("ACTIVITY_NOT_STARTED"));
                        }
                    }else {
                        // 此处2是管理员用户的ID
                        if(uid != 2 && uid != 1 && uid != 10001) {
                            return MessageResult.error(500, msService.getMessage("UNABLE_TO_PLACE_BUY_ORDER"));
                        }
                    }
                }else {
                    // 活动进行期间，无法下卖单 + 无法下市价单
                    if(currentTime < dateTimeFormat.parse(exchangeCoin.getEndTime()).getTime()) {
                        if(direction == ExchangeOrderDirection.SELL) {
                            return MessageResult.error(500, msService.getMessage("UNABLE_TO_PLACE_SELL_ORDER"));
                        }
                        if(type == ExchangeOrderType.MARKET_PRICE){
                            return MessageResult.error(500, msService.getMessage("ITS_NOT_MARKETABLE"));
                        }
                    }else {
                        // 清盘期间，无法下单
                        if(currentTime < dateTimeFormat.parse(exchangeCoin.getClearTime()).getTime()) {
                            return MessageResult.error(500, msService.getMessage("WINDING_UP"));
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return MessageResult.error(500,msService.getMessage("EXAPI_UNKNOWN_ERROR0"));
            }
        }
        // 分摊模式活动订单限制(开始前任何人无法下单)
        if(exchangeCoin.getPublishType() == ExchangeCoinPublishType.FENTAN.getCode()) {
            try {

                if(currentTime < dateTimeFormat.parse(exchangeCoin.getStartTime()).getTime()) {
                    // UNABLE_TO_PLACE_BUY_ORDER
                    return MessageResult.error(500, msService.getMessage("ACTIVITY_NOT_STARTED"));
                }else {
                    // 活动开始后且在结束前，无法下卖单 + 下单金额必须符合规定
                    if(currentTime < dateTimeFormat.parse(exchangeCoin.getEndTime()).getTime()) {
                        if(direction == ExchangeOrderDirection.SELL) {
                            return MessageResult.error(500, msService.getMessage("ACTIVITY_STARTED_CANT_SELL"));
                        }else {
                            if(type == ExchangeOrderType.MARKET_PRICE) {
                                return MessageResult.error(500, msService.getMessage("ITS_NOT_MARKETABLE"));
                            }else {
                                if(price.compareTo(exchangeCoin.getPublishPrice()) != 0) {
                                    return MessageResult.error(500, msService.getMessage("ORDER_PRICE") + exchangeCoin.getPublishPrice());
                                }
                            }
                        }
                    }else {
                        // 清盘期间，普通用户无法下单，仅有管理员用户ID可下单
                        if(currentTime < dateTimeFormat.parse(exchangeCoin.getClearTime()).getTime()) {
                            // 此处2是超级管理员用户的ID
                            if(uid != 2 && uid != 1 && uid != 10001) {
                                return MessageResult.error(500, msService.getMessage("WINDING_UP"));
                            }else {
                                if(price.compareTo(exchangeCoin.getPublishPrice()) != 0) {
                                    return MessageResult.error(500, msService.getMessage("ORDER_PRICE") + exchangeCoin.getPublishPrice());
                                }
                                if(direction == ExchangeOrderDirection.BUY) {
                                    return MessageResult.error(500, msService.getMessage("PERIOD_LIQUIDATION"));
                                }
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return MessageResult.error(500,msService.getMessage("EXAPI_UNKNOWN_ERROR1"));
            }
        }
        order.setMemberId(uid);
        order.setSymbol(symbol);
        order.setBaseSymbol(baseCoin);
        order.setCoinSymbol(exCoin);
        order.setType(type);
        order.setDirection(direction);
        if(order.getType() == ExchangeOrderType.MARKET_PRICE){
            order.setPrice(BigDecimal.ZERO);
        }
        else{
            order.setPrice(price);
        }
        order.setUseDiscount("0");
        //限价买入单时amount为用户设置的总成交额
        order.setAmount(amount);

        MessageResult mr = orderService.addOrder(uid, order);
        if (mr.getCode() != 0) {
            return MessageResult.error(500, msService.getMessage("ORDER_FAILED") + mr.getMessage());
        }
        log.info(">>>>>>>>>>订单提交完成>>>>>>>>>>");
        // 发送消息至Exchange系统
        rocketMQTemplate.convertAndSend("exchange-order", JSON.toJSONString(order));
        MessageResult result = MessageResult.success(msService.getMessage("EXAPI_SUCCESS"));
        result.setData(order.getOrderId());
        return result;
    }

    @RequestMapping("getExchangeTurnoverBase")
    public List<ExchangeOrder> getExchangeTurnoverBase(@RequestParam("dateStr")String dateStr){
        return this.orderService.getExchangeTurnoverBase(dateStr);
    }

    @RequestMapping("getExchangeTurnoverCoin")
    public List<ExchangeOrder> getExchangeTurnoverCoin(@RequestParam("dateStr")String dateStr){
        return this.orderService.getExchangeTurnoverCoin(dateStr);
    }

    @RequestMapping("getExchangeTurnoverSymbol")
    public List<ExchangeOrder> getExchangeTurnoverSymbol(@RequestParam("dateStr")String dateStr){
        return this.orderService.getExchangeTurnoverSymbol(dateStr);
    }

    /**
     * 添加委托订单
     * @param memberId
     * @param direction
     * @param symbol
     * @param price
     * @param amount
     * @param type
     *          usedisCount 暂时不用
     * @return
     */
    @ApiOperation(value = "添加委托订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "direction", value = "方向 0：买  1：卖"),
            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
            @ApiImplicitParam(name = "amount", value = "金额"),
            @ApiImplicitParam(name = "price", value = "价格"),
            @ApiImplicitParam(name = "type", value = "0 市价 1 限价"),
    })
    @RequestMapping("addOrder")
    public MessageResult addOrder(@RequestParam("memberId") Long memberId,
                                  @RequestParam("direction")Integer directionCode,
                                  @RequestParam("symbol")String symbol,
                                  @RequestParam("price")BigDecimal price,
                                  @RequestParam("amount")BigDecimal amount,
                                  @RequestParam("type")Integer typeCode) {

//        int expireTime = SysConstant.USER_ADD_EXCHANGE_ORDER_TIME_LIMIT_EXPIRE_TIME;
//        ValueOperations valueOperations =  redisTemplate.opsForValue();
        if(amount==null || directionCode == null || typeCode == null){
            return MessageResult.error(500,msService.getMessage("PARAMETER_ERROR"));
        }
        ExchangeOrderDirection direction = ExchangeOrderDirection.creator(directionCode);
        ExchangeOrderType type = ExchangeOrderType.creator(typeCode);
        //获取 redis 锁
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String key = String.format(addRedisKey,memberId,symbol);
        String redisVal = ops.get(key);
        if(redisVal!=null){
            return MessageResult.error(500, msService.getMessage("PLEASE_WAIT"));
        }
        ops.set(key,"11",3, TimeUnit.MINUTES);//3分钟

        Member member = memberFeign.findMemberById(memberId);
//        if(member.getMemberLevel()== MemberLevelEnum.GENERAL){
//            return MessageResult.error(500,msService.getMessage("REAL_NAME_AUTHENTICATION"));
//        }

        //是否被禁止交易
        if(member.getTransactionStatus().equals(BooleanEnum.IS_FALSE)){
            redisTemplate.delete(key);
            return MessageResult.error(500,msService.getMessage("CANNOT_TRADE"));
        }
        ExchangeOrder order = new ExchangeOrder();
        //判断限价输入值是否小于零
        if (price.compareTo(BigDecimal.ZERO) <= 0 && type == ExchangeOrderType.LIMIT_PRICE) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        //判断数量小于零
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("NUMBER_OF_ILLEGAL"));
        }
        //根据交易对名称（symbol）获取交易对儿信息
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        if (exchangeCoin == null) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        if(exchangeCoin.getEnable() != 1 || exchangeCoin.getExchangeable() != 1) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
        }
        // 不允许卖
        if(exchangeCoin.getEnableSell() == BooleanEnum.IS_FALSE.getCode() && direction == ExchangeOrderDirection.SELL){
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("STOP_SELLING"));
        }

        // 不允许买
        if(exchangeCoin.getEnableBuy() == BooleanEnum.IS_FALSE.getCode() && direction == ExchangeOrderDirection.BUY){
            redisTemplate.delete(key);
            return MessageResult.error(500,  msService.getMessage("STOP_BUYING"));
        }

        //获取基准币
        String baseCoin = exchangeCoin.getBaseSymbol();
        //获取交易币
        String exCoin = exchangeCoin.getCoinSymbol();
        Coin coin=null;
        //根据交易方向查询币种信息
        if (direction == ExchangeOrderDirection.SELL) {
            coin = coinService.findByUnit(exCoin);
        } else {
            coin = coinService.findByUnit(baseCoin);
        }
        if (coin == null) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        //设置价格精度
        price = price.setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
        //委托数量和精度控制
        if (direction == ExchangeOrderDirection.BUY && type == ExchangeOrderType.MARKET_PRICE) {
            amount = amount.setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
            //最小成交额控制
            if (amount.compareTo(exchangeCoin.getMinTurnover()) < 0) {
                redisTemplate.delete(key);
                return MessageResult.error(500, msService.getMessage("AMOUNT_TOO_SMALL") + exchangeCoin.getMinTurnover());
            }
        } else {
            amount = amount.setScale(exchangeCoin.getCoinScale(), BigDecimal.ROUND_DOWN);
            //成交量范围控制
            if(exchangeCoin.getMaxVolume()!=null&&exchangeCoin.getMaxVolume().compareTo(BigDecimal.ZERO)!=0
                    &&exchangeCoin.getMaxVolume().compareTo(amount)<0){
                redisTemplate.delete(key);
                return MessageResult.error(msService.getMessage("AMOUNT_OVER_SIZE")+" "+exchangeCoin.getMaxVolume());
            }
            if(exchangeCoin.getMinVolume()!=null&&exchangeCoin.getMinVolume().compareTo(BigDecimal.ZERO)!=0
                    &&exchangeCoin.getMinVolume().compareTo(amount)>0){
                log.info("msService.getMessage(AMOUNT_TOO_SMALL)===={}",msService.getMessage("AMOUNT_TOO_SMALL"));
                redisTemplate.delete(key);
                return MessageResult.error(msService.getMessage("AMOUNT_TOO_SMALL")+" "+exchangeCoin.getMinVolume());
            }
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0 && type == ExchangeOrderType.LIMIT_PRICE) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("NUMBER_OF_ILLEGAL"));
        }
        MemberWallet baseCoinWallet = walletService.findByCoinUnitAndMemberId(baseCoin, member.getId());
        MemberWallet exCoinWallet = walletService.findByCoinUnitAndMemberId(exCoin, member.getId());
        if (baseCoinWallet == null || exCoinWallet == null) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }

        if (baseCoinWallet.getIsLock() == BooleanEnum.IS_TRUE || exCoinWallet.getIsLock() == BooleanEnum.IS_TRUE) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("WALLET_LOCKED"));
        }
        //如果有最低卖价限制，出价不能低于此价,且禁止市场价格卖
        if (direction == ExchangeOrderDirection.SELL && exchangeCoin.getMinSellPrice().compareTo(BigDecimal.ZERO) > 0
                && ((price.compareTo(exchangeCoin.getMinSellPrice()) < 0) || type == ExchangeOrderType.MARKET_PRICE)) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("FLOOR_PRICE") + exchangeCoin.getMinSellPrice());
        }
        // 如果有最高买价限制，出价不能高于此价，且禁止市场价格买
        if(direction == ExchangeOrderDirection.BUY && exchangeCoin.getMaxBuyPrice().compareTo(BigDecimal.ZERO) > 0
                && ((price.compareTo(exchangeCoin.getMaxBuyPrice()) > 0) || type == ExchangeOrderType.MARKET_PRICE)) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("PRICE_CEILING") + exchangeCoin.getMaxBuyPrice());
        }
        //查看是否启用市价买卖
        if (type == ExchangeOrderType.MARKET_PRICE) {
            if (exchangeCoin.getEnableMarketBuy() == BooleanEnum.IS_FALSE.getCode() && direction == ExchangeOrderDirection.BUY) {
                redisTemplate.delete(key);
                return MessageResult.error(500, msService.getMessage("NO_MARKET_PRICE_BUY"));
            } else if (exchangeCoin.getEnableMarketSell() == BooleanEnum.IS_FALSE.getCode() && direction == ExchangeOrderDirection.SELL) {
                redisTemplate.delete(key);
                return MessageResult.error(500, msService.getMessage("NO_MARKET_PRICE_SELL"));
            }
        }
        //限制委托数量
        if (exchangeCoin.getMaxTradingOrder() > 0 && orderService.findCurrentTradingCount(member.getId(), symbol, direction) >= exchangeCoin.getMaxTradingOrder()) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("MAXIMUM_QUANTITY") + exchangeCoin.getMaxTradingOrder());
        }

        // 抢购模式活动订单限制（用户无法在活动前下买单）
        long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
        // 抢购模式下，无法在活动开始前下买单，仅限于管理员下卖单
        if(exchangeCoin.getPublishType() == ExchangeCoinPublishType.QIANGGOU.getCode()) {
            // 抢购模式订单
            try {
                if(currentTime < dateTimeFormat.parse(exchangeCoin.getStartTime()).getTime()) {
                    if(direction == ExchangeOrderDirection.BUY) {
                        // 抢购未开始
                        if(currentTime < dateTimeFormat.parse(exchangeCoin.getStartTime()).getTime()) {
                            redisTemplate.delete(key);
                            return MessageResult.error(500, msService.getMessage("ACTIVITY_NOT_STARTED"));
                        }
                    }else {
                        // 此处2是管理员用户的ID
                        if(member.getId() != 2) {
                            redisTemplate.delete(key);
                            return MessageResult.error(500, msService.getMessage("UNABLE_TO_PLACE_BUY_ORDER"));
                        }
                    }
                }else {
                    // 活动进行期间，无法下卖单 + 无法下市价单
                    if(currentTime < dateTimeFormat.parse(exchangeCoin.getEndTime()).getTime()) {
                        if(direction == ExchangeOrderDirection.SELL) {
                            redisTemplate.delete(key);
                            return MessageResult.error(500, msService.getMessage("UNABLE_TO_PLACE_SELL_ORDER"));
                        }
                        if(type == ExchangeOrderType.MARKET_PRICE){
                            redisTemplate.delete(key);
                            return MessageResult.error(500, msService.getMessage("ITS_NOT_MARKETABLE"));
                        }
                    }else {
                        // 清盘期间，无法下单
                        if(currentTime < dateTimeFormat.parse(exchangeCoin.getClearTime()).getTime()) {
                            redisTemplate.delete(key);
                            return MessageResult.error(500, msService.getMessage("WINDING_UP"));
                        }
                    }
                }
            } catch (ParseException e) {
                redisTemplate.delete(key);
                e.printStackTrace();
                return MessageResult.error(500,msService.getMessage("EXAPI_UNKNOWN_ERROR0"));
            }
        }
        // 分摊模式活动订单限制(开始前任何人无法下单)
        if(exchangeCoin.getPublishType() == ExchangeCoinPublishType.FENTAN.getCode()) {
            try {

                if(currentTime < dateTimeFormat.parse(exchangeCoin.getStartTime()).getTime()) {
                    // UNABLE_TO_PLACE_BUY_ORDER
                    redisTemplate.delete(key);
                    return MessageResult.error(500, msService.getMessage("ACTIVITY_NOT_STARTED"));
                }else {
                    // 活动开始后且在结束前，无法下卖单 + 下单金额必须符合规定
                    if(currentTime < dateTimeFormat.parse(exchangeCoin.getEndTime()).getTime()) {
                        if(direction == ExchangeOrderDirection.SELL) {
                            redisTemplate.delete(key);
                            return MessageResult.error(500, msService.getMessage("ACTIVITY_STARTED_CANT_SELL"));
                        }else {
                            if(type == ExchangeOrderType.MARKET_PRICE) {
                                redisTemplate.delete(key);
                                return MessageResult.error(500, msService.getMessage("ITS_NOT_MARKETABLE"));
                            }else {
                                if(price.compareTo(exchangeCoin.getPublishPrice()) != 0) {
                                    redisTemplate.delete(key);
                                    return MessageResult.error(500, msService.getMessage("ORDER_PRICE")+exchangeCoin.getPublishPrice());
                                }
                            }
                        }
                    }else {
                        // 清盘期间，普通用户无法下单，仅有管理员用户ID可下单
                        if(currentTime < dateTimeFormat.parse(exchangeCoin.getClearTime()).getTime()) {
                            // 此处2和10001是管理员用户的ID
                            if(member.getId() != 2 ) {
                                redisTemplate.delete(key);
                                return MessageResult.error(500, msService.getMessage("WINDING_UP"));
                            }else {
                                if(price.compareTo(exchangeCoin.getPublishPrice()) != 0) {
                                    redisTemplate.delete(key);
                                    return MessageResult.error(500, msService.getMessage("ORDER_PRICE")+exchangeCoin.getPublishPrice());
                                }
                                if(direction == ExchangeOrderDirection.BUY) {
                                    redisTemplate.delete(key);
                                    return MessageResult.error(500, msService.getMessage("PERIOD_LIQUIDATION"));
                                }
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                redisTemplate.delete(key);
                e.printStackTrace();
                return MessageResult.error(500,msService.getMessage("EXAPI_UNKNOWN_ERROR1"));
            }
        }
        order.setMemberId(member.getId());
        order.setSymbol(symbol);
        order.setBaseSymbol(baseCoin);
        order.setCoinSymbol(exCoin);
        order.setType(type);
        order.setDirection(direction);
        if(order.getType() == ExchangeOrderType.MARKET_PRICE){
            order.setPrice(BigDecimal.ZERO);
        }
        else{
            order.setPrice(price);
        }
        order.setUseDiscount("0");
        //限价买入单时amount为用户设置的总成交额
        order.setAmount(amount);

        MessageResult mr = orderService.addOrder(member.getId(), order);
        if (mr.getCode() != 0) {
            redisTemplate.delete(key);
            return MessageResult.error(500, msService.getMessage("ORDER_FAILED") + mr.getMessage());
        }
        log.info(">>>>>>>>>>订单提交完成>>>>>>>>>>");
        // 发送消息至Exchange系统
        rocketMQTemplate.convertAndSend("exchange-order", JSON.toJSONString(order));
        MessageResult result = MessageResult.success(msService.getMessage("SWAP_SUCCESS"));
        result.setData(order.getOrderId());
        redisTemplate.delete(key);
        return result;
    }

    /**
     * 个人中心当前委托
     * @param memberId
     * @param symbol
     * @param typeCode
     * @param startTime
     * @param endTime
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "个人中心当前委托")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
            @ApiImplicitParam(name = "type", value = "0 市价 1 限价"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
            @ApiImplicitParam(name = "direction", value = "方向 0：买  1：卖"),

    })
    @RequestMapping("personal/current")
    public Page<ExchangeOrder> personalCurrentOrder(@RequestParam("memberId") Long memberId,
                                                    @RequestParam(value = "symbol",required = false) String symbol,
                                                    @RequestParam(value = "type",required = false) Integer typeCode,
                                                    @RequestParam(value = "startTime",required = false) String startTime,
                                                    @RequestParam(value = "endTime",required = false) String endTime,
                                                    @RequestParam(value = "direction",required = false) Integer directionCode,
                                                    @RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
                                                    @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        ExchangeOrderType type = typeCode!=null ? ExchangeOrderType.creator(typeCode):null;
        ExchangeOrderDirection direction = directionCode!=null ? ExchangeOrderDirection.creator(directionCode):null;
        Page<ExchangeOrder> page = orderService.findPersonalCurrent(memberId, symbol,type,startTime,endTime, direction, pageNo, pageSize);
        page.getRecords().forEach(exchangeOrder -> {
            //获取交易成交详情
            BigDecimal tradedAmount = BigDecimal.ZERO;
            BigDecimal turnover = BigDecimal.ZERO;
            BigDecimal fee = BigDecimal.ZERO;
            List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(exchangeOrder.getOrderId());
            exchangeOrder.setDetail(details);
            for (ExchangeOrderDetail trade : details) {
                tradedAmount = tradedAmount.add(trade.getAmount());
                turnover = turnover.add(trade.getTurnover());
                fee=fee.add(trade.getFee()==null?BigDecimal.ZERO:trade.getFee());
            }
            exchangeOrder.setTradedAmount(tradedAmount);
            exchangeOrder.setTurnover(turnover);
            exchangeOrder.setFee(fee);
        });
        return page;
    }

    @ApiOperation(value = "取消委托")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单id"),
    })
    @RequestMapping("cancelOrder4API")
    public MessageResult cancelOrder4API(@RequestParam("memberId") Long memberId,@RequestParam("orderId") String orderId) {
        ExchangeOrder order = orderService.getById(orderId);
        if (!order.getMemberId().equals(memberId)) {
            return MessageResult.error(500, msService.getMessage("OPERATION_FORBIDDEN"));
        }
        if (order.getStatus() != ExchangeOrderStatus.TRADING) {
            return MessageResult.error(500, msService.getMessage("ORDER_STATUS_ERROR"));
        }
        // 活动清盘期间，无法撤销订单
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(order.getSymbol());
        if(exchangeCoin.getPublishType() != ExchangeCoinPublishType.NONE.getCode()) {
            long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
            try {
                // 处在活动结束时间与清盘结束时间之间
                if(currentTime > dateTimeFormat.parse(exchangeCoin.getEndTime()).getTime() &&
                        currentTime < dateTimeFormat.parse(exchangeCoin.getClearTime()).getTime()) {
                    return MessageResult.error(500, msService.getMessage("CANNOT_CANCEL_ORDER"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return MessageResult.error(500, msService.getMessage("EXAPI_UNKNOWN_ERROR3"));
            }
        }
        if(isExchangeOrderExist(order)){
            if (maxCancelTimes > 0 && orderService.findTodayOrderCancelTimes(memberId, order.getSymbol()) >= maxCancelTimes) {
                return MessageResult.error(500, msService.getMessage("CANCELLED") + maxCancelTimes + msService.getMessage("SECOND"));
            }
            // 发送消息至Exchange系统
            rocketMQTemplate.convertAndSend("exchange-order-cancel",JSON.toJSONString(order));
        }
        else{
            //强制取消
            orderService.forceCancelOrder(order);
        }
        return MessageResult.success(msService.getMessage("EXAPI_SUCCESS"));
    }


    /**
     * 个人中心历史委托
     */
    @ApiOperation(value = "个人中心历史委托")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对符号"),
            @ApiImplicitParam(name = "type", value = "0 市价 1 限价"),
            @ApiImplicitParam(name = "status", value = "交易状态"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
            @ApiImplicitParam(name = "direction", value = "方向 0：买  1：卖"),

    })
    @PermissionOperation
    @RequestMapping("personal/history")
    public Page<ExchangeOrder> personalHistoryOrder(
            @RequestParam("memberId") Long memberId,
            @RequestParam(value = "symbol" ,required = false) String symbol,
            @RequestParam(value = "type",required = false) Integer typeCode,
            @RequestParam(value = "status" ,required = false) Integer statusCode,
            @RequestParam(value = "startTime",required = false) String startTime,
            @RequestParam(value = "endTime",required = false) String endTime,
            @RequestParam(value = "direction",required = false) Integer directionCode,
            @RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {

        ExchangeOrderType type = typeCode!=null ? ExchangeOrderType.creator(typeCode):null;
        ExchangeOrderDirection direction = directionCode!=null ? ExchangeOrderDirection.creator(directionCode):null;
        ExchangeOrderStatus status = statusCode!=null ? ExchangeOrderStatus.creator(statusCode):null;

        Page<ExchangeOrder> page = orderService.findPersonalHistory(memberId, symbol, type, status, startTime, endTime,direction, pageNo, pageSize);

        page.getRecords().forEach(exchangeOrder -> {
            //获取交易成交详情
            BigDecimal fee = BigDecimal.ZERO;
            List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(exchangeOrder.getOrderId());
            if(details!=null && details.size()>0){
                for (ExchangeOrderDetail detail : details) {
                    fee=fee.add(detail.getFee()==null?BigDecimal.ZERO:detail.getFee());
                }
            }
            exchangeOrder.setDetail(exchangeOrderDetailService.findAllByOrderId(exchangeOrder.getOrderId()));
            exchangeOrder.setFee(fee);
        });
        return page;
    }

}
