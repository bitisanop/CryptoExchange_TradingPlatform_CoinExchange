package com.bitisan.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bitisan.admin.entity.SysAdvertise;
import com.bitisan.admin.mapper.SysAdvertiseMapper;
import com.bitisan.admin.service.SysAdvertiseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitisan.constant.CommonStatus;
import com.bitisan.constant.SysAdvertiseLocation;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 轮播图 服务实现类
 * </p>
 *
 * @author markchao
 * @since 2021-08-20
 */
@Service
public class SysAdvertiseServiceImpl extends ServiceImpl<SysAdvertiseMapper, SysAdvertise> implements SysAdvertiseService {

    @Override
    public List<SysAdvertise> findAllNormal(SysAdvertiseLocation sysAdvertiseLocation, String lang) {
        QueryWrapper<SysAdvertise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", CommonStatus.NORMAL.getCode()).eq("sys_advertise_location",sysAdvertiseLocation.getCode()).eq("lang",lang);
        queryWrapper.orderByAsc("sort");
        return this.list(queryWrapper);
    }

    @Override
    public int getMaxSort() {
        return this.baseMapper.getMaxSort();
    }

    @Override
    public List<SysAdvertise> querySysAdvertise(int sort, int cate) {
        return this.baseMapper.querySysAdvertise(sort,cate);
    }
}
