package com.bitisan.admin.controller.second;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.screen.ContractSecondOrderScreen;
import com.bitisan.second.entity.ContractSecondOrder;
import com.bitisan.second.feign.ContractSecondOrderFeign;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/second-order")
@Slf4j
public class ContractSecondOrderController extends BaseAdminController {


    @Autowired
    private ContractSecondOrderFeign contractSecondOrderService;
    @Autowired
    private LocaleMessageSourceService messageSource;

    /**
     * 分页查询所有订单
     * @param screen
     * @return
     */
    @RequiresPermissions("second:order:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约订单 列表")
    public MessageResult detail(
            ContractSecondOrderScreen screen) {
        //获取查询条件
        Page<ContractSecondOrder> all = contractSecondOrderService.findAll(screen);
        return success(IPage2Page(all));
    }

    /**
     * 设置预设平仓价格
     * @return
     */
    @RequiresPermissions("second:order:alter")
    @PostMapping("alter")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "设置预设平仓价格")
    public MessageResult alter(
            @RequestParam(value = "id",required = true) Long id,
            @RequestParam(value = "preClosePrice", required = true) BigDecimal presetPrice){
        contractSecondOrderService.updatePreClosePrice(id,presetPrice);
        return success(messageSource.getMessage("SAVE_SUCCESS"));
    }

}
