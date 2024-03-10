package com.bitisan.admin.controller.swap;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.screen.ContractRewardRecordScreen;
import com.bitisan.swap.entity.ContractRewardRecord;
import com.bitisan.swap.feign.ContractRewardRecordFeign;
import com.bitisan.swap.vo.RewardSetVo;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/swap/reward")
@Slf4j
public class ContractRewardRecordController extends BaseAdminController {
    @Autowired
    private ContractRewardRecordFeign contractRewardRecordService;

    /**
     * 分页查询
     * @param screen
     * @return
     */
    @RequiresPermissions("swap:reward:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "永续合约返佣订单 列表")
    public MessageResult pageQuery(
            ContractRewardRecordScreen screen) {
        //获取查询条件
        Page<ContractRewardRecord> all = contractRewardRecordService.findAll(screen);
        return success(IPage2Page(all));
    }

    /**
     * 返佣设置查询
     * @return
     */
    @RequiresPermissions("swap:reward:rewardSets")
    @PostMapping("rewardSets")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "返佣设置查询")
    public MessageResult rewardSets() {
        //获取查询条件
        RewardSetVo vo = contractRewardRecordService.findAllRewardSetVo();
        return success(vo);
    }

    /**
     * 清除返佣设置缓存
     * @return
     */
    @RequiresPermissions("swap:reward:clear")
    @PostMapping("clear")
    @AccessLog(module = AdminModule.CONTRACTOPTION, operation = "清除返佣设置缓存")
    public MessageResult clear() {
        //获取查询条件
        contractRewardRecordService.clearAllRewardSetVo();
        return success();
    }

}
