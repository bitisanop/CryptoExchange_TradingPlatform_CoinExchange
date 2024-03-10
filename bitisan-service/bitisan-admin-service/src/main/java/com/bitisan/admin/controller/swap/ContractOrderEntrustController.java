package com.bitisan.admin.controller.swap;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.ContractOrderEntrustStatus;
import com.bitisan.screen.ContractOrderEntrustScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.swap.entity.ContractOrderEntrust;
import com.bitisan.swap.feign.ContractOrderEntrustFeign;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/swap/order")
@Slf4j
public class ContractOrderEntrustController extends BaseAdminController {
    @Autowired
    private ContractOrderEntrustFeign contractOrderEntrustService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private LocaleMessageSourceService messageSource;

    /**
     * 分页查询
     * @param screen
     * @return
     */
    @RequiresPermissions("swap:order:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约订单 列表")
    public MessageResult pageQuery(
            ContractOrderEntrustScreen screen) {
        //获取查询条件
        Page<ContractOrderEntrust> all = contractOrderEntrustService.pageQuery(screen);
        return success(IPage2Page(all));
    }



    /**
     * 撤销委托
     * @param orderId
     * @return
     */
    @RequiresPermissions("swap:order:cancel")
    @PostMapping("cancel")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "永续合约 撤单")
    public MessageResult cancelOrder(Long orderId) {
        ContractOrderEntrust order = contractOrderEntrustService.findOne(orderId);
        if(order == null) {
            return MessageResult.error(messageSource.getMessage("CANCEL_ORDER_FAILED"));
        }
        if(order.getStatus() != ContractOrderEntrustStatus.ENTRUST_ING) {
            return MessageResult.error(messageSource.getMessage("DELEGATE_STATUS_ERROR"));
        }
        // 发送消息至Exchange系统
        rocketMQTemplate.convertAndSend("swap-order-cancel", JSON.toJSONString(order));

        log.info(">>>>>>>>>>订单撤销提交完成>>>>>>>>>>");
        return MessageResult.success(messageSource.getMessage("OPERATION_SUCCESS"));
    }
}
