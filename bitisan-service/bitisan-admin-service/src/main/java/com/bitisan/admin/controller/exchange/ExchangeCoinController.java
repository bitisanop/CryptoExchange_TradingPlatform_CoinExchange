package com.bitisan.admin.controller.exchange;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.Admin;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.BooleanEnum;
import com.bitisan.constant.ExchangeOrderStatus;
import com.bitisan.constant.SysConstant;
import com.bitisan.exchange.entity.ExchangeCoin;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.feign.ExchangeCoinFeign;
import com.bitisan.exchange.feign.ExchangeOrderFeign;
import com.bitisan.exchange.feign.MonitorFeign;
import com.bitisan.market.feign.MarketFeign;
import com.bitisan.robot.normal.entity.CustomRobotKline;
import com.bitisan.robot.normal.entity.RobotParams;
import com.bitisan.robot.normal.feign.RobotNormalFeign;
import com.bitisan.screen.ExchangeCoinScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.util.MD5;
import com.bitisan.util.MessageResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.baomidou.mybatisplus.core.toolkit.Assert.notNull;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 币币交易手续费
 * @date 2019/1/19 15:16
 */
@RestController
@RequestMapping("exchange/exchange-coin")
public class ExchangeCoinController extends BaseAdminController {

    private Logger logger = LoggerFactory.getLogger(ExchangeCoinController.class);

    @Value("${spark.system.md5.key}")
    private String md5Key;

    @Autowired
    private LocaleMessageSourceService messageSource;
    @Autowired
    private MonitorFeign monitorFeign;
    @Autowired
    private MarketFeign marketFeign;

    @Autowired
    private ExchangeCoinFeign exchangeCoinService;

    @Autowired
    private ExchangeOrderFeign exchangeOrderService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private CoinFeign coinService;
    @Autowired
    private RobotNormalFeign robotNormalFeign;

