package com.bitisan.admin.controller.earn;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.Admin;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.SysConstant;
import com.bitisan.earn.entity.AutoInvestActivity;
import com.bitisan.earn.entity.AutoInvestPlan;
import com.bitisan.earn.feign.AutoInvestActivityFeign;
import com.bitisan.earn.feign.AutoInvestPlanFeign;
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

import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author Bitisan  E-mail:bizzanhevin@gmail.com
 * @description 后台货币web
 * @date 2021/12/29 15:01
 */
@RestController
@RequestMapping("/auto/invest")
@Slf4j
public class AutoInvestController extends BaseAdminController {


    @Autowired
    private AutoInvestActivityFeign autoInvestActivityService;
    @Autowired
    private AutoInvestPlanFeign autoInvestPlanService;
    @Autowired
    private LocaleMessageSourceService messageSource;


    @RequiresPermissions("auto:invest:create")
    @PostMapping("create")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建定投活动")
    public MessageResult create(@Valid AutoInvestActivity activity) {
        notNull(activity.getCoinUnit(), "validate Coin.Unit!");
        activity.setUpdateTime(DateUtil.getCurrentDate());
        activity.setCreateTime(DateUtil.getCurrentDate());
        autoInvestActivityService.save(activity);
        return success();
    }


    @RequiresPermissions("auto:invest:update")
    @PostMapping("update")
    @AccessLog(module = AdminModule.SYSTEM, operation = "更新定投活动")
    public MessageResult update(
            @Valid AutoInvestActivity activity,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin,
            BindingResult bindingResult) {

        Assert.notNull(admin, messageSource.getMessage("DATA_EXPIRED_LOGIN_AGAIN"));

        notNull(activity.getCoinUnit(), "validate Coin.Unit!");
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        AutoInvestActivity one = autoInvestActivityService.findById(activity.getId());
        notNull(one, "validate coin.name!");
        activity.setUpdateTime(DateUtil.getCurrentDate());
        autoInvestActivityService.save(activity);
        return success();
    }

    @RequiresPermissions("auto:invest:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.SYSTEM, operation = "后台定投活动详情")
    public MessageResult detail(@RequestParam("id") Long id) {
        AutoInvestActivity one = autoInvestActivityService.findById(id);
        notNull(one, "validate Coin.Unit!");
        return success(one);
    }

    @RequiresPermissions("auto:invest:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "分页查找定投活动")
    public MessageResult pageQuery(ActivityParam pageParam) {
        Page<AutoInvestActivity> pageResult = autoInvestActivityService.findAll(pageParam);
        return success(IPage2Page(pageResult));
    }


    @RequiresPermissions("auto:plan:page-query")
    @PostMapping("queryPlan")
    @AccessLog(module = AdminModule.SYSTEM, operation = "分页查找定投计划")
    public MessageResult queryPlan(ActivityParam pageParam) {
        Page<AutoInvestPlan> pageResult = autoInvestPlanService.findAll(pageParam);
        return success(IPage2Page(pageResult));
    }

}
