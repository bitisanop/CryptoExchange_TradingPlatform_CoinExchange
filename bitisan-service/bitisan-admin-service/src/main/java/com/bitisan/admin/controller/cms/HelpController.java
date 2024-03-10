package com.bitisan.admin.controller.cms;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.SysHelp;
import com.bitisan.admin.service.SysHelpService;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.screen.PageParam;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.BindingResultUtil;
import com.bitisan.util.DateUtil;
import com.bitisan.util.FileUtil;
import com.bitisan.util.MessageResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author Bitisan  E-mail:bizzanhevin@gmail.com
 * @description 后台帮助web
 * @date 2019/1/9 10:11
 */
@RestController
@RequestMapping("/cms/system-help")
public class HelpController extends BaseAdminController {

    @Autowired
    private SysHelpService sysHelpService;
    @Autowired
    private LocaleMessageSourceService msService;

    @RequiresPermissions("cms:system-help:create")
    @PostMapping("/create")
    @AccessLog(module = AdminModule.CMS, operation = "创建系统帮助")
    public MessageResult create(@Valid SysHelp sysHelp, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        sysHelp.setCreateTime(DateUtil.getCurrentDate());
        sysHelpService.save(sysHelp);
        return success(sysHelp);
    }

    @RequiresPermissions("cms:system-help:all")
    @PostMapping("/all")
    @AccessLog(module = AdminModule.CMS, operation = "查找所有系统帮助")
    public MessageResult all() {
        List<SysHelp> sysHelps = sysHelpService.list();
        if (sysHelps != null && sysHelps.size() > 0) {
            return success(sysHelps);
        }
        return error("data null");
    }

    @RequiresPermissions("cms:system-help:top")
    @PostMapping("top")
    @AccessLog(module = AdminModule.CMS, operation = "系统帮助置顶")
    public MessageResult toTop(@RequestParam("id")long id){
        SysHelp help = sysHelpService.getById(id);
        int a = sysHelpService.getMaxSort();
        help.setSort(a+1);
        help.setIsTop("0");
        sysHelpService.updateById(help);
        return success(msService.getMessage("TOP_SUCCESS"));
    }

    /**
     * 系统帮助取消置顶
     * @param id
     * @return
     */
    @RequiresPermissions("cms:system-help:down")
    @PostMapping("down")
    @AccessLog(module = AdminModule.CMS, operation = "系统帮助取消置顶")
    public MessageResult toDown(@RequestParam("id")long id){
        SysHelp help = sysHelpService.getById(id);
        help.setIsTop("1");
        sysHelpService.updateById(help);
        return success();
    }

    @RequiresPermissions("cms:system-help:detail")
    @PostMapping("/detail")
    @AccessLog(module = AdminModule.CMS, operation = "系统帮助详情")
    public MessageResult detail(@RequestParam(value = "id") Long id) {
        SysHelp sysHelp = sysHelpService.getById(id);
        notNull(sysHelp, "validate id!");
        return success(sysHelp);
    }

    @RequiresPermissions("cms:system-help:update")
    @PostMapping("/update")
    @AccessLog(module = AdminModule.CMS, operation = "更新系统帮助")
    public MessageResult update(@Valid SysHelp sysHelp, BindingResult bindingResult) {
        notNull(sysHelp.getId(), "validate id!");
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        SysHelp one = sysHelpService.getById(sysHelp.getId());
        notNull(one, "validate id!");
        sysHelpService.updateById(sysHelp);
        return success();
    }

    @RequiresPermissions("cms:system-help:deletes")
    @PostMapping("/deletes")
    @AccessLog(module = AdminModule.CMS, operation = "删除系统帮助")
    public MessageResult deleteOne(@RequestParam("ids") Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        sysHelpService.removeByIds(idList);
        return success();
    }

    @RequiresPermissions("cms:system-help:page-query")
    @PostMapping("/page-query")
    @AccessLog(module = AdminModule.CMS, operation = "分页查询系统帮助")
    public MessageResult pageQuery(PageParam pageParam) {
        IPage<SysHelp> page = new Page<>(pageParam.getPageNo(),pageParam.getPageSize());
        IPage<SysHelp> all = sysHelpService.page(page);
        return success(IPage2Page(all));
    }

    @RequiresPermissions("cms:system-help:out-excel")
    @GetMapping("/out-excel")
    @AccessLog(module = AdminModule.CMS, operation = "导出系统帮助Excel")
    public MessageResult outExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List all = sysHelpService.list();
        return new FileUtil().exportExcel(request, response, all, "sysHelp");
    }
}
