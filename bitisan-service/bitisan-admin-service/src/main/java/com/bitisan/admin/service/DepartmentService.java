package com.bitisan.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bitisan.admin.entity.Department;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bitisan.util.MessageResult;

/**
 * <p>
 * 后台部门 服务类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
public interface DepartmentService extends IService<Department> {

    IPage<Department> findAll(Integer pageNo, Integer pageSize);

    MessageResult deletes(Long id);
}
