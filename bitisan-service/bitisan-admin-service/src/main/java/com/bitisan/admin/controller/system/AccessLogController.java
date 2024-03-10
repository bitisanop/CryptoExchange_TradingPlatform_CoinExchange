package com.bitisan.admin.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.AdminAccessLog;
import com.bitisan.admin.service.AdminAccessLogService;
import com.bitisan.admin.service.AdminService;
import com.bitisan.admin.vo.AdminAccessLogVo;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.screen.AccessLogScreen;
import com.bitisan.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.Assert.notNull;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @description 日志管理
 * @date 2019/12/22 17:27
 */
@Slf4j
@RestController
@RequestMapping("/system/access-log")
@Transactional(readOnly = true)
public class AccessLogController extends BaseAdminController {

    @Autowired
    private AdminAccessLogService adminAccessLogService;

    @Autowired
    private AdminService adminService ;

    @RequiresPermissions("system:access-log:all")
    @GetMapping("/all")
    @AccessLog(module = AdminModule.SYSTEM, operation = "所有操作/访问日志AdminAccessLog")
    public MessageResult all() {
        List<AdminAccessLog> adminAccessLogList = adminAccessLogService.list();
        return success(adminAccessLogList);
    }

    @RequiresPermissions("system:access-log:detail")
    @GetMapping("/{id}")
    @AccessLog(module = AdminModule.SYSTEM, operation = "操作/访问日志AdminAccessLog 详情")
    public MessageResult detail(@PathVariable("id") Long id) {
        AdminAccessLog adminAccessLog = adminAccessLogService.getById(id);
        notNull(adminAccessLog, "validate id!");
        return success(adminAccessLog);
    }

    @RequiresPermissions("system:access-log:page-query")
    @GetMapping("/page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "分页查找操作/访问日志AdminAccessLog")
    public MessageResult pageQuery(AccessLogScreen accessLogScreen) {
        IPage<AdminAccessLogVo> all = adminAccessLogService.pageQuery(accessLogScreen);
        return success(IPage2Page(all));
    }


}
