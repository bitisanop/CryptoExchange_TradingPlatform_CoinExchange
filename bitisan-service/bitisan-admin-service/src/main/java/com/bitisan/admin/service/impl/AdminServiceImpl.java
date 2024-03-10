package com.bitisan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.bitisan.admin.entity.Admin;
import com.bitisan.admin.entity.Department;
import com.bitisan.admin.mapper.AdminMapper;
import com.bitisan.admin.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 管理员 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public Admin findByUsername(String name) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",name).eq("status",0);
        return getOne(queryWrapper);
    }

    @Override
    public Admin login(String username, String password) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username).eq("password",password).eq("status",0);
        return getOne(queryWrapper);
    }

    @Override
    public void update(Date date, String host, Long id) {
        Admin admin = new Admin();
        admin.setLastLoginTime(date);
        admin.setLastLoginIp(host);
        UpdateWrapper<Admin> update = new UpdateWrapper<>();
        update.eq("id",id);
        this.baseMapper.update(admin,update);
    }

    @Override
    public Map findAdminDetail(Long id) {
        return baseMapper.findAdminDetail(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletes(Long[] ids) {
        for (long id : ids) {
            this.baseMapper.deleteById(id);
        }
    }

    @Override
    public List<Admin> findAllByRoleId(Long id) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id",id);
        return this.list(queryWrapper);
    }

    @Override
    public List<Admin> findAllByDepartment(Long departmentId) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id",departmentId);
        return this.list(queryWrapper);
    }
}
