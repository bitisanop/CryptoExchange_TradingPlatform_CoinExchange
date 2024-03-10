package com.bitisan.admin.mapper;

import com.bitisan.admin.entity.AdminPermission;
import com.bitisan.admin.entity.AdminRolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 后台用户权限 Mapper 接口
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface AdminRolePermissionMapper extends BaseMapper<AdminRolePermission> {

    List<AdminPermission> getPermissionsByRoleId(@Param("roleId") Long roleId);
}
