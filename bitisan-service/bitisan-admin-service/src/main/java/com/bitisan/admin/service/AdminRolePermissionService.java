package com.bitisan.admin.service;

import com.bitisan.admin.entity.AdminPermission;
import com.bitisan.admin.entity.AdminRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 后台用户权限 服务类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface AdminRolePermissionService extends IService<AdminRolePermission> {

    List<AdminPermission> getPermissionsByRoleId(Long roleId);

    void deleteByPermissionId(Long id);

    void deleteByRoleId(Long id);
}
