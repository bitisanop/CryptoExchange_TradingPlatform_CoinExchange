package com.bitisan.admin.controller.coinswap;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.coinswap.entity.MemberContractWalletCoin;
import com.bitisan.coinswap.feign.ContractCoinMarketFeign;
import com.bitisan.coinswap.feign.MemberContractCoinWalletFeign;
import com.bitisan.constant.AdminModule;
import com.bitisan.pojo.CoinThumb;
import com.bitisan.screen.MemberContractWalletCoinScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 持仓管理
 */
@RestController
@RequestMapping("/coinswap/position")
@Slf4j
public class MemberContractWalletCoinController extends BaseAdminController {
    @Autowired
    private MemberContractCoinWalletFeign memberContractWalletService;
    @Autowired
    private ContractCoinMarketFeign contractCoinMarketFeign;

    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("coinswap:position:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "币本位合约用户持仓管理 列表")
    public MessageResult detail(
            MemberContractWalletCoinScreen screen) {

        Page<MemberContractWalletCoin> all = memberContractWalletService.findAll(screen);
        // 获取最新价格
        List<CoinThumb> thumbList =contractCoinMarketFeign.findSymbolThumb4Feign();


        List<MemberContractWalletCoin> list = all.getRecords();
        for(MemberContractWalletCoin wallet :list) {
            for(int i = 0; i < thumbList.size(); i++) {
                CoinThumb thumb = thumbList.get(i);
                if(wallet.getContractCoin().getSymbol().equals(thumb.getSymbol())) {
                    wallet.setCurrentPrice(thumb.getClose());
                }
            }
            // 设置CNY / USDT汇率
            wallet.setCnyRate(BigDecimal.valueOf(6.98));
        }
        return success(IPage2Page(all));
    }


    /**
     * 强制市价平仓
     * @param walletId
     * @return
     */
    @RequiresPermissions("coinswap:order:force-close")
    @PostMapping("force-close")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "币本位合约用户持仓管理 强制平仓")
    public MessageResult forceClose(Long walletId) {
//        MemberContractWalletCoin wallet = memberContractWalletService.findOne(walletId);
//        if(wallet == null) {
//            return MessageResult.error("撤销委托失败");
//        }
        return MessageResult.success(messageSource.getMessage("OPERATION_SUCCESS"));
    }

}
