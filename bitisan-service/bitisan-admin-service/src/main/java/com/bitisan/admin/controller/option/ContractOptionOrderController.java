package com.bitisan.admin.controller.option;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.option.entity.ContractOptionOrder;
import com.bitisan.option.feign.ContractOptionOrderFeign;
import com.bitisan.screen.ContractOptionOrderScreen;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/option/order")
@Slf4j
public class ContractOptionOrderController extends BaseAdminController {

    @Autowired
    private ContractOptionOrderFeign contractOptionOrderService;

    /**
     * 分页查询所有订单
     * @param screen
     * @return
     */
    @RequiresPermissions("option:order:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约订单 列表")
    public MessageResult detail(
            ContractOptionOrderScreen screen) {
        Page<ContractOptionOrder> all = contractOptionOrderService.findAll(screen);
        return success(IPage2Page(all));
    }



    /**
     * 某期合约所有订单
     * @param optionId
     * @return
     */
    @RequiresPermissions("option:order:option-list")
    @PostMapping("option-list")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约订单 列表")
    public MessageResult queryByOptionId(Long optionId){
        List<ContractOptionOrder> list = contractOptionOrderService.findByOptionId(optionId);
        return success(list);
    }

    /**
     * 某用户合约所有订单
     * @param memberId
     * @return
     */
    @RequiresPermissions("option:order:member-list")
    @PostMapping("member-list")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "期权合约订单 列表")
    public MessageResult queryByMember(Long memberId){
        List<ContractOptionOrder> list = contractOptionOrderService.findByMemberId(memberId);
        return success(list);
    }
    @RequiresPermissions("option:order:setOptionOrder")
    @PostMapping("setOptionOrder")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "设置期权合约")
    public MessageResult setOptionOrder(Long memberId,Integer optionNo,Short optionNoChange,Short directionChange){
        return contractOptionOrderService.setOptionOrder(memberId,optionNo,optionNoChange,directionChange);

    }

}
