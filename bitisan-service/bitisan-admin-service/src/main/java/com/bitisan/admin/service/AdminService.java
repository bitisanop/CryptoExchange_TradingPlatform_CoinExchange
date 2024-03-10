package com.bitisan.admin.service;

import com.bitisan.admin.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bitisan.admin.entity.Department;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 管理员 服务类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface AdminService extends IService<Admin> {

    Admin findByUsername(String name);

    Admin login(String username, String password);

    void update(Date date, String host, Long id);

    Map findAdminDetail(Long id);

    void deletes(Long[] ids);

    List<Admin> findAllByRoleId(Long id);

    List<Admin> findAllByDepartment(Long departmentId);
}
