package com.bitisan.admin.controller.second;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.screen.ContractFinanceScreen;
import com.bitisan.screen.ContractSecondOrderScreen;
import com.bitisan.screen.PageParam;
import com.bitisan.second.entity.ContractSecondCycle;
import com.bitisan.second.entity.ContractSecondOrder;
import com.bitisan.second.entity.ContractSecondSet;
import com.bitisan.second.feign.ContractSecondCycleFeign;
import com.bitisan.second.feign.ContractSecondOrderFeign;
import com.bitisan.second.feign.ContractSecondSetFeign;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.MemberSecondWallet;
import com.bitisan.user.feign.MemberSecondWalletFeign;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/second")
@Slf4j
public class ContractSecondController extends BaseAdminController {
    @Autowired
    private ContractSecondSetFeign contractSecondSetService;
    @Autowired
    private ContractSecondCycleFeign contractSecondCycleService;
    @Autowired
    private ContractSecondOrderFeign contractSecondOrderService;
    @Autowired
    private MemberSecondWalletFeign memberSecondWalletService;
    @Autowired
    private LocaleMessageSourceService messageSource;


    /**
     * 查询
     * @param
     * @return
     */
    @RequiresPermissions("second-order:page-query")
    @PostMapping("order/page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "秒合约 列表")
    public MessageResult queryOrders(ContractSecondOrderScreen screen) {
        Page<ContractSecondOrder> all = contractSecondOrderService.findAll(screen);
        return success(IPage2Page(all));
    }
    /**
     * 查询
     * @param
     * @return
     */
    @RequiresPermissions("set:page-query")
    @PostMapping("set/page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "包赔设置 列表")
    public MessageResult querySets(PageParam pageParam) {
        //获取查询条件
        Page<ContractSecondSet> all = contractSecondSetService.findSecondSetAll(pageParam);
        return success(IPage2Page(all));
    }


    /**
     * 添加包赔设置
     * @param contractSecondSet
     * @return
     */
    @RequiresPermissions("second-set:add")
    @PostMapping("set/add")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "添加包赔设置 新增")
    public MessageResult add(@Valid ContractSecondSet contractSecondSet) {
        contractSecondSet.setCreateTime(new Date());
        contractSecondSet.setUpdateTime(new Date());
        contractSecondSet = contractSecondSetService.save(contractSecondSet);
        return MessageResult.getSuccessInstance("添加成功！", contractSecondSet);
    }

    /**
     * 修改包赔设置
     * @return
     */
    @RequiresPermissions("second-set:alter")
    @PostMapping("set/alter")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "修改包赔设置 修改")
    public MessageResult alter(
            @RequestParam("id") Long id,
            @RequestParam(value = "startTime", required = false) String startTime, // 开始时间
            @RequestParam(value = "endTime", required = false) String endTime, // 结束时间
            @RequestParam(value = "orderNum", required = false) Integer orderNum, // 包赔单数
            @RequestParam(value = "limitRate", required = false) BigDecimal limitRate
    ) {
        ContractSecondSet set = contractSecondSetService.findOne(id);
        if(set == null) {
            return error(messageSource.getMessage("STOP_LOSS") + id + messageSource.getMessage("NOT_FOUND"));
        }
        if(startTime != null) set.setStartTime(startTime);
        if(endTime != null) set.setEndTime(endTime);
        if(orderNum != null) set.setOrderNum(orderNum);
        if(limitRate != null) set.setLimitRate(limitRate);
        set.setUpdateTime(new Date());
        contractSecondSetService.save(set);
        return success(messageSource.getMessage("SAVE_SUCCESS"));
    }

    /**
     * 修改包赔设置
     * @return
     */
    @RequiresPermissions("second-set:del")
    @PostMapping("set/delete")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "包赔设置 删除")
    public MessageResult delete(
            @RequestParam(value = "ids") String[] ids
    ) {
        List<Long> delIds = new ArrayList<>();
        if(ids!=null && ids.length>0){
            for (String id : ids) {
                delIds.add(Long.parseLong(id));
            }
        }
        contractSecondSetService.deleteBatch(delIds);
        return success();
    }


    /**
     * 查询
     * @param
     * @return
     */
    @RequiresPermissions("cycle:page-query")
    @PostMapping("cycle/page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "秒合约周期 列表")
    public MessageResult queryCycles(PageParam pageParam) {
        Page<ContractSecondCycle> all = contractSecondCycleService.findAll(pageParam);
        return success(IPage2Page(all));
    }

    /**
     * 添加秒合约周期
     * @param contractSecondCycle
     * @return
     */
    @RequiresPermissions("second-cycle:add")
    @PostMapping("cycle/add")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "添加秒合约周期 新增")
    public MessageResult addCycle(@Valid ContractSecondCycle contractSecondCycle) {
        contractSecondCycle.setCreateTime(new Date());
        contractSecondCycle.setUpdateTime(new Date());
        contractSecondCycle = contractSecondCycleService.save(contractSecondCycle);
        return MessageResult.getSuccessInstance(messageSource.getMessage("ADD_SUCCESS"), contractSecondCycle);
    }

    /**
     * 修改秒合约周期
     * @return
     */
    @RequiresPermissions("second-cycle:alter")
    @PostMapping("cycle/alter")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "修改秒合约周期")
    public MessageResult alterCycle(
            @RequestParam("id") Long id,
            @RequestParam(value = "cycleRate", required = false) BigDecimal cycleRate, // 周期赔率
            @RequestParam(value = "cycleLength", required = false) Long cycleLength, // 周期时长（秒）
            @RequestParam(value = "minAmount", required = false) BigDecimal minAmount, // 最小数量
            @RequestParam(value = "maxAmount", required = false) BigDecimal maxAmount //最大数量
    ) {
        ContractSecondCycle cycle = contractSecondCycleService.findOne(id);
        if(cycle == null) {
            return error(messageSource.getMessage("MODIFY_FUTURES_CONTRACT_PERIOD") + id + messageSource.getMessage("NOT_FOUND"));
        }
        if(cycleRate != null) cycle.setCycleRate(cycleRate);
        if(cycleLength != null) cycle.setCycleLength(cycleLength);
        if(minAmount != null) cycle.setMinAmount(minAmount);
        if(maxAmount != null) cycle.setMaxAmount(maxAmount);
        cycle.setUpdateTime(new Date());
        contractSecondCycleService.save(cycle);
        return success(messageSource.getMessage("SAVE_SUCCESS"));
    }

    /**
     * 删除秒合约周期
     * @return
     */
    @RequiresPermissions("second-cycle:del")
    @PostMapping("cycle/delete")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "秒合约周期 删除")
    public MessageResult delCycle(@RequestParam(value = "ids") String[] ids) {
        List<Long> delIds = new ArrayList<>();
        if(ids!=null && ids.length>0){
            for (String id : ids) {
                delIds.add(Long.parseLong(id));
            }
        }
        contractSecondCycleService.deleteBatch(delIds);
        return success();
    }


    /**
     * 查询 期权账户
     * @param
     * @return
     */
    @RequiresPermissions("second-account:page-query")
    @PostMapping("account/page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "秒合约账户 列表")
    public MessageResult queryAccounts(ContractFinanceScreen screen) {

        Page<MemberSecondWallet> all = memberSecondWalletService.findAll(screen);
        return success(IPage2Page(all));
    }

}
