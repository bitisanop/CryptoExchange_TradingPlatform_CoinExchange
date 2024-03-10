package com.bitisan.admin.controller.swap;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.pojo.CoinThumb;
import com.bitisan.screen.MemberContractWalletScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.swap.entity.MemberContractWallet;
import com.bitisan.swap.feign.ContractMarketFeign;
import com.bitisan.swap.feign.MemberContractWalletFeign;
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
@RequestMapping("/swap/position")
@Slf4j
public class MemberContractWalletController extends BaseAdminController {
    @Autowired
    private MemberContractWalletFeign memberContractWalletService;
    @Autowired
    private ContractMarketFeign contractMarketFeign;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("swap:position:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "永续合约用户持仓管理 列表")
    public MessageResult detail(
            MemberContractWalletScreen screen) {
        //获取查询条件
        Page<MemberContractWallet> all = memberContractWalletService.findAll(screen);
        // 获取最新价格
        List<CoinThumb> thumbList =contractMarketFeign.findSymbolThumb4Feign();

        List<MemberContractWallet> list = all.getRecords();
        for(MemberContractWallet wallet :list) {
            for(int i = 0; i < thumbList.size(); i++) {
                CoinThumb thumb = thumbList.get(i);
                if(wallet.getSymbol().equals(thumb.getSymbol())) {
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
    @RequiresPermissions("swap:order:force-close")
    @PostMapping("force-close")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "永续合约用户持仓管理 强制平仓")
    public MessageResult forceClose(Long walletId) {
        //未实现
//        MemberContractWallet wallet = memberContractWalletService.findOne(walletId);
//        if(wallet == null) {
//            return MessageResult.error("撤销委托失败");
//        }
        return MessageResult.success(messageSource.getMessage("OPERATION_SUCCESS"));
    }

}
