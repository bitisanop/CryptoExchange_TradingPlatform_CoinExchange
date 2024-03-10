package com.bitisan.admin.controller.exchange;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.ExchangeOrderStatus;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.entity.ExchangeOrderDetail;
import com.bitisan.exchange.feign.ExchangeOrderFeign;
import com.bitisan.screen.ExchangeOrderScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.MessageResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description
 * @date 2019/1/31 10:52
 */
@RestController
@RequestMapping("exchange/exchange-order")
public class ExchangeOrderController extends BaseAdminController {

    @Autowired
    private ExchangeOrderFeign exchangeOrderService;
    @Autowired
    private LocaleMessageSourceService messageSource;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @RequiresPermissions("exchange:exchange-order:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "exchangeOrder详情")
    public MessageResult detail(String id) {
        List<ExchangeOrderDetail> one = exchangeOrderService.findAllDetailByOrderId(id);
        if (one == null) {
            return error(messageSource.getMessage("NO_DATA"));
        }
        return success(one);
    }

    @RequiresPermissions("exchange:exchange-order:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "分页查找exchangeOrder")
    public MessageResult page(
            ExchangeOrderScreen screen) {
        Page<ExchangeOrder> all = exchangeOrderService.findAll(screen);
        return success(IPage2Page(all));
    }

    @RequiresPermissions("exchange:exchange-order:cancel")
    @PostMapping("cancel")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "取消委托")
    public MessageResult cancelOrder(String orderId) {
        ExchangeOrder order = exchangeOrderService.findOne(orderId);
        if (order.getStatus() != ExchangeOrderStatus.TRADING) {
            return MessageResult.error(500, "order not in trading");
        }
        // 发送消息至Exchange系统
        rocketMQTemplate.convertAndSend("exchange-order-cancel",JSON.toJSONString(order));
        return MessageResult.success(messageSource.getMessage("SUCCESS"));
    }
}
