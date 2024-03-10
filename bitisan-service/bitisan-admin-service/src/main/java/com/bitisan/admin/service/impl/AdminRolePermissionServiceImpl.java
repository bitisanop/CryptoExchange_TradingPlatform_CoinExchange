package com.bitisan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bitisan.admin.entity.AdminPermission;
import com.bitisan.admin.entity.AdminRolePermission;
import com.bitisan.admin.mapper.AdminRolePermissionMapper;
import com.bitisan.admin.service.AdminRolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 后台用户权限 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Service
public class AdminRolePermissionServiceImpl extends ServiceImpl<AdminRolePermissionMapper, AdminRolePermission> implements AdminRolePermissionService {

    @Override
    public List<AdminPermission> getPermissionsByRoleId(Long roleId) {
        return this.baseMapper.getPermissionsByRoleId(roleId);
    }

    @Override
    public void deleteByPermissionId(Long id) {
        QueryWrapper<AdminRolePermission> query = new QueryWrapper<>();
        query.eq("rule_id",id);
        this.remove(query);
    }

    @Override
    public void deleteByRoleId(Long id) {
        QueryWrapper<AdminRolePermission> query = new QueryWrapper<>();
        query.eq("role_id",id);
        this.remove(query);
    }
}
