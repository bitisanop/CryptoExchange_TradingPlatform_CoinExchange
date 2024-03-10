package com.bitisan.admin.controller.cms;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.Announcement;
import com.bitisan.admin.entity.SysAdvertise;
import com.bitisan.admin.service.SysAdvertiseService;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.CommonStatus;
import com.bitisan.constant.SysAdvertiseLocation;
import com.bitisan.screen.PageParam;
import com.bitisan.screen.SysAdvertiseScreen;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;


/**
 * @author Bitisan  E-mail:bizzanhevin@gmail.com
 * @description 系统广告
 * @date 2019/1/6 15:03
 */
@Slf4j
@RestController
@RequestMapping("/cms/system-advertise")
public class AdvertiseController extends BaseAdminController {
    @Autowired
    private SysAdvertiseService sysAdvertiseService;
    @Autowired
    private LocaleMessageSourceService msService;

    @RequiresPermissions("cms:system-advertise:create")
    @PostMapping("/create")
    @AccessLog(module = AdminModule.CMS, operation = "创建系统广告")
    public MessageResult findOne(@Valid SysAdvertise sysAdvertise, BindingResult bindingResult) {
        Date end = DateUtil.strToDate(sysAdvertise.getEndTime());
        Date start = DateUtil.strToDate(sysAdvertise.getStartTime());
        Assert.isTrue(end.after(start), msService.getMessage("START_END_TIME"));
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        sysAdvertise.setSerialNumber(UUIDUtil.getUUID());
        sysAdvertise.setCreateTime(DateUtil.getCurrentDate());

        updateSort(sysAdvertise.getSort(),sysAdvertise.getSysAdvertiseLocation());
        return success(sysAdvertiseService.save(sysAdvertise));
    }

    @RequiresPermissions("cms:system-advertise:all")
    @PostMapping("/all")
    @AccessLog(module = AdminModule.CMS, operation = "所有系统广告")
    public MessageResult all() {
        List<SysAdvertise> all = sysAdvertiseService.list();
        if (all != null & all.size() > 0) {
            return success(all);
        }
        return error("data null");
    }

    @RequiresPermissions("cms:system-advertise:detail")
    @PostMapping("/detail")
    @AccessLog(module = AdminModule.CMS, operation = "系统广告详情")
    public MessageResult findOne(@RequestParam(value = "serialNumber") String serialNumber) {
        SysAdvertise sysAdvertise = sysAdvertiseService.getById(serialNumber);
        notNull(sysAdvertise, "validate serialNumber!");
        return success(sysAdvertise);
    }

    @RequiresPermissions("cms:system-advertise:update")
    @PostMapping("/update")
    @AccessLog(module = AdminModule.CMS, operation = "更新系统广告")
    public MessageResult update(@Valid SysAdvertise sysAdvertise, BindingResult bindingResult) {
        notNull(sysAdvertise.getSerialNumber(), "validate serialNumber(null)!");
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        SysAdvertise one = sysAdvertiseService.getById(sysAdvertise.getSerialNumber());
        notNull(one, "validate serialNumber!");
        updateSort(sysAdvertise.getSort(), sysAdvertise.getSysAdvertiseLocation());
        sysAdvertiseService.save(sysAdvertise);
        return success();
    }



    @RequiresPermissions("cms:system-advertise:deletes")
    @PostMapping("/deletes")
    @AccessLog(module = AdminModule.CMS, operation = "批量删除系统广告")
    public MessageResult delete(@RequestParam(value = "ids") String[] ids) {
        List<String> idList = Arrays.asList(ids);
        sysAdvertiseService.removeByIds(idList);
        return success();
    }


    @RequiresPermissions("cms:system-advertise:page-query")
    @PostMapping("/page-query")
    @AccessLog(module = AdminModule.CMS, operation = "分页查询系统广告")
    public MessageResult pageQuery(PageParam pageParam, SysAdvertiseScreen screen) {

        IPage<SysAdvertise> page = new Page<>(pageParam.getPageNo(),pageParam.getPageSize());
        QueryWrapper<SysAdvertise> queryWrapper = new QueryWrapper<>();
        if(screen.getStatus() != null) {
            queryWrapper.eq( "status", screen.getStatus());
        }
        if(screen.getSysAdvertiseLocation() != null){
            queryWrapper.eq("sys_advertise_location",screen.getSysAdvertiseLocation().getCode());
        }
        if(StringUtils.isNotBlank(screen.getSerialNumber())){
            queryWrapper.like("serial_number",screen.getSerialNumber());
        }
        queryWrapper.orderByDesc("create_time");
        IPage<SysAdvertise> all = sysAdvertiseService.page(page, queryWrapper);
        return success(IPage2Page(all));
    }

    @RequiresPermissions("cms:system-advertise:top")
    @PostMapping("top")
    @AccessLog(module = AdminModule.CMS, operation = "广告置顶")
    public MessageResult toTop(@RequestParam("serialNum") String serialNum) {
        SysAdvertise advertise = sysAdvertiseService.getById(serialNum);
        int a = sysAdvertiseService.getMaxSort();
        advertise.setSort(a + 1);
        sysAdvertiseService.save(advertise);
        return success();
    }


    @RequiresPermissions("cms:system-advertise:out-excel")
    @GetMapping("/out-excel")
    @AccessLog(module = AdminModule.CMS, operation = "导出系统广告Excel")
    public MessageResult outExcel(
            @RequestParam(value = "serialNumber", required = false) String serialNumber,
            @RequestParam(value = "sysAdvertiseLocation", required = false) SysAdvertiseLocation sysAdvertiseLocation,
            @RequestParam(value = "status", required = false) CommonStatus status,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        LambdaQueryWrapper<SysAdvertise> query = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(serialNumber)){
            query.eq(SysAdvertise::getSerialNumber,serialNumber);
        }
        if(sysAdvertiseLocation != null){
            query.eq(SysAdvertise::getSysAdvertiseLocation,sysAdvertiseLocation.getCode());
        }
        if(status != null){
            query.eq(SysAdvertise::getStatus,status.getCode());
        }

        List list = sysAdvertiseService.list(query);
        return new FileUtil().exportExcel(request, response, list, "sysAdvertise");
    }


    /***
     * 广告排序
     * 获取所有广告，没有该序号设定，存在该序号时，大于该序号的全部递增
     * @param sort
     */
    public void updateSort(int sort ,int cate){
        List<SysAdvertise> list=sysAdvertiseService.querySysAdvertise(sort,cate);
        //筛选大于该序号的广告
        for(int i=0;i<list.size();i++){
            list.get(i).setSort(list.get(i).getSort()+1);
        }
        sysAdvertiseService.updateBatchById(list);
    }
}