    @RequiresPermissions("exchange:exchange-coin:merge")
    @PostMapping("merge")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "新增币币交易对exchangeCoin")
    public MessageResult ExchangeCoinList(
            @Valid ExchangeCoin exchangeCoin) {
        logger.info("Add exchange coin: " + JSON.toJSONString(exchangeCoin));

        ExchangeCoin findResult = exchangeCoinService.findBySymbol(exchangeCoin.getSymbol());
        if(findResult != null) {
            return error("[" + exchangeCoin.getSymbol() + "]" + messageSource.getMessage("PAIR_ALREADY_EXISTS"));
        }
        Coin c1 = coinService.findByUnit(exchangeCoin.getBaseSymbol());
        if(c1 == null) {
            return error("[" + exchangeCoin.getBaseSymbol() + "]" + messageSource.getMessage("SETTLEMENT_CURRENCY_NOT_FOUND"));
        }
        Coin c2 = coinService.findByUnit(exchangeCoin.getCoinSymbol());
        if(c2 == null) {
            return error("[" + exchangeCoin.getCoinSymbol() + "]" + messageSource.getMessage("TRADING_CURRENCY_NOT_FOUND"));
        }
        exchangeCoin = exchangeCoinService.save(exchangeCoin);
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), exchangeCoin);
    }

    @RequiresPermissions("exchange:exchange-coin:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "分页查找币币交易手续费exchangeCoin")
    public MessageResult ExchangeCoinList(ExchangeCoinScreen screen) {
        Page<ExchangeCoin> all = exchangeCoinService.findAll(screen);

        //远程RPC服务URL,获取当前交易引擎支持的币种
        Map<String, Integer> engineSymbols = monitorFeign.engines();
        for(ExchangeCoin item : all.getRecords()) {
            if(engineSymbols != null && engineSymbols.containsKey(item.getSymbol())) {
                item.setEngineStatus(engineSymbols.get(item.getSymbol())); // 1: 运行中  2:暂停中
            }else {
                item.setEngineStatus(0); // 0:不可用
            }
            item.setCurrentTime(Calendar.getInstance().getTimeInMillis());
        }

        Map<String, Integer> marketEngineSymbols = marketFeign.engines();

        for(ExchangeCoin item : all.getRecords()) {
            // 行情引擎
            if(marketEngineSymbols != null && marketEngineSymbols.containsKey(item.getSymbol())) {
                item.setMarketEngineStatus(marketEngineSymbols.get(item.getSymbol()));
            }else {
                item.setMarketEngineStatus(0);
            }

            // 机器人
            if(this.isRobotExists(item)) {
                item.setExEngineStatus(1);
            }else {
                item.setExEngineStatus(0);
            }
        }
        return success(IPage2Page(all));
    }

    /**
     * 查看交易对详情
     * @param symbol
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "币币交易对exchangeCoin 详情")
    public MessageResult detail(
            @RequestParam(value = "symbol") String symbol) {
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");
        return success(exchangeCoin);
    }

    @RequiresPermissions("exchange:exchange-coin:deletes")
    @PostMapping("deletes")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "币币交易对exchangeCoin 删除")
    public MessageResult deletes(
            @RequestParam(value = "ids") String[] ids) {
        // 检查是否有未成交订单
        String coins = "";
        for(String id: ids) {
            ExchangeCoin temCoin = exchangeCoinService.findBySymbol(id);
            notNull(temCoin, "ID=" + id + messageSource.getMessage("PAIR_ALREADY_EXISTS"));
            List<ExchangeOrder> orders = exchangeOrderService.findAllTradingOrderBySymbol(temCoin.getSymbol());
            if(orders.size() > 0) {
                return error(temCoin.getSymbol() + messageSource.getMessage("PAIR_STILL_HAS") + orders.size() + messageSource.getMessage("UNEXECUTED_ORDERS_CANCEL_BEFORE_DELETE"));
            }
            if(temCoin.getEnable() == 1 || temCoin.getExchangeable() == 1) {
                return error(messageSource.getMessage("PLEASE_COLSE") + temCoin.getSymbol() + messageSource.getMessage("TRADING_ENGINE_AND_SET_PAIR_STATUS"));
            }
            coins += temCoin.getSymbol() + ",";
        }
        logger.info("Delete exchange coin: " + coins.substring(0, coins.length()-1));
        exchangeCoinService.deletes(ids);
        return success(messageSource.getMessage("SUCCESS"));
    }

    @RequiresPermissions("exchange:exchange-coin:alter-rate")
    @PostMapping("alter-rate")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "修改币币交易对exchangeCoin")
    public MessageResult alterExchangeCoinRate(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "fee", required = false) BigDecimal fee,
            @RequestParam(value = "maxBuyPrice", required = false) BigDecimal maxBuyPrice,
            @RequestParam(value = "minTurnover", required = false) BigDecimal minTurnover,
            @RequestParam(value = "enable", required = false) Integer enable, // 上下架（1:上,2）
            @RequestParam(value = "visible", required = false) Integer visible, // 是否显示（1:是,2）
            @RequestParam(value = "exchangeable", required = false) Integer exchangeable, // 是否可交易(1:是,2)
            @RequestParam(value = "enableMarketBuy", required = false) Integer enableMarketBuy, // 是否可市价买（1:是,0:否）
            @RequestParam(value = "enableMarketSell", required = false) Integer enableMarketSell, // 是否可市价买（1:是,0:否）
            @RequestParam(value = "enableBuy", required = false) Integer enableBuy, // 是否可买（1:是,0:否）
            @RequestParam(value = "enableSell", required = false) Integer enableSell, // 是否可卖（1:是,0:否）
            @RequestParam(value = "sort", required = false) Integer sort,
            @RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) throws Exception {
        password = MD5.md5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");
        if (fee != null) {
            exchangeCoin.setFee(fee);//修改手续费
        }
        if(minTurnover != null) {
            exchangeCoin.setMinTurnover(minTurnover);
        }
        if(maxBuyPrice != null) {
            exchangeCoin.setMaxBuyPrice(maxBuyPrice);
        }
        if (sort != null) {
            exchangeCoin.setSort(sort);//设置排序
        }
        if (enable != null && enable > 0 && enable < 3) {
            exchangeCoin.setEnable(enable);//设置启用 禁用
        }
        if(visible != null && visible > 0 && visible <3) {
            exchangeCoin.setVisible(visible);
        }
        if(exchangeable != null && exchangeable > 0 && exchangeable < 3) {
            exchangeCoin.setExchangeable(exchangeable);
        }
        if(enableMarketBuy != null && enableMarketBuy >= 0 && enableMarketBuy <2) {
            exchangeCoin.setEnableMarketBuy(enableMarketBuy==1 ? BooleanEnum.IS_TRUE.getCode() : BooleanEnum.IS_FALSE.getCode());
        }
        if(enableMarketSell != null && enableMarketSell >= 0 && enableMarketSell <2) {
            exchangeCoin.setEnableMarketSell(enableMarketSell==1 ? BooleanEnum.IS_TRUE.getCode() : BooleanEnum.IS_FALSE.getCode());
        }
        if(enableBuy != null && enableBuy >= 0 && enableBuy <2) {
            exchangeCoin.setEnableBuy(enableBuy==1 ? BooleanEnum.IS_TRUE.getCode() : BooleanEnum.IS_FALSE.getCode());
        }
        if(enableSell != null && enableSell >= 0 && enableSell <2) {
            exchangeCoin.setEnableSell(enableSell ==1 ? BooleanEnum.IS_TRUE.getCode() : BooleanEnum.IS_FALSE.getCode());
        }
        logger.info("Modify exchange coin: " + symbol);
        exchangeCoinService.save(exchangeCoin);
        return success(messageSource.getMessage("SUCCESS"));
    }

    /**
     * 启动交易引擎（若不存在，则创建）
     * @param symbol
     * @param password
     * @param admin
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:start-trader")
    @PostMapping("start-trader")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "启动交易引擎")
    public MessageResult startExchangeCoinEngine(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) throws Exception{
        password = MD5.md5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");

        if(exchangeCoin.getEnable() != 1) {
            return MessageResult.error(500, messageSource.getMessage("PLEASE_ENABLE_PAIR_BEFORE_TRADING"));
        }

        MessageResult result = monitorFeign.startTrader(symbol);

        if(result.getCode() == 0) {
            logger.info("Start exchange engine successful: " + symbol);
            return success(messageSource.getMessage("SUCCESS"));
        }else {
            logger.info("Start exchange engine failed: " + symbol);
            return error(result.getMessage());
        }
    }

    /**
     * 停止交易引擎（若不存在，则创建）
     * @param symbol
     * @param password
     * @param admin
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:stop-trader")
    @PostMapping("stop-trader")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "暂停交易引擎")
    public MessageResult stopExchangeCoinEngine(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) throws Exception {
        password = MD5.md5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");

        if(exchangeCoin.getExchangeable() != 2) {
            return MessageResult.error(500, messageSource.getMessage("PLEASE_SET_PAIR_NOT_TRADING"));
        }
        MessageResult result = monitorFeign.stopTrader(symbol);
        if(result.getCode() == 0) {
            logger.info("Stop exchange engine successful: " + symbol);
            return success(messageSource.getMessage("SUCCESS"));
        }else {
            logger.info("Stop exchange engine failed: " + symbol);
            return error(result.getMessage());
        }
    }

    /**
     * 重置引擎
     * @param symbol
     * @param password
     * @param admin
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:reset-trader")
    @PostMapping("reset-trader")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "重置交易引擎")
    public MessageResult resetExchangeCoinEngine(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) throws Exception {
        password = MD5.md5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");

        if(exchangeCoin.getExchangeable() != 1) {
            return MessageResult.error(500, messageSource.getMessage("PLEASE_SET_PAIR_TRADING"));
        }
        MessageResult result = monitorFeign.resetTrader(symbol);

        if(result.getCode() == 0) {
            logger.info("Reset exchange engine successful: " + symbol);
            return success(messageSource.getMessage("SUCCESS"));
        }else {
            logger.info("Reset exchange engine failed: " + symbol);
            return error(result.getMessage());
        }
    }

//    @RequiresPermissions("exchange:exchange-coin:out-excel")
//    @GetMapping("out-excel")
//    @AccessLog(module = AdminModule.EXCHANGE, operation = "导出币币交易手续费exchangeCoin Excel")
//    public MessageResult outExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        List all = exchangeCoinService.findAll();
//        return new FileUtil().exportExcel(request, response, all, "exchangeCoin");
//    }

    /**
     * 获取所有交易区币种的单位
     *
     * @return
     */
    @PostMapping("all-base-symbol-units")
    public MessageResult getAllBaseSymbolUnits() {
        List<String> list = exchangeCoinService.getBaseSymbol();
        return success(messageSource.getMessage("SUCCESS"), list);
    }

    /**
     * 获取交易区币种 所支持的交易 币种
     *
     * @return
     */
    @PostMapping("all-coin-symbol-units")
    public MessageResult getAllCoinSymbolUnits(@RequestParam("baseSymbol") String baseSymbol) {
        List<String> list = exchangeCoinService.getCoinSymbol(baseSymbol);
        return success(messageSource.getMessage("SUCCESS"), list);
    }

    /**
     * 取消某交易对所有订单
     * @param symbol
     * @param password
     * @param admin
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:cancel-all-order")
    @PostMapping("cancel-all-order")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "撤销某交易对所有委托exchangeCoin")
    public MessageResult cancelAllOrderBySymbol(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) throws Exception {
        password = MD5.md5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");
        if(exchangeCoin.getExchangeable() != 2) {
            return MessageResult.error(500, messageSource.getMessage("PLEASE_SET_PAIR_NOT_TRADING"));
        }
        List<ExchangeOrder> orders = exchangeOrderService.findAllTradingOrderBySymbol(symbol);
        List<ExchangeOrder> cancelOrders = new ArrayList<ExchangeOrder>();
        for(ExchangeOrder order : orders) {
            if (order.getStatus() != ExchangeOrderStatus.TRADING) {
                continue;
            }
            if(isExchangeOrderExist(order)){
                logger.info("Cancel exchange order: (" + symbol + ") " + JSON.toJSONString(orders));

                rocketMQTemplate.convertAndSend("exchange-order-cancel",JSON.toJSONString(order));
                cancelOrders.add(order);
            }else {
                //强制取消
                exchangeOrderService.forceCancelOrder(order);
            }
        }

        return success(messageSource.getMessage("UNEXECUTED_ORDER_COUNT") + ":" + orders.size() + "," + messageSource.getMessage("SUCCESSFULLY_CANCELED") + ":"+cancelOrders.size(), cancelOrders);
    }

    /**
     * 查看交易对交易盘面详情（卖盘、买盘等）
     * @param symbol
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:exchange-overview")
    @PostMapping("exchange-overview")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "查看交易对交易盘面详情")
    public MessageResult overviewExchangeCoin(@RequestParam("symbol") String symbol) {

        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");

        JSONObject result = monitorFeign.traderOverview(symbol);
        logger.info("Overview exchange coin: " + symbol);
        return success(messageSource.getMessage("SUCCESS"), result);
    }

    /**
     * 查看交易对机器人参数
     * @param symbol
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:robot-config")
    @RequestMapping("robot-config")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "查看交易对机器人参数")
    public MessageResult getRobotConfig(@RequestParam("symbol") String symbol) {

        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");
        if(exchangeCoin.getRobotType() == 0 || exchangeCoin.getRobotType() == 1) {
            try {
                MessageResult result = robotNormalFeign.getRobotParams(symbol);
                if(result.getCode() == 0) {
                    return success(messageSource.getMessage("SUCCESS"), result.getData());
                }else {
                    return error(messageSource.getMessage("GET_ROBOT_PARAMETERS_FAILED_NO_ROBOT_OR_ROBOT_STOPPED"));
                }
            }catch(Exception e) {
                e.printStackTrace();
                return error(messageSource.getMessage("GET_ROBOT_PARAMETERS_FAILED_NO_ROBOT_OR_ROBOT_STOPPED"));
            }
        }
//        else if(exchangeCoin.getRobotType() == 1) { // 独立出来，后面修改控盘机器人方便一些，其实代码现在是一样的
//            String serviceName = "ROBOT-TRADE-NORMAL";
//            String contextPath = "/ernormal";
//            String url = "http://" + serviceName + "/ernormal/getRobotParams?coinName=" + symbol;
//            try {
//                ResponseEntity<JSONObject> resultStr = restTemplate.getForEntity(url, JSONObject.class);
//                logger.info("Get robot config: " + resultStr.toString());
//                JSONObject result = (JSONObject)resultStr.getBody();
//                if(result.getIntValue("code") == 0) {
//                    return success(messageSource.getMessage("SUCCESS"), result.getJSONObject("data"));
//                }else {
//                    return error("获取机器人参数失败（该交易对无机器人或机器人意外停止）！");
//                }
//            }catch(Exception e) {
//                e.printStackTrace();
//                return error("获取机器人参数失败（该交易对无机器人或机器人意外停止）！");
//            }
//        }
        else if(exchangeCoin.getRobotType() == 2) {
            // 控盘机器人
            return null;
        }else {
            return null;
        }

    }

    /**
     * 检测是否存在交易机器人
     * @param coin
     * @return
     */
    private boolean isRobotExists(ExchangeCoin coin) {
        if(coin.getRobotType() == 0 || coin.getRobotType() == 1) {
            try {
                MessageResult result = robotNormalFeign.getRobotParams(coin.getSymbol());
                if(result.getCode() == 0) {
                    return true;
                }else {
                    return false;
                }
            }catch(Exception e) {
                e.printStackTrace();
                return false;
            }
        }
//        else if(coin.getRobotType() == 1){ // 独立出来，后面修改控盘机器人方便一些，其实代码现在是一样的
//            String serviceName = "ROBOT-TRADE-NORMAL"; // 控盘机器人也通过此处进行控制
//            String url = "http://" + serviceName + "/ernormal/getRobotParams?coinName=" + coin.getSymbol();
//            try {
//                ResponseEntity<JSONObject> resultStr = restTemplate.getForEntity(url, JSONObject.class);
//                logger.info("Get robot config: " + resultStr.toString());
//                JSONObject result = (JSONObject)resultStr.getBody();
//                if(result.getIntValue("code") == 0) {
//                    return true;
//                }else {
//                    return false;
//                }
//            }catch(Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
        else if(coin.getRobotType() == 2) {
            return false;
        }else {
            return false;
        }
    }

    /**
     * 新建/修改交易机器人参数（一般机器人）
     * @param symbol
     * @param startAmount
     * @param randRange0
     * @param randRange1
     * @param randRange2
     * @param randRange3
     * @param randRange4
     * @param randRange5
     * @param randRange6
     * @param scale
     * @param amountScale
     * @param maxSubPrice
     * @param initOrderCount
     * @param priceStepRate
     * @param runTime
     * @param admin
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:alter-robot-config")
    @PostMapping("alter-robot-config")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "修改交易对机器人参数")
    public MessageResult alterRobotConfig(
            @RequestParam("symbol") String symbol,
            @RequestParam("isHalt") Integer isHalt,
            @RequestParam("startAmount") Double startAmount,
            @RequestParam("randRange0") Double randRange0,
            @RequestParam("randRange1") Double randRange1,
            @RequestParam("randRange2") Double randRange2,
            @RequestParam("randRange3") Double randRange3,
            @RequestParam("randRange4") Double randRange4,
            @RequestParam("randRange5") Double randRange5,
            @RequestParam("randRange6") Double randRange6,
            @RequestParam("scale") Integer scale,
            @RequestParam("amountScale") Integer amountScale,
            @RequestParam("maxSubPrice") BigDecimal maxSubPrice,
            @RequestParam("initOrderCount") Integer initOrderCount,
            @RequestParam("priceStepRate") BigDecimal priceStepRate,
            @RequestParam("runTime") Integer runTime,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");
        // 一般机器人和控盘机器人
        if(exchangeCoin.getRobotType() == 0 || exchangeCoin.getRobotType() == 1) {
            RobotParams params = new RobotParams();
            params.setCoinName(symbol);
            if(isHalt.intValue() == 0) {
                params.setHalt(false);
            }else {
                params.setHalt(true);
            }
            params.setStartAmount(startAmount);
            params.setRandRange0(randRange0);
            params.setRandRange1(randRange1);
            params.setRandRange2(randRange2);
            params.setRandRange3(randRange3);
            params.setRandRange4(randRange4);
            params.setRandRange5(randRange5);
            params.setRandRange6(randRange6);
            params.setScale(scale);
            params.setAmountScale(amountScale);
            params.setMaxSubPrice(maxSubPrice);
            params.setInitOrderCount(initOrderCount);
            params.setPriceStepRate(priceStepRate);
            params.setRunTime(runTime);
            params.setRobotType(exchangeCoin.getRobotType());

            // 获取控盘机器人策略
            try {
                MessageResult<RobotParams> result = robotNormalFeign.getRobotParams(symbol);
                if(result.getCode() == 0) {
                    RobotParams robotParams = result.getData();
                    params.setStrategyType(robotParams.getStrategyType());
                    params.setFlowPair(robotParams.getFlowPair());
                    params.setFlowPercent(robotParams.getFlowPercent());
                }else {
                    return error(messageSource.getMessage("GET_ROBOT_PARAMETERS_FAILED_NO_ROBOT_OR_ROBOT_STOPPED"));
                }
            }catch(Exception e) {
                e.printStackTrace();
                return error(messageSource.getMessage("GET_ROBOT_PARAMETERS_FAILED_NO_ROBOT_OR_ROBOT_STOPPED"));
            }
            try {
                MessageResult messageResult = robotNormalFeign.setRobotParams(params);
                if(messageResult.getCode() == 0) {
                    return success(messageSource.getMessage("SUCCESS"), messageResult);
                }else {
                    return error(messageSource.getMessage("MODIFY_ROBOT_PARAMETERS_FAILED_NO_ROBOT_OR_ROBOT_STOPPED"));
                }
            }catch(Exception e) {
                return error(messageSource.getMessage("MODIFY_ROBOT_PARAMETERS_FAILED_NO_ROBOT_OR_ROBOT_STOPPED"));
            }
        }else {
            return error(messageSource.getMessage("MODIFY_ROBOT_PARAMETERS_FAILED_NOT_GENERAL_ROBOT"));
        }
    }

    @RequiresPermissions("exchange:exchange-coin:create-robot-config")
    @PostMapping("create-robot-config")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "修改交易对机器人参数")
    public MessageResult createRobotConfig(
            @RequestParam("symbol") String symbol,
            @RequestParam("isHalt") Integer isHalt,
            @RequestParam("startAmount") Double startAmount,
            @RequestParam("randRange0") Double randRange0,
            @RequestParam("randRange1") Double randRange1,
            @RequestParam("randRange2") Double randRange2,
            @RequestParam("randRange3") Double randRange3,
            @RequestParam("randRange4") Double randRange4,
            @RequestParam("randRange5") Double randRange5,
            @RequestParam("randRange6") Double randRange6,
            @RequestParam("scale") Integer scale,
            @RequestParam("amountScale") Integer amountScale,
            @RequestParam("maxSubPrice") BigDecimal maxSubPrice,
            @RequestParam("initOrderCount") Integer initOrderCount,
            @RequestParam("priceStepRate") BigDecimal priceStepRate,
            @RequestParam("runTime") Integer runTime,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");
        // 一般机器人和控盘机器人
        if(exchangeCoin.getRobotType() == 0 || exchangeCoin.getRobotType() == 1) {
            RobotParams params = new RobotParams();
            params.setCoinName(symbol);
            if(isHalt.intValue() == 0) {
                params.setHalt(false);
            }else {
                params.setHalt(true);
            }
            params.setStartAmount(startAmount);
            params.setRandRange0(randRange0);
            params.setRandRange1(randRange1);
            params.setRandRange2(randRange2);
            params.setRandRange3(randRange3);
            params.setRandRange4(randRange4);
            params.setRandRange5(randRange5);
            params.setRandRange6(randRange6);
            params.setScale(scale);
            params.setAmountScale(amountScale);
            params.setMaxSubPrice(maxSubPrice);
            params.setInitOrderCount(initOrderCount);
            params.setPriceStepRate(priceStepRate);
            params.setRunTime(runTime);
            params.setRobotType(exchangeCoin.getRobotType());
            params.setStrategyType(2); // 默认自定义
            params.setFlowPair("BTC/USDT"); // 默认BTC/USDT
            params.setFlowPercent(BigDecimal.valueOf(1));
            MessageResult result = null;

            try {
                if(exchangeCoin.getRobotType() == 1) {
                    result = robotNormalFeign.createCustomRobot(params);
                }else {
                    result = robotNormalFeign.createRobot(params);
                }
                if(result.getCode() == 0) {
                    return success(messageSource.getMessage("SUCCESS"), result);
                }else {
                    return error(messageSource.getMessage("CREATION_FAILED") + ":" + result.getMessage());
                }
            }catch(Exception e) {
                return error(messageSource.getMessage("CREATE_ROBOT_FAILED_NOT_GENERAL_ROBOT"));
            }
        }else {
            return error(messageSource.getMessage("CREATE_ROBOT_FAILED_NOT_GENERAL_ROBOT"));
        }
    }

    public boolean isExchangeOrderExist(ExchangeOrder order){
        try {
            ExchangeOrder result = monitorFeign.findOrder(order);
            return result != null;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存机器人行情趋势线
     * @param symbol
     * @param kdate
     * @param kline
     * @param pricePencent 价格浮动允许范围
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:save-robot-kline")
    @PostMapping("save-robot-kline")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "保存机器人行情趋势线")
    public MessageResult createRobotKlineData(@RequestParam("symbol") String symbol,
                                              @RequestParam("kdate") String kdate,
                                              @RequestParam("kline") String kline,
                                              @RequestParam("pricePencent") Integer pricePencent) {
        // 检查币种
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");
        if(exchangeCoin.getRobotType() != 1) {
            return error(messageSource.getMessage("PAIR_NOT_GENERAL_ROBOT"));
        }
        if(kdate.equals("") || kdate.length() < 10) {
            return error(messageSource.getMessage("INVALID_DATE_INPUT"));
        }
        kdate = kdate.substring(0, 10); // 前台传的形式是：2020-12-01T16:00:00.000Z

        // 保存
        try {
            MessageResult result = robotNormalFeign.setRobotStrategy(symbol, 2, "BTC/USDT", BigDecimal.ONE);
            if(result.getCode() == 0) {
                // do nothing
            }else {
                return error(messageSource.getMessage("PLEASE_CREATE_ROBOT_FIRST"));
            }
        }catch(Exception e) {
            return error(messageSource.getMessage("SAVE_FAILED"));
        }

        // 构造参数
        CustomRobotKline params = new CustomRobotKline();
        params.setCoinName(symbol);
        params.setKdate(kdate);
        params.setKline(kline);
        params.setPricePencent(pricePencent);

//        serviceName = "ROBOT-TRADE-NORMAL";
//        url = "http://" + serviceName + "/ernormal/saveKline";
        try {
//            ResponseEntity<JSONObject> resultStr = restTemplate.postForEntity(url, params, JSONObject.class);
//            logger.info("save robot kline: " + resultStr.toString());
//            JSONObject result = (JSONObject)resultStr.getBody();

            MessageResult result = robotNormalFeign.saveKline(params);

            if(result.getCode() == 0) {
                return success(messageSource.getMessage("SUCCESS"), result);
            }else {
                return error(messageSource.getMessage("CREATION_FAILED") + ":" + result.getMessage());
            }
        }catch(Exception e) {
            return error(messageSource.getMessage("CREATE_ROBOT_FAILED_NO_ROBOT_OR_ROBOT_STOPPED"));
        }
    }


    /**
     *
     * @param symbol
     * @param pair
     * @param flowPercent
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:save-robot-flow")
    @PostMapping("save-robot-flow")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "设置跟随型控盘趋势")
    public MessageResult createRobotFlow(@RequestParam("symbol") String symbol,
                                         @RequestParam("pair") String pair,
                                         @RequestParam("flowPercent") BigDecimal flowPercent) {
        // 检查币种
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");
        if(exchangeCoin.getRobotType() != 1) {
            return error(messageSource.getMessage("PAIR_NOT_GENERAL_ROBOT"));
        }
        if(StringUtils.isEmpty(pair)) {
            return error(messageSource.getMessage("PLEASE_SELECT_FOLLOWING_TRADING_PAIR"));
        }

        try {
            MessageResult result = robotNormalFeign.setRobotStrategy(symbol, 1, pair, flowPercent);
            if(result.getCode() == 0) {
                return success(messageSource.getMessage("SUCCESS"), result.getData());
            }else {
                logger.info("获取机器人K线参数失败");
                return error(messageSource.getMessage("GET_ROBOT_KLINE_PARAMETERS_FAILED_NO_ROBOT_OR_ROBOT_STOPPED"));
            }
        }catch(Exception e) {
            return error(messageSource.getMessage("SAVE_FAILED"));
        }
    }

    /**
     * 获取所有控盘交易对列表
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:custom-coin-list")
    @PostMapping("custom-coin-list")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "获取所有控盘交易对列表")
    public MessageResult customRobotCoinList() {
        List<ExchangeCoin> coinList = exchangeCoinService.findAllByRobotType(1);
        return success(coinList);
    }

    /**
     * 获取控盘机器人K线趋势参数列表（日期-数组）
     * @param symbol
     * @param kdate
     * @return
     */
    @RequiresPermissions("exchange:exchange-coin:robot-kline-list")
    @PostMapping("robot-kline-list")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "获取行情趋势线列表")
    public MessageResult RobotKlineDataList(@RequestParam("symbol") String symbol, @RequestParam("kdate") String kdate) {
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        notNull(exchangeCoin, "validate symbol!");
        if(exchangeCoin.getRobotType() != 1) {
            return error(messageSource.getMessage("PAIR_NOT_GENERAL_ROBOT"));
        }

        kdate = kdate.substring(0, 10); // 前台传的形式是：2020-12-01T16:00:00.000Z

        String currentDate = kdate;
        // 默认获取当前日期及以后的日期
        if(currentDate.equals("") || currentDate == null) {
            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            currentDate = sf.format(date);
        }

        try {
            MessageResult result = robotNormalFeign.getRobotKline(symbol, currentDate);
            if(result.getCode()== 0) {
                return success(messageSource.getMessage("SUCCESS"), result.getData());
            }else {
                logger.info("获取机器人K线参数失败");
                return error(messageSource.getMessage("GET_ROBOT_KLINE_PARAMETERS_FAILED_NO_ROBOT_OR_ROBOT_STOPPED"));
            }
        }catch(Exception e) {
            e.printStackTrace();
            return error(messageSource.getMessage("GET_ROBOT_KLINE_PARAMETERS_FAILED_NO_ROBOT_OR_ROBOT_STOPPED"));
        }
    }

