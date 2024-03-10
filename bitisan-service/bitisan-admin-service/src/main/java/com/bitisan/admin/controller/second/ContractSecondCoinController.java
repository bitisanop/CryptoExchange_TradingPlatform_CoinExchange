package com.bitisan.admin.controller.second;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.BooleanEnum;
import com.bitisan.screen.PageParam;
import com.bitisan.second.entity.ContractSecondCoin;
import com.bitisan.second.feign.ContractSecondCoinFeign;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.feign.MemberSecondWalletFeign;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/second-coin")
@Slf4j
public class ContractSecondCoinController extends BaseAdminController {

    @Autowired
    private ContractSecondCoinFeign contractCoinService;
    @Autowired
    private MemberSecondWalletFeign memberSecondWalletService;
    @Autowired
    private LocaleMessageSourceService messageSource;

    /**
     * 获取永续合约交易对列表
     * @param pageParam
     * @return
     */
    @RequiresPermissions("second-coin:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "秒合约交易对 列表")
    public MessageResult list(PageParam pageParam) {

        Page<ContractSecondCoin> coinList = contractCoinService.findAll(pageParam);
        return success(IPage2Page(coinList));
    }

    /**
     * 获取永续合约交易对详情
     * @param contractId
     * @return
     */
    @RequiresPermissions("second-coin:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "秒合约交易对 详情")
    public MessageResult detail(@RequestParam(value = "symbol") Long contractId) {
        ContractSecondCoin coin = contractCoinService.findOne(contractId);
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
    @RequiresPermissions("second-coin:add")
    @PostMapping("add")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "秒合约交易对 新增")
    public MessageResult add(@Valid ContractSecondCoin contractCoin) {
        ContractSecondCoin coin = contractCoinService.findBySymbol(contractCoin.getSymbol());
        if(coin != null) {
            return error(messageSource.getMessage("ADD_FAILED_PAIR") + contractCoin.getSymbol() + messageSource.getMessage("ALREADY_EXISTS"));
        }
        contractCoin.setTotalProfit(BigDecimal.ZERO);
        contractCoin.setTotalCloseFee(BigDecimal.ZERO);
        contractCoin.setTotalLoss(BigDecimal.ZERO);
        contractCoin.setTotalOpenFee(BigDecimal.ZERO);
        contractCoin = contractCoinService.save(contractCoin);
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
    @RequiresPermissions("second-coin:alter")
    @PostMapping("alter")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "秒合约交易对 新增")
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
        ContractSecondCoin coin = contractCoinService.findOne(id);
        if(coin == null) {
            return error(messageSource.getMessage("PAIR") + coin.getSymbol() + messageSource.getMessage("NOT_FOUND"));
        }

        if(name != null) coin.setName(name);
        if(sort != null) coin.setSort(sort);
        if(enable != null) coin.setEnable(enable);
        if(visible != null) coin.setVisible(visible);
        if(exchangeable != null) coin.setExchangeable(exchangeable);
        if(enableOpenSell != null) coin.setEnableOpenSell(enableOpenSell == 1 ? BooleanEnum.IS_TRUE.getCode() : BooleanEnum.IS_FALSE.getCode());
        if(enableOpenBuy != null) coin.setEnableOpenBuy(enableOpenBuy == 1 ? BooleanEnum.IS_TRUE.getCode() : BooleanEnum.IS_FALSE.getCode());
        if(enableMarketSell != null) coin.setEnableMarketSell(enableMarketSell == 1 ? BooleanEnum.IS_TRUE.getCode() : BooleanEnum.IS_FALSE.getCode());
        if(enableMarketBuy != null) coin.setEnableMarketBuy(enableMarketBuy == 1 ? BooleanEnum.IS_TRUE.getCode() : BooleanEnum.IS_FALSE.getCode());
        if(enableTriggerEntrust != null) coin.setEnableTriggerEntrust(enableTriggerEntrust == 1 ? BooleanEnum.IS_TRUE.getCode() : BooleanEnum.IS_FALSE.getCode());
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

        contractCoinService.save(coin);
        return success(messageSource.getMessage("SAVE_SUCCESS"));
    }

}
