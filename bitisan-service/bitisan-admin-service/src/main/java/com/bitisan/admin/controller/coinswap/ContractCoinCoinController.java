package com.bitisan.admin.controller.coinswap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.coinswap.entity.ContractCoinCoin;
import com.bitisan.coinswap.feign.ContractCoinCoinFeign;
import com.bitisan.coinswap.feign.MemberContractCoinWalletFeign;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.BooleanEnum;
import com.bitisan.screen.PageParam;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/coinswap-coin")
@Slf4j
public class ContractCoinCoinController extends BaseAdminController {

    @Autowired
    private ContractCoinCoinFeign contractCoinCoinService;
    @Autowired
    private MemberContractCoinWalletFeign memberContractWalletFeign;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private LocaleMessageSourceService messageSource;

    /**
     * 获取永续合约交易对列表
     * @return
     */
    @RequiresPermissions("coinswap-coin:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "币本位合约交易对 列表")
    public MessageResult list(PageParam pageParam) {
        Page<ContractCoinCoin> coinList = contractCoinCoinService.findAll(pageParam);
        return success(IPage2Page(coinList));
    }

    /**
     * 获取永续合约交易对详情
     * @param contractId
     * @return
     */
    @RequiresPermissions("coinswap-coin:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "币本位合约交易对 详情")
    public MessageResult detail(@RequestParam(value = "symbol") Long contractId) {
        ContractCoinCoin coin = contractCoinCoinService.findOne(contractId);
        if(coin == null){
            return error(messageSource.getMessage("PAIR_NOT_FOUND"));
        }
        return success(coin);
    }

    /**
     * 添加永续合约交易对
     * @param contractCoin
     * @return
     */
    @RequiresPermissions("coinswap-coin:add")
    @PostMapping("add")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "币本位合约交易对 新增")
    public MessageResult add(@Valid ContractCoinCoin contractCoin) {
        ContractCoinCoin coin = contractCoinCoinService.findBySymbol(contractCoin.getSymbol());
        if(coin != null) {
            return error(messageSource.getMessage("ADD_FAILED") + "！" + messageSource.getMessage("ADD_FAILED") + contractCoin.getSymbol() + messageSource.getMessage("ALREADY_EXISTS"));
        }
        contractCoin.setTotalProfit(BigDecimal.ZERO);
        contractCoin.setTotalCloseFee(BigDecimal.ZERO);
        contractCoin.setTotalLoss(BigDecimal.ZERO);
        contractCoin.setTotalOpenFee(BigDecimal.ZERO);
        contractCoin = contractCoinCoinService.save(contractCoin);
        return MessageResult.getSuccessInstance(messageSource.getMessage("ADD_PAIR_SUCCESS"), contractCoin);
    }

    /**
     * 修改永续合约交易对信息
     * @param id
     * @param symbol
     * @param sort
     * @param enable
     * @param visible
     * @param exchangeable
     * @param enableOpenSell
     * @param enableOpenBuy
     * @param enableMarketSell
     * @param enableMarketBuy
     * @param enableTriggerEntrust
     * @param spreadType
     * @param spread
     * @param leverageType
     * @param leverage
     * @param minShare
     * @param maxShare
     * @param intervalHour
     * @param feePercent
     * @param maintenanceMarginRate
     * @param openFee
     * @param closeFee
     * @param takerFee
     * @param makerFee
     * @return
     */
    @RequiresPermissions("coinswap-coin:alter")
    @PostMapping("alter")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "币本位合约交易对 新增")
    public MessageResult alter(
            @RequestParam("id") Long id,
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sort", required = false) Integer sort, // 排序
            @RequestParam(value = "enable", required = false) Integer enable, // 上下架（1:上,2）
            @RequestParam(value = "visible", required = false) Integer visible, // 是否显示（1:是,2）
            @RequestParam(value = "exchangeable", required = false) Integer exchangeable, // 是否显示（1:是,2）
            @RequestParam(value = "enableOpenSell", required = false) Integer enableOpenSell, // 是否可买（1:是,0:否）
            @RequestParam(value = "enableOpenBuy", required = false) Integer enableOpenBuy, // 是否可买（1:是,0:否）
            @RequestParam(value = "enableMarketSell", required = false) Integer enableMarketSell, // 是否可买（1:是,0:否）
            @RequestParam(value = "enableMarketBuy", required = false) Integer enableMarketBuy, // 是否可买（1:是,0:否）
            @RequestParam(value = "enableTriggerEntrust", required = false) Integer enableTriggerEntrust, // 是否可买（1:是,0:否）
            @RequestParam(value = "spreadType", required = false) Integer spreadType, // 上下架（1:上,2）
            @RequestParam(value = "spread", required = false) BigDecimal spread,
            @RequestParam(value = "leverageType", required = false) Integer leverageType, // 是否显示（1:是,2）
            @RequestParam(value = "leverage", required = false) String leverage, // 允许投注数量
            @RequestParam(value = "minShare", required = false) BigDecimal minShare,
            @RequestParam(value = "maxShare", required = false) BigDecimal maxShare,
            @RequestParam(value = "intervalHour", required = false) Integer intervalHour,
            @RequestParam(value = "feePercent", required = false) BigDecimal feePercent,
            @RequestParam(value = "maintenanceMarginRate", required = false) BigDecimal maintenanceMarginRate,
            @RequestParam(value = "openFee", required = false) BigDecimal openFee,
            @RequestParam(value = "closeFee", required = false) BigDecimal closeFee,
            @RequestParam(value = "takerFee", required = false) BigDecimal takerFee,
            @RequestParam(value = "makerFee", required = false) BigDecimal makerFee
    ) {
        ContractCoinCoin coin = contractCoinCoinService.findOne(id);
        if(coin == null) {
            return error(messageSource.getMessage("PAIR") + coin.getSymbol() + messageSource.getMessage("NOT_FOUND"));
        }

        if(name != null) coin.setName(name);
        if(sort != null) coin.setSort(sort);
        if(enable != null) coin.setEnable(enable);
        if(visible != null) coin.setVisible(visible);
        if(exchangeable != null) coin.setExchangeable(exchangeable);
        if(enableOpenSell != null) coin.setEnableOpenSell(enableOpenSell == 1 ? BooleanEnum.IS_TRUE : BooleanEnum.IS_FALSE);
        if(enableOpenBuy != null) coin.setEnableOpenBuy(enableOpenBuy == 1 ? BooleanEnum.IS_TRUE : BooleanEnum.IS_FALSE);
        if(enableMarketSell != null) coin.setEnableMarketSell(enableMarketSell == 1 ? BooleanEnum.IS_TRUE : BooleanEnum.IS_FALSE);
        if(enableMarketBuy != null) coin.setEnableMarketBuy(enableMarketBuy == 1 ? BooleanEnum.IS_TRUE : BooleanEnum.IS_FALSE);
        if(enableTriggerEntrust != null) coin.setEnableTriggerEntrust(enableTriggerEntrust == 1 ? BooleanEnum.IS_TRUE : BooleanEnum.IS_FALSE);
        if(spreadType != null) coin.setSpreadType(spreadType);
        if(spread != null) coin.setSpread(spread);
        if(leverageType != null) coin.setLeverageType(leverageType);
        if(leverage != null) coin.setLeverage(leverage);
        if(minShare != null) coin.setMinShare(minShare);
        if(maxShare != null) coin.setMaxShare(maxShare);
        if(intervalHour != null) coin.setIntervalHour(intervalHour);
        if(feePercent != null) coin.setFeePercent(feePercent);
        if(maintenanceMarginRate != null) coin.setMaintenanceMarginRate(maintenanceMarginRate);
        if(openFee != null) coin.setOpenFee(openFee);
        if(closeFee != null) coin.setCloseFee(closeFee);
        if(takerFee != null) coin.setTakerFee(takerFee);
        if(makerFee != null) coin.setMakerFee(makerFee);

        contractCoinCoinService.save(coin);
        return success(messageSource.getMessage("SAVE_SUCCESS"));
    }

