package com.bitisan.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitisan.admin.entity.Admin;
import com.bitisan.admin.entity.AdminPermission;
import com.bitisan.admin.entity.AdminRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bitisan.constant.TransactionType;
import com.bitisan.core.Menu;
import com.bitisan.screen.PageParam;
import com.bitisan.user.entity.MemberTransaction;
import com.bitisan.util.MessageResult;

import java.util.List;

/**
 * <p>
 * 后台角色 服务类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface AdminRoleService extends IService<AdminRole> {

    List<Menu> toMenus(List<AdminPermission> list, Long parentId);

    IPage<AdminRole> findAll(int pageNo, int pageSize);

    MessageResult deletes(Long id);

}
