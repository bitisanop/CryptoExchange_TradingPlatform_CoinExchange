package com.bitisan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitisan.admin.entity.Admin;
import com.bitisan.admin.entity.Department;
import com.bitisan.admin.mapper.DepartmentMapper;
import com.bitisan.admin.service.AdminService;
import com.bitisan.admin.service.DepartmentService;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.util.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 后台部门 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Autowired
    private LocaleMessageSourceService messageSourceService;
    @Autowired
    private AdminService adminService;

    @Override
    public IPage<Department> findAll(Integer pageNo, Integer pageSize) {
        IPage<Department> page = new Page<>(pageNo,pageSize);
        QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
        return this.page(page,queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResult deletes(Long id) {
        List<Admin> list = adminService.findAllByDepartment(id);
        if (list != null && list.size() > 0) {
            MessageResult result = MessageResult.error(messageSourceService.getMessage("DELETE_ALL_USERS_IN_THIS_DEPARTMENT"));
            return result;
        }
        this.removeById(id);
        return MessageResult.success(messageSourceService.getMessage("SUCCESS"));
    }
}
