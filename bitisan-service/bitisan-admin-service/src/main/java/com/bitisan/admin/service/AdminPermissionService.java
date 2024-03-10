package com.bitisan.admin.service;

import com.bitisan.admin.entity.AdminPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 后台菜单 服务类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface AdminPermissionService extends IService<AdminPermission> {

    List<AdminPermission> getPermissionsByRid(Long uid);

    void deletes(Long[] ids);
}
