package com.bitisan.admin.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.AdminPermission;
import com.bitisan.admin.entity.AdminRole;
import com.bitisan.admin.entity.AdminRolePermission;
import com.bitisan.admin.service.AdminPermissionService;
import com.bitisan.admin.service.AdminRolePermissionService;
import com.bitisan.admin.service.AdminRoleService;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.core.Menu;
import com.bitisan.screen.PageParam;
import com.bitisan.util.BindingResultUtil;
import com.bitisan.util.MessageResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年12月20日
 */
@RestController
@RequestMapping(value = "system/role")
public class RoleController extends BaseAdminController {

    @Autowired
    private AdminRoleService adminRoleService;
    @Autowired
    private AdminPermissionService adminPermissionService;
    @Autowired
    private AdminRolePermissionService adminRolePermissionService;

    /**
     * 创建或修改角色
     *
     * @param sysRole
     * @param bindingResult
     * @return
     */


    @RequiresPermissions("system:role:merge")
    @RequestMapping("merge")
    @Transactional(rollbackFor = Exception.class)
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建或修改角色SysRole")
    public MessageResult mergeRole(@Valid AdminRole sysRole, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        adminRoleService.saveOrUpdate(sysRole);
        if (sysRole != null) {
            //修改权限
            //删除之前的
            adminRolePermissionService.deleteByRoleId(sysRole.getId());
            //创建现在的
            List<AdminRolePermission> collect = Arrays.stream(sysRole.getPermissions()).map(x -> {
                AdminRolePermission arp = new AdminRolePermission();
                arp.setRoleId(sysRole.getId());
                arp.setRuleId(x);
                return arp;
            }).collect(Collectors.toList());
            adminRolePermissionService.saveBatch(collect);
            result = success("操作成功");
            result.setData(sysRole);
            return result;
        } else {
            return MessageResult.error(500, "操作失败");
        }

    }

    /**
     * 全部权限树
     *
     * @return
     */
    @RequiresPermissions("system:role:permission:all")
    @RequestMapping("permission/all")
    @AccessLog(module = AdminModule.SYSTEM, operation = "全部权限树Menu")
    public MessageResult allMenu() {
        List<Menu> list = adminRoleService.toMenus(adminPermissionService.list(), 0L);
        MessageResult result = success("success");
        result.setData(list);
        return result;
    }

    /**
     * 角色拥有的权限
     *
     * @param roleId
     * @return
     */
    @RequiresPermissions("system:role:permission")
    @RequestMapping("permission")
    @AccessLog(module = AdminModule.SYSTEM, operation = "角色拥有的权限Menu")
    public MessageResult roleAllPermission(Long roleId) {
        List<AdminPermission> list = adminRolePermissionService.getPermissionsByRoleId(roleId);
        List<Menu> content = adminRoleService.toMenus(list, 0L);
        MessageResult result = success();
        result.setData(content);
        return result;
    }

    /**
     * 更改角色权限
     *
     * @param roleId
     * @param permissionId
     * @return
     */
    //@RequiresPermissions("system:role:permission:update")
   /* @RequestMapping("permission/update")
    @Transactional(rollbackFor = Exception.class)
    @AccessLog(module = AdminModule.SYSTEM, operation = "更改角色拥有的权限Menu")
    public MessageResult updateRolePermission(Long roleId, Long[] permissionId) {
        SysRole sysRole = sysRoleService.findOne(roleId);
        if (permissionId != null) {
            List<SysPermission> list = Arrays.stream(permissionId)
                    .map(x -> sysPermissionService.findOne(x))
                    .collect(Collectors.toList());
            sysRole.setPermissions(list);
        } else {
            sysRole.setPermissions(null);
        }
        return success("操作成功");
    }*/

    /**
     * 全部角色
     *
     * @return
     */
    @RequiresPermissions("system:role:all")
    @RequestMapping("all")
    @AccessLog(module = AdminModule.SYSTEM, operation = "所有角色SysRole")
    public MessageResult getAllRole(PageParam pageParam) {
        IPage<AdminRole> all = adminRoleService.findAll(pageParam.getPageNo(),pageParam.getPageSize());
        return success(IPage2Page(all));
    }

    /**
     * 删除角色
     *
     * @return
     */
    @RequiresPermissions("system:role:deletes")
    @RequestMapping("deletes")
    @AccessLog(module = AdminModule.SYSTEM, operation = "删除角色SysRole")
    public MessageResult deletes(Long id) {
        MessageResult result = adminRoleService.deletes(id);
        if(result.getCode()==0){
            //删除权限
            adminRolePermissionService.deleteByRoleId(id);
        }
        return result;
    }


}