    /**
     * 批量为用户添加钱包
     * @param contractId
     * @return
     */
    @RequiresPermissions("coinswap-coin:init-wallet")
    @PostMapping("init-wallet")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "币本位合约交易对新增钱包 新增")
    public MessageResult generateWallet(@RequestParam("contractId") Long contractId) {
        ContractCoinCoin coin = contractCoinCoinService.findOne(contractId);
        if(coin == null) {
            return MessageResult.error(messageSource.getMessage("CONTRACT_CURRENCY_CONFIGURATION_NOT_FOUND"));
        }
        memberContractWalletFeign.initWallet(contractId);
        return MessageResult.success(messageSource.getMessage("OPERATION_SUCCESS"));
    }

    /**
     * 定点爆仓
     * @param contractId
     * @param price
     * @return
     */
    @RequiresPermissions("coinswap-coin:blast")
    @PostMapping("blast")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "币本位合约 定向爆仓")
    public MessageResult blast(@RequestParam("contractId") Long contractId, @RequestParam("price") BigDecimal price) {
        ContractCoinCoin coin = contractCoinCoinService.findOne(contractId);
        if(coin == null) {
            return MessageResult.error(messageSource.getMessage("CONTRACT_CURRENCY_CONFIGURATION_NOT_FOUND"));
        }

        return MessageResult.success(messageSource.getMessage("OPERATION_SUCCESS"));
    }

    /**
     * 戳一下
     * @param contractId
     * @param price
     * @return
     */
    @RequiresPermissions("coinswap-coin:poke")
    @PostMapping("poke")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "币本位合约 戳一下")
    public MessageResult poke(@RequestParam("contractId") Long contractId, @RequestParam("price") BigDecimal price) {
        ContractCoinCoin coin = contractCoinCoinService.findOne(contractId);
        if(coin == null) {
            return MessageResult.error(messageSource.getMessage("CONTRACT_CURRENCY_CONFIGURATION_NOT_FOUND"));
        }
        JSONObject msg = new JSONObject();
        msg.put("price",price);
        msg.put("symbol",coin.getSymbol());
        rocketMQTemplate.convertAndSend("admin-save-coin-swap-poke", JSON.toJSONString(msg));
        return MessageResult.success(messageSource.getMessage("OPERATION_SUCCESS"));
    }
}
