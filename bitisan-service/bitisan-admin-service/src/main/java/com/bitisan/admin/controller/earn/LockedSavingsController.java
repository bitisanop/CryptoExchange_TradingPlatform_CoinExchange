package com.bitisan.admin.controller.earn;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.Admin;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.SysConstant;
import com.bitisan.earn.entity.LockedSavingsActivity;
import com.bitisan.earn.entity.LockedSavingsOrder;
import com.bitisan.earn.feign.LockedSavingsActivityFeign;
import com.bitisan.earn.feign.LockedSavingsOrderFeign;
import com.bitisan.earn.vo.ActivityParam;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.BindingResultUtil;
import com.bitisan.util.DateUtil;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author Bitisan  E-mail:bizzanhevin@gmail.com
 * @description 后台货币web
 * @date 2021/12/29 15:01
 */
@RestController
@RequestMapping("/locked/activity")
@Slf4j
public class LockedSavingsController extends BaseAdminController {


    @Autowired
    private LockedSavingsActivityFeign lockedSavingsActivityService;

    @Autowired
    private LockedSavingsOrderFeign lockedSavingsOrderService;

    @Autowired
    private LocaleMessageSourceService messageSource;


    @RequiresPermissions("locked:activity:create")
    @PostMapping("create")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建定期活动")
    public MessageResult create(@Valid LockedSavingsActivity activity) {
        notNull(activity.getCoinUnit(), "validate Coin.Unit!");
        activity.setUpdateTime(DateUtil.getCurrentDate());
        activity.setCreateTime(DateUtil.getCurrentDate());
        lockedSavingsActivityService.save(activity);
        return success();
    }


    @RequiresPermissions("locked:activity:update")
    @PostMapping("update")
    @AccessLog(module = AdminModule.SYSTEM, operation = "更新定期活动")
    public MessageResult update(
            @Valid LockedSavingsActivity activity,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin,
            BindingResult bindingResult) {

        Assert.notNull(admin, messageSource.getMessage("DATA_EXPIRED_LOGIN_AGAIN"));

        notNull(activity.getCoinUnit(), "validate Coin.Unit!");
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        LockedSavingsActivity one = lockedSavingsActivityService.findById(activity.getId());
        notNull(one, "validate coin.name!");
        activity.setUpdateTime(DateUtil.getCurrentDate());
        lockedSavingsActivityService.save(activity);
        return success();
    }

    @RequiresPermissions("locked:activity:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.SYSTEM, operation = "后台定期活动详情")
    public MessageResult detail(@RequestParam("id") Long id) {
        LockedSavingsActivity one = lockedSavingsActivityService.findById(id);
        notNull(one, "validate Coin.Unit!");
        return success(one);
    }

    @RequiresPermissions("locked:activity:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "分页查找定期活动")
    public MessageResult pageQuery(ActivityParam pageParam) {
        Page<LockedSavingsActivity> pageResult = lockedSavingsActivityService.findAll(pageParam);
        return success(IPage2Page(pageResult));
    }


    @RequiresPermissions("locked:order:page-query")
    @PostMapping("queryOrder")
    @AccessLog(module = AdminModule.SYSTEM, operation = "分页查找定期订单")
    public MessageResult queryOrder(ActivityParam pageParam) {
        Page<LockedSavingsOrder> pageResult = lockedSavingsOrderService.findAll(pageParam);
        return success(pageResult);
    }

}