//    /**
//     * 此处修改需要修改机器人交易工程RobotParams
//     * @author Bitisan  E-mail:bitisanop@gmail.com
//     *
//     */
//    class RobotParams {
//        private String coinName = ""; // 如btcusdt
//        private boolean isHalt = true; // 是否暂停状态
//        private double startAmount = 0.001; // 最低交易量
//        private double randRange0 = 20; // 交易量随机数范围 1%概率
//        private double randRange1 = 4; // 交易量随机数范围 9%概率
//        private double randRange2 = 1; //  交易量随机数范围0.1(0.0001 ~ 0.09) 20%概率
//        private double randRange3 = 0.1; // 交易量随机数范围0.1(0.0001 ~ 0.09) 20%概率
//        private double randRange4 = 0.01; // 交易量随机数范围0.1(0.0001 ~ 0.09) 20%概率
//        private double randRange5 = 0.001; // 交易量随机数范围0.1(0.0001 ~ 0.09) 20%概率
//        private double randRange6 = 0.0001; // 交易量随机数范围0.1(0.0001 ~ 0.09) 10%概率
//        private int scale = 4;//价格精度要求
//        private int amountScale = 6; // 数量精度要求
//        private BigDecimal maxSubPrice = new BigDecimal(20); // 买盘最高价与卖盘最低价相差超过20美金
//        private int initOrderCount = 30; // 初始订单数量（此数字必须大于24）
//        private BigDecimal priceStepRate = new BigDecimal(0.003); // 价格变化步长(0.01 = 1%)
//        private int runTime = 1000; // 行情请求间隔时间（5000 = 5秒）
//
//        private int robotType = 0; // 机器人类型
//        private int strategyType = 1; // 控盘机器人策略（1：跟随，2：自定义）
//        private String flowPair = "BTC/USDT"; // 跟随交易对
//        private BigDecimal flowPercent = BigDecimal.valueOf(1); // 跟随比例
//
//        public BigDecimal getFlowPercent() {
//            return flowPercent;
//        }
//
//        public void setFlowPercent(BigDecimal flowPercent) {
//            this.flowPercent = flowPercent;
//        }
//
//        public String getFlowPair() { return flowPair; }
//        public void setFlowPair(String flowPair) { this.flowPair = flowPair; }
//
//        public int getStrategyType() { return strategyType; }
//        public void setStrategyType(int strategyType) { this.strategyType = strategyType; }
//
//        public int getRobotType() {
//            return robotType;
//        }
//        public void setRobotType(int robotType) {
//            this.robotType = robotType;
//        }
//
//        public double getStartAmount() {
//            return startAmount;
//        }
//        public void setStartAmount(double startAmount) {
//            this.startAmount = startAmount;
//        }
//        public double getRandRange0() {
//            return randRange0;
//        }
//        public void setRandRange0(double randRange0) {
//            this.randRange0 = randRange0;
//        }
//        public double getRandRange1() {
//            return randRange1;
//        }
//        public void setRandRange1(double randRange1) {
//            this.randRange1 = randRange1;
//        }
//        public double getRandRange2() {
//            return randRange2;
//        }
//        public void setRandRange2(double randRange2) {
//            this.randRange2 = randRange2;
//        }
//        public double getRandRange3() {
//            return randRange3;
//        }
//        public void setRandRange3(double randRange3) {
//            this.randRange3 = randRange3;
//        }
//        public double getRandRange4() {
//            return randRange4;
//        }
//        public void setRandRange4(double randRange4) {
//            this.randRange4 = randRange4;
//        }
//        public double getRandRange5() {
//            return randRange5;
//        }
//        public void setRandRange5(double randRange5) {
//            this.randRange5 = randRange5;
//        }
//        public double getRandRange6() {
//            return randRange6;
//        }
//        public void setRandRange6(double randRange6) {
//            this.randRange6 = randRange6;
//        }
//        public int getScale() {
//            return scale;
//        }
//        public void setScale(int scale) {
//            this.scale = scale;
//        }
//        public int getAmountScale() {
//            return amountScale;
//        }
//        public void setAmountScale(int amountScale) {
//            this.amountScale = amountScale;
//        }
//        public BigDecimal getMaxSubPrice() {
//            return maxSubPrice;
//        }
//        public void setMaxSubPrice(BigDecimal maxSubPrice) {
//            this.maxSubPrice = maxSubPrice;
//        }
//        public int getInitOrderCount() {
//            return initOrderCount;
//        }
//        public void setInitOrderCount(int initOrderCount) {
//            this.initOrderCount = initOrderCount;
//        }
//        public BigDecimal getPriceStepRate() {
//            return priceStepRate;
//        }
//        public void setPriceStepRate(BigDecimal priceStepRate) {
//            this.priceStepRate = priceStepRate;
//        }
//        public int getRunTime() {
//            return runTime;
//        }
//        public void setRunTime(int runTime) {
//            this.runTime = runTime;
//        }
//        public String getCoinName() {
//            return coinName;
//        }
//        public void setCoinName(String coinName) {
//            this.coinName = coinName;
//        }
//        public boolean isHalt() {
//            return isHalt;
//        }
//        public void setHalt(boolean isHalt) {
//            this.isHalt = isHalt;
//        }
//    }

//    /**
//     * K线数据
//     * @author Bitisan  E-mail:bitisanop@gmail.com
//     *
//     */
//    class CustomRobotKline{
//        private String coinName = ""; // 交易对名称，如：xxxusdt
//        private String kdate = ""; // K线日期，如：2020/02/02
//        private String kline = ""; // K线数组JSON字符串
//        private int pricePencent = 0; // 价格浮动范围
//
//        public int getPricePencent() {
//            return pricePencent;
//        }
//        public void setPricePencent(int pricePencent) {
//            this.pricePencent = pricePencent;
//        }
//        public String getCoinName() {
//            return coinName;
//        }
//        public void setCoinName(String coinName) {
//            this.coinName = coinName;
//        }
//        public String getKdate() {
//            return kdate;
//        }
//        public void setKdate(String kdate) {
//            this.kdate = kdate;
//        }
//        public String getKline() {
//            return kline;
//        }
//        public void setKline(String kline) {
//            this.kline = kline;
//        }
//    }
}
