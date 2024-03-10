package com.bitisan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.admin.entity.SysHelp;
import com.bitisan.admin.mapper.SysHelpMapper;
import com.bitisan.admin.service.SysHelpService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitisan.constant.SysHelpClassification;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统帮助 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Service
public class SysHelpServiceImpl extends ServiceImpl<SysHelpMapper, SysHelp> implements SysHelpService {

    @Override
    public List<SysHelp> findBySysHelpClassification(SysHelpClassification sysHelpClassification) {
        QueryWrapper<SysHelp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sys_help_classification",sysHelpClassification.getCode());
        return this.list(queryWrapper);
    }

    @Override
    public IPage<SysHelp> findByCondition(int pageNo, int pageSize, SysHelpClassification help, String lang) {

        IPage<SysHelp> page = new Page<>(pageNo,pageSize);
        QueryWrapper<SysHelp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sys_help_classification",help.getCode()).eq("lang",lang);
        queryWrapper.orderByDesc("sort");
        return this.page(page,queryWrapper);
    }

    @Override
    public List<SysHelp> getCateTops(String cate, String lang) {
        return this.baseMapper.getCateTops(cate,lang);
    }

    @Override
    public int getMaxSort() {
        return this.baseMapper.getMaxSort();
    }
}
