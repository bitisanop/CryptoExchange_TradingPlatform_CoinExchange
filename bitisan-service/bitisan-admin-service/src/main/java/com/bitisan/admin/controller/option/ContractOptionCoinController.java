package com.bitisan.admin.controller.option;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.BooleanEnum;
import com.bitisan.option.entity.ContractOptionCoin;
import com.bitisan.option.feign.ContractOptionCoinFeign;
import com.bitisan.screen.PageParam;
import com.bitisan.service.LocaleMessageSourceService;
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
import java.util.Date;

@RestController
@RequestMapping("/option-coin")
@Slf4j
public class ContractOptionCoinController extends BaseAdminController {

    @Autowired
    private ContractOptionCoinFeign contractOptionCoinService;

    @Autowired
    private LocaleMessageSourceService messageSource;

    /**
     * 获取期权合约交易对列表
     * @param pageParam
     * @return
     */
    @RequiresPermissions("option-coin:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约交易对 列表")
    public MessageResult list(PageParam pageParam) {

        Page<ContractOptionCoin> coinList = contractOptionCoinService.findAll(pageParam);
        return success(IPage2Page(coinList));
    }

    /**
     * 获取期权合约交易对详情
     * @param symbol
     * @return
     */
    @RequiresPermissions("option-coin:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约交易对 详情")
    public MessageResult detail(@RequestParam(value = "symbol") String symbol) {
        ContractOptionCoin coin = contractOptionCoinService.findOneBySymbol(symbol);
        if(coin == null){
            return error(messageSource.getMessage("PAIR_NOT_FOUND"));
        }
        return success(coin);
    }

    /**
     * 添加期权合约交易对
     * @param contractOptionCoin
     * @return
     */
    @RequiresPermissions("option-coin:add")
    @PostMapping("add")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约交易对 新增")
    public MessageResult add(@Valid ContractOptionCoin contractOptionCoin) {
        ContractOptionCoin coin = contractOptionCoinService.findOneBySymbol(contractOptionCoin.getSymbol());
        if(coin != null) {
            return error(messageSource.getMessage("ADD_FAILED_PAIR_ALREADY_EXISTS") + contractOptionCoin.getSymbol() + messageSource.getMessage("ALREADY_EXISTS"));
        }
        if(contractOptionCoin.getCloseTimeGap() <= 0 || contractOptionCoin.getOpenTimeGap() <= 0) {
            return error(messageSource.getMessage("BET_OR_DRAW_INTERVAL_MUST_BE_GREATER_THAN_0"));
        }
        contractOptionCoin.setCreateTime(new Date());
        contractOptionCoin.setTotalProfit(BigDecimal.ZERO);
        contractOptionCoinService.add(contractOptionCoin);
        return MessageResult.getSuccessInstance(messageSource.getMessage("ADD_PAIR_SUCCESS"), coin);
    }

    /**
     * 修改期权合约交易对
     * @param symbol
     * @param enable
     * @param enableBuy
     * @param enableSell
     * @param visible
     * @param sort
     * @param amount
     * @param feePercent
     * @param winFeePercent
     * @param openTimeGap
     * @param closeTimeGap
     * @param tiedType
     * @return
     */
    @RequiresPermissions("option-coin:alter")
    @PostMapping("alter")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约交易对 新增")
    public MessageResult alter(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "enable", required = false) Integer enable, // 上下架（1:上,2）
            @RequestParam(value = "enableBuy", required = false) Integer enableBuy, // 是否可买（1:是,0:否）
            @RequestParam(value = "enableSell", required = false) Integer enableSell, // 是否可卖（1:是,0:否）
            @RequestParam(value = "visible", required = false) Integer visible, // 是否显示（1:是,2）
            @RequestParam(value = "sort", required = false) Integer sort, // 排序
            @RequestParam(value = "amount", required = false) String amount, // 允许投注数量
            @RequestParam(value = "feePercent", required = false) BigDecimal feePercent,
            @RequestParam(value = "oods", required = false) BigDecimal oods,
            @RequestParam(value = "winFeePercent", required = false) BigDecimal winFeePercent,
            @RequestParam(value = "openTimeGap", required = false) Integer openTimeGap,
            @RequestParam(value = "closeTimeGap", required = false) Integer closeTimeGap,
            @RequestParam(value = "initBuyReward", required = false) BigDecimal initBuyReward,
            @RequestParam(value = "initSellReward", required = false) BigDecimal initSellReward,
            @RequestParam(value = "tiedType", required = false) Integer tiedType
    ) {
        ContractOptionCoin coin = contractOptionCoinService.findOneBySymbol(symbol);
        if(coin == null) {
            return error(messageSource.getMessage("PAIR") + coin.getSymbol() + messageSource.getMessage("NOT_FOUND"));
        }

        if(enable != null) coin.setEnable(enable);
        if(enableBuy != null) coin.setEnableBuy(enableBuy == 1 ? BooleanEnum.IS_TRUE.getCode() : BooleanEnum.IS_FALSE.getCode());
        if(enableSell != null) coin.setEnableSell(enableSell == 1 ? BooleanEnum.IS_TRUE.getCode() : BooleanEnum.IS_FALSE.getCode());
        if(visible != null) coin.setVisible(visible);
        if(sort != null) coin.setSort(sort);
        if(amount != null) coin.setAmount(amount);
        if(feePercent != null) coin.setFeePercent(feePercent);
        if(winFeePercent != null) coin.setWinFeePercent(winFeePercent);
        if(openTimeGap != null) coin.setOpenTimeGap(openTimeGap);
        if(closeTimeGap != null) coin.setCloseTimeGap(closeTimeGap);
        if(tiedType != null) coin.setTiedType(tiedType);
        if(initBuyReward != null) coin.setInitBuyReward(initBuyReward);
        if(initSellReward != null) coin.setInitSellReward(initSellReward);
        if(oods != null) coin.setOods(oods);

        contractOptionCoinService.alert(coin);
        return success(messageSource.getMessage("SAVE_SUCCESS"));
    }
}
