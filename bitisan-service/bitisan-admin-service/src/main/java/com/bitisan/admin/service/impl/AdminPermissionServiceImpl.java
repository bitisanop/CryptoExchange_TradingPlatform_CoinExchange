package com.bitisan.admin.service.impl;

import com.bitisan.admin.entity.AdminPermission;
import com.bitisan.admin.mapper.AdminPermissionMapper;
import com.bitisan.admin.service.AdminPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitisan.admin.service.AdminRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 后台菜单 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Service
public class AdminPermissionServiceImpl extends ServiceImpl<AdminPermissionMapper, AdminPermission> implements AdminPermissionService {

    @Autowired
    private AdminRolePermissionService adminRolePermissionService;

    @Override
    public List<AdminPermission> getPermissionsByRid(Long uid) {
        return this.baseMapper.getPermissionsByRid(uid);
    }

    @Override
    public void deletes(Long[] ids) {
        for (Long id : ids) {
            adminRolePermissionService.deleteByPermissionId(id);
            this.removeById(id);
        }

    }
}
