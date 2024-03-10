package com.bitisan.admin.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.AdminRole;
import com.bitisan.admin.entity.Department;
import com.bitisan.admin.service.DepartmentService;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.screen.PageParam;
import com.bitisan.util.BindingResultUtil;
import com.bitisan.util.MessageResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Bitisan  E-mail:bitisanop@gmail.com
 * @date 2020年12月20日
 */
@RestController
@RequestMapping(value = "/system/department")
public class DepartmentController extends BaseAdminController {

    @Autowired
    private DepartmentService departmentService;


    /**
     * 创建或更新部门
     *
     * @param department
     * @return
     */
    @RequiresPermissions("system:department:merge")
    @RequestMapping("merge")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建或更新部门Department")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult save(@Valid Department department, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        if (department.getId() != null) {
            department.setCreateTime(departmentService.getById(department.getId()).getCreateTime());
        }
        departmentService.saveOrUpdate(department);
        return success();
    }

    /**
     * 部门详情
     *
     * @param departmentId
     * @return
     */
    @RequiresPermissions("system:department:detail")
    @RequestMapping("detail")
    @AccessLog(module = AdminModule.SYSTEM, operation = "部门Department详情")
    public MessageResult detail(Long departmentId) {
        Department department = departmentService.getById(departmentId);
        return success(department);
    }

    /**
     * 全部部门
     *
     * @return
     */
    @RequiresPermissions("system:department:all")
    @RequestMapping("all")
    @AccessLog(module = AdminModule.SYSTEM, operation = "所有部门Department")
    public MessageResult allDepartment(PageParam pageParam) {
        IPage<Department> all = departmentService.findAll(pageParam.getPageNo(),pageParam.getPageSize());
        return success(IPage2Page(all));
    }

    @RequiresPermissions("system:department:deletes")
    @RequestMapping("deletes")
    @AccessLog(module = AdminModule.SYSTEM, operation = "批量删除部门")
    public MessageResult deletes(@RequestParam(value = "id") Long id) {
        MessageResult result = departmentService.deletes(id);
        return result;
    }

}
