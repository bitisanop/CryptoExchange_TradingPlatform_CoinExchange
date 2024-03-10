package com.bitisan.admin.controller.coinswap;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.coinswap.entity.ContractOrderEntrustCoin;
import com.bitisan.coinswap.feign.ContractCoinOrderEntrustFeign;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.ContractOrderEntrustStatus;
import com.bitisan.screen.ContractOrderEntrustCoinScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coinswap/order")
@Slf4j
public class ContractOrderEntrustCoinController extends BaseAdminController {
    @Autowired
    private ContractCoinOrderEntrustFeign contractOrderEntrustService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private LocaleMessageSourceService messageSource;


    /**
     * 分页查询
     * @param
     * @param screen
     * @return
     */
    @RequiresPermissions("coinswap:order:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "币本位合约订单 列表")
    public MessageResult pageQuery(
            ContractOrderEntrustCoinScreen screen) {
        Page<ContractOrderEntrustCoin> all = contractOrderEntrustService.pageQuery(screen);
        return success(IPage2Page(all));
    }

    /**
     * 撤销委托
     * @param orderId
     * @return
     */
    @RequiresPermissions("coinswap:order:cancel")
    @PostMapping("cancel")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "币本位合约 撤单")
    public MessageResult cancelOrder(Long orderId) {
        ContractOrderEntrustCoin order = contractOrderEntrustService.findOne(orderId);
        if(order == null) {
            return MessageResult.error(messageSource.getMessage("CANCEL_ORDER_FAILED"));
        }
        if(order.getStatus() != ContractOrderEntrustStatus.ENTRUST_ING) {
            return MessageResult.error(messageSource.getMessage("CONTRACT_INVALID_ORDER_STATUS"));
        }
        // 发送消息至Exchange系统
        rocketMQTemplate.convertAndSend("swap-coin-order-cancel", JSON.toJSONString(order));

        log.info(">>>>>>>>>>订单撤销提交完成>>>>>>>>>>");
        return MessageResult.success(messageSource.getMessage("OPERATION_SUCCESS"));
    }
}
